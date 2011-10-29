/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.dfranek.typoscript.parser;

import org.netbeans.modules.csl.api.Severity;
import org.openide.filesystems.FileObject;

/**
 *
 * @author daniel
 */
public class TSError implements org.netbeans.modules.csl.api.Error{

	private final String displayName;

    private final FileObject file;
    private final int startPosition;
    private final int endPosition;
    private final Severity severity;
    private final Object[] parameters;

	public TSError(String name, FileObject file, int start, int end, Severity severity, Object[] params) {
		displayName = name;
		this.file = file;
		startPosition = start;
		endPosition = end;
		this.severity = severity;
		this.parameters = params;
	}
	
	@Override
	public String getDisplayName() {
		return displayName;
	}

	@Override
	public String getDescription() {
		return null;
	}

	@Override
	public String getKey() {
		return "[" + startPosition + "," + endPosition + "]-" + displayName ;
	}

	@Override
	public FileObject getFile() {
		return this.file;
	}

	@Override
	public int getStartPosition() {
		return startPosition;
	}

	@Override
	public int getEndPosition() {
		return endPosition;
	}

	@Override
	public boolean isLineError() {
		return true;
	}

	@Override
	public Severity getSeverity() {
		return severity;
	}

	@Override
	public Object[] getParameters() {
		return parameters;
	}
	
}
