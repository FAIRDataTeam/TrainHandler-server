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
import org.fairdatatrain.trainhandler.api.dto.stationdirectory.StationDirectoryChangeDTO;
import org.fairdatatrain.trainhandler.api.dto.stationdirectory.StationDirectoryDTO;
import org.fairdatatrain.trainhandler.exception.NotFoundException;
import org.fairdatatrain.trainhandler.service.stationdirectory.StationDirectoryService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.UUID;

@Tag(name = "Station Directories")
@PreAuthorize("hasRole('user')")
@RestController
@RequestMapping("/station-directories")
@RequiredArgsConstructor
public class StationDirectoryController {

    private final StationDirectoryService stationDirectoryService;

    @GetMapping(path = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public Page<StationDirectoryDTO> getPaged(
            @RequestParam(value = "query", required = false, defaultValue = "") String query,
            @PageableDefault(sort = "displayName") Pageable pageable) {
        return stationDirectoryService.getPaged(query, pageable);
    }

    @PostMapping(
            path = "",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(code = HttpStatus.CREATED)
    public StationDirectoryDTO create(
            @Valid @RequestBody StationDirectoryChangeDTO reqDto
    ) throws NotFoundException {
        final StationDirectoryDTO dto = stationDirectoryService.create(reqDto);
        stationDirectoryService.reindex(dto.getUuid());
        return dto;
    }

    @GetMapping(path = "/{uuid}", produces = MediaType.APPLICATION_JSON_VALUE)
    public StationDirectoryDTO getSingle(@PathVariable UUID uuid) throws NotFoundException {
        return stationDirectoryService.getSingle(uuid);
    }

    @PutMapping(path = "/{uuid}", produces = MediaType.APPLICATION_JSON_VALUE)
    public StationDirectoryDTO update(
            @PathVariable UUID uuid, @Valid @RequestBody StationDirectoryChangeDTO reqDto)
            throws NotFoundException {
        final StationDirectoryDTO dto = stationDirectoryService.update(uuid, reqDto);
        stationDirectoryService.reindex(dto.getUuid());
        return dto;
    }

    @DeleteMapping(path = "/{uuid}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID uuid) throws NotFoundException {
        stationDirectoryService.delete(uuid);
    }

    @PutMapping(path = "/{uuid}/stations", params = "refresh")
    @ResponseStatus(code = HttpStatus.ACCEPTED)
    public void refreshStations(@PathVariable UUID uuid) throws NotFoundException {
        stationDirectoryService.reindex(uuid);
    }
}
