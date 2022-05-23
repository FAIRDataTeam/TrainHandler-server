package org.fairdatatrain.trainhandler.service.job.artifact;

import org.fairdatatrain.trainhandler.api.dto.job.JobArtifactDTO;
import org.fairdatatrain.trainhandler.data.model.JobArtifact;
import org.springframework.stereotype.Component;

@Component
public class JobArtifactMapper {
    public JobArtifactDTO toDTO(JobArtifact artifact) {
        return JobArtifactDTO
                .builder()
                .uuid(artifact.getUuid())
                .displayName(artifact.getDisplayName())
                .filename(artifact.getFilename())
                .bytesize(artifact.getBytesize())
                .hash(artifact.getHash())
                .occurredAt(artifact.getOccurredAt().toInstant())
                .createdAt(artifact.getCreatedAt().toInstant())
                .updatedAt(artifact.getUpdatedAt().toInstant())
                .build();
    }
}
