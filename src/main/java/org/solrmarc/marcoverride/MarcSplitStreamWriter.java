
package org.solrmarc.marcoverride;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.List;

import org.marc4j.Constants;
import org.marc4j.MarcException;
import org.marc4j.MarcStreamWriter;
import org.marc4j.marc.ControlField;
import org.marc4j.marc.DataField;
import org.marc4j.marc.Leader;
import org.marc4j.marc.Record;
import org.marc4j.marc.Subfield;

public class MarcSplitStreamWriter extends MarcStreamWriter {

    private final int recordThreshold;

    private final String fieldsToSplit;

    /**
     * Creates a split stream writer from the supplied {@link OutputStream} using the supplied threshold and fields to
     * split.
     *
     * @param out
     * @param threshhold
     * @param fieldsToSplit
     */
    public MarcSplitStreamWriter(final OutputStream out, final int threshold, final String fieldsToSplit) {
        super(out, false);
        recordThreshold = threshold;
        this.fieldsToSplit = fieldsToSplit;
    }

    /**
     * Creates a split stream writer from the supplied {@link OutputStream} using the supplied encoding, threshold, and
     * fields to split.
     *
     * @param out
     * @param encoding
     * @param threshold
     * @param fieldsToSplit
     */
    public MarcSplitStreamWriter(final OutputStream out, final String encoding, final int threshold,
            final String fieldsToSplit) {
        super(out, encoding, false);
        recordThreshold = threshold;
        this.fieldsToSplit = fieldsToSplit;
        // TODO Auto-generated constructor stub
    }

    /**
     * Writes a <code>Record</code> object to the writer.
     *
     * @param record - the <code>Record</code> object
     */
    @Override
    public void write(final Record record) {
        List<?> fields = record.getDataFields();
        Iterator<?> i = fields.iterator();
        boolean doneWithRec = false;

        while (i.hasNext()) {
            final DataField df = (DataField) i.next();

            if (!df.getTag().matches(fieldsToSplit)) {
                continue;
            }

            df.setId(null);
        }

        while (!doneWithRec) {
            try {
                final ByteArrayOutputStream data = new ByteArrayOutputStream();
                final ByteArrayOutputStream dir = new ByteArrayOutputStream();
                int previous = 0;

                // control fields
                fields = record.getControlFields();
                i = fields.iterator();

                while (i.hasNext()) {
                    final ControlField cf = (ControlField) i.next();

                    data.write(getDataElement(cf.getData()));
                    data.write(Constants.FT);
                    dir.write(getEntry(cf.getTag(), data.size() - previous, previous));
                    previous = data.size();
                }

                // data fields
                fields = record.getDataFields();
                i = fields.iterator();

                while (i.hasNext()) {
                    final DataField df = (DataField) i.next();
                    if (df.getTag().matches(fieldsToSplit)) {
                        continue;
                    }

                    data.write(df.getIndicator1());
                    data.write(df.getIndicator2());

                    final List subfields = df.getSubfields();
                    final Iterator si = subfields.iterator();

                    while (si.hasNext()) {
                        final Subfield sf = (Subfield) si.next();
                        data.write(Constants.US);
                        data.write(sf.getCode());
                        data.write(getDataElement(sf.getData()));
                    }

                    data.write(Constants.FT);
                    dir.write(getEntry(df.getTag(), data.size() - previous, previous));
                    previous = data.size();
                }

                // data fields
                fields = record.getDataFields();
                i = fields.iterator();

                while (i.hasNext() && previous < recordThreshold) {
                    final DataField df = (DataField) i.next();

                    if (!df.getTag().matches(fieldsToSplit)) {
                        continue;
                    }

                    if (!(df.getId() == null || df.getId().intValue() != 0)) {
                        continue;
                    }

                    df.setId(new Long(0));
                    data.write(df.getIndicator1());
                    data.write(df.getIndicator2());
                    final List subfields = df.getSubfields();
                    final Iterator si = subfields.iterator();
                    while (si.hasNext()) {
                        final Subfield sf = (Subfield) si.next();
                        data.write(Constants.US);
                        data.write(sf.getCode());
                        data.write(getDataElement(sf.getData()));
                    }
                    data.write(Constants.FT);
                    dir.write(getEntry(df.getTag(), data.size() - previous, previous));
                    previous = data.size();
                }
                if (!i.hasNext()) {
                    doneWithRec = true;
                }
                dir.write(Constants.FT);

                // base address of data and logical record length
                final Leader ldr = record.getLeader();

                final int baseAddress = 24 + dir.size();
                ldr.setBaseAddressOfData(baseAddress);
                final int recordLength = ldr.getBaseAddressOfData() + data.size() + 1;
                ldr.setRecordLength(recordLength);

                // write record to output stream
                dir.close();
                data.close();

                if (!allowOversizeEntry && (hasOversizeLength)) {
                    throw new MarcException("Record has field that is too long to be a valid MARC binary record. "
                            + "The maximum length for a field counting all of the sub-fields is 9999 bytes.");
                }
                writeLeader(ldr);
                out.write(dir.toByteArray());
                out.write(data.toByteArray());
                out.write(Constants.RT);

            } catch (final IOException e) {
                throw new MarcException("IO Error occured while writing record", e);
            } catch (final MarcException e) {
                throw e;
            }
        }
    }

}
