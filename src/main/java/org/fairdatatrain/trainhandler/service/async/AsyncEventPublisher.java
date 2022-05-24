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
package org.fairdatatrain.trainhandler.service.async;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.fairdatatrain.trainhandler.api.dto.job.JobDTO;
import org.fairdatatrain.trainhandler.api.dto.run.RunDTO;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import static java.lang.String.format;

@Component
@AllArgsConstructor
@Slf4j
public class AsyncEventPublisher {

    private final ApplicationEventPublisher publisher;

    public void publishNewJobEventNotification(final RunDTO run, final JobDTO job) {
        log.info(
                format(
                        "Publishing new job notification for job %s (run %s)",
                        job.getUuid(), run.getUuid()
                )
        );
        publisher.publishEvent(new JobNotification(this, run, job));
    }

    public void publishNewJobEventNotification(final RunDTO run) {
        log.info(
                format(
                        "Publishing new run notification for run %s",
                        run.getUuid()
                )
        );
        publisher.publishEvent(new JobNotification(this, run, null));
    }

}
