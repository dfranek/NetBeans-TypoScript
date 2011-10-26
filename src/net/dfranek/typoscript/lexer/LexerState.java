/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.dfranek.typoscript.lexer;

/**
 *
 * @author daniel
 */
public enum LexerState {
	IN_VALUE,
	IN_COMMENT,
	IN_PARANTHESE,
	IN_REGEXP,
	DEFAULT
}