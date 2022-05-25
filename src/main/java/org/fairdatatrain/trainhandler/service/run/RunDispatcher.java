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
package org.fairdatatrain.trainhandler.service.run;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.fairdatatrain.trainhandler.data.model.Job;
import org.fairdatatrain.trainhandler.data.model.Run;
import org.fairdatatrain.trainhandler.data.model.enums.JobStatus;
import org.fairdatatrain.trainhandler.data.model.enums.RunStatus;
import org.fairdatatrain.trainhandler.data.repository.JobRepository;
import org.fairdatatrain.trainhandler.data.repository.RunRepository;
import org.fairdatatrain.trainhandler.service.dispatch.DispatchService;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static java.lang.String.format;
import static org.fairdatatrain.trainhandler.utils.TimeUtils.now;

@Slf4j
@Service
@RequiredArgsConstructor
public class RunDispatcher {

    private final DispatchService dispatchService;

    private final JobRepository jobRepository;

    private final RunRepository runRepository;

    @Async
    @Transactional
    public void dispatchNewRun(Run run) {
        if (run.getShouldStartAt() != null || !run.getStatus().equals(RunStatus.PREPARED)) {
            return;
        }
        dispatchRun(run);
    }

    @Scheduled(
            initialDelayString = "${dispatch.initDelay:PT1M}",
            fixedRateString = "${dispatch.interval:PT1M}"
    )
    public void dispatchScheduledRuns() {
        log.info("Dispatching scheduled runs");
        final Set<UUID> dispatchedRuns = new HashSet<>();
        boolean dispatching = true;
        while (dispatching) {
            final UUID dispatchedRun = tryDispatchRun();
            dispatching = dispatchedRun != null && !dispatchedRuns.contains(dispatchedRun);
            if (dispatching) {
                dispatchedRuns.add(dispatchedRun);
                log.info("Dispatched run: " + dispatchedRun);
            }
            else {
                log.info("No more runs to be dispatched now");
            }
        }
    }

    @Transactional
    public UUID tryDispatchRun() {
        final Optional<Run> optionalRun = runRepository.findRunToDispatch(now());
        if (optionalRun.isEmpty()) {
            return null;
        }
        final Run run = optionalRun.get();
        log.info("Run selected for dispatching: " + run.getUuid());
        dispatchRun(run);
        return run.getUuid();
    }

    private void dispatchRun(Run run) {
        run.setStartedAt(now());
        run.getJobs().forEach(this::dispatchJob);
        run.setStatus(RunStatus.RUNNING);
        runRepository.save(run);
    }

    private void dispatchJob(Job job) {
        job.setStartedAt(now());
        try {
            dispatchService.dispatch(job);
            job.setStatus(JobStatus.RUNNING);
        }
        catch (Exception ex) {
            log.warn(format("Failed to dispatch job %s: %s", job.getUuid(), ex.getMessage()));
            // TODO add event (?)
            job.setStatus(JobStatus.ERRORED);
        }
        finally {
            jobRepository.save(job);
        }
    }
}
