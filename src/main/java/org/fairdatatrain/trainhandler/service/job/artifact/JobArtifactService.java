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

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.fairdatatrain.trainhandler.api.dto.job.JobArtifactCreateDTO;
import org.fairdatatrain.trainhandler.api.dto.job.JobArtifactDTO;
import org.fairdatatrain.trainhandler.data.model.Job;
import org.fairdatatrain.trainhandler.data.model.JobArtifact;
import org.fairdatatrain.trainhandler.data.model.enums.ArtifactStorage;
import org.fairdatatrain.trainhandler.data.repository.JobArtifactRepository;
import org.fairdatatrain.trainhandler.data.repository.JobRepository;
import org.fairdatatrain.trainhandler.exception.JobSecurityException;
import org.fairdatatrain.trainhandler.exception.NotFoundException;
import org.fairdatatrain.trainhandler.service.job.JobService;
import org.fairdatatrain.trainhandler.service.run.RunService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.validation.ValidationException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static java.lang.String.format;
import static org.fairdatatrain.trainhandler.utils.HashUtils.bytesToHex;

@Service
@RequiredArgsConstructor
@Slf4j
public class JobArtifactService {

    public static final String ENTITY_NAME = "JobArtifact";

    private final JobRepository jobRepository;

    private final JobArtifactRepository jobArtifactRepository;

    private final JobArtifactMapper jobArtifactMapper;

    private final JobService jobService;

    @PersistenceContext
    private final EntityManager entityManager;

    public JobArtifact getByIdOrThrow(UUID uuid) throws NotFoundException {
        return jobArtifactRepository
                .findById(uuid)
                .orElseThrow(() -> new NotFoundException(ENTITY_NAME, uuid));
    }

    public List<JobArtifactDTO> getArtifacts(UUID runUuid, UUID jobUuid) throws NotFoundException {
        final Job job = jobService.getByIdOrThrow(jobUuid);
        if (!job.getRun().getUuid().equals(runUuid)) {
            throw new NotFoundException(JobService.ENTITY_NAME, jobUuid);
        }
        return job.getArtifacts().stream().map(jobArtifactMapper::toDTO).toList();
    }

    public JobArtifact getArtifact(
            UUID runUuid, UUID jobUuid, UUID artifactUuid
    ) throws NotFoundException {
        final JobArtifact artifact = getByIdOrThrow(artifactUuid);
        if (!artifact.getJob().getUuid().equals(jobUuid)) {
            throw new NotFoundException(JobService.ENTITY_NAME, jobUuid);
        }
        if (!artifact.getJob().getRun().getUuid().equals(runUuid)) {
            throw new NotFoundException(RunService.ENTITY_NAME, runUuid);
        }
        return artifact;
    }

    public byte[] getArtifactData(JobArtifact artifact) {
        if (artifact.getStorage().equals(ArtifactStorage.POSTGRES)) {
            return artifact.getData();
        }
        throw new RuntimeException(
                format("Unsupported artifact storage: %s", artifact.getStorage())
        );
    }

    @Transactional
    public JobArtifactDTO createArtifact(
            UUID runUuid, UUID jobUuid, JobArtifactCreateDTO reqDto
    ) throws NotFoundException, JobSecurityException {
        final Job job = jobService.getByIdOrThrow(jobUuid);
        if (!job.getRun().getUuid().equals(runUuid)) {
            throw new NotFoundException(JobService.ENTITY_NAME, jobUuid);
        }
        if (!Objects.equals(job.getSecret(), reqDto.getSecret())) {
            throw new JobSecurityException("Incorrect secret for creating job event");
        }
        if (job.getRemoteId() == null) {
            job.setRemoteId(reqDto.getRemoteId());
            jobRepository.save(job);
        }
        else if (!Objects.equals(job.getRemoteId(), reqDto.getRemoteId())) {
            throw new JobSecurityException("Incorrect remote ID for creating job event");
        }
        final byte[] data = Base64.getDecoder().decode(reqDto.getBase64data());
        validate(reqDto, data);
        final JobArtifact jobArtifact = jobArtifactRepository.save(
                jobArtifactMapper.fromCreateDTO(reqDto, job, data)
        );
        entityManager.flush();
        entityManager.refresh(jobArtifact);
        return jobArtifactMapper.toDTO(jobArtifact);
    }

    private void validate(JobArtifactCreateDTO reqDto, byte[] data) {
        MessageDigest digest = null;
        try {
            digest = MessageDigest.getInstance("SHA-256");
        }
        catch (NoSuchAlgorithmException exc) {
            throw new RuntimeException("SHA-256 hashing is not supported");
        }
        final String hash = bytesToHex(digest.digest(data));
        if (data.length != reqDto.getBytesize()) {
            throw new ValidationException("Bytesize does not match");
        }
        if (!hash.equals(reqDto.getHash())) {
            throw new ValidationException("SHA-256 hash does not match");
        }
    }
}
