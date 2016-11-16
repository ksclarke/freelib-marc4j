package org.marc4j;

import java.io.Closeable;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.StandardCharsets;
import org.marc4j.converter.CharConverter;
import org.marc4j.marc.ControlField;
import org.marc4j.marc.DataField;
import org.marc4j.marc.Leader;
import org.marc4j.marc.Record;
import org.marc4j.marc.Subfield;
import org.marc4j.marc.VariableField;

/**
 *
 * @author Admin
 */
public class Mrk8StreamWriter implements MarcWriter, Closeable {

    private final PrintWriter mrk8Writer;
    private final CharsetEncoder encoder;

    public Mrk8StreamWriter(final OutputStream output) {
        this.encoder = StandardCharsets.UTF_8.newEncoder();
        this.mrk8Writer = new PrintWriter(output);
    }

    @Override
    public void write(Record record) {
        StringBuilder recordStringBuilder = new StringBuilder();

        Leader ldr = record.getLeader();
        recordStringBuilder.append("=").append("LDR").append("  ").append(ldr.marshal());

        for (VariableField field : record.getVariableFields()) {
            recordStringBuilder.append("=").append(field.getTag()).append("  ");

            if (field instanceof ControlField) {
                ControlField controlField = (ControlField) field;
                String data;
                try {
                    data = this.encoder.encode(CharBuffer.wrap(controlField.getData())).asCharBuffer().toString();
                } catch (CharacterCodingException cce) {
                    data = controlField.getData();
                }
                recordStringBuilder.append(data);
            } else if (field instanceof DataField) {
                DataField dataField = (DataField) field;
                recordStringBuilder.append((dataField.getIndicator1() == ' ') ? "\\" : dataField.getIndicator1());
                recordStringBuilder.append((dataField.getIndicator2() == ' ') ? "\\" : dataField.getIndicator2());
                for (Subfield subField : dataField.getSubfields()) {
                    String data;
                    try {
                        data = this.encoder.encode(CharBuffer.wrap(subField.getData())).asCharBuffer().toString();
                    } catch (CharacterCodingException cce) {
                        data = subField.getData();
                    }
                    recordStringBuilder.append("$").append(subField.getCode()).append(data);
                }
            }
            recordStringBuilder.append(System.lineSeparator());
        }
        recordStringBuilder.append(System.lineSeparator());

        this.mrk8Writer.append(recordStringBuilder);
        this.mrk8Writer.flush();
    }

    @Override
    public void setConverter(CharConverter converter) {
    }

    @Override
    public CharConverter getConverter() {
        return null;
    }

    @Override
    public void close() {
        this.mrk8Writer.flush();
        this.mrk8Writer.close();
    }
}
