
package info.freelibrary.marc4j.utils;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.marc4j.marc.ControlField;
import org.marc4j.marc.DataField;
import org.marc4j.marc.Record;
import org.marc4j.marc.Subfield;
import org.marc4j.marc.VariableField;

/**
 * Methods to assert when Record objects are equal or not, etc.
 *
 * @author Naomi
 */
public class RecordTestingUtils {

    /**
     * assert two Record objects are equal by comparing them as strings
     */
    public static void assertEquals(final Record expected, final Record actual) {
        final String actualId = actual.getControlNumber();
        final String errmsg = "Record " + actualId + " wasn't as expected";

        if (actualId.equals(expected.getControlNumber())) {
            assertTrue(errmsg, expected.toString().equals(actual.toString()));
        } else {
            fail(errmsg);
        }
    }

    /**
     * assert two Record objects aren't equal by comparing them as strings
     */
    public static void assertNotEqual(final Record expected, final Record actual) {
        final String actualId = actual.getControlNumber();
        if (!actualId.equals(expected.getControlNumber())) {
            return;
        }

        assertFalse("Records unexpectedly the same: " + actualId, expected.toString().equals(actual.toString()));
    }

    /**
     * assert two Record objects are equal by comparing them as strings, skipping over the leader
     */
    public static void assertEqualsIgnoreLeader(final Record expected, final Record actual) {
        final String actualId = actual.getControlNumber();
        final String errmsg = "Record " + actualId + " wasn't as expected";

        if (actualId.equals(expected.getControlNumber())) {
            assertTrue(errmsg, expected.toString().substring(24).equals(actual.toString().substring(24)));
        } else {
            fail(errmsg);
        }
    }

    /**
     * assert two Record objects are equal by comparing them as strings, skipping over the leader
     */
    public static String getFirstRecordDifferenceIgnoreLeader(final Record expected, final Record actual) {
        final String actualId = actual.getControlNumber();

        final String expectedSubstring = expected.toString().substring(24);
        final String actualSubstring = actual.toString().substring(24);

        if (actualId.equals(expected.getControlNumber())) {
            if (!expectedSubstring.equals(actualSubstring)) {
                final String expectedLines[] = expectedSubstring.split("\n");
                final String actualLines[] = actualSubstring.split("\n");

                int i = 0;

                for (; i < Math.min(expectedLines.length, actualLines.length); i++) {
                    if (!expectedLines[i].equals(actualLines[i])) {
                        return (expectedLines[i] + "\n" + actualLines[i]);
                    }
                }

                if (i >= expectedLines.length && i < actualLines.length) {
                    return (actualLines[i]);
                }

                if (i < expectedLines.length && i >= actualLines.length) {
                    return (expectedLines[i]);
                }
            }
        }

        return null;
    }

    /**
     * assert two Record objects are not equal by comparing them as strings, skipping over the leader
     */
    public static void assertNotEqualIgnoreLeader(final Record expected, final Record actual) {
        final String actualId = actual.getControlNumber();
        if (!actualId.equals(expected.getControlNumber())) {
            return;
        }

        assertFalse("Records unexpectedly the same: " + actualId, expected.toString().substring(24).equals(actual
                .toString().substring(24)));
    }

    /**
     * compare two marc records; the expected result is represented as an array of strings. The leaders don't match;
     * not sure why or if it matters.
     *
     * @param expected
     * @param actual
     */
    public static void assertEqualsIgnoreLeader(final String[] expected, final Record actual) {
        final String actualAsStr = actual.toString();
        // removing leader is removing "LEADER " and the 24 char leader and the newline
        final String actualAsStrWithoutLdr = actualAsStr.substring(32);

        final StringBuffer buf = new StringBuffer();
        for (int i = 1; i < expected.length; i++) {
            buf.append(expected[i] + "\n");
        }

        org.junit.Assert.assertEquals("Records weren't equal", buf.toString(), actualAsStrWithoutLdr);
    }

    /**
     * Given an expected marc record as an Array of strings corresponding to the lines in the output of MarcPrinter
     * and given the actual marc record as an InputStream, assert they are equal
     */
    public static void assertMarcRecsEqual(final String[] expectedAsLines, final InputStream actualAsInputStream) {
        BufferedReader actualAsBuffRdr = null;
        try {
            actualAsBuffRdr = new BufferedReader(new InputStreamReader(actualAsInputStream, "UTF-8"));
        } catch (final UnsupportedEncodingException e) {
            e.printStackTrace();
            fail("couldn't read record to be tested from InputStream");
        }

        final int numExpectedLines = expectedAsLines.length;

        try {
            int lineCnt = 0;
            String actualLine = null;
            while ((actualLine = actualAsBuffRdr.readLine()) != null) {
                if (actualLine.length() == 0) {
                    // do nothing;
                } else if (numExpectedLines > 0 && lineCnt < numExpectedLines) {
                    if (actualLine.equals("Flushing results...") || actualLine.equals("Flushing results done") ||
                            actualLine.startsWith("Cobertura:")) {
                        continue; // skip this line and don't even count it. I don't know where these "Flushing
                                  // Results..." lines are coming from.
                    }

                    final String expectedLine = expectedAsLines[lineCnt];
                    org.junit.Assert.assertEquals("output line [" + actualLine + "]  doesn't match expected [" +
                            expectedLine + "]", expectedLine, actualLine);
                }
                lineCnt++;
            }
        } catch (final IOException e) {
            e.printStackTrace();
            fail("couldn't compare records");
        }
    }

    /**
     * Assert that each instance of the subfield is in the expected values and that the number of instances match.
     */
    public static void assertSubfieldHasExpectedValues(final Record record, final String fieldTag,
            final char subfieldCode, final Set<String> expectedVals) {
        final Set<String> resultSet = new LinkedHashSet<String>();
        for (final VariableField vf : record.getVariableFields(fieldTag)) {
            final DataField df = (DataField) vf;
            final List<Subfield> sfList = df.getSubfields(subfieldCode);
            for (final Iterator<Subfield> iter2 = sfList.iterator(); iter2.hasNext();) {
                final Subfield sf = iter2.next();
                final String val = sf.getData();
                resultSet.add(val);
                assertTrue("Got unexpected value " + val, expectedVals.contains(val));
            }
        }
        org.junit.Assert.assertEquals("Number of values doesn't match", expectedVals.size(), resultSet.size());
    }

    /**
     * Assign id of record to be the ckey. Our ckeys are in 001 subfield a. Marc4j is unhappy with subfields in a
     * control field so this is a kludge work around.
     */
    public static String getRecordIdFrom001(final Record record) {
        String id = null;
        final ControlField fld = (ControlField) record.getVariableField("001");
        if (fld != null && fld.getData() != null) {
            final String rawVal = fld.getData();
            // 'u' is for testing
            if (rawVal.startsWith("a") || rawVal.startsWith("u")) {
                id = rawVal.substring(1);
            }
        }
        return id;
    }

}
