
package info.freelibrary.marc4j;

import static org.junit.Assert.fail;

import java.io.OutputStream;
import java.io.PrintStream;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.Set;

import org.junit.Test;
import org.reflections.Reflections;
import org.reflections.scanners.ResourcesScanner;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;

/**
 * This is not really a unit test, but just a way to run all the MARC4J samples. They will be rewritten as unit tests,
 * but until then this is just an automated sanity check to make sure no unexpected exceptions are being thrown.
 *
 * @author <a href="mailto:ksclarke@gmail.com">Kevin S. Clarke</a>
 */
public class SamplesTest {

    /**
     * A pseudo-test that runs all the MARC4J sample classes.
     */
    @Test
    public void test() {
        final Reflections reflections =
                new Reflections(new ConfigurationBuilder().setScanners(new SubTypesScanner(false),
                        new ResourcesScanner()).setUrls(ClasspathHelper.forPackage("org.marc4j.samples")));

        final Set<Class<? extends Object>> samples = reflections.getSubTypesOf(Object.class);
        final Iterator<Class<? extends Object>> iterator = samples.iterator();
        final PrintStream outStream = System.out;
        final PrintStream errStream = System.err;

        // Turn off the System.out and System.err for these sample classes
        System.setOut(new PrintStream(new OutputStream() {

            @Override
            public void write(final int b) {
            }
        }));

        System.setErr(new PrintStream(new OutputStream() {

            @Override
            public void write(final int b) {
            }
        }));

        // Go through and find all the sample classes and run them as a pseudo-test
        while (iterator.hasNext()) {
            final Class<? extends Object> c = iterator.next();

            /*
             * Let's go through and run all our samples. These are not real tests, but will throw exceptions.
             */
            if (c.getPackage().getName().equals("org.marc4j.samples")) {
                try {
                    final Class<?> cls = Class.forName(c.getName());
                    final Method method = cls.getMethod("main", String[].class);
                    final String[] params = null;

                    method.invoke(null, (Object) params);
                } catch (final NoSuchMethodException details) {
                    // Okay to skip for now... interested in low hanging fruit
                } catch (final Exception details) {
                    System.setOut(outStream);
                    System.setErr(errStream);

                    fail(c.getName() + " failed: " + details.getMessage());
                }
            }
        }

        System.setOut(outStream);
        System.setErr(errStream);
    }

}
