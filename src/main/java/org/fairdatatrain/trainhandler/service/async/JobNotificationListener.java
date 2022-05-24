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

import lombok.Synchronized;
import lombok.extern.slf4j.Slf4j;
import org.fairdatatrain.trainhandler.api.dto.job.JobDTO;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.async.DeferredResult;

import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.UUID;

import static java.lang.String.format;

@Component
@Slf4j
public class JobNotificationListener {

    private final Map<UUID, PriorityQueue<PollJobContainer>> queues = new HashMap<>();

    @EventListener
    public void handleJobEventNotification(JobNotification notification) {
        if (notification.getJob() != null) {
            updateResults(notification.getJob());
        }
    }

    @Synchronized
    public void updateResults(JobDTO job) {
        log.info(format(
                "Updating results for job '%s' (version '%s')",
                job.getUuid(), job.getVersion()
        ));
        if (queues.containsKey(job.getUuid())) {
            while (!queues.get(job.getUuid()).isEmpty()) {
                final PollJobContainer container = queues.get(job.getUuid()).peek();
                if (container == null) {
                    queues.get(job.getUuid()).poll();
                }
                else if (container.getVersion() <= job.getVersion()) {
                    log.info(format(
                            "Sending job for requested version: %s",
                            container.getVersion()
                    ));
                    container.getResult().setResult(job);
                    queues.get(job.getUuid()).poll();
                }
                else {
                    log.info(format(
                            "Next requested job version: %s",
                            container.getVersion())
                    );
                    break;
                }
            }
        }
        log.info(format(
                "Updating results for job '%s' (version '%s'): done",
                job.getUuid(), job.getVersion())
        );
    }

    @Synchronized
    public void enqueue(UUID jobUuid, Long version, DeferredResult<JobDTO> result) {
        if (!queues.containsKey(jobUuid)) {
            queues.put(jobUuid, new PriorityQueue<>());
        }
        queues.get(jobUuid).add(
                PollJobContainer.builder()
                        .version(version)
                        .result(result)
                        .build()
        );
    }
}
