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
package org.fairdatatrain.trainhandler.service.fixtures;

import org.fairdatatrain.trainhandler.api.dto.traingarage.TrainInfoDTO;

import java.util.Arrays;
import java.util.List;

public class TrainGarageFixtures {

    public static final TrainInfoDTO TRAIN_A =
            new TrainInfoDTO("https://example.com/fdt/train/a", "Train A: Cancer SPARQL Express");
    public static final TrainInfoDTO TRAIN_B =
            new TrainInfoDTO("https://example.com/fdt/train/b", "Train B: Cancer SPARQL Slow");
    public static final TrainInfoDTO TRAIN_C =
            new TrainInfoDTO("https://example.com/fdt/train/c", "Train C: COVID Portal Analysis");
    public static final TrainInfoDTO TRAIN_D =
            new TrainInfoDTO("https://example.com/fdt/train/d", "Train D: COVID SPARQL Simple");

    public static final List<TrainInfoDTO> TRAINS =
            Arrays.asList(TRAIN_A, TRAIN_B, TRAIN_C, TRAIN_D);
}
