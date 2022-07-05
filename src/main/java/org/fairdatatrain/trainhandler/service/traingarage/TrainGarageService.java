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

import lombok.RequiredArgsConstructor;
import org.fairdatatrain.trainhandler.api.dto.traingarage.TrainGarageChangeDTO;
import org.fairdatatrain.trainhandler.api.dto.traingarage.TrainGarageDTO;
import org.fairdatatrain.trainhandler.data.model.TrainGarage;
import org.fairdatatrain.trainhandler.data.model.enums.SyncServiceStatus;
import org.fairdatatrain.trainhandler.data.repository.TrainGarageRepository;
import org.fairdatatrain.trainhandler.exception.NotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TrainGarageService {

    private static final String ENTITY_NAME = "TrainGarage";

    private final TrainGarageRepository trainGarageRepository;

    private final TrainGarageMapper trainGarageMapper;

    private final TrainGarageIndexer trainGarageIndexer;

    private TrainGarage getByIdOrThrow(UUID uuid) throws NotFoundException {
        return trainGarageRepository
                .findById(uuid)
                .orElseThrow(() -> new NotFoundException(ENTITY_NAME, uuid));
    }

    public void delete(UUID uuid) throws NotFoundException {
        final TrainGarage trainGarage = getByIdOrThrow(uuid);
        trainGarageRepository.delete(trainGarage);
    }

    public Page<TrainGarageDTO> getPaged(String query, Pageable pageable) {
        if (query.isBlank()) {
            return trainGarageRepository.findAll(pageable).map(trainGarageMapper::toDTO);
        }
        return trainGarageRepository
                .findByDisplayNameContainingIgnoreCase(query, pageable)
                .map(trainGarageMapper::toDTO);
    }

    public TrainGarageDTO getSingle(UUID uuid) throws NotFoundException {
        final TrainGarage trainGarage = getByIdOrThrow(uuid);
        return trainGarageMapper.toDTO(trainGarage);
    }

    public TrainGarageDTO create(TrainGarageChangeDTO reqDto) {
        // TODO: validate?
        final TrainGarage newTrainGarage =
                trainGarageRepository.save(trainGarageMapper.fromCreateDTO(reqDto));
        trainGarageIndexer.indexGarage(newTrainGarage);
        return trainGarageMapper.toDTO(newTrainGarage);
    }

    public TrainGarageDTO update(UUID uuid, TrainGarageChangeDTO reqDto)
            throws NotFoundException {
        // TODO: validate?
        final TrainGarage trainGarage = getByIdOrThrow(uuid);
        final TrainGarage updatedTrainGarage =
                trainGarageRepository.save(
                        trainGarageMapper.fromUpdateDTO(reqDto, trainGarage));
        trainGarageIndexer.indexGarage(updatedTrainGarage);
        return trainGarageMapper.toDTO(updatedTrainGarage);
    }

    public void reindex(UUID uuid) throws NotFoundException {
        final TrainGarage trainGarage = getByIdOrThrow(uuid);
        trainGarage.setStatus(SyncServiceStatus.SYNCING);
        final TrainGarage updatedTrainGarage =
                trainGarageRepository.save(trainGarage);
        trainGarageIndexer.indexGarage(updatedTrainGarage);
    }
}
