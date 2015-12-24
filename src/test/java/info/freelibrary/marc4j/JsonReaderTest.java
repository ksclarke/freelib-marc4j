
package info.freelibrary.marc4j;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.InputStream;

import org.junit.Test;
import org.marc4j.MarcJsonReader;
import org.marc4j.MarcReader;
import org.marc4j.marc.Record;
import org.marc4j.util.JsonParser;

import info.freelibrary.marc4j.utils.RecordTestingUtils;
import info.freelibrary.marc4j.utils.StaticTestRecords;
import info.freelibrary.marc4j.utils.TestUtils;

public class JsonReaderTest {

    /**
     * Tests reading invalid MARC with {@link JsonReader}.
     */
    @Test
    public void testInvalidMarcInJsonReader() {
        try {
            checkMarcJsonDylanRecordFromFile(StaticTestRecords.RESOURCES_ILLEGAL_MARC_IN_JSON_JSON);
            fail();
        } catch (final JsonParser.Escape e) {
            final String msg =
                    "controls must be escaped using \\uHHHH; at Input Source: \"MarcInput\", Line: 170, Column: EOL";
            assertTrue("EOL", e.getMessage().contains(msg));
        }
    }

    /**
     * Tests {@link MarcJsonReader}.
     *
     * @throws Exception
     */
    @Test
    public void testMarcJsonReaders() throws Exception {
        final InputStream input1 = getResourceAsStream(StaticTestRecords.RESOURCES_MARC_JSON_JSON);
        final MarcReader reader1 = new MarcJsonReader(input1);

        final InputStream input2 = getResourceAsStream(StaticTestRecords.RESOURCES_MARC_IN_JSON_JSON);
        final MarcReader reader2 = new MarcJsonReader(input2);

        while (reader1.hasNext() && reader2.hasNext()) {
            final Record record1 = reader1.next();
            final Record record2 = reader2.next();
            final String recordAsStrings1[] = record1.toString().split("\n");
            final String recordAsStrings2[] = record2.toString().split("\n");

            for (int i = 0; i < recordAsStrings1.length; i++) {
                assertEquals("line mismatch between two records", recordAsStrings1[i], recordAsStrings2[i]);
            }

            if (record1 != null && record2 != null) {
                RecordTestingUtils.assertEqualsIgnoreLeader(record1, record2);
            }
        }

        input1.close();
        input2.close();
    }

    /**
     * Tests {@link MarcJsonReader}.
     *
     * @throws Exception
     */
    @Test
    public void testMarcJsonReader() throws Exception {
        checkMarcJsonDylanRecordFromFile(StaticTestRecords.RESOURCES_MARC_JSON_JSON);
    }

    /**
     * Tests legal MARC in JSON.
     *
     * @throws Exception
     */
    @Test
    public void testLegalMarcInJson() throws Exception {
        checkMarcJsonDylanRecordFromFile(StaticTestRecords.RESOURCES_LEGAL_JSON_MARC_IN_JSON_JSON);
    }

    private InputStream getResourceAsStream(final String fileName) {
        final InputStream input1 = getClass().getResourceAsStream(fileName);

        assertNotNull(fileName, input1);

        return input1;
    }

    private void checkMarcJsonDylanRecordFromFile(final String fileName) {
        final InputStream input = getResourceAsStream(fileName);
        final MarcReader reader = new MarcJsonReader(input);

        if (!reader.hasNext()) {
            fail("should have at least one record");
        }

        final Record record = reader.next();

        TestUtils.validateFreewheelingBobDylanRecord(record);

        if (reader.hasNext()) {
            fail("should not have more than one record");
        }
    }

}
