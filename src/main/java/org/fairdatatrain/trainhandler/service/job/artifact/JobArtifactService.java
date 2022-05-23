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
package org.fairdatatrain.trainhandler.service.job.artifact;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.fairdatatrain.trainhandler.api.dto.job.JobArtifactDTO;
import org.fairdatatrain.trainhandler.data.model.Job;
import org.fairdatatrain.trainhandler.data.model.JobArtifact;
import org.fairdatatrain.trainhandler.data.model.enums.ArtifactStorage;
import org.fairdatatrain.trainhandler.data.repository.JobArtifactRepository;
import org.fairdatatrain.trainhandler.exception.NotFoundException;
import org.fairdatatrain.trainhandler.service.job.JobService;
import org.fairdatatrain.trainhandler.service.run.RunService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

import static java.lang.String.format;

@Service
@RequiredArgsConstructor
@Slf4j
public class JobArtifactService {

    public static final String ENTITY_NAME = "JobArtifact";

    private final JobArtifactRepository jobArtifactRepository;

    private final JobArtifactMapper jobArtifactMapper;

    private final JobService jobService;

    public JobArtifact getByIdOrThrow(UUID uuid) throws NotFoundException {
        return jobArtifactRepository
                .findById(uuid)
                .orElseThrow(() -> new NotFoundException(ENTITY_NAME, uuid));
    }

    public List<JobArtifactDTO> getArtifacts(UUID runUuid, UUID jobUuid) throws NotFoundException {
        final Job job = jobService.getByIdOrThrow(jobUuid);
        if (!job.getRun().getUuid().equals(runUuid)) {
            throw new NotFoundException(JobService.ENTITY_NAME, jobUuid);
        }
        return job.getArtifacts().stream().map(jobArtifactMapper::toDTO).toList();
    }

    public JobArtifact getArtifact(
            UUID runUuid, UUID jobUuid, UUID artifactUuid
    ) throws NotFoundException {
        final JobArtifact artifact = getByIdOrThrow(artifactUuid);
        if (!artifact.getJob().getUuid().equals(jobUuid)) {
            throw new NotFoundException(JobService.ENTITY_NAME, jobUuid);
        }
        if (!artifact.getJob().getRun().getUuid().equals(runUuid)) {
            throw new NotFoundException(RunService.ENTITY_NAME, runUuid);
        }
        return artifact;
    }

    public byte[] getArtifactData(JobArtifact artifact) {
        if (artifact.getStorage().equals(ArtifactStorage.POSTGRES)) {
            return artifact.getData();
        }
        throw new RuntimeException(
                format("Unsupported artifact storage: %s", artifact.getStorage())
        );
    }
}
