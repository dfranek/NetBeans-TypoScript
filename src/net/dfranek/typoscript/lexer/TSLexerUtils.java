/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.dfranek.typoscript.lexer;

import java.util.List;
import javax.swing.text.Document;
import org.netbeans.api.lexer.TokenHierarchy;
import org.netbeans.api.lexer.TokenSequence;

/**
 *
 * @author daniel
 */
public class TSLexerUtils {
	public static TokenSequence<TSTokenId> getTSTokenSequence(TokenHierarchy<?> th, int offset) {
        TokenSequence<TSTokenId> ts = th == null ? null : th.tokenSequence(TSTokenId.getLanguage());
        if (ts == null) {
            // Possibly an embedding scenario such as an RHTML file
            // First try with backward bias true
            List<TokenSequence<?>> list = th.embeddedTokenSequences(offset, true);
            for (TokenSequence t : list) {
                if (t.language() == TSTokenId.getLanguage()) {
                    ts = t;
                    break;
                }
            }
            if (ts == null) {
                list = th.embeddedTokenSequences(offset, false);
                for (TokenSequence t : list) {
                    if (t.language() == TSTokenId.getLanguage()) {
                        ts = t;
                        break;
                    }
                }
            }
        }
        return ts;
    }
	
	@SuppressWarnings("unchecked")
    public static TokenSequence<TSTokenId> getTSTokenSequence(Document doc, int offset) {
        TokenHierarchy<Document> th = TokenHierarchy.get(doc);
        return getTSTokenSequence(th, offset);
    }
}
