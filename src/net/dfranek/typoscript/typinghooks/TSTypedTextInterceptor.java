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

import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import net.dfranek.typoscript.TSLanguage;
import net.dfranek.typoscript.lexer.TSLexerUtils;
import net.dfranek.typoscript.lexer.TSTokenId;
import org.netbeans.api.editor.mimelookup.MimePath;
import org.netbeans.api.editor.mimelookup.MimeRegistration;
import org.netbeans.api.lexer.Token;
import org.netbeans.api.lexer.TokenId;
import org.netbeans.api.lexer.TokenSequence;
import org.netbeans.editor.BaseDocument;
import org.netbeans.spi.editor.typinghooks.TypedTextInterceptor;

/**
 *
 * @author Daniel Franek <daniel@dfranek.net>
 */
public class TSTypedTextInterceptor implements TypedTextInterceptor {

	@Override
	public boolean beforeInsert(Context cntxt) throws BadLocationException {
		return false;
	}

	@Override
	public void insert(MutableContext context) throws BadLocationException {
		Document document = context.getDocument();
		BaseDocument doc = (BaseDocument) document;
		int caretOffset = context.getOffset();
		char ch = context.getText().charAt(0);
		if (doNotAutoComplete(ch) || caretOffset == 0) {
			return;
		}
		String selection = context.getReplacedText();
		if (selection != null && selection.length() > 0) {
			if ((ch == '[' || ch == '{')) {
				char firstChar = selection.charAt(0);
				if (firstChar != ch) {

					int lastChar = selection.charAt(selection.length() - 1);
					// Replace the surround-with chars?
					if (selection.length() > 1
							&& ((firstChar == '"' || firstChar == '\'' || firstChar == '('
							|| firstChar == '{' || firstChar == '[')
							&& lastChar == matching(firstChar))) {
						String innerText = selection.substring(1, selection.length() - 1);
						String text = Character.toString(ch) + innerText + Character.toString(matching(ch));
						context.setText(text, text.length());
					} else {
						String text = ch + selection + matching(ch);
						context.setText(text, text.length());
					}
				}
			}
		}
		if (ch == '[') {
			completeOpeningBracket(context, ch);
		}
	}

	private boolean doNotAutoComplete(final char ch) {
		return (ch != '[' && ch != '{');
	}

	/**
	 * Returns for an opening bracket or quote the appropriate closing
	 * character.
	 */
	private char matching(char bracket) {
		switch (bracket) {
			case '(':
				return ')';
			case '/':
				return '/';
			case '[':
				return ']';
			case '\"':
				return '\"'; // NOI18N
			case '\'':
				return '\'';
			case '{':
				return '}';
			case '}':
				return '{';
			default:
				return bracket;
		}
	}

	/**
	 * Check for various conditions and possibly add a pairing bracket to the
	 * already inserted.
	 *
	 * @param doc the document
	 * @param dotPos position of the opening bracket (already in the doc)
	 * @param caret caret
	 * @param bracket the bracket that was inserted
	 */
	private void completeOpeningBracket(MutableContext context, char bracket) throws BadLocationException {
		if(completeBracket(context)) {
			StringBuilder sb = new StringBuilder();
			sb.append(bracket);
			sb.append(matching(bracket));
			context.setText(sb.toString(), 1);
		}
	}
	
	private boolean completeBracket(MutableContext context) {
		int offset = context.getOffset();
		final BaseDocument doc = (BaseDocument) context.getDocument();
		TokenSequence<? extends TSTokenId> ts = TSLexerUtils.getTSTokenSequence(doc, offset);
		if (ts == null) {
			return true;
		}
		ts.move(offset);
		if (!ts.moveNext() && !ts.movePrevious()) {
			return true;
		}
		Token<? extends TSTokenId> token = ts.token();
		TokenId id = token.id();
		
		return id != TSTokenId.TS_CURLY_OPEN;
	}

	@Override
	public void afterInsert(Context cntxt) throws BadLocationException {
		// deliberatly empty
	}

	@Override
	public void cancelled(Context cntxt) {
		// deliberatly empty
	}

	@MimeRegistration(mimeType = TSLanguage.TS_MIME_TYPE, service = TypedTextInterceptor.Factory.class)
	public static class Factory implements TypedTextInterceptor.Factory {

		@Override
		public TypedTextInterceptor createTypedTextInterceptor(MimePath mimePath) {
			return new TSTypedTextInterceptor();
		}
	}

}
