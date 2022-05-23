package org.fairdatatrain.trainhandler.api.dto.job;

import lombok.*;

import java.time.Instant;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder(toBuilder = true)
public class JobArtifactDTO {

    private UUID uuid;

    private String displayName;

    private String filename;

    private Long bytesize;

    private String hash;

    private Instant occurredAt;

    private Instant createdAt;

    private Instant updatedAt;
}
