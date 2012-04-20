/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2011 Daniel Franek.
 *
 * Oracle and Java are registered trademarks of Oracle and/or its affiliates.
 * Other names may be trademarks of their respective owners.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common
 * Development and Distribution License("CDDL") (collectively, the
 * "License"). You may not use this file except in compliance with the
 * License. You can obtain a copy of the License at
 * http://www.netbeans.org/cddl-gplv2.html
 * or nbbuild/licenses/CDDL-GPL-2-CP. See the License for the
 * specific language governing permissions and limitations under the
 * License.  When distributing the software, include this License Header
 * Notice in each file and include the License file at
 * nbbuild/licenses/CDDL-GPL-2-CP.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the GPL Version 2 section of the License file that
 * accompanied this code. If applicable, add the following below the
 * License Header, with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 * 
 * If you wish your version of this file to be governed by only the CDDL
 * or only the GPL Version 2, indicate your decision by adding
 * "[Contributor] elects to include this software in this distribution
 * under the [CDDL or GPL Version 2] license." If you do not indicate a
 * single choice of license, a recipient has the option to distribute
 * your version of this file under either the CDDL, the GPL Version 2 or
 * to extend the choice of license to its licensees as provided above.
 * However, if you add GPL Version 2 code and therefore, elected the GPL
 * Version 2 license, then the option applies only if the new code is
 * made subject to such option by the copyright holder.
 * 
 */
package net.dfranek.typoscript.lexer;

import java.io.File;
import junit.framework.TestCase;
import org.junit.Test;
import org.netbeans.api.lexer.TokenSequence;

/**
 *
 * @author Daniel Franek
 */
public class TSScannerTest extends TestCase {

	@Test
	public void testLineComment() {
		TokenSequence<?> ts = TSLexerTestUtils.seqForText("# comment\n", TSTokenId.getLanguage());
		TSLexerTestUtils.next(ts, TSTokenId.TS_COMMENT, "# comment");
		TSLexerTestUtils.next(ts, TSTokenId.TS_NL, "\n");
	}
	
	@Test
	public void testMultiLineComment() {
		TokenSequence<?> ts = TSLexerTestUtils.seqForText("/* first line\n*second line\n*/\n", TSTokenId.getLanguage());
		TSLexerTestUtils.next(ts, TSTokenId.TS_COMMENT, "/* first line\n");
		TSLexerTestUtils.next(ts, TSTokenId.TS_COMMENT, "*second line\n");
		TSLexerTestUtils.next(ts, TSTokenId.TS_COMMENT, "*/");
		TSLexerTestUtils.next(ts, TSTokenId.TS_NL, "\n");
	}
	
	@Test
	public void testProperty() {
		TokenSequence<?> ts = TSLexerTestUtils.seqForText("page.10.marks.TEST = something", TSTokenId.getLanguage());
		TSLexerTestUtils.next(ts, TSTokenId.TS_KEYWORD2, "page");
		TSLexerTestUtils.next(ts, TSTokenId.TS_OPERATOR, ".");
		TSLexerTestUtils.next(ts, TSTokenId.TS_NUMBER, "10");
		TSLexerTestUtils.next(ts, TSTokenId.TS_OPERATOR, ".");
		TSLexerTestUtils.next(ts, TSTokenId.TS_KEYWORD2, "marks");
		TSLexerTestUtils.next(ts, TSTokenId.TS_OPERATOR, ".");
		TSLexerTestUtils.next(ts, TSTokenId.TS_PROPERTY, "TEST");
		TSLexerTestUtils.next(ts, TSTokenId.WHITESPACE, " ");
		TSLexerTestUtils.next(ts, TSTokenId.TS_OPERATOR, "=");
		TSLexerTestUtils.next(ts, TSTokenId.WHITESPACE, " ");
		TSLexerTestUtils.next(ts, TSTokenId.TS_VALUE, "something");
	}
	
