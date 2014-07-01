/**
 * Copyright (C) 2002-2006 Bas Peters
 *
 * This file is part of MARC4J
 *
 * MARC4J is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * MARC4J is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with MARC4J; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package org.marc4j.samples;

import info.freelibrary.marc4j.converter.impl.AnselToUnicode;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import org.marc4j.MarcReader;
import org.marc4j.MarcStreamReader;
import org.marc4j.MarcWriter;
import org.marc4j.MarcXmlWriter;
import org.marc4j.marc.Record;

/**
 * Writes MARC XML in UTF-8 to standard output.
 *
 * @author Bas Peters
 */
public class Marc8ToMarcXmlExample {

    private Marc8ToMarcXmlExample() {
    }

    /**
     * The main class for Marc8ToMarcXmlExample.
     *
     * @param args
     * @throws Exception
     */
    public static void main(final String args[]) throws Exception {
        final InputStream input = new FileInputStream("src/test/resources/brkrtest.mrc");
        final OutputStream out = new FileOutputStream(System.getProperty("java.io.tmpdir") + "/summerland.xml");
        final MarcReader reader = new MarcStreamReader(input);
        final MarcWriter writer = new MarcXmlWriter(out, true);
        final AnselToUnicode converter = new AnselToUnicode();

        writer.setConverter(converter);

        while (reader.hasNext()) {
            final Record record = reader.next();
            writer.write(record);
        }
        writer.close();
    }
}
