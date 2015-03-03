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

package org.marc4j.marc;

import java.util.List;

/**
 * DataField defines behavior for a data field (tag 010-999).
 * <p>
 * Data fields are variable fields identified by tags beginning with ASCII numeric values other than two zero's. Data
 * fields contain indicators, subfield codes, data and a field terminator.
 *
 * @author Bas Peters
 * @author Kevin S. Clarke <ksclarke@gmail.com>
 */
public interface DataField extends VariableField {

    /**
     * Returns the first indicator of the <code>DataField</code>
     *
     * @return The first indicator of the <code>DataField</code>
     */
    public char getIndicator1();

    /**
     * Sets the first indicator of the <code>DataField</code>.
     *
     * @param aFirstInd The first indicator of the <code>DataField</code>
     */
    public void setIndicator1(char aFirstInd);

    /**
     * Returns the second indicator of the <code>DataField</code>.
     *
     * @return The second indicator character of the <code>DataField</code>
     */
    public char getIndicator2();

    /**
     * Sets the second indicator of the <code>DataField</code>.
     *
     * @param aSecondInd The second indicator of the <code>DataField</code>
     */
    public void setIndicator2(char aSecondInd);

    /**
     * Returns the {@link List} of {@link Subfield}.
     *
     * @return The {@link List} of {@link Subfield}s
     */
    public List<Subfield> getSubfields();

    /**
     * Returns the {@link List} of {@link Subfield}s for the given subfield code.
     *
     * @param aCode The code of the subfields to return
     * @return The {@link List} of {@link Subfield}s in the <code>DataField</code>
     */
    public List<Subfield> getSubfields(char aCode);

    /**
     * Returns the {@link List} of {@link Subfield}s that match the supplied subfield code pattern. The pattern can
     * either be a list of subfield codes (e.g. "abc") or it can be a regular expression that includes brackets (e.g.,
     * "[a-cf-g]"). The brackets indicate whether a regular expression matcher will be used or not.
     *
     * @param aPattern A subfield code pattern
     * @return List - the list of <code>Subfield</code> objects
     */
    public List<Subfield> getSubfields(String aPattern);

    /**
     * Gets the data from the specified subfields and returns a concatenated string.
     * <p>
     * Currently, the pattern must specify each subfield needed, there is no covenient shorthand for a range.
     *
     * @param aPattern A subfield pattern which can include multiple subfield codes (e.g., "abc")
     * @return A concatenated string of subfield values or null if no subfields were matched
     */
    public String getSubfieldsAsString(String aPattern);

    /**
     * Gets the data from the specified subfields and returns a concatenated string. The supplied padding character is
     * placed between the data from each subfield.
     * <p>
     * Currently, the pattern must specify each subfield needed, there is no covenient shorthand for a range.
     *
     * @param aPattern A subfield pattern which can include multiple subfield codes (e.g. "abc")
     * @param aPaddingChar A character to insert between the data from each string
     * @return A concatenated string of subfield values or null if no subfields were matched
     */
    public String getSubfieldsAsString(String aPattern, char aPaddingChar);

    /**
     * Returns the first <code>Subfield</code> with the given code.
     *
     * @param aCode The subfield code of the <code>Subfield</code> to return
     * @return The <code>Subfield</code> or null if no subfield is found
     */
    public Subfield getSubfield(char aCode);

    /**
     * Adds the supplied <code>Subfield</code> to the <code>DataField</code>.
     *
     * @param aSubfield The <code>Subfield</code> object
     * @throws IllegalAddException when the parameter is not a <code>Subfield</code> instance
     */
    public void addSubfield(Subfield aSubfield);

    /**
     * Inserts a <code>Subfield</code> at the specified position.
     *
     * @param aIndex The position at which to add the <code>Subfield<code>
     * @param aSubfield The <code>Subfield</code> to add to the <code>DataField</code>
     * @throws IllegalAddException when the supplied <code>Subfield</code> is of the wrong type
     */
    public void addSubfield(int aIndex, Subfield aSubfield);

    /**
     * Removes a <code>Subfield</code>.
     *
     * @param aSubfield The <code>Subfield</code> to remove
     */
    public void removeSubfield(Subfield aSubfield);

    /**
     * Returns the number of subfields in this <code>DataField</code>.
     *
     * @return The number of subfields in this <code>DataField</code>
     */
    public int countSubfields();

}
