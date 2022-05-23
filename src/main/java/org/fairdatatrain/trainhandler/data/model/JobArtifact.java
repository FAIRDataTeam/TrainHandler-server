package org.fairdatatrain.trainhandler.data.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.fairdatatrain.trainhandler.data.model.base.BaseEntity;
import org.fairdatatrain.trainhandler.data.model.enums.ArtifactStorage;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.sql.Timestamp;

@Entity(name = "JobArtifact")
@Table(name = "job_artifact")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class JobArtifact extends BaseEntity {

    @NotNull
    @Column(name = "display_name")
    private String displayName;

    @NotNull
    @Column(name = "filename", nullable = false)
    private String filename;

    @NotNull
    @Column(name = "bytesize", nullable = false)
    private Long bytesize;

    @NotNull
    @Column(name = "hash", nullable = false)
    private String hash;

    @NotNull
    @Column(name = "content_type", nullable = false)
    private String contentType;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "storage", columnDefinition = "artifact_storage", nullable = false)
    private ArtifactStorage storage;

    @NotNull
    @Column(name = "occurred_at", nullable = false)
    private Timestamp occurredAt;

    @Lob
    @Column(name = "data")
    @Type(type = "org.hibernate.type.BinaryType")
    private byte[] data;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "job_id", nullable = false)
    private Job job;

}
