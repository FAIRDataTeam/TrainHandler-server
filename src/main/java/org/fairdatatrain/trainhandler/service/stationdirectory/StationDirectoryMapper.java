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
package org.fairdatatrain.trainhandler.service.stationdirectory;

import org.fairdatatrain.trainhandler.api.dto.stationdirectory.StationDirectoryChangeDTO;
import org.fairdatatrain.trainhandler.api.dto.stationdirectory.StationDirectoryDTO;
import org.fairdatatrain.trainhandler.api.dto.stationdirectory.StationDirectorySimpleDTO;
import org.fairdatatrain.trainhandler.data.model.StationDirectory;
import org.fairdatatrain.trainhandler.data.model.enums.SyncServiceStatus;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Optional;

@Component
public class StationDirectoryMapper {
    public StationDirectoryDTO toDTO(StationDirectory stationDirectory) {
        return StationDirectoryDTO.builder()
                .uuid(stationDirectory.getUuid())
                .uri(stationDirectory.getUri())
                .displayName(stationDirectory.getDisplayName())
                .note(stationDirectory.getNote())
                .status(stationDirectory.getStatus())
                .deletable(stationDirectory.isDeletable())
                .lastContactAt(
                        Optional.ofNullable(stationDirectory.getLastContactAt())
                                .map(Timestamp::toInstant)
                                .orElse(null))
                .createdAt(stationDirectory.getCreatedAt().toInstant())
                .updatedAt(stationDirectory.getUpdatedAt().toInstant())
                .build();
    }

    public StationDirectory fromCreateDTO(StationDirectoryChangeDTO reqDto) {
        final Timestamp now = Timestamp.from(Instant.now());
        return StationDirectory.builder()
                .uri(reqDto.getUri())
                .displayName(reqDto.getDisplayName())
                .note(reqDto.getNote())
                .status(SyncServiceStatus.SYNCING)
                .metadata(null)
                .lastContactAt(null)
                .createdAt(now)
                .updatedAt(now)
                .build();
    }

    public StationDirectory fromUpdateDTO(
            StationDirectoryChangeDTO reqDto,
            StationDirectory stationDirectory
    ) {
        final Timestamp now = Timestamp.from(Instant.now());
        stationDirectory.setUri(reqDto.getUri());
        stationDirectory.setDisplayName(reqDto.getDisplayName());
        stationDirectory.setNote(reqDto.getNote());
        stationDirectory.setStatus(SyncServiceStatus.SYNCING);
        stationDirectory.setUpdatedAt(now);
        return stationDirectory;
    }

    public StationDirectorySimpleDTO toSimpleDTO(StationDirectory stationDirectory) {
        return StationDirectorySimpleDTO.builder()
                .uuid(stationDirectory.getUuid())
                .uri(stationDirectory.getUri())
                .displayName(stationDirectory.getDisplayName())
                .build();
    }
}
