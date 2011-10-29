/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.dfranek.typoscript.parser;

import net.dfranek.typoscript.lexer.TSTokenId;
import org.netbeans.api.lexer.Token;
import org.netbeans.api.lexer.TokenSequence;
import org.netbeans.modules.csl.api.Severity;
import org.netbeans.modules.parsing.api.Snapshot;

/**
 *
 * @author daniel
 */
public class TSTokenParser {
	private TokenSequence<TSTokenId> sequence;
	private Snapshot snapshot;
	private boolean commentOpen = false;
	private boolean valueOpen = false;
	private int curlyOpen = 0;
	


	TSTokenParser(TokenSequence<TSTokenId> source, Snapshot snapshot) {
		sequence = source;
		this.snapshot = snapshot;
	}
	
	public TSParser.TSParserResult analyze() {
		TSParser.TSParserResult r = new TSParser.TSParserResult(snapshot);
		Token<TSTokenId> t;
		while (sequence.moveNext()) {
			t = sequence.token();
			if(t.text() == "{") {
				curlyOpen++;
			} else if(t.text() == "}") {
				curlyOpen--;
			}
		}
		
		if(curlyOpen != 0) {
			r.addError(new TSError("On return to [GLOBAL] scope, the script was short of " + curlyOpen + " end brace(s)", snapshot.getSource().getFileObject(), 1, 2, Severity.ERROR, new Object[]{this}));
		}
		
		return r;
	}

}
