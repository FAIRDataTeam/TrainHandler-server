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
import org.fairdatatrain.trainhandler.api.dto.plan.PlanCreateDTO;
import org.fairdatatrain.trainhandler.api.dto.plan.PlanDTO;
import org.fairdatatrain.trainhandler.api.dto.plan.PlanSimpleDTO;
import org.fairdatatrain.trainhandler.api.dto.plan.PlanUpdateDTO;
import org.fairdatatrain.trainhandler.api.dto.run.RunSimpleDTO;
import org.fairdatatrain.trainhandler.exception.CannotPerformException;
import org.fairdatatrain.trainhandler.exception.NotFoundException;
import org.fairdatatrain.trainhandler.service.plan.PlanService;
import org.fairdatatrain.trainhandler.service.run.RunService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.UUID;

@Tag(name = "Plans")
@RestController
@RequestMapping("/plans")
@RequiredArgsConstructor
public class PlanController {

    private final PlanService planService;

    private final RunService runService;

    @GetMapping(path = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public Page<PlanSimpleDTO> getPaged(
            @RequestParam(value = "query", required = false, defaultValue = "") String query,
            Pageable pageable) {
        return planService.getPaged(query, pageable);
    }

    @PostMapping(
            path = "",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(code = HttpStatus.CREATED)
    public PlanDTO create(@Valid @RequestBody PlanCreateDTO reqDto) throws NotFoundException {
        return planService.create(reqDto);
    }

    @GetMapping(path = "/{uuid}", produces = MediaType.APPLICATION_JSON_VALUE)
    public PlanDTO getSingle(@PathVariable UUID uuid) throws NotFoundException {
        return planService.getSingle(uuid);
    }

    @PutMapping(
            path = "/{uuid}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public PlanDTO update(@PathVariable UUID uuid, @Valid @RequestBody PlanUpdateDTO reqDto)
            throws NotFoundException, CannotPerformException {
        return planService.update(uuid, reqDto);
    }

    @DeleteMapping(path = "/{uuid}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID uuid) throws NotFoundException, CannotPerformException {
        planService.delete(uuid);
    }

    @GetMapping(path = "/{uuid}/runs", produces = MediaType.APPLICATION_JSON_VALUE)
    public Page<RunSimpleDTO> getPlanRuns(@PathVariable UUID uuid, Pageable pageable) {
        return runService.getRunsForPlanUuid(uuid, pageable);
    }
}
