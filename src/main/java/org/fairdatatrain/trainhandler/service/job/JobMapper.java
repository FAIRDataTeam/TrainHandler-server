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

import org.fairdatatrain.trainhandler.api.dto.job.JobDTO;
import org.fairdatatrain.trainhandler.api.dto.job.JobSimpleDTO;
import org.fairdatatrain.trainhandler.data.model.*;
import org.fairdatatrain.trainhandler.data.model.enums.JobStatus;
import org.fairdatatrain.trainhandler.service.job.artifact.JobArtifactMapper;
import org.fairdatatrain.trainhandler.service.job.event.JobEventMapper;
import org.fairdatatrain.trainhandler.service.run.RunMapper;
import org.fairdatatrain.trainhandler.service.station.StationMapper;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.fairdatatrain.trainhandler.utils.RandomUtils.randomSecret;

@Component
public class JobMapper {

    private final StationMapper stationMapper;

    private final RunMapper runMapper;

    private final JobEventMapper jobEventMapper;

    private final JobArtifactMapper jobArtifactMapper;

    public JobMapper(
            StationMapper stationMapper,
            JobEventMapper jobEventMapper,
            JobArtifactMapper jobArtifactMapper,
            @Lazy RunMapper runMapper
    ) {
        this.stationMapper = stationMapper;
        this.runMapper = runMapper;
        this.jobEventMapper = jobEventMapper;
        this.jobArtifactMapper = jobArtifactMapper;
    }

    public JobSimpleDTO toSimpleDTO(Job job) {
        return JobSimpleDTO.builder()
                .uuid(job.getUuid())
                .remoteId(job.getRemoteId())
                .status(job.getStatus())
                .startedAt(
                        Optional.ofNullable(job.getStartedAt())
                                .map(Timestamp::toInstant)
                                .map(Instant::toString)
                                .orElse(null))
                .finishedAt(
                        Optional.ofNullable(job.getFinishedAt())
                                .map(Timestamp::toInstant)
                                .map(Instant::toString)
                                .orElse(null))
                .target(stationMapper.toSimpleDTO(job.getTarget().getStation()))
                .artifacts(job.getArtifacts().stream().map(jobArtifactMapper::toDTO).toList())
                .runUuid(job.getRun().getUuid())
                .version(job.getVersion())
                .build();
    }

    public JobDTO toDTO(Job job) {
        return JobDTO.builder()
                .uuid(job.getUuid())
                .remoteId(job.getRemoteId())
                .status(job.getStatus())
                .startedAt(
                        Optional.ofNullable(job.getStartedAt())
                                .map(Timestamp::toInstant)
                                .map(Instant::toString)
                                .orElse(null))
                .finishedAt(
                        Optional.ofNullable(job.getFinishedAt())
                                .map(Timestamp::toInstant)
                                .map(Instant::toString)
                                .orElse(null))
                .target(stationMapper.toSimpleDTO(job.getTarget().getStation()))
                .run(runMapper.toSimpleDTO(job.getRun()))
                .events(job.getEvents().stream().map(jobEventMapper::toDTO).toList())
                .artifacts(job.getArtifacts().stream().map(jobArtifactMapper::toDTO).toList())
                .createdAt(job.getCreatedAt().toInstant().toString())
                .updatedAt(job.getUpdatedAt().toInstant().toString())
                .version(job.getVersion())
                .build();
    }

    public Job fromTarget(Run run, PlanTarget target) {
        return Job.builder()
                .uuid(UUID.randomUUID())
                .status(JobStatus.PREPARED)
                .target(target)
                .run(run)
                .secret(randomSecret())
                .createdAt(run.getCreatedAt())
                .updatedAt(run.getUpdatedAt())
                .version(run.getVersion())
                .build();
    }
}
