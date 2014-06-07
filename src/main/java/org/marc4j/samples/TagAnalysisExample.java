
package org.marc4j.samples;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import org.marc4j.MarcReader;
import org.marc4j.MarcStreamReader;
import org.marc4j.marc.Record;
import org.marc4j.marc.VariableField;

/**
 * Outputs list of used tags.
 *
 * @author Bas Peters
 */
public class TagAnalysisExample {

    /**
     * The main class for the TagAnalysisExample.
     *
     * @param args
     * @throws Exception
     */
    public static void main(final String args[]) throws Exception {
        final InputStream input = new FileInputStream("src/test/resources/chabon.mrc");
        final Hashtable table = new Hashtable();
        int counter = 0;

        final MarcReader reader = new MarcStreamReader(input);
        while (reader.hasNext()) {
            counter++;

            final Record record = reader.next();
            final List fields = record.getVariableFields();
            final Iterator i = fields.iterator();

            while (i.hasNext()) {
                final VariableField field = (VariableField) i.next();
                final String tag = field.getTag();

                if (table.containsKey(tag)) {
                    final Integer counts = (Integer) table.get(tag);
                    table.put(tag, new Integer(counts.intValue() + 1));
                } else {
                    table.put(tag, new Integer(1));
                }
            }

        }

        System.out.println("Analyzed " + counter + " records");
        System.out.println("Tag\tCount");

        final List list = new ArrayList(table.keySet());
        Collections.sort(list);
        final Iterator i = list.iterator();
        while (i.hasNext()) {
            final String tag = (String) i.next();
            final Integer value = (Integer) table.get(tag);
            System.out.println(tag + "\t" + value);
        }

    }
}
