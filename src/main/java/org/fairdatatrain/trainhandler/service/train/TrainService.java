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
import org.fairdatatrain.trainhandler.api.dto.station.StationSimpleDTO;
import org.fairdatatrain.trainhandler.api.dto.train.TrainDTO;
import org.fairdatatrain.trainhandler.api.dto.train.TrainSimpleDTO;
import org.fairdatatrain.trainhandler.api.dto.train.TrainUpdateDTO;
import org.fairdatatrain.trainhandler.data.model.Station;
import org.fairdatatrain.trainhandler.data.model.Train;
import org.fairdatatrain.trainhandler.data.model.TrainType;
import org.fairdatatrain.trainhandler.data.repository.TrainRepository;
import org.fairdatatrain.trainhandler.exception.NotFoundException;
import org.fairdatatrain.trainhandler.service.station.StationMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TrainService {

    private static final String ENTITY_NAME = "Train";

    private final TrainRepository trainRepository;

    private final TrainMapper trainMapper;

    private final StationMapper stationMapper;

    public Train getByIdOrThrow(UUID uuid) throws NotFoundException {
        return trainRepository
                .findById(uuid)
                .orElseThrow(() -> new NotFoundException(ENTITY_NAME, uuid));
    }

    public Page<TrainSimpleDTO> getPaged(String query, Pageable pageable) {
        if (query.isBlank()) {
            return trainRepository
                    .findAllBySoftDeletedIsFalse(pageable)
                    .map(trainMapper::toSimpleDTO);
        }
        return trainRepository
                .findByTitleContainingIgnoreCaseAndSoftDeletedIsFalse(query, pageable)
                .map(trainMapper::toSimpleDTO);
    }

    public TrainDTO getSingle(UUID uuid) throws NotFoundException {
        final Train train = getByIdOrThrow(uuid);
        return trainMapper.toDTO(train);
    }

    public List<TrainSimpleDTO> getAll(String query) {
        return trainRepository
                .findByTitleContainingIgnoreCase(query)
                .stream()
                .map(trainMapper::toSimpleDTO)
                .toList();
    }

    public List<StationSimpleDTO> getSuitableStations(UUID uuid) throws NotFoundException {
        final Train train = getByIdOrThrow(uuid);
        final Set<Station> suitableStations = new HashSet<>();
        train.getTypes().stream().map(TrainType::getStations).forEach(suitableStations::addAll);
        return suitableStations.stream().map(stationMapper::toSimpleDTO).toList();
    }

    public TrainDTO update(UUID uuid, TrainUpdateDTO dto) throws NotFoundException {
        final Train train = getByIdOrThrow(uuid);
        final Train newTrain = trainRepository.save(trainMapper.fromUpdateDTO(dto, train));
        return trainMapper.toDTO(newTrain);
    }

    public TrainDTO softDelete(UUID uuid) throws NotFoundException {
        final Train train = getByIdOrThrow(uuid);
        train.setSoftDeleted(true);
        return trainMapper.toDTO(trainRepository.save(train));
    }
}
