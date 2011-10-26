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
	public TSCompletionItem(int anchor) {
		this.anchor = anchor;
	}
	
	@Override
	public int getAnchorOffset() {
		return anchor;
	}

	@Override
	public ElementHandle getElement() {
		return null;
	}

	@Override
	public String getName() {
		return "Test";
	}

	@Override
	public String getInsertPrefix() {
		return "testprefix";
	}

	@Override
	public String getSortText() {
		return getName();
	}

	@Override
	public String getLhsHtml(HtmlFormatter hf) {
		return "some test text lhs";
	}

	@Override
	public String getRhsHtml(HtmlFormatter hf) {
		return "some test text rhs";
	}

	@Override
	public ElementKind getKind() {
		return ElementKind.VARIABLE;
	}

	@Override
	public ImageIcon getIcon() {
		return new ImageIcon(ImageUtilities.loadImage("net/dfranek/typoscript/ts.png"));
	}

	@Override
	public Set<Modifier> getModifiers() {
		return Collections.emptySet();
	}

	@Override
	public boolean isSmart() {
		return false;
	}

	@Override
	public int getSortPrioOverride() {
		return 0;
	}

	@Override
	public String getCustomInsertTemplate() {
		return "custom insert template";
	}
	
}
