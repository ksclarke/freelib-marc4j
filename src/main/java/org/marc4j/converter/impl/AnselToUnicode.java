/**
 * Copyright (C) 2002 Bas Peters
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

package org.marc4j.converter.impl;

import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.marc4j.ErrorHandler;
import org.marc4j.MarcException;
import org.marc4j.converter.CharConverter;

/**
 * <p/>
 * A utility to convert MARC-8 data to non-precomposed UCS/Unicode.
 * <p/>
 * <p/>
 * The MARC-8 to Unicode mapping used is the version with the March 2005 revisions.
 * <p/>
 *
 * @author Bas Peters
 * @author Corey Keith
 */
public class AnselToUnicode extends CharConverter {

    class Queue extends Vector<Character> {

        private static final long serialVersionUID = 1L;

        /**
         * Puts an item into the queue.
         *
         * @param item the item to be put into the queue.
         */
        public Object put(final Character item) {
            addElement(item);

            return item;
        }

        /**
         * Gets an item from the front of the queue.
         */
        public Object get() {
            Object obj;
            // int len = size();

            obj = peek();
            removeElementAt(0);

            return obj;
        }

        /**
         * Peeks at the front of the queue.
         */
        public Object peek() {
            // int len = size();

            return elementAt(0);
        }

        /**
         * Returns true if the queue is empty.
         */
        public boolean empty() {
            return size() == 0;
        }
    }

    class CodeTracker {

        int offset;

        int g0;

        int g1;

        boolean multibyte;

        @Override
        public String toString() {
            return "Offset: " + offset + " G0: " + Integer.toHexString(g0) + " G1: " + Integer.toHexString(g1) +
                    " Multibyte: " + multibyte;
        }
    }

    protected CodeTableInterface ct;

    protected boolean loadedMultibyte = false;

    // flag that indicates whether Numeric Character References of the form
    // &#XXXX; should be translated to the
    // unicode code point specified by the 4 hexidecimal digits. As described on
    // this page
    // http://www.loc.gov/marc/specifications/speccharconversion.html#lossless
    protected boolean translateNCR = false;

    /**
     * Returns <code>true</code> if NCR should be translated; else, <code>false</code>.
     *
     * @return Whether the NCR should be translated
     */
    public boolean shouldTranslateNCR() {
        return translateNCR;
    }

    /**
     * Sets whether the NCR should be translated.
     *
     * @param translateNCR
     */
    public void setTranslateNCR(final boolean translateNCR) {
        this.translateNCR = translateNCR;
    }

    /**
     * Should return true if the CharConverter outputs Unicode encoded characters
     *
     * @return boolean whether the CharConverter returns Unicode encoded characters
     */
    @Override
    public boolean outputsUnicode() {
        return (true);
    }

    protected ErrorHandler errorList = null;

    /**
     * Creates a new instance and loads the MARC4J supplied conversion tables based on the official LC tables.
     */
    public AnselToUnicode() {
        ct = loadGeneratedTable(false);
    }

    /**
     * Creates a new instance and loads the MARC4J supplied conversion tables based on the official LC tables.
     */
    public AnselToUnicode(final boolean loadMultibyte) {
        ct = loadGeneratedTable(loadMultibyte);
    }

    /**
     * Creates a new instance and loads the MARC4J supplied conversion tables based on the official LC tables.
     */
    public AnselToUnicode(final ErrorHandler errorList) {
        ct = loadGeneratedTable(false);
        this.errorList = errorList;
    }

    /**
     * Creates a new instance and loads the MARC4J supplied conversion tables based on the official LC tables.
     */
    public AnselToUnicode(final ErrorHandler errorList, final boolean loadMultibyte) {
        ct = loadGeneratedTable(loadMultibyte);
        this.errorList = errorList;
    }

