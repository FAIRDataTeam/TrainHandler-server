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
package org.fairdatatrain.trainhandler.service.traininstance;

import lombok.RequiredArgsConstructor;
import org.fairdatatrain.trainhandler.api.dto.traininstance.TrainRunSimpleDTO;
import org.fairdatatrain.trainhandler.data.model.Station;
import org.fairdatatrain.trainhandler.data.model.TrainInstance;
import org.fairdatatrain.trainhandler.data.model.TrainRun;
import org.fairdatatrain.trainhandler.service.station.StationMapper;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class TrainRunMapper {

    private StationMapper stationMapper;

    public TrainRunSimpleDTO toSimpleDTO(TrainRun trainRun) {
        return TrainRunSimpleDTO.builder()
                .station(stationMapper.toSimpleDTO(trainRun.getStation()))
                .status(trainRun.getStatus())
                .build();
    }

    public TrainRun fromInstance(TrainInstance newTrainInstance, Station station) {
        return TrainRun.builder()
                .uuid(UUID.randomUUID())
                .trainInstance(newTrainInstance)
                .station(station)
                .status("INIT")
                .createdAt(newTrainInstance.getCreatedAt())
                .updatedAt(newTrainInstance.getUpdatedAt())
                .build();
    }
}
