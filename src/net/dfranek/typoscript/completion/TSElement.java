/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.dfranek.typoscript.completion;

import java.util.Collections;
import java.util.Set;
import net.dfranek.typoscript.TSLanguage;
import org.netbeans.modules.csl.api.ElementHandle;
import org.netbeans.modules.csl.api.ElementKind;
import org.netbeans.modules.csl.api.Modifier;
import org.netbeans.modules.csl.api.OffsetRange;
import org.netbeans.modules.csl.spi.ParserResult;
import org.openide.filesystems.FileObject;

/**
 *
 * @author daniel
 */
public class TSElement implements ElementHandle {
	
	private CharSequence name;

	public TSElement(CharSequence name) {
		this.name = name;
	}

	@Override
	public FileObject getFileObject() {
		return null;
	}

	@Override
	public String getMimeType() {
		return TSLanguage.TS_MIME_TYPE;
	}

	@Override
	public String getName() {
		return name.toString();
	}

	@Override
	public String getIn() {
		return null;
	}

	@Override
	public ElementKind getKind() {
		return ElementKind.VARIABLE;
	}

	@Override
	public Set<Modifier> getModifiers() {
		return Collections.emptySet();
	}

	@Override
	public boolean signatureEquals(ElementHandle eh) {
		return false;
	}

	@Override
	public OffsetRange getOffsetRange(ParserResult pr) {
		return null;
	}
	
}
