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
import org.fairdatatrain.trainhandler.exception.JobSecurityException;
import org.fairdatatrain.trainhandler.exception.NotFoundException;
import org.fairdatatrain.trainhandler.service.job.event.JobEventService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.UUID;

@Tag(name = "Runs")
@RestController
@RequestMapping("/runs")
@RequiredArgsConstructor
public class JobEventController {

    public static final String EVENT_CALLBACK_LOCATION = "/{runUuid}/jobs/{jobUuid}/events";

    private final JobEventService jobEventService;

    @GetMapping(
            path = "/{runUuid}/jobs/{jobUuid}/events",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public List<JobEventDTO> getJobEvents(
            @PathVariable UUID runUuid,
            @PathVariable UUID jobUuid
    ) throws NotFoundException {
        return jobEventService.getEvents(runUuid, jobUuid);
    }

    @PostMapping(
            path = EVENT_CALLBACK_LOCATION,
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
