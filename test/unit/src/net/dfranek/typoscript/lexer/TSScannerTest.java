/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.dfranek.typoscript.lexer;

import junit.framework.TestCase;
import org.junit.*;
import org.netbeans.api.lexer.TokenSequence;

/**
 *
 * @author Daniel
 */
public class TSScannerTest extends TestCase {
	
	public TSScannerTest() {
	}

	@BeforeClass
	public static void setUpClass() throws Exception {
	}

	@AfterClass
	public static void tearDownClass() throws Exception {
	}
	
	@Before
	public void setUp() {
	}
	
	@After
	public void tearDown() {
	}

	@Test
	public void testLineComment() {
		TokenSequence<?> ts = TSLexerTestUtils.seqForText("# comment\n", TSTokenId.getLanguage());
		TSLexerTestUtils.next(ts, TSTokenId.TS_COMMENT, "# comment\n");
	}
	
	@Test
	public void testMultiLineComment() {
		TokenSequence<?> ts = TSLexerTestUtils.seqForText("/* first line\n*second line\n*/\n", TSTokenId.getLanguage());
		TSLexerTestUtils.next(ts, TSTokenId.TS_COMMENT, "/* first line\n");
		TSLexerTestUtils.next(ts, TSTokenId.TS_COMMENT, "*second line\n");
		TSLexerTestUtils.next(ts, TSTokenId.TS_COMMENT, "*/");
		TSLexerTestUtils.next(ts, TSTokenId.TS_NL, "\n");
	}
}
