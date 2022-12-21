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
package org.fairdatatrain.trainhandler.service.plan;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.fairdatatrain.trainhandler.api.dto.plan.PlanCreateDTO;
import org.fairdatatrain.trainhandler.api.dto.plan.PlanDTO;
import org.fairdatatrain.trainhandler.api.dto.plan.PlanSimpleDTO;
import org.fairdatatrain.trainhandler.api.dto.plan.PlanUpdateDTO;
import org.fairdatatrain.trainhandler.data.model.Plan;
import org.fairdatatrain.trainhandler.data.model.PlanTarget;
import org.fairdatatrain.trainhandler.data.model.Station;
import org.fairdatatrain.trainhandler.data.model.Train;
import org.fairdatatrain.trainhandler.data.repository.PlanRepository;
import org.fairdatatrain.trainhandler.data.repository.PlanTargetRepository;
import org.fairdatatrain.trainhandler.exception.CannotPerformException;
import org.fairdatatrain.trainhandler.exception.NotFoundException;
import org.fairdatatrain.trainhandler.service.station.StationService;
import org.fairdatatrain.trainhandler.service.train.TrainService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

import static java.lang.String.format;
import static org.fairdatatrain.trainhandler.utils.CompareUtils.compareListContents;

@Service
@RequiredArgsConstructor
public class PlanService {

    private static final String ENTITY_NAME = "Plan";

    private final PlanRepository planRepository;

    private final PlanTargetRepository planTargetRepository;

    private final PlanMapper planMapper;

    private final PlanTargetMapper planTargetMapper;

    private final TrainService trainService;

    private final StationService stationService;

    @PersistenceContext
    private final EntityManager entityManager;

    public Plan getByIdOrThrow(UUID uuid) throws NotFoundException {
        return planRepository
                .findById(uuid)
                .orElseThrow(() -> new NotFoundException(ENTITY_NAME, uuid));
    }

    public Page<PlanSimpleDTO> getPaged(String query, Pageable pageable) {
        return planRepository
                .findByDisplayNameContainingIgnoreCase(query, pageable)
                .map(planMapper::toSimpleDTO);
    }

    public PlanDTO getSingle(UUID uuid) throws NotFoundException {
        final Plan plan = getByIdOrThrow(uuid);
        return planMapper.toDTO(plan);
    }

    @Transactional
    public PlanDTO create(PlanCreateDTO reqDto) throws NotFoundException {
        // TODO: validate? (empty list of stations)
        final Train train = trainService.getByIdOrThrow(reqDto.getTrainUuid());
        final List<Station> stations = new ArrayList<>();
        for (UUID stationUuid : reqDto.getStationUuids()) {
            stations.add(stationService.getByIdOrThrow(stationUuid));
        }
        final Plan newPlan = planRepository.save(planMapper.fromCreateDTO(reqDto, train));
        entityManager.flush();
        entityManager.refresh(newPlan);
        final List<PlanTarget> targets =
                stations.stream()
                        .map(station -> planTargetMapper.forPlan(newPlan, station))
                        .toList();
        newPlan.setTargets(targets);
        planTargetRepository.saveAll(targets);
        entityManager.flush();
        entityManager.refresh(newPlan);
        return planMapper.toDTO(newPlan);
    }

    @Transactional
    public PlanDTO update(UUID uuid, PlanUpdateDTO reqDto)
            throws NotFoundException, CannotPerformException {
        final String action = "update";
        final Plan plan = getByIdOrThrow(uuid);
        final Set<UUID> oldStationUuids =
                plan.getTargets().stream()
                        .map(PlanTarget::getStation)
                        .map(Station::getUuid)
                        .collect(Collectors.toSet());
        final Set<UUID> newStationUuids = new HashSet<>(reqDto.getStationUuids());
        final boolean planExecuted = !plan.getRuns().isEmpty();
        final boolean changeTrain = reqDto.getTrainUuid() != plan.getTrain().getUuid();
        final boolean changeTargets =
                compareListContents(reqDto.getStationUuids(), oldStationUuids);
        if (planExecuted && changeTrain) {
            throw new CannotPerformException(
                    ENTITY_NAME,
                    uuid,
                    action,
                    "It is not possible to change train of already used plan.");
        }
        if (planExecuted && changeTargets) {
            throw new CannotPerformException(
                    ENTITY_NAME,
                    uuid,
                    action,
                    "It is not possible to change targets of already used plan.");
        }
        final Train train = trainService.getByIdOrThrow(reqDto.getTrainUuid());
        final List<Station> stations = new ArrayList<>();
        for (UUID stationUuid : reqDto.getStationUuids()) {
            if (!oldStationUuids.contains(stationUuid)) {
                stations.add(stationService.getByIdOrThrow(stationUuid));
            }
        }
        for (PlanTarget target : plan.getTargets()) {
            if (!newStationUuids.contains(target.getStation().getUuid())) {
                planTargetRepository.delete(target);
            }
        }
        final Plan updatedPlan = planRepository.save(planMapper.fromUpdateDTO(reqDto, plan, train));
        entityManager.flush();
        entityManager.refresh(updatedPlan);
        final List<PlanTarget> targets =
                stations.stream()
                        .map(station -> planTargetMapper.forPlan(updatedPlan, station))
                        .toList();
        updatedPlan.setTargets(targets);
        planTargetRepository.saveAll(targets);
        entityManager.flush();
        entityManager.refresh(updatedPlan);
        return planMapper.toDTO(updatedPlan);
    }

    @Transactional
    public void delete(UUID uuid) throws NotFoundException, CannotPerformException {
        final Plan plan = getByIdOrThrow(uuid);
        if (!plan.getRuns().isEmpty()) {
            throw new CannotPerformException(
                    ENTITY_NAME,
                    uuid,
                    "delete",
                    format("There are already %d runs for this plan.", plan.getRuns().size()));
        }
        planRepository.delete(plan);
    }
}
