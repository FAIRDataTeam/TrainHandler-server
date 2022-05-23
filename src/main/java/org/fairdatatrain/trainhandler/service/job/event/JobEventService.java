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
import org.fairdatatrain.trainhandler.api.dto.job.JobEventCreateDTO;
import org.fairdatatrain.trainhandler.api.dto.job.JobEventDTO;
import org.fairdatatrain.trainhandler.data.model.Job;
import org.fairdatatrain.trainhandler.data.model.JobEvent;
import org.fairdatatrain.trainhandler.data.repository.JobEventRepository;
import org.fairdatatrain.trainhandler.data.repository.JobRepository;
import org.fairdatatrain.trainhandler.exception.JobSecurityException;
import org.fairdatatrain.trainhandler.exception.NotFoundException;
import org.fairdatatrain.trainhandler.service.async.AsyncEventPublisher;
import org.fairdatatrain.trainhandler.service.async.JobNotificationListener;
import org.fairdatatrain.trainhandler.service.job.JobService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class JobEventService {

    public static final String ENTITY_NAME = "JobEvent";

    private final JobRepository jobRepository;

    private final JobService jobService;

    private final JobEventRepository jobEventRepository;

    private final JobEventMapper jobEventMapper;

    private final AsyncEventPublisher asyncEventPublisher;

    private final JobNotificationListener jobNotificationListener;

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

    private List<JobEventDTO> getEventsAfter(
            UUID runUuid, UUID jobUuid, UUID afterEventUuid
    ) throws NotFoundException {
        if (afterEventUuid == null) {
            return getEvents(runUuid, jobUuid);
        }
        final JobEvent event = getByIdOrThrow(afterEventUuid);
        return jobEventRepository
                .findAllByJobAndOccurredAtAfterOrderByOccurredAtAsc(
                        event.getJob(), event.getOccurredAt()
                )
                .parallelStream()
                .map(jobEventMapper::toDTO)
                .toList();
    }

    @Transactional
    public List<JobEventDTO> pollEvents(
            UUID runUuid, UUID jobUuid, UUID afterEventUuid
    ) throws NotFoundException, InterruptedException {
        List<JobEventDTO> events = getEventsAfter(runUuid, jobUuid, afterEventUuid);
        if (events.isEmpty()) {
            log.info("No events at this point");
            log.info("Starting to wait");
            jobNotificationListener.wait(jobUuid);
            log.info("Finished to wait");
            entityManager.flush();
            events = getEventsAfter(runUuid, jobUuid, afterEventUuid);
        }
        return events;
    }

    @Transactional
    public JobEventDTO createEvent(
            UUID runUuid, UUID jobUuid, JobEventCreateDTO reqDto
    ) throws NotFoundException, JobSecurityException {
        final Job job = jobService.getByIdOrThrow(jobUuid);
        if (!job.getRun().getUuid().equals(runUuid)) {
            throw new NotFoundException(JobService.ENTITY_NAME, jobUuid);
        }
        if (!Objects.equals(job.getSecret(), reqDto.getSecret())) {
            throw new JobSecurityException("Incorrect secret for creating job event");
        }
        if (job.getRemoteId() == null) {
            job.setRemoteId(reqDto.getRemoteId());
            jobRepository.save(job);
        }
        else if (!Objects.equals(job.getRemoteId(), reqDto.getRemoteId())) {
            throw new JobSecurityException("Incorrect remote ID for creating job event");
        }
        if (reqDto.getResultStatus() != null) {
            job.setStatus(reqDto.getResultStatus());
            jobRepository.save(job);
        }
        final JobEvent jobEvent = jobEventRepository.save(
                jobEventMapper.fromCreateDTO(reqDto, job)
        );
        entityManager.flush();
        entityManager.refresh(jobEvent);
        return jobEventMapper.toDTO(jobEvent);
    }

    public void notify(UUID jobUuid) {
        asyncEventPublisher.publishNewJobEventNotification(jobUuid);
    }
}
