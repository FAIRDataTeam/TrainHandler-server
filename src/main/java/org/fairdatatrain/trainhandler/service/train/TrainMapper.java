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
import org.fairdatatrain.trainhandler.api.dto.train.TrainDTO;
import org.fairdatatrain.trainhandler.api.dto.train.TrainSimpleDTO;
import org.fairdatatrain.trainhandler.api.dto.train.TrainUpdateDTO;
import org.fairdatatrain.trainhandler.data.model.Train;
import org.fairdatatrain.trainhandler.service.traingarage.TrainGarageMapper;
import org.fairdatatrain.trainhandler.service.traintype.TrainTypeMapper;
import org.springframework.stereotype.Component;

import java.util.Arrays;

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
                                .toList())
                .status(train.getStatus())
                .softDeleted(train.getSoftDeleted())
                .metadata(train.getMetadata())
                .garage(trainGarageMapper.toSimpleDTO(train.getGarage()))
                .types(train.getTypes().stream().map(trainTypeMapper::toSimpleDTO).toList())
                .lastContactAt(train.getLastContactAt().toInstant().toString())
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
                                .toList())
                .status(train.getStatus())
                .garage(trainGarageMapper.toSimpleDTO(train.getGarage()))
                .types(train.getTypes().stream().map(trainTypeMapper::toSimpleDTO).toList())
                .build();
    }

    public Train fromUpdateDTO(TrainUpdateDTO dto, Train train) {
        return train.toBuilder()
                .softDeleted(dto.getSoftDeleted())
                .build();
    }
}
