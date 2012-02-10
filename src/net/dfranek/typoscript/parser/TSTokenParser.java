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

import java.util.Enumeration;
import java.util.logging.Logger;
import javax.swing.tree.DefaultMutableTreeNode;
import net.dfranek.typoscript.lexer.TSTokenId;
import net.dfranek.typoscript.parser.ast.TSASTNode;
import net.dfranek.typoscript.parser.ast.TSASTNodeType;
import org.netbeans.api.lexer.Token;
import org.netbeans.api.lexer.TokenSequence;
import org.netbeans.modules.csl.api.Severity;
import org.netbeans.modules.parsing.api.Snapshot;

/**
 *
 * @author daniel, Eric Waldburger
 */
public class TSTokenParser {

	private TokenSequence<TSTokenId> sequence;
	private Snapshot snapshot;
	private boolean commentOpen = false;
	private boolean valueOpen = false;
	private int curlyOpen = 0;
	private BracketNode last = new BracketNode("root", null);
	private BracketNode root = last;
	private TSASTNode tree;
	private static final Logger logger =  Logger.getLogger(TSTokenParser.class.getName());

	TSTokenParser(TokenSequence<TSTokenId> source, Snapshot snapshot) {
		sequence = source;
		this.snapshot = snapshot;
		this.curlyOpen = 0;
		tree = new TSASTNode("", "", TSASTNodeType.ROOTLEVEL);
	}

	public TSParserResult analyze() {
		TSASTNode node;
		TSParserResult r = new TSParserResult(snapshot);
		Token<TSTokenId> t;
		TSTokenId id;
		TSASTNode actNode = tree;

		while (sequence.moveNext()) {
			//content of token
			t = sequence.token();
			//type of token
			id = t.id();
//                      Debugger.pr(t);
//			Debugger.pr(t.id());

			//TODO bnf umsetzen von (http://wiki.typo3.org/TypoScript_technical_aspects)

			//TODO CC Objekt abhängig machen

			//ignore Comments
			if (id.equals(TSTokenId.TS_COMMENT)) {
				continue;
			}

			//Bracket Handling
			// DF: sollte nur für { und ( greifen, Conditions sind gesamt ein Token, hier sollte überprüft werden ob das letzte Zeichen ein ] ist. Bei Values wirds mit meinem momentanen Tokenizer nicht funktioneren, bei Operatoren sehe ich hier keinen Sinn
			if (id.equals(TSTokenId.TS_CURLY) || id.equals(TSTokenId.TS_PARANTHESE)/* || id.equals(TSTokenId.TS_CONDITION) || id.equals(TSTokenId.TS_VALUE) || id.equals(TSTokenId.TS_OPERATOR))*/) {
				switch (t.text().toString()) {
					case "{":
						//add bracket to list
						if (last == null) {
							last = new BracketNode("{", null);
						} else {
							last.setNext(new BracketNode("{", last));
							last = last.getNext();
						}
						break;
					case "}":
						//delete last bracket from list if its the opposite bracket
						//else throw error
						if (last.getValue().equals("{")) {
							last = last.getPrev();
							last.setNext(null);
						} else {
//					    Debugger.pr("missing {");
							r.addError(new TSError("No matching bracket found for "+last.getValue(), snapshot.getSource().getFileObject(), id.getStart(), id.getEnd(), Severity.ERROR, new Object[]{this}));
						}
						break;
					case "(":
						if (last == null) {
							last = new BracketNode("(", null);
						} else {
							last.setNext(new BracketNode("(", last));
							last = last.getNext();
						}
						break;
					case ")":
						if (last.getValue().equals("(")) {
							last = last.getPrev();
							last.setNext(null);
						} else {
//					    Debugger.pr("missing (");
							r.addError(new TSError("No matching bracket found for "+last.getValue(), snapshot.getSource().getFileObject(), id.getStart(), id.getEnd(), Severity.ERROR, new Object[]{this}));
						}
						break;
					case "[":
						if (last == null) {
							last = new BracketNode("[", null);
						} else {
							last.setNext(new BracketNode("[", last));
							last = last.getNext();
						}
						break;
					case "]":
						if (last.getValue().equals("[")) {
							last = last.getPrev();
							last.setNext(null);
						} else {
//					    Debugger.pr("missing [");
							r.addError(new TSError("No matching bracket found for "+last.getValue(), snapshot.getSource().getFileObject(), id.getStart(), id.getEnd(), Severity.ERROR, new Object[]{this}));
						}
						break;
				}
			}

			if (!id.equals(TSTokenId.TS_COMMENT) && !id.equals(TSTokenId.WHITESPACE) && !id.equals(TSTokenId.TS_NL) && !id.equals(TSTokenId.TS_OPERATOR)) {
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
		
		if (root.getNext() != null) {
			r.addError(new TSError("Not all brackets where closed", snapshot.getSource().getFileObject(), snapshot.getSource().getDocument(true).getLength()-1, snapshot.getSource().getDocument(true).getLength(), Severity.ERROR, new Object[]{this}));
		}

		logger.info("return value");
		return r;
	}
}
