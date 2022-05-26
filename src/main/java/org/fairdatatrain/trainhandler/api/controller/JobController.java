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
package org.fairdatatrain.trainhandler.api.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.fairdatatrain.trainhandler.api.dto.job.*;
import org.fairdatatrain.trainhandler.config.DispatcherConfig;
import org.fairdatatrain.trainhandler.exception.NotFoundException;
import org.fairdatatrain.trainhandler.service.job.JobService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.async.DeferredResult;

import java.util.UUID;

@Tag(name = "Runs")
@RestController
@RequestMapping("/runs")
@RequiredArgsConstructor
public class JobController {

    private final JobService jobService;

    private final DispatcherConfig config;

    @GetMapping(path = "/{runUuid}/jobs", produces = MediaType.APPLICATION_JSON_VALUE)
    public Page<JobSimpleDTO> getJobs(@PathVariable UUID runUuid, Pageable pageable) {
        return jobService.getJobsForRun(runUuid, pageable);
    }

    @GetMapping(
            path = "/{runUuid}/jobs/{jobUuid}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public DeferredResult<JobDTO> pollJob(
            @PathVariable UUID runUuid,
            @PathVariable UUID jobUuid,
            @RequestParam(required = false, defaultValue = "0") Long after
    ) throws NotFoundException {
        final JobDTO currentJob = jobService.getSingle(runUuid, jobUuid);
        final DeferredResult<JobDTO> result = new DeferredResult<>(
                config.getPolling().getTimeoutMs(), currentJob
        );
        jobService.poll(jobUuid, result, after, currentJob);
        return result;
    }
}
