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

package net.dfranek.typoscript.embedding;

import java.util.ArrayList;
import java.util.List;
import net.dfranek.typoscript.lexer.TSTokenId;
import org.netbeans.api.lexer.Token;
import org.netbeans.api.lexer.TokenSequence;
import org.netbeans.modules.parsing.api.Embedding;
import org.netbeans.modules.parsing.api.Snapshot;
import org.netbeans.modules.parsing.spi.EmbeddingProvider;

/**
 *
 * @author Daniel Franek
 */
@EmbeddingProvider.Registration(
        mimeType="text/x-typoscript",
        targetMimeType="text/html"
)
public class HtmlEmbeddingProvider extends EmbeddingProvider {
	
	private final String HTML_MIME_TYPE = "text/html";
	
	@Override
	public List<Embedding> getEmbeddings(Snapshot snapshot) {
		final List<Embedding> embeddings = new ArrayList<>();
		final TokenSequence<TSTokenId> ts = snapshot.getTokenHierarchy().tokenSequence(TSTokenId.language());
		
		while (ts.moveNext()) {
			Token<TSTokenId> token = ts.token();
			if(token.id() == TSTokenId.TS_MULTILINE_VALUE) {
				embeddings.add(snapshot.create(ts.offset(), token.length(), HTML_MIME_TYPE));
			}
		}
		
		return embeddings;
	}

	@Override
	public int getPriority() {
		return 50;
	}

	@Override
	public void cancel() {
		// do nothing
	}
	
}
