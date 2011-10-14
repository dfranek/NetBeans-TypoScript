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
	TS_EQUALS (null, "operator"),
	TS_LT(null, "operator"),
	TS_GT(null, "operator"),
	TS_CURLY(null, "brace"),
	TS_PARANTHESE_START(null, "brace"),
	TS_PARANTHESE_END(null, "brace"),
	TS_DOT(null,"operator"),
	TS_COLON_EQUALS(null, "operator"),
	TS_LINE_COMMENT(null, "comment"),
	TS_COMMENT(null, "comment"),
	TS_COMMENT_START(null, "comment"),
	TS_COMMENT_END(null, "comment"),
	TS_VALUE(null, "value"),
	TS_CONDITION_START(null, "condition"),
	TS_CONDITION_END(null, "condition"),
	TS_PROPERTY(null, "ts"),
	TS_CONDITION(null, "condition"),
	TS_END(null, "keyword"),
	TS_GLOBAL(null,"keyword"),
	TS_ELSE(null,"keyword"),
	TS_INCLUDE(null, "keyword"),
	TS_SOURCE(null, "keyword"),
	TS_INCLUDE_START(null, "include"),
	TS_INCLUDE_END(null, "include"),
	TS_PIPE(null, "operator"),
        TS_STRING(null,"string"),
        TS_NUMBER(null,"number"),
        TS_REGEXP(null,"string"),
        TS_OPERATOR(null,"operator"),
        TS_NL(null,"whitespace"),
	WHITESPACE(null, "whitespace"),
	UNKNOWN_TOKEN(null, "error");

    private final String primaryCategory;
	private final String fixedText;
	
	TSTokenId(String fixedText, String primaryCategory) {
		this.fixedText = fixedText;
		this.primaryCategory = primaryCategory;
	}
	
	private static final Language<TSTokenId> language = new TSLanguageHierarchy ().language ();

    public static final Language<TSTokenId> getLanguage () {
        return language;
    }

	@Override
	public String primaryCategory() {
		return primaryCategory;
	}
}
