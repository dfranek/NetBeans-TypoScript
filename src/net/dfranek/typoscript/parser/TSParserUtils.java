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

import java.util.List;
import javax.swing.text.BadLocationException;
import net.dfranek.typoscript.lexer.TSLexerUtils;
import net.dfranek.typoscript.lexer.TSTokenId;
import net.dfranek.typoscript.parser.ast.TSASTNode;
import org.netbeans.api.lexer.Token;
import org.netbeans.api.lexer.TokenSequence;
import org.netbeans.editor.BaseDocument;
import org.netbeans.editor.Utilities;

/**
 *
 * @author Daniel Franek
 */
public class TSParserUtils {

	public static int findHierarchyStart(TokenSequence<TSTokenId> ts, int caretOffset) {
		ts.move(caretOffset);
		ts.moveNext();
		ts.movePrevious();
		Token<TSTokenId> t = ts.token();
		// causes an error if care is at the end of a token -> move back
		if (ts.offset() + t.length() == caretOffset && !t.id().equals(TSTokenId.TS_NL)) {
			ts.movePrevious();
			t = ts.token();
		}
		while (!TSLexerUtils.tokenIsKeyword(t.id()) && !t.id().equals(TSTokenId.TS_NL) && ts.movePrevious()) {
			t = ts.token();
		}

		if (t.id().equals(TSTokenId.TS_NL)) {
			int balance = 0;
			ts.movePrevious();

			do {
				if (balance > 0 && t.id().equals(TSTokenId.TS_CURLY_OPEN)) {
					balance--;
				}
				t = ts.token();
				if (t.id().equals(TSTokenId.TS_CURLY_CLOSE)) {
					balance++;
				}
			} while (ts.movePrevious() && (!t.id().equals(TSTokenId.TS_CURLY_OPEN) || balance != 0));

			t = ts.token();
			while (t.id().equals(TSTokenId.WHITESPACE) && ts.movePrevious()) {
				t = ts.token();
			}
		}
		return ts.offset();
	}

	public static List<Token<TSTokenId>> getCurrentHierarchy(int caretOffset, BaseDocument doc, TokenSequence<TSTokenId> ts, TSASTNode tree, List<Token<TSTokenId>> h) throws BadLocationException {
		if (caretOffset == 0) {
			return h;
		}
		int lineStart = Utilities.getRowFirstNonWhite(doc, caretOffset);
		ts.move(caretOffset);
		ts.moveNext();
		Token<TSTokenId> token = ts.token();
		if (token == null) {
			ts.movePrevious();
			token = ts.token();
		}

		if (token.text().toString().equals(".")) {
			ts.movePrevious();
			token = ts.token();
		}

		if (TSLexerUtils.tokenIsKeyword(token.id())) {
			h.add(token);
		}
		while (ts.offset() > lineStart) {
			ts.movePrevious();
			token = ts.token();
			if (token.id().equals(TSTokenId.TS_OPERATOR) && token.text().toString().equals("<")) {
				return h;
			}
			if (TSLexerUtils.tokenIsKeyword(token.id())) {
				h.add(token);
			}
		}
		if (tree.getChild(token.text().toString()) != null && getCurlyBalance(ts) == 0) {
			return h;
		} else {
			ts.movePrevious();
			do {
				token = ts.token();
			} while (!token.id().equals(TSTokenId.TS_CURLY_OPEN) && ts.movePrevious());
			ts.movePrevious();
			token = ts.token();
			while (token.id().equals(TSTokenId.WHITESPACE) && ts.movePrevious()) {
				token = ts.token();
			}

			if (tree.getChild(token.text().toString()) != null && getCurlyBalance(ts) == 0) {
				h.add(token);
				return h;
			} else {
				return getCurrentHierarchy(ts.offset(), doc, ts, tree, h);
			}
		}
	}

	private static int getCurlyBalance(TokenSequence<TSTokenId> ts) {
		int balance = 0;
		int curOffset = ts.offset();
		Token<TSTokenId> t;
		while (ts.movePrevious()) {
			t = ts.token();
			if (t.id().equals(TSTokenId.TS_CURLY_OPEN)) {
				balance--;
			}
			if (t.id().equals(TSTokenId.TS_CURLY_CLOSE)) {
				balance++;
			}
		}
		ts.move(curOffset);
		ts.movePrevious();

		return balance;
	}
}
