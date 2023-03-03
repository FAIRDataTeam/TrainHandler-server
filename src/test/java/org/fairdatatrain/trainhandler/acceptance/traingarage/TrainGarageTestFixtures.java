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
package org.fairdatatrain.trainhandler.acceptance.traingarage;

import org.fairdatatrain.trainhandler.api.dto.traingarage.TrainGarageChangeDTO;
import org.fairdatatrain.trainhandler.data.model.TrainGarage;
import org.fairdatatrain.trainhandler.data.model.enums.SyncServiceStatus;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class TrainGarageTestFixtures {

    public static TrainGarage TRAIN_GARAGE_A =
            TrainGarage.builder()
                    .uuid(UUID.randomUUID())
                    .createdAt(Timestamp.from(Instant.now()))
                    .updatedAt(Timestamp.from(Instant.now()))
                    .uri("https://example.com/fdt/train-garage/A")
                    .displayName("Train Garage A")
                    .note("")
                    .metadata("")
                    .status(SyncServiceStatus.SYNCED)
                    .lastContactAt(Timestamp.from(Instant.now()))
                    .build();

    public static TrainGarage TRAIN_GARAGE_B =
            TrainGarage.builder()
                    .uuid(UUID.randomUUID())
                    .createdAt(Timestamp.from(Instant.now()))
                    .updatedAt(Timestamp.from(Instant.now()))
                    .uri("https://example.com/fdt/train-garage/B")
                    .displayName("Train Garage B")
                    .note("")
                    .metadata("")
                    .status(SyncServiceStatus.SYNCED)
                    .lastContactAt(Timestamp.from(Instant.now()))
                    .build();

    public static TrainGarage TRAIN_GARAGE_C =
            TrainGarage.builder()
                    .uuid(UUID.randomUUID())
                    .createdAt(Timestamp.from(Instant.now()))
                    .updatedAt(Timestamp.from(Instant.now()))
                    .uri("https://example.com/fdt/train-garage/C")
                    .displayName("Train Garage C")
                    .note("")
                    .metadata("")
                    .status(SyncServiceStatus.SYNCED)
                    .lastContactAt(Timestamp.from(Instant.now()))
                    .build();

    public static TrainGarageChangeDTO TRAIN_GARAGE_X_CREATE = TrainGarageChangeDTO
            .builder()
            .uri("https://example.com/fdt/train-garage/X")
            .displayName("Train Garage X")
            .note("My custom train garage")
            .build();

    public static TrainGarageChangeDTO TRAIN_GARAGE_A_UPDATE = TrainGarageChangeDTO
            .builder()
            .uri("https://example.com/fdt/train-garage/A/edit")
            .displayName("Train Garage A: EDIT")
            .note("My custom train garage edited")
            .build();

    public static List<TrainGarage> TRAIN_GARAGES = Arrays.asList(TRAIN_GARAGE_A, TRAIN_GARAGE_B, TRAIN_GARAGE_C);
}
