
package org.marc4j.marc.impl;

import org.marc4j.marc.Leader;
import org.marc4j.marc.Record;

/**
 * @author Robert Haschart
 */
public class SortedMarcFactoryImpl extends MarcFactoryImpl {

    /**
     * Returns a new {@link Record} from the supplied {@link Leader}.
     */
    public Record newRecord(Leader leader) {
        Record record = new SortedRecordImpl();
        record.setLeader(leader);
        return record;
    }

}
