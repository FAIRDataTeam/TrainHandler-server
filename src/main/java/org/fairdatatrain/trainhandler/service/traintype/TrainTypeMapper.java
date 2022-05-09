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
package org.fairdatatrain.trainhandler.service.traintype;

import org.fairdatatrain.trainhandler.api.dto.traintype.TrainTypeChangeDTO;
import org.fairdatatrain.trainhandler.api.dto.traintype.TrainTypeDTO;
import org.fairdatatrain.trainhandler.api.dto.traintype.TrainTypeSimpleDTO;
import org.fairdatatrain.trainhandler.data.model.TrainType;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.UUID;

@Component
public class TrainTypeMapper {
    public TrainTypeDTO toDTO(TrainType trainType) {
        return TrainTypeDTO.builder()
                .uuid(trainType.getUuid())
                .title(trainType.getTitle())
                .uri(trainType.getUri())
                .note(trainType.getNote())
                .createdAt(trainType.getCreatedAt().toInstant())
                .updatedAt(trainType.getUpdatedAt().toInstant())
                .build();
    }

    public TrainType fromCreateDTO(TrainTypeChangeDTO reqDto) {
        final Timestamp now = Timestamp.from(Instant.now());
        return TrainType.builder()
                .uuid(UUID.randomUUID())
                .title(reqDto.getTitle())
                .uri(reqDto.getUri())
                .note(reqDto.getNote())
                .createdAt(now)
                .updatedAt(now)
                .build();
    }

    public TrainType fromUpdateDTO(TrainTypeChangeDTO reqDto, TrainType trainType) {
        final Timestamp now = Timestamp.from(Instant.now());
        trainType.setTitle(reqDto.getTitle());
        trainType.setUri(reqDto.getUri());
        trainType.setNote(reqDto.getNote());
        trainType.setUpdatedAt(now);
        return trainType;
    }

    public TrainTypeSimpleDTO toSimpleDTO(TrainType trainType) {
        return TrainTypeSimpleDTO.builder()
                .uuid(trainType.getUuid())
                .title(trainType.getTitle())
                .uri(trainType.getUri())
                .build();
    }
}
