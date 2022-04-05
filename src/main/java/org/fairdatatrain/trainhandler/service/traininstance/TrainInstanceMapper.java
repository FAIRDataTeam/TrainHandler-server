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
package org.fairdatatrain.trainhandler.service.traininstance;

import lombok.RequiredArgsConstructor;
import org.fairdatatrain.trainhandler.api.dto.traininstance.TrainInstanceCreateDTO;
import org.fairdatatrain.trainhandler.api.dto.traininstance.TrainInstanceDTO;
import org.fairdatatrain.trainhandler.api.dto.traininstance.TrainInstanceSimpleDTO;
import org.fairdatatrain.trainhandler.data.model.Train;
import org.fairdatatrain.trainhandler.data.model.TrainInstance;
import org.fairdatatrain.trainhandler.service.train.TrainMapper;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Collections;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class TrainInstanceMapper {

    private TrainRunMapper trainRunMapper;

    private TrainMapper trainMapper;

    public TrainInstanceDTO toDTO(TrainInstance trainInstance) {
        return TrainInstanceDTO.builder()
                .uuid(trainInstance.getUuid())
                .displayName(trainInstance.getDisplayName())
                .note(trainInstance.getNote())
                .train(trainMapper.toDTO(trainInstance.getTrain()))
                .runs(trainInstance.getRuns().stream().map(trainRunMapper::toSimpleDTO).toList())
                .createdAt(trainInstance.getCreatedAt().toInstant())
                .updatedAt(trainInstance.getUpdatedAt().toInstant())
                .build();
    }

    public TrainInstanceSimpleDTO toSimpleDTO(TrainInstance trainInstance) {
        return null;
    }

    public TrainInstance fromCreateDTO(TrainInstanceCreateDTO reqDto, Train train) {
        final Timestamp now = Timestamp.from(Instant.now());
        return TrainInstance.builder()
                .uuid(UUID.randomUUID())
                .displayName(reqDto.getDisplayName())
                .note(reqDto.getNote())
                .train(train)
                .runs(Collections.emptyList())
                .createdAt(now)
                .updatedAt(now)
                .build();
    }
}
