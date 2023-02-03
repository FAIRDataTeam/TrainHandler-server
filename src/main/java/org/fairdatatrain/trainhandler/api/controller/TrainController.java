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
import org.fairdatatrain.trainhandler.api.dto.station.StationSimpleDTO;
import org.fairdatatrain.trainhandler.api.dto.train.TrainCreateDTO;
import org.fairdatatrain.trainhandler.api.dto.train.TrainDTO;
import org.fairdatatrain.trainhandler.api.dto.train.TrainSimpleDTO;
import org.fairdatatrain.trainhandler.api.dto.train.TrainUpdateDTO;
import org.fairdatatrain.trainhandler.exception.NotFoundException;
import org.fairdatatrain.trainhandler.service.train.TrainService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.UUID;

@Tag(name = "Trains")
@PreAuthorize("hasRole('user')")
@RestController
@RequestMapping("/trains")
@RequiredArgsConstructor
public class TrainController {

    private final TrainService trainService;

    @GetMapping(path = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public Page<TrainSimpleDTO> getPaged(
            @RequestParam(value = "query", required = false, defaultValue = "") String query,
            Pageable pageable) {
        return trainService.getPaged(query, pageable);
    }

    @GetMapping(path = "/all", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<TrainSimpleDTO> getPaged(
            @RequestParam(value = "query", required = false, defaultValue = "") String query) {
        return trainService.getAll(query);
    }

    @PostMapping(
            path = "",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public TrainDTO create(
            @RequestBody @Valid TrainCreateDTO reqDto
    ) throws NotFoundException {
        return trainService.create(reqDto);
    }

    @GetMapping(path = "/{uuid}", produces = MediaType.APPLICATION_JSON_VALUE)
    public TrainDTO getSingle(@PathVariable UUID uuid) throws NotFoundException {
        return trainService.getSingle(uuid);
    }

    @GetMapping(path = "/{uuid}/stations", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<StationSimpleDTO> getSuitableStations(@PathVariable UUID uuid)
            throws NotFoundException {
        return trainService.getSuitableStations(uuid);
    }

    @PutMapping(
            path = "/{uuid}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public TrainDTO update(
            @PathVariable UUID uuid,
            @RequestBody @Valid TrainUpdateDTO reqDto
    ) throws NotFoundException {
        return trainService.update(uuid, reqDto);
    }

    @DeleteMapping(path = "/{uuid}", produces = MediaType.APPLICATION_JSON_VALUE)
    public TrainDTO delete(@PathVariable UUID uuid) throws NotFoundException {
        return trainService.softDelete(uuid);
    }
}
