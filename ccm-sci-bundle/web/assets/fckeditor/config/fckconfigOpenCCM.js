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
 * File Name: fckconfigOpenCCM.js
 * 	Editor configuration settings for use in admin interface in APLAWS/OpenCCM. 
 *	This configuration is designed to match the following goals:
 *		- Easy useage of the editor
 *		- Clean HTML output, this means for example that no deprected elements are useable from the editor
 *
 * 	This file is organized in the same way as the page of the FCKEditor documentation showing all config options.
 *	The page can be found here: http://docs.fckeditor.net/FCKeditor_2.x/Developers_Guide/Configuration/Configuration_Options
 * 
 *		- Semantic HTML output. This means that elements like <em> are prefered against such as <b>	
 *  This config is loaded from the Javascript in the calling page using AFTER the
 *  default fckconfig.js is loaded.
 * 
 * File Authors:
 * 		Chris Burnett (orginale fckeditordefaultstyle.js for APLAWS)
 * 		Jens Pelzetter
 */

/* **************************************************************************************************************** */


/* Editor Behavior 
   --------------- */

FCKConfig.AutoDetectPasteFromWord = false ; //Paste Word text directly to the editor (only IE)
FCKConfig.CleanWordKeepsStructure = true ; //Prefer to keep HTML structure, not layout
FCKConfig.DocType = '<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">' ;
FCKConfig.ForcePasteAsPlainText	= true ; //Text pasted in from another app will appear as plain text


/* Styles
   ------ */

FCKConfig.StylesXmlPath = FCKConfig.EditorPath + 'config/fckconfigOpenCCM.xml' ; //Some styles

/* HTML Output
   ----------- */

FCKConfig.FormatOutput = true ; //Format output code
FCKConfig.FormatSource = true ;

/* User interface
   -------------- */

FCKConfig.ContextMenu = ['Generic','Link','Anchor','Image','Flash','Select','Textarea','Checkbox','Radio','TextField','HiddenField','ImageButton','Button','BulletedList','NumberedList','TableCell','Table','Form'] ;
FCKConfig.ImageDlgHideLink	= true ;
FCKConfig.ImageDlgHideAdvanced	= true ;
FCKConfig.ShowDropDialog = true ;
FCKConfig.ToolbarSets["AplawsBasic"] = [
    ['Style','-','OrderedList','UnorderedList','Subscript', 'Superscript', '-','SpellCheck','Link','Unlink','RemoveFormat','-','ImageButton', '-', 'AdvToolbar', 'About']
] ;

FCKConfig.ToolbarSets["AplawsAdvanced"] = [
    ['Style','-','OrderedList','UnorderedList','Subscript', 'Superscript'],
  ['Cut','Copy','Paste','PasteText','PasteWord','Undo','Redo','RemoveFormat'],
  ['Find','Replace','SelectAll', '-','SpellCheck'],
  '/',
  ['Link','Unlink','Anchor','-','Source'],
  ['SimpleToolbar']
] ;


// Replace the Advanced toolbar with AplawsAdvanced & setup the Basic toolbar to use the AplawsBasic toolbar
// The two toolbars are used to support the dynamic toolbar switching plugin.
// Set the Default to be the Basic version so that the editor loads the basic toolbar on startup.

FCKConfig.ToolbarSets["Advanced"] = FCKConfig.ToolbarSets["AplawsAdvanced"] ;
FCKConfig.ToolbarSets["Basic"] = FCKConfig.ToolbarSets["AplawsBasic"] ;
FCKConfig.ToolbarSets["Default"] = FCKConfig.ToolbarSets["Basic"] ;

/* Advanced
   -------- */
FCKConfig.FirefoxSpellChecker = true ;
FCKConfig.SpellChecker			= 'ieSpell' ;	// 'ieSpell' | 'SpellerPages'
FCKConfig.IeSpellDownloadUrl	= 'http://www.iespell.com/rel/ieSpellSetup211325.exe' ;

/* File Browser and Updoader
   ------------------------- */

FCKConfig.LinkBrowser = true ;
FCKConfig.LinkBrowserURL = "/ccm/content/admin/search.jsp?useURL=true&widget=getElementById('txtUrl')" ;
FCKConfig.LinkBrowserWindowWidth    = FCKConfig.ScreenWidth * 0.7 ;     // 70%
FCKConfig.LinkBrowserWindowHeight   = FCKConfig.ScreenHeight * 0.7 ;    // 70%


/*
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
*/

