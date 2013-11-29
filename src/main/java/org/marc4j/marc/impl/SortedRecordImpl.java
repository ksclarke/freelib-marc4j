
package org.marc4j.marc.impl;

import org.marc4j.marc.ControlField;
import org.marc4j.marc.DataField;
import org.marc4j.marc.VariableField;

import java.util.Collections;

/**
 * @author Robert Haschart
 */
public class SortedRecordImpl extends RecordImpl {

    /**
     * The class' <code>serialVersionUID</code>.
     */
    private static final long serialVersionUID = 21647558104914722L;

    /**
     * Creates a {@link SortedRecord}.
     */
    public SortedRecordImpl() {
        super();
    }

    /**
     * Adds a {@link VariableField} to the record.
     */
    public void addVariableField(VariableField field) {
        if (field instanceof ControlField) {
            ControlField controlField = (ControlField) field;
            String tag = controlField.getTag();

            if (Verifier.isControlNumberField(tag)) {
                if (Verifier.hasControlNumberField(getControlFields())) {
                    getControlFields().set(0, controlField);
                } else {
                    getControlFields().add(0, controlField);
                }

                Collections.sort(controlFields);
            } else if (Verifier.isControlField(tag)) {
                getControlFields().add(controlField);
                Collections.sort(controlFields);
            }
        } else {
            getDataFields().add((DataField) field);
            Collections.sort(dataFields);
        }
    }
}
