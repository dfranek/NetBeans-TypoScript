//
//		Project:	{project.displayName} - {project.name}
//		Version:	1.0.0
//		Date:		${date} ${time}
//		Auhor:	${user}
//
//		Coded with Netbeans!
//

#| --- Plugin ----------------------------------------------------------------------------------------- |#


#| --- Libs ------------------------------------------------------------------------------------------- |#


#| --- tt_content ------------------------------------------------------------------------------------- |#


#| --- Code Cleanup ----------------------------------------------------------------------------------- |#
lib.parseFunc_RTE.nonTypoTagStdWrap.encapsLines.addAttributes.P.class = 


#| --- System & Page Config --------------------------------------------------------------------------- |#
config {
	uniqueLinkVars = 1
	sys_language_uid = 0
	language = de
	locale_all = de_DE
	meaningfulTempFilePrefix = 64
}

page = PAGE
page {
	config {
		doctype = html_5
		disableImgBorderAttr = 1
		disablePrefixComment = 1
		htmlTag_langKey = de-DE 
		meta.language = de
		spamProtectEmailAddresses = 2
		spamProtectEmailAddresses_atSubst = (at)
		spamProtectEmailAddresses_lastDotSubst = (dot) 
		pageTitleFirst = 1
		removeDefaultJS = 1
		inlineStyle2TempFile = 1
		}


#| --- Header & Body ---------------------------------------------------------------------------------- |#
	meta { 
		keywords.field = keywords
		keywords.ifEmpty ( 
			// Enter default keywords of the website here
		)
		description.field = description
		description.ifEmpty ( 
			// Enter default description of the website here
		)
		FLAGS.DC = 0
		robots = 
		author = 
		revisited-after = 14 
		audience = 
		page-topic = 
		MSSmartTagsPreventParsing = true
		imagetoolbar  = false
	}
	includeCSS {
		screen		= fileadmin/
		mobil		   = fileadmin/
		print			= fileadmin/
		print.media = print
	}
	includeJS { 
		jQuery		= fileadmin/
	}
	includeJSFooter {
		tracking		= fileadmin/
	}
}


#| --- Template --------------------------------------------------------------------------------------- |#
page.10 = TEMPLATE
page.10 {
	template = FILE
	workOnSubpart = DOC
	template.file = fileadmin/templates/standard.html
}


#| --- Markers & Subparts ----------------------------------------------------------------------------- |#

