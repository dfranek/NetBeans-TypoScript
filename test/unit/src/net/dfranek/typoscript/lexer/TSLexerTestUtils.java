/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.dfranek.typoscript.lexer;

import junit.framework.TestCase;
import org.netbeans.api.lexer.*;
import org.netbeans.lib.lexer.LexerUtilsConstants;

/**
 *
 * @author Daniel
 */
public class TSLexerTestUtils extends TestCase {

	static TokenSequence<?> seqForText(String text, Language<TSTokenId> language) {
		TokenHierarchy<?> hi = TokenHierarchy.create(text, language);
        return hi.tokenSequence(language);
	}
	
	public static void next(TokenSequence<?> ts, TokenId id, String fixedText) {
        assertTrue(ts.moveNext());
        TSLexerTestUtils.assertTokenEquals("Token index[" + ts.index() + "]", ts, id, fixedText, -1);
    }
	
	/**
     * Compare <code>TokenSequence.token()</code> to the given
     * token id, text and offset.
     *
     * @param offset expected offset. It may be -1 to prevent offset testing.
     */
    public static void assertTokenEquals(String message, TokenSequence<?> ts, TokenId id, String text, int offset) {
        message = messagePrefix(message);
        Token<?> t = ts.token();
        TestCase.assertNotNull("Token is null", t);
        TokenId tId = t.id();
        TestCase.assertEquals(message + "Invalid token.id() for text=\"" + debugTextOrNull(t.text()) + '"', id, tId);
        CharSequence tText = t.text();
        assertTextEquals(message + "Invalid token.text() for id=" + LexerUtilsConstants.idToString(id), text, tText);
        // The token's length must correspond to text.length()
        TestCase.assertEquals(message + "Invalid token.length()", text.length(), t.length());

        if (offset != -1) {
            int tsOffset = ts.offset();
            TestCase.assertEquals(message + "Invalid tokenSequence.offset()", offset, tsOffset);

            // It should also be true that if the token is non-flyweight then
            // ts.offset() == t.offset()
            // and if it's flyweight then t.offset() == -1
            int tOffset = t.offset(null);
            assertTokenOffsetMinusOneForFlyweight(t.isFlyweight(), tOffset);
            if (!t.isFlyweight()) {
                assertTokenOffsetsEqual(message, tOffset, offset);
            }
        }
    }
	
	private static String messagePrefix(String message) {
        if (message != null) {
            message = message + ": ";
        } else {
            message = "";
        }
        return message;
    }
	
	/**
     * Return the given text as String
     * translating the special characters (and '\') into escape sequences.
     *
     * @param text non-null text to be debugged.
     * @return non-null string containing the debug text or "<null>".
     */
    public static String debugTextOrNull(CharSequence text) {
        return (text != null) ? debugText(text) : "<null>";
    }
	
	public static void assertTextEquals(String message, CharSequence expected, CharSequence actual) {
        if (!textEquals(expected, actual)) {
            TestCase.fail(messagePrefix(message) +
                " expected:\"" + expected + "\" but was:\"" + actual + "\"");
        }
    }
	
	/**
     * Return the given text as String
     * translating the special characters (and '\') into escape sequences.
     *
     * @param text non-null text to be debugged.
     * @return non-null string containing the debug text.
     */
    public static String debugText(CharSequence text) {
        return TokenUtilities.debugText(text);
    }
	
	/**
     * Compare whether the two character sequences represent the same text.
     */
    public static boolean textEquals(CharSequence text1, CharSequence text2) {
        return TokenUtilities.equals(text1, text2);
    }
	
	private static void assertTokenOffsetMinusOneForFlyweight(boolean tokenFlyweight, int offset) {
        if (tokenFlyweight) {
            TestCase.assertEquals("Flyweight token => token.offset()=-1", -1, offset);
        } else { // non-flyweight
            TestCase.assertTrue("Non-flyweight token => token.offset()!=-1 but " + offset, (offset != -1));
        }
    }
	
	public static void assertTokenOffsetsEqual(String message, int offset1, int offset2) {
        if (offset1 != -1 && offset2 != -1) { // both non-flyweight
            TestCase.assertEquals(messagePrefix(message)
                    + "Offsets equal", offset1, offset2);
        }
    }
	
}
