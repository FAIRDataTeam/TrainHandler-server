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
package org.fairdatatrain.trainhandler.service.traininstance;

import lombok.RequiredArgsConstructor;
import org.fairdatatrain.trainhandler.api.dto.traininstance.TrainInstanceCreateDTO;
import org.fairdatatrain.trainhandler.api.dto.traininstance.TrainInstanceDTO;
import org.fairdatatrain.trainhandler.api.dto.traininstance.TrainInstanceSimpleDTO;
import org.fairdatatrain.trainhandler.data.model.Station;
import org.fairdatatrain.trainhandler.data.model.Train;
import org.fairdatatrain.trainhandler.data.model.TrainInstance;
import org.fairdatatrain.trainhandler.data.model.TrainRun;
import org.fairdatatrain.trainhandler.data.repository.TrainInstanceRepository;
import org.fairdatatrain.trainhandler.data.repository.TrainRunRepository;
import org.fairdatatrain.trainhandler.exception.NotFoundException;
import org.fairdatatrain.trainhandler.service.station.StationService;
import org.fairdatatrain.trainhandler.service.train.TrainService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TrainInstanceService {

    private static final String ENTITY_NAME = "TrainInstance";

    private TrainInstanceRepository trainInstanceRepository;

    private TrainRunRepository trainRunRepository;

    private TrainInstanceMapper trainInstanceMapper;

    private TrainRunMapper trainRunMapper;

    private TrainService trainService;

    private StationService stationService;

    @PersistenceContext
    private EntityManager entityManager;

    public TrainInstance getByIdOrThrow(UUID uuid) throws NotFoundException {
        return trainInstanceRepository
                .findById(uuid)
                .orElseThrow(() -> new NotFoundException(ENTITY_NAME, uuid));
    }

    public Page<TrainInstanceSimpleDTO> getPaged(String query, Pageable pageable) {
        return trainInstanceRepository
                .findByDisplayNameContainingIgnoreCase(query, pageable)
                .map(trainInstanceMapper::toSimpleDTO);
    }

    public TrainInstanceDTO getSingle(UUID uuid) throws NotFoundException {
        final TrainInstance trainInstance = getByIdOrThrow(uuid);
        return trainInstanceMapper.toDTO(trainInstance);
    }

    @Transactional
    public TrainInstanceDTO create(TrainInstanceCreateDTO reqDto) throws NotFoundException {
        // TODO: validate? (empty list of stations)
        final Train train = trainService.getByIdOrThrow(reqDto.getTrainUuid());
        final List<Station> stations = new ArrayList<>();
        for (UUID uuid : reqDto.getStationUuids()) {
            stations.add(stationService.getByIdOrThrow(uuid));
        }
        final TrainInstance newTrainInstance =
                trainInstanceRepository.save(trainInstanceMapper.fromCreateDTO(reqDto, train));
        entityManager.flush();
        entityManager.refresh(newTrainInstance);
        final List<TrainRun> runs =
                stations.stream()
                        .map(station -> trainRunMapper.fromInstance(newTrainInstance, station))
                        .toList();
        newTrainInstance.setRuns(runs);
        trainRunRepository.saveAll(runs);
        return trainInstanceMapper.toDTO(newTrainInstance);
    }
}
