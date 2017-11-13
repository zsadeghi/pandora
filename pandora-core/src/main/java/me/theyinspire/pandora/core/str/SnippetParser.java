package me.theyinspire.pandora.core.str;

/**
 * @author Zohreh Sadeghi (zsadeghi@uw.edu)
 * @since 1.0 (10/26/17, 7:13 PM)
 */
public interface SnippetParser<E> {

    /**
     * This method will be called by an instance of {@link DocumentReader} through the
     * {@link DocumentReader#parse(SnippetParser)} method
     * @param reader the reader which holds the current document
     * @return the result of the parse operation. This usually is some sort of data
     * represented by the text just read.
     */
    E parse(DocumentReader reader);

}
