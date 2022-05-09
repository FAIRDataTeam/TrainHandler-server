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
package org.fairdatatrain.trainhandler.service.job;

import org.fairdatatrain.trainhandler.api.dto.job.JobEventCreateDTO;
import org.fairdatatrain.trainhandler.api.dto.job.JobEventDTO;
import org.fairdatatrain.trainhandler.data.model.Job;
import org.fairdatatrain.trainhandler.data.model.JobEvent;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.UUID;

@Component
public class JobEventMapper {
    public JobEventDTO toDTO(JobEvent jobEvent) {
        return JobEventDTO
                .builder()
                .uuid(jobEvent.getUuid())
                .type(jobEvent.getType())
                .message(jobEvent.getMessage())
                .payload(jobEvent.getPayload())
                .resultStatus(jobEvent.getResultStatus())
                .createdAt(jobEvent.getCreatedAt().toInstant())
                .occurredAt(jobEvent.getOccurredAt().toInstant())
                .build();
    }

    public JobEvent fromCreateDTO(JobEventCreateDTO reqDto, Job job) {
        final Timestamp now = Timestamp.from(Instant.now());
        return JobEvent
                .builder()
                .uuid(UUID.randomUUID())
                .message(reqDto.getMessage())
                .payload(reqDto.getPayload())
                .resultStatus(reqDto.getResultStatus())
                .type(reqDto.getType())
                .occurredAt(Timestamp.from(reqDto.getOccurredAt()))
                .job(job)
                .createdAt(now)
                .updatedAt(now)
                .build();
    }
}
