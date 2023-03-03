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
package org.fairdatatrain.trainhandler.service.job.event;

import org.fairdatatrain.trainhandler.api.dto.job.JobEventCreateDTO;
import org.fairdatatrain.trainhandler.api.dto.job.JobEventDTO;
import org.fairdatatrain.trainhandler.data.model.Job;
import org.fairdatatrain.trainhandler.data.model.JobEvent;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.util.UUID;

import static org.fairdatatrain.trainhandler.utils.TimeUtils.now;

@Component
public class JobEventMapper {
    public JobEventDTO toDTO(JobEvent jobEvent) {
        return JobEventDTO
                .builder()
                .uuid(jobEvent.getUuid())
                .message(jobEvent.getMessage())
                .resultStatus(jobEvent.getResultStatus())
                .createdAt(jobEvent.getCreatedAt().toInstant().toString())
                .updatedAt(jobEvent.getUpdatedAt().toInstant().toString())
                .occurredAt(jobEvent.getOccurredAt().toInstant().toString())
                .build();
    }

    public JobEvent fromCreateDTO(JobEventCreateDTO reqDto, Job job) {
        final Timestamp now = now();
        return JobEvent
                .builder()
                .uuid(UUID.randomUUID())
                .message(reqDto.getMessage())
                .resultStatus(reqDto.getResultStatus())
                .occurredAt(Timestamp.from(reqDto.getOccurredAt()))
                .job(job)
                .createdAt(now)
                .updatedAt(now)
                .build();
    }
}
