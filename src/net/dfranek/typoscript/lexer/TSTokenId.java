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

import org.netbeans.api.lexer.Language;
import org.netbeans.api.lexer.TokenId;

/**
 * Enum with possible tokens for the TypoScript Language
 * 
 * @author Daniel Franek
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
	TS_PARANTHESE(null, "brace"),
	WHITESPACE(null, "whitespace"),
	UNKNOWN_TOKEN(null, "error");
	private final String primaryCategory;
	private final String fixedText;

	TSTokenId(String fixedText, String primaryCategory) {
		this.fixedText = fixedText;
		this.primaryCategory = primaryCategory;
	}
	private static final Language<TSTokenId> language = new TSLanguageHierarchy().language();

	public static Language<TSTokenId> getLanguage() {
		return language;
	}

	@Override
	public String primaryCategory() {
		return primaryCategory;
	}
}
