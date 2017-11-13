package me.theyinspire.pandora.core.str;

/**
 * Token readers are abstract representations of the process of reading a single token from a given text.
 * It is assumed that each token reader will be capable of distinguishing a single type of token from the input text.
 *
 * @author Zohreh Sadeghi (zsadeghi@uw.edu)
 * @since 1.0 (10/26/17, 7:14 PM)
 */
public interface TokenReader {

    /**
     * This method will attempt to read the token this reader recognizes from the text input.
     * If the input does not match the expectations, it is expected that a {@code null} value
     * be returned.
     * @param text    the text input
     * @return the read token or {@code null} if no valid tokens were found
     */
    Token read(String text);

}