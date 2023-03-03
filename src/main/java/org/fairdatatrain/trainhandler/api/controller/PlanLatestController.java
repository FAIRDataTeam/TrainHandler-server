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
import org.fairdatatrain.trainhandler.api.dto.job.JobArtifactDTO;
import org.fairdatatrain.trainhandler.data.model.JobArtifact;
import org.fairdatatrain.trainhandler.exception.NotFoundException;
import org.fairdatatrain.trainhandler.service.plan.latest.PlanLatestService;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

import static java.lang.String.format;

@Tag(name = "Plans")
@RestController
@RequestMapping("/plans")
@RequiredArgsConstructor
public class PlanLatestController {

    private final PlanLatestService planLatestService;

    @GetMapping(path = "/{planUuid}/latest/artifacts", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<JobArtifactDTO> getLatestArtifacts(@PathVariable UUID planUuid) throws NotFoundException {
        return planLatestService.getLatestArtifacts(planUuid);
    }

    @GetMapping(path = "/{planUuid}/latest/artifacts/{artifactUuid}")
    public ResponseEntity<Resource> downloadLatestArtifact(
            @PathVariable UUID planUuid, @PathVariable UUID artifactUuid
    ) throws NotFoundException {
        final JobArtifact artifact = planLatestService.getLatestJobArtifact(planUuid, artifactUuid);
        final byte[] data = planLatestService.getArtifactData(artifact);
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

    @GetMapping(
            path = "/{planUuid}/target/{targetUuid}/latest/artifacts",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public List<JobArtifactDTO> getLatestArtifactsOfTarget(
            @PathVariable UUID planUuid, @PathVariable UUID targetUuid
    ) throws NotFoundException {
        return planLatestService.getLatestArtifactsOfTarget(planUuid, targetUuid);
    }
}
