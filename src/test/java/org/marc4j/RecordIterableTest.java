package org.marc4j;

import info.freelibrary.marc4j.utils.StaticTestRecords;
import org.junit.Test;
import org.marc4j.marc.Record;

import static info.freelibrary.marc4j.utils.ResourceLoadUtils.getMARCXMLReader;
import static org.junit.Assert.assertEquals;

/**
 * Unit tests for <code>RecordIterable</code>
 */
public class RecordIterableTest {

    @Test
    public void testCount() {
        int EXPECTED_COUNT = 1;
        String testFile = StaticTestRecords.RESOURCES_OCLC814388508_XML;
        RecordIterable instance =  new RecordIterable(getMARCXMLReader(testFile));
        int count = 0;
        for( Record rec : instance ) {
            count++;
        }
        assertEquals("Unexpected number of records in " + testFile, EXPECTED_COUNT, count);
    }

}
