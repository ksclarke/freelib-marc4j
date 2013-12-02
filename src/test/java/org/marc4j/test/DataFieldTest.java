
package org.marc4j.test;

import org.marc4j.marc.InvalidMARCException;

import org.junit.Test;
import org.marc4j.marc.DataField;
import org.marc4j.marc.MarcFactory;
import org.marc4j.marc.Subfield;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

public class DataFieldTest {

    private final MarcFactory factory = MarcFactory.newInstance();

    /**
     * Tests the {@link DataField} constructor.
     */
    @Test
    public void testConstructor() {
        DataField df = factory.newDataField("245", '1', '0');
        assertEquals("245", df.getTag());
        assertEquals('1', df.getIndicator1());
        assertEquals('0', df.getIndicator2());
        assertEquals(0, df.countSubfields());

        df = factory.newDataField();
        assertNull(df.getTag());
        assertEquals('\u0000', df.getIndicator1());
        assertEquals('\u0000', df.getIndicator2());
        assertEquals(0, df.countSubfields());
    }

    /**
     * Tests {@link DataField#addSubfield(Subfield)}.
     */
    @Test
    public void testAddSubfield() {
        DataField df = factory.newDataField("245", '1', '0');
        Subfield sf = factory.newSubfield('a', "Summerland");

        // Get a baseline count of subfields
        assertEquals(0, df.countSubfields());

        // Add a single subfield to a DataField
        df.addSubfield(sf);

        // Confirm that subfield was added
        assertEquals(1, df.countSubfields());
    }

    /**
     * Tests {@link DataField#addSubfield(int, Subfield).
     */
    @Test
    public void testAddSubfieldIndexSubfield() {
        DataField df = factory.newDataField("245", '1', '0');
        Subfield sf1 = factory.newSubfield('a', "Summerland");
        Subfield sf2 = factory.newSubfield('c', "Michael Chabon");

        // Adds a subfield to give us something to insert before
        df.addSubfield(sf2);

        // Add a subfield at a particular index position
        df.addSubfield(0, sf1);

        // Test that we have two subfields and that ours is first
        assertEquals(2, df.countSubfields());
        assertEquals('a', df.getSubfields().get(0).getCode());
    }

    /**
     * Tests the {@link Comparable} aspect of {@link DataField}.
     * 
     * @throws Exception
     */
    @Test
    public void testComparable() {
        DataField df1 = factory.newDataField("600", '0', '0');
        DataField df2 = factory.newDataField("600", '0', '0');

        // This should be equal at this point
        assertEquals(0, df1.compareTo(df2));

        // df1 (600) should now be sorted after df2 (245)
        df2.setTag("245");
        assertEquals(4, df1.compareTo(df2));

        // df1 (600) should now be sorted before df2 (700)
        df2.setTag("700");
        assertEquals(-1, df1.compareTo(df2));
    }

    /**
     * Tests the validity checking of the {@link DataField}'s tag.
     * 
     * @throws Exception
     */
    @Test
    public void testInvalidTags() {
        @SuppressWarnings("unused")
        DataField df;

        try {
            df = factory.newDataField("009", '1', '4');
            fail("009 is not a valid DataField tag");
        } catch (InvalidMARCException details) {
            // expected
        }

        try {
            df = factory.newDataField("01", '1', '4');
            fail("01 is not a valid DataField tag");
        } catch (InvalidMARCException details) {
            // expected
        }
    }

}
