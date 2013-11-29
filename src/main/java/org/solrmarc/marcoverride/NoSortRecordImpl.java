
package org.solrmarc.marcoverride;

import org.marc4j.marc.ControlField;
import org.marc4j.marc.DataField;
import org.marc4j.marc.VariableField;
import org.marc4j.marc.impl.RecordImpl;
import org.marc4j.marc.impl.Verifier;

/**
 * @author Robert Haschart
 * @version $Id$
 */
public class NoSortRecordImpl extends RecordImpl {

    /**
     * The <code>serialVersionUID</code> for the class.
     */
    private static final long serialVersionUID = -5870251915056214892L;

    /**
     * Creates a no sort record.
     */
    public NoSortRecordImpl() {
        super();
    }

    /**
     * Adds a {@link VariableField} to the record.
     */
    public void addVariableField(VariableField field) {
        if (field instanceof ControlField) {
            ControlField controlField = (ControlField) field;
            String tag = field.getTag();

            if (Verifier.isControlNumberField(tag)) {
                if (Verifier.hasControlNumberField(getControlFields())) {
                    getControlFields().set(0, controlField);
                } else {
                    getControlFields().add(0, controlField);
                }
            } else if (Verifier.isControlField(tag)) {
                getControlFields().add(controlField);
            }
        } else {
            getDataFields().add((DataField) field);
        }

    }

}
