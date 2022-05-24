package org.fairdatatrain.trainhandler.service.async;

import lombok.*;
import org.fairdatatrain.trainhandler.api.dto.run.RunDTO;
import org.springframework.web.context.request.async.DeferredResult;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class PollRunContainer implements Comparable<PollRunContainer> {

    private Long version;

    private DeferredResult<RunDTO> result;

    @Override
    public int compareTo(PollRunContainer o) {
        return version.compareTo(o.version);
    }
}
