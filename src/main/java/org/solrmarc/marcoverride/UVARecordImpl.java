
package org.solrmarc.marcoverride;

import org.marc4j.marc.ControlField;
import org.marc4j.marc.IllegalAddException;
import org.marc4j.marc.VariableField;
import org.marc4j.marc.impl.Verifier;

/**
 * @author Robert Haschart
 */
public class UVARecordImpl extends NoSortRecordImpl {

    /**
     * The <code>serialVersionUID</code> for this class.
     */
    private static final long serialVersionUID = 6261234132097657666L;

    /**
     * Creates a new UVA record.
     */
    public UVARecordImpl() {
        super();
    }

    /**
     * Adds a {@link VariableField} to the UVA record.
     */
    public void addVariableField(final VariableField field) {
        if (!(field instanceof VariableField)) {
            throw new IllegalAddException("Expected VariableField instance");
        }

        String tag = field.getTag();
        if (Verifier.isControlNumberField(tag)) {
            ControlField cfield = getControlNumberField();
            if (cfield != null) {
                if (!((ControlField) field).getData().startsWith("u") &&
                        cfield.getData().startsWith("u")) {
                    // ditch it!
                    return;
                }
            }
        }
        super.addVariableField(field);
    }

}
