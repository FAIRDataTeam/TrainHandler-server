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
package org.fairdatatrain.trainhandler.acceptance.stationdirectory;

import org.fairdatatrain.trainhandler.acceptance.WebIntegrationTest;
import org.fairdatatrain.trainhandler.api.dto.stationdirectory.StationDirectoryChangeDTO;
import org.fairdatatrain.trainhandler.api.dto.stationdirectory.StationDirectoryDTO;
import org.fairdatatrain.trainhandler.data.model.StationDirectory;
import org.fairdatatrain.trainhandler.data.repository.StationDirectoryRepository;
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
import java.util.UUID;

import static java.lang.String.format;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@DisplayName("PUT /station-directories/:uuid")
public class Detail_PUT extends WebIntegrationTest {

	private URI url(UUID uuid) {
		return URI.create(format("/station-directories/%s", uuid));
	}

	@Autowired
	private StationDirectoryRepository stationDirectoryRepository;

	@Test
	@DisplayName("HTTP 200")
	public void res200() {
		// GIVEN: prepare data
		stationDirectoryRepository.deleteAll();
		StationDirectory directory = stationDirectoryRepository.save(StationDirectoryTestFixtures.STATION_DIRECTORY_A);
		StationDirectoryChangeDTO reqDto = StationDirectoryTestFixtures.STATION_DIRECTORY_A_UPDATE;

		// AND: prepare request
		RequestEntity<StationDirectoryChangeDTO> request = RequestEntity
				.put(url(directory.getUuid()))
				.accept(MediaType.APPLICATION_JSON)
				.body(reqDto);
		ParameterizedTypeReference<StationDirectoryDTO> responseType = new ParameterizedTypeReference<>() {
		};

		// WHEN:
		ResponseEntity<StationDirectoryDTO> result = client.exchange(request, responseType);

		// THEN:
		assertThat("Correct response code is received", result.getStatusCode(), is(equalTo(HttpStatus.OK)));
		assertThat("Body is not null", result.getBody(), is(notNullValue()));
		Common.assertEquals(result.getBody(), reqDto);
		Optional<StationDirectory> directoryAfter = stationDirectoryRepository.findById(directory.getUuid());
		assertThat("Station directory is saved", directoryAfter.isPresent(), is(equalTo(true)));
		Common.assertEquals(result.getBody(), directoryAfter.get());
	}

	@Test
	@DisplayName("HTTP 400")
	public void res400() {
		// GIVEN: prepare data
		stationDirectoryRepository.deleteAll();
		StationDirectory directory = stationDirectoryRepository.save(StationDirectoryTestFixtures.STATION_DIRECTORY_A);
        StationDirectoryChangeDTO reqDto =
                StationDirectoryTestFixtures.STATION_DIRECTORY_A_UPDATE
						.toBuilder()
						.displayName("")
						.build();

		// AND: prepare request
		RequestEntity<StationDirectoryChangeDTO> request = RequestEntity
				.put(url(directory.getUuid()))
				.accept(MediaType.APPLICATION_JSON)
				.body(reqDto);
		ParameterizedTypeReference<?> responseType = new ParameterizedTypeReference<>() {
		};

		// WHEN:
		ResponseEntity<?> result = client.exchange(request, responseType);

		// THEN:
		assertThat("Correct response code is received", result.getStatusCode(), is(equalTo(HttpStatus.BAD_REQUEST)));
	}

	@Test
	@DisplayName("HTTP 404")
	public void res404() {
		// GIVEN: prepare data
		stationDirectoryRepository.deleteAll();
		StationDirectoryChangeDTO reqDto = StationDirectoryTestFixtures.STATION_DIRECTORY_A_UPDATE;

		// AND: prepare request
		RequestEntity<StationDirectoryChangeDTO> request = RequestEntity
				.put(url(UUID.randomUUID()))
				.accept(MediaType.APPLICATION_JSON)
				.body(reqDto);
		ParameterizedTypeReference<?> responseType = new ParameterizedTypeReference<>() {
		};

		// WHEN:
		ResponseEntity<?> result = client.exchange(request, responseType);

		// THEN:
		assertThat("Correct response code is received", result.getStatusCode(), is(equalTo(HttpStatus.NOT_FOUND)));
	}

}