    private CodeTableInterface loadGeneratedTable(final boolean loadMultibyte) {
        try {
            final Class<?> generated = Class.forName("org.marc4j.converter.impl.CodeTableGenerated");
            final Constructor<?> cons = generated.getConstructor();
            final Object ct = cons.newInstance();
            loadedMultibyte = true;
            return ((CodeTableInterface) ct);
        } catch (final Exception e) {
            CodeTableInterface ct;
            if (loadMultibyte) {
                ct = new CodeTable(AnselToUnicode.class.getResourceAsStream("resources/codetables.xml"));
            } else {
                ct = new CodeTable(AnselToUnicode.class.getResourceAsStream("resources/codetablesnocjk.xml"));
            }
            loadedMultibyte = loadMultibyte;
            return (ct);
        }

    }

    /**
     * Constructs an instance with the specified pathname. Use this constructor to create an instance with a customized
     * code table mapping. The mapping file should follow the structure of LC's XML MARC-8 to Unicode mapping (see:
     * http://www.loc.gov/marc/specifications/codetables.xml).
     */
    public AnselToUnicode(final String pathname) {
        ct = new CodeTable(pathname);
        loadedMultibyte = true;
    }

    /**
     * Constructs an instance with the specified input stream. Use this constructor to create an instance with a
     * customized code table mapping. The mapping file should follow the structure of LC's XML MARC-8 to Unicode mapping
     * (see: http://www.loc.gov/marc/specifications/codetables.xml).
     */
    public AnselToUnicode(final InputStream in) {
        ct = new CodeTable(in);
        loadedMultibyte = true;
    }

    /**
     * Loads the entire mapping (including multibyte characters) from the Library of Congress.
     */
    private void loadMultibyte() {
        ct = new CodeTable(getClass().getResourceAsStream("resources/codetables.xml"));
    }

    private void checkMode(final char[] data, final CodeTracker cdt) {
        int extra = 0;
        int extra2 = 0;

        while (cdt.offset + extra + extra2 < data.length && isEscape(data[cdt.offset])) {
            if (cdt.offset + extra + extra2 + 1 == data.length) {
                cdt.offset += 1;

                if (errorList != null) {
                    errorList.addError(ErrorHandler.MINOR_ERROR,
                            "Escape character found at end of field, discarding it.");
                } else {
                    throw new MarcException("Escape character found at end of field");
                }

                break;
            }

            switch (data[cdt.offset + 1 + extra]) {
                case 0x28: // '('
                case 0x2c: // ','
                    set_cdt(cdt, 0, data, 2 + extra, false);
                    break;
                case 0x29: // ')'
                case 0x2d: // '-'
                    set_cdt(cdt, 1, data, 2 + extra, false);
                    break;
                case 0x24: // '$'
                    if (!loadedMultibyte) {
                        loadMultibyte();
                        loadedMultibyte = true;
                    }

                    switch (data[cdt.offset + 2 + extra + extra2]) {
                        case 0x29: // ')'
                        case 0x2d: // '-'
                            set_cdt(cdt, 1, data, 3 + extra + extra2, true);
                            break;
                        case 0x2c: // ','
                            set_cdt(cdt, 0, data, 3 + extra + extra2, true);
                            break;
                        case 0x31: // '1'
                            cdt.g0 = data[cdt.offset + 2 + extra + extra2];
                            cdt.offset += 3 + extra + extra2;
                            cdt.multibyte = true;
                            break;
                        case 0x20: // ' '
                            // space found in escape code: look ahead and try to
                            // proceed
                            extra2++;
                            break;
                        default:
                            // unknown code character found: discard escape
                            // sequence and return
                            cdt.offset += 1;

                            if (errorList != null) {
                                errorList.addError(ErrorHandler.MINOR_ERROR,
                                        "Unknown character set code found following escape character. "
                                                + "Discarding escape character.");
                            } else {
                                throw new MarcException(
                                        "Unknown character set code found following escape character.");
                            }

                            break;
                    }

                    break;
                case 0x67: // 'g'
                case 0x62: // 'b'
                case 0x70: // 'p'
                    cdt.g0 = data[cdt.offset + 1 + extra];
                    cdt.offset += 2 + extra;
                    cdt.multibyte = false;
                    break;
                case 0x73: // 's'
                    cdt.g0 = 0x42;
                    cdt.offset += 2 + extra;
                    cdt.multibyte = false;
                    break;
                case 0x20: // ' '
                    // space found in escape code: look ahead and try to proceed
                    if (errorList == null) {
                        throw new MarcException(
                                "Extraneous space character found within MARC8 character set escape sequence");
                    }

                    extra++;
                    break;
                default:
                    // unknown code character found: discard escape sequence and
                    // return
                    cdt.offset += 1;

                    if (errorList != null) {
                        errorList.addError(ErrorHandler.MINOR_ERROR,
                                "Unknown character set code found following escape character. "
                                        + "Discarding escape character.");
                    } else {
                        throw new MarcException("Unknown character set code found following escape character.");
                    }

                    break;
            }
        }
        if (errorList != null && (extra != 0 || extra2 != 0)) {
            errorList.addError(ErrorHandler.ERROR_TYPO, "" + (extra + extra2) +
                    " extraneous space characters found within MARC8 character set escape sequence");
        }
    }