	@Test
	public void testExampleFile() throws Exception {
		TokenSequence<?> ts = TSLexerTestUtils.seqForText(TSLexerTestUtils.getFileContent(new File(TSScannerTest.class.getResource("/net/dfranek/typoscript/lexer/TSExample").toURI())), TSTokenId.getLanguage());
		TSLexerTestUtils.next(ts, TSTokenId.TS_PROPERTY, "headerimg");
		TSLexerTestUtils.next(ts, TSTokenId.TS_OPERATOR, ".");
		TSLexerTestUtils.next(ts, TSTokenId.TS_RESERVED, "file");
		TSLexerTestUtils.next(ts, TSTokenId.WHITESPACE, " ");
		TSLexerTestUtils.next(ts, TSTokenId.TS_CURLY_OPEN, "{");
		TSLexerTestUtils.next(ts, TSTokenId.TS_NL, "\n");
		TSLexerTestUtils.next(ts, TSTokenId.WHITESPACE, "\t");
		TSLexerTestUtils.next(ts, TSTokenId.TS_KEYWORD3, "XY");
		TSLexerTestUtils.next(ts, TSTokenId.WHITESPACE, " ");
		TSLexerTestUtils.next(ts, TSTokenId.TS_OPERATOR, "=");
		TSLexerTestUtils.next(ts, TSTokenId.WHITESPACE, " ");
		TSLexerTestUtils.next(ts, TSTokenId.TS_OPERATOR, "[");
		TSLexerTestUtils.next(ts, TSTokenId.TS_NUMBER, "10");
		TSLexerTestUtils.next(ts, TSTokenId.TS_OPERATOR, ".");
		TSLexerTestUtils.next(ts, TSTokenId.TS_VALUE, "w");
		TSLexerTestUtils.next(ts, TSTokenId.TS_OPERATOR, "]");
		TSLexerTestUtils.next(ts, TSTokenId.TS_OPERATOR, "+");
		TSLexerTestUtils.next(ts, TSTokenId.TS_NUMBER, "2");
		TSLexerTestUtils.next(ts, TSTokenId.TS_OPERATOR, ",");
		TSLexerTestUtils.next(ts, TSTokenId.TS_NUMBER, "20");
		TSLexerTestUtils.next(ts, TSTokenId.TS_NL, "\n");
		TSLexerTestUtils.next(ts, TSTokenId.TS_CURLY_CLOSE, "}");
		TSLexerTestUtils.next(ts, TSTokenId.TS_NL, "\n");
		TSLexerTestUtils.next(ts, TSTokenId.TS_NL, "\n");
		TSLexerTestUtils.next(ts, TSTokenId.TS_CONDITION, "[globalVar=TSFE:type = 0]");
		TSLexerTestUtils.next(ts, TSTokenId.TS_NL, "\n");
		TSLexerTestUtils.next(ts, TSTokenId.WHITESPACE, "\t");
		TSLexerTestUtils.next(ts, TSTokenId.TS_KEYWORD2, "page");
		TSLexerTestUtils.next(ts, TSTokenId.TS_OPERATOR, ".");
		TSLexerTestUtils.next(ts, TSTokenId.TS_RESERVED, "typeNum");
		TSLexerTestUtils.next(ts, TSTokenId.WHITESPACE, " ");
		TSLexerTestUtils.next(ts, TSTokenId.TS_OPERATOR, "=");
		TSLexerTestUtils.next(ts, TSTokenId.WHITESPACE, " ");
		TSLexerTestUtils.next(ts, TSTokenId.TS_NUMBER, "0");
		TSLexerTestUtils.next(ts, TSTokenId.TS_NL, "\n");
		TSLexerTestUtils.next(ts, TSTokenId.TS_CONDITION, "[global]");
		TSLexerTestUtils.next(ts, TSTokenId.TS_NL, "\n");
		TSLexerTestUtils.next(ts, TSTokenId.TS_NL, "\n");
		TSLexerTestUtils.next(ts, TSTokenId.TS_KEYWORD2, "page");
		TSLexerTestUtils.next(ts, TSTokenId.TS_OPERATOR, ".");
		TSLexerTestUtils.next(ts, TSTokenId.TS_NUMBER, "10");
		TSLexerTestUtils.next(ts, TSTokenId.TS_OPERATOR, ".");
		TSLexerTestUtils.next(ts, TSTokenId.TS_RESERVED, "value");
		TSLexerTestUtils.next(ts, TSTokenId.WHITESPACE, " ");
		TSLexerTestUtils.next(ts, TSTokenId.TS_PARANTHESE, "(");
		TSLexerTestUtils.next(ts, TSTokenId.TS_VALUE, "\n\tMulti\n\tLine\n\tValue\n");
		TSLexerTestUtils.next(ts, TSTokenId.TS_PARANTHESE, ")");
	}
	
