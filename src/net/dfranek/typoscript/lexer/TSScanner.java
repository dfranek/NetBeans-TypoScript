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
package net.dfranek.typoscript.lexer;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.TreeSet;
import java.util.regex.Pattern;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.*;
import org.netbeans.spi.lexer.LexerInput;
import org.netbeans.spi.lexer.LexerRestartInfo;
import org.openide.util.Exceptions;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class TSScanner {

	private LexerInput input;
	private int readLength = 0;
	private TSLexerState state;
	private XPath xpath;
	
	private Document doc;
	/**
	 * Contructor. Sets input and current state.
	 *
	 * @param info information about current document
	 */
	public TSScanner(LexerRestartInfo<TSTokenId> info) {
		this.input = info.input();
		if (info.state() != null) {
			this.state = (TSLexerState) info.state();
		} else {
			this.state = TSLexerState.DEFAULT;
		}
		
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setNamespaceAware(true);
		try {
			DocumentBuilder builder = factory.newDocumentBuilder();
			doc = builder.parse(TSScanner.class.getResource("/net/dfranek/typoscript/resources/properties.xml").toString());
		} catch (ParserConfigurationException ex) {
			Exceptions.printStackTrace(ex);
		} catch (SAXException ex) {
			Exceptions.printStackTrace(ex);
		} catch (IOException ex) {
			Exceptions.printStackTrace(ex);
		}
		XPathFactory xpFactory = XPathFactory.newInstance();
		xpath = xpFactory.newXPath();
	}

	/**
	 * Resumes scanning until the next regular expression is matched, the end of
	 * input is encountered or an I/O-Error occurs.
	 *
	 * @return the next token
	 * @exception java.io.IOException if any I/O-Error occurs
	 */
	public TSTokenId nextToken() throws java.io.IOException {
		TSTokenId token = TSTokenId.UNKNOWN_TOKEN;

		//input.backup(1);
		char ch = (char) input.read();

		if (state == TSLexerState.IN_PARANTHESE && ch != ')') {
			token = readWhileInParanthese();
		} else if (ch == '\n') {
			token = TSTokenId.TS_NL;
			state = TSLexerState.DEFAULT;
		} else if (state != TSLexerState.IN_VALUE && state == TSLexerState.IN_COMMENT) {
			token = readMultilineComment(ch);
		} else if (isWhiteSpace(ch)) {
			nextWhileWhiteSpace();
			token = TSTokenId.WHITESPACE;
		} else if (state != TSLexerState.IN_VALUE && (ch == '"' || ch == '\'')) {
			nextUntilUnescaped(ch);
			token = TSTokenId.TS_STRING;
		} else if (((ch == '<' || ch == '>' || (ch == '=' && (char) input.read() != '<')) && (char) input.read() != '\n') && state != TSLexerState.IN_VALUE) { // there must be some value behind the operator!
			state = TSLexerState.IN_VALUE;
			token = TSTokenId.TS_OPERATOR;
			input.backup(1);
			if (ch == '=') {
				input.backup(1);
			}
		} else if (state != TSLexerState.IN_VALUE && ch == '[') {
			nextUntilUnescaped(']');
			token = TSTokenId.TS_CONDITION;
			// with punctuation, the type of the token is the symbol itself
		} else if (state != TSLexerState.IN_VALUE && ch == ')') {
			char next = (char) input.read();
			if (next == '\n') {
				token = TSTokenId.TS_PARANTHESE;
				state = TSLexerState.DEFAULT;
			} else {
				token = TSTokenId.TS_VALUE;
			}
			input.backup(1);
		} else if (state != TSLexerState.IN_VALUE && ch == '(') {
			token = TSTokenId.TS_PARANTHESE;
			state = TSLexerState.IN_PARANTHESE;
		} else if (state != TSLexerState.IN_VALUE && Pattern.matches("[\\[\\]\\(\\),;\\:\\.\\<\\>\\=]", new Character(ch).toString())) {
			token = TSTokenId.TS_OPERATOR;
		} else if (state != TSLexerState.IN_VALUE && (ch == '{' || ch == '}')) {
			token = TSTokenId.TS_CURLY;
		} else if (state != TSLexerState.IN_VALUE && ch == '0' && (input.read() == 'x' || input.read() == 'X')) {
			token = readHexNumber();
		} else if (state != TSLexerState.IN_VALUE && isDigit(new Character(ch).toString())) {
			token = readNumber();
		} else if (state != TSLexerState.IN_VALUE && ch == '/') {
			char next = (char) input.read();

			if (next == '*') {
				token = readMultilineComment(ch);

			} else if (next == '/') {
				nextUntilUnescaped('\n');
				token = TSTokenId.TS_COMMENT;

			} else if (state == TSLexerState.IN_REGEXP) {
				token = readRegexp();

			} else {
				nextWhileOperatorChar();
				token = TSTokenId.TS_OPERATOR;
			}

		} else if (state != TSLexerState.IN_VALUE && ch == '#') {
			nextUntilUnescaped('\n');
			token = TSTokenId.TS_COMMENT;
		} else if (state != TSLexerState.IN_VALUE && isOperatorChar(new Character(ch).toString())) {
			nextWhileOperatorChar();
			token = TSTokenId.TS_OPERATOR;
		} else {
			String word = nextWhileWordChar(ch);
			token = TSTokenId.TS_PROPERTY;
			try {
				XPathExpression expr = xpath.compile("//property[@name='"+word+"']");
				NodeList nodes = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);
				if (nodes.getLength() > 0) {
					Node node = nodes.item(0).getAttributes().getNamedItem("type");
					String propertyType = node.getNodeValue();
					if (propertyType.equals("object")) {
						token = TSTokenId.TS_OBJECT;
					}
				}
			} catch (XPathExpressionException ex) {
				Exceptions.printStackTrace(ex);
			}
			
			
			if (TSScannerKeyWords.keywords.contains(word)) {
				token = TSTokenId.TS_KEYWORD;
			} else if (TSScannerKeyWords.keywords2.contains(word)) {
				token = TSTokenId.TS_KEYWORD2;
			} else if (TSScannerKeyWords.keywords3.contains(word)) {
				token = TSTokenId.TS_KEYWORD3;
			} else if (TSScannerKeyWords.reservedWord.contains(word)) {
				token = TSTokenId.TS_RESERVED;
			} else if (state == TSLexerState.IN_VALUE) {
				token = TSTokenId.TS_VALUE;
			}
		}

		this.readLength = input.readLength();
		return token;
	}

	protected TSTokenId readRegexp() {
		nextUntilUnescaped('/');
		nextWhileMatchesRegExp("[gi]");
		return TSTokenId.TS_REGEXP;
	}

	protected String nextWhileWordChar(char ch) {
		// Backing up 1 position to fix bug
		if (ch == '>') {
			input.backup(1);
		}
		if (ch == '=') {
			input.backup(2);
		}

		StringBuilder sb = new StringBuilder();
		char next;
		while (((next = (char) input.read()) != LexerInput.EOF) && next != '\n' && isWordChar(new Character(next).toString())) {
			sb.append(next);
		}
		input.backup(1);

		return input.readText().toString();
	}

	protected String nextWhileMatchesRegExp(String pattern) {
		char next;
		while (((next = (char) input.read()) != LexerInput.EOF) && Pattern.matches(pattern, new Character(next).toString())) {
		}
		input.backup(1);
		return input.readText().toString();
	}

	protected TSTokenId readNumber() {
		nextWhileDigit();
		return TSTokenId.TS_NUMBER;
	}

	protected TSTokenId readHexNumber() {
		input.read();
		// skip the 'x'
		nextWhileHexDigit();

		return TSTokenId.TS_NUMBER;
	}

	protected String nextWhileOperatorChar() {
		char next;
		while (((next = (char) input.read()) != LexerInput.EOF) && isOperatorChar(new Character(next).toString())) {
		}
		input.backup(1);
		return input.readText().toString();
	}

	protected String nextWhileDigit() {
		char next;
		while (((next = (char) input.read()) != LexerInput.EOF) && isDigit(new Character(next).toString())) {
		}
		input.backup(1);
		return input.readText().toString();
	}

	protected String nextWhileHexDigit() {
		char next;
		while (((next = (char) input.read()) != LexerInput.EOF) && isHexDigit(new Character(next).toString())) {
		}
		return input.readText().toString();
	}

	protected String nextWhileWhiteSpace() {
		char next;
		while (((next = (char) input.read()) != LexerInput.EOF) && isWhiteSpace(next)) {
		}
		input.backup(1);
		return input.readText().toString();
	}

	protected String nextUntilUnescaped(char end) {
		boolean escaped = false;
		char next = (char) input.read();
		while (((next = (char) input.read()) != LexerInput.EOF) && next != '\n' && next != '\uffff') {
			if (next == end && !escaped) {
				break;
			}
			escaped = next == '\\';
		}
		if (next == '\n') {
			input.backup(1);
		}
		return input.readText().toString();
	}

	protected TSTokenId readWhileInParanthese() {
		char next;
		while (((next = (char) input.read()) != LexerInput.EOF) && next != ')' && next != '\uffff') {
		}
		input.backup(1);

		return TSTokenId.TS_VALUE;
	}

	protected TSTokenId readMultilineComment(char start) {
		state = TSLexerState.IN_COMMENT;
		boolean maybeEnd = (start == '*');
		while (true) {
			char next = (char) input.read();
			if (next == '\n') {
				break;
			}

			if (next == '/' && maybeEnd) {
				state = TSLexerState.DEFAULT;
				break;
			}
			maybeEnd = (next == '*');
		}

		return TSTokenId.TS_COMMENT;
	}

	protected boolean isOperatorChar(String input) {
		return Pattern.matches("[\\+\\-\\*\\&\\%\\/=<>!\\?]", input);
	}

	protected boolean isDigit(String input) {
		return Pattern.matches("[0-9]+", input);
	}

	protected boolean isHexDigit(String input) {
		return Pattern.matches("[0-9A-Fa-f]", input);
	}

	protected boolean isWordChar(String input) {
		return Pattern.matches("[\\w\\$_{}]", input);
	}

	protected boolean isWhiteSpace(char ch) {
		return ch != '\n' && (ch == ' ' || Pattern.matches("\\s", new Character(ch).toString()));
	}

	/**
	 * Returns the length that has been read by nextToken()
	 *
	 * @return the readLength
	 */
	public int getReadLength() {
		return readLength;
	}

	/**
	 * Returns the current state of the scanner
	 *
	 * @return the current state
	 */
	public TSLexerState getState() {
		return state;
	}

	public static final class TSScannerKeyWords {

		public static final Collection<String> keywords3 = new TreeSet<String>(
				Arrays.asList(
				"ACT",
				"ACTIFSUB",
				"ACTIFSUBRO",
				"ACTRO",
				"all",
				"arrowACT",
				"arrowNO",
				"ascii",
				"atLeast",
				"atMost",
				"BE",
				"be_groups",
				"be_users",
				"BOX",
				"browse",
				"bullets",
				"CUR",
				"CURIFSUB",
				"CURIFSUBRO",
				"CURRO",
				"default",
				"description",
				"directory",
				"directReturn",
				"div",
				"else",
				"email",
				"end",
				"equals",
				"external",
				"false",
				"FE",
				"fe_groups",
				"fe_users",
				"feadmin",
				"header",
				"html",
				"id",
				"if",
				"ifEmpty",
				"IFSUB",
				"IFSUBRO",
				"image",
				"inBranch",
				"isFalse",
				"isGreaterThan",
				"isInList",
				"isLessThan",
				"isPositive",
				"isTrue",
				"language",
				"leveltitle",
				"list",
				"login",
				"mailform",
				"media",
				"menu",
				"mod",
				"multimedia",
				"negate",
				"NEW",
				"NO",
				"none",
				"pages",
				"pages_language_overlay",
				"parseFunc_RTE",
				"pid",
				"required",
				"RO",
				"rootline",
				"script",
				"search",
				"shortcut",
				"sitemap",
				"SPC",
				"splash",
				"sys_dmail",
				"sys_domain",
				"sys_filemounts",
				"sys_note",
				"sys_template",
				"tabel",
				"text",
				"textpic",
				"this",
				"top",
				"true",
				"twice",
				"uid",
				"uniqueGlobal",
				"uniqueLocal",
				"unsetEmpty",
				"updated",
				"uploads",
				"us",
				"user_task",
				"USERDEF1",
				"USERDEF1RO",
				"USERDEF2",
				"USERDEF2RO",
				"usergroup",
				"USR",
				"USRRO",
				"web_func",
				"web_info",
				"web_layout",
				"web_list",
				"web_ts",
				"xhtml_strict",
				"xhtml_trans",
				"XY",
				"ypMenu"));
		public static final Collection<String> keywords2 = new TreeSet<String>(
				Arrays.asList(
				"admPanel",
				"alt_print",
				"auth",
				"browser",
				"cache",
				"CHECK",
				"cObj",
				"cObject",
				"COMMENT",
				"config",
				"content",
				"copy",
				"cut",
				"dataArray",
				"dayofmonth",
				"dayofweek",
				"db_list",
				"device",
				"dynCSS",
				"edit",
				"edit_access",
				"edit_pageheader",
				"folder",
				"folderTree",
				"foldoutMenu",
				"Functions",
				"gmenu_foldout",
				"gmenu_layers",
				"hostname",
				"hour",
				"imgList",
				"imgResource",
				"imgText",
				"info",
				"IP",
				"jsmenu",
				"JSwindow",
				"LABEL",
				"layout",
				"lib",
				"loginUser",
				"marks",
				"minute",
				"mod",
				"module",
				"month",
				"move_wizard",
				"new",
				"new_wizard",
				"noResultObj",
				"numRows",
				"options",
				"page",
				"pageTree",
				"paste",
				"perms",
				"PIDinRootline",
				"PIDupinRootline",
				"plugin",
				"postform",
				"postform_newThread",
				"preview",
				"publish",
				"RADIO",
				"renderObj",
				"REQ",
				"RTE",
				"RTE_compliant",
				"select",
				"setup",
				"split",
				"stat",
				"stat_apache",
				"stat_apache_logfile",
				"stat_apache_noHost",
				"stat_apache_notExtended",
				"stat_apache_pagenames",
				"stat_excludeBEuserHits",
				"stat_excludeIPList",
				"stat_mysql",
				"stat_titleLen",
				"stat_typeNumList",
				"stdWrap",
				"subparts",
				"system",
				"temp",
				"template",
				"treeLevel",
				"tsdebug",
				"typolink",
				"url",
				"useragent",
				"userFunc",
				"version",
				"view",
				"workOnSubpart"));
		public static final Collection<String> reservedWord = new TreeSet<String>(
				Arrays.asList(
				"_offset",
				"absRefPrefix",
				"accessibility",
				"accessKey",
				"addAttributes",
				"addExtUrlsAndShortCuts",
				"addItems",
				"additionalHeaders",
				"additionalParams",
				"addParams",
				"addQueryString",
				"adjustItemsH",
				"adjustSubItemsH",
				"adminPanelStyles",
				"after",
				"afterImg",
				"afterImgLink",
				"afterImgTagParams",
				"afterROImg",
				"afterWrap",
				"age",
				"alertPopups",
				"align",
				"allow",
				"allowCaching",
				"allowedAttribs",
				"allowedClasses",
				"allowedCols",
				"allowEdit",
				"allowedNewTables",
				"allowNew",
				"allowTags",
				"allowTVlisting",
				"allSaveFunctions",
				"allStdWrap",
				"allWrap",
				"alternateBgColors",
				"alternativeSortingField",
				"alternativeTempPath",
				"altImgResource",
				"altLabels",
				"altTarget",
				"altText",
				"altUrl",
				"altUrl_noDefaultParams",
				"altWrap",
				"always",
				"alwaysActivePIDlist",
				"alwaysLink",
				"alwaysShowClickMenuInTopFrame",
				"andWhere",
				"angle",
				"antiAlias",
				"append",
				"applyTotalH",
				"applyTotalW",
				"archive",
				"archiveTypoLink",
				"arrayReturnMode",
				"arrowACT",
				"arrowImgParams",
				"arrowNO",
				"ATagAfterWrap",
				"ATagBeforeWrap",
				"ATagParams",
				"ATagTitle",
				"attribute",
				"autoInsertPID",
				"autoLevels",
				"autonumber",
				"backColor",
				"background",
				"badMess",
				"baseURL",
				"before",
				"beforeImg",
				"beforeImgLink",
				"beforeImgTagParams",
				"beforeROImg",
				"beforeWrap",
				"begin",
				"beLoginLinkIPList",
				"beLoginLinkIPList_login",
				"beLoginLinkIPList_logout",
				"bgCol",
				"bgImg",
				"blankStrEqFalse",
				"blur",
				"bm",
				"bodyTag",
				"bodyTagAdd",
				"bodyTagCObject",
				"bodyTagMargins",
				"bodytext",
				"border",
				"borderCol",
				"bordersWithin",
				"borderThick",
				"bottomBackColor",
				"bottomContent",
				"bottomHeight",
				"bottomImg",
				"bottomImg_mask",
				"br",
				"brTag",
				"bullet",
				"bulletlist",
				"bytes",
				"cache_clearAtMidnight",
				"cache_period",
				"caption",
				"caption_stdWrap",
				"captionAlign",
				"captionHeader",
				"captionSplit",
				"case",
				"casesensitiveComp",
				"cellpadding",
				"cellspacing",
				"centerImgACT",
				"centerImgCUR",
				"centerImgNO",
				"centerLeftImgACT",
				"centerLeftImgCUR",
				"centerLeftImgNO",
				"centerRightImgACT",
				"centerRightImgCUR",
				"centerRightImgNO",
				"char",
				"charcoal",
				"charMapConfig",
				"check",
				"class",
				"classesAnchor",
				"classesCharacter",
				"classesImage",
				"classesParagraph",
				"classicPageEditMode",
				"clear",
				"clearCache",
				"clearCache_disable",
				"clearCache_pageGrandParent",
				"clearCache_pageSiblingChildren",
				"clearCacheCmd",
				"clearCacheLevels",
				"clearCacheOfPages",
				"clickMenuTimeOut",
				"clickTitleMode",
				"clipboardNumberPads",
				"cMargins",
				"cObjNum",
				"collapse",
				"color",
				"color1",
				"color2",
				"color3",
				"color4",
				"colors",
				"colour",
				"colPos_list",
				"colRelations",
				"cols",
				"colSpace",
				"comment_auto",
				"commentWrap",
				"compensateFieldWidth",
				"compX",
				"compY",
				"condensedMode",
				"conf",
				"constants",
				"content_from_pid_allowOutsideDomain",
				"contextMenu",
				"copyLevels",
				"count_HMENU_MENUOBJ",
				"count_menuItems",
				"count_MENUOBJ",
				"create",
				"createFoldersInEB",
				"crop",
				"csConv",
				"CSS_inlineStyle",
				"current",
				"curUid",
				"cWidth",
				"data",
				"dataWrap",
				"date",
				"date_stdWrap",
				"datePrefix",
				"debug",
				"debugData",
				"debugFunc",
				"debugItemConf",
				"debugRenumberedObject",
				"default",
				"defaultAlign",
				"defaultCmd",
				"defaultFileUploads",
				"defaultHeaderType",
				"defaultOutput",
				"defaults",
				"defaultType",
				"delete",
				"denyTags",
				"depth",
				"DESC",
				"dimensions",
				"directionLeft",
				"directionUp",
				"disableAdvanced",
				"disableAllHeaderCode",
				"disableAltText",
				"disableBigButtons",
				"disableCacheSelector",
				"disableCharsetHeader",
				"disableCMlayers",
				"disabled",
				"disableDelete",
				"disableDocModuleInAB",
				"disableDocSelector",
				"disableHideAtCopy",
				"disableIconLinkToContextmenu",
				"disableItems",
				"disableNewContentElementWizard",
				"disableNoMatchingValueElement",
				"disablePageExternalUrl",
				"disablePrefixComment",
				"disablePrependAtCopy",
				"disableSearchBox",
				"disableSingleTableView",
				"disableTabInTextarea",
				"displayActiveOnLoad",
				"displayContent",
				"displayFieldIcons",
				"displayIcons",
				"displayMessages",
				"displayQueries",
				"displayRecord",
				"displayTimes",
				"distributeX",
				"distributeY",
				"DIV",
				"doctype",
				"doctypeSwitch",
				"doktype",
				"doNotLinkIt",
				"doNotShowLink",
				"doNotStripHTML",
				"dontCheckPid",
				"dontFollowMouse",
				"dontHideOnMouseUp",
				"dontLinkIfSubmenu",
				"dontShowPalettesOnFocusInAB",
				"dontWrapInTable",
				"doubleBrTag",
				"doublePostCheck",
				"dWorkArea",
				"edge",
				"edit_docModuleUpload",
				"edit_docModuleUpload",
				"edit_RTE",
				"edit_showFieldHelp",
				"edit_wideDocument",
				"editFieldsAtATime",
				"editFormsOnPage",
				"editIcons",
				"editNoPopup",
				"editPanel",
				"elements",
				"emailMeAtLogin",
				"emailMess",
				"emboss",
				"enable",
				"encapsLines",
				"encapsLinesStdWrap",
				"encapsTagList",
				"entryLevel",
				"equalH",
				"everybody",
				"excludeDoktypes",
				"excludeUidList",
				"expAll",
				"expand",
				"explode",
				"ext",
				"externalBlocks",
				"extTarget",
				"face",
				"fe_adminLib",
				"field",
				"fieldOrder",
				"fieldRequired",
				"fields",
				"fieldWrap",
				"file",
				"file1",
				"file2",
				"file3",
				"file4",
				"file5",
				"filelink",
				"filelist",
				"firstLabel",
				"firstLabelGeneral",
				"fixAttrib",
				"flip",
				"flop",
				"foldSpeed",
				"foldTimer",
				"fontColor",
				"fontFile",
				"fontOffset",
				"fontSize",
				"fontSizeMultiplicator",
				"fontTag",
				"forceDisplayFieldIcons",
				"forceDisplayIcons",
				"forceNoPopup",
				"forceTemplateParsing",
				"forceTypeValue",
				"format",
				"frame",
				"frameReloadIfNotInFrameset",
				"frameSet",
				"freezeMouseover",
				"ftu",
				"function",
				"gamma",
				"gapBgCol",
				"gapLineCol",
				"gapLineThickness",
				"gapWidth",
				"get",
				"getBorder",
				"getLeft",
				"getRight",
				"globalNesting",
				"goodMess",
				"gray",
				"group",
				"groupBy",
				"groupid",
				"header",
				"header_layout",
				"headerComment",
				"headerData",
				"headerSpace",
				"headTag",
				"height",
				"helpText",
				"hidden",
				"hiddenFields",
				"hide",
				"hideButCreateMap",
				"hideMenuTimer",
				"hideMenuWhenNotOver",
				"hidePStyleItems",
				"hideRecords",
				"hideSubmoduleIcons",
				"highColor",
				"history",
				"hover",
				"hoverStyle",
				"HTMLparser",
				"HTMLparser_tags",
				"htmlSpecialChars",
				"htmlTag_dir",
				"htmlTag_langKey",
				"htmlTag_setParams",
				"http",
				"icon",
				"icon_image_ext_list",
				"icon_link",
				"iconCObject",
				"ifEmpty",
				"image",
				"image_compression",
				"image_effects",
				"image_frames",
				"imageLinkWrap",
				"imagePath",
				"images",
				"imageWrapIfAny",
				"imgList",
				"imgMap",
				"imgMapExtras",
				"imgMax",
				"imgNameNotRandom",
				"imgNamePrefix",
				"imgObjNum",
				"imgParams",
				"imgPath",
				"imgStart",
				"import",
				"inc",
				"includeCSS",
				"includeLibrary",
				"includeNotInMenu",
				"incT3Lib_htmlmail",
				"index",
				"index_descrLgd",
				"index_enable",
				"index_externals",
				"inlineStyle2TempFile",
				"innerStdWrap",
				"innerStdWrap_all",
				"innerWrap",
				"innerWrap2",
				"input",
				"inputLevels",
				"insertClassesFromRTE",
				"insertData",
				"insertDmailerBoundaries",
				"intensity",
				"intTarget",
				"intval",
				"invert",
				"IProcFunc",
				"itemArrayProcFunc",
				"itemH",
				"items",
				"itemsProcFunc",
				"iterations",
				"join",
				"JSWindow",
				"JSwindow_params",
				"jumpurl",
				"jumpUrl",
				"jumpurl_enable",
				"jumpurl_mailto_disable",
				"jumpUrl_transferSession",
				"keep",
				"keepEntries",
				"keepNonMatchedTags",
				"key",
				"label",
				"labelStdWrap",
				"labelWrap",
				"lang",
				"language",
				"language_alt",
				"languageField",
				"layer_menu_id",
				"layerStyle",
				"left",
				"leftIcons",
				"leftImgACT",
				"leftImgCUR",
				"leftImgNO",
				"leftjoin",
				"leftOffset",
				"levels",
				"leveluid",
				"limit",
				"line",
				"lineColor",
				"lineThickness",
				"linkPrefix",
				"linkTitleToSelf",
				"linkVars",
				"linkWrap",
				"listNum",
				"listOnlyInSingleTableView",
				"lm",
				"locale_all",
				"localNesting",
				"locationData",
				"lockFilePath",
				"lockPosition",
				"lockPosition_addSelf",
				"lockPosition_adjust",
				"lockToIP",
				"longdescURL",
				"lowColor",
				"lower",
				"LR",
				"mailto",
				"main",
				"mainScript",
				"makelinks",
				"markerWrap",
				"mask",
				"max",
				"maxAge",
				"maxAgeDays",
				"maxChars",
				"maxH",
				"maxHeight",
				"maxItems",
				"maxW",
				"maxWidth",
				"maxWInText",
				"mayNotCreateEditShortcuts",
				"menu_type",
				"menuBackColor",
				"menuHeight",
				"menuName",
				"menuOffset",
				"menuWidth",
				"message_page_is_being_generated",
				"message_preview",
				"meta",
				"metaCharset",
				"method",
				"min",
				"minH",
				"minItems",
				"minW",
				"mode",
				"moduleMenuCollapsable",
				"MP_defaults",
				"MP_disableTypolinkClosestMPvalue",
				"MP_mapRootPoints",
				"name",
				"navFrameResizable",
				"navFrameWidth",
				"nesting",
				"netprintApplicationLink",
				"neverHideAtCopy",
				"newPageWiz",
				"newRecordFromTable",
				"newWindow",
				"newWizards",
				"next",
				"niceText",
				"nicetext",
				"no_cache",
				"no_search",
				"noAttrib",
				"noBlur",
				"noCache",
				"noCols",
				"noCreateRecordsLink",
				"noLink",
				"noLinkUnderline",
				"noMatchingValue_label",
				"noMenuMode",
				"nonCachedSubst",
				"nonTypoTagStdWrap",
				"nonTypoTagUserFunc",
				"nonWrappedTag",
				"noOrderBy",
				"noPageTitle",
				"noRows",
				"noScaleUp",
				"noStretchAndMarginCells",
				"noThumbsInEB",
				"noThumbsInRTEimageSelect",
				"notification_email_charset",
				"notification_email_encoding",
				"notification_email_urlmode",
				"noTrimWrap",
				"noValueInsert",
				"obj",
				"offset",
				"offsetWrap",
				"onlineWorkspaceInfo",
				"onlyCurrentPid",
				"opacity",
				"orderBy",
				"outerWrap",
				"outline",
				"outputLevels",
				"override",
				"overrideAttribs",
				"overrideEdit",
				"overrideId",
				"overridePageModule",
				"overrideWithExtension",
				"pageFrameObj",
				"pageGenScript",
				"pageTitleFirst",
				"parameter",
				"params",
				"parseFunc",
				"parser",
				"password",
				"path",
				"permissions",
				"pid_list",
				"pidInList",
				"pixelSpaceFontSizeRef",
				"plaintextLib",
				"plainTextStdWrap",
				"postCObject",
				"postLineBlanks",
				"postLineChar",
				"postLineLen",
				"postUserFunc",
				"postUserFuncInt",
				"preBlanks",
				"preCObject",
				"prefix",
				"prefixComment",
				"prefixLocalAnchors",
				"prefixRelPathWith",
				"preIfEmptyListNum",
				"preLineBlanks",
				"preLineChar",
				"preLineLen",
				"prepend",
				"preserveEntities",
				"preUserFunc",
				"prev",
				"previewBorder",
				"prevnextToSection",
				"printheader",
				"prioriCalc",
				"proc",
				"processScript",
				"properties",
				"protect",
				"protectLvar",
				"publish_levels",
				"QEisDefault",
				"quality",
				"radio",
				"radioWrap",
				"range",
				"rawUrlEncode",
				"recipient",
				"recursive",
				"recursiveDelete",
				"redirect",
				"redirectToURL",
				"reduceColors",
				"register",
				"relativeToParentLayer",
				"relativeToTriggerItem",
				"relPathPrefix",
				"remap",
				"remapTag",
				"removeBadHTML",
				"removeDefaultJS",
				"removeIfEquals",
				"removeIfFalse",
				"removeItems",
				"removeObjectsOfDummy",
				"removePrependedNumbers",
				"removeTags",
				"removeWrapping",
				"renderCharset",
				"renderWrap",
				"reset",
				"resources",
				"resultObj",
				"returnLast",
				"returnUrl",
				"rightImgACT",
				"rightImgCUR",
				"rightImgNO",
				"rightjoin",
				"rm",
				"rmTagIfNoAttrib",
				"RO_chBgColor",
				"rotate",
				"rows",
				"rowSpace",
				"RTEfullScreenWidth",
				"rules",
				"sample",
				"saveClipboard",
				"saveDocNew",
				"secondRow",
				"section",
				"sectionIndex",
				"select",
				"select_key",
				"selectFields",
				"separator",
				"set",
				"setContentToCurrent",
				"setCurrent",
				"setfixed",
				"setFixedHeight",
				"setFixedWidth",
				"setJS_mouseOver",
				"setJS_openPic",
				"setOnly",
				"shadow",
				"sharpen",
				"shear",
				"short",
				"shortcut",
				"shortcut_onEditId_dontSetPageTree",
				"shortcut_onEditId_keepExistingExpanded",
				"shortcutFrame",
				"shortcutGroups",
				"shortcutIcon",
				"show",
				"showAccessRestrictedPages",
				"showActive",
				"showClipControlPanelsDespiteOfCMlayers",
				"showFirst",
				"showHiddenPages",
				"showHiddenRecords",
				"showHistory",
				"showPageIdWithTitle",
				"showTagFreeClasses",
				"simulateDate",
				"simulateStaticDocuments",
				"simulateStaticDocuments_addTitle",
				"simulateStaticDocuments_dontRedirectPathInfoError",
				"simulateStaticDocuments_noTypeIfNoTitle",
				"simulateStaticDocuments_pEnc",
				"simulateStaticDocuments_pEnc_onlyP",
				"simulateUserGroup",
				"singlePid",
				"site_author",
				"site_reserved",
				"sitetitle",
				"siteUrl",
				"size",
				"smallFormFields",
				"solarize",
				"sorting",
				"source",
				"space",
				"spaceAfter",
				"spaceBefore",
				"spaceBelowAbove",
				"spaceLeft",
				"spaceRight",
				"spacing",
				"spamProtectEmailAddresses",
				"spamProtectEmailAddresses_atSubst",
				"spamProtectEmailAddresses_lastDotSubst",
				"special",
				"splitChar",
				"splitRendering",
				"src",
				"startInTaskCenter",
				"stayFolded",
				"stdheader",
				"stdWrap",
				"stdWrap2",
				"strftime",
				"stripHtml",
				"styles",
				"stylesheet",
				"submenuObjSuffixes",
				"subMenuOffset",
				"submit",
				"subst_elementUid",
				"substMarksSeparately",
				"substring",
				"swirl",
				"sword",
				"sword_noMixedCase",
				"SWORD_PARAMS",
				"sword_standAlone",
				"sys_language_mode",
				"sys_language_overlay",
				"sys_language_softMergeIfNotBlank",
				"sys_language_uid",
				"table",
				"tableCellColor",
				"tableParams",
				"tables",
				"tableStdWrap",
				"tableStyle",
				"tableWidth",
				"tags",
				"target",
				"TDparams",
				"templateContent",
				"templateFile",
				"text",
				"textarea",
				"textMargin",
				"textMargin_outOfText",
				"textMaxLength",
				"textObjNum",
				"textPos",
				"textStyle",
				"thickness",
				"thumbnailsByDefault",
				"tile",
				"time_stdWrap",
				"tipafriendLib",
				"title",
				"titleLen",
				"titleTagFunction",
				"titleText",
				"tm",
				"token",
				"topOffset",
				"totalWidth",
				"transparentBackground",
				"transparentColor",
				"trim",
				"tsdebug_tree",
				"type",
				"typeNum",
				"types",
				"typolinkCheckRootline",
				"uidInList",
				"unset",
				"uploadFieldsInTopOfEB",
				"uploads",
				"upper",
				"useCacheHash",
				"useLargestItemX",
				"useLargestItemY",
				"user",
				"userdefined",
				"userfunction",
				"userid",
				"userIdColumn",
				"USERNAME_substToken",
				"userProc",
				"value",
				"valueArray",
				"wave",
				"where",
				"width",
				"wiz",
				"wordSpacing",
				"workArea",
				"wrap",
				"wrap1",
				"wrap2",
				"wrap3",
				"wrapAfterTags",
				"wrapAlign",
				"wrapFieldName",
				"wrapItemAndSub",
				"wrapNonWrappedLines",
				"wraps",
				"xhtml_cleaning",
				"xmlprologue",
				"xPosOffset",
				"yPosOffset"));
		public static final Collection<String> keywords = new TreeSet<String>(
				Arrays.asList(
				"_CSS_DEFAULT_STYLE",
				"_DEFAULT_PI_VARS",
				"_GIFBUILDER",
				"_LOCAL_LANG",
				"CARRAY",
				"CASE",
				"CLEARGIF",
				"COA",
				"COA_INT",
				"COBJ_ARRAY",
				"COLUMNS",
				"CONFIG",
				"CONSTANTS",
				"CONTENT",
				"CTABLE",
				"CType",
				"DB",
				"DOCUMENT_BODY",
				"EDITPANEL",
				"EFFECT",
				"FE_DATA",
				"FE_TABLE",
				"FEData",
				"FILE",
				"FORM",
				"FRAME",
				"FRAMESET",
				"GIFBUILDER",
				"global",
				"globalString",
				"globalVar",
				"GMENU",
				"GMENU_FOLDOUT",
				"GMENU_LAYERS",
				"GP",
				"HMENU",
				"HRULER",
				"HTML",
				"IENV",
				"IMAGE",
				"IMG_RESOURCE",
				"IMGMENU",
				"IMGMENUITEM",
				"IMGTEXT",
				"INCLUDE_TYPOSCRIPT",
				"includeLibs",
				"JSMENU",
				"JSMENUITEM",
				"LIT",
				"LOAD_REGISTER",
				"META",
				"MULTIMEDIA",
				"OTABLE",
				"PAGE",
				"PAGE_TARGET",
				"PAGE_TSCONFIG_ID",
				"PAGE_TSCONFIG_IDLIST",
				"PAGE_TSCONFIG_STR",
				"PHP_SCRIPT",
				"PHP_SCRIPT_EXT",
				"PHP_SCRIPT_INT",
				"RECORDS",
				"REMOTE_ADDR",
				"RESTORE_REGISTER",
				"RTE",
				"SEARCHRESULT",
				"SHARED",
				"TCAdefaults",
				"TCEFORM",
				"TCEMAIN",
				"TEMPLATE",
				"TEXT",
				"TMENU",
				"TMENU_LAYERS",
				"TMENUITEM",
				"TSFE",
				"USER",
				"USER_INT",
				"userFunc"));
	}
}
