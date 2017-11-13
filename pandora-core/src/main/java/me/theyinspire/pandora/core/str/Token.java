package me.theyinspire.pandora.core.str;

/**
 * This interface defines the abstract meaning of a token as a {@code <start, end>} pair that can optionally
 * have a tag that will attach semantics to the token.
 *
 * @author Zohreh Sadeghi (zsadeghi@uw.edu)
 * @since 1.0 (10/26/17, 7:14 PM)
 */
public interface Token {

    int NO_TAG = 0;

    /**
     * @return the (optional) tag associated with this token
     */
    int getTag();

    /**
     * @return the start position of the token relevant to the text it was generated from
     */
    int getStart();

    /**
     * @return the position of the last character of this token
     */
    int getEnd();

    /**
     * @return the number of characters that should be skipped as part of the semantics
     * of the definition for this token, which hold no value for the reader.
     */
    int getMargin();

    /**
     * @return the length of the text this token denotes
     */
    int getLength();

    /**
     * @return {@code true} if the token has a tag assigned
     */
    boolean isTagged();

}
