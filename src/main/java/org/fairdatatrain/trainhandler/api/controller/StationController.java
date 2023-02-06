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
import org.fairdatatrain.trainhandler.api.dto.station.StationCreateDTO;
import org.fairdatatrain.trainhandler.api.dto.station.StationDTO;
import org.fairdatatrain.trainhandler.api.dto.station.StationSimpleDTO;
import org.fairdatatrain.trainhandler.api.dto.station.StationUpdateDTO;
import org.fairdatatrain.trainhandler.api.dto.train.TrainSimpleDTO;
import org.fairdatatrain.trainhandler.exception.NotFoundException;
import org.fairdatatrain.trainhandler.service.station.StationService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.UUID;

@Tag(name = "Stations")
@PreAuthorize("hasRole('user')")
@RestController
@RequestMapping("/stations")
@RequiredArgsConstructor
public class StationController {

    private final StationService stationService;

    @GetMapping(path = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public Page<StationSimpleDTO> getPaged(
            @RequestParam(value = "query", required = false, defaultValue = "") String query,
            Pageable pageable) {
        return stationService.getPaged(query, pageable);
    }

    @GetMapping(path = "/all", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<StationSimpleDTO> getPaged(
            @RequestParam(value = "query", required = false, defaultValue = "") String query) {
        return stationService.getAll(query);
    }

    @PostMapping(
            path = "",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public StationDTO create(
            @RequestBody @Valid StationCreateDTO reqDto
    ) throws NotFoundException {
        return stationService.create(reqDto);
    }

    @GetMapping(path = "/{uuid}", produces = MediaType.APPLICATION_JSON_VALUE)
    public StationDTO getSingle(@PathVariable UUID uuid) throws NotFoundException {
        return stationService.getSingle(uuid);
    }

    @GetMapping(path = "/{uuid}/trains", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<TrainSimpleDTO> getSuitableTrains(@PathVariable UUID uuid)
            throws NotFoundException {
        return stationService.getSuitableStations(uuid);
    }

    @PutMapping(
            path = "/{uuid}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public StationDTO update(
            @PathVariable UUID uuid,
            @RequestBody @Valid StationUpdateDTO reqDto
    ) throws NotFoundException {
        return stationService.update(uuid, reqDto);
    }

    @DeleteMapping(path = "/{uuid}", produces = MediaType.APPLICATION_JSON_VALUE)
    public StationDTO delete(@PathVariable UUID uuid) throws NotFoundException {
        return stationService.softDelete(uuid);
    }
}
