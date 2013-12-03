
package org.marc4j.test;

import org.junit.Test;

import org.marc4j.marc.ControlField;
import org.marc4j.marc.MarcFactory;
import org.marc4j.marc.InvalidMARCException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;

/**
 * A series of tests for a MARC <code>ControlField</code>.
 * 
 * @author Kevin S. Clarke <ksclarke@gmail.com>
 */
public class ControlFieldTest {

    private MarcFactory myFactory = MarcFactory.newInstance();

    /**
     * Tests the {@link ControlField} constructor.
     */
    @Test
    public void testConstructor() {
        ControlField cf = myFactory.newControlField();
        assertNull(cf.getTag());
        assertNull(cf.getData());

        cf = myFactory.newControlField("001");
        assertEquals("001", cf.getTag());
        assertNull(cf.getData());

        cf = myFactory.newControlField("001", "00001");
        assertEquals("001", cf.getTag());
        assertEquals("00001", cf.getData());
    }

    /**
     * Tests invalid tag values with {@link ControlField}.
     */
    @Test
    public void testInvalidTags() {
        @SuppressWarnings("unused")
        ControlField cf;

        try {
            cf = myFactory.newControlField("999");
            fail("Failed to throw exception for a 999 ControlField tag");
        } catch (InvalidMARCException details) {
            // expected
        }

        try {
            cf = myFactory.newControlField(null);
            fail("Failed to throw exception for a null ControlField tag");
        } catch (NullPointerException details) {
            assertEquals("ControlFieldImpl's tag can't be null", details
                    .getMessage());
        }

        try {
            cf = myFactory.newControlField("00X");
            fail("Failed to throw exception for a 00X ControlField tag");
        } catch (InvalidMARCException details) {
            // expected
        }
    }

    /**
     * Tests {@link ControlField#setData(String)} and
     * {@link ControlField#getData()}.
     */
    @Test
    public void testGetSetData() {
        ControlField cf = myFactory.newControlField("001");

        // Tests that what's set is what we get
        cf.setData("12883376");
        assertEquals("12883376", cf.getData());
    }

    /**
     * Tests {@link ControlField#setTag(String)} and
     * {@link ControlField#getTag()}.
     */
    @Test
    public void testGetSetTag() {
        ControlField cf = myFactory.newControlField();

        // Tests that what's set is what we get
        assertNull(cf.getTag());
        cf.setTag("001");
        assertEquals("001", cf.getTag());
    }

    /**
     * Tests {@link ControlField#find(String)}.
     */
    @Test
    public void testFind() {
        ControlField cf = myFactory.newControlField("001", "010101");

        // This one should pass
        assertTrue(cf.find("^0101[01]{2}$"));

        // This one should fail
        assertFalse(cf.find("^[01]{5}$"));
    }

    /**
     * Tests the {@link Comparable} aspect of {@link ControlField}.
     */
    @Test
    public void testComparable() {
        ControlField cf1 = myFactory.newControlField("008", "12345");
        ControlField cf2 = myFactory.newControlField("008", "12345");

        // These two should be the same at this point.
        assertEquals(0, cf1.compareTo(cf2));

        // Now, cf1 should be less than cf2
        cf2.setTag("009");
        assertEquals(-1, cf1.compareTo(cf2));

        // Now, cf1 should be greater than cf2
        cf2.setTag("007");
        assertEquals(1, cf1.compareTo(cf2));
    }

    /**
     * Tests {@link ControlField#toString()}.
     */
    @Test
    public void testToString() {
        ControlField cf = myFactory.newControlField("001", "000001");
        assertEquals("001 000001", cf.toString());
    }

    /**
     * Tests {@link ControlField#getId} and {@link ControlField#setId(Long)}.
     */
    @Test
    public void TestGetSetId() {
        ControlField cf = myFactory.newControlField();
        cf.setId(12345678910L);
        assertEquals((Long) 12345678910L, cf.getId());
    }
}
