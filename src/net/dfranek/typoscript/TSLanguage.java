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
package net.dfranek.typoscript;

import static net.dfranek.typoscript.TSLanguage.TS_MIME_TYPE;
import net.dfranek.typoscript.completion.TSCodeCompletion;
import net.dfranek.typoscript.lexer.TSTokenId;
import net.dfranek.typoscript.nav.TSDeclarationFinder;
import net.dfranek.typoscript.parser.TSParser;
import net.dfranek.typoscript.parser.TSStructureScanner;
import org.netbeans.api.lexer.Language;
import org.netbeans.api.templates.TemplateRegistration;
import org.netbeans.api.templates.TemplateRegistrations;
import org.netbeans.core.spi.multiview.MultiViewElement;
import org.netbeans.core.spi.multiview.text.MultiViewEditorElement;
import org.netbeans.modules.csl.api.CodeCompletionHandler;
import org.netbeans.modules.csl.api.DeclarationFinder;
import org.netbeans.modules.csl.api.StructureScanner;
import org.netbeans.modules.csl.spi.DefaultLanguageConfig;
import org.netbeans.modules.csl.spi.LanguageRegistration;
import org.netbeans.modules.parsing.spi.Parser;
import org.netbeans.modules.parsing.spi.indexing.PathRecognizerRegistration;
import org.openide.WizardDescriptor;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.filesystems.MIMEResolver;
import org.openide.util.Lookup;
import org.openide.windows.TopComponent;

/**
 *
 * @author Daniel Franek
 */

@LanguageRegistration(mimeType=TSLanguage.TS_MIME_TYPE, useMultiview = true)
@PathRecognizerRegistration(mimeTypes = TSLanguage.TS_MIME_TYPE, libraryPathIds = {}, binaryLibraryPathIds = {}) //NOI18N
@ActionReferences({
	@ActionReference(
        path = "Loaders/text/x-typoscript/Actions",
		id = @ActionID(category = "System", id = "org.openide.actions.PropertiesAction"),
		position = 100,
		separatorAfter = 200
	),
	@ActionReference(
        path = "Loaders/text/x-typoscript/Actions",
		id = @ActionID(category = "Edit", id = "org.openide.actions.DeleteAction"),
		position = 300
	),
	@ActionReference(
        path = "Loaders/text/x-typoscript/Actions",
		id = @ActionID(category = "Edit", id = "org.openide.actions.CopyAction"),
		position = 400,
		separatorAfter = 500
	),
	@ActionReference(
        path = "Loaders/text/x-typoscript/Actions",
		id = @ActionID(category = "System", id = "org.openide.actions.FileSystemAction"),
		position = 600
	),
	@ActionReference(
        path = "Loaders/text/x-typoscript/Actions",
		id = @ActionID(category = "System", id = "org.openide.actions.SaveAsTemplateAction"),
		position = 700,
		separatorAfter = 800
	),
	@ActionReference(
        path = "Loaders/text/x-typoscript/Actions",
		id = @ActionID(category = "System", id = "org.openide.actions.ToolsAction"),
		position = 900,
		separatorAfter = 1000
	),
	@ActionReference(
        path = "Loaders/text/x-typoscript/Actions",
		id = @ActionID(category = "System", id = "org.openide.actions.OpenAction"),
		position = 1100,
		separatorAfter = 1200
	),
	@ActionReference(
        path = "Loaders/text/x-typoscript/Actions",
		id = @ActionID(category = "System", id = "org.openide.actions.RenameAction"),
		position = 1400
	),
})
@MIMEResolver.Registration(
    displayName = "#text/x-typoscript",
	resource = "resources/TSResolver.xml"
)
public class TSLanguage extends DefaultLanguageConfig {

	private static final String LINE_COMMENT_PREFIX = "#";//NOI18N
	public static final String TS_MIME_TYPE = "text/x-typoscript";//NOI18N
	
	@MultiViewElement.Registration(
		displayName="#LBL_TSEditorTab",
        iconBase="net/dfranek/typoscript/resources/ts_file_16.png",
        persistenceType=TopComponent.PERSISTENCE_ONLY_OPENED,
        preferredID="ts.source",
        mimeType=TS_MIME_TYPE,
		position=1
	)
    public static MultiViewEditorElement createMultiViewEditorElement(Lookup context) {
        return new MultiViewEditorElement(context);
    }
	
	@TemplateRegistrations({
		@TemplateRegistration(folder = "TypoScript", content = "resources/templates/TSTemplate.ts", description = "resources/templates/TSTemplate.html", displayName = "#Templates/TypoScript/TSTemplate.ts", scriptEngine = "freemarker"),
		@TemplateRegistration(folder = "TypoScript", content = "resources/templates/TSTemplateComments.ts", description = "resources/templates/TSTemplateComments.html", displayName = "#Templates/TypoScript/TSTemplateComments.ts", scriptEngine = "freemarker"),
		@TemplateRegistration(folder = "TypoScript", content = "resources/templates/TSTemplateHTML5Advanced.ts", description = "resources/templates/TSTemplateHTML5Advanced.html", displayName = "#Templates/TypoScript/TSTemplateHTML5Advanced.ts", scriptEngine = "freemarker"),
		@TemplateRegistration(folder = "TypoScript", content = "resources/templates/TSTemplateHTML5Multilingual.ts", description = "resources/templates/TSTemplateHTML5Multilingual.html", displayName = "#Templates/TypoScript/TSTemplateHTML5Multilingual.ts", scriptEngine = "freemarker"),
		@TemplateRegistration(folder = "TypoScript", content = "resources/templates/TSTemplateRTE.ts", description = "resources/templates/TSTemplateRTE.html", displayName = "#Templates/TypoScript/TSTemplateRTE.ts", scriptEngine = "freemarker")
	})
	@SuppressWarnings("rawtypes")
    public static WizardDescriptor.InstantiatingIterator templateIterator() {
        return null;
    }
	
	@Override
	public Language<TSTokenId> getLexerLanguage() {
		return TSTokenId.getLanguage();
	}

	@Override
	public String getDisplayName() {
		return "TypoScript";
	}

	@Override
	public String getLineCommentPrefix() {
		return LINE_COMMENT_PREFIX;
	}

	@Override
	public String getPreferredExtension() {
		return "ts";
	}
	
	
	@Override
	public boolean isIdentifierChar(char c) {
		return Character.isJavaIdentifierPart(c);
	}

	@Override
	public CodeCompletionHandler getCompletionHandler() {
		return new TSCodeCompletion();
	}

	@Override
	public Parser getParser() {
		return new TSParser();
	}

	@Override
	public boolean hasStructureScanner() {
		return true;
	}

	@Override
	public StructureScanner getStructureScanner() {
		return new TSStructureScanner();
	}
	
	@Override
	public DeclarationFinder getDeclarationFinder() {
		return new TSDeclarationFinder();
	}
	
}
