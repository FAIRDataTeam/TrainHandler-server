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
import org.fairdatatrain.trainhandler.data.model.TrainGarage;
import org.fairdatatrain.trainhandler.data.repository.TrainGarageRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;

import java.net.URI;
import java.util.Optional;
import java.util.UUID;

import static java.lang.String.format;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

@DisplayName("DELETE /train-garages/:uuid")
public class Detail_DELETE extends WebIntegrationTest {

	private URI url(UUID uuid) {
		return URI.create(format("/train-garages/%s", uuid));
	}

	@Autowired
	private TrainGarageRepository trainGarageRepository;

	@Test
	@DisplayName("HTTP 204")
	public void res204() {
		// GIVEN: prepare data
		trainGarageRepository.deleteAll();
		TrainGarage garage = trainGarageRepository.save(TrainGarageTestFixtures.TRAIN_GARAGE_A);

		// AND: prepare request
		RequestEntity<Void> request = RequestEntity
				.delete(url(garage.getUuid()))
				.build();
		ParameterizedTypeReference<Void> responseType = new ParameterizedTypeReference<>() {
		};

		// WHEN:
		ResponseEntity<Void> result = client.exchange(request, responseType);

		// THEN:
		assertThat("Correct response code is received", result.getStatusCode(), is(equalTo(HttpStatus.NO_CONTENT)));
		Optional<TrainGarage> directoryAfter = trainGarageRepository.findById(garage.getUuid());
		assertThat("Station directory is deleted", directoryAfter.isEmpty(), is(equalTo(true)));
	}

	@Test
	@DisplayName("HTTP 404")
	public void res404() {
		// GIVEN: prepare data
		trainGarageRepository.deleteAll();

		// AND: prepare request
		RequestEntity<Void> request = RequestEntity
				.delete(url(UUID.randomUUID()))
				.build();
		ParameterizedTypeReference<?> responseType = new ParameterizedTypeReference<>() {
		};

		// WHEN:
		ResponseEntity<?> result = client.exchange(request, responseType);

		// THEN:
		assertThat("Correct response code is received", result.getStatusCode(), is(equalTo(HttpStatus.NOT_FOUND)));
	}

}
