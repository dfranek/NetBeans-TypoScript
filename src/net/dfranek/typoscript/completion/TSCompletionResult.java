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
package net.dfranek.typoscript.completion;

import java.util.ArrayList;
import java.util.Collection;
import java.util.logging.Logger;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import org.netbeans.modules.csl.api.CodeCompletionContext;
import org.netbeans.modules.csl.api.CompletionProposal;
import org.netbeans.modules.csl.spi.DefaultCompletionResult;
import org.openide.util.Exceptions;
/**
 *
 * @author daniel
 */
class TSCompletionResult extends DefaultCompletionResult {
	
	private final CodeCompletionContext context;

	public TSCompletionResult(CodeCompletionContext completionContext) {
		super(new ArrayList<CompletionProposal>(), false);
		context = completionContext;
	}

	public void addAll(final Collection<CompletionProposal>  proposals) {
        list.addAll(proposals);
    }
    
	public void add(CompletionProposal  proposal) {
        list.add(proposal);
    }

	@Override
	public void afterInsert(CompletionProposal cp) {
		Document doc = context.getParserResult().getSnapshot().getSource().getDocument(true);
		try {
			// Remove typed characters
			Logger.getLogger(TSCompletionResult.class.getName()).finest(doc.getText(cp.getAnchorOffset()-context.getPrefix().length(), context.getPrefix().length()));
			doc.remove(cp.getAnchorOffset()-context.getPrefix().length(), context.getPrefix().length());
		} catch (BadLocationException ex) {
			Exceptions.printStackTrace(ex);
		}
	}
	
}
