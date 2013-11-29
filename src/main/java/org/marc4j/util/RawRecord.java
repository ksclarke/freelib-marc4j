
package org.marc4j.util;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.marc4j.Constants;
import org.marc4j.MarcPermissiveStreamReader;
//import org.marc4j.MarcReader;
import org.marc4j.marc.Record;
import org.marc4j.marc.VariableField;

public class RawRecord {

    String id;

    byte rawRecordData[];

    byte leader[] = null;

    /**
     * Creates a RawRecord from the supplied {@link DataInputStream}.
     * 
     * @param ds
     */
    public RawRecord(DataInputStream ds) {
        init(ds);

        if (rawRecordData != null) {
            id = getRecordId();
        }
    }

    private void init(DataInputStream ds) {
        id = null;
        ds.mark(24);

        if (leader == null) {
            leader = new byte[24];
        }

        try {
            ds.readFully(leader);
            int length = parseRecordLength(leader);
            ds.reset();
            ds.mark(length * 2);
            rawRecordData = new byte[length];

            try {
                ds.readFully(rawRecordData);
            } catch (EOFException e) {
                ds.reset();
                int c;
                int cnt = 0;

                while ((c = ds.read()) != -1) {
                    rawRecordData[cnt++] = (byte) c;
                }

                int location = byteArrayContains(rawRecordData, Constants.RT);

                if (location != -1) {
                    length = location + 1;
                } else {
                    throw (e);
                }
            }

            if (rawRecordData[length - 1] != Constants.RT) {
                int location = byteArrayContains(rawRecordData, Constants.RT);

                // Specified length was longer that actual length
                if (location != -1) {
                    ds.reset();
                    rawRecordData = new byte[location];
                    ds.readFully(rawRecordData);
                } else {
                    // keep reading until end of record found
                    ArrayList<Byte> recBuf = new ArrayList<Byte>();
                    ds.reset();

                    byte byteRead[] = new byte[1];

                    while (true) {
                        int numRead = ds.read(byteRead);

                        if (numRead == -1) {
                            break; // probably should throw
                        }

                        // something here.
                        recBuf.add(byteRead[0]);

                        if (byteRead[0] == Constants.RT) {
                            break;
                        }
                    }

                    rawRecordData = new byte[recBuf.size()];

                    for (int i = 0; i < recBuf.size(); i++) {
                        rawRecordData[i] = recBuf.get(i);
                    }
                }
            }
        } catch (IOException e) {
            try {
                rawRecordData = null;
                ds.reset();
            } catch (IOException e1) {
            }
        }

    }

    private static int byteArrayContains(byte data[], int value) {
        for (int i = 0; i < data.length; i++) {
            if (data[i] == value) {
                return (i);
            }
        }
        return (-1);
    }

    /**
     * Creates a new raw record from the two supplied raw records.
     * 
     * @param a1stRawRecord
     * @param a2ndRawRecord
     */
    public RawRecord(RawRecord a1stRawRecord, RawRecord a2ndRawRecord) {
        rawRecordData =
                new byte[a1stRawRecord.getRecordBytes().length +
                        a2ndRawRecord.getRecordBytes().length];

        System.arraycopy(a1stRawRecord.getRecordBytes(), 0, rawRecordData, 0,
                a1stRawRecord.getRecordBytes().length);
        System.arraycopy(a2ndRawRecord.getRecordBytes(), 0, rawRecordData,
                a1stRawRecord.getRecordBytes().length, a2ndRawRecord
                        .getRecordBytes().length);

        id = getRecordId();
    }

    /**
     * Gets the record ID.
     * 
     * @return The record ID
     */
    public String getRecordId() {
        if (id != null) {
            return (id);
        }
        id = getFieldVal("001");
        return (id);
    }

    /**
     * Gets the value of the field with the supplied ID.
     * 
     * @param idField
     * @return The value of the field with the supplied ID
     */
    public String getFieldVal(String idField) {
        String recordStr = null;

        try {
            recordStr = new String(rawRecordData, "ISO-8859-1");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        int offset = Integer.parseInt(recordStr.substring(12, 17));
        int dirOffset = 24;
        String fieldNum = recordStr.substring(dirOffset, dirOffset + 3);

        while (dirOffset < offset) {
            if (fieldNum.equals(idField)) {
                int length =
                        Integer.parseInt(recordStr.substring(dirOffset + 3,
                                dirOffset + 7));
                int offset2 =
                        Integer.parseInt(recordStr.substring(dirOffset + 7,
                                dirOffset + 12));
                String id =
                        recordStr.substring(offset + offset2,
                                offset + offset2 + length - 1).trim();
                return id;
            }

            dirOffset += 12;
            fieldNum = recordStr.substring(dirOffset, dirOffset + 3);
        }

        return null;
    }

    /**
     * Gets the record in byte form.
     * 
     * @return
     */
    public byte[] getRecordBytes() {
        return (rawRecordData);
    }

    /**
     * Gets the raw record as a {@link Record}.
     * 
     * @param permissive
     * @param toUtf8
     * @param combinePartials
     * @param defaultEncoding
     * @return
     */
    public Record getAsRecord(boolean permissive, boolean toUtf8,
            String combinePartials, String defaultEncoding) {
        ByteArrayInputStream bais = new ByteArrayInputStream(rawRecordData);
        MarcPermissiveStreamReader reader =
                new MarcPermissiveStreamReader(bais, permissive, toUtf8,
                        defaultEncoding);
        Record next = reader.next();
        if (combinePartials != null) {
            while (reader.hasNext()) {
                Record nextNext = reader.next();
                List<VariableField> fieldsAll =
                        (List<VariableField>) nextNext.getVariableFields();
                Iterator<VariableField> fieldIter = fieldsAll.iterator();
                while (fieldIter.hasNext()) {
                    VariableField vf = fieldIter.next();
                    if (combinePartials.contains(vf.getTag())) {
                        next.addVariableField(vf);
                    }
                }
            }
        }
        return (next);
    }

    private static int parseRecordLength(byte[] leaderData) throws IOException {
        InputStreamReader isr =
                new InputStreamReader(new ByteArrayInputStream(leaderData));
        int length = -1;
        char[] tmp = new char[5];
        isr.read(tmp);
        try {
            length = Integer.parseInt(new String(tmp));
        } catch (NumberFormatException e) {
            throw new IOException("unable to parse record length");
        }
        return (length);
    }

    // public boolean hasNext()
    // {
    // // TODO Auto-generated method stub
    // return false;
    // }
    //
    // public Record next()
    // {
    // // TODO Auto-generated method stub
    // return null;
    // }
    //
    //

}
