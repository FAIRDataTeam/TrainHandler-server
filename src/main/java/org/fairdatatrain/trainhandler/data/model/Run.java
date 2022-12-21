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

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.fairdatatrain.trainhandler.data.model.base.BaseEntity;
import org.fairdatatrain.trainhandler.data.model.enums.RunStatus;

import java.sql.Timestamp;
import java.util.List;

@Entity(name = "Run")
@Table(name = "run")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class Run extends BaseEntity {

    @NotBlank
    @NotNull
    @Column(name = "display_name", nullable = false)
    private String displayName;

    @NotNull
    @Column(name = "note", nullable = false)
    private String note;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", columnDefinition = "run_status", nullable = false)
    private RunStatus status;

    @Column(name = "should_start_at")
    private Timestamp shouldStartAt;

    @Column(name = "started_at")
    private Timestamp startedAt;

    @Column(name = "finished_at")
    private Timestamp finishedAt;

    @Column(name = "version", nullable = false)
    private Long version;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "plan_id")
    private Plan plan;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "run")
    private List<Job> jobs;

    public boolean isFinished() {
        return getStatus().equals(RunStatus.FAILED)
                || getStatus().equals(RunStatus.FINISHED)
                || getStatus().equals(RunStatus.ERRORED);
    }
}