    private void set_cdt(final CodeTracker cdt, final int g0_or_g1, final char[] data, int addnlOffset,
            final boolean multibyte) {
        if (data[cdt.offset + addnlOffset] == '!' && data[cdt.offset + addnlOffset + 1] == 'E') {
            addnlOffset++;
        } else if (data[cdt.offset + addnlOffset] == ' ') {
            if (errorList != null) {
                errorList.addError(ErrorHandler.ERROR_TYPO,
                        "Extraneous space character found within MARC8 character set escape sequence. "
                                + "Skipping over space.");
            } else {
                throw new MarcException("Extraneous space character found within MARC8 character set escape sequence");
            }
            addnlOffset++;
        } else if ("(,)-$!".indexOf(data[cdt.offset + addnlOffset]) != -1) {
            if (errorList != null) {
                errorList.addError(ErrorHandler.MINOR_ERROR,
                        "Extraneaous intermediate character found following escape character. "
                                + "Discarding intermediate character.");
            } else {
                throw new MarcException("Extraneaous intermediate character found following escape character.");
            }
            addnlOffset++;
        }
        if ("34BE1NQS2".indexOf(data[cdt.offset + addnlOffset]) == -1) {
            cdt.offset += 1;
            cdt.multibyte = false;
            if (errorList != null) {
                errorList.addError(ErrorHandler.MINOR_ERROR,
                        "Unknown character set code found following escape character. Discarding escape character.");
            } else {
                throw new MarcException("Unknown character set code found following escape character.");
            }
        } else {
            // All is well, proceed normally
            if (g0_or_g1 == 0) {
                cdt.g0 = data[cdt.offset + addnlOffset];
            } else {
                cdt.g1 = data[cdt.offset + addnlOffset];
            }
            cdt.offset += 1 + addnlOffset;
            cdt.multibyte = multibyte;
        }
    }

