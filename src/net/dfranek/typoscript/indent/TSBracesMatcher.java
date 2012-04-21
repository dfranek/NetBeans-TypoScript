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
package net.dfranek.typoscript.indent;

import javax.swing.text.AbstractDocument;
import javax.swing.text.BadLocationException;
import net.dfranek.typoscript.lexer.TSLexerUtils;
import net.dfranek.typoscript.lexer.TSTokenId;
import org.netbeans.api.lexer.Token;
import org.netbeans.api.lexer.TokenId;
import org.netbeans.api.lexer.TokenSequence;
import org.netbeans.editor.BaseDocument;
import org.netbeans.modules.csl.api.OffsetRange;
import org.netbeans.spi.editor.bracesmatching.BracesMatcher;
import org.netbeans.spi.editor.bracesmatching.MatcherContext;

/**
 *
 * @author Daniel Franek
 */
public class TSBracesMatcher implements BracesMatcher {

	MatcherContext context;

	TSBracesMatcher(MatcherContext mc) {
		context = mc;
	}

	@Override
	public int[] findOrigin() throws InterruptedException, BadLocationException {
		((AbstractDocument) context.getDocument()).readLock();
        try {
            BaseDocument doc = (BaseDocument) context.getDocument();
            int offset = context.getSearchOffset();
            
            TokenSequence<?extends TSTokenId> ts = TSLexerUtils.getTSTokenSequence(doc, offset);
            
            if (ts != null) {
                ts.move(offset);

                if (!ts.moveNext()) {
                    return null;
                }

                Token<?extends TSTokenId> token = ts.token();

                if (token == null) {
                    return null;
                }
                
                TokenId id = token.id();
                
                if (TSLexerUtils.textEquals(token.text(), '(')) {
                    return new int [] { ts.offset(), ts.offset() + token.length() };
                } else if (TSLexerUtils.textEquals(token.text(), ')')) {
                    return new int [] { ts.offset(), ts.offset() + token.length() };
                } else if (id == TSTokenId.TS_CURLY_OPEN) {
                    return new int [] { ts.offset(), ts.offset() + token.length() };
                } else if (id == TSTokenId.TS_CURLY_CLOSE) {
                    return new int [] { ts.offset(), ts.offset() + token.length() };
                } else if (TSLexerUtils.textEquals(token.text(), '[')) {
                    return new int [] { ts.offset(), ts.offset() + token.length() };
                } else if (TSLexerUtils.textEquals(token.text(), ']')) {
                    return new int [] { ts.offset(), ts.offset() + token.length() };
                }
                
            }
            return null;
        } finally {
            ((AbstractDocument) context.getDocument()).readUnlock();
        }
	}

	@Override
	public int[] findMatches() throws InterruptedException, BadLocationException {
		((AbstractDocument) context.getDocument()).readLock();
        try {
            BaseDocument doc = (BaseDocument) context.getDocument();
            int offset = context.getSearchOffset();

            TokenSequence<?extends TSTokenId> ts = TSLexerUtils.getTSTokenSequence(doc, offset);

            if (ts != null) {
                ts.move(offset);

                if (!ts.moveNext()) {
                    return null;
                }

                Token<?extends TSTokenId> token = ts.token();

                if (token == null) {
                    return null;
                }
                
                TokenId id = token.id();
                
                OffsetRange r;
                if (TSLexerUtils.textEquals(token.text(), '(')) {
                    r = TSLexerUtils.findFwd(ts, TSTokenId.TS_PARANTHESE, '(', TSTokenId.TS_PARANTHESE, ')');
                    return new int [] {r.getStart(), r.getEnd() };
                } else if (TSLexerUtils.textEquals(token.text(), ')')) {
                    r = TSLexerUtils.findBwd(ts, TSTokenId.TS_PARANTHESE, '(', TSTokenId.TS_PARANTHESE, ')');
                    return new int [] {r.getStart(), r.getEnd() };
                } else if (id == TSTokenId.TS_CURLY_OPEN) {
                    r= TSLexerUtils.findFwd(ts, TSTokenId.TS_CURLY_OPEN ,'{', TSTokenId.TS_CURLY_CLOSE, '}');
                    return new int [] {r.getStart(), r.getEnd() };
                } else if (id == TSTokenId.TS_CURLY_CLOSE) {
                    r = TSLexerUtils.findBwd(ts, TSTokenId.TS_CURLY_OPEN, '{', TSTokenId.TS_CURLY_CLOSE, '}');
                    return new int [] {r.getStart(), r.getEnd() };
                } else if (TSLexerUtils.textEquals(token.text(), '[')) {
                    r = TSLexerUtils.findFwd(ts, TSTokenId.TS_OPERATOR, '[', TSTokenId.TS_OPERATOR, ']');
                    return new int [] {r.getStart(), r.getEnd() };
                } else if (TSLexerUtils.textEquals(token.text(), ']')) {
                    r = TSLexerUtils.findBwd(ts, TSTokenId.TS_OPERATOR, '[', TSTokenId.TS_OPERATOR, ']');
                    return new int [] {r.getStart(), r.getEnd() };
                }
            }
            return null;
        } finally {
            ((AbstractDocument) context.getDocument()).readUnlock();
        }
	}
}
