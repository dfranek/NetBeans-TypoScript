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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;
import net.dfranek.typoscript.completion.tsref.TSRef;
import net.dfranek.typoscript.completion.tsref.TSRefProperty;
import net.dfranek.typoscript.completion.tsref.TSRefType;
import net.dfranek.typoscript.lexer.TSLexerUtils;
import net.dfranek.typoscript.lexer.TSTokenId;
import net.dfranek.typoscript.parser.TSParserResult;
import net.dfranek.typoscript.parser.TSParserUtils;
import net.dfranek.typoscript.parser.ast.TSASTNode;
import net.dfranek.typoscript.parser.ast.TSASTNodeType;
import org.netbeans.api.lexer.Token;
import org.netbeans.api.lexer.TokenHierarchy;
import org.netbeans.api.lexer.TokenSequence;
import org.netbeans.editor.BaseDocument;
import org.netbeans.editor.Utilities;
import org.netbeans.modules.csl.api.CodeCompletionContext;
import org.netbeans.modules.csl.api.CodeCompletionHandler;
import org.netbeans.modules.csl.api.CodeCompletionResult;
import org.netbeans.modules.csl.api.CompletionProposal;
import org.netbeans.modules.csl.api.ElementHandle;
import org.netbeans.modules.csl.api.ElementKind;
import org.netbeans.modules.csl.api.ParameterInfo;
import org.netbeans.modules.csl.spi.ParserResult;
import org.netbeans.modules.parsing.spi.indexing.support.QuerySupport;
import org.netbeans.modules.parsing.spi.indexing.support.QuerySupport.Kind;
import org.openide.util.Exceptions;

/**
 *
 * @author Daniel Franek
 */
public class TSCodeCompletion implements CodeCompletionHandler {

	private final static Collection<Character> AUTOPOPUP_STOP_CHARS = new TreeSet<>(
			Arrays.asList('=', ';', '+', '-', '*', '/', '%', '(', ')', '[', ']', '{', '}', '?', '\n'));
	private boolean caseSensitive;
	private Kind nameKind;

	@Override
	public CodeCompletionResult complete(CodeCompletionContext context) {
		TSRef.initTSRef();
		TSParserResult result = (TSParserResult) context.getParserResult();
		TSASTNode tree = result.getTree();

		List<Token<TSTokenId>> h = new ArrayList<>();

		try {
			BaseDocument doc = (BaseDocument) result.getSnapshot().getSource().getDocument(false);
			if (doc == null) {
				return CodeCompletionResult.NONE;
			}
			final TSCompletionResult completionResult = new TSCompletionResult(context);
			completionResult.setFilterable(true);
			int caretOffset = context.getCaretOffset();

			int lineBegin = Utilities.getRowStart(doc, caretOffset);

			this.caseSensitive = context.isCaseSensitive();
			this.nameKind = caseSensitive ? QuerySupport.Kind.PREFIX : QuerySupport.Kind.CASE_INSENSITIVE_PREFIX;

			if (lineBegin != -1) {
				TokenHierarchy<?> th = result.getSnapshot().getTokenHierarchy();
				TokenSequence<TSTokenId> ts = th.tokenSequence(TSTokenId.getLanguage());

				TSParserUtils.getCurrentHierarchy(TSParserUtils.findHierarchyStart(ts, caretOffset), doc, ts, tree, h);
				Collections.reverse(h);
				TSASTNode currTree = tree;
				for (Token<TSTokenId> token : h) {
					if (currTree.hasChild(token.text().toString())) {
						currTree = currTree.getChild(token.text().toString());
					}
				}

				TSRefType help = TSRef.getHelpForType(currTree.getType().getTypeString());
				List<String> addedItems = new ArrayList<>();

				for (TSASTNode node : currTree.getChildren()) {
					if (node.getType() != TSASTNodeType.CONDITION) {
						addedItems.add(node.getName());
						if (help != null) {
							TSRefProperty p = help.getProperty(node.getName());
							String documentation = "";
							if (p != null) {
								documentation = p.getDescription();
							}
							completionResult.add(new TSCompletionItem(context.getCaretOffset(), node.getName(), ElementKind.PROPERTY, context.getPrefix(), true, currTree.getName(), documentation));
						} else {
							completionResult.add(new TSCompletionItem(context.getCaretOffset(), node.getName(), ElementKind.PROPERTY, context.getPrefix(), true, currTree.getName()));
						}
					}
				}

				if (help != null) {
					HashMap<String, TSRefProperty> properties = help.getProperties();
					for (Map.Entry<String, TSRefProperty> entry : properties.entrySet()) {
						String name = entry.getKey();
						TSRefProperty property = entry.getValue();

						if (!addedItems.contains(name)) {
							completionResult.add(new TSCompletionItem(context.getCaretOffset(), name, ElementKind.PROPERTY, context.getPrefix(), false, currTree.getType().toString(), property.getDescription()));
						}
					}
				} else {
					int lineEnd = Utilities.getRowEnd(doc, caretOffset);
					int lineOffset = caretOffset - lineBegin;
					String line = doc.getText(lineBegin, lineEnd - lineBegin);
					if (line.lastIndexOf("=", lineOffset) != -1) {
						addKeywords(completionResult, context);
					} else {
						addReservedWords(completionResult, context);
					}
				}
			}


			return completionResult;
		} catch (BadLocationException ex) {
			Exceptions.printStackTrace(ex);
		}
		return CodeCompletionResult.NONE;
	}

