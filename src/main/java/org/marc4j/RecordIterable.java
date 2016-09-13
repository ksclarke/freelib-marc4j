package org.marc4j;

import org.marc4j.marc.Record;

import java.util.Iterator;
import java.util.Spliterator;
import java.util.function.Consumer;

/**
 * Decorator to allow MarcReader instances to be used as <code>Iterable</code>s (e.g. in foreach loops).
 * <p>Implementation note: delegates to <code>ReccordIterator</code> for most of its functionality.</p>
 */
public class RecordIterable implements Iterable<Record> {

    private boolean used = false;

    private RecordIterator iterator;

    /**
     * Creates a new instance wrapping a supplied reader.
     * @param reader the reader to be decorated.
     */
    public RecordIterable(MarcReader reader) {
        this.iterator = new RecordIterator(reader);
    }


    /**
     * @inheritDoc
     */
    @Override
    public Iterator<Record> iterator() {
        if ( used  ) {
            throw new IllegalStateException("Unable to read more than once");
        }
        used = true;
        return iterator;

    }

    /**
     * @inheritDoc
     */
    @Override
    public void forEach(Consumer<? super Record> action) {
        iterator.forEachRemaining(action);
    }

    /**
     * @inheritDoc
     */
    @Override
    public Spliterator<Record> spliterator() {
        return iterator.getSpliterator();
    }
}
