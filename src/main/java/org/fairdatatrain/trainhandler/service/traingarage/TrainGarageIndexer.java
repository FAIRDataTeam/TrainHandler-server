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
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.rdf4j.model.*;
import org.eclipse.rdf4j.model.vocabulary.DCAT;
import org.eclipse.rdf4j.model.vocabulary.LDP;
import org.eclipse.rdf4j.model.vocabulary.RDF;
import org.eclipse.rdf4j.model.vocabulary.RDFS;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.fairdatatrain.trainhandler.data.model.Train;
import org.fairdatatrain.trainhandler.data.model.TrainGarage;
import org.fairdatatrain.trainhandler.data.model.TrainType;
import org.fairdatatrain.trainhandler.data.model.enums.SyncItemStatus;
import org.fairdatatrain.trainhandler.data.model.enums.SyncServiceStatus;
import org.fairdatatrain.trainhandler.data.repository.TrainGarageRepository;
import org.fairdatatrain.trainhandler.data.repository.TrainRepository;
import org.fairdatatrain.trainhandler.data.repository.TrainTypeRepository;
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
import static org.fairdatatrain.trainhandler.utils.RdfIOUtils.*;
import static org.fairdatatrain.trainhandler.utils.RdfUtils.getObjectsBy;
import static org.fairdatatrain.trainhandler.utils.RdfUtils.getStringObjectBy;
import static org.fairdatatrain.trainhandler.utils.TimeUtils.now;
import static org.fairdatatrain.trainhandler.utils.ValueFactoryUtils.i;

@Slf4j
@Service
@RequiredArgsConstructor
public class TrainGarageIndexer {

    private static final String TRAIN_TYPE = "https://w3id.org/fdp/fdt-o#hasTrainType";

    private static final String TRAIN = "https://w3id.org/fdp/fdt-o#Train";

    private final TrainGarageRepository trainGarageRepository;

    private final TrainTypeRepository trainTypeRepository;

    private final TrainRepository trainRepository;

    private final WebClient webClient;

    @Transactional
    public void indexGarage(TrainGarage trainGarage) {
        final Set<String> visitedUris = new HashSet<>();
        final Queue<String> toVisitUris = new LinkedList<>();
        final List<Train> trains = new ArrayList<>();
        final List<TrainType> trainTypes = trainTypeRepository.findAllBy();

        Model model;
        try {
            model = makeRequest(trainGarage.getUri());
            updateGarage(trainGarage, model);
            trains.addAll(extractTrains(trainGarage, model, trainTypes));
            toVisitUris.addAll(extractChildren(model));
        }
        catch (Exception exception) {
            updateFaultyGarage(trainGarage);
        }

        while (!toVisitUris.isEmpty()) {
            final String uri = toVisitUris.poll();
            log.debug(format("Indexing garage (%s): traversing %s", trainGarage.getUri(), uri));
            if (visitedUris.contains(uri)) {
                continue;
            }
            else {
                visitedUris.add(uri);
            }
            try {
                model = makeRequest(uri);
                trains.addAll(extractTrains(trainGarage, model, trainTypes));
                toVisitUris.addAll(extractChildren(model));
            }
            catch (Exception exception) {
                log.debug(format("Skipping %s (exception: %s)", uri, exception));
            }
        }

        updateTrains(trainGarage, trains);
    }

    private void updateGarage(TrainGarage trainGarage, Model model) {
        trainGarage.setMetadata("");
        trainGarage.setLastContactAt(now());
        trainGarage.setStatus(SyncServiceStatus.SYNCED);
        trainGarageRepository.save(trainGarage);
    }

    private void updateFaultyGarage(TrainGarage trainGarage) {
        trainGarage.setStatus(SyncServiceStatus.UNREACHABLE);
        trainGarageRepository.save(trainGarage);
    }

    private void updateTrains(TrainGarage trainGarage, List<Train> trains) {
        final Map<String, Train> existingTrains = trainGarage
                .getTrains()
                .stream()
                .collect(Collectors.toMap(Train::getUri, Function.identity()));
        final Map<String, Train> currentTrains = trains
                .stream()
                .collect(Collectors.toMap(Train::getUri, Function.identity()));

        currentTrains.forEach((uri, train) -> {
            if (existingTrains.containsKey(uri)) {
                updateTrain(existingTrains.get(uri), train);
            }
            else {
                train.setGarage(trainGarage);
                trainGarage.getTrains().add(train);
            }
        });
        existingTrains.forEach((uri, train) -> {
            if (!currentTrains.containsKey(uri)) {
                deprecateTrain(train);
            }
        });

        trainRepository.saveAll(trainGarage.getTrains());
    }

    private void deprecateTrain(Train existingTrain) {
        existingTrain.setStatus(SyncItemStatus.DELETED);
        existingTrain.setUpdatedAt(now());
    }

    private void updateTrain(Train existingTrain, Train newTrain) {
        existingTrain.setTitle(newTrain.getTitle());
        existingTrain.setDescription(newTrain.getDescription());
        existingTrain.setKeywords(newTrain.getKeywords());
        existingTrain.setTypes(newTrain.getTypes());
        existingTrain.setMetadata(newTrain.getMetadata());
        existingTrain.setLastContactAt(newTrain.getLastContactAt());
        existingTrain.setUpdatedAt(now());
    }

    private List<Train> extractTrains(
            TrainGarage trainGarage,
            Model model,
            List<TrainType> trainTypes
    ) {
        return model
                .filter(null, RDF.TYPE, i(TRAIN))
                .parallelStream()
                .map(stmt -> extractTrain(trainGarage, stmt.getSubject(), model, trainTypes))
                .filter(Objects::nonNull)
                .toList();
    }

    private Train extractTrain(
            TrainGarage trainGarage,
            Resource resource,
            Model model,
            List<TrainType> trainTypes
    ) {
        log.info(format("Train found: %s", resource));
        final String title = getStringObjectBy(model, resource, RDFS.LABEL);
        final String description = getStringObjectBy(model, resource, RDFS.COMMENT);
        final List<String> keywords = getObjectsBy(model, resource, DCAT.KEYWORD)
                .stream()
                .map(Value::stringValue)
                .toList();
        final Set<String> trainTypeUris = getObjectsBy(model, resource, i(TRAIN_TYPE))
                .stream()
                .map(Value::stringValue)
                .collect(Collectors.toSet());

        if (title == null || trainTypeUris.isEmpty()) {
            log.warn(format("Skipping train %s (missing required information)", resource));
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

        return Train
                .builder()
                .uuid(UUID.randomUUID())
                .uri(resource.stringValue())
                .title(title)
                .description(description == null ? "" : description)
                .keywords(String.join(",", keywords))
                .metadata(write(model.filter(resource, null, null)))
                .types(matchingTrainTypes)
                .garage(trainGarage)
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
