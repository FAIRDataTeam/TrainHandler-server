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
package org.fairdatatrain.trainhandler.service.traintype;

import lombok.RequiredArgsConstructor;
import org.fairdatatrain.trainhandler.api.dto.traintype.TrainTypeChangeDTO;
import org.fairdatatrain.trainhandler.api.dto.traintype.TrainTypeDTO;
import org.fairdatatrain.trainhandler.data.model.TrainType;
import org.fairdatatrain.trainhandler.data.repository.TrainTypeRepository;
import org.fairdatatrain.trainhandler.exception.NotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TrainTypeService {

    private static final String ENTITY_NAME = "TrainType";

    private final TrainTypeRepository trainTypeRepository;

    private final TrainTypeMapper trainTypeMapper;

    private TrainType getByIdOrThrow(UUID uuid) throws NotFoundException {
        return trainTypeRepository
                .findById(uuid)
                .orElseThrow(() -> new NotFoundException(ENTITY_NAME, uuid));
    }

    public void delete(UUID uuid) throws NotFoundException {
        final TrainType trainType = getByIdOrThrow(uuid);
        trainTypeRepository.delete(trainType);
    }

    public Page<TrainTypeDTO> getPaged(String query, Pageable pageable) {
        if (query.isBlank()) {
            return trainTypeRepository.findAll(pageable).map(trainTypeMapper::toDTO);
        }
        return trainTypeRepository
                .findByTitleContainingIgnoreCase(query, pageable)
                .map(trainTypeMapper::toDTO);
    }

    public TrainTypeDTO getSingle(UUID uuid) throws NotFoundException {
        final TrainType trainType = getByIdOrThrow(uuid);
        return trainTypeMapper.toDTO(trainType);
    }

    @Transactional
    public TrainTypeDTO create(TrainTypeChangeDTO reqDto) {
        // TODO: validate?
        final TrainType newTrainType =
                trainTypeRepository.saveAndFlush(trainTypeMapper.fromCreateDTO(reqDto));
        return trainTypeMapper.toDTO(newTrainType);
    }

    @Transactional
    public TrainTypeDTO update(UUID uuid, TrainTypeChangeDTO reqDto) throws NotFoundException {
        // TODO: validate?
        final TrainType trainType = getByIdOrThrow(uuid);
        final TrainType updatedTrainType =
                trainTypeRepository.saveAndFlush(trainTypeMapper.fromUpdateDTO(reqDto, trainType));
        return trainTypeMapper.toDTO(updatedTrainType);
    }
}
