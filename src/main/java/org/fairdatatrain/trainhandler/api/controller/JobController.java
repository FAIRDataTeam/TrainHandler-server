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
import org.fairdatatrain.trainhandler.api.dto.job.JobDTO;
import org.fairdatatrain.trainhandler.api.dto.job.JobEventCreateDTO;
import org.fairdatatrain.trainhandler.api.dto.job.JobEventDTO;
import org.fairdatatrain.trainhandler.api.dto.job.JobSimpleDTO;
import org.fairdatatrain.trainhandler.exception.JobSecurityException;
import org.fairdatatrain.trainhandler.exception.NotFoundException;
import org.fairdatatrain.trainhandler.service.job.JobEventService;
import org.fairdatatrain.trainhandler.service.job.JobService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.async.DeferredResult;

import javax.validation.Valid;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Tag(name = "Runs")
@RestController
@RequestMapping("/runs")
@RequiredArgsConstructor
public class JobController {
    private final JobService jobService;

    private final JobEventService jobEventService;

    @GetMapping(path = "/{runUuid}/jobs", produces = MediaType.APPLICATION_JSON_VALUE)
    public Page<JobSimpleDTO> getJobs(@PathVariable UUID runUuid, Pageable pageable) {
        return jobService.getJobsForRun(runUuid, pageable);
    }

    @GetMapping(
            path = "/{runUuid}/jobs/{jobUuid}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public JobDTO getJobs(
            @PathVariable UUID runUuid, @PathVariable UUID jobUuid
    ) throws NotFoundException {
        return jobService.getSingle(runUuid, jobUuid);
    }

    @GetMapping(
            path = "/{runUuid}/jobs/{jobUuid}/events",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public List<JobEventDTO> getJobEvents(
            @PathVariable UUID runUuid, @PathVariable UUID jobUuid
    ) throws NotFoundException {
        return jobEventService.getEvents(runUuid, jobUuid);
    }

    @GetMapping(
            path = "/{runUuid}/jobs/{jobUuid}/events/poll",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public DeferredResult<List<JobEventDTO>> pollNewerJobEvents(
            @PathVariable UUID runUuid,
            @PathVariable UUID jobUuid,
            @RequestParam(required = false) UUID afterEventUuid
    ) {
        // TODO: configurable timeout
        final DeferredResult<List<JobEventDTO>> events = new DeferredResult<>(
                10 * 1000L, Collections.emptyList()
        );
        CompletableFuture.runAsync(() -> {
            try {
                events.setResult(jobEventService.pollEvents(runUuid, jobUuid, afterEventUuid));
            }
            catch (Exception ex) {
                ex.printStackTrace();
                events.setResult(Collections.emptyList());
                // TODO: better error handling
            }
        });
        return events;
    }

    @PostMapping(
            path = "/{runUuid}/jobs/{jobUuid}/events",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public JobEventDTO addJobEvent(
            @PathVariable UUID runUuid,
            @PathVariable UUID jobUuid,
            @RequestBody @Valid JobEventCreateDTO reqDto
    ) throws NotFoundException, JobSecurityException {
        final JobEventDTO dto = jobEventService.createEvent(runUuid, jobUuid, reqDto);
        jobEventService.notify(jobUuid);
        return dto;
    }
}