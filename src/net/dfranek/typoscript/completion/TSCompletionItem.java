/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.dfranek.typoscript.completion;

import java.util.Collections;
import java.util.Set;
import javax.swing.ImageIcon;
import org.netbeans.modules.csl.api.CompletionProposal;
import org.netbeans.modules.csl.api.ElementHandle;
import org.netbeans.modules.csl.api.ElementKind;
import org.netbeans.modules.csl.api.HtmlFormatter;
import org.netbeans.modules.csl.api.Modifier;
import org.openide.util.ImageUtilities;

/**
 *
 * @author daniel
 */
public class TSCompletionItem implements CompletionProposal {
	private int anchor;
	private String name;
	private ElementKind kind;
	
	public TSCompletionItem(int anchor, String name, ElementKind kind) {
		this.anchor = anchor;
		this.name = name;
		this.kind = kind;
	}
	
	@Override
	public int getAnchorOffset() {
		return anchor;
	}

	@Override
	public ElementHandle getElement() {
		return new TSElement(getName());
	}

	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public String getInsertPrefix() {
		return getName();
	}

	@Override
	public String getSortText() {
		return getName();
	}

	@Override
	public String getLhsHtml(HtmlFormatter hf) {
		return getName();
	}

	@Override
	public String getRhsHtml(HtmlFormatter hf) {
		return this.kind.toString();
	}

	@Override
	public ElementKind getKind() {
		return this.kind;
	}

	@Override
	public ImageIcon getIcon() {
		return new ImageIcon(ImageUtilities.loadImage("net/dfranek/typoscript/resources/ts.png"));
	}

	@Override
	public Set<Modifier> getModifiers() {
		return Collections.emptySet();
	}

	@Override
	public boolean isSmart() {
		return true;
	}

	@Override
	public int getSortPrioOverride() {
		return 0;
	}

	@Override
	public String getCustomInsertTemplate() {
		return null;
	}
	
}
