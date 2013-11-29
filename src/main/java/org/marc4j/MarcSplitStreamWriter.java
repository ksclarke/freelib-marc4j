
package org.marc4j;

import org.marc4j.marc.ControlField;
import org.marc4j.marc.DataField;
import org.marc4j.marc.Leader;
import org.marc4j.marc.Record;
import org.marc4j.marc.Subfield;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class MarcSplitStreamWriter extends MarcStreamWriter {

    private int recordThreshold;

    private String fieldsToSplit;

    /**
     * Creates a MarcSplitStreamWriter with a record threshold and fields to
     * split from the supplied {@link OutputStream}.
     * 
     * @param out
     * @param threshold
     * @param fieldsToSplit
     */
    public MarcSplitStreamWriter(OutputStream out, int threshold,
            String fieldsToSplit) {
        super(out, false);
        recordThreshold = threshold;
        this.fieldsToSplit = fieldsToSplit;
    }

    /**
     * Creates a MarcSplitStreamWriter with an encoding, a record threshold, and
     * fields to split from the supplied {@link OutputStream}.
     * 
     * @param out
     * @param encoding
     * @param threshold
     * @param fieldsToSplit
     */
    public MarcSplitStreamWriter(OutputStream out, String encoding,
            int threshold, String fieldsToSplit) {
        super(out, encoding, false);
        recordThreshold = threshold;
        this.fieldsToSplit = fieldsToSplit;
    }

    /**
     * Writes a <code>Record</code> object to the writer.
     * 
     * @param record - the <code>Record</code> object
     */
    public void write(Record record) {
        boolean doneWithRec = false;
        for (DataField df : record.getDataFields()) {
            if (!df.getTag().matches(fieldsToSplit)) {
                continue;
            }
            df.setId(null);
        }

        while (!doneWithRec) {
            try {
                int previous = 0;
                ByteArrayOutputStream data = new ByteArrayOutputStream();
                ByteArrayOutputStream dir = new ByteArrayOutputStream();

                // control fields
                for (ControlField cf : record.getControlFields()) {
                    data.write(getDataElement(cf.getData()));
                    data.write(Constants.FT);
                    dir.write(getEntry(cf.getTag(), data.size() - previous,
                            previous));
                    previous = data.size();
                }

                // data fields
                for (DataField df : record.getDataFields()) {
                    if (df.getTag().matches(fieldsToSplit)) {
                        continue;
                    }
                    data.write(df.getIndicator1());
                    data.write(df.getIndicator2());
                    for (Subfield sf : df.getSubfields()) {
                        data.write(Constants.US);
                        data.write(sf.getCode());
                        data.write(getDataElement(sf.getData()));
                    }
                    data.write(Constants.FT);
                    dir.write(getEntry(df.getTag(), data.size() - previous,
                            previous));
                    previous = data.size();
                }
                // data fields
                doneWithRec = true;
                for (DataField df : record.getDataFields()) {
                    if (previous >= recordThreshold) {
                        doneWithRec = false;
                        break;
                    }
                    if (!df.getTag().matches(fieldsToSplit)) {
                        continue;
                    }
                    if (!(df.getId() == null || df.getId().intValue() != 0)) {
                        continue;
                    }
                    df.setId(new Long(0));
                    data.write(df.getIndicator1());
                    data.write(df.getIndicator2());
                    for (Subfield sf : df.getSubfields()) {
                        data.write(Constants.US);
                        data.write(sf.getCode());
                        data.write(getDataElement(sf.getData()));
                    }
                    data.write(Constants.FT);
                    dir.write(getEntry(df.getTag(), data.size() - previous,
                            previous));
                    previous = data.size();
                }
                dir.write(Constants.FT);

                // base address of data and logical record length
                Leader ldr = record.getLeader();

                int baseAddress = 24 + dir.size();
                ldr.setBaseAddressOfData(baseAddress);
                int recordLength = ldr.getBaseAddressOfData() + data.size() + 1;
                ldr.setRecordLength(recordLength);

                // write record to output stream
                dir.close();
                data.close();

                if (!allowOversizeEntry && (hasOversizeLength)) {
                    throw new MarcException(
                            "Record has field that is too long to be a valid MARC binary record. The maximum length for a field counting all of the sub-fields is 9999 bytes.");
                }
                writeLeader(ldr);
                out.write(dir.toByteArray());
                out.write(data.toByteArray());
                out.write(Constants.RT);

            } catch (IOException e) {
                throw new MarcException(
                        "IO Error occured while writing record", e);
            } catch (MarcException e) {
                throw e;
            }
        }
    }

}
