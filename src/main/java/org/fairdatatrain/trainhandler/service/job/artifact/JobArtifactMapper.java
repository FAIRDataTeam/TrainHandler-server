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
package org.fairdatatrain.trainhandler.service.job.artifact;

import org.fairdatatrain.trainhandler.api.dto.job.JobArtifactCreateDTO;
import org.fairdatatrain.trainhandler.api.dto.job.JobArtifactDTO;
import org.fairdatatrain.trainhandler.data.model.Job;
import org.fairdatatrain.trainhandler.data.model.JobArtifact;
import org.fairdatatrain.trainhandler.data.model.enums.ArtifactStorage;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.util.UUID;

import static org.fairdatatrain.trainhandler.utils.TimeUtils.now;

@Component
public class JobArtifactMapper {
    public JobArtifactDTO toDTO(JobArtifact artifact) {
        return JobArtifactDTO
                .builder()
                .uuid(artifact.getUuid())
                .displayName(artifact.getDisplayName())
                .filename(artifact.getFilename())
                .bytesize(artifact.getBytesize())
                .contentType(artifact.getContentType())
                .hash(artifact.getHash())
                .occurredAt(artifact.getOccurredAt().toInstant())
                .createdAt(artifact.getCreatedAt().toInstant())
                .updatedAt(artifact.getUpdatedAt().toInstant())
                .build();
    }

    public JobArtifact fromCreateDTO(JobArtifactCreateDTO reqDto, Job job, byte[] data) {
        final Timestamp now = now();
        return JobArtifact
                .builder()
                .uuid(UUID.randomUUID())
                .displayName(reqDto.getDisplayName())
                .filename(reqDto.getFilename())
                .hash(reqDto.getHash())
                .bytesize(reqDto.getBytesize())
                .contentType(reqDto.getContentType())
                .data(data)
                .storage(ArtifactStorage.POSTGRES)
                .occurredAt(Timestamp.from(reqDto.getOccurredAt()))
                .job(job)
                .createdAt(now)
                .updatedAt(now)
                .build();
    }
}
