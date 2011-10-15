/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.dfranek.typoscript.lexer;

import org.netbeans.api.lexer.Language;
import org.netbeans.api.lexer.TokenId;

/**
 *
 * @author Daniel
 */
public enum TSTokenId implements TokenId {
	TS_CURLY(null, "brace"),
	TS_COMMENT(null, "comment"),
	TS_VALUE(null, "value"),
	TS_PROPERTY(null, "ts"),
	TS_CONDITION(null, "condition"),
	TS_STRING(null, "string"),
	TS_NUMBER(null, "number"),
	TS_REGEXP(null, "string"),
	TS_OPERATOR(null, "operator"),
	TS_KEYWORD(null,"keyword"),
	TS_KEYWORD2(null,"keyword"),
	TS_KEYWORD3(null,"keyword"),
	TS_RESERVED(null,"reserved"),
	TS_NL(null, "whitespace"),
	WHITESPACE(null, "whitespace"),
	UNKNOWN_TOKEN(null, "error");
	private final String primaryCategory;
	private final String fixedText;

	TSTokenId(String fixedText, String primaryCategory) {
		this.fixedText = fixedText;
		this.primaryCategory = primaryCategory;
	}
	private static final Language<TSTokenId> language = new TSLanguageHierarchy().language();

	public static final Language<TSTokenId> getLanguage() {
		return language;
	}

	@Override
	public String primaryCategory() {
		return primaryCategory;
	}
}