	private void addKeywords(TSCompletionResult result, CodeCompletionContext context) {
		Collection<String> keywords = TSLexerUtils.getAllKeywordsOfType("keywords");
		for (String word : keywords) {
			if (word.toLowerCase().startsWith(context.getPrefix().toLowerCase())) {
				result.add(new TSCompletionItem(context.getCaretOffset(), word, ElementKind.KEYWORD, context.getPrefix(), false));
			}
		}
	}

	private void addReservedWords(TSCompletionResult result, CodeCompletionContext context) {
		Collection<String> reservedWords = TSLexerUtils.getAllKeywordsOfType("reserved");
		for (String word : reservedWords) {
			if (word.toLowerCase().startsWith(context.getPrefix().toLowerCase())) {
				result.add(new TSCompletionItem(context.getCaretOffset(), word, ElementKind.PROPERTY, context.getPrefix(), false));
			}
		}
	}

	@Override
	public String document(ParserResult pr, ElementHandle eh) {
		TSElement tseh = (TSElement) eh;

		String documentation = tseh.getDocumentation();
		if (documentation != null && !documentation.isEmpty()) {
			documentation = "<h3>" + eh.getName() + "</h3>" + documentation;
		} else {
			documentation = null;
		}

		return documentation;
	}

	@Override
	public ElementHandle resolveLink(String string, ElementHandle eh) {
		return null;
	}

	@Override
	public String getPrefix(ParserResult info, int caretOffset, boolean upToOffset) {

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
				if (line.lastIndexOf(".", lineOffset) != -1) {
					start = line.lastIndexOf(".", lineOffset) + 1;
				} else if (line.lastIndexOf("=", lineOffset) != -1) {
					start = line.lastIndexOf("=", lineOffset) + 1;
				}


				String prefix;
				if (upToOffset) {
					try {
						prefix = line.substring(start, lineOffset).trim();
					} catch (StringIndexOutOfBoundsException e) {
						return null;
					}
				} else {
					if (lineOffset == line.length()) {
						prefix = line.substring(start);
					} else {
						int n = line.length();
						int end = lineOffset;
						for (int j = lineOffset; j < n; j++) {
							char d = line.charAt(j);
							if (!Character.isJavaIdentifierPart(d)) {
								break;
							} else {
								end = j + 1;
							}
						}
						try {
							prefix = line.substring(start, end);
						} catch (StringIndexOutOfBoundsException e) {
							return null;
						}

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
		if (AUTOPOPUP_STOP_CHARS.contains(lastChar)) {
			return QueryType.STOP;
		}

		Document document = component.getDocument();
		int offset = component.getCaretPosition();
		TokenSequence<TSTokenId> ts = TSLexerUtils.getTSTokenSequence(document, offset);
		if (ts == null) {
			return QueryType.STOP;
		}

		Token<TSTokenId> t;
		int diff = ts.move(offset);
		if (diff > 0 && ts.moveNext() || ts.movePrevious()) {
			t = ts.token();
			if (t.id() == TSTokenId.TS_OPERATOR || t.id() == TSTokenId.TS_PROPERTY || t.id() == TSTokenId.TS_KEYWORD || t.id() == TSTokenId.TS_RESERVED) {
				return QueryType.COMPLETION;
			}
		}

		return QueryType.NONE;
	}

	@Override
	@SuppressWarnings("rawtypes")
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
