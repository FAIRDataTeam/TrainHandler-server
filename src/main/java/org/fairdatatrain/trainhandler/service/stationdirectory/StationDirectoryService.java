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

import lombok.RequiredArgsConstructor;
import org.fairdatatrain.trainhandler.api.dto.stationdirectory.StationDirectoryChangeDTO;
import org.fairdatatrain.trainhandler.api.dto.stationdirectory.StationDirectoryDTO;
import org.fairdatatrain.trainhandler.data.model.StationDirectory;
import org.fairdatatrain.trainhandler.data.repository.StationDirectoryRepository;
import org.fairdatatrain.trainhandler.exception.NotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class StationDirectoryService {

    private static final String ENTITY_NAME = "StationDirectory";

    private final StationDirectoryRepository stationDirectoryRepository;

    private final StationDirectoryMapper stationDirectoryMapper;

    private StationDirectory getByIdOrThrow(UUID uuid) throws NotFoundException {
        return stationDirectoryRepository
                        .findById(uuid)
                        .orElseThrow(() -> new NotFoundException(ENTITY_NAME, uuid));
    }

    public void delete(UUID uuid) throws NotFoundException {
        final StationDirectory stationDirectory = getByIdOrThrow(uuid);
        stationDirectoryRepository.delete(stationDirectory);
    }

    public Page<StationDirectoryDTO> getPaged(String query, Pageable pageable) {
        if (query.isBlank()) {
            return stationDirectoryRepository.findAll(pageable).map(stationDirectoryMapper::toDTO);
        }
        return stationDirectoryRepository
                .findByDisplayNameContainingIgnoreCase(query, pageable)
                .map(stationDirectoryMapper::toDTO);
    }

    public StationDirectoryDTO getSingle(UUID uuid) throws NotFoundException {
        final StationDirectory stationDirectory = getByIdOrThrow(uuid);
        return stationDirectoryMapper.toDTO(stationDirectory);
    }

    public StationDirectoryDTO create(StationDirectoryChangeDTO reqDto) {
        // TODO: validate?
        final StationDirectory newStationDirectory =
                stationDirectoryRepository.save(stationDirectoryMapper.fromCreateDTO(reqDto));
        return this.stationDirectoryMapper.toDTO(newStationDirectory);
    }

    public StationDirectoryDTO update(UUID uuid, StationDirectoryChangeDTO reqDto)
            throws NotFoundException {
        // TODO: validate?
        final StationDirectory stationDirectory = getByIdOrThrow(uuid);
        final StationDirectory updatedStationDirectory =
                stationDirectoryRepository.save(
                        stationDirectoryMapper.fromUpdateDTO(reqDto, stationDirectory));
        return this.stationDirectoryMapper.toDTO(updatedStationDirectory);
    }
}
