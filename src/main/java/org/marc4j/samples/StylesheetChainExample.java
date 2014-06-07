
package org.marc4j.samples;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import javax.xml.transform.Result;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.sax.TransformerHandler;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.marc4j.MarcReader;
import org.marc4j.MarcStreamReader;
import org.marc4j.MarcWriter;
import org.marc4j.MarcXmlWriter;
import org.marc4j.marc.Record;

/**
 * A chain of transformation stages.
 *
 * @author Bas Peters
 */
public class StylesheetChainExample {

    /**
     * The main class for StylesheetChainExample.
     *
     * @param args
     * @throws Exception
     */
    public static void main(final String args[]) throws Exception {

        final TransformerFactory tFactory = TransformerFactory.newInstance();

        if (tFactory.getFeature(SAXSource.FEATURE) && tFactory.getFeature(SAXResult.FEATURE)) {

            // cast the transformer handler to a sax transformer handler
            final SAXTransformerFactory saxTFactory = ((SAXTransformerFactory) tFactory);

            // create a TransformerHandler for each stylesheet.
            final TransformerHandler tHandler1 =
                    saxTFactory.newTransformerHandler(new StreamSource(new File(
                            "src/test/resources/MARC21slim2MODS3.xsl")));
            final TransformerHandler tHandler2 =
                    saxTFactory.newTransformerHandler(new StreamSource(new File(
                            "src/test/resources/MODS2MARC21slim.xsl")));
            final TransformerHandler tHandler3 =
                    saxTFactory.newTransformerHandler(new StreamSource(new File(
                            "src/test/resources/MARC21slim2HTML.xsl")));

            // chain the transformer handlers
            tHandler1.setResult(new SAXResult(tHandler2));
            tHandler2.setResult(new SAXResult(tHandler3));

            final OutputStream out = new FileOutputStream(System.getProperty("java.io.tmpdir") + "/output.html");
            tHandler3.setResult(new StreamResult(out));

            // create a SAXResult with the first handler
            final Result result = new SAXResult(tHandler1);

            // create the input stream
            final InputStream input = new FileInputStream("src/test/resources/summerland.mrc");

            // parse the input
            final MarcReader reader = new MarcStreamReader(input);
            final MarcWriter writer = new MarcXmlWriter(result);

            while (reader.hasNext()) {
                final Record record = reader.next();
                writer.write(record);
            }

            writer.close();
            out.close();
        }
    }

}
