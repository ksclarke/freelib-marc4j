
package org.marc4j.samples;

import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.InputStream;

import javax.xml.transform.Source;
import javax.xml.transform.Templates;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.sax.TransformerHandler;
import javax.xml.transform.stream.StreamSource;

import org.marc4j.MarcReader;
import org.marc4j.MarcXmlReader;
import org.marc4j.marc.Record;

/**
 * Transformation with compiled stylesheet.
 *
 * @author Bas Peters
 */
public class TemplatesExample {

    /**
     * The main class for TemplateExample.
     *
     * @param args
     * @throws Exception
     */
    public static void main(final String args[]) throws Exception {
        final String inputDir = new File("src/test/resources/").getAbsolutePath();
        final TransformerFactory tFactory = TransformerFactory.newInstance();

        if (tFactory.getFeature(SAXSource.FEATURE) && tFactory.getFeature(SAXResult.FEATURE)) {

            // cast the transformer handler to a sax transformer handler
            final SAXTransformerFactory saxTFactory = ((SAXTransformerFactory) tFactory);
            final Source stylesheet = new StreamSource("http://www.loc.gov/standards/marcxml/xslt/MODS2MARC21slim.xsl");

            // create an in-memory stylesheet representation
            final Templates templates = tFactory.newTemplates(stylesheet);

            final File dir = new File(inputDir);

            // create a filter to include only .xml files
            final FilenameFilter filter = new FilenameFilter() {

                public boolean accept(final File dir, final String name) {
                    return name.endsWith(".xml");
                }
            };
            final File[] files = dir.listFiles(filter);

            for (int i = 0; i < files.length; i++) {
                final InputStream input = new FileInputStream(files[i]);

                final TransformerHandler handler = saxTFactory.newTransformerHandler(templates);

                // parse the input
                final MarcReader reader = new MarcXmlReader(input, handler);
                while (reader.hasNext()) {
                    final Record record = reader.next();
                    System.out.println(record.toString());
                }
            }
        }
    }

}
