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
package net.dfranek.typoscript.parser;

import java.util.logging.Logger;
import net.dfranek.typoscript.lexer.TSTokenId;
import net.dfranek.typoscript.parser.ast.TSASTNode;
import net.dfranek.typoscript.parser.ast.TSASTNodeType;
import org.netbeans.api.lexer.Token;
import org.netbeans.api.lexer.TokenSequence;
import org.netbeans.modules.csl.api.OffsetRange;
import org.netbeans.modules.csl.api.Severity;
import org.netbeans.modules.parsing.api.Snapshot;

/**
 *
 * @author Daniel Franek, Eric Waldburger
 */
public class TSTokenParser {

	private TokenSequence<TSTokenId> sequence;
	private Snapshot snapshot;
	private boolean commentOpen = false;
	private boolean valueOpen = false;
	private int curlyOpen = 0;
	private TSBracketNode last = new TSBracketNode("root", null);
	private TSBracketNode root = last;
	private TSASTNode tree;
	private static final Logger logger = Logger.getLogger(TSTokenParser.class.getName());
	
	private TSParserResult result;

	TSTokenParser(TokenSequence<TSTokenId> source, Snapshot snapshot) {
		sequence = source;
		this.snapshot = snapshot;
		this.curlyOpen = 0;
		result = new TSParserResult(snapshot);
		tree = new TSASTNode("", "", TSASTNodeType.ROOTLEVEL);
	}

	public TSParserResult analyze() {
		TSASTNode node;
		
		result.setSequence(sequence);
		
		Token<TSTokenId> t;
		TSTokenId id;
		TSASTNode actNode = tree;

		while (sequence.moveNext()) {
			//content of token
			t = sequence.token();
			//type of token
			id = t.id();
			//TODO bnf umsetzen von (http://wiki.typo3.org/TypoScript_technical_aspects)

			//TODO CC Objekt abhängig machen

			//ignore Comments
			if (id.equals(TSTokenId.TS_COMMENT)) {
				continue;
			}

			// DF: ausgelagert in eigene Funktion
			checkBraces(id, t, sequence);

			if (!id.equals(TSTokenId.WHITESPACE) && !id.equals(TSTokenId.TS_NL) && !id.equals(TSTokenId.TS_OPERATOR)) {
				node = new TSASTNode(t.text().toString(), "", TSASTNodeType.UNKNOWN);

				if (!actNode.hasChild(node)) {
					actNode.addChild(node);
					actNode = node;
				} else {
					actNode = node;
				}
			}
			if (id.equals(TSTokenId.TS_NL)) {
				actNode = tree;
			}
		}
		
		result.setTree(tree);

		if (root.getNext() != null) {
			result.addError(new TSError("Not all brackets where closed", snapshot.getSource().getFileObject(), snapshot.getSource().getDocument(true).getLength() - 1, snapshot.getSource().getDocument(true).getLength(), Severity.ERROR, new Object[]{this}));
		}

		return result;
	}

	private void checkBraces(TSTokenId id, Token<TSTokenId> t, TokenSequence<TSTokenId> ts) {
		//Bracket Handling
			// DF:  Conditions sind gesamt ein Token, hier sollte überprüft werden ob das letzte Zeichen ein ] ist.
			if (id.equals(TSTokenId.TS_CURLY_OPEN) || id.equals(TSTokenId.TS_CURLY_CLOSE) || id.equals(TSTokenId.TS_PARANTHESE) || id.equals(TSTokenId.TS_CONDITION) || id.equals(TSTokenId.TS_VALUE) || id.equals(TSTokenId.TS_OPERATOR)) {
				String tokenText = t.text().toString();
				if (id == TSTokenId.TS_CURLY_OPEN) {
					if (last == null) {
						last = new TSBracketNode("{", null, ts.offset());
					} else {
						last.setNext(new TSBracketNode("{", last, ts.offset()));
						last = last.getNext();
					}
					result.addCodeBlock(new OffsetRange(last.getOffset(), ts.offset()));
				} else if (id == TSTokenId.TS_CURLY_CLOSE) {
					if (last.getValue().equals("{")) {
						last = last.getPrev();
						last.setNext(null);
					} else {
						result.addError(new TSError("No matching bracket found for " + last.getValue(), snapshot.getSource().getFileObject(), id.getStart(), id.getEnd(), Severity.ERROR, new Object[]{this}));
					}
				} else if (tokenText.equals("(") && id == TSTokenId.TS_PARANTHESE) {
					if (last == null) {
						last = new TSBracketNode("(", null, ts.offset());
					} else {
						last.setNext(new TSBracketNode("(", last, ts.offset()));
						last = last.getNext();
					}
						result.addCodeBlock(new OffsetRange(last.getOffset(), ts.offset()));
				} else if (tokenText.equals(")") && id == TSTokenId.TS_PARANTHESE) {
					if (last.getValue().equals("(")) {
						last = last.getPrev();
						last.setNext(null);
					} else {
						result.addError(new TSError("No matching bracket found for " + last.getValue(), snapshot.getSource().getFileObject(), id.getStart(), id.getEnd(), Severity.ERROR, new Object[]{this}));
					}
				} else if (tokenText.equals("[")) {
					if (last == null) {
						last = new TSBracketNode("[", null, ts.offset());
					} else {
						last.setNext(new TSBracketNode("[", last, ts.offset()));
						last = last.getNext();
					}
				} else if (tokenText.equals("]")) {
					if (last.getValue().equals("[")) {
						last = last.getPrev();
						last.setNext(null);
					} else {
						result.addError(new TSError("No matching bracket found for " + last.getValue(), snapshot.getSource().getFileObject(), id.getStart(), id.getEnd(), Severity.ERROR, new Object[]{this}));
					}
				} else if (id == TSTokenId.TS_CONDITION && (!tokenText.equals("[global]") && !tokenText.equals("[end]"))) {
					result.addCodeBlock(new OffsetRange(ts.offset(), ts.offset()+t.length()));
				}
			}
	}
}
