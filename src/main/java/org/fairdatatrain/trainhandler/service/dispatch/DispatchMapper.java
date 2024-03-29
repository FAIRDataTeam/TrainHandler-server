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

import org.fairdatatrain.trainhandler.api.controller.JobArtifactController;
import org.fairdatatrain.trainhandler.api.controller.JobEventController;
import org.fairdatatrain.trainhandler.data.model.Job;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class DispatchMapper {

    @Value("${dispatcher.dispatch.root}")
    private String dispatchRoot;

    public DispatchPayload toPayload(Job job) {
        return DispatchPayload
                .builder()
                .jobUuid(job.getUuid())
                .secret(job.getSecret())
                .trainUri(job.getRun().getPlan().getTrain().getUri())
                .callbackEventLocation(makeCallback(
                        JobEventController.EVENT_CALLBACK_LOCATION, job
                ))
                .callbackArtifactLocation(makeCallback(
                        JobArtifactController.ARTIFACT_CALLBACK_LOCATION, job
                ))
                .build();
    }

    private String makeCallback(String fragment, Job job) {
        return dispatchRoot + "/runs" + fragment
                .replace("{runUuid}", job.getRun().getUuid().toString())
                .replace("{jobUuid}", job.getUuid().toString());
    }
}
