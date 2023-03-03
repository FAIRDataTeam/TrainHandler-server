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
package org.fairdatatrain.trainhandler.service.plan.latest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.fairdatatrain.trainhandler.api.dto.job.JobArtifactDTO;
import org.fairdatatrain.trainhandler.data.model.JobArtifact;
import org.fairdatatrain.trainhandler.data.model.Plan;
import org.fairdatatrain.trainhandler.data.repository.JobArtifactRepository;
import org.fairdatatrain.trainhandler.exception.NotFoundException;
import org.fairdatatrain.trainhandler.service.job.artifact.JobArtifactMapper;
import org.fairdatatrain.trainhandler.service.job.artifact.JobArtifactService;
import org.fairdatatrain.trainhandler.service.plan.PlanService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PlanLatestService {

    private final PlanService planService;

    private final JobArtifactService jobArtifactService;

    private final JobArtifactRepository jobArtifactRepository;

    private final JobArtifactMapper jobArtifactMapper;

    public JobArtifact getLatestJobArtifact(UUID planUuid, UUID artifactUuid) throws NotFoundException {
        final JobArtifact jobArtifact = jobArtifactService.getByIdOrThrow(artifactUuid);
        if (!jobArtifact.getJob().getTarget().getPublishArtifacts()
                || !jobArtifact.getJob().getTarget().getPlan().getUuid().equals(planUuid)) {
            throw new NotFoundException("Cannot find public artifact", artifactUuid);
        }
        return jobArtifact;
    }

    public List<JobArtifactDTO> getLatestArtifacts(UUID planUuid) throws NotFoundException {
        final Plan plan = planService.getByIdOrThrow(planUuid);
        if (!plan.getPublishArtifacts()) {
            return List.of();
        }
        return jobArtifactRepository
                .getPublicJobArtifactsOfPlan(planUuid)
                .stream()
                .map(jobArtifactMapper::toDTO)
                .toList();
    }

    public List<JobArtifactDTO> getLatestArtifactsOfTarget(
            UUID planUuid, UUID targetUuid
    ) throws NotFoundException {
        final Plan plan = planService.getByIdOrThrow(planUuid);
        if (!plan.getPublishArtifacts()) {
            return List.of();
        }
        return jobArtifactRepository
                .getPublicJobArtifactsOfTarget(targetUuid)
                .stream()
                .map(jobArtifactMapper::toDTO)
                .toList();
    }

    public byte[] getArtifactData(JobArtifact jobArtifact) {
        return jobArtifactService.getArtifactData(jobArtifact);
    }
}
