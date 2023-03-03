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
import org.fairdatatrain.trainhandler.api.dto.station.StationCreateDTO;
import org.fairdatatrain.trainhandler.api.dto.station.StationDTO;
import org.fairdatatrain.trainhandler.api.dto.station.StationSimpleDTO;
import org.fairdatatrain.trainhandler.api.dto.station.StationUpdateDTO;
import org.fairdatatrain.trainhandler.data.model.Station;
import org.fairdatatrain.trainhandler.data.model.TrainType;
import org.fairdatatrain.trainhandler.data.model.enums.SyncItemStatus;
import org.fairdatatrain.trainhandler.service.stationdirectory.StationDirectoryMapper;
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
                                .filter(item -> !item.isBlank())
                                .toList())
                .endpointUrl(station.getEndpointUrl())
                .endpointDescription(station.getEndpointDescription())
                .status(station.getStatus())
                .softDeleted(station.getSoftDeleted())
                .metadata(station.getMetadata())
                .directory(
                        Optional.ofNullable(station.getDirectory())
                                .map(stationDirectoryMapper::toSimpleDTO)
                                .orElse(null)
                )
                .types(station.getTypes().stream().map(trainTypeMapper::toSimpleDTO).toList())
                .lastContactAt(
                        Optional.ofNullable(station.getLastContactAt())
                                .map(Timestamp::toInstant)
                                .map(Instant::toString)
                                .orElse(null)
                )
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
                                .filter(item -> !item.isBlank())
                                .toList())
                .endpointUrl(station.getEndpointUrl())
                .endpointDescription(station.getEndpointDescription())
                .status(station.getStatus())
                .directory(
                        Optional.ofNullable(station.getDirectory())
                                .map(stationDirectoryMapper::toSimpleDTO)
                                .orElse(null)
                )
                .types(station.getTypes().stream().map(trainTypeMapper::toSimpleDTO).toList())
                .build();
    }

    public Station fromUpdateDTO(StationUpdateDTO dto, Station station, List<TrainType> trainTypes) {
        return station.toBuilder()
                .uri(dto.getUri())
                .title(dto.getTitle())
                .description(dto.getDescription())
                .keywords(String.join(KEYWORD_SEP, dto.getKeywords()))
                .endpointUrl(dto.getEndpointUrl())
                .endpointDescription(dto.getEndpointDescription())
                .metadata(dto.getMetadata())
                .types(trainTypes)
                .softDeleted(
                        Optional.ofNullable(dto.getSoftDeleted())
                                .orElse(station.getSoftDeleted())
                )
                .updatedAt(now())
                .build();
    }

    public Station fromCreateDTO(StationCreateDTO dto, List<TrainType> trainTypes) {
        final Timestamp now = now();
        return Station.builder()
                .uri(dto.getUri())
                .title(dto.getTitle())
                .description(dto.getDescription())
                .keywords(String.join(KEYWORD_SEP, dto.getKeywords()))
                .endpointUrl(dto.getEndpointUrl())
                .endpointDescription(dto.getEndpointDescription())
                .metadata(dto.getMetadata())
                .types(trainTypes)
                .status(SyncItemStatus.SYNCED)
                .softDeleted(false)
                .createdAt(now)
                .updatedAt(now)
                .build();
    }

    public Station updateFetch(Station station, Station fetchedStation) {
        return station.toBuilder()
                .title(fetchedStation.getTitle())
                .description(fetchedStation.getDescription())
                .keywords(fetchedStation.getKeywords())
                .metadata(fetchedStation.getMetadata())
                .lastContactAt(fetchedStation.getLastContactAt())
                .types(fetchedStation.getTypes())
                .status(fetchedStation.getStatus())
                .updatedAt(now())
                .build();
    }
}
