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
import org.fairdatatrain.trainhandler.acceptance.helper.HelperPage;
import org.fairdatatrain.trainhandler.api.dto.traingarage.TrainGarageDTO;
import org.fairdatatrain.trainhandler.model.TrainGarage;
import org.fairdatatrain.trainhandler.repository.TrainGarageRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;

import java.net.URI;
import java.util.List;
import java.util.stream.StreamSupport;

import static java.lang.String.format;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@DisplayName("GET /train-garages")
public class List_GET extends WebIntegrationTest {

	private URI url() {
		return URI.create("/train-garages");
	}

	private URI urlPaged(int page, int size) {
		return URI.create(format("/train-garages?page=%d&size=%d", page, size));
	}

	@Autowired
	private TrainGarageRepository trainGarageRepository;

	@Test
	@DisplayName("HTTP 200: empty")
	public void res200_empty() {
		// GIVEN: prepare data
		trainGarageRepository.deleteAll();

		// AND: prepare request
		RequestEntity<Void> request = RequestEntity
				.get(url())
				.build();
		ParameterizedTypeReference<HelperPage<TrainGarageDTO>> responseType = new ParameterizedTypeReference<>() {
		};

		// WHEN:
		ResponseEntity<HelperPage<TrainGarageDTO>> result = client.exchange(request, responseType);

		// THEN:
		assertThat("Correct response code is received", result.getStatusCode(), is(equalTo(HttpStatus.OK)));
		assertThat("Body is not null", result.getBody(), is(notNullValue()));
		assertThat("Results page is declared as empty", result.getBody().isEmpty(), is(equalTo(true)));
		assertThat("Results page is empty", result.getBody().getContent().isEmpty(), is(equalTo(true)));
	}

	@Test
	@DisplayName("HTTP 200: non-empty")
	public void res200_nonEmpty() {
		// GIVEN: prepare data
		trainGarageRepository.deleteAll();
		List<TrainGarage> trainGarages = StreamSupport.stream(
				trainGarageRepository.saveAll(TrainGarageTestFixtures.TRAIN_GARAGES).spliterator(),
				false
		).toList();

		// AND: prepare request
		RequestEntity<Void> request = RequestEntity
				.get(url())
				.build();
		ParameterizedTypeReference<HelperPage<TrainGarageDTO>> responseType = new ParameterizedTypeReference<>() {
		};

		// WHEN:
		ResponseEntity<HelperPage<TrainGarageDTO>> result = client.exchange(request, responseType);

		// THEN:
		assertThat("Correct response code is received", result.getStatusCode(), is(equalTo(HttpStatus.OK)));
		assertThat("Body is not null", result.getBody(), is(notNullValue()));
		assertThat("Results page is declared as not-empty", result.getBody().isEmpty(), is(equalTo(false)));
		assertThat("Results page has declared correct number of elements", result.getBody().getNumberOfElements(), comparesEqualTo(trainGarages.size()));
		assertThat("Results page has declared correct total elements", result.getBody().getTotalElements(), comparesEqualTo(trainGarages.size()));
		assertThat("Results page has correct size", result.getBody().getContent().size(), comparesEqualTo(trainGarages.size()));
	}

	@Test
	@DisplayName("HTTP 200: multi-page")
	public void res200_multiPage() {
		// GIVEN: prepare data
		trainGarageRepository.deleteAll();
		List<TrainGarage> trainGarages = StreamSupport.stream(
				trainGarageRepository.saveAll(TrainGarageTestFixtures.TRAIN_GARAGES).spliterator(),
				false
		).toList();

		// AND: prepare request
		RequestEntity<Void> request = RequestEntity
				.get(urlPaged(2,1))
				.build();
		ParameterizedTypeReference<HelperPage<TrainGarageDTO>> responseType = new ParameterizedTypeReference<>() {
		};

		// WHEN:
		ResponseEntity<HelperPage<TrainGarageDTO>> result = client.exchange(request, responseType);

		// THEN:
		assertThat("Correct response code is received", result.getStatusCode(), is(equalTo(HttpStatus.OK)));
		assertThat("Body is not null", result.getBody(), is(notNullValue()));
		assertThat("Results page is declared as not-empty", result.getBody().isEmpty(), is(equalTo(false)));
		assertThat("Results page has declared current page number", result.getBody().getNumber(), comparesEqualTo(2));
		assertThat("Results page has declared correct number of elements", result.getBody().getNumberOfElements(), comparesEqualTo(1));
		assertThat("Results page has declared correct total elements", result.getBody().getTotalElements(), comparesEqualTo(trainGarages.size()));
		assertThat("Results page has correct size", result.getBody().getContent().size(), comparesEqualTo(1));
	}

}
