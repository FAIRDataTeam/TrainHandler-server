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
package org.fairdatatrain.trainhandler.service.station;

import lombok.RequiredArgsConstructor;
import org.fairdatatrain.trainhandler.api.dto.station.StationDTO;
import org.fairdatatrain.trainhandler.api.dto.station.StationSimpleDTO;
import org.fairdatatrain.trainhandler.api.dto.station.StationUpdateDTO;
import org.fairdatatrain.trainhandler.data.model.Station;
import org.fairdatatrain.trainhandler.service.stationdirectory.StationDirectoryMapper;
import org.fairdatatrain.trainhandler.service.traintype.TrainTypeMapper;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
@RequiredArgsConstructor
public class StationMapper {

    private static final String KEYWORD_SEP = ",";

    private final StationDirectoryMapper stationDirectoryMapper;

    private final TrainTypeMapper trainTypeMapper;

    public StationDTO toDTO(Station station) {
        return StationDTO.builder()
                .uuid(station.getUuid())
                .title(station.getTitle())
                .uri(station.getUri())
                .description(station.getDescription())
                .keywords(
                        Arrays.stream(station.getKeywords().split(KEYWORD_SEP))
                                .map(String::trim)
                                .toList())
                .status(station.getStatus())
                .softDeleted(station.getSoftDeleted())
                .metadata(station.getMetadata())
                .directory(stationDirectoryMapper.toSimpleDTO(station.getDirectory()))
                .types(station.getTypes().stream().map(trainTypeMapper::toSimpleDTO).toList())
                .lastContactAt(station.getLastContactAt().toInstant().toString())
                .createdAt(station.getCreatedAt().toInstant().toString())
                .updatedAt(station.getUpdatedAt().toInstant().toString())
                .build();
    }

    public StationSimpleDTO toSimpleDTO(Station station) {
        return StationSimpleDTO.builder()
                .uuid(station.getUuid())
                .title(station.getTitle())
                .uri(station.getUri())
                .keywords(
                        Arrays.stream(station.getKeywords().split(KEYWORD_SEP))
                                .map(String::trim)
                                .toList())
                .status(station.getStatus())
                .directory(stationDirectoryMapper.toSimpleDTO(station.getDirectory()))
                .types(station.getTypes().stream().map(trainTypeMapper::toSimpleDTO).toList())
                .build();
    }

    public Station fromUpdateDTO(StationUpdateDTO dto, Station station) {
        return station.toBuilder()
                .softDeleted(dto.getSoftDeleted())
                .build();
    }
}
