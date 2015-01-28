
package org.solrmarc.marcoverride;

import java.io.InputStream;
import java.util.List;

import org.marc4j.MarcReader;
import org.marc4j.MarcXmlReader;
import org.marc4j.marc.ControlField;
import org.marc4j.marc.DataField;
import org.marc4j.marc.Record;
import org.marc4j.marc.Subfield;

public class MarcUnprettyXmlReader implements MarcReader {

    private MarcXmlReader reader = null;

    /**
     * Creates an ugly XML reader from the supplied {@link InputStream}.
     *
     * @param input
     */
    public MarcUnprettyXmlReader(final InputStream input) {
        reader = new MarcXmlReader(input);
    }

    /**
     * Returns <code>true</code> if there is a next record to read; else,
     * <code>false</code>.
     */
    @Override
    public boolean hasNext() {
        return (reader.hasNext());
    }

    /**
     * Returns the next {@link Record} from the reader.
     *
     * @return The next {@link Record} from the reader
     */
    @Override
    public Record next() {
        final Record rec = reader.next();
        final List<?> varFields = rec.getVariableFields();

        rec.getLeader().setCharCodingScheme('a');

        for (final Object f : varFields) {
            if (f instanceof ControlField) {
                final ControlField cf = (ControlField) f;
                String data = cf.getData();

                if (data.contains("\n")) {
                    data = data.replaceAll("\\r?\\n[ \t]*", " ");
                    data = data.trim();
                    cf.setData(data);
                }
            } else if (f instanceof DataField) {
                final DataField df = (DataField) f;
                final List<?> subFields = df.getSubfields();

                for (final Object s : subFields) {
                    final Subfield sf = (Subfield) s;
                    String data = sf.getData();

                    if (data.contains("\n")) {
                        data = data.replaceAll("\\r?\\n[ \t]*", " ");
                        data = data.trim();
                        sf.setData(data);
                    }
                }
            }
        }

        return rec;
    }

}
