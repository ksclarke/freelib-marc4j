package org.marc4j;

import org.marc4j.marc.Record;

import java.util.Iterator;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.Consumer;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * Decorator for <code>MarcReader</code> objects that implements <code>Iterator</code>, and provides
 * methods for creating <code>Iterable</code> and <code>Stream</code> instances from the underlying reader.
 *
 * <p>
 *     Example usage:
 *
 *     <code>
 *         try( BufferedInputStream input = new BufferedInputStream( new FileInputSream("marc.xml") ) ) {
 *             MarcXmlReader reader = new MarcXmlReader(input);
 *             new RecordInterator(reader).forEach( (rec) -> {

 *              };
 *          }</code>
 * </p>
 *     <code>
 *         // assuming you know you have 245$a in all records ...
 *          List<String> titles = new RecordIterator(reader).toStream()
 *              .map( rec -> ((DataField)rec.getVariableField("245")).getSubfield('a").getValue()) )
 *              .filter( s -> s.contains("Badgers")
 *              .collect( Collectors.toList() );
 *     </code>
 * </p>
 *
 * @see MarcReader
 * @see Iterator
 */
public class RecordIterator implements Iterator<Record> {

    private final MarcReader wrappedReader;


    private boolean used = false;

    public RecordIterator(MarcReader wrappedReader) {
        this.wrappedReader = wrappedReader;
    }

    /**
     * @inheritDoc
     * @return
     */
    @Override
    public boolean hasNext() {
        return wrappedReader.hasNext();
    }

    /**
     * @inheritDoc
     */
    @Override
    public Record next() {
        return wrappedReader.next();
    }

    /**
     * @inheritDoc
     */
    @Override
    public void remove() {
        throw new UnsupportedOperationException("remove() not supported on this class");
    }

    /*
     * @inheritDoc
     */
    @Override
    public void forEachRemaining(Consumer<? super Record> action) {
        while( wrappedReader.hasNext() ) {
            action.accept(wrappedReader.next());
        }
    }

    /**
     * Adapt the underlying <code>MarcReader</code> as a Stream.
     * <p>
     *     Due to the properties of the underlying <code>MarcReader</code>, the stream is of unknown size,
     *     ordered, and immutable.
     * </p></p>
     * @return a sequence of records.
     */
    public Stream<Record> toStream() {
        return StreamSupport.stream( getSpliterator(), false);
    }

    protected Spliterator<Record> getSpliterator() {
        return Spliterators.spliteratorUnknownSize(this, Spliterator.ORDERED | Spliterator.DISTINCT | Spliterator.IMMUTABLE);
    }

}