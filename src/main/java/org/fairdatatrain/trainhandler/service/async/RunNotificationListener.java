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
import org.fairdatatrain.trainhandler.api.dto.run.RunDTO;
import org.fairdatatrain.trainhandler.config.DispatcherConfig;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.async.DeferredResult;

import java.time.Instant;
import java.util.*;

import static java.lang.String.format;

@Component
@Slf4j
@RequiredArgsConstructor
public class RunNotificationListener {

    private final DispatcherConfig config;

    private final Map<UUID, List<PollContainer<RunDTO>>> queues = new HashMap<>();

    @EventListener
    public void handleJobEventNotification(JobNotification notification) {
        updateResults(notification.getRun());
    }

    @Synchronized
    public void updateResults(RunDTO run) {
        log.info(format(
                "Updating results for run '%s' (version '%s')",
                run.getUuid(), run.getVersion())
        );
        final Instant now = Instant.now();
        if (queues.containsKey(run.getUuid())) {
            final List<PollContainer<RunDTO>> remainings = new ArrayList<>();
            queues.get(run.getUuid())
                    .stream()
                    .filter(container -> container.getTimeoutsAt().isAfter(now))
                    .forEach(container -> {
                        if (container.getVersion() <= run.getVersion()) {
                            log.info(format("Sending run for requested version: %s",
                                    container.getVersion()));
                            container.getResult().setResult(run);
                        }
                        else {
                            remainings.add(container);
                        }
                    });
            queues.put(run.getUuid(), remainings);
        }
        log.info(format(
                "Updating results for run '%s' (version '%s'): done",
                run.getUuid(), run.getVersion())
        );
    }

    @Synchronized
    public void enqueue(UUID runUuid, Long version, DeferredResult<RunDTO> result) {
        final Instant now = Instant.now();
        if (!queues.containsKey(runUuid)) {
            queues.put(runUuid, new ArrayList<>());
        }
        else {
            final List<PollContainer<RunDTO>> remainings = new ArrayList<>();
            queues.get(runUuid).forEach(container -> {
                if (container.getTimeoutsAt().isAfter(now)) {
                    remainings.add(container);
                }
            });
            queues.put(runUuid, remainings);
        }
        queues.get(runUuid).add(
                new PollContainer<>(
                        version,
                        config.getPolling().getTimeoutForCurrentPoll(),
                        result
                )
        );
    }

}
