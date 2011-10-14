/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.dfranek.typoscript.lexer;
import java.util.*;
import org.netbeans.spi.lexer.*;

/**
 *
 * @author Daniel
 */
public class TSLanguageHierarchy extends LanguageHierarchy<TSTokenId> {

	private static EnumSet<TSTokenId>  tokens;
    private static Map<Integer,TSTokenId> idToToken;
	
	private static void init () {
        tokens = EnumSet.allOf(TSTokenId.class);
        idToToken = new HashMap<Integer, TSTokenId> ();
        for (TSTokenId token : tokens)
            idToToken.put (token.ordinal (), token);
    }
	
	static synchronized TSTokenId getToken (int id) {
        if (idToToken == null)
            init ();
        return idToToken.get (id);
    }
	
	@Override
	protected synchronized  Collection<TSTokenId> createTokenIds() {
		if (tokens == null)
            init ();
        return tokens;
	}

	@Override
	protected synchronized Lexer<TSTokenId> createLexer(LexerRestartInfo<TSTokenId> lri) {
		 return new TSLexer (lri);
	}

	@Override
	protected String mimeType() {
		return "text/x-typoscript";
	}
	
}
