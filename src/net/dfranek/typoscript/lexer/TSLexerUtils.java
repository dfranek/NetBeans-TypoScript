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

import com.google.gson.Gson;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.text.Document;
import org.netbeans.api.lexer.Token;
import org.netbeans.api.lexer.TokenHierarchy;
import org.netbeans.api.lexer.TokenId;
import org.netbeans.api.lexer.TokenSequence;
import org.netbeans.modules.csl.api.OffsetRange;

/**
 *
 * @author Daniel Franek
 */
public class TSLexerUtils {

	protected static HashMap<String, Collection<String>> completionList = new HashMap<>();
	protected static HashMap<String, String> keywords;

	
	@SuppressWarnings("unchecked")
	public static TokenSequence<TSTokenId> getTSTokenSequence(TokenHierarchy<?> th, int offset) {
		TokenSequence<TSTokenId> ts = th == null ? null : th.tokenSequence(TSTokenId.getLanguage());
		if (ts == null) {
			// Possibly an embedding scenario such as an RHTML file
			// First try with backward bias true
			List<TokenSequence<?>> list = th.embeddedTokenSequences(offset, true);
			for (TokenSequence<?> t : list) {
				if (t.language() == TSTokenId.getLanguage()) {
					ts = (TokenSequence<TSTokenId>) t;
					break;
				}
			}
			if (ts == null) {
				list = th.embeddedTokenSequences(offset, false);
				for (TokenSequence<?> t : list) {
					if (t.language() == TSTokenId.getLanguage()) {
						ts = (TokenSequence<TSTokenId>) t;
						break;
					}
				}
			}
		}
		return ts;
	}

	@SuppressWarnings("unchecked")
	public static TokenSequence<TSTokenId> getTSTokenSequence(Document doc, int offset) {
		TokenHierarchy<Document> th = TokenHierarchy.get(doc);
		return getTSTokenSequence(th, offset);
	}

	public static boolean textEquals(CharSequence text1, char... text2) {
		int len = text1.length();
		if (len == text2.length) {
			for (int i = len - 1; i >= 0; i--) {
				if (text1.charAt(i) != text2[i]) {
					return false;
				}
			}
			return true;
		}
		return false;
	}

	/**
	 * Search forwards in the token sequence until a token of type
	 * <code>down</code> is found
	 */
	public static OffsetRange findFwd(TokenSequence<? extends TSTokenId> ts, TSTokenId tokenUpId, char up, TSTokenId tokenDownId, char down) {
		int balance = 0;

		while (ts.moveNext()) {
			Token<? extends TSTokenId> token = ts.token();

			if ((token.id() == tokenUpId && textEquals(token.text(), up))/*
					 * || (tokenUpId == TSTokenId.TS_CURLY_OPEN && token.id() ==
					 * TSTokenId.PHP_TOKEN &&
					 * token.text().charAt(token.text().length() - 1) == '{')
					 */) {
				balance++;
			} else if (token.id() == tokenDownId && textEquals(token.text(), down)) {
				if (balance == 0) {
					return new OffsetRange(ts.offset(), ts.offset() + token.length());
				}

				balance--;
			}
		}

		return OffsetRange.NONE;
	}

	/**
	 * Search backwards in the token sequence until a token of type
	 * <code>up</code> is found
	 */
	public static OffsetRange findBwd(TokenSequence<? extends TSTokenId> ts, TSTokenId tokenUpId, char up, TSTokenId tokenDownId, char down) {
		int balance = 0;

		while (ts.movePrevious()) {
			Token<? extends TSTokenId> token = ts.token();
			TokenId id = token.id();

			if (token.id() == tokenUpId && textEquals(token.text(), up)/*
					 * || (tokenUpId == PHPTokenId.PHP_CURLY_OPEN && token.id()
					 * == PHPTokenId.PHP_TOKEN &&
					 * token.text().charAt(token.text().length() - 1) == '{')
					 */) {
				if (balance == 0) {
					return new OffsetRange(ts.offset(), ts.offset() + token.length());
				}

				balance++;
			} else if (token.id() == tokenDownId && textEquals(token.text(), down)) {
				balance--;
			}
		}

		return OffsetRange.NONE;
	}

