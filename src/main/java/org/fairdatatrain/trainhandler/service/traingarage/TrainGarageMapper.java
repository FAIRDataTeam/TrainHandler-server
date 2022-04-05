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
package org.fairdatatrain.trainhandler.service.traingarage;

import org.fairdatatrain.trainhandler.api.dto.traingarage.TrainGarageChangeDTO;
import org.fairdatatrain.trainhandler.api.dto.traingarage.TrainGarageDTO;
import org.fairdatatrain.trainhandler.api.dto.traingarage.TrainGarageSimpleDTO;
import org.fairdatatrain.trainhandler.data.model.TrainGarage;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Optional;

@Component
public class TrainGarageMapper {
    public TrainGarageDTO toDTO(TrainGarage trainGarage) {
        return TrainGarageDTO.builder()
                .uuid(trainGarage.getUuid())
                .uri(trainGarage.getUri())
                .displayName(trainGarage.getDisplayName())
                .note(trainGarage.getNote())
                .metadata(trainGarage.getMetadata())
                .status(trainGarage.getStatus())
                .lastContactAt(
                        Optional.ofNullable(trainGarage.getLastContactAt())
                                .map(Timestamp::toInstant)
                                .orElse(null))
                .createdAt(trainGarage.getCreatedAt().toInstant())
                .updatedAt(trainGarage.getCreatedAt().toInstant())
                .build();
    }

    public TrainGarage fromCreateDTO(TrainGarageChangeDTO reqDto) {
        final Timestamp now = Timestamp.from(Instant.now());
        return TrainGarage.builder()
                .uri(reqDto.getUri())
                .displayName(reqDto.getDisplayName())
                .note(reqDto.getNote())
                .status("NEW")
                .metadata(null)
                .lastContactAt(null)
                .createdAt(now)
                .updatedAt(now)
                .build();
    }

    public TrainGarage fromUpdateDTO(
            TrainGarageChangeDTO reqDto,
            TrainGarage trainGarage
    ) {
        final Timestamp now = Timestamp.from(Instant.now());
        trainGarage.setUri(reqDto.getUri());
        trainGarage.setDisplayName(reqDto.getDisplayName());
        trainGarage.setNote(reqDto.getNote());
        trainGarage.setUpdatedAt(now);
        return trainGarage;
    }

    public TrainGarageSimpleDTO toSimpleDTO(TrainGarage trainGarage) {
        return TrainGarageSimpleDTO.builder()
                .uuid(trainGarage.getUuid())
                .uri(trainGarage.getUri())
                .displayName(trainGarage.getDisplayName())
                .build();
    }
}
