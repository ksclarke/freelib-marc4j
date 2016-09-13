package org.marc4j;

import info.freelibrary.marc4j.utils.StaticTestRecords;
import org.junit.Test;
import org.marc4j.marc.DataField;
import org.marc4j.marc.Record;

import java.util.*;
import java.util.stream.Collectors;

import static info.freelibrary.marc4j.utils.ResourceLoadUtils.getMARC21Reader;
import static info.freelibrary.marc4j.utils.ResourceLoadUtils.getMARCXMLReader;
import static org.junit.Assert.assertEquals;

/**
 * Unit tests (and examples) for the <code>RecordIterator</code> class.
 */
public class RecordIteratorTest {


    private List<String> chabonTitles = Arrays.asList("The amazing adventures of Kavalier and Clay :", "Summerland /");

    private Map<String, Integer> expectedCounts = new HashMap<>();


    public RecordIteratorTest() {
        expectedCounts.put(StaticTestRecords.RESOURCES_CHABON_MRC, 2);
        expectedCounts.put(StaticTestRecords.RESOURCES_CHABON_XML, 2);
    }

    private int countRecords(RecordIterator iterator) {
        int count = 0;
        while (iterator.hasNext() ) {
            count++;
            iterator.next();
        }
        return count;
    }


    @Test
    public void testForEachRemaining() {
        List<String> titles = new ArrayList<>();
        String testFile = StaticTestRecords.RESOURCES_CHABON_XML;
        RecordIterator instance = new RecordIterator( getMARCXMLReader(testFile) );
        instance.forEachRemaining( (rec) -> titles.add( getTitle(rec) ) );
        for(int i = 0, n = titles.size();i<n; i++ ) {
            assertEquals("Found unexpected title #" + i + " in " + testFile, chabonTitles.get(i), titles.get(i));
        }
    }


    @Test
    public void testSpliterator() {
        List<String> titles = new ArrayList<>();
        String testFile = StaticTestRecords.RESOURCES_CHABON_XML;
        RecordIterator instance = new RecordIterator( getMARCXMLReader(testFile) );
        int characteristics = instance.getSpliterator().characteristics();
        assertEquals("Spliterator should not be sized",0, characteristics & Spliterator.SIZED);
        assertEquals("Spliterator should not be subsized",0, characteristics & Spliterator.SUBSIZED);
        assertEquals("Spliterator should be distinct", Spliterator.DISTINCT, characteristics & Spliterator.DISTINCT);
        assertEquals("Spliterator shoudl be immutable", Spliterator.IMMUTABLE, characteristics & Spliterator.IMMUTABLE);
    }

    @Test
    public void testCountXML() {
        String testFile = StaticTestRecords.RESOURCES_CHABON_XML;
        int EXPECTED_COUNT = expectedCounts.get(testFile);
        RecordIterator instance = new RecordIterator( getMARCXMLReader(testFile) );
        assertEquals("Unexpected count of records in " + testFile, EXPECTED_COUNT, countRecords(instance));
    }

    @Test
    public void testCountStreamReader() {
        String testFile = StaticTestRecords.RESOURCES_CHABON_MRC;
        int EXPECTED_COUNT = expectedCounts.get(testFile);
        RecordIterator iterator = new RecordIterator( getMARC21Reader( testFile ) );
        assertEquals("Unexpected count of records in " + testFile, EXPECTED_COUNT, countRecords(iterator));
    }

    @Test
    public void testStreamCount() {

        String testFile = StaticTestRecords.RESOURCES_CHABON_MRC;
        long EXPECTED_COUNT = (long)expectedCounts.get(testFile);
        RecordIterator iterator = new RecordIterator( getMARC21Reader(testFile) );
        long streamCount =  iterator.toStream().count();
        assertEquals("Unexpected number of records in " + testFile , EXPECTED_COUNT, streamCount);
    }

    @Test
    public void testTitles() {
        String testFile = StaticTestRecords.RESOURCES_CHABON_MRC;
        RecordIterator iterator = new RecordIterator( getMARC21Reader(testFile) );
        List<String> titles = iterator.toStream().map(RecordIteratorTest::getTitle).collect( Collectors.toList() );
        assertEquals("Found unexpected title count in " + testFile, (long)expectedCounts.get(testFile), titles.size() );
        for(int i = 0, n = titles.size();i<n; i++ ) {
            assertEquals("Found unexpected title #" + i + " in " + testFile, chabonTitles.get(i), titles.get(i));
        }
    }

    private static String getTitle(Record rec) {
        return ((DataField) rec.getVariableField("245")).getSubfieldsAsString("a");
    }


}
