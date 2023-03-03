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
package org.fairdatatrain.trainhandler.acceptance.actuator;

import org.fairdatatrain.trainhandler.acceptance.WebIntegrationTest;
import org.fairdatatrain.trainhandler.acceptance.actuator.ActuatorInfoDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;

import java.net.URI;

import static java.lang.String.format;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@DisplayName("OPTIONS  /actuator/info")
public class CorsTest extends WebIntegrationTest {


	private URI url() {
		return URI.create("/actuator/info");
	}

	@Test
	@DisplayName("Correct CORS headers")
	public void corsHeaders() {
		// GIVEN:
		RequestEntity<Void> request = RequestEntity
				.options(url())
				.build();
		ParameterizedTypeReference<ActuatorInfoDTO> responseType = new ParameterizedTypeReference<>() {
		};

		// WHEN:
		ResponseEntity<ActuatorInfoDTO> result = client.exchange(request, responseType);

		// THEN:
		assertThat(result.getHeaders().get(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN), is(notNullValue()));
		assertThat(result.getHeaders().get(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN).size(), is(equalTo(1)));
		assertThat(result.getHeaders().get(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN).get(0), is(equalTo("*")));
		assertThat(result.getHeaders().get(HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS), is(notNullValue()));
		assertThat(result.getHeaders().get(HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS).size(), is(equalTo(1)));
		assertThat(result.getHeaders().get(HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS).get(0), is(equalTo("GET,DELETE,PATCH,POST,PUT")));
		assertThat(result.getHeaders().get(HttpHeaders.ACCESS_CONTROL_ALLOW_HEADERS), is(notNullValue()));
		assertThat(result.getHeaders().get(HttpHeaders.ACCESS_CONTROL_ALLOW_HEADERS).size(), is(equalTo(1)));
		assertThat(result.getHeaders().get(HttpHeaders.ACCESS_CONTROL_ALLOW_HEADERS).get(0), is(equalTo("Accept,Authorization,Content-Type,Origin")));
		assertThat(result.getHeaders().get(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS), is(notNullValue()));
		assertThat(result.getHeaders().get(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS).size(), is(equalTo(1)));
		assertThat(result.getHeaders().get(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS).get(0), is(equalTo("Link,Location")));
	}
}
