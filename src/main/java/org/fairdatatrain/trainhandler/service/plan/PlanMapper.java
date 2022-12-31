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
package org.fairdatatrain.trainhandler.service.plan;

import lombok.RequiredArgsConstructor;
import org.fairdatatrain.trainhandler.api.dto.plan.*;
import org.fairdatatrain.trainhandler.data.model.Plan;
import org.fairdatatrain.trainhandler.data.model.PlanTarget;
import org.fairdatatrain.trainhandler.data.model.Train;
import org.fairdatatrain.trainhandler.service.station.StationMapper;
import org.fairdatatrain.trainhandler.service.train.TrainMapper;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.util.Collections;
import java.util.UUID;

import static org.fairdatatrain.trainhandler.utils.TimeUtils.now;

@Component
@RequiredArgsConstructor
public class PlanMapper {

    private final TrainMapper trainMapper;

    private final StationMapper stationMapper;

    public PlanDTO toDTO(Plan plan) {
        return PlanDTO.builder()
                .uuid(plan.getUuid())
                .displayName(plan.getDisplayName())
                .note(plan.getNote())
                .train(trainMapper.toDTO(plan.getTrain()))
                .targets(
                        plan.getTargets()
                                .stream()
                                .map(this::toPlanTargetDTO)
                                .toList())
                .publishArtifacts(plan.getPublishArtifacts())
                .createdAt(plan.getCreatedAt().toInstant().toString())
                .updatedAt(plan.getUpdatedAt().toInstant().toString())
                .build();
    }

    public PlanSimpleDTO toSimpleDTO(Plan plan) {
        return PlanSimpleDTO.builder()
                .uuid(plan.getUuid())
                .displayName(plan.getDisplayName())
                .note(plan.getNote())
                .train(trainMapper.toSimpleDTO(plan.getTrain()))
                .targets(
                        plan.getTargets()
                                .stream()
                                .map(this::toPlanTargetDTO)
                                .toList()
                )
                .publishArtifacts(plan.getPublishArtifacts())
                .build();
    }

    public Plan fromCreateDTO(PlanCreateDTO reqDto, Train train) {
        final Timestamp now = now();
        return Plan.builder()
                .uuid(UUID.randomUUID())
                .displayName(reqDto.getDisplayName())
                .note(reqDto.getNote())
                .train(train)
                .targets(Collections.emptyList())
                .publishArtifacts(reqDto.getPublishArtifacts())
                .createdAt(now)
                .updatedAt(now)
                .build();
    }

    public Plan fromUpdateDTO(PlanUpdateDTO reqDto, Plan plan, Train train) {
        final Timestamp now = now();
        plan.setTrain(train);
        plan.setDisplayName(reqDto.getDisplayName());
        plan.setNote(reqDto.getNote());
        plan.setUpdatedAt(now);
        plan.setPublishArtifacts(reqDto.getPublishArtifacts());
        return plan;
    }

    public PlanTargetDTO toPlanTargetDTO(PlanTarget target) {
        return PlanTargetDTO.builder()
                .station(stationMapper.toSimpleDTO(target.getStation()))
                .publishArtifacts(target.getPublishArtifacts())
                .build();
    }
}
