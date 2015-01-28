
package org.marc4j.samples;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.marc4j.MarcException;
import org.marc4j.MarcReader;
import org.marc4j.marc.ControlField;
import org.marc4j.marc.DataField;
import org.marc4j.marc.MarcFactory;
import org.marc4j.marc.Record;

public class PersonalNamesReader implements MarcReader {

    private BufferedReader br = null;

    private final MarcFactory factory;

    private String line;

    /**
     * Creates a PersonalNamesReader from the supplied InputStream.
     *
     * @param in
     */
    public PersonalNamesReader(final InputStream in) {
        factory = MarcFactory.newInstance();
        br = new BufferedReader(new InputStreamReader(in));
    }

    /**
     * Returns true if the PersonalNamesReader has another Record.
     */
    @Override
    public boolean hasNext() {
        try {
            if ((line = br.readLine()) != null) {
                return true;
            } else {
                return false;
            }
        } catch (final IOException e) {
            throw new MarcException(e.getMessage(), e);
        }
    }

    /**
     * Returns the next Record in the PersonalNamesReader.
     */
    @Override
    public Record next() {
        final String[] tokens = line.trim().split("\t");

        if (tokens.length != 3) {
            throw new MarcException("Index out of bounds");
        }

        final Record record = factory.newRecord("00000nz  a2200000o  4500");

        final ControlField cf = factory.newControlField("001", tokens[0]);
        record.addVariableField(cf);

        final DataField df = factory.newDataField("100", '1', ' ');
        df.addSubfield(factory.newSubfield('a', tokens[1]));
        df.addSubfield(factory.newSubfield('d', tokens[2]));
        record.addVariableField(df);

        return record;
    }

}
