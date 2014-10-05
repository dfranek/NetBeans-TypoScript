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
package net.dfranek.typoscript.nav;

import javax.swing.text.Document;
import net.dfranek.typoscript.lexer.TSLexerUtils;
import net.dfranek.typoscript.lexer.TSTokenId;
import net.dfranek.typoscript.parser.TSParserResult;
import net.dfranek.typoscript.parser.ast.TSASTNode;
import org.netbeans.api.lexer.Token;
import org.netbeans.api.lexer.TokenHierarchy;
import org.netbeans.api.lexer.TokenSequence;
import org.netbeans.modules.csl.api.DeclarationFinder;
import org.netbeans.modules.csl.api.OffsetRange;
import org.netbeans.modules.csl.spi.ParserResult;
import org.openide.filesystems.FileObject;

/**
 *
 * @author Daniel Franek
 */
public class TSDeclarationFinder implements DeclarationFinder  {

	@Override
	public DeclarationLocation findDeclaration(ParserResult info, int caretOffset) {
		DeclarationLocation declLocation = DeclarationLocation.NONE;
		if (!(info instanceof TSParserResult)) return DeclarationLocation.NONE;
        TSParserResult result = (TSParserResult) info;
		
		TokenHierarchy<?> th = result.getSnapshot().getTokenHierarchy();
		TokenSequence<TSTokenId> ts = th.tokenSequence(TSTokenId.getLanguage());
		TSASTNode tree = result.getTree();
		ts.move(caretOffset);
		ts.moveNext();
		Token<TSTokenId> token = ts.token();
		TSASTNode item = tree.getChild(token.text().toString());
		FileObject fo = result.getSnapshot().getSource().getFileObject();
		if(fo != null && item != null) {
			declLocation = new DeclarationLocation(result.getSnapshot().getSource().getFileObject(), item.getOffset());
		}
		
		return declLocation;
	}

	@Override
	public OffsetRange getReferenceSpan(final Document doc, final int caretOffset) {
		TokenSequence<TSTokenId> ts = TSLexerUtils.getTSTokenSequence(doc, caretOffset);
		ts.move(caretOffset);
		if (ts.moveNext()) {
			int offset = ts.offset();
			Token<TSTokenId> token = ts.token();
			TSTokenId id = token.id();
			if(id.equals(TSTokenId.TS_PROPERTY)) {
				Token<? extends TSTokenId> opVal = TSLexerUtils.findBwdNonSpace(ts);
				if ((opVal.id().equals(TSTokenId.TS_OPERATOR) && opVal.text().toString().equals("<"))) {
					return new OffsetRange(offset, offset + token.length());
				}
			}
		}
		
		
		
		return OffsetRange.NONE;
	}

}
