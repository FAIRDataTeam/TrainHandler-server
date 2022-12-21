/**
 * The MIT License
 * Copyright © 2022 FAIR Data Team
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.fairdatatrain.trainhandler.service.run;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.fairdatatrain.trainhandler.api.dto.run.RunCreateDTO;
import org.fairdatatrain.trainhandler.api.dto.run.RunDTO;
import org.fairdatatrain.trainhandler.api.dto.run.RunSimpleDTO;
import org.fairdatatrain.trainhandler.api.dto.run.RunUpdateDTO;
import org.fairdatatrain.trainhandler.data.model.Job;
import org.fairdatatrain.trainhandler.data.model.Plan;
import org.fairdatatrain.trainhandler.data.model.Run;
import org.fairdatatrain.trainhandler.data.repository.JobRepository;
import org.fairdatatrain.trainhandler.data.repository.RunRepository;
import org.fairdatatrain.trainhandler.exception.NotFoundException;
import org.fairdatatrain.trainhandler.service.async.RunNotificationListener;
import org.fairdatatrain.trainhandler.service.job.JobMapper;
import org.fairdatatrain.trainhandler.service.plan.PlanService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.async.DeferredResult;

import java.util.List;
import java.util.UUID;

import static java.lang.String.format;

@Service
@RequiredArgsConstructor
@Slf4j
public class RunService {

    public static final String ENTITY_NAME = "Run";

    private final RunRepository runRepository;

    private final RunMapper runMapper;

    private final PlanService planService;

    private final JobMapper jobMapper;

    private final JobRepository jobRepository;

    private final RunNotificationListener runNotificationListener;

    @PersistenceContext
    private final EntityManager entityManager;

    public Run getByIdOrThrow(UUID uuid) throws NotFoundException {
        return runRepository
                .findById(uuid)
                .orElseThrow(() -> new NotFoundException(ENTITY_NAME, uuid));
    }

    public Page<RunSimpleDTO> getRunsForPlanUuid(UUID planUuid, Pageable pageable) {
        return runRepository.findAllByPlanUuid(planUuid, pageable).map(runMapper::toSimpleDTO);
    }

    public RunDTO getSingle(UUID uuid) throws NotFoundException {
        final Run run = getByIdOrThrow(uuid);
        return runMapper.toDTO(run);
    }

    @Transactional
    public RunDTO create(RunCreateDTO reqDto) throws NotFoundException {
        final Plan plan = planService.getByIdOrThrow(reqDto.getPlanUuid());
        final Run newRun = runRepository.save(runMapper.fromCreateDTO(reqDto, plan));
        entityManager.flush();
        entityManager.refresh(newRun);
        final List<Job> jobs = plan.getTargets().stream()
                .map(target -> jobMapper.fromTarget(newRun, target))
                .toList();
        jobRepository.saveAll(jobs);
        entityManager.flush();
        entityManager.refresh(newRun);
        newRun.getJobs().forEach(entityManager::refresh);
        return runMapper.toDTO(newRun);
    }

    @Transactional
    public RunDTO update(UUID uuid, RunUpdateDTO reqDto) throws NotFoundException {
        // TODO: abort (?)
        final Run run = getByIdOrThrow(uuid);
        final Run updatedRun = runRepository.save(runMapper.fromUpdateDTO(reqDto, run));
        return runMapper.toDTO(updatedRun);
    }

    @Transactional
    public void poll(
            UUID runUuid,
            DeferredResult<RunDTO> result,
            Long version,
            RunDTO currentRun
    ) {
        log.info(format("REQUESTED VERSION: %s", version));
        log.info(format("CURRENT VERSION: %s", currentRun.getVersion()));
        if (version < currentRun.getVersion()) {
            result.setResult(currentRun);
        }
        log.info("No run update at this point, enqueueing...");
        runNotificationListener.enqueue(runUuid, version, result);
    }
}
