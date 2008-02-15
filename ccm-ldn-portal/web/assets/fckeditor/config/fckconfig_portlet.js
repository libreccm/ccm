/*
 * FCKeditor - The text editor for internet
 * Copyright (C) 2003-2005 Frederico Caldeira Knabben
 * 
 * Custom amendments for freeform html portlet
 */

FCKConfig.FillEmptyBlocks	= true ;

FCKConfig.ToolbarSets["Basic"] = [
	['Source','-','Preview','-'],
	['Bold','Italic','Underline','StrikeThrough','-','Subscript','Superscript'],
	['Cut','Copy','Paste','PasteText','PasteWord','-','Print'],
	['OrderedList','UnorderedList','-','Outdent','Indent'],			
	['Link','Unlink'],
	['Undo','Redo','-','SelectAll'],	
	['TextColor','BGColor'],
	['Rule','SpecialChar'],
	['FontFormat','FontName'],
	['FontSize']		
] ;

//only licenced for personal use
//FCKConfig.SpellChecker			= 'ieSpell' ;	// 'ieSpell' | 'SpellerPages'
//FCKConfig.IeSpellDownloadUrl	= 'http://www.iespell.com/rel/ieSpellSetup211325.exe' ;

FCKConfig.LinkBrowser = false ;
FCKConfig.LinkBrowserURL = FCKConfig.BasePath + 'filemanager/browser/default/browser.html?Connector=connectors/' + _FileBrowserLanguage + '/connector.' + _FileBrowserExtension ;

FCKConfig.SmileyImages	= ['regular_smile.gif','sad_smile.gif','wink_smile.gif','teeth_smile.gif','confused_smile.gif','tounge_smile.gif','embaressed_smile.gif','omg_smile.gif','whatchutalkingabout_smile.gif','angry_smile.gif','angel_smile.gif','shades_smile.gif','devil_smile.gif','cry_smile.gif','lightbulb.gif','thumbs_down.gif','thumbs_up.gif','heart.gif','broken_heart.gif','kiss.gif','envelope.gif'] ;
