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
package org.fairdatatrain.trainhandler.acceptance.stationdirectory;

import org.fairdatatrain.trainhandler.api.dto.stationdirectory.StationDirectoryChangeDTO;
import org.fairdatatrain.trainhandler.data.model.StationDirectory;
import org.fairdatatrain.trainhandler.data.model.enums.SyncServiceStatus;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class StationDirectoryTestFixtures {

    public static StationDirectory STATION_DIRECTORY_A =
            StationDirectory.builder()
                    .uuid(UUID.randomUUID())
                    .createdAt(Timestamp.from(Instant.now()))
                    .updatedAt(Timestamp.from(Instant.now()))
                    .uri("https://example.com/fdt/station-directory/A")
                    .displayName("Station Directory A")
                    .note("")
                    .metadata("")
                    .status(SyncServiceStatus.SYNCED)
                    .lastContactAt(Timestamp.from(Instant.now()))
                    .build();

    public static StationDirectory STATION_DIRECTORY_B =
            StationDirectory.builder()
                    .uuid(UUID.randomUUID())
                    .createdAt(Timestamp.from(Instant.now()))
                    .updatedAt(Timestamp.from(Instant.now()))
                    .uri("https://example.com/fdt/station-directory/B")
                    .displayName("Station Directory B")
                    .note("")
                    .metadata("")
                    .status(SyncServiceStatus.SYNCED)
                    .lastContactAt(Timestamp.from(Instant.now()))
                    .build();

    public static StationDirectory STATION_DIRECTORY_C =
            StationDirectory.builder()
                    .uuid(UUID.randomUUID())
                    .createdAt(Timestamp.from(Instant.now()))
                    .updatedAt(Timestamp.from(Instant.now()))
                    .uri("https://example.com/fdt/station-directory/C")
                    .displayName("Station Directory C")
                    .note("")
                    .metadata("")
                    .status(SyncServiceStatus.SYNCED)
                    .lastContactAt(Timestamp.from(Instant.now()))
                    .build();

    public static StationDirectoryChangeDTO STATION_DIRECTORY_X_CREATE = StationDirectoryChangeDTO
            .builder()
            .uri("https://example.com/fdt/station-directory/X")
            .displayName("Station Directory X")
            .note("My custom station directory")
            .build();

    public static StationDirectoryChangeDTO STATION_DIRECTORY_A_UPDATE = StationDirectoryChangeDTO
            .builder()
            .uri("https://example.com/fdt/station-directory/A/edit")
            .displayName("Station Directory A: EDIT")
            .note("My custom station directory edited")
            .build();

    public static List<StationDirectory> STATION_DIRECTORIES = Arrays.asList(STATION_DIRECTORY_A, STATION_DIRECTORY_B, STATION_DIRECTORY_C);
}
