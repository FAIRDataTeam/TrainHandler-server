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
import org.fairdatatrain.trainhandler.data.model.Job;
import org.fairdatatrain.trainhandler.data.model.PlanTarget;
import org.fairdatatrain.trainhandler.data.model.Run;
import org.fairdatatrain.trainhandler.data.model.enums.JobStatus;
import org.fairdatatrain.trainhandler.service.run.RunMapper;
import org.fairdatatrain.trainhandler.service.station.StationMapper;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.util.Optional;
import java.util.UUID;

import static org.fairdatatrain.trainhandler.utils.RandomUtils.randomSecret;
import static org.fairdatatrain.trainhandler.utils.TimeUtils.now;

@Component
public class JobMapper {

    private final StationMapper stationMapper;

    private final RunMapper runMapper;

    public JobMapper(StationMapper stationMapper, @Lazy RunMapper runMapper) {
        this.stationMapper = stationMapper;
        this.runMapper = runMapper;
    }

    public JobSimpleDTO toSimpleDTO(Job job) {
        return JobSimpleDTO.builder()
                .uuid(job.getUuid())
                .remoteId(job.getRemoteId())
                .status(job.getStatus())
                .startedAt(
                        Optional.ofNullable(job.getStartedAt())
                                .map(Timestamp::toInstant)
                                .orElse(null))
                .finishedAt(
                        Optional.ofNullable(job.getStartedAt())
                                .map(Timestamp::toInstant)
                                .orElse(null))
                .target(stationMapper.toSimpleDTO(job.getTarget().getStation()))
                .runUuid(job.getRun().getUuid())
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
                                .orElse(null))
                .finishedAt(
                        Optional.ofNullable(job.getStartedAt())
                                .map(Timestamp::toInstant)
                                .orElse(null))
                .target(stationMapper.toSimpleDTO(job.getTarget().getStation()))
                .run(runMapper.toSimpleDTO(job.getRun()))
                .createdAt(job.getCreatedAt().toInstant())
                .updatedAt(job.getUpdatedAt().toInstant())
                .build();
    }

    public Job fromTarget(Run run, PlanTarget target) {
        final Timestamp now = now();
        return Job.builder()
                .uuid(UUID.randomUUID())
                .status(JobStatus.PREPARED)
                .target(target)
                .run(run)
                .secret(randomSecret())
                .createdAt(now)
                .updatedAt(now)
                .build();
    }
}
