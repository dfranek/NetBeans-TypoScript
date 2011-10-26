/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.dfranek.typoscript.completion;

import java.util.ArrayList;
import java.util.Collection;
import org.netbeans.modules.csl.api.CodeCompletionContext;
import org.netbeans.modules.csl.api.CompletionProposal;
import org.netbeans.modules.csl.spi.DefaultCompletionResult;

/**
 *
 * @author daniel
 */
class TSCompletionResult extends DefaultCompletionResult {

	public TSCompletionResult(CodeCompletionContext completionContext) {
		super(new ArrayList<CompletionProposal>(), false);
	}

	public void addAll(final Collection<CompletionProposal>  proposals) {
        list.addAll(proposals);
    }
    
	public void add(CompletionProposal  proposal) {
        list.add(proposal);
    }
}
