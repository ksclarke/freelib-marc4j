
package info.freelibrary.marc4j;

import static org.junit.Assert.assertNotNull;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import org.junit.Test;
import org.marc4j.MarcPermissiveStreamReader;
import org.marc4j.MarcReader;
import org.marc4j.MarcStreamReader;
import org.marc4j.MarcStreamWriter;
import org.marc4j.MarcXmlReader;
import org.marc4j.MarcXmlWriter;
import org.marc4j.marc.Record;

import info.freelibrary.marc4j.converter.impl.AnselToUnicode;
import info.freelibrary.marc4j.converter.impl.UnicodeToAnsel;
import info.freelibrary.marc4j.utils.RecordTestingUtils;
import info.freelibrary.marc4j.utils.StaticTestRecords;

public class RoundtripTest {

    /**
     * This test reads in a file of UTF-8 encoded binary MARC records then writes those records out as UTF-8 encoded
     * binary MARC records, then reads those records. The test then compares those records with the original records,
     * expecting them to be identical.
     *
     * @throws Exception
     */
    @Test
    public void testWriteAndReadRoundtrip() throws Exception {
        final InputStream input = getClass().getResourceAsStream(StaticTestRecords.RESOURCES_CHABON_MRC);
        assertNotNull(input);
        final ByteArrayOutputStream out1 = new ByteArrayOutputStream();
        final MarcStreamReader marcReader = new MarcStreamReader(input);
        final MarcXmlWriter xmlWriter = new MarcXmlWriter(out1);
        while (marcReader.hasNext()) {
            final Record record = marcReader.next();
            xmlWriter.write(record);
        }
        input.close();
        xmlWriter.close();
        out1.close();
        final ByteArrayOutputStream out2 = new ByteArrayOutputStream();
        final ByteArrayInputStream in = new ByteArrayInputStream(out1.toByteArray());
        final MarcXmlReader xmlReader = new MarcXmlReader(in);
        final MarcStreamWriter marcWriter = new MarcStreamWriter(out2);
        while (xmlReader.hasNext()) {
            final Record record = xmlReader.next();
            marcWriter.write(record);
        }
        in.close();
        marcWriter.close();

        out2.close();

        final InputStream inputCompare1 = getClass().getResourceAsStream(StaticTestRecords.RESOURCES_CHABON_MRC);
        final InputStream inputCompare2 = new ByteArrayInputStream(out2.toByteArray());
        final MarcReader readComp1 = new MarcStreamReader(inputCompare1);
        final MarcReader readComp2 = new MarcStreamReader(inputCompare2);
        Record r1, r2;
        do {
            r1 = (readComp1.hasNext()) ? readComp1.next() : null;
            r2 = (readComp2.hasNext()) ? readComp2.next() : null;
            if (r1 != null && r2 != null) {
                RecordTestingUtils.assertEqualsIgnoreLeader(r1, r2);
            }
        } while (r1 != null && r2 != null);
    }

    /**
     * This test reads in a file of UTF8 encoded binary MARC records then writes those records out as MARC8 encoded
     * binary MARC records, then reads those records back in and writes them out as UTF8 encoded binary MARC records.
     * The test then compares those records with the original records, expecting them to be identical. Specifically
     * this tests for handling when sequence of Multibyte characters has one (or more) characters from G1 character
     * set. e.g. a center dot punctuation mark (0xA8) between two Chinese characters. Previously the UnicodeToAnsel
     * conversion would produce output in this situation that the AnselToUnicode converter would claim had errors.
     *
     * @throws Exception
     */
    @Test
    public void testWriteAndReadRoundtripChineseConverted() throws Exception {
        final InputStream input = getClass().getResourceAsStream(
                StaticTestRecords.RESOURCES_CHINESE_WITH_CENTRAL_DOT_MRC);
        assertNotNull(input);
        final ByteArrayOutputStream out1 = new ByteArrayOutputStream();
        final MarcStreamReader marcReader1 = new MarcStreamReader(input);
        final MarcStreamWriter marcWriter1 = new MarcStreamWriter(out1);
        marcWriter1.setConverter(new UnicodeToAnsel());
        while (marcReader1.hasNext()) {
            final Record record = marcReader1.next();
            marcWriter1.write(record);
        }
        input.close();
        marcWriter1.close();
        out1.close();
        final ByteArrayOutputStream out2 = new ByteArrayOutputStream();
        final ByteArrayInputStream in = new ByteArrayInputStream(out1.toByteArray());
        final MarcStreamReader marcReader2 = new MarcStreamReader(in);
        final MarcStreamWriter marcWriter2 = new MarcStreamWriter(out2, "UTF-8");
        marcWriter2.setConverter(new AnselToUnicode());
        while (marcReader2.hasNext()) {
            final Record record = marcReader2.next();
            marcWriter2.write(record);
        }
        in.close();
        marcWriter2.close();
        out2.close();

        final InputStream inputCompare1 = getClass().getResourceAsStream(
                StaticTestRecords.RESOURCES_CHINESE_WITH_CENTRAL_DOT_MRC);
        final InputStream inputCompare2 = new ByteArrayInputStream(out2.toByteArray());
        final MarcReader readComp1 = new MarcStreamReader(inputCompare1);
        final MarcReader readComp2 = new MarcStreamReader(inputCompare2);
        Record r1, r2;
        do {
            r1 = (readComp1.hasNext()) ? readComp1.next() : null;
            r2 = (readComp2.hasNext()) ? readComp2.next() : null;
            if (r1 != null && r2 != null) {
                RecordTestingUtils.assertEqualsIgnoreLeader(r1, r2);
            }
        } while (r1 != null && r2 != null);
    }

