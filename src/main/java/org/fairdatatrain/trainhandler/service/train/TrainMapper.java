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
package org.fairdatatrain.trainhandler.service.train;

import lombok.RequiredArgsConstructor;
import org.fairdatatrain.trainhandler.api.dto.train.TrainCreateDTO;
import org.fairdatatrain.trainhandler.api.dto.train.TrainDTO;
import org.fairdatatrain.trainhandler.api.dto.train.TrainSimpleDTO;
import org.fairdatatrain.trainhandler.api.dto.train.TrainUpdateDTO;
import org.fairdatatrain.trainhandler.data.model.Train;
import org.fairdatatrain.trainhandler.data.model.TrainType;
import org.fairdatatrain.trainhandler.data.model.enums.SyncItemStatus;
import org.fairdatatrain.trainhandler.service.traingarage.TrainGarageMapper;
import org.fairdatatrain.trainhandler.service.traintype.TrainTypeMapper;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.fairdatatrain.trainhandler.utils.TimeUtils.now;

@Component
@RequiredArgsConstructor
public class TrainMapper {

    private static final String KEYWORD_SEP = ",";

    private final TrainGarageMapper trainGarageMapper;

    private final TrainTypeMapper trainTypeMapper;

    public TrainDTO toDTO(Train train) {
        return TrainDTO.builder()
                .uuid(train.getUuid())
                .title(train.getTitle())
                .uri(train.getUri())
                .description(train.getDescription())
                .keywords(
                        Arrays.stream(train.getKeywords().split(KEYWORD_SEP))
                                .map(String::trim)
                                .filter(item -> !item.isBlank())
                                .toList())
                .status(train.getStatus())
                .softDeleted(train.getSoftDeleted())
                .metadata(train.getMetadata())
                .garage(
                        Optional.ofNullable(train.getGarage())
                                .map(trainGarageMapper::toSimpleDTO)
                                .orElse(null)
                )
                .types(train.getTypes().stream().map(trainTypeMapper::toSimpleDTO).toList())
                .lastContactAt(
                        Optional.ofNullable(train.getLastContactAt())
                                .map(Timestamp::toInstant)
                                .map(Instant::toString)
                                .orElse(null)
                )
                .createdAt(train.getCreatedAt().toInstant().toString())
                .updatedAt(train.getUpdatedAt().toInstant().toString())
                .build();
    }

    public TrainSimpleDTO toSimpleDTO(Train train) {
        return TrainSimpleDTO.builder()
                .uuid(train.getUuid())
                .title(train.getTitle())
                .uri(train.getUri())
                .keywords(
                        Arrays.stream(train.getKeywords().split(KEYWORD_SEP))
                                .map(String::trim)
                                .filter(item -> !item.isBlank())
                                .toList())
                .status(train.getStatus())
                .garage(
                        Optional.ofNullable(train.getGarage())
                                .map(trainGarageMapper::toSimpleDTO)
                                .orElse(null)
                )
                .types(train.getTypes().stream().map(trainTypeMapper::toSimpleDTO).toList())
                .build();
    }

    public Train fromUpdateDTO(TrainUpdateDTO dto, Train train, List<TrainType> trainTypes) {
        return train.toBuilder()
                .uri(dto.getUri())
                .title(dto.getTitle())
                .description(dto.getDescription())
                .keywords(String.join(KEYWORD_SEP, dto.getKeywords()))
                .metadata(dto.getMetadata())
                .types(trainTypes)
                .softDeleted(
                        Optional.ofNullable(dto.getSoftDeleted())
                                .orElse(train.getSoftDeleted())
                )
                .updatedAt(now())
                .build();
    }

    public Train fromCreateDTO(TrainCreateDTO dto, List<TrainType> trainTypes) {
        final Timestamp now = now();
        return Train.builder()
                .uri(dto.getUri())
                .title(dto.getTitle())
                .description(dto.getDescription())
                .keywords(String.join(KEYWORD_SEP, dto.getKeywords()))
                .metadata(dto.getMetadata())
                .types(trainTypes)
                .status(SyncItemStatus.SYNCED)
                .softDeleted(false)
                .createdAt(now)
                .updatedAt(now)
                .build();
    }

    public Train updateFetch(Train train, Train fetchedTrain) {
        return train.toBuilder()
                .title(fetchedTrain.getTitle())
                .description(fetchedTrain.getDescription())
                .keywords(fetchedTrain.getKeywords())
                .metadata(fetchedTrain.getMetadata())
                .lastContactAt(fetchedTrain.getLastContactAt())
                .types(fetchedTrain.getTypes())
                .status(fetchedTrain.getStatus())
                .updatedAt(now())
                .build();
    }
}
