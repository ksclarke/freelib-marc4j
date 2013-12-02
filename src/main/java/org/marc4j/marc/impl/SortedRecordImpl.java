
package org.marc4j.marc.impl;

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
     * Adds a {@link VariableField} to the record.
     */
    public void addVariableField(VariableField field) {
        int cfSize = controlFields.size();

        // Let the class we extend do the work...
        super.addVariableField(field);

        // If a control field was added, sort them; else, sort the data fields
        if (cfSize != controlFields.size()) {
            Collections.sort(controlFields);
        } else {
            Collections.sort(dataFields);
        }
    }
}