    /**
     * This test reads in a file of UTF8 encoded binary MARC records then writes those records out as MARC8 encoded
     * binary MARC records, then reads those records back in and writes them out as UTF8 encoded binary MARC records.
     * The test then compares those records with the original records, expecting them to be identical. Specifically
     * this tests for handling when sequence of Multibyte characters has one (or more) characters from G1 character
     * set. e.g. a center dot punctuation mark (0xA8) between two Chinese characters. Previously the UnicodeToAnsel
     * conversion would produce output in this situation that the AnselToUnicode converter would claim had errors.
     *
     * @throws Exception
     */
    @Test
    public void testWriteAndReadRoundtripChineseConvertedPermissive() throws Exception {
        final InputStream input = getClass().getResourceAsStream(
                StaticTestRecords.RESOURCES_CHINESE_WITH_CENTRAL_DOT_MRC);
        assertNotNull(input);
        final ByteArrayOutputStream out1 = new ByteArrayOutputStream();
        final MarcStreamReader marcReader1 = new MarcStreamReader(input);
        final MarcStreamWriter marcWriter1 = new MarcStreamWriter(out1);
        marcWriter1.setConverter(new UnicodeToAnsel());
        while (marcReader1.hasNext()) {
            final Record record = marcReader1.next();
            marcWriter1.write(record);
        }
        input.close();
        marcWriter1.close();
        out1.close();
        final ByteArrayOutputStream out2 = new ByteArrayOutputStream();
        final ByteArrayInputStream in = new ByteArrayInputStream(out1.toByteArray());
        final MarcReader marcReader2 = new MarcPermissiveStreamReader(in, true, true);
        final MarcStreamWriter marcWriter2 = new MarcStreamWriter(out2, "UTF-8");
        // marcWriter2.setConverter(new AnselToUnicode());
        while (marcReader2.hasNext()) {
            final Record record = marcReader2.next();
            marcWriter2.write(record);
        }
        in.close();
        marcWriter2.close();
        out2.close();

        final InputStream inputCompare1 = getClass().getResourceAsStream(
                StaticTestRecords.RESOURCES_CHINESE_WITH_CENTRAL_DOT_MRC);
        final InputStream inputCompare2 = new ByteArrayInputStream(out2.toByteArray());
        final MarcReader readComp1 = new MarcStreamReader(inputCompare1);
        final MarcReader readComp2 = new MarcStreamReader(inputCompare2);
        Record r1, r2;
        do {
            r1 = (readComp1.hasNext()) ? readComp1.next() : null;
            r2 = (readComp2.hasNext()) ? readComp2.next() : null;
            if (r1 != null && r2 != null) {
                RecordTestingUtils.assertEqualsIgnoreLeader(r1, r2);
            }
        } while (r1 != null && r2 != null);
    }

