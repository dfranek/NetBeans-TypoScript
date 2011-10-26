/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.dfranek.typoscript.parser;

import java.io.Reader;
import java.io.StringReader;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.event.ChangeListener;
import org.netbeans.modules.csl.api.Error;
import org.netbeans.modules.csl.spi.ParserResult;
import org.netbeans.modules.parsing.api.Snapshot;
import org.netbeans.modules.parsing.api.Task;
import org.netbeans.modules.parsing.spi.ParseException;
import org.netbeans.modules.parsing.spi.Parser;
import org.netbeans.modules.parsing.spi.SourceModificationEvent;

/**
 *
 * @author daniel
 */
public class TSParser extends Parser {

	private Snapshot snapshot;
	private ParserResult result = null;
	private static final Logger LOGGER = Logger.getLogger(TSParser.class.getName());

	@Override
	public void parse(Snapshot snapshot, Task task, SourceModificationEvent sme) throws ParseException {
		this.snapshot = snapshot;
		String source = snapshot.getText().toString();
		try {
			result = parseSource(source);
		} catch(Exception e) {
			LOGGER.log (Level.FINE, "Exception during parsing: {0}", e);
			result = new TSParserResult(snapshot);
		}
	}

	@Override
	public Result getResult(Task task) throws ParseException {
		return result;
	}

	@Override
	public void addChangeListener(ChangeListener cl) {
	}

	@Override
	public void removeChangeListener(ChangeListener cl) {
	}

	private ParserResult parseSource(String source) {
		throw new UnsupportedOperationException("Not yet implemented");
	}

	public static class TSParserResult extends ParserResult {

		private List<Error> errors;
		private boolean valid = true;

		public TSParserResult(Snapshot snapshot) {
			super(snapshot);
			this.errors = Collections.<Error>emptyList();
		}

		@Override
		public List<? extends Error> getDiagnostics() {
			return errors;
		}

		@Override
		protected void invalidate() {
			valid = false;
		}
	}
}
