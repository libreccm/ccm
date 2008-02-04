/*
 * FCKeditor - The text editor for internet
 * Copyright (C) 2003-2005 Frederico Caldeira Knabben
 * 
 * Licensed under the terms of the GNU Lesser General Public License:
 * 		http://www.opensource.org/licenses/lgpl-license.php
 * 
 * For further information visit:
 * 		http://www.fckeditor.net/
 * 
 * File Name: fckconfigstyledefault.js
 * 	Default Editor configuration settings for use in admin interface in APLAWS.
 *  This config is loaded from the Javascript in the calling page using AFTER the
 *  default fckconfig.js is loaded.
 * 
 * File Authors:
 * 		Chris Burnett
 */


FCKConfig.ToolbarSets["AplawsBasic"] = [
  ['Style','-','Bold','OrderedList','UnorderedList','-','SpellCheck','Link','Unlink','RemoveFormat','-','AdvToolbar']
] ;

FCKConfig.ToolbarSets["AplawsAdvanced"] = [
  ['Style','-','Bold','OrderedList','UnorderedList'],
  ['Cut','Copy','Paste','PasteText','PasteWord','Undo','Redo','RemoveFormat'],
  ['Find','Replace','SelectAll', '-','SpellCheck'],
  '/',
  ['UniversalKey'],
  ['Link','Unlink','Anchor','-','Source'],
  ['SimpleToolbar']
] ;


// Replace the Advanced toolbar with AplawsAdvanced & setup the Basic toolbar to use the AplawsBasic toolbar
// The two toolbars are used to support the dynamic toolbar switching plugin.
// Set the Default to be the Basic version so that the editor loads the basic toolbar on startup.

FCKConfig.ToolbarSets["Advanced"] = FCKConfig.ToolbarSets["AplawsAdvanced"] ;
FCKConfig.ToolbarSets["Basic"] = FCKConfig.ToolbarSets["AplawsBasic"] ;
FCKConfig.ToolbarSets["Default"] = FCKConfig.ToolbarSets["Basic"] ;


FCKConfig.StylesXmlPath		= FCKConfig.EditorPath + 'config/fckconfigstyledefault.xml' ;
FCKConfig.ContextMenu = ['Generic','Link','Anchor','Image','Flash','Select','Textarea','Checkbox','Radio','TextField','HiddenField','ImageButton','Button','BulletedList','NumberedList','TableCell','Table','Form'] ;

FCKConfig.SpellChecker			= 'ieSpell' ;	// 'ieSpell' | 'SpellerPages'
FCKConfig.IeSpellDownloadUrl	= 'http://www.iespell.com/rel/ieSpellSetup211325.exe' ;

FCKConfig.ImageDlgHideLink	= true ;
FCKConfig.ImageDlgHideAdvanced	= true ;

FCKConfig.FlashDlgHideAdvanced	= true ;

FCKConfig.LinkBrowser = true ;
FCKConfig.LinkBrowserURL = "/ccm/content/admin/search.jsp?useURL=true&widget=getElementById('txtUrl')" ;
FCKConfig.LinkBrowserWindowWidth    = FCKConfig.ScreenWidth * 0.7 ;     // 70%
FCKConfig.LinkBrowserWindowHeight   = FCKConfig.ScreenHeight * 0.7 ;    // 70%