    /**
     * <p>
     * Converts MARC-8 data to UCS/Unicode.
     * </p>
     *
     * @param data - the MARC-8 data in an array of char
     * @return String - the UCS/Unicode data
     */
    @Override
    public String convert(final char data[]) {
        final StringBuffer sb = new StringBuffer();
        final int len = data.length;

        final CodeTracker cdt = new CodeTracker();

        cdt.g0 = 0x42;
        cdt.g1 = 0x45;
        cdt.multibyte = false;

        cdt.offset = 0;

        checkMode(data, cdt);

        final Queue diacritics = new Queue();

        while (cdt.offset < data.length) {
            if (ct.isCombining(data[cdt.offset], cdt.g0, cdt.g1) && hasNext(cdt.offset, len)) {

                while (cdt.offset < len && ct.isCombining(data[cdt.offset], cdt.g0, cdt.g1) &&
                        hasNext(cdt.offset, len)) {
                    final char c = getChar(data[cdt.offset], cdt.g0, cdt.g1);
                    if (c != 0) {
                        diacritics.put(new Character(c));
                    }
                    cdt.offset++;
                    checkMode(data, cdt);
                }
                if (cdt.offset >= len) {
                    if (errorList != null) {
                        errorList.addError(ErrorHandler.MINOR_ERROR,
                                "Diacritic found at the end of field, without the character that it is "
                                        + "supposed to decorate");
                        break;
                    }
                }
                final char c2 = getChar(data[cdt.offset], cdt.g0, cdt.g1);
                cdt.offset++;
                checkMode(data, cdt);
                if (c2 != 0) {
                    sb.append(c2);
                }

                while (!diacritics.isEmpty()) {
                    final char c1 = ((Character) diacritics.get()).charValue();
                    sb.append(c1);
                }

            } else if (cdt.multibyte) {
                if (data[cdt.offset] == 0x20) {
                    // if a 0x20 byte occurs amidst a sequence of multibyte
                    // characters
                    // skip over it and output a space.
                    sb.append(getChar(data[cdt.offset], cdt.g0, cdt.g1));
                    cdt.offset += 1;
                } else if (cdt.offset + 3 <= data.length &&
                        (errorList == null || data[cdt.offset + 1] != 0x20 && data[cdt.offset + 2] != 0x20)) {
                    final char c =
                            getMBChar(makeMultibyte(data[cdt.offset], data[cdt.offset + 1], data[cdt.offset + 2]));

                    if (errorList == null || c != 0) {
                        sb.append(c);
                        cdt.offset += 3;
                    } else if (cdt.offset + 6 <= data.length && data[cdt.offset + 4] != 0x20 &&
                            data[cdt.offset + 5] != 0x20 &&
                            getMBChar(makeMultibyteAlias(data, cdt.offset, new int[] { 3, 4, 5 })) != 0) {
                        if (errorList != null) {
                            errorList.addError(ErrorHandler.MINOR_ERROR,
                                    "Erroneous MARC8 multibyte character, Discarding bad character and "
                                            + "continuing reading Multibyte characters");
                            sb.append("[?]");
                            cdt.offset += 3;
                        }
                    } else if (cdt.offset + 4 <= data.length && data[cdt.offset] > 0x7f &&
                            getMBChar(makeMultibyteAlias(data, cdt.offset, new int[] { 1, 2, 3 })) != 0) {
                        if (errorList != null) {
                            errorList.addError(ErrorHandler.MINOR_ERROR,
                                    "Erroneous character in MARC8 multibyte character, Copying bad character "
                                            + "and continuing reading Multibyte characters");
                            sb.append(getChar(data[cdt.offset], 0x42, 0x45));
                            cdt.offset += 1;
                        }
                    } else {
                        if (errorList != null) {
                            errorList.addError(ErrorHandler.MINOR_ERROR,
                                    "Erroneous MARC8 multibyte character, inserting change to default character set");
                        }
                        cdt.multibyte = false;
                        cdt.g0 = 0x42;
                        cdt.g1 = 0x45;
                    }
                } else if (errorList != null && cdt.offset + 4 <= data.length &&
                        (data[cdt.offset + 1] == 0x20 || data[cdt.offset + 2] == 0x20)) {
                    final int multiByte =
                            makeMultibyte(data[cdt.offset], ((data[cdt.offset + 1] != 0x20) ? data[cdt.offset + 1]
                                    : data[cdt.offset + 2]), data[cdt.offset + 3]);
                    final char c = getMBChar(multiByte);
                    if (c != 0) {
                        if (errorList != null) {
                            errorList.addError(ErrorHandler.ERROR_TYPO,
                                    "Extraneous space found within MARC8 multibyte character");
                        }
                        sb.append(c);
                        sb.append(' ');
                        cdt.offset += 4;
                    } else {
                        if (errorList != null) {
                            errorList.addError(ErrorHandler.MINOR_ERROR,
                                    "Erroneous MARC8 multibyte character, inserting change to default character set");
                        }
                        cdt.multibyte = false;
                        cdt.g0 = 0x42;
                        cdt.g1 = 0x45;
                    }
                } else if (cdt.offset + 3 > data.length || cdt.offset + 3 == data.length &&
                        (data[cdt.offset + 1] == 0x20 || data[cdt.offset + 2] == 0x20)) {
                    if (errorList != null) {
                        errorList.addError(ErrorHandler.MINOR_ERROR,
                                "Partial MARC8 multibyte character, inserting change to default character set");
                        cdt.multibyte = false;
                        cdt.g0 = 0x42;
                        cdt.g1 = 0x45;
                    } else {
                        // if a field ends with an incomplete encoding of a
                        // multibyte character simply discard that final partial
                        // character.
                        cdt.offset += 3;
                    }
                }
            } else {
                final char c = getChar(data[cdt.offset], cdt.g0, cdt.g1);
                if (c != 0) {
                    sb.append(c);
                } else {
                    final String val = "0000" + Integer.toHexString((data[cdt.offset]));
                    sb.append("<U+" + (val.substring(val.length() - 4, val.length())) + ">");
                }
                cdt.offset += 1;
            }
            if (hasNext(cdt.offset, len)) {
                checkMode(data, cdt);
            }
        }

        String dataElement = sb.toString();

        if (translateNCR && dataElement.matches("[^&]*&#x[0-9A-Fa-f][0-9A-Fa-f][0-9A-Fa-f][0-9A-Fa-f];.*")) {
            final Pattern pattern = Pattern.compile("&#x([0-9A-Fa-f][0-9A-Fa-f][0-9A-Fa-f][0-9A-Fa-f]);");
            final Matcher matcher = pattern.matcher(dataElement);
            final StringBuffer newElement = new StringBuffer();

            int prevEnd = 0;

            while (matcher.find()) {
                newElement.append(dataElement.substring(prevEnd, matcher.start()));
                newElement.append(getCharFromCodePoint(matcher.group(1)));
                prevEnd = matcher.end();
            }

            newElement.append(dataElement.substring(prevEnd));
            dataElement = newElement.toString();
        }

        return (dataElement);
    }

