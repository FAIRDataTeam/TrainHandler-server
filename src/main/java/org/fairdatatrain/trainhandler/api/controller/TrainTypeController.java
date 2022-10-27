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
import org.fairdatatrain.trainhandler.api.dto.traintype.TrainTypeChangeDTO;
import org.fairdatatrain.trainhandler.api.dto.traintype.TrainTypeDTO;
import org.fairdatatrain.trainhandler.exception.NotFoundException;
import org.fairdatatrain.trainhandler.service.traintype.TrainTypeService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.UUID;

@Tag(name = "Train Types")
@PreAuthorize("hasRole('user')")
@RestController
@RequestMapping("/train-types")
@RequiredArgsConstructor
public class TrainTypeController {

    private final TrainTypeService trainTypeService;

    @GetMapping(path = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public Page<TrainTypeDTO> getPaged(
            @RequestParam(value = "query", required = false, defaultValue = "") String query,
            Pageable pageable) {
        return trainTypeService.getPaged(query, pageable);
    }

    @PostMapping(
            path = "",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(code = HttpStatus.CREATED)
    public TrainTypeDTO create(@Valid @RequestBody TrainTypeChangeDTO reqDto) {
        return trainTypeService.create(reqDto);
    }

    @GetMapping(path = "/{uuid}", produces = MediaType.APPLICATION_JSON_VALUE)
    public TrainTypeDTO getSingle(@PathVariable UUID uuid) throws NotFoundException {
        return trainTypeService.getSingle(uuid);
    }

    @PutMapping(path = "/{uuid}", produces = MediaType.APPLICATION_JSON_VALUE)
    public TrainTypeDTO update(
            @PathVariable UUID uuid, @Valid @RequestBody TrainTypeChangeDTO reqDto)
            throws NotFoundException {
        return trainTypeService.update(uuid, reqDto);
    }

    @DeleteMapping(path = "/{uuid}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID uuid) throws NotFoundException {
        trainTypeService.delete(uuid);
    }
}