	@Test
	public void testEqualsInValue() {
		TokenSequence<?> ts = TSLexerTestUtils.seqForText("page.10.marks.TEST.select.where = colPos=0\npage.typeNum=0", TSTokenId.getLanguage());
		TSLexerTestUtils.next(ts, TSTokenId.TS_KEYWORD2, "page");
		TSLexerTestUtils.next(ts, TSTokenId.TS_OPERATOR, ".");
		TSLexerTestUtils.next(ts, TSTokenId.TS_NUMBER, "10");
		TSLexerTestUtils.next(ts, TSTokenId.TS_OPERATOR, ".");
		TSLexerTestUtils.next(ts, TSTokenId.TS_KEYWORD2, "marks");
		TSLexerTestUtils.next(ts, TSTokenId.TS_OPERATOR, ".");
		TSLexerTestUtils.next(ts, TSTokenId.TS_PROPERTY, "TEST");
		TSLexerTestUtils.next(ts, TSTokenId.TS_OPERATOR, ".");
		TSLexerTestUtils.next(ts, TSTokenId.TS_FUNCTION, "select");
		TSLexerTestUtils.next(ts, TSTokenId.TS_OPERATOR, ".");
		TSLexerTestUtils.next(ts, TSTokenId.TS_RESERVED, "where");
		TSLexerTestUtils.next(ts, TSTokenId.WHITESPACE, " ");
		TSLexerTestUtils.next(ts, TSTokenId.TS_OPERATOR, "=");
		TSLexerTestUtils.next(ts, TSTokenId.WHITESPACE, " ");
		TSLexerTestUtils.next(ts, TSTokenId.TS_VALUE, "colPos");
		TSLexerTestUtils.next(ts, TSTokenId.TS_OPERATOR, "=");
		TSLexerTestUtils.next(ts, TSTokenId.TS_NUMBER, "0");
		TSLexerTestUtils.next(ts, TSTokenId.TS_NL, "\n");
		TSLexerTestUtils.next(ts, TSTokenId.TS_KEYWORD2, "page");
		TSLexerTestUtils.next(ts, TSTokenId.TS_OPERATOR, ".");
		TSLexerTestUtils.next(ts, TSTokenId.TS_RESERVED, "typeNum");
		TSLexerTestUtils.next(ts, TSTokenId.TS_OPERATOR, "=");
		TSLexerTestUtils.next(ts, TSTokenId.TS_NUMBER, "0");
	}
	
	public void testOpeningCurly() {
		TokenSequence<?> ts = TSLexerTestUtils.seqForText("page{", TSTokenId.getLanguage());
		TSLexerTestUtils.next(ts, TSTokenId.TS_KEYWORD2, "page");
		TSLexerTestUtils.next(ts, TSTokenId.TS_CURLY_OPEN, "{");
	}
	
	public void testConstantAndComment() {
		TokenSequence<?> ts = TSLexerTestUtils.seqForText("shortcutIcon = {$filepaths.templates.images}favicon.icon # But doesn't work ofcourse", TSTokenId.getLanguage());
		TSLexerTestUtils.next(ts, TSTokenId.TS_RESERVED, "shortcutIcon");
		TSLexerTestUtils.next(ts, TSTokenId.WHITESPACE, " ");
		TSLexerTestUtils.next(ts, TSTokenId.TS_OPERATOR, "=");
		TSLexerTestUtils.next(ts, TSTokenId.WHITESPACE, " ");
		TSLexerTestUtils.next(ts, TSTokenId.TS_CONSTANT, "{$filepaths.templates.images}");
		TSLexerTestUtils.next(ts, TSTokenId.TS_VALUE, "favicon");
		TSLexerTestUtils.next(ts, TSTokenId.TS_OPERATOR, ".");
		TSLexerTestUtils.next(ts, TSTokenId.TS_RESERVED, "icon");
	}
	
}
