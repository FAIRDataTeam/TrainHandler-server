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
import org.fairdatatrain.trainhandler.data.model.JobArtifact;
import org.fairdatatrain.trainhandler.exception.JobSecurityException;
import org.fairdatatrain.trainhandler.exception.NotFoundException;
import org.fairdatatrain.trainhandler.service.job.artifact.JobArtifactService;
import org.fairdatatrain.trainhandler.service.job.event.JobEventService;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.UUID;

import static java.lang.String.format;

@Tag(name = "Runs")
@PreAuthorize("hasRole('user')")
@RestController
@RequestMapping("/runs")
@RequiredArgsConstructor
public class JobArtifactController {

    public static final String ARTIFACT_CALLBACK_LOCATION = "/{runUuid}/jobs/{jobUuid}/artifacts";

    private final JobArtifactService jobArtifactService;

    private final JobEventService jobEventService;

    @GetMapping(
            path = "/{runUuid}/jobs/{jobUuid}/artifacts",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public List<JobArtifactDTO> getJobArtifacts(
            @PathVariable UUID runUuid, @PathVariable UUID jobUuid
    ) throws NotFoundException {
        return jobArtifactService.getArtifacts(runUuid, jobUuid);
    }

    @GetMapping(
            path = "/{runUuid}/jobs/{jobUuid}/artifacts/{artifactUuid}/download"
    )
    public ResponseEntity<Resource> getJobArtifactData(
            @PathVariable UUID runUuid,
            @PathVariable UUID jobUuid,
            @PathVariable UUID artifactUuid
    ) throws NotFoundException {
        final JobArtifact artifact = jobArtifactService.getArtifact(runUuid, jobUuid, artifactUuid);
        final byte[] data = jobArtifactService.getArtifactData(artifact);
        final ByteArrayResource resource = new ByteArrayResource(data);
        return ResponseEntity
                .ok()
                .contentLength(artifact.getBytesize())
                .contentType(MediaType.parseMediaType(artifact.getContentType()))
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        format("attachment;filename=%s", artifact.getFilename())
                )
                .body(resource);
    }

    @PostMapping(
            path = ARTIFACT_CALLBACK_LOCATION,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public JobArtifactDTO addJobArtifact(
            @PathVariable UUID runUuid,
            @PathVariable UUID jobUuid,
            @RequestBody @Valid JobArtifactCreateDTO reqDto
    ) throws NotFoundException, JobSecurityException {
        final JobArtifactDTO dto = jobArtifactService.createArtifact(runUuid, jobUuid, reqDto);
        jobEventService.notify(jobUuid);
        return dto;
    }
}
