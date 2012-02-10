/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.dfranek.typoscript.parser;

import java.util.AbstractList;
import java.util.Collections;
import java.util.List;
import java.util.Vector;
import net.dfranek.typoscript.lexer.TSTokenId;
import org.netbeans.api.lexer.TokenSequence;
import org.netbeans.modules.csl.spi.ParserResult;
import org.netbeans.modules.parsing.api.Snapshot;
import net.dfranek.typoscript.debug.Debugger;

/**
 *
 * @author Eric Waldburger
 */

public class TSParserResult extends ParserResult {
                
    private Vector<org.netbeans.modules.csl.api.Error> errors;
    private boolean valid = true;

    public TSParserResult(Snapshot snapshot) {
            super(snapshot);
	    this.errors = new Vector<org.netbeans.modules.csl.api.Error>();
    }

    private TSParserResult(Snapshot snapshot, TokenSequence<TSTokenId> source) {
            super(snapshot);
            this.errors = new Vector<org.netbeans.modules.csl.api.Error>();
    }

    @Override
    public List<? extends org.netbeans.modules.csl.api.Error> getDiagnostics() {
            return errors;
    }

    public void addError(org.netbeans.modules.csl.api.Error e ) {
            errors.add(e);
    }

    @Override
    protected void invalidate() {
            valid = false;
}
}
