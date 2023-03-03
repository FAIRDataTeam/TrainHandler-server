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
package org.fairdatatrain.trainhandler.utils;

import org.eclipse.rdf4j.model.*;
import org.eclipse.rdf4j.model.util.Models;

import java.util.List;

import static org.fairdatatrain.trainhandler.utils.ValueFactoryUtils.i;

public class RdfUtils {

    public static List<Value> getObjectsBy(Model model, Resource subject, IRI predicate) {
        return Models.getProperties(model, subject, predicate).stream().toList();
    }

    public static List<Value> getObjectsBy(Model model, String subject, String predicate) {
        return getObjectsBy(model, i(subject, model), i(predicate, model));
    }

    public static Value getObjectBy(Model model, Resource subject, IRI predicate) {
        return Models.getProperty(model, subject, predicate).orElse(null);
    }

    public static String getStringObjectBy(Model model, Resource subject, IRI predicate) {
        return Models.getProperty(model, subject, predicate).map(Value::stringValue).orElse(null);
    }
}
