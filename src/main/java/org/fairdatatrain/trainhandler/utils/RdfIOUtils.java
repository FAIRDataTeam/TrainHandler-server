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

import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.vocabulary.*;
import org.eclipse.rdf4j.rio.*;
import org.eclipse.rdf4j.rio.helpers.BasicWriterSettings;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;

public class RdfIOUtils {

    private static final WriterConfig WRITER_CONFIG = new WriterConfig();

    static {
        WRITER_CONFIG.set(BasicWriterSettings.INLINE_BLANK_NODES, true);
    }

    public static Model read(String content, String baseUri) {
        return read(content, baseUri, RDFFormat.TURTLE);
    }

    public static Model read(String content, String baseUri, RDFFormat format) {
        try (InputStream inputStream = new ByteArrayInputStream(content.getBytes())) {
            return Rio.parse(inputStream, baseUri, format);
        }
        catch (IOException exception) {
            throw new RuntimeException("Unable to read RDF (IO exception)");
        }
        catch (RDFParseException exception) {
            throw new RuntimeException("Unable to read RDF (parse exception)");
        }
        catch (RDFHandlerException exception) {
            throw new RuntimeException("Unable to read RDF (handler exception)");
        }
    }

    public static String write(Model model) {
        return write(model, RDFFormat.TURTLE);
    }

    public static String write(Model model, RDFFormat format) {
        model.setNamespace(DCTERMS.NS);
        model.setNamespace(DCAT.NS);
        model.setNamespace(FOAF.NS);
        model.setNamespace(XMLSchema.NS);
        model.setNamespace(LDP.NS);

        try (StringWriter out = new StringWriter()) {
            Rio.write(model, out, format, WRITER_CONFIG);
            return out.toString();
        }
        catch (IOException exception) {
            throw new RuntimeException("Unable to write RDF (IO exception)");
        }
    }
}
