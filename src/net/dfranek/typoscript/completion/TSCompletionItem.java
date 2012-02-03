/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2011 Daniel Franek.
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
 * @author Daniel Franek
 */
public class TSCompletionItem implements CompletionProposal {

	private int anchor;
	private String name;
	private ElementKind kind;
	private String prefix;

	public TSCompletionItem(int anchor, String name, ElementKind kind, String prefix) {
		this.anchor = anchor;
		this.name = name;
		this.kind = kind;
		this.prefix = prefix;
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
		int indexOf = (this.prefix != null && this.name != null) ? this.name.toLowerCase().indexOf(this.prefix.toLowerCase()) : -1;
		return indexOf > 0 ? this.name.substring(indexOf) : this.name;
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
