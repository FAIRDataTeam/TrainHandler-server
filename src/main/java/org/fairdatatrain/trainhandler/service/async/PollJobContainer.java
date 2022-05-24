package org.fairdatatrain.trainhandler.service.async;

import lombok.*;
import org.fairdatatrain.trainhandler.api.dto.job.JobDTO;
import org.springframework.web.context.request.async.DeferredResult;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class PollJobContainer implements Comparable<PollJobContainer> {

    private Long version;

    private DeferredResult<JobDTO> result;

    @Override
    public int compareTo(PollJobContainer o) {
        return version.compareTo(o.version);
    }
}
