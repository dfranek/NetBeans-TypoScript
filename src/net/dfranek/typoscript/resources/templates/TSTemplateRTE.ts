/*
 *    Project:	${project.displayName} - ${project.name}
 *    Version:	1.0.0
 *    Date:		${date} ${time}
 *    Author:	${user} 
 *
 *    Coded with Netbeans!
 */

# RTE_for_all_users
setup.default.edit_RTE = 1

RTE.default {
	# Markup_Options_(Word_cleanup_etc.)
	//enableWordClean = 1
	//removeTrailingBR = 1
	//removeComments = 1
	//removeTags = sdfield 
	//blindImageOptions = magic,plain,dragdrop
	//disablePCexamples = 1
	//disableTYPO3Browsers = 1
	 
	# Show_Hide_Color_Picker
	disableColorPicker = 1
	    
	# Show_Hide_Statusbar_of_HTMLarea
	//showStatusBar =  0
	
	# Show_Hide_Table_Options_Buttons
	//hideTableOperationsInToolbar = 0
	
	# Options_for_Table_Editing_(cellspacing_cellpadding_border)
	//disableSpacingFieldsetInTableOperations = 0
	//disableAlignmentFieldsetInTableOperations = 0
	//disableColorFieldsetInTableOperations = 0
	//disableLayoutFieldsetInTableOperations = 0
	//disableBordersFieldsetInTableOperations = 0     
	     
	# Show_Hide_Buttons
	showButtons = formatblock, blockstyle, headline, textstylelabel,  blockstylelabel, bold, italic, underline, unorderedlist, insertcharacter, link, image, removeformat, table, toggleborders, tableproperties, rowproperties, rowinsertabove, rowinsertunder, rowdelete, rowsplit, columninsertbefore, columninsertafter, columndelete, columnsplit, cellproperties, cellinsertbefore, cellinsertafter, celldelete, cellsplit, cellmerge, findreplace, insertcharacter, undo, redo, chMode, showhelp,
	hideButtons = fontstyle, fontsize, size, textstyle strikethrough,lefttoright, righttoleft, textindicator, emoticon, user, spellcheck, inserttag, outdent, indent, justifyfull, subscript, superscript, acronym, copy, cut, paste, about, textcolor, left, center, right, orderedlist, line;
	 
	# Show_Hide_additional_Buttons
	//showButtons := addToList(textcolor, bgcolor, size, fontsize )
	 
	# Sorting_of_Buttons
	toolbarOrder = headline,textstyle,formatblock,blockstylelabel, blockstyle, bold, italic, underline, orderedlist, unorderedlist, findreplace, insertcharacter, undo, redo, showhelp, textstylelabel, image, link,  line, table, toggleborders, tableproperties,rowproperties, rowinsertabove, rowinsertunder, rowdelete, rowsplit, columninsertbefore, columninsertafter, columndelete, columnsplit, cellproperties, cellinsertbefore, cellinsertafter, celldelete, cellsplit, cellmerge, textcolor,chMode,removeformat,about
	 
	# Grouping_of_Buttons
	//keepButtonGroupTogether = 1
	//keepToggleBordersInToolbar = 1
}


# Setup_RTE
RTE.default {
	# RTE_CSS_to_use  
	contentCSS = fileadmin/templates/rte.css
	
	# Show_all_Classes  
	showTagFreeClasses = 1
	
	# Overwrite_RTE_Styles_and_use_Custom_Styles
	ignoreMainStyleOverride = 1

	# Definitions_of_Classes_for_Anchors_Characters_Images_Paragraphs_Tables
	classesAnchor = YourClassname1, YourClassname2 
	classesCharacter = YourClassname1, YourClassname2
	classesImage = YourClassname1, YourClassname2
	classesParagraph = YourClassname1, YourClassname2
	classesTable = YourClassname1, YourClassname2
	classesTR = YourClassname1, YourClassname2
	classesTH = YourClassname1, YourClassname2
	classesTD = YourClassname1, YourClassname2
}


# Allowed_span_Classes
RTE.config.tt_content.bodytext.proc.allowedClasses = author


RTE.default.proc {
	# Overwrite_TypoScript_Styles_and_use_Custom_Styles
	overruleMode = ts_css
	
	# Allowed_Denied_Tags
	allowTags = table, tbody, tr, th, td, h1, h2, h3, h4, h5, h6, div, p, br, span, ul, ol, li, re, blockquote, strong, em, b, i, u, sub, sup, strike, a, img, nobr, hr, tt, q, cite, abbr, acronym, center, bgcolor, bg, color, underline
	denyTags = font 
	
	# Don't_convert_BR_to_P
	dontConvBRtoParagraph = 1
	
	# Allowed_outside_of_P_and_DIV
	allowTagsOutside = img,hr,center
	
	# Allowed_Attributes_of_P_and_DIV
	keepPDIVattribs = class,style,id
}

