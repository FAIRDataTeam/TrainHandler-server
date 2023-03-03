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
package org.fairdatatrain.trainhandler.service.job.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.fairdatatrain.trainhandler.api.dto.job.JobDTO;
import org.fairdatatrain.trainhandler.api.dto.job.JobEventCreateDTO;
import org.fairdatatrain.trainhandler.api.dto.job.JobEventDTO;
import org.fairdatatrain.trainhandler.api.dto.run.RunDTO;
import org.fairdatatrain.trainhandler.data.model.Job;
import org.fairdatatrain.trainhandler.data.model.JobEvent;
import org.fairdatatrain.trainhandler.data.model.Run;
import org.fairdatatrain.trainhandler.data.model.enums.JobStatus;
import org.fairdatatrain.trainhandler.data.model.enums.RunStatus;
import org.fairdatatrain.trainhandler.data.repository.JobEventRepository;
import org.fairdatatrain.trainhandler.data.repository.JobRepository;
import org.fairdatatrain.trainhandler.data.repository.RunRepository;
import org.fairdatatrain.trainhandler.exception.JobSecurityException;
import org.fairdatatrain.trainhandler.exception.NotFoundException;
import org.fairdatatrain.trainhandler.service.async.AsyncEventPublisher;
import org.fairdatatrain.trainhandler.service.job.JobMapper;
import org.fairdatatrain.trainhandler.service.job.JobService;
import org.fairdatatrain.trainhandler.service.run.RunMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.sql.Timestamp;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class JobEventService {

    public static final String ENTITY_NAME = "JobEvent";

    private final RunRepository runRepository;

    private final JobRepository jobRepository;

    private final JobMapper jobMapper;

    private final RunMapper runMapper;

    private final JobService jobService;

    private final JobEventRepository jobEventRepository;

    private final JobEventMapper jobEventMapper;

    private final AsyncEventPublisher asyncEventPublisher;

    @PersistenceContext
    private final EntityManager entityManager;

    public JobEvent getByIdOrThrow(UUID uuid) throws NotFoundException {
        return jobEventRepository
                .findById(uuid)
                .orElseThrow(() -> new NotFoundException(ENTITY_NAME, uuid));
    }

    public List<JobEventDTO> getEvents(UUID runUuid, UUID jobUuid) throws NotFoundException {
        final Job job = jobService.getByIdOrThrow(jobUuid);
        if (!job.getRun().getUuid().equals(runUuid)) {
            throw new NotFoundException(JobService.ENTITY_NAME, jobUuid);
        }
        return jobEventRepository
                .findAllByJobOrderByOccurredAtAsc(job)
                .parallelStream()
                .map(jobEventMapper::toDTO)
                .toList();
    }

    @Transactional
    public JobEventDTO createEvent(
            UUID runUuid, UUID jobUuid, JobEventCreateDTO reqDto
    ) throws NotFoundException, JobSecurityException {
        final Job job = jobService.getByIdOrThrow(jobUuid);
        final Run run = job.getRun();
        if (!run.getUuid().equals(runUuid)) {
            throw new NotFoundException(JobService.ENTITY_NAME, jobUuid);
        }
        if (!Objects.equals(job.getSecret(), reqDto.getSecret())) {
            throw new JobSecurityException("Incorrect secret for creating job event");
        }
        if (job.getRemoteId() == null) {
            job.setRemoteId(reqDto.getRemoteId());
        }
        else if (!Objects.equals(job.getRemoteId(), reqDto.getRemoteId())) {
            throw new JobSecurityException("Incorrect remote ID for creating job event");
        }
        if (reqDto.getResultStatus() != null) {
            job.setStatus(reqDto.getResultStatus());
            if (job.getFinishedAt() == null && job.isFinished()) {
                job.setFinishedAt(Timestamp.from(reqDto.getOccurredAt()));
            }
            run.setStatus(getNextRunStatus(job, reqDto));
            if (run.getFinishedAt() == null && run.isFinished()) {
                run.setFinishedAt(Timestamp.from(reqDto.getOccurredAt()));
            }
        }
        final JobEvent jobEvent = jobEventRepository.saveAndFlush(
                jobEventMapper.fromCreateDTO(reqDto, job)
        );
        job.setVersion(jobEvent.getUpdatedAt().toInstant().toEpochMilli());
        jobRepository.saveAndFlush(job);
        job.getRun().setVersion(job.getVersion());
        runRepository.saveAndFlush(run);
        entityManager.flush();
        entityManager.refresh(jobEvent);
        return jobEventMapper.toDTO(jobEvent);
    }

    @Transactional
    public void notify(UUID jobUuid) throws NotFoundException {
        final Job job = jobService.getByIdOrThrow(jobUuid);
        final JobDTO jobDto = jobMapper.toDTO(job);
        final RunDTO runDto = runMapper.toDTO(job.getRun());
        asyncEventPublisher.publishNewJobEventNotification(runDto, jobDto);
    }

    private RunStatus getNextRunStatus(Job job, JobEventCreateDTO reqDto) {
        final List<JobStatus> jobStatuses = job.getRun().getJobs().stream().map(job1 -> {
            if (job1.getUuid() == job.getUuid()) {
                return reqDto.getResultStatus();
            }
            else {
                return job.getStatus();
            }
        }).toList();
        if (jobStatuses.stream().anyMatch(JobStatus.RUNNING::equals)) {
            return RunStatus.RUNNING;
        }
        if (jobStatuses.stream().anyMatch(JobStatus.ABORTING::equals)) {
            return RunStatus.ABORTING;
        }
        if (jobStatuses.stream().allMatch(JobStatus.ERRORED::equals)) {
            return RunStatus.ERRORED;
        }
        if (jobStatuses.stream().allMatch(JobStatus.FINISHED::equals)) {
            return RunStatus.FINISHED;
        }
        if (jobStatuses.stream().anyMatch(JobStatus.FAILED::equals)) {
            return RunStatus.FAILED;
        }
        return job.getRun().getStatus();
    }
}
