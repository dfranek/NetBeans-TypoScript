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
package net.dfranek.typoscript.parser.ast;

import java.util.Collection;
import java.util.HashMap;

/**
 *
 * @author Daniel Franek
 */
public class TSASTNode {
	private String name;
	private String value;
	private TSASTNodeType type;
	private HashMap<String, TSASTNode> children = new HashMap<String, TSASTNode>();
	private TSASTNode parent;
	
	private int length;
	private int offset;

	public TSASTNode(String name, String value, TSASTNodeType type, int offset, int length) {
		this.name = name;
		this.value = value;
		this.type = type;
		this.length = length;
		this.offset = offset;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	
	public void addChild(TSASTNode node) {
		node.setParent(this);
		children.put(node.getName(), node);
	}
	
	public void removeChild(TSASTNode node) {
		children.remove(node.getName());
	}
	
	public Collection<TSASTNode> getChildren() {
		return children.values();
	}
	
	public boolean hasChild(TSASTNode node) {
		return children.containsKey(node.getName());
	}

	public boolean hasChildren() {
		return !children.isEmpty();
	}
	
	/**
	 * @return the value
	 */
	public String getValue() {
		return value;
	}

	/**
	 * @return the type
	 */
	public TSASTNodeType getType() {
		return type;
	}

	/**
	 * @param value the value to set
	 */
	public void setValue(String value) {
		this.value = value;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(TSASTNodeType type) {
		this.type = type;
	}
	
	public void setParent(TSASTNode node) {
		this.parent = node;
	}
	
	public TSASTNode getParent() {
		return this.parent;
	}

	/**
	 * @return the length
	 */
	public int getLength() {
		return length;
	}

	/**
	 * @return the offset
	 */
	public int getOffset() {
		return offset;
	}

	public TSASTNode getChild(String name) {
		return children.get(name);
	}
	
}
