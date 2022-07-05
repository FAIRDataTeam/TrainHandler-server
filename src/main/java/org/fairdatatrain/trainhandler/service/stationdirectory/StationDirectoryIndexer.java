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
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.model.Value;
import org.eclipse.rdf4j.model.vocabulary.DCAT;
import org.eclipse.rdf4j.model.vocabulary.LDP;
import org.eclipse.rdf4j.model.vocabulary.RDF;
import org.eclipse.rdf4j.model.vocabulary.RDFS;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.fairdatatrain.trainhandler.data.model.*;
import org.fairdatatrain.trainhandler.data.model.enums.SyncItemStatus;
import org.fairdatatrain.trainhandler.data.model.enums.SyncServiceStatus;
import org.fairdatatrain.trainhandler.data.repository.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientException;

import java.net.URI;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.lang.String.format;
import static java.util.Optional.ofNullable;
import static org.fairdatatrain.trainhandler.utils.RdfIOUtils.read;
import static org.fairdatatrain.trainhandler.utils.RdfIOUtils.write;
import static org.fairdatatrain.trainhandler.utils.RdfUtils.getObjectsBy;
import static org.fairdatatrain.trainhandler.utils.RdfUtils.getStringObjectBy;
import static org.fairdatatrain.trainhandler.utils.TimeUtils.now;
import static org.fairdatatrain.trainhandler.utils.ValueFactoryUtils.i;

@Slf4j
@Service
@RequiredArgsConstructor
public class StationDirectoryIndexer {

    private static final String TRAIN_TYPE = "https://w3id.org/fdp/fdt-o#hasTrainType";

    private static final String STATION = "http://www.w3.org/ns/dcat#DataService";

    private final StationDirectoryRepository stationDirectoryRepository;

    private final TrainTypeRepository trainTypeRepository;

    private final StationRepository stationRepository;

    private final WebClient webClient;

    @Transactional
    public void indexDirectory(StationDirectory stationDirectory) {
        final Set<String> visitedUris = new HashSet<>();
        final Queue<String> toVisitUris = new LinkedList<>();
        final List<Station> stations = new ArrayList<>();
        final List<TrainType> trainTypes = trainTypeRepository.findAllBy();

        Model model;
        try {
            model = makeRequest(stationDirectory.getUri());
            updateDirectory(stationDirectory, model);
            stations.addAll(extractStations(stationDirectory, model, trainTypes));
            toVisitUris.addAll(extractChildren(model));
        }
        catch (Exception exception) {
            updateFaultyDirectory(stationDirectory);
        }

        while (!toVisitUris.isEmpty()) {
            final String uri = toVisitUris.poll();
            log.debug(format("Indexing garage (%s): traversing %s", stationDirectory.getUri(), uri));
            if (visitedUris.contains(uri)) {
                continue;
            }
            else {
                visitedUris.add(uri);
            }
            try {
                model = makeRequest(uri);
                stations.addAll(extractStations(stationDirectory, model, trainTypes));
                toVisitUris.addAll(extractChildren(model));
            }
            catch (Exception exception) {
                log.debug(format("Skipping %s (exception: %s)", uri, exception));
            }
        }

        updateStations(stationDirectory, stations);
    }

    private void updateDirectory(StationDirectory stationDirectory, Model model) {
        stationDirectory.setMetadata("");
        stationDirectory.setLastContactAt(now());
        stationDirectory.setStatus(SyncServiceStatus.SYNCED);
        stationDirectoryRepository.save(stationDirectory);
    }

    private void updateFaultyDirectory(StationDirectory stationDirectory) {
        stationDirectory.setStatus(SyncServiceStatus.UNREACHABLE);
        stationDirectoryRepository.save(stationDirectory);
    }

    private void updateStations(StationDirectory stationDirectory, List<Station> stations) {
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
            }
        });
        existingStations.forEach((uri, station) -> {
            if (!currentStations.containsKey(uri)) {
                deprecateStation(station);
            }
        });

        stationRepository.saveAll(stationDirectory.getStations());
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
                .filter(null, RDF.TYPE, i(STATION))
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
        final List<String> keywords = getObjectsBy(model, resource, DCAT.KEYWORD)
                .stream()
                .map(Value::stringValue)
                .toList();
        // TODO: how to get related train types?
        final Set<String> trainTypeUris = getObjectsBy(model, resource, i(TRAIN_TYPE))
                .stream()
                .map(Value::stringValue)
                .collect(Collectors.toSet());

        if (title == null) {
            log.warn(format("Skipping station %s (missing required information)", resource));
            return null;
        }

        final List<TrainType> matchingTrainTypes = trainTypes
                .stream()
                .filter(trainType -> trainTypeUris.contains(trainType.getUri()))
                .toList();
/*
        if (matchingTrainTypes.isEmpty()) {
            log.warn(format("No matching train type found: %s", trainTypeUris));
            return null;
        }
*/
        return Station
                .builder()
                .uuid(UUID.randomUUID())
                .uri(resource.stringValue())
                .title(title)
                .description(description == null ? "" : description)
                .keywords(String.join(",", keywords))
                .metadata(write(model.filter(resource, null, null)))
                .types(matchingTrainTypes)
                .directory(stationDirectory)
                .status(SyncItemStatus.SYNCED)
                .lastContactAt(now())
                .createdAt(now())
                .updatedAt(now())
                .build();
    }

    private List<String> extractChildren(Model model) {
        return model
                .filter(null, LDP.CONTAINS, null)
                .parallelStream()
                .map(statement -> statement.getObject().stringValue())
                .toList();
    }

    @SneakyThrows
    private Model makeRequest(String uri) {
        log.info(format("Making request to '%s'", uri));
        try {
            // TODO: async?
            final String response = webClient
                    .get()
                    .uri(URI.create(uri))
                    .accept(MediaType.parseMediaType(RDFFormat.TURTLE.getDefaultMIMEType()))
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
            log.info(format("Request to '%s' successfully received", uri));
            final Model result = read(response, uri, RDFFormat.TURTLE);
            log.info(format("Request to '%s' successfully parsed", uri));
            return result;
        }
        catch (WebClientException exception) {
            log.info(format("Request to '%s' failed", uri));
            throw new HttpClientErrorException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    ofNullable(exception.getMessage()).orElse("HTTP request failed")
            );
        }
    }
}
