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

/**
 *
 * @author Daniel Franek
 */
public enum TSASTNodeType {
	// Top Level Objects
	PLUGIN,
	CONFIG,
	CONSTANTS,
	PAGE,
	FE_DATA,
	FE_TABLE,
	FRAMESET,
	FRAME,
	META,
	CARRAY,
	
	// Graphic Functions
	GIFBUILDER,
	GIFBUILDER_TEXT,
	GIFBUILDER_IMAGE,
	SHADOW,
	EMBOSS,
	OUTLINE,
	BOX,
	EFFECT,
	WORKAREA,
	CROP,
	SCALE,
	ADJUST,
	IMGMAP,
	
	// Menu Objects
	GMENU,
	GMENU_LAYERS,
	TMENU,
	TMENU_LAYERS,
	GMENU_FOLDOUT,
	TMENUITEM,
	IMGMENU,
	IMGMENUITEM,
	JSMENU,
	JSMENUITEM,
	
	// Content Objects
	HTML,
	TEXT,
	COBJ_ARRAY,
	COA,
	COA_INT,
	FILE,
	IMAGE,
	IMG_RESOURCE,
	CLEARGIF,
	CONTENT,
	RECORDS,
	HMENU,
	CTABLE,
	OTABLE,
	COLUMNS,
	HRULER,
	IMGTEXT,
	CASE,
	LOAD_REGISTER,
	RESTORE_REGISTER,
	FORM,
	SEARCHRESULT,
	USER,
	USER_INT,
	PHP_SCRIPT,
	PHP_SCRIPT_INT,
	PHP_SCRIPT_EXT,
	TEMPLATE,
	MULTIMEDIA,
	EDITPANEL,
	STDWRAP,
	
	// Relevant for Parsing
	UNKNOWN,
	VALUE,
	ROOTLEVEL,
	COPIED_PROPERTY,
	CLEARED_PROPERY,
	CONDITION;
	
	public static TSASTNodeType getNodeTypeForObject(String name) {
		for (TSASTNodeType type : TSASTNodeType.values()) {
			if(type.name().equals(name)){
				return type;
			}
			if(name.equals("config")) {
				return TSASTNodeType.CONFIG;
			}
			if (name.equals("stdWrap")) {
				return TSASTNodeType.STDWRAP;
			}
		}
		return TSASTNodeType.UNKNOWN;
	}
	
}
