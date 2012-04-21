/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.dfranek.typoscript.parser;

import java.util.ArrayList;
import java.util.List;
import net.dfranek.typoscript.lexer.TSTokenId;
import net.dfranek.typoscript.parser.ast.TSASTNode;
import org.netbeans.api.lexer.TokenSequence;
import org.netbeans.modules.csl.api.Error;
import org.netbeans.modules.csl.api.OffsetRange;
import org.netbeans.modules.csl.spi.ParserResult;
import org.netbeans.modules.parsing.api.Snapshot;

/**
 *
 * @author Eric Waldburger
 */
public class TSParserResult extends ParserResult {

	private List<Error> errors;
	private boolean valid = true;
	private TSASTNode tree;
	private TokenSequence<TSTokenId> sequence;
	private List<OffsetRange> codeblocks;

	public TSParserResult(Snapshot snapshot) {
		super(snapshot);
		this.errors = new ArrayList<Error>();
		this.codeblocks = new ArrayList<OffsetRange>();
	}

	private TSParserResult(Snapshot snapshot, TokenSequence<TSTokenId> source) {
		super(snapshot);
		this.errors = new ArrayList<Error>();
	}

	@Override
	public List<? extends Error> getDiagnostics() {
		return errors;
	}

	public void addError(Error e) {
		errors.add(e);
	}
	
	public void addCodeBlock(OffsetRange r) {
		codeblocks.add(r);
	}

	@Override
	protected void invalidate() {
		valid = false;
	}
	
	public TSASTNode getTree() {
		return tree;
	}
	
	public void setTree(TSASTNode tree) {
		this.tree = tree;
	}

	public void setSequence(TokenSequence<TSTokenId> sequence) {
		this.sequence = sequence;
	}
	
	public TokenSequence<TSTokenId> getSequence() {
		return this.sequence;
	}
	
	public List<OffsetRange> getCodeBlocks() {
		return codeblocks;
	}
	
}
