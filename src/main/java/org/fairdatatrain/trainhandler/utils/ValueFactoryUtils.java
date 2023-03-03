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
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.eclipse.rdf4j.model.util.Values;

import java.util.Optional;

public class ValueFactoryUtils {

    private static final String PREFIX_SEP = ":";

    private static final ValueFactory VF = SimpleValueFactory.getInstance();

    public static IRI i(String iri) {
        if (iri == null) {
            return null;
        }
        return Values.iri(iri);
    }

    public static IRI i(Optional<String> optionalIri) {
        if (optionalIri.isEmpty()) {
            return null;
        }
        return i(optionalIri.get());
    }

    public static IRI i(String iri, Model model) {
        if (iri != null) {
            // URI: ':title'
            if (iri.startsWith(PREFIX_SEP)) {
                final Optional<Namespace> optionalNamespace = model.getNamespace("");
                final String prefix = optionalNamespace.get().getName();
                return i(prefix + iri.substring(1));
            }

            // URI: 'rda:title'
            final String[] splitted = iri.split(PREFIX_SEP);
            if (splitted.length == 2 && splitted[1].charAt(0) != '/') {
                final Optional<Namespace> optionalNamespace = model.getNamespace(splitted[0]);
                final String prefix = optionalNamespace.get().getName();
                return i(prefix + splitted[1]);
            }

            // URI: 'http://schema.org/person#title'
            if (iri.contains("://")) {
                return i(iri);
            }

        }
        return null;
    }

    public static IRI i(Value value) {
        if (value == null) {
            return null;
        }
        return i(value.stringValue());
    }

    public static Statement s(Resource subject, IRI predicate, Value object) {
        return VF.createStatement(subject, predicate, object);
    }

    public static Statement s(Resource subject, IRI predicate, Value object, Resource context) {
        return VF.createStatement(subject, predicate, object, context);
    }
}
