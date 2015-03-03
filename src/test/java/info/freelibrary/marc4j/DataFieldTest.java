
package info.freelibrary.marc4j;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import java.util.Iterator;
import java.util.List;
import java.util.regex.PatternSyntaxException;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.marc4j.marc.DataField;
import org.marc4j.marc.InvalidMARCException;
import org.marc4j.marc.MarcFactory;
import org.marc4j.marc.Subfield;

public class DataFieldTest {

    @Rule
    public ExpectedException exception = ExpectedException.none();

    private final MarcFactory myFactory = MarcFactory.newInstance();

    /**
     * Tests the {@link DataField} constructor.
     */
    @Test
    public void testConstructor() {
        DataField df = myFactory.newDataField("245", '1', '0');
        assertEquals("245", df.getTag());
        assertEquals('1', df.getIndicator1());
        assertEquals('0', df.getIndicator2());
        assertEquals(0, df.countSubfields());

        df = myFactory.newDataField();
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
        final DataField df = myFactory.newDataField("245", '1', '0');
        final Subfield sf = myFactory.newSubfield('a', "Summerland");

        // Get a baseline count of subfields
        assertEquals(0, df.countSubfields());

        // Add a single subfield to a DataField
        df.addSubfield(sf);

        // Confirm that subfield was added
        assertEquals(1, df.countSubfields());
    }

    /**
     * Tests {@link DataField#setIndicator1()}.
     */
    @Test
    public void testSetIndicator1() {
        final DataField df = myFactory.newDataField("245", '1', '0');
        assertEquals('1', df.getIndicator1());
        df.setIndicator1('0');
        assertEquals('0', df.getIndicator1());
    }

    /**
     * Tests {@link DataField#setIndicator2()}.
     */
    @Test
    public void testSetIndicator2() {
        final DataField df = myFactory.newDataField("245", '1', '0');
        assertEquals('0', df.getIndicator2());
        df.setIndicator2('4');
        assertEquals('4', df.getIndicator2());
    }

    /**
     * Tests {@link DataField#addSubfield(int, Subfield).
     */
    @Test
    public void testAddSubfieldIndexSubfield() {
        final DataField df = myFactory.newDataField("245", '1', '0');
        final Subfield sf1 = myFactory.newSubfield('a', "Summerland");
        final Subfield sf2 = myFactory.newSubfield('c', "Michael Chabon");

        // Adds a subfield to give us something to insert before
        df.addSubfield(sf2);

        // Add a subfield at a particular index position
        df.addSubfield(0, sf1);

        // Test that we have two subfields and that ours is first
        assertEquals(2, df.countSubfields());
        assertEquals('a', df.getSubfields().get(0).getCode());
    }

    /**
     * Tests {@link DataField#countSubfields()}.
     */
    @Test
    public void testCountSubfields() {
        final DataField df = myFactory.newDataField("245", '1', '4');
        final Subfield sf1 = myFactory.newSubfield('a', "Summerland");
        final Subfield sf2 = myFactory.newSubfield('c', "Michael Chabon");

        assertEquals(0, df.countSubfields());
        df.addSubfield(sf1);
        assertEquals(1, df.countSubfields());
        df.addSubfield(sf2);
        assertEquals(2, df.countSubfields());
    }

    /**
     * Tests {@link DataField#getIndicator1()} and {@link DataField#getIndicator2()}.
     */
    @Test
    public void testGetIndicators() {
        final DataField df = myFactory.newDataField("245", '1', '4');
        assertEquals('1', df.getIndicator1());
        assertEquals('4', df.getIndicator2());
    }

    /**
     * Tests {@link DataField#getSubfield()}.
     */
    @Test
    public void testGetSubfield() {
        final DataField df = myFactory.newDataField("852", '0', '0');
        df.addSubfield(myFactory.newSubfield('h', "AC20"));
        df.addSubfield(myFactory.newSubfield('m', "Pre-1801 Coll"));
        df.addSubfield(myFactory.newSubfield('m', "fol"));
        df.addSubfield(myFactory.newSubfield('a', "DLC"));

        assertEquals("Pre-1801 Coll", df.getSubfield('m').getData());
    }

