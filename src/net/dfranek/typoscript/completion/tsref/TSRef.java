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
package net.dfranek.typoscript.completion.tsref;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.HashMap;
import net.dfranek.typoscript.lexer.TSLexerUtils;

/**
 *
 * @author Daniel Franek
 */
public class TSRef {
	
	protected static HashMap<String, TSRefType> tsref;

	public static void initTSRef() {
		Gson gson = new Gson();
		Type tsrefType = new TypeToken<HashMap<String, TSRefType>>(){}.getType();
		tsref = gson.fromJson(new InputStreamReader(TSLexerUtils.class.getResourceAsStream("/net/dfranek/typoscript/resources/tsref.json")), tsrefType);
	}
	
	public static TSRefType getHelpForType(String type) {
		TSRefType t = null;
		if (tsref.containsKey(type)) {
			t = tsref.get(type);
			t = addBaseType(t);
		}
		
		return t;
	}

	private static TSRefType addBaseType(TSRefType t) {
		if (!t.getExtends().isEmpty()) {
			TSRefType baseType = tsref.get(t.getExtends());
			t.addProperties(baseType.getProperties());
			t.setExtends(baseType.getExtends());
			t = addBaseType(t);
		}
		return t;
	}
}
