
package info.freelibrary.marc4j;

import static org.junit.Assert.fail;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

import org.junit.Test;
import org.marc4j.MarcException;
import org.marc4j.MarcStreamReader;

/**
 * Tests of {@link MarcStreamReader}.
 *
 * @author <a href="mailto:ksclarke@gmail.com">Kevin S. Clarke</a>
 */
public class MarcStreamReaderTest {

    /**
     * Tests private {@link MarcStreamReader#getDataAsString()} when an explicit non-(UTF-8, Ansel, or ISO-8859-1)
     * character set is used.
     */
    @Test
    public void testGetDataAsStringWithExplicitCharset() {
        try {
            final MarcStreamReader reader =
                    new MarcStreamReader(new FileInputStream("src/test/resources/cyrillic_capital_e.mrc"),
                            "iso-8859-5");

            if (reader.hasNext()) {
                final String ctrlField = reader.next().getControlNumberField().getData();

                if (!ctrlField.equals("u6015439")) {
                    fail("Failed to get control number field data with explicit character set requested [Found: " +
                            ctrlField + "]");
                }
            }
        } catch (final FileNotFoundException details) {
            fail("Couldn't find the expected cyrillic_capital_e.mrc test resource file");
        }
    }

    @Test
    public void testParseRecordOnUnorderDirectoryEntries() {
        String file = "src/test/resources/unordered-directory-entries.mrc";
        try {
			MarcStreamReader reader =
                    new MarcStreamReader(new FileInputStream(file));

            while (reader.hasNext()) {
                reader.next();
            }
        } catch (FileNotFoundException e) {
            fail("Couldn't find the expected test resource file: " + file);
        } catch (MarcException e) {
            fail("Failed to parse record having unordered directory entries");
        }
    }

}
