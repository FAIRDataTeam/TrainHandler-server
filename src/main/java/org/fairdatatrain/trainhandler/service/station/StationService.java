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
import org.fairdatatrain.trainhandler.api.dto.train.TrainSimpleDTO;
import org.fairdatatrain.trainhandler.data.model.Station;
import org.fairdatatrain.trainhandler.data.model.Train;
import org.fairdatatrain.trainhandler.data.model.TrainType;
import org.fairdatatrain.trainhandler.data.repository.StationRepository;
import org.fairdatatrain.trainhandler.exception.NotFoundException;
import org.fairdatatrain.trainhandler.service.stationdirectory.StationDirectoryIndexer;
import org.fairdatatrain.trainhandler.service.train.TrainMapper;
import org.fairdatatrain.trainhandler.service.traintype.TrainTypeService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
public class StationService {

    private static final String ENTITY_NAME = "Station";

    private final StationRepository stationRepository;

    private final StationMapper stationMapper;

    private final TrainMapper trainMapper;

    private final TrainTypeService trainTypeService;

    private final StationDirectoryIndexer stationDirectoryIndexer;

    public Station getByIdOrThrow(UUID uuid) throws NotFoundException {
        return stationRepository
                .findById(uuid)
                .orElseThrow(() -> new NotFoundException(ENTITY_NAME, uuid));
    }

    public Page<StationSimpleDTO> getPaged(String query, Pageable pageable) {
        if (query.isBlank()) {
            return stationRepository
                    .findAllBySoftDeletedIsFalse(pageable)
                    .map(stationMapper::toSimpleDTO);
        }
        return stationRepository
                .findByTitleContainingIgnoreCaseAndSoftDeletedIsFalse(query, pageable)
                .map(stationMapper::toSimpleDTO);
    }

    public StationDTO getSingle(UUID uuid) throws NotFoundException {
        final Station station = getByIdOrThrow(uuid);
        return stationMapper.toDTO(station);
    }

    public List<StationSimpleDTO> getAll(String query) {
        return stationRepository
                .findByTitleContainingIgnoreCase(query)
                .stream()
                .map(stationMapper::toSimpleDTO)
                .toList();
    }

    public List<TrainSimpleDTO> getSuitableStations(UUID uuid) throws NotFoundException {
        final Station station = getByIdOrThrow(uuid);
        final Set<Train> suitableTrains = new HashSet<>();
        station.getTypes().stream().map(TrainType::getTrains).forEach(suitableTrains::addAll);
        return suitableTrains.stream().map(trainMapper::toSimpleDTO).toList();
    }

    @Transactional
    public StationDTO update(UUID uuid, StationUpdateDTO dto) throws NotFoundException {
        final Station station = getByIdOrThrow(uuid);
        final List<TrainType> trainTypes =
                trainTypeService.getTrainTypes(dto.getTrainTypeUuids());
        Station newStation = stationRepository
                .saveAndFlush(stationMapper.fromUpdateDTO(dto, station, trainTypes));
        if (dto.getFetch()) {
            newStation = updateFetch(newStation);
        }
        return stationMapper.toDTO(newStation);
    }

    @Transactional
    public StationDTO softDelete(UUID uuid) throws NotFoundException {
        final Station station = getByIdOrThrow(uuid);
        station.setSoftDeleted(true);
        return stationMapper.toDTO(stationRepository.save(station));
    }

    public StationDTO create(StationCreateDTO dto) {
        final List<TrainType> trainTypes =
                trainTypeService.getTrainTypes(dto.getTrainTypeUuids());
        Station newStation = stationRepository
                .saveAndFlush(stationMapper.fromCreateDTO(dto, trainTypes));
        if (dto.getFetch()) {
            newStation = updateFetch(newStation);
        }
        return stationMapper.toDTO(newStation);
    }

    private Station updateFetch(Station station) {
        final Station fetchedStation = stationDirectoryIndexer.tryToFetchStation(station.getUri());
        if (fetchedStation != null) {
            return stationRepository
                    .saveAndFlush(stationMapper.updateFetch(station, fetchedStation));
        }
        return station;
    }
}