    /**
     * This test reads in a file of Marc8 encoded binary MARC records then writes those records out as UTF-8 encoded
     * binary MARC records, then reads those records back in and writes them out as Marc8 encoded binary MARC records.
     * The test then compares those records with the original records, expecting them to be identical.
     *
     * @throws Exception
     */
    @Test
    public void testWriteAndReadRoundtripConverted() throws Exception {
        final InputStream input = getClass().getResourceAsStream(StaticTestRecords.RESOURCES_BRKRTEST_MRC);
        assertNotNull(input);
        final ByteArrayOutputStream out1 = new ByteArrayOutputStream();
        final MarcStreamReader marcReader1 = new MarcStreamReader(input);
        final MarcStreamWriter marcWriter1 = new MarcStreamWriter(out1, "UTF-8");
        marcWriter1.setConverter(new AnselToUnicode());
        while (marcReader1.hasNext()) {
            final Record record = marcReader1.next();
            marcWriter1.write(record);
        }
        input.close();
        marcWriter1.close();
        out1.close();
        final ByteArrayOutputStream out2 = new ByteArrayOutputStream();
        final ByteArrayInputStream in = new ByteArrayInputStream(out1.toByteArray());
        final MarcStreamReader marcReader2 = new MarcStreamReader(in);
        final MarcStreamWriter marcWriter2 = new MarcStreamWriter(out2);
        marcWriter2.setConverter(new UnicodeToAnsel());
        while (marcReader2.hasNext()) {
            final Record record = marcReader2.next();
            marcWriter2.write(record);
        }
        in.close();
        marcWriter2.close();
        out2.close();

        final InputStream inputCompare1 = getClass().getResourceAsStream(StaticTestRecords.RESOURCES_BRKRTEST_MRC);
        final InputStream inputCompare2 = new ByteArrayInputStream(out2.toByteArray());
        final MarcReader readComp1 = new MarcStreamReader(inputCompare1);
        final MarcReader readComp2 = new MarcStreamReader(inputCompare2);
        Record r1, r2;
        do {
            r1 = (readComp1.hasNext()) ? readComp1.next() : null;
            r2 = (readComp2.hasNext()) ? readComp2.next() : null;
            if (r1 != null && r2 != null) {
                RecordTestingUtils.assertEqualsIgnoreLeader(r1, r2);
            }
        } while (r1 != null && r2 != null);
    }

    /**
     * This test reads in a file of Marc8 encoded binary MARC records then writes those records out as UTF-8 encoded
     * MarcXML records, then reads those records back in and writes them out as Marc8 binary MARC records. The test
     * then compares those binary MARC records with the original records, expecting them to be identical.
     *
     * @throws Exception
     */
    @Test
    public void testConvertToXMLRoundtrip() throws Exception {
        final InputStream input = getClass().getResourceAsStream(StaticTestRecords.RESOURCES_BRKRTEST_MRC);
        assertNotNull(input);
        final ByteArrayOutputStream out1 = new ByteArrayOutputStream();
        final MarcStreamReader marcReader = new MarcStreamReader(input);
        final MarcXmlWriter xmlWriter = new MarcXmlWriter(out1);
        xmlWriter.setConverter(new AnselToUnicode());
        while (marcReader.hasNext()) {
            final Record record = marcReader.next();
            xmlWriter.write(record);
        }
        input.close();
        xmlWriter.close();
        out1.close();
        final ByteArrayOutputStream out2 = new ByteArrayOutputStream();
        final ByteArrayInputStream in = new ByteArrayInputStream(out1.toByteArray());
        final MarcXmlReader xmlReader = new MarcXmlReader(in);
        final MarcStreamWriter marcWriter = new MarcStreamWriter(out2);
        marcWriter.setConverter(new UnicodeToAnsel());
        while (xmlReader.hasNext()) {
            final Record record = xmlReader.next();
            marcWriter.write(record);
        }
        in.close();
        marcWriter.close();

        out2.close();
        final ByteArrayOutputStream out3 = new ByteArrayOutputStream();
        final ByteArrayInputStream in2 = new ByteArrayInputStream(out2.toByteArray());
        final MarcStreamReader marcReader2 = new MarcStreamReader(in2);
        final MarcXmlWriter xmlWriter2 = new MarcXmlWriter(out3);
        xmlWriter2.setConverter(new AnselToUnicode());
        while (marcReader2.hasNext()) {
            final Record record = marcReader2.next();
            xmlWriter2.write(record);
        }
        in2.close();
        xmlWriter2.close();

        out3.close();

        final InputStream inputCompare1 = new ByteArrayInputStream(out1.toByteArray());
        final InputStream inputCompare2 = new ByteArrayInputStream(out3.toByteArray());
        final MarcXmlReader readComp1 = new MarcXmlReader(inputCompare1);
        final MarcXmlReader readComp2 = new MarcXmlReader(inputCompare2);
        Record r1, r2;
        do {
            r1 = (readComp1.hasNext()) ? readComp1.next() : null;
            r2 = (readComp2.hasNext()) ? readComp2.next() : null;
            if (r1 != null && r2 != null) {
                RecordTestingUtils.assertEqualsIgnoreLeader(r1, r2);
            }
        } while (r1 != null && r2 != null);
    }