    /**
     * Alias function for:
     * 
     * <pre>
     * makeMultibyte(data[cdt.offset + 3], data[cdt.offset + 4], data[cdt.offset + 5])
     * </pre>
     *
     * @param data
     * @param offset
     * @param bumps
     * @return
     */
    private int makeMultibyteAlias(final char[] data, final int offset, final int[] bumps) {
        return makeMultibyte(data[offset + bumps[0]], data[offset + bumps[1]], data[offset + bumps[2]]);
    }

    private String getCharFromCodePoint(final String charCodePoint) {
        final int charNum = Integer.parseInt(charCodePoint, 16);
        final String result = "" + ((char) charNum);
        return (result);
    }

    /**
     * Makes a multibyte character.
     *
     * @param c1
     * @param c2
     * @param c3
     * @return
     */
    public int makeMultibyte(final char c1, final char c2, final char c3) {
        final int[] chars = new int[3];
        chars[0] = c1 << 16;
        chars[1] = c2 << 8;
        chars[2] = c3;
        return chars[0] | chars[1] | chars[2];
    }

    private char getChar(final int ch, final int g0, final int g1) {
        if (ch <= 0x7E) {
            return ct.getChar(ch, g0);
        } else {
            return ct.getChar(ch, g1);
        }
    }

    /**
     * Gets the multibyte character.
     *
     * @param ch
     * @return
     */
    public char getMBChar(final int ch) {
        return ct.getChar(ch, 0x31);
    }

    private static boolean hasNext(final int pos, final int len) {
        if (pos < (len - 1)) {
            return true;
        }

        return false;
    }

    private static boolean isEscape(final int i) {
        if (i == 0x1B) {
            return true;
        }

        return false;
    }

}
