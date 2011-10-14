/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.dfranek.typoscript.lexer;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.netbeans.api.lexer.Token;
import org.netbeans.api.lexer.TokenUtilities;
import org.netbeans.spi.lexer.Lexer;
import org.netbeans.spi.lexer.LexerInput;
import org.netbeans.spi.lexer.LexerRestartInfo;
import org.netbeans.spi.lexer.TokenFactory;

/**
 *
 * @author Daniel
 */
public class TSLexer implements Lexer<TSTokenId> {

    private LexerRestartInfo<TSTokenId> info;
    private TokenFactory<TSTokenId> tokenFactory;
    private final TSScanner scanner;

    TSLexer(LexerRestartInfo<TSTokenId> info) {
        this.info = info;
        scanner = new TSScanner(info);
        tokenFactory = info.tokenFactory();
    }

    @Override
    public Token<TSTokenId> nextToken() {
        
        try {
            TSTokenId tokenId = scanner.nextToken();
            int readLen = scanner.getReadLength();
            if (readLen < 1) return null;
            Token<TSTokenId> token = null;
            
            if (tokenId != null) {
                token = tokenFactory.createToken(tokenId);
            }
            return token;
        } catch (Exception ex) {
           Logger.getLogger(TSLexer.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    @Override
    public Object state() {
        return null;
    }

    @Override
    public void release() {
    }
}
