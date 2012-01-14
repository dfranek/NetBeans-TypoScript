/*
 *    Project:     {project.displayName} - {project.name}
 *    Version:    1.0.0
 *    Date:        ${date} ${time}
 *    Auhor:      ${user} 
 *
 *    Coded with Netbeans!
 */


#| --- Plugin ----------------------------------------------------------------------------------------- |#


#| --- Libs ------------------------------------------------------------------------------------------- |#


#| --- tt_content ------------------------------------------------------------------------------------- |#


#| --- Code Cleanup ----------------------------------------------------------------------------------- |#
lib.parseFunc_RTE.nonTypoTagStdWrap.encapsLines.addAttributes.P.class = 


#| --- System & Page Config --------------------------------------------------------------------------- |#
config {
	linkVars = L(0-3) 
	uniqueLinkVars = 1
	language = de
	locale_all = de_DE
	sys_language_uid = 0
	sys_language_mode = content_fallback
	meaningfulTempFilePrefix = 64
}
[globalVar = GP:L = 1]
	config {
		sys_language_uid = 1
		language = en
		locale_all = en_EN
	}
[global]
[globalVar = GP:L = 2]
	config {
		sys_language_uid = 2
		language = es
		locale_all = es_ES 
	}
[global]
[globalVar = GP:L = 3]
	config {
		sys_language_uid = 3
		language = fr
		locale_all = fr_FR
	}
[global]

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

