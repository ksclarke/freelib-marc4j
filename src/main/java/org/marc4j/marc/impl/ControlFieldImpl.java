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

package org.marc4j.marc.impl;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.marc4j.marc.ControlField;

/**
 * Represents a control field in a MARC record.
 * 
 * @author Bas Peters
 */
public class ControlFieldImpl extends VariableFieldImpl implements ControlField {

    private Long id;

    private String data;

    /**
     * Creates a new <code>ControlField</code>.
     */
    public ControlFieldImpl() {
    }

    /**
     * Creates a new <code>ControlField</code> and sets the tag name.
     */
    public ControlFieldImpl(String tag) {
        super(tag);
    }

    /**
     * Creates a new <code>ControlField</code> and sets the tag name and the
     * data element.
     */
    public ControlFieldImpl(String tag, String data) {
        super(tag);
        this.setData(data);
    }

    /**
     * Sets the {@link ControlField} data.
     * 
     * @param data
     */
    public void setData(String data) {
        this.data = data;
    }

    /**
     * Gets the {@link ControlField} data. return The {@link ControlField}'s
     * data
     */
    public String getData() {
        return data;
    }

    /**
     * Returns a string representation of this control field.
     * <p>
     * Example:
     * 
     * <pre>
     *     001 12883376
     * </pre>
     * 
     * @return String - a string representation of this control field
     */
    public String toString() {
        return super.toString() + " " + getData();
    }

    /**
     * Finds a match to a regular expression pattern in the {@link ControlField}
     * 's data.
     * 
     * @param pattern
     */
    public boolean find(String pattern) {
        Pattern p = Pattern.compile(pattern);
        Matcher m = p.matcher(getData());
        return m.find();
    }

    /**
     * Sets an ID for the {@link ControlField}.
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Return the {@link ControlField}'s ID.
     */
    public Long getId() {
        return id;
    }

}
