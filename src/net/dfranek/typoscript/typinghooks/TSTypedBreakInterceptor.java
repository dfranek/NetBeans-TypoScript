/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.dfranek.typoscript.typinghooks;

import java.util.Arrays;
import java.util.List;
import javax.swing.text.BadLocationException;
import net.dfranek.typoscript.lexer.TSLexerUtils;
import net.dfranek.typoscript.lexer.TSTokenId;
import org.netbeans.api.editor.mimelookup.MimePath;
import org.netbeans.api.editor.mimelookup.MimeRegistration;
import org.netbeans.api.lexer.Token;
import org.netbeans.api.lexer.TokenId;
import org.netbeans.api.lexer.TokenSequence;
import org.netbeans.editor.BaseDocument;
import org.netbeans.editor.Utilities;
import org.netbeans.modules.editor.indent.api.IndentUtils;
import org.netbeans.spi.editor.typinghooks.TypedBreakInterceptor;

/**
 *
 * @author Daniel
 */
public class TSTypedBreakInterceptor implements TypedBreakInterceptor {

	@Override
	public boolean beforeInsert(Context cntxt) throws BadLocationException {
		return false;
	}

	@Override
	public void insert(MutableContext context) throws BadLocationException {
		final BaseDocument doc = (BaseDocument) context.getDocument();
		int offset = context.getCaretOffset();
		int lineBegin = Utilities.getRowStart(doc, offset);
		int lineEnd = Utilities.getRowEnd(doc, offset);
		if (lineBegin == offset && lineEnd == offset) {
			// Pressed return on a blank newline - do nothing
			return;
		}
		TokenSequence<? extends TSTokenId> ts = TSLexerUtils.getTSTokenSequence(doc, offset);
		if (ts == null) {
			return;
		}
		ts.move(offset);
		if (!ts.moveNext() && !ts.movePrevious()) {
			return;
		}
		Token<? extends TSTokenId> token = ts.token();
		TokenId id = token.id();
		int tokenOffsetOnCaret = ts.offset();
		// Insert an end statement? Insert a } marker?
		int[] startOfContext = new int[1];
		TSTokenId completeIn = findContextForEnd(ts, offset, startOfContext);
		boolean insert = completeIn != null && isEndMissing(doc, offset, completeIn);
		if (insert) {
			int indent = IndentUtils.lineIndent(doc, IndentUtils.lineStartOffset(doc, startOfContext[0]));
			int afterLastNonWhite = Utilities.getRowLastNonWhite(doc, offset);
			
			int newIndent = ((indent / IndentUtils.tabSize(doc)) + 1) * IndentUtils.tabSize(doc);
			
			StringBuilder sb = new StringBuilder("\n");
			String restOfLine = doc.getText(offset, Utilities.getRowEnd(doc, afterLastNonWhite) - offset);
			sb.append(restOfLine);
			sb.append(IndentUtils.createIndentString(doc, newIndent));
			sb.append("\n"); //NOI18N
			sb.append(IndentUtils.createIndentString(doc, indent));
			doc.remove(offset, restOfLine.length());
			if(completeIn == TSTokenId.TS_CURLY_OPEN) {
				sb.append('}');
			}
			
			context.setText(sb.toString(), 0, IndentUtils.createIndentString(doc, newIndent).length()+1);
		}
	}

	private TSTokenId findContextForEnd(TokenSequence<? extends TSTokenId> ts, int offset, int[] startOfContext) {
		if (ts == null) {
			return null;
		}
		if (ts.offset() != offset) {
			ts.move(offset);

			if (!ts.moveNext() && !ts.movePrevious()) {
				return null;
			}
		}
		TSTokenId result = null;

		// at fist there should be find a bracket  '{' or column ':'
		Token<? extends TSTokenId> bracketColumnToken = TSLexerUtils.findPrevious(ts,
				Arrays.asList(TSTokenId.TS_COMMENT, TSTokenId.WHITESPACE, TSTokenId.TS_NL));
		if (bracketColumnToken != null
				&& (bracketColumnToken.id() == TSTokenId.TS_CURLY_OPEN)) {
			startOfContext[0] = ts.offset();
			// we are interested only in adding end for { or alternative syntax :
			List<TSTokenId> lookFor = Arrays.asList(TSTokenId.TS_CURLY_CLOSE, TSTokenId.TS_CURLY_OPEN);
			Token<? extends TSTokenId> keyToken = TSLexerUtils.findPreviousToken(ts, lookFor);

			if (bracketColumnToken.id() == TSTokenId.TS_CURLY_OPEN) {
				result = TSTokenId.TS_CURLY_OPEN;
			}
			
			if (keyToken.id() != TSTokenId.TS_CURLY_CLOSE) {
				startOfContext[0] = ts.offset();
			}
		}
		ts.move(offset);
		if (!ts.moveNext() && !ts.movePrevious()) {
			return null;
		}
		return result;
	}

	private boolean isEndMissing(BaseDocument doc, int offset, TSTokenId startTokenId) throws BadLocationException {
		TokenSequence<? extends TSTokenId> ts = TSLexerUtils.getTSTokenSequence(doc, offset);
		if (ts == null) {
			return false;
		}
		ts.move(0);
		if (!ts.moveNext() && !ts.movePrevious()) {
			return false;
		}
		Token<? extends TSTokenId> token;
		int curlyBalance = 0;
		boolean curlyProcessed = false;
		if (startTokenId == TSTokenId.TS_CURLY_OPEN) {
			boolean unfinishedComment = false;
			do {
				token = ts.token();
				if (token.id() == TSTokenId.TS_CURLY_CLOSE) {
					curlyBalance--;
					curlyProcessed = true;
				} else if (token.id() == TSTokenId.TS_CURLY_OPEN) { //NOI18N
					curlyBalance++;
					curlyProcessed = true;
				}

				if (curlyBalance == 0 && curlyProcessed && ts.offset() > offset) {
					break;
				}
			} while (ts.moveNext());
			if (unfinishedComment) {
				curlyBalance--;
			}
		}
		return curlyBalance > 0;
	}

	@Override
	public void afterInsert(Context cntxt) throws BadLocationException {
		// deliberatly empty
	}

	@Override
	public void cancelled(Context cntxt) {
		// deliberatly empty
	}
	
	@MimeRegistration(mimeType = "text/x-typoscript", service = TypedBreakInterceptor.Factory.class)
    public static class PhpFactory implements TypedBreakInterceptor.Factory {

        @Override
        public TypedBreakInterceptor createTypedBreakInterceptor(MimePath mimePath) {
            return new TSTypedBreakInterceptor();
        }
    }

}
