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

import org.marc4j.marc.VariableField;

/**
 * Represents a variable field in a MARC record.
 * 
 * @author Bas Peters
 * @author Kevin S. Clarke <ksclarke@gmail.com>
 */
public abstract class VariableFieldImpl implements VariableField {

    /**
     * A <code>serialVersionUID</code> for the class.
     */
    private static final long serialVersionUID = -8396090810780390995L;

    private String myTag;

    private Long myID;

    /**
     * Sets this field's tag.
     * 
     * @param aTag This field's tag
     */
    @Override
    public void setTag(final String aTag) {
        if (aTag == null) {
            final String className = getClass().getSimpleName();
            throw new NullPointerException(className + "'s tag can't be null");
        }

        myTag = aTag;
    }

    /**
     * Returns this field's tag.
     * 
     * @return This field's tag
     */
    @Override
    public String getTag() {
        return myTag;
    }

    /**
     * Compare's this {@link VariableField} to the supplied one.
     * 
     * @param aObject A {@link VariableField} to compare to this one
     * @return 0 for a match, -1 if this one sorts first, or 1 if it sorts last
     */
    @Override
    public int compareTo(final VariableField aObject) {
        if (!(aObject instanceof VariableFieldImpl)) {
            throw new ClassCastException("A VariableField object expected");
        }

        return myTag.compareTo(aObject.getTag());
    }

    /**
     * Sets an ID.
     * 
     * @param aID A unique ID for this field
     */
    @Override
    public void setId(final Long aID) {
        // TODO: understand the point of this ID
        myID = aID;
    }

    /**
     * Return the field's ID.
     * 
     * @return The field's unique ID
     */
    @Override
    public Long getId() {
        return myID;
    }

    /**
     * Returns a string representation of this variable field.
     * 
     * @return A string representation of this variable field
     */
    @Override
    public String toString() {
        return myTag;
    }

}
