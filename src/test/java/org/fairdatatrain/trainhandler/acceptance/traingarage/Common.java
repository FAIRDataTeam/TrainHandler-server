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
import org.fairdatatrain.trainhandler.api.dto.traingarage.TrainGarageDTO;
import org.fairdatatrain.trainhandler.data.model.TrainGarage;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

public class Common {

    public static void assertEquals(TrainGarageDTO dto, TrainGarage entity) {
        assertThat(dto.getUuid(), is(equalTo(entity.getUuid())));
        assertThat(dto.getDisplayName(), is(equalTo(entity.getDisplayName())));
        assertThat(dto.getMetadata(), is(equalTo(entity.getMetadata())));
        assertThat(dto.getUri(), is(equalTo(entity.getUri())));
        assertThat(dto.getNote(), is(equalTo(entity.getNote())));
        assertThat(dto.getStatus(), is(equalTo(entity.getStatus())));
    }

    public static void assertEquals(TrainGarageDTO dto, TrainGarageChangeDTO changeDto) {
        assertThat(dto.getDisplayName(), is(equalTo(changeDto.getDisplayName())));
        assertThat(dto.getUri(), is(equalTo(changeDto.getUri())));
        assertThat(dto.getNote(), is(equalTo(changeDto.getNote())));
    }
}
