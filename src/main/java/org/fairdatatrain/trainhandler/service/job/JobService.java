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
package org.fairdatatrain.trainhandler.service.job;

import lombok.RequiredArgsConstructor;
import org.fairdatatrain.trainhandler.api.dto.job.JobDTO;
import org.fairdatatrain.trainhandler.api.dto.job.JobSimpleDTO;
import org.fairdatatrain.trainhandler.data.model.Job;
import org.fairdatatrain.trainhandler.data.repository.JobRepository;
import org.fairdatatrain.trainhandler.exception.NotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class JobService {

    private static final String ENTITY_NAME = "Job";

    private final JobRepository jobRepository;

    private final JobMapper jobMapper;

    public Job getByIdOrThrow(UUID uuid) throws NotFoundException {
        return jobRepository
                .findById(uuid)
                .orElseThrow(() -> new NotFoundException(ENTITY_NAME, uuid));
    }

    public Page<JobSimpleDTO> getJobsForRun(UUID runUuid, Pageable pageable) {
        return jobRepository.findAllByRunUuid(runUuid, pageable).map(jobMapper::toSimpleDTO);
    }

    public JobDTO getSingle(UUID runUuid, UUID jobUuid) throws NotFoundException {
        // TODO: check runUuid?
        final Job job = getByIdOrThrow(jobUuid);
        return jobMapper.toDTO(job);
    }
}