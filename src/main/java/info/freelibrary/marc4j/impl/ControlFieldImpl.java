/**
 * Copyright (C) 2004 Bas Peters
 *
 * This file is part of MARC4J
 *
 * MARC4J is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * MARC4J is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with MARC4J; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package info.freelibrary.marc4j.impl;

import org.marc4j.Constants;

import org.marc4j.marc.InvalidMARCException;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.marc4j.marc.ControlField;

/**
 * ControlField defines behavior for a control field (tag 001-009).
 * <p>
 * Control fields are variable fields identified by tags beginning with two
 * zero's. They are comprised of data and a field terminator and do not contain
 * indicators or subfield codes. The structure of a control field according to
 * the MARC standard is as follows:
 * 
 * <pre>DATA_ELEMENT FIELD_TERMINATOR</pre>
 * </p>
 * 
 * @author Bas Peters
 * @author Kevin S. Clarke <ksclarke@gmail.com>
 */
public class ControlFieldImpl extends VariableFieldImpl implements ControlField {

    private static final long serialVersionUID = 8049827626175226331L;

    private String myData;

    /**
     * Creates a new <code>ControlField</code>.
     */
    ControlFieldImpl() {
    }

    /**
     * Creates a new <code>ControlField</code> and sets the tag name.
     * 
     * @param aTag The field tag for the <code>ControlField</code>
     */
    ControlFieldImpl(String aTag) {
        setTag(aTag);
    }

    /**
     * Creates a new <code>ControlField</code> and sets the tag name and the
     * data element.
     * 
     * @param aTag The tag for the <code>ControlField</code>
     * @param aData The data for the <code>ControlField</code>
     */
    ControlFieldImpl(String aTag, String aData) {
        setTag(aTag);
        setData(aData);
    }

    /**
     * Sets the tag of a <code>ControlField</code>.
     * 
     * @param aTag The tag of a <code>ControlField</code>
     */
    public void setTag(String aTag) {
        super.setTag(aTag);

        if (!Constants.CF_TAG_PATTERN.matcher(aTag).find()) {
            throw new InvalidMARCException(aTag +
                    " is not a valid ControlField tag");
        }
    }

    /**
     * Sets the {@link ControlField} data.
     * 
     * @param aData The data for the <code>ControlField</code>
     */
    public void setData(String aData) {
        myData = aData;
    }

    /**
     * Gets the {@link ControlField} data.
     * 
     * @return The <code>ControlField</code>'s data
     */
    public String getData() {
        return myData;
    }

    /**
     * Returns a string representation of this control field.
     * <p>
     * For example:
     * 
     * <pre>
     *     001 12883376
     * </pre>
     * </p>
     * 
     * @return A string representation of this control field
     */
    public String toString() {
        return super.toString() + " " + getData();
    }

    /**
     * Finds a match to a regular expression pattern in the {@link ControlField}
     * 's data.
     * 
     * @param aPattern The regular expression pattern to compare against the
     *        <code>ControlField</code>'s data
     */
    public boolean find(String aPattern) {
        Pattern pattern = Pattern.compile(aPattern);
        Matcher matcher = pattern.matcher(getData());

        return matcher.find();
    }

}
