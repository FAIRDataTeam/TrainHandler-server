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
package org.fairdatatrain.trainhandler.api.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.fairdatatrain.trainhandler.api.dto.run.RunCreateDTO;
import org.fairdatatrain.trainhandler.api.dto.run.RunDTO;
import org.fairdatatrain.trainhandler.api.dto.run.RunUpdateDTO;
import org.fairdatatrain.trainhandler.exception.CannotPerformException;
import org.fairdatatrain.trainhandler.exception.NotFoundException;
import org.fairdatatrain.trainhandler.service.run.RunService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.async.DeferredResult;

import javax.validation.Valid;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Tag(name = "Runs")
@RestController
@RequestMapping("/runs")
@RequiredArgsConstructor
public class RunController {

    private final RunService runService;

    @PostMapping(
            path = "",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(code = HttpStatus.CREATED)
    public RunDTO create(@Valid @RequestBody RunCreateDTO reqDto) throws NotFoundException {
        return runService.create(reqDto);
    }

    @GetMapping(
            path = "/{uuid}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public DeferredResult<RunDTO> pollRun(
            @PathVariable UUID uuid,
            @RequestParam(required = false, defaultValue = "0") Long after
    ) throws NotFoundException {
        // TODO: configurable timeout
        final RunDTO currentRun = runService.getSingle(uuid);
        final DeferredResult<RunDTO> run = new DeferredResult<>(
                10 * 1000L, currentRun
        );
        CompletableFuture.runAsync(() -> {
            try {
                run.setResult(runService.poll(uuid, after, currentRun));
            }
            catch (Exception ex) {
                ex.printStackTrace();
                run.setResult(null);
                // TODO: better error handling
            }
        });
        return run;
    }

    @PutMapping(
            path = "/{uuid}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public RunDTO update(
            @PathVariable UUID uuid, @Valid @RequestBody RunUpdateDTO reqDto
    ) throws NotFoundException, CannotPerformException {
        // TODO: abort? duplicate?
        return runService.update(uuid, reqDto);
    }
}
