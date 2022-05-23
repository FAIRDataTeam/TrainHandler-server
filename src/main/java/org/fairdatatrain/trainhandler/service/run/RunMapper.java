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

import lombok.RequiredArgsConstructor;
import org.fairdatatrain.trainhandler.api.dto.job.JobSimpleDTO;
import org.fairdatatrain.trainhandler.api.dto.run.RunCreateDTO;
import org.fairdatatrain.trainhandler.api.dto.run.RunDTO;
import org.fairdatatrain.trainhandler.api.dto.run.RunSimpleDTO;
import org.fairdatatrain.trainhandler.api.dto.run.RunUpdateDTO;
import org.fairdatatrain.trainhandler.data.model.Plan;
import org.fairdatatrain.trainhandler.data.model.Run;
import org.fairdatatrain.trainhandler.data.model.enums.RunStatus;
import org.fairdatatrain.trainhandler.service.job.JobMapper;
import org.fairdatatrain.trainhandler.service.plan.PlanMapper;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.fairdatatrain.trainhandler.utils.TimeUtils.now;

@Component
@RequiredArgsConstructor
public class RunMapper {

    private final PlanMapper planMapper;

    private final JobMapper jobMapper;

    public RunSimpleDTO toSimpleDTO(Run run) {
        return RunSimpleDTO.builder()
                .uuid(run.getUuid())
                .displayName(run.getDisplayName())
                .note(run.getNote())
                .status(run.getStatus())
                .trainUuid(run.getPlan().getTrain().getUuid())
                .planUuid(run.getPlan().getUuid())
                .shouldStartAt(
                        Optional.ofNullable(run.getShouldStartAt())
                                .map(Timestamp::toInstant)
                                .orElse(null))
                .startedAt(
                        Optional.ofNullable(run.getStartedAt())
                                .map(Timestamp::toInstant)
                                .orElse(null))
                .finishedAt(
                        Optional.ofNullable(run.getFinishedAt())
                                .map(Timestamp::toInstant)
                                .orElse(null))
                .createdAt(run.getCreatedAt().toInstant())
                .updatedAt(run.getUpdatedAt().toInstant())
                .build();
    }

    public RunDTO toDTO(Run run) {
        final List<JobSimpleDTO> jobs = run.getJobs().stream()
                .map(jobMapper::toSimpleDTO)
                .toList();
        final Long version = jobs.stream()
                .map(JobSimpleDTO::getVersion)
                .max(Long::compareTo)
                .orElse(0L);
        return RunDTO.builder()
                .uuid(run.getUuid())
                .displayName(run.getDisplayName())
                .note(run.getNote())
                .status(run.getStatus())
                .plan(planMapper.toSimpleDTO(run.getPlan()))
                .jobs(jobs)
                .version(version)
                .shouldStartAt(
                        Optional.ofNullable(run.getShouldStartAt())
                                .map(Timestamp::toInstant)
                                .orElse(null))
                .startedAt(
                        Optional.ofNullable(run.getStartedAt())
                                .map(Timestamp::toInstant)
                                .orElse(null))
                .finishedAt(
                        Optional.ofNullable(run.getFinishedAt())
                                .map(Timestamp::toInstant)
                                .orElse(null))
                .createdAt(run.getCreatedAt().toInstant())
                .updatedAt(run.getUpdatedAt().toInstant())
                .build();
    }

    public Run fromCreateDTO(RunCreateDTO reqDto, Plan plan) {
        final Timestamp now = now();
        return Run.builder()
                .uuid(UUID.randomUUID())
                .displayName(reqDto.getDisplayName())
                .note(reqDto.getNote())
                .status(reqDto.getShouldStartAt() == null
                        ? RunStatus.PREPARED
                        : RunStatus.SCHEDULED)
                .plan(plan)
                .shouldStartAt(
                        Optional.ofNullable(reqDto.getShouldStartAt())
                                .map(Timestamp::from)
                                .orElse(null))
                .startedAt(null)
                .finishedAt(null)
                .createdAt(now)
                .updatedAt(now)
                .build();
    }

    public Run fromUpdateDTO(RunUpdateDTO reqDto, Run run) {
        final Timestamp now = now();
        run.setDisplayName(reqDto.getDisplayName());
        run.setNote(reqDto.getNote());
        run.setUpdatedAt(now);
        return run;
    }
}
