
package org.solrmarc.marcoverride;

import org.marc4j.marc.Leader;
import org.marc4j.marc.Record;
import org.marc4j.marc.impl.MarcFactoryImpl;

/**
 * @author Robert Haschart
 */
public class UVAMarcFactoryImpl extends MarcFactoryImpl {

    /**
     * Creates a new UVA record from the supplied {@link Leader}.
     */
    public Record newRecord(Leader leader) {
        Record record = new UVARecordImpl();
        record.setLeader(leader);
        return record;
    }

}
