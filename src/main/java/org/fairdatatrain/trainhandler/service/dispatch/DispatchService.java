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
package org.fairdatatrain.trainhandler.service.dispatch;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.fairdatatrain.trainhandler.data.model.Job;
import org.fairdatatrain.trainhandler.data.model.enums.JobStatus;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientException;

import static java.lang.String.format;

@Slf4j
@Service
@RequiredArgsConstructor
public class DispatchService {

    private final DispatchMapper dispatchMapper;

    private final WebClient webClient;

    public void dispatch(Job job) throws JsonProcessingException {
        if (!job.getStatus().equals(JobStatus.PREPARED)) {
            throw new RuntimeException("Job not in state PREPARED, cannot dispatch");
        }
        final DispatchPayload payload = dispatchMapper.toPayload(job);
        // TODO: should it POST directly to the station URI or some other endpoint?
        // TODO: what should be the response?
        final String uri = job.getTarget().getStation().getUri();
        log.info(format("Dispatching job %s by POST to %s", job.getUuid(), uri));
        try {
            final String response = webClient
                    .post()
                    .uri(uri)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(payload)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
        }
        catch (WebClientException exception) {
            exception.printStackTrace();
            log.warn(format(
                    "Dispatching job %s failed: %s", job.getUuid(), exception.getMessage()
            ));
            throw new RuntimeException(
                    "Station responded with status: " + exception.getMessage()
            );
        }
        log.info(format("Dispatching job %s accepted", job.getUuid()));
    }

}
