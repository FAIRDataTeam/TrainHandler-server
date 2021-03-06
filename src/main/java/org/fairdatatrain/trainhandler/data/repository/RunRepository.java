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
package org.fairdatatrain.trainhandler.data.repository;

import org.fairdatatrain.trainhandler.data.model.Run;
import org.fairdatatrain.trainhandler.data.repository.base.BaseRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.UUID;

@Repository
public interface RunRepository extends BaseRepository<Run> {

    Page<Run> findAllByPlanUuid(UUID planUuid, Pageable pageable);

    @Query("""
        SELECT r
        FROM Run r
        WHERE
            (
                r.status = 'SCHEDULED'
                AND r.shouldStartAt IS NOT NULL
                AND r.shouldStartAt < :timestamp
            ) OR (
                r.status = 'PREPARED'
                AND r.startedAt IS NULL
                AND r.shouldStartAt IS NULL
            )
        ORDER BY r.shouldStartAt ASC
        """)
    Page<Run> findRunToDispatch(@Param("timestamp") Timestamp timestamp, Pageable pageable);
}