    /**
     * This test reads in a file of Marc8 encoded binary MARC records then writes those records out as UTF-8 encoded
     * MarcXML records using unicode normalization, which combines diacritics with the character they adorn (whenever
     * possible). It then reads those records back in and writes them out as Marc8 binary MARC records. The test then
     * compares those binary MARC records with original binary MARC records, expecting them to be identical. Note:
     * Since there are multiple ways of representing some unicode characters in marc8, it is not possible to guarantee
     * roundtripping from MARC-8 encoded records to UTF-8 and back to marc8, the likelihood is even higher when the
     * UTF-8 characters are normalized. It is possible to guarantee roundtripping from normalized UTF-8 encoded
     * records to MARC-8 encoded binary MARC records back to normalized UTF-8 records.
     *
     * @throws Exception
     */
    @Test
    public void testConvertToXMLNormalizedRoundtrip() throws Exception {
        final InputStream input = getClass().getResourceAsStream(StaticTestRecords.RESOURCES_BRKRTEST_MRC);
        assertNotNull(input);
        final ByteArrayOutputStream out1 = new ByteArrayOutputStream();
        final MarcStreamReader marcReader = new MarcStreamReader(input);
        final MarcXmlWriter xmlWriter = new MarcXmlWriter(out1);
        xmlWriter.setConverter(new AnselToUnicode());
        xmlWriter.setUnicodeNormalization(true);
        while (marcReader.hasNext()) {
            final Record record = marcReader.next();
            xmlWriter.write(record);
        }
        input.close();
        xmlWriter.close();
        out1.close();
        final ByteArrayOutputStream out2 = new ByteArrayOutputStream();
        final ByteArrayInputStream in = new ByteArrayInputStream(out1.toByteArray());
        final MarcXmlReader xmlReader = new MarcXmlReader(in);
        final MarcStreamWriter marcWriter = new MarcStreamWriter(out2);
        marcWriter.setConverter(new UnicodeToAnsel());
        while (xmlReader.hasNext()) {
            final Record record = xmlReader.next();
            marcWriter.write(record);
        }
        in.close();
        marcWriter.close();

        out2.close();
        final InputStream inputCompare1 = getClass().getResourceAsStream(StaticTestRecords.RESOURCES_BRKRTEST_MRC);
        assertNotNull(inputCompare1);
        final InputStream inputCompare2 = new ByteArrayInputStream(out2.toByteArray());
        final MarcStreamReader readComp1 = new MarcStreamReader(inputCompare1);
        final MarcStreamReader readComp2 = new MarcStreamReader(inputCompare2);
        Record r1, r2;
        do {
            r1 = (readComp1.hasNext()) ? readComp1.next() : null;
            r2 = (readComp2.hasNext()) ? readComp2.next() : null;
            if (r1 != null && r2 != null) {
                RecordTestingUtils.assertEqualsIgnoreLeader(r1, r2);
            }
        } while (r1 != null && r2 != null);

    }

    /**
     * This test reads in a file of UTF-8 encoded MarcXML records then writes those records out as MARC-8 encoded
     * binary records using numeric characters representations (NCR) instead of the standard MARC-8 encodings. It then
     * reads those records back in and writes them out as UTF-8 encoded MarcXML records. The test then compares those
     * MarcXML records with the UTF-8 encoded MarcXML records, expecting them to be identical.
     *
     * @throws Exception
     */
    @Test
    public void testConvertToMarc8NCRRoundtrip() throws Exception {
        final InputStream input = getClass().getResourceAsStream(StaticTestRecords.RESOURCES_OCLC814388508_XML);
        assertNotNull(input);
        final ByteArrayOutputStream out1 = new ByteArrayOutputStream();
        final MarcXmlReader marcReader1 = new MarcXmlReader(input);
        final MarcStreamWriter marcWriter1 = new MarcStreamWriter(out1);
        marcWriter1.setConverter(new UnicodeToAnsel(true));
        while (marcReader1.hasNext()) {
            final Record record = marcReader1.next();
            marcWriter1.write(record);
        }
        input.close();
        marcWriter1.close();
        out1.close();
        final ByteArrayOutputStream out2 = new ByteArrayOutputStream();
        final ByteArrayInputStream in = new ByteArrayInputStream(out1.toByteArray());
        final MarcStreamReader marcReader2 = new MarcStreamReader(in);
        final MarcXmlWriter marcWriter2 = new MarcXmlWriter(out2);
        final AnselToUnicode conv = new AnselToUnicode();
        conv.setTranslateNCR(true);
        marcWriter2.setConverter(conv);
        while (marcReader2.hasNext()) {
            final Record record = marcReader2.next();
            marcWriter2.write(record);
        }
        in.close();
        marcWriter2.close();
        out2.close();

        final InputStream inputCompare1 = getClass().getResourceAsStream(
                StaticTestRecords.RESOURCES_OCLC814388508_XML);
        final InputStream inputCompare2 = new ByteArrayInputStream(out2.toByteArray());
        final MarcReader readComp1 = new MarcXmlReader(inputCompare1);
        final MarcReader readComp2 = new MarcXmlReader(inputCompare2);
        Record r1, r2;
        do {
            r1 = (readComp1.hasNext()) ? readComp1.next() : null;
            r2 = (readComp2.hasNext()) ? readComp2.next() : null;
            if (r1 != null && r2 != null) {
                RecordTestingUtils.assertEqualsIgnoreLeader(r1, r2);
            }
        } while (r1 != null && r2 != null);
    }

}
