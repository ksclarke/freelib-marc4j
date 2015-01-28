
package info.freelibrary.marc4j;

import org.junit.Test;

import junit.framework.TestCase;
import org.marc4j.marc.Leader;
import org.marc4j.marc.MarcFactory;

public class LeaderTest extends TestCase {

    MarcFactory factory = MarcFactory.newInstance();

    /**
     * Tests the constructor of {@link Leader}.
     */
    @Test
    public void testConstructor() {
        Leader leader = factory.newLeader();
        assertNotNull("leader is null", leader);
    }

    /**
     * Tests {@link Leader#unmarshal(String)}.
     */
    @Test
    public void testUnmarshal() {
        Leader leader = factory.newLeader();
        leader.unmarshal("00714cam a2200205 a 4500");
        assertEquals("00714cam a2200205 a 4500", leader.toString());
    }

    /**
     * Tests {@link Leader#unmarshal(String)}.
     */
    @Test
    public void testUnmarshalSubfieldCodeLength() {
        Leader leader = factory.newLeader();
        leader.unmarshal("00714cam a2100205 a 4500");
        assertEquals(1, leader.getSubfieldCodeLength());
    }

    /**
     * Tests {@link Leader#marshal()}.
     */
    @Test
    public void testMarshal() {
        Leader leader = factory.newLeader("00714cam a2200205 a 4500");
        assertEquals("00714cam a2200205 a 4500", leader.marshal());
    }

}
