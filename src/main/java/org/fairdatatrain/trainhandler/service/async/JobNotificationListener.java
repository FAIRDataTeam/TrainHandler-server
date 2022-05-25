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

import lombok.RequiredArgsConstructor;
import lombok.Synchronized;
import lombok.extern.slf4j.Slf4j;
import org.fairdatatrain.trainhandler.api.dto.job.JobDTO;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.async.DeferredResult;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;

import static java.lang.String.format;

@Component
@Slf4j
@RequiredArgsConstructor
public class JobNotificationListener {

    private final Long pollTimeout;

    private final Map<UUID, List<PollContainer<JobDTO>>> queues = new HashMap<>();

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
        final Instant now = Instant.now();
        if (queues.containsKey(job.getUuid())) {
            final List<PollContainer<JobDTO>> remainings = new ArrayList<>();
            queues.get(job.getUuid())
                    .stream()
                    .filter(container -> container.getTimeoutsAt().isAfter(now))
                    .forEach(container -> {
                        if (container.getVersion() <= job.getVersion()) {
                            log.info(format("Sending job for requested version: %s",
                                    container.getVersion()));
                            container.getResult().setResult(job);
                        }
                        else {
                            remainings.add(container);
                        }
                    });
            queues.put(job.getUuid(), remainings);
        }
        log.info(format(
                "Updating results for job '%s' (version '%s'): done",
                job.getUuid(), job.getVersion())
        );
    }

    @Synchronized
    public void enqueue(UUID jobUuid, Long version, DeferredResult<JobDTO> result) {
        final Instant now = Instant.now();
        if (!queues.containsKey(jobUuid)) {
            queues.put(jobUuid, new ArrayList<>());
        }
        else {
            final List<PollContainer<JobDTO>> remainings = new ArrayList<>();
            queues.get(jobUuid).forEach(container -> {
                if (container.getTimeoutsAt().isAfter(now)) {
                    remainings.add(container);
                }
            });
            queues.put(jobUuid, remainings);
        }
        queues.get(jobUuid).add(
                new PollContainer<>(
                        version,
                        Instant.now().plus(pollTimeout, ChronoUnit.MILLIS),
                        result
                )
        );
    }
}
