
package org.solrmarc.marcoverride;

import org.marc4j.marc.Leader;
import org.marc4j.marc.Record;
import org.marc4j.marc.impl.MarcFactoryImpl;

/**
 * @author Robert Haschart
 */
public class NoSortMarcFactoryImpl extends MarcFactoryImpl {

    /**
     * Creates a new record from the supplied {@link Leader}.
     */
    public Record newRecord(Leader leader) {
        Record record = new NoSortRecordImpl();
        record.setLeader(leader);
        return record;
    }

}
