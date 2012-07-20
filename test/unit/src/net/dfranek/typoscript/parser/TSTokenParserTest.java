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
package net.dfranek.typoscript.parser;

import java.io.File;
import java.net.URISyntaxException;
import junit.framework.TestCase;
import net.dfranek.typoscript.lexer.TSLexerTestUtils;
import net.dfranek.typoscript.lexer.TSScannerTest;
import net.dfranek.typoscript.lexer.TSTokenId;
import net.dfranek.typoscript.parser.ast.TSASTNode;
import net.dfranek.typoscript.parser.ast.TSASTNodeType;
import org.junit.*;
import static org.junit.Assert.*;
import org.netbeans.api.lexer.TokenSequence;
import org.netbeans.junit.NbTestCase;

/**
 *
 * @author Daniel
 */
public class TSTokenParserTest extends TestCase {

	public TSTokenParserTest(String name) {
		super(name);
	}

	public void testParser() throws URISyntaxException, Exception {
		TokenSequence<TSTokenId> ts = (TokenSequence<TSTokenId>) TSLexerTestUtils.seqForText(TSLexerTestUtils.getFileContent(new File(TSTokenParserTest.class.getResource("/net/dfranek/typoscript/parser/test.ts").toURI())), TSTokenId.getLanguage());
		TSTokenParser tp = new TSTokenParser(ts);
		TSASTNode tree = tp.buildTree();
		TSParserTestUtils.assertNodeEquals(tree.getChild("seite"), "seite", TSASTNodeType.PAGE, "", 7);
		TSParserTestUtils.assertNodeEquals(tree.getChild("seite").getChild("headerData"), "headerData", TSASTNodeType.UNKNOWN, "", 1);
		TSParserTestUtils.assertNodeEquals(tree.getChild("seite").getChild("headerData").getChild("10"), "10", TSASTNodeType.UNKNOWN, "", 1);
		TSParserTestUtils.assertNodeEquals(tree.getChild("seite").getChild("headerData").getChild("10").getChild("value"), "value", TSASTNodeType.VALUE, "test", 0);
		TSParserTestUtils.assertNodeEquals(tree.getChild("seite").getChild("config"), "config", TSASTNodeType.UNKNOWN, "", 1);
		TSParserTestUtils.assertNodeEquals(tree.getChild("seite").getChild("config").getChild("test"), "test", TSASTNodeType.VALUE, "129", 0);
		TSParserTestUtils.assertNodeEquals(tree.getChild("seite").getChild("test2"), "test2", TSASTNodeType.VALUE, "[10.w] + 16,19", 0);
		TSParserTestUtils.assertNodeEquals(tree.getChild("seite").getChild("test3"), "test3", TSASTNodeType.UNKNOWN, "", 1);
		TSParserTestUtils.assertNodeEquals(tree.getChild("seite").getChild("test3").getChild("test"), "test", TSASTNodeType.VALUE, "4", 0);
		TSParserTestUtils.assertNodeEquals(tree.getChild("seite").getChild("test4"), "test4", TSASTNodeType.VALUE, "3", 0);
		TSParserTestUtils.assertNodeEquals(tree.getChild("seite").getChild("test8"), "test8", TSASTNodeType.COPIED_PROPERTY, "seite.config.test", 0);
		TSParserTestUtils.assertNodeEquals(tree.getChild("seite").getChild("test5"), "test5", TSASTNodeType.VALUE, "asdasdasdasdasdsa", 0);

	}
}