    /**
     * Tests {@link DataField#getSubfields()}.
     */
    @Test
    public void testGetSubfieldsChar() {
        final DataField df = myFactory.newDataField("852", '0', '0');
        df.addSubfield(myFactory.newSubfield('h', "AC20"));
        df.addSubfield(myFactory.newSubfield('m', "Pre-1801 Coll"));
        df.addSubfield(myFactory.newSubfield('m', "fol"));
        df.addSubfield(myFactory.newSubfield('a', "DLC"));

        // Check that resulting list contains the correct number of subfields
        final List<Subfield> subfields = df.getSubfields('m');
        assertEquals(2, subfields.size());

        final Iterator<Subfield> iterator = subfields.iterator();

        // Check that the list only contains subfields with code 'm'
        while (iterator.hasNext()) {
            assertEquals('m', iterator.next().getCode());
        }
    }

    /**
     * Tests {@link DataField#removeSubfield()}.
     */
    @Test
    public void testRemoveSubfield() {
        final DataField df = myFactory.newDataField("852", '0', '0');
        final Subfield sf = myFactory.newSubfield('m', "Pre-1801 Coll");

        df.addSubfield(myFactory.newSubfield('h', "AC20"));
        df.addSubfield(sf);
        df.addSubfield(myFactory.newSubfield('a', "DLC"));

        assertEquals(3, df.countSubfields());
        df.removeSubfield(sf);
        assertEquals(2, df.countSubfields());
        assertNull(df.getSubfield('m'));
    }

    /**
     * Tests {@link DataField#getSubfields()}.
     */
    @Test
    public void testGetSubfieldsString() {
        final DataField df = myFactory.newDataField("245", '1', '0');
        final Subfield sf1 = myFactory.newSubfield('a', "Summerland");
        final Subfield sf2 = myFactory.newSubfield('c', "Michael Chabon");

        df.addSubfield(sf2);
        df.addSubfield(0, sf1);

        final List<Subfield> sList = df.getSubfields("a");

        assertEquals(1, sList.size());
        assertEquals('a', sList.get(0).getCode());

        final List<Subfield> sList2 = df.getSubfields("ac");

        assertEquals(2, sList2.size());
        assertEquals('a', sList2.get(0).getCode());
        assertEquals('c', sList2.get(1).getCode());

        final List<Subfield> sList3 = df.getSubfields("[a-c]");

        assertEquals(2, sList3.size());
        assertEquals('a', sList3.get(0).getCode());
        assertEquals('c', sList3.get(1).getCode());

        final List<Subfield> sList4 = df.getSubfields("[c-e]");

        assertEquals(1, sList4.size());
        assertEquals('c', sList4.get(0).getCode());
    }

    /**
     * Tests the {@link Comparable} aspect of {@link DataField}.
     *
     * @throws Exception
     */
    @Test
    public void testComparable() {
        final DataField df1 = myFactory.newDataField("600", '0', '0');
        final DataField df2 = myFactory.newDataField("600", '0', '0');

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
            df = myFactory.newDataField("009", '1', '4');
            fail("009 is not a valid DataField tag");
        } catch (final InvalidMARCException details) {
            // expected
        }

        try {
            df = myFactory.newDataField("01", '1', '4');
            fail("01 is not a valid DataField tag");
        } catch (final InvalidMARCException details) {
            // expected
        }
    }

    @Test
    public void testGetSubfieldsWithBadSubfieldSpec1() {
        final DataField df = myFactory.newDataField("245", '0', '4');
        final Subfield sf1 = myFactory.newSubfield('a', "The summer-land ");
        final Subfield sf2 = myFactory.newSubfield('h', "[electronic resource] : ");
        final Subfield sf3 = myFactory.newSubfield('b', "a southern story / ");
        final Subfield sf4 = myFactory.newSubfield('c', "by a child of the sun.");
        df.addSubfield(sf1);
        df.addSubfield(sf2);
        df.addSubfield(sf3);
        df.addSubfield(sf4);

        exception.expect(PatternSyntaxException.class);
        df.getSubfields("[c-a]");
    }

    @Test
    public void testGetSubfieldsWithBadSubfieldSpec2() {
        final DataField df = myFactory.newDataField("245", '0', '4');
        final Subfield sf1 = myFactory.newSubfield('a', "The summer-land ");
        final Subfield sf2 = myFactory.newSubfield('h', "[electronic resource] : ");
        final Subfield sf3 = myFactory.newSubfield('b', "a southern story / ");
        final Subfield sf4 = myFactory.newSubfield('c', "by a child of the sun.");

        df.addSubfield(sf1);
        df.addSubfield(sf2);
        df.addSubfield(sf3);
        df.addSubfield(sf4);

        exception.expect(PatternSyntaxException.class);
        df.getSubfields("[abc");
    }

}