	public static OffsetRange findNextStartsWith(TokenSequence<? extends TSTokenId> ts, TSTokenId tokenId, String startsWith) {
		while (ts.moveNext()) {
			Token<? extends TSTokenId> token = ts.token();
			TokenId id = token.id();
			if (token.id() == tokenId && token.text().toString().startsWith(startsWith)) {
				return new OffsetRange(ts.offset(), ts.offset() + token.length());
			}
		}
		return OffsetRange.NONE;
	}
	
	
	public static OffsetRange findNextEndsWith(TokenSequence<? extends TSTokenId> ts, TSTokenId tokenId, String endsWith) {
		while (ts.moveNext()) {
			Token<? extends TSTokenId> token = ts.token();
			TokenId id = token.id();
			if (token.id() == tokenId && token.text().toString().endsWith(endsWith)) {
				return new OffsetRange(ts.offset(), ts.offset() + token.length());
			}
		}
		return OffsetRange.NONE;
	}
	
	
	public static Token<? extends TSTokenId> findFwdNonSpace(TokenSequence<? extends TSTokenId> ts) {
		while (ts.moveNext() && ts.token().id().equals(TSTokenId.WHITESPACE)) {}
		return ts.token();
	}
	
	public static Token<? extends TSTokenId> findBwdNonSpace(TokenSequence<? extends TSTokenId> ts) {
		while (ts.movePrevious() && ts.token().id().equals(TSTokenId.WHITESPACE)) {}
		return ts.token();
	}

	public static String getWordFromXML(String word) {
		String propertyType = "";
		if(keywords.containsKey(word)) {
			propertyType = keywords.get(word);
		}

		return propertyType;
	}
	
	public static Collection<String> getAllKeywordsOfType(String type) {
		if(completionList.containsKey(type)) {
			return completionList.get(type);
		}
		Collection<String> properties = new ArrayList<>();
		for (Map.Entry<String, String> entry : keywords.entrySet()) {
			String property = entry.getKey();
			if (entry.getValue().equals(type)) {
				properties.add(property);
			}
		}
		completionList.put(type, properties);

		return properties;
	}

	@SuppressWarnings("unchecked")
	static void initKeywords() {
		Gson gson = new Gson();
		keywords = gson.fromJson(new InputStreamReader(TSLexerUtils.class.getResourceAsStream("/net/dfranek/typoscript/resources/properties.json")), HashMap.class);		
	}
	
	public static boolean tokenIsKeyword(TSTokenId id) {
		return id.equals(TSTokenId.TS_OBJECT) || id.equals(TSTokenId.TS_EXTENSION) || id.equals(TSTokenId.TS_PROPERTY) || id.equals(TSTokenId.TS_NUMBER) || id.equals(TSTokenId.TS_KEYWORD) || id.equals(TSTokenId.TS_RESERVED) || id.equals(TSTokenId.TS_FUNCTION);
	}
	
	public static Token<?extends TSTokenId> findPrevious(TokenSequence<?extends TSTokenId> ts, List<TSTokenId> ignores) {
        if (ignores.contains(ts.token().id())) {
            while (ts.movePrevious() && ignores.contains(ts.token().id())) {
            }
        }
        return ts.token();
    }
	
	public static Token<?extends TSTokenId> findPreviousToken(TokenSequence<?extends TSTokenId> ts, List<TSTokenId> lookfor) {
        if (!lookfor.contains(ts.token().id())) {
            while (ts.movePrevious() && !lookfor.contains(ts.token().id())) {
            }
        }
        return ts.token();
    }
	
}
