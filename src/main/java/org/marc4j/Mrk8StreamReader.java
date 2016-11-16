package org.marc4j;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import org.marc4j.marc.DataField;
import org.marc4j.marc.Leader;
import org.marc4j.marc.MarcFactory;
import org.marc4j.marc.Record;
import org.marc4j.marc.Subfield;
import org.marc4j.marc.VariableField;

/**
 *
 * @author Admin
 */
public class Mrk8StreamReader implements MarcReader {

    private final Scanner input;

    private final MarcFactory factory;

    private String lastLineRead;

    public Mrk8StreamReader(InputStream input) {
        this.input = new Scanner(new BufferedInputStream(input), StandardCharsets.UTF_8.name());
        this.factory = MarcFactory.newInstance();
    }

    @Override
    public boolean hasNext() {
        return this.input.hasNextLine();
    }

    @Override
    public Record next() {
        List<String> lines = new ArrayList<>();
        if (!this.hasNext()) {
            return null;
        }
        if (this.lastLineRead != null && this.lastLineRead.substring(1, 4).equalsIgnoreCase("LDR")) {
            lines.add(lastLineRead);
        }
        while (this.input.hasNextLine()) {
            String line = this.input.nextLine();

            if (line.trim().length() == 0) {
                //this is a blank line. We do not need it
                continue;
            }
            this.lastLineRead = line;
            if (line.substring(1, 4).equalsIgnoreCase("LDR") && lines.size() > 0) {
                //we have reached the next record... break for parsing;
                break;
            }

            lines.add(line);
        }
        return this.parse(lines);
    }

    protected Record parse(List<String> lines) {
        if (lines == null || lines.isEmpty()) {
            return null;
        }

        Record record = this.factory.newRecord();

        for (String line : lines) {
            if (line.trim().length() == 0) {
                continue;
            }

            String tag = line.substring(1, 4);

            if (tag.equalsIgnoreCase("LDR")) {
                record.setLeader(getLeader(line.substring(6)));
            } else {
                VariableField field;
                if (this.isControlField(tag)) {
                    field = this.factory.newControlField(tag, line.substring(6));
                } else {
                    //this is obviously a data field
                    String data = line.substring(6);

                    char indicator1 = (data.startsWith("\\", 0) ? ' ' : data.charAt(0));
                    char indicator2 = (data.startsWith("\\", 1) ? ' ' : data.charAt(1));

                    if (!this.isValidIndicator(indicator1) || !this.isValidIndicator(indicator2)) {
                        throw new MarcException("Wrong indicator format. It has to be a number or a space");
                    }

                    field = this.factory.newDataField(tag, indicator1, indicator2);

                    List<String> subs = Arrays.asList(data.substring(3).split("\\$"));
                    
                    for (String sub : subs){
                        Subfield subfield = this.factory.newSubfield(sub.charAt(0), sub.substring(1));
                        ((DataField) field).addSubfield(subfield);
                    }
                }
                record.addVariableField(field);
            }
        }

        return record;
    }

    protected boolean isValidIndicator(char indicator) {
        return (indicator == ' ' || (indicator >= '0' && indicator <= '9'));
    }

    protected Leader getLeader(String substring) {
        Leader leader = this.factory.newLeader();
        leader.unmarshal(substring);
        return null;
    }

    protected boolean isControlField(String tag) {
        //can probably be replaced with (Integer.parseInt(tag)<10)
        return ((tag.length() == 3) && tag.startsWith("00") && (tag.charAt(2) >= '0') && (tag.charAt(2) <= '9'));
    }
}
