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
import lombok.extern.slf4j.Slf4j;
import nl.dtls.fairdatapoint.vocabulary.FDT;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.model.Value;
import org.eclipse.rdf4j.model.vocabulary.DCAT;
import org.eclipse.rdf4j.model.vocabulary.RDF;
import org.eclipse.rdf4j.model.vocabulary.RDFS;
import org.fairdatatrain.trainhandler.data.model.*;
import org.fairdatatrain.trainhandler.data.model.enums.SyncItemStatus;
import org.fairdatatrain.trainhandler.data.model.enums.SyncServiceStatus;
import org.fairdatatrain.trainhandler.data.repository.*;
import org.fairdatatrain.trainhandler.service.indexing.BaseIndexer;
import org.fairdatatrain.trainhandler.utils.ValueFactoryUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.lang.String.format;
import static org.fairdatatrain.trainhandler.utils.RdfIOUtils.write;
import static org.fairdatatrain.trainhandler.utils.RdfUtils.getObjectsBy;
import static org.fairdatatrain.trainhandler.utils.RdfUtils.getStringObjectBy;
import static org.fairdatatrain.trainhandler.utils.TimeUtils.now;
import static org.fairdatatrain.trainhandler.utils.ValueFactoryUtils.i;

@Slf4j
@Service
@RequiredArgsConstructor
public class StationDirectoryIndexer {

    // TODO: from config/ontology
    private static final Map<IRI, List<IRI>> MECHANISM_TRAIN_TYPES = new HashMap<>();

    private final StationDirectoryRepository stationDirectoryRepository;

    private final TrainTypeRepository trainTypeRepository;

    private final StationRepository stationRepository;

    private final BaseIndexer baseIndexer;

    static {
        MECHANISM_TRAIN_TYPES.put(FDT.SPARQL, List.of(FDT.SPARQLTRAIN));
        MECHANISM_TRAIN_TYPES.put(FDT.PYTHON, List.of(FDT.PYTHONTRAIN));
        MECHANISM_TRAIN_TYPES.put(FDT.DOCKER, List.of(FDT.DOCKERTRAIN));
        MECHANISM_TRAIN_TYPES.put(FDT.GRAPHQL, List.of(FDT.GRAPHQLTRAIN));
        MECHANISM_TRAIN_TYPES.put(FDT.API, List.of(FDT.APITRAIN));
        MECHANISM_TRAIN_TYPES.put(FDT.SCRIPT, List.of(FDT.SCRIPTTRAIN));
        MECHANISM_TRAIN_TYPES.put(FDT.SINGULARITY, List.of(FDT.SINGULARITYTRAIN));
        MECHANISM_TRAIN_TYPES.put(FDT.FHIRAPI, List.of(FDT.FHIRTRAIN));
        MECHANISM_TRAIN_TYPES.put(FDT.EXECUTABLECONTAINER, List.of(FDT.CONTAINERTRAIN));
        MECHANISM_TRAIN_TYPES.put(FDT.R, List.of(FDT.RTRAIN));
        MECHANISM_TRAIN_TYPES.put(FDT.QUERY, List.of(FDT.QUERYTRAIN));
        MECHANISM_TRAIN_TYPES.put(FDT.SQL, List.of(FDT.SQLTRAIN));
        MECHANISM_TRAIN_TYPES.put(FDT.RESTAPI, List.of(FDT.RESTTRAIN));
    }

    @Transactional
    public void indexDirectory(StationDirectory stationDirectory) {
        final Set<String> visitedUris = new HashSet<>();
        final Queue<String> toVisitUris = new LinkedList<>();
        final List<Station> stations = new ArrayList<>();
        final List<TrainType> trainTypes = trainTypeRepository.findAll();

        Model model;
        try {
            model = baseIndexer.makeRequest(stationDirectory.getUri());
            updateDirectory(stationDirectory, model);
            stations.addAll(extractStations(stationDirectory, model, trainTypes));
            toVisitUris.addAll(baseIndexer.extractChildren(model));
        }
        catch (Exception exception) {
            updateFaultyDirectory(stationDirectory);
        }

        while (!toVisitUris.isEmpty()) {
            final String uri = toVisitUris.poll();
            log.debug(format("Indexing garage (%s): traversing %s",
                    stationDirectory.getUri(), uri));
            if (visitedUris.contains(uri)) {
                continue;
            }
            else {
                visitedUris.add(uri);
            }
            try {
                model = baseIndexer.makeRequest(uri);
                stations.addAll(extractStations(stationDirectory, model, trainTypes));
                toVisitUris.addAll(baseIndexer.extractChildren(model));
            }
            catch (Exception exception) {
                log.debug(format("Skipping %s (exception: %s)", uri, exception));
            }
        }

        updateStations(stationDirectory, stations);
    }

    public Station tryToFetchStation(String uri) {
        final List<TrainType> trainTypes = trainTypeRepository.findAll();
        try {
            final Model model = baseIndexer.makeRequest(uri);
            final List<Station> stations = extractStations(null, model, trainTypes);
            if (stations.size() > 0) {
                return stations.get(0);
            }
        }
        catch (Exception exception) {
            log.debug(format("Failed to fetch %s (exception: %s)", uri, exception));
        }
        return null;
    }

