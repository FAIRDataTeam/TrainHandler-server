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
package org.fairdatatrain.trainhandler.acceptance.traingarage;

import org.fairdatatrain.trainhandler.acceptance.WebIntegrationTest;
import org.fairdatatrain.trainhandler.api.dto.traingarage.TrainGarageChangeDTO;
import org.fairdatatrain.trainhandler.api.dto.traingarage.TrainGarageDTO;
import org.fairdatatrain.trainhandler.model.TrainGarage;
import org.fairdatatrain.trainhandler.repository.TrainGarageRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;

import java.net.URI;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@DisplayName("POST /train-garages")
public class List_POST extends WebIntegrationTest {

    private URI url() {
        return URI.create("/train-garages");
    }

    @Autowired
    private TrainGarageRepository trainGarageRepository;

    @Test
    @DisplayName("HTTP 200")
    public void res200() {
        // GIVEN: Prepare data
        trainGarageRepository.deleteAll();
        TrainGarageChangeDTO reqDto = TrainGarageTestFixtures.TRAIN_GARAGE_X_CREATE;

        // AND: Prepare request
        RequestEntity<TrainGarageChangeDTO> request = RequestEntity
                .post(url())
                .accept(MediaType.APPLICATION_JSON)
                .body(reqDto);
        ParameterizedTypeReference<TrainGarageDTO> responseType = new ParameterizedTypeReference<>() {
        };

        // WHEN:
        ResponseEntity<TrainGarageDTO> result = client.exchange(request, responseType);

        // THEN:
        assertThat("Correct response code is received", result.getStatusCode(), is(equalTo(HttpStatus.CREATED)));
        assertThat("Body is not null", result.getBody(), is(notNullValue()));
        Optional<TrainGarage> oTrainGarage = trainGarageRepository.findById(result.getBody().getUuid());
        assertThat("Entity is saved with UUID", oTrainGarage.isPresent(), is(equalTo(true)));
        Common.assertEquals(result.getBody(), oTrainGarage.get());
    }

    @Test
    @DisplayName("HTTP 400: invalid")
    public void res400_invalid() {
        // GIVEN: Prepare data
        trainGarageRepository.deleteAll();
        TrainGarageChangeDTO reqDto =
                TrainGarageTestFixtures.TRAIN_GARAGE_X_CREATE
                        .toBuilder()
                        .displayName("")
                        .build();

        // AND: Prepare request
        RequestEntity<TrainGarageChangeDTO> request = RequestEntity
                .post(url())
                .accept(MediaType.APPLICATION_JSON)
                .body(reqDto);
        ParameterizedTypeReference<?> responseType = new ParameterizedTypeReference<>() {
        };

        // WHEN:
        ResponseEntity<?> result = client.exchange(request, responseType);

        // THEN:
        assertThat("Correct response code is received", result.getStatusCode(), is(equalTo(HttpStatus.BAD_REQUEST)));
    }
}
