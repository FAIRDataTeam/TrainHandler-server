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
package org.fairdatatrain.trainhandler.data.model;

import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.fairdatatrain.trainhandler.data.model.base.BaseEntity;
import org.fairdatatrain.trainhandler.data.model.enums.JobEventType;
import org.fairdatatrain.trainhandler.data.model.enums.JobStatus;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.sql.Timestamp;

@Entity(name = "JobEvent")
@Table(name = "job_event")
@TypeDef(name = "json", typeClass = JsonBinaryType.class)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class JobEvent extends BaseEntity {

    @Enumerated(EnumType.STRING)
    @Column(name = "type", columnDefinition = "job_event_type", nullable = false)
    private JobEventType type;

    @Enumerated(EnumType.STRING)
    @Column(name = "result_status", columnDefinition = "job_status")
    private JobStatus resultStatus;

    @NotNull
    @Column(name = "occurred_at")
    private Timestamp occurredAt;

    @NotNull
    @Column(name = "message", nullable = false)
    private String message;

    @Column(name = "payload", columnDefinition = "json")
    @Type(type = "json")
    private String payload;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "job_id")
    private Job job;
}