/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2013 Daniel Franek.
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
package net.dfranek.typoscript.typinghooks;

import java.util.Arrays;
import java.util.List;
import javax.swing.text.BadLocationException;
import net.dfranek.typoscript.TSLanguage;
import net.dfranek.typoscript.lexer.TSLexerUtils;
import net.dfranek.typoscript.lexer.TSTokenId;
import org.netbeans.api.editor.mimelookup.MimePath;
import org.netbeans.api.editor.mimelookup.MimeRegistration;
import org.netbeans.api.lexer.Token;
import org.netbeans.api.lexer.TokenId;
import org.netbeans.api.lexer.TokenSequence;
import org.netbeans.editor.BaseDocument;
import org.netbeans.editor.Utilities;
import org.netbeans.modules.csl.api.OffsetRange;
import org.netbeans.modules.editor.indent.api.IndentUtils;
import org.netbeans.spi.editor.typinghooks.TypedBreakInterceptor;

/**
 *
 * @author Daniel Franek <daniel@dfranek.net>
 */
public class TSTypedBreakInterceptor implements TypedBreakInterceptor {

	@Override
	public boolean beforeInsert(Context cntxt) throws BadLocationException {
		return false;
	}

	@Override
	public void insert(MutableContext context) throws BadLocationException {
		final BaseDocument doc = (BaseDocument) context.getDocument();
		int offset = context.getCaretOffset();
		int lineBegin = Utilities.getRowStart(doc, offset);
		int lineEnd = Utilities.getRowEnd(doc, offset);
		if (lineBegin == offset && lineEnd == offset) {
			// Pressed return on a blank newline - do nothing
			return;
		}
		TokenSequence<? extends TSTokenId> ts = TSLexerUtils.getTSTokenSequence(doc, offset);
		if (ts == null) {
			return;
		}
		ts.move(offset);
		if (!ts.moveNext() && !ts.movePrevious()) {
			return;
		}
		Token<? extends TSTokenId> token = ts.token();
		TokenId id = token.id();
		
		// Insert an end statement? Insert a } marker?
		int[] startOfContext = new int[1];
		TSTokenId completeIn = findContextForEnd(ts, offset, startOfContext);
		boolean insert = completeIn != null && isEndMissing(doc, offset, completeIn);
		if (insert) {
			int indent = IndentUtils.lineIndent(doc, IndentUtils.lineStartOffset(doc, startOfContext[0]));
			int afterLastNonWhite = Utilities.getRowLastNonWhite(doc, offset);
			
			int newIndent = ((indent / IndentUtils.tabSize(doc)) + 1) * IndentUtils.tabSize(doc);
			
			StringBuilder sb = new StringBuilder("\n");
			String restOfLine = doc.getText(offset, Utilities.getRowEnd(doc, afterLastNonWhite) - offset);
			sb.append(restOfLine);
			sb.append(IndentUtils.createIndentString(doc, newIndent));
			sb.append("\n"); //NOI18N
			sb.append(IndentUtils.createIndentString(doc, indent));
			doc.remove(offset, restOfLine.length());
			if(completeIn == TSTokenId.TS_CURLY_OPEN) {
				sb.append('}');
			}
			if(completeIn == TSTokenId.TS_PARANTHESE_OPEN) {
				sb.append(')');
			}
			
			context.setText(sb.toString(), 0, IndentUtils.createIndentString(doc, newIndent).length()+1);
		}
	}

	private TSTokenId findContextForEnd(TokenSequence<? extends TSTokenId> ts, int offset, int[] startOfContext) {
		if (ts == null) {
			return null;
		}
		if (ts.offset() != offset) {
			ts.move(offset);

			if (!ts.moveNext() && !ts.movePrevious()) {
				return null;
			}
		}
		TSTokenId result = null;

		// at fist there should be find a bracket  '{' or column ':'
		Token<? extends TSTokenId> bracketColumnToken = TSLexerUtils.findPrevious(ts,
				Arrays.asList(TSTokenId.TS_COMMENT, TSTokenId.WHITESPACE, TSTokenId.TS_NL, TSTokenId.TS_VALUE));
		if (bracketColumnToken != null
				&& (bracketColumnToken.id() == TSTokenId.TS_CURLY_OPEN ||
					bracketColumnToken.id() == TSTokenId.TS_PARANTHESE_OPEN
					)) {
			startOfContext[0] = ts.offset();
			// we are interested only in adding end for { or alternative syntax :
			List<TSTokenId> lookFor = Arrays.asList(TSTokenId.TS_CURLY_CLOSE, TSTokenId.TS_CURLY_OPEN, TSTokenId.TS_PARANTHESE_CLOSE, TSTokenId.TS_PARANTHESE_OPEN);
			Token<? extends TSTokenId> keyToken = TSLexerUtils.findPreviousToken(ts, lookFor);

			if (bracketColumnToken.id() == TSTokenId.TS_CURLY_OPEN) {
				result = TSTokenId.TS_CURLY_OPEN;
			}

			if (bracketColumnToken.id() == TSTokenId.TS_PARANTHESE_OPEN) {
				result = TSTokenId.TS_PARANTHESE_OPEN;
			}
			
			if (keyToken.id() != TSTokenId.TS_CURLY_CLOSE && keyToken.id() != TSTokenId.TS_PARANTHESE_CLOSE) {
				startOfContext[0] = ts.offset();
			}
		}
		ts.move(offset);
		if (!ts.moveNext() && !ts.movePrevious()) {
			return null;
		}
		return result;
	}

	private boolean isEndMissing(BaseDocument doc, int offset, TSTokenId startTokenId) throws BadLocationException {
		TokenSequence<? extends TSTokenId> ts = TSLexerUtils.getTSTokenSequence(doc, offset);
		if (ts == null) {
			return false;
		}
		ts.move(0);
		if (!ts.moveNext() && !ts.movePrevious()) {
			return false;
		}
		Token<? extends TSTokenId> token;
		int curlyBalance = 0;
		boolean curlyProcessed = false;
		if (startTokenId == TSTokenId.TS_CURLY_OPEN) {
			do {
				token = ts.token();
				if (token.id() == TSTokenId.TS_CURLY_CLOSE) {
					curlyBalance--;
					curlyProcessed = true;
				} else if (token.id() == TSTokenId.TS_CURLY_OPEN) { //NOI18N
					curlyBalance++;
					curlyProcessed = true;
				}

				if (curlyBalance == 0 && curlyProcessed && ts.offset() > offset) {
					break;
				}
			} while (ts.moveNext());
			return curlyBalance > 0;
		}
		
		ts.move(offset);
		if (startTokenId == TSTokenId.TS_PARANTHESE_OPEN) {
			OffsetRange r = TSLexerUtils.findFwd(ts, TSTokenId.TS_PARANTHESE_OPEN, '(', TSTokenId.TS_PARANTHESE_CLOSE, ')');
			if (r == OffsetRange.NONE) {
				return true;
			}
		}
		
		return false;
	}

	@Override
	public void afterInsert(Context cntxt) throws BadLocationException {
		// deliberatly empty
	}

	@Override
	public void cancelled(Context cntxt) {
		// deliberatly empty
	}
	
	@MimeRegistration(mimeType = TSLanguage.TS_MIME_TYPE, service = TypedBreakInterceptor.Factory.class)
    public static class PhpFactory implements TypedBreakInterceptor.Factory {

        @Override
        public TypedBreakInterceptor createTypedBreakInterceptor(MimePath mimePath) {
            return new TSTypedBreakInterceptor();
        }
    }

}
