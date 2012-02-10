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
package net.dfranek.typoscript.completion;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;
import net.dfranek.typoscript.lexer.TSLexerUtils;
import net.dfranek.typoscript.lexer.TSScanner;
import net.dfranek.typoscript.lexer.TSTokenId;
import org.netbeans.api.lexer.Token;
import org.netbeans.api.lexer.TokenSequence;
import org.netbeans.modules.csl.api.CodeCompletionContext;
import org.netbeans.modules.csl.api.CodeCompletionHandler;
import org.netbeans.modules.csl.api.CodeCompletionResult;
import org.netbeans.modules.csl.api.CompletionProposal;
import org.netbeans.modules.csl.api.ElementHandle;
import org.netbeans.modules.csl.api.ParameterInfo;
import org.netbeans.modules.csl.spi.ParserResult;
import org.openide.util.Exceptions;
import org.netbeans.editor.BaseDocument;
import org.netbeans.editor.Utilities;
import org.netbeans.modules.csl.api.ElementKind;

/**
 *
 * @author Daniel Franek
 */
public class TSCodeCompletion implements CodeCompletionHandler {

	private final static Collection<Character> AUTOPOPUP_STOP_CHARS = new TreeSet<Character>(
			Arrays.asList('=', ';', '+', '-', '*', '/', '%', '(', ')', '[', ']', '{', '}', '?','\n'));
	
	
	@Override
	public CodeCompletionResult complete(CodeCompletionContext context) {
		try {
			BaseDocument doc = (BaseDocument) context.getParserResult().getSnapshot().getSource().getDocument(false);
			if (doc == null) {
				return CodeCompletionResult.NONE;
			}
			final TSCompletionResult completionResult = new TSCompletionResult(context);
			int caretOffset = context.getCaretOffset();
			int lineBegin = Utilities.getRowStart(doc, caretOffset);
			if (lineBegin != -1) {
				
				int lineEnd = Utilities.getRowEnd(doc, caretOffset);
				int lineOffset = caretOffset - lineBegin;
				String line = doc.getText(lineBegin, lineEnd - lineBegin);
				if(line.lastIndexOf("=", lineOffset) != -1) {
					addKeywords(completionResult, context);
				} else {
					addReservedWords(completionResult, context);
				}
			}

			return completionResult;
		} catch (BadLocationException ex) {
			Exceptions.printStackTrace(ex);
		}
		return CodeCompletionResult.NONE;
	}
	
	private void addKeywords(TSCompletionResult result, CodeCompletionContext context) {
		for (Iterator<String> it = TSScanner.TSScannerKeyWords.keywords.iterator(); it.hasNext();) {
			String word = it.next();
			if (word.toLowerCase().startsWith(context.getPrefix().toLowerCase())) {
				result.add(new TSCompletionItem(context.getCaretOffset(), word, ElementKind.KEYWORD, context.getPrefix()));
			}
		}
	}
	
	private void addReservedWords(TSCompletionResult result, CodeCompletionContext context) {
		for (Iterator<String> it = TSScanner.TSScannerKeyWords.reservedWord.iterator(); it.hasNext();) {
			String word = it.next();
			if (word.toLowerCase().startsWith(context.getPrefix().toLowerCase())) {
				result.add(new TSCompletionItem(context.getCaretOffset(), word, ElementKind.PROPERTY, context.getPrefix()));
			}
		}
		for (Iterator<String> it = TSScanner.TSScannerKeyWords.keywords2.iterator(); it.hasNext();) {
			String word = it.next();
			if (word.toLowerCase().startsWith(context.getPrefix().toLowerCase())) {
				result.add(new TSCompletionItem(context.getCaretOffset(), word, ElementKind.PROPERTY, context.getPrefix()));
			}
		}
	}

	@Override
	public String document(ParserResult pr, ElementHandle eh) {
		return null;
	}
	

	@Override
	public ElementHandle resolveLink(String string, ElementHandle eh) {
		return null;
	}

	@Override
	public String getPrefix(ParserResult info, int caretOffset, boolean upToOffset) {
		upToOffset = false;
		try {
			BaseDocument doc = (BaseDocument) info.getSnapshot().getSource().getDocument(false);
			if (doc == null) {
				return null;
			}

			int lineBegin = Utilities.getRowStart(doc, caretOffset);
			if (lineBegin != -1) {
				int lineEnd = Utilities.getRowEnd(doc, caretOffset);
				String line = doc.getText(lineBegin, lineEnd - lineBegin);
				int lineOffset = caretOffset - lineBegin;
				int start = 0;
				if(line.lastIndexOf(".", lineOffset) != -1) {
					start = line.lastIndexOf(".", lineOffset)+1;	
				} else if(line.lastIndexOf("=", lineOffset) != -1) {
					start = line.lastIndexOf("=", lineOffset)+1;
				}
				

				String prefix;
				if (upToOffset) {
					prefix = line.substring(start, lineOffset).trim();
				} else {
					if (lineOffset == line.length()) {
						prefix = line.substring(start);
					} else {
						int n = line.length();
						int end = lineOffset;
						for (int j = lineOffset; j < n; j++) {
							char d = line.charAt(j);
							// Try to accept Foo::Bar as well
							if (!Character.isJavaIdentifierPart(d)) {
								break;
							} else {
								end = j + 1;
							}
						}
						prefix = line.substring(start, end);
					}
				}
				return prefix;
			}
		} catch (BadLocationException ex) {
			Exceptions.printStackTrace(ex);
		}
		return null;
	}

	@Override
	public QueryType getAutoQuery(JTextComponent component, String typedText) {
		if (typedText.length() == 0) {
			return QueryType.NONE;
		}

		char lastChar = typedText.charAt(typedText.length() - 1);
		if (AUTOPOPUP_STOP_CHARS.contains(Character.valueOf(lastChar))) {
			return QueryType.STOP;
	}

		Document document = component.getDocument();
		int offset = component.getCaretPosition();
		TokenSequence<TSTokenId> ts = TSLexerUtils.getTSTokenSequence(document, offset);
		if (ts == null) {
			return QueryType.STOP;
		}

		Token<TSTokenId> t = null;
		int diff = ts.move(offset);
		if (diff > 0 && ts.moveNext() || ts.movePrevious()) {
			t = ts.token();
			if (t.id() == TSTokenId.TS_OPERATOR || t.id() == TSTokenId.TS_PROPERTY || t.id() == TSTokenId.TS_KEYWORD || t.id() == TSTokenId.TS_KEYWORD2 || t.id() == TSTokenId.TS_KEYWORD3 || t.id() == TSTokenId.TS_RESERVED) {
				return QueryType.COMPLETION;
			}
		}


		return QueryType.NONE;
	}

	@Override
	public String resolveTemplateVariable(String string, ParserResult pr, int i, String string1, Map map) {
		return null;
	}

	@Override
	public Set<String> getApplicableTemplates(Document dcmnt, int i, int i1) {
		return null;
	}

	@Override
	public ParameterInfo parameters(ParserResult pr, int i, CompletionProposal cp) {
		return ParameterInfo.NONE;
	}
}