    private void updateDirectory(StationDirectory stationDirectory, Model model) {
        stationDirectory.setMetadata("");
        stationDirectory.setLastContactAt(now());
        stationDirectory.setStatus(SyncServiceStatus.SYNCED);
        stationDirectoryRepository.saveAndFlush(stationDirectory);
    }

    private void updateFaultyDirectory(StationDirectory stationDirectory) {
        stationDirectory.setStatus(SyncServiceStatus.UNREACHABLE);
        stationDirectoryRepository.saveAndFlush(stationDirectory);
    }

    private void updateStations(StationDirectory stationDirectory, List<Station> stations) {
        final List<Station> stationsToSave = new ArrayList<>();
        final Map<String, Station> existingStations = stationDirectory
                .getStations()
                .stream()
                .collect(Collectors.toMap(Station::getUri, Function.identity()));
        final Map<String, Station> currentStations = stations
                .stream()
                .collect(Collectors.toMap(Station::getUri, Function.identity()));

        currentStations.forEach((uri, station) -> {
            if (existingStations.containsKey(uri)) {
                updateStation(existingStations.get(uri), station);
            }
            else {
                station.setDirectory(stationDirectory);
                stationDirectory.getStations().add(station);
                stationsToSave.add(station);
            }
        });
        existingStations.forEach((uri, station) -> {
            if (!currentStations.containsKey(uri)) {
                deprecateStation(station);
            }
        });

        stationRepository.saveAllAndFlush(stationsToSave);
    }

    private void deprecateStation(Station existingStation) {
        existingStation.setStatus(SyncItemStatus.DELETED);
        existingStation.setUpdatedAt(now());
    }

    private void updateStation(Station existingStation, Station newStation) {
        existingStation.setTitle(newStation.getTitle());
        existingStation.setDescription(newStation.getDescription());
        existingStation.setKeywords(newStation.getKeywords());
        existingStation.setTypes(newStation.getTypes());
        existingStation.setMetadata(newStation.getMetadata());
        existingStation.setLastContactAt(newStation.getLastContactAt());
        existingStation.setUpdatedAt(now());
    }

    private List<Station> extractStations(
            StationDirectory stationDirectory,
            Model model,
            List<TrainType> trainTypes
    ) {
        return model
                .filter(null, RDF.TYPE, i(FDT.DATASTATION))
                .parallelStream()
                .map(stmt -> extractStation(stationDirectory, stmt.getSubject(), model, trainTypes))
                .filter(Objects::nonNull)
                .toList();
    }

    private Station extractStation(
            StationDirectory stationDirectory,
            Resource resource,
            Model model,
            List<TrainType> trainTypes
    ) {
        log.info(format("Station found: %s", resource));
        final String title = getStringObjectBy(model, resource, RDFS.LABEL);
        final String description = getStringObjectBy(model, resource, RDFS.COMMENT);
        final String endpointUrl = getStringObjectBy(model, resource, DCAT.ENDPOINT_URL);
        final String endpointDescription = getStringObjectBy(model, resource, DCAT.ENDPOINT_DESCRIPTION);
        final List<String> keywords = getObjectsBy(model, resource, DCAT.KEYWORD)
                .stream()
                .map(Value::stringValue)
                .toList();

        final Set<IRI> interactionMechanisms =
                getObjectsBy(model, resource, FDT.IMPLEMENTSINTERACTIONMECHANISM)
                        .stream()
                        .map(Value::stringValue)
                        .map(ValueFactoryUtils::i)
                        .collect(Collectors.toSet());
        final Set<String> trainTypeUris = interactionMechanisms
                .stream()
                .map(this::getTrainsByInteractionMechanism)
                .flatMap(List::stream)
                .map(IRI::stringValue)
                .collect(Collectors.toSet());

        if (title == null) {
            log.warn(format("Skipping station %s (missing required information: title)", resource));
            return null;
        }
        if (endpointUrl == null) {
            log.warn(format("Skipping station %s (missing required information: endpoint URL)", resource));
            return null;
        }

        final List<TrainType> matchingTrainTypes = trainTypes
                .stream()
                .filter(trainType -> trainTypeUris.contains(trainType.getUri()))
                .toList();

        if (matchingTrainTypes.isEmpty()) {
            log.warn(format("No matching train type found: %s", trainTypeUris));
            return null;
        }

        return Station
                .builder()
                .uuid(UUID.randomUUID())
                .uri(resource.stringValue())
                .title(title)
                .description(description == null ? "" : description)
                .keywords(String.join(",", keywords))
                .endpointUrl(endpointUrl)
                .endpointDescription(endpointDescription == null ? "" : endpointDescription)
                .metadata(write(model))
                .types(matchingTrainTypes)
                .directory(stationDirectory)
                .status(SyncItemStatus.SYNCED)
                .lastContactAt(now())
                .createdAt(now())
                .updatedAt(now())
                .softDeleted(false)
                .build();
    }

    private List<IRI> getTrainsByInteractionMechanism(IRI interactionMechanism) {
        return Optional.ofNullable(MECHANISM_TRAIN_TYPES.get(interactionMechanism))
                .orElse(Collections.emptyList());
    }
}
