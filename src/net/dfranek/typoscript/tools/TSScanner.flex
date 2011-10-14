/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2010 Oracle and/or its affiliates. All rights reserved.
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
 * Contributor(s):
 * 
 * Portions Copyrighted 2008 Sun Microsystems, Inc.
 */

package net.dfranek.typoscript.lexer;

import org.netbeans.spi.lexer.LexerInput;
import org.netbeans.spi.lexer.LexerRestartInfo;
%%

%public
%class TSScanner
%type TSTokenId
%function nextToken
%unicode
%caseless
%char

%state LINE_COMMENT
%state COMMENT_SECTION
%state CONDITION
%state VALUE
%state CURLY
%state PARANTHESE
%state INCLUDE

%eofval{
       if(input.readLength() > 0) {
            // backup eof
            input.backup(1);
            //and return the text as error token
            return TSTokenId.UNKNOWN_TOKEN;
        } else {
            return null;
        }
%eofval}

%{
private LexerInput input;
public TSScanner(LexerRestartInfo info) {
	this.input = info.input();
}
%}

WHITESPACE=[ \n\r\t]+
NEWLINE=("\r"|"\n"|"\r\n")
ANY_CHAR=(.|[\n])

%%

<YYINITIAL> {
    "<INCLUDE_TYPOSCRIPT:" { yybegin(INCLUDE); return TSTokenId.TS_INCLUDE_START;}
    "<"	{ return TSTokenId.TS_LT; }
    ">"	{ return TSTokenId.TS_GT; }
    "{"	{ return TSTokenId.TS_CURLY_START; }
    "}"	{ return TSTokenId.TS_CURLY_END; }
    "("	{ yybegin(PARANTHESE); return TSTokenId.TS_PARANTHESE_START; }
	"." { return TSTokenId.TS_DOT; }
    ":=" {yybegin(VALUE);return TSTokenId.TS_COLON_EQUALS; }
    "="	{yybegin(VALUE); return TSTokenId.TS_EQUALS; }
    "|" { return TSTokenId.TS_PIPE;}
    {WHITESPACE}+ {return TSTokenId.WHITESPACE;}
    ([#]|"/") {yybegin(LINE_COMMENT);}
    "/*" {yybegin(COMMENT_SECTION); return TSTokenId.TS_COMMENT_START;}
    "[" {yybegin(CONDITION); return TSTokenId.TS_CONDITION_START;}
}

<LINE_COMMENT> {
	{NEWLINE} {yybegin(YYINITIAL); return TSTokenId.TS_LINE_COMMENT;}
	[^\n\r]*{ANY_CHAR} {return TSTokenId.TS_LINE_COMMENT;}
}

<PARANTHESE> {
	")"	{yybegin(YYINITIAL); return TSTokenId.TS_PARANTHESE_END; }
	~")" {yybegin(YYINITIAL);return TSTokenId.TS_VALUE;}
}

<VALUE> {
	{NEWLINE} {yybegin(YYINITIAL);return TSTokenId.TS_VALUE;}
	[^\n\r]*{ANY_CHAR} {return TSTokenId.TS_VALUE;}
}

<COMMENT_SECTION> {
	"*/"  {yybegin(YYINITIAL); return TSTokenId.TS_COMMENT_END;}
	~"*/" {return TSTokenId.TS_COMMENT;}
}

<CONDITION> {
	"]" {yybegin(YYINITIAL); return TSTokenId.TS_CONDITION_END;}
	{NEWLINE} {yybegin(YYINITIAL); return TSTokenId.TS_PROPERTY;}
	"end" {return TSTokenId.TS_END;}
	"global" {return TSTokenId.TS_GLOBAL;}
	"else" {return TSTokenId.TS_ELSE;}
	~"]" {return TSTokenId.TS_CONDITION;}
}

<INCLUDE> {
	">" {yybegin(YYINITIAL); return TSTokenId.TS_INCLUDE_END;}
	{NEWLINE} {yybegin(YYINITIAL); return TSTokenId.TS_INCLUDE_END;}
	"source" { return TSTokenId.TS_SOURCE;}
	~">" {return TSTokenId.TS_INCLUDE;}
}

.|\n {return TSTokenId.TS_PROPERTY;}