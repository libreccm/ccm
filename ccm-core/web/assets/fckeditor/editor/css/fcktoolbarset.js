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
 * "Support Open Source software. What about a donation today?"
 * 
 * File Name: fcktoolbarset.js
 * 	Defines the FCKToolbarSet object that is used to load and draw the 
 * 	toolbar.
 * 
 * File Authors:
 * 		Frederico Caldeira Knabben (fredck@fckeditor.net)
 */

var FCKToolbarSet = FCK.ToolbarSet = new Object() ;

// A map of toolbar sets, key is the name, value is the state of the toolbar set.
// It is used to save and restore states, when the toolbars are switched.
FCKToolbarSet.LoadedSets = new Object();


document.getElementById( 'ExpandHandle' ).title		= FCKLang.ToolbarExpand ;
document.getElementById( 'CollapseHandle' ).title	= FCKLang.ToolbarCollapse ;

FCKToolbarSet.Toolbars = new Array() ;

// Array of toolbat items that are active only on WYSIWYG mode.
FCKToolbarSet.ItemsWysiwygOnly = new Array() ;

// Array of toolbar items that are sensitive to the cursor position.
FCKToolbarSet.ItemsContextSensitive = new Array() ;

FCKToolbarSet.Expand = function()
{
	document.getElementById( 'Collapsed' ).style.display = 'none' ;
	document.getElementById( 'Expanded' ).style.display = '' ;
	
	if ( ! FCKBrowserInfo.IsIE )
	{
		// I had to use "setTimeout" because Gecko was not responding in a right
		// way when calling window.onresize() directly.
		window.setTimeout( "window.onresize()", 1 ) ;
	}
}

FCKToolbarSet.Collapse = function()
{
	document.getElementById( 'Collapsed' ).style.display = '' ;
	document.getElementById( 'Expanded' ).style.display = 'none' ;
	
	if ( ! FCKBrowserInfo.IsIE )
	{
		// I had to use "setTimeout" because Gecko was not responding in a right
		// way when calling window.onresize() directly.
		window.setTimeout( "window.onresize()", 1 ) ;
	}
}

FCKToolbarSet.Restart = function()
{
	if ( !FCKConfig.ToolbarCanCollapse || FCKConfig.ToolbarStartExpanded )
		this.Expand() ;
	else
		this.Collapse() ;
	
	document.getElementById( 'CollapseHandle' ).style.display = FCKConfig.ToolbarCanCollapse ? '' : 'none' ;
}

FCKToolbarSet.Load = function( toolbarSetName )
{
	this.DOMElement = document.getElementById( 'eToolbar' ) ;

	// first, save the current state, if any
	this.SaveCurrentState();
	// try to restore a saved state (if it has been already loaded)
	if (this.RestoreState(toolbarSetName)) {
		// the toolbar set is completely restored...
		return;
	}
	// we have to create a new toolbar set
	this.ToolbarSetName = toolbarSetName;
	this.ItemsWysiwygOnly = new Array();
	this.ItemsContextSensitive = new Array() ;
	
	var ToolbarSet = FCKConfig.ToolbarSets[toolbarSetName] ;
	
	if (! ToolbarSet)
	{
		alert( FCKLang.UnknownToolbarSet.replace( /%1/g, toolbarSetName ) ) ;
		return ;
	}
	
	this.Toolbars = new Array() ;
	
	for ( var x = 0 ; x < ToolbarSet.length ; x++ ) 
	{
		var oToolbarItems = ToolbarSet[x] ;
		
		var oToolbar ;
		
		if ( typeof( oToolbarItems ) == 'string' )
		{
			if ( oToolbarItems == '/' )
				oToolbar = new FCKToolbarBreak() ;
		}
		else
		{
			oToolbar = new FCKToolbar() ;
			
			for ( var j = 0 ; j < oToolbarItems.length ; j++ ) 
			{
				var sItem = oToolbarItems[j] ;
				
				if ( sItem == '-')
					oToolbar.AddSeparator() ;
				else
				{
					var oItem = FCKToolbarItems.GetItem( sItem ) ;
					if ( oItem )
					{
						oToolbar.AddItem( oItem ) ;

						if ( !oItem.SourceView )
							this.ItemsWysiwygOnly[this.ItemsWysiwygOnly.length] = oItem ;
						
						if ( oItem.ContextSensitive )
							this.ItemsContextSensitive[this.ItemsContextSensitive.length] = oItem ;
					}
				}
			}
			
			oToolbar.AddTerminator() ;
		}

		this.Toolbars[ this.Toolbars.length ] = oToolbar ;
	}
	FCKToolbarSet.EnableItems();
}

FCKToolbarSet.RefreshModeState = function()
{
	if ( FCK.EditMode == FCK_EDITMODE_WYSIWYG )
	{
		// Enable all buttons that are available on WYSIWYG mode only.
		for ( var i = 0 ; i < FCKToolbarSet.ItemsWysiwygOnly.length ; i++ )
			FCKToolbarSet.ItemsWysiwygOnly[i].Enable() ;

		// Refresh the buttons state.
		FCKToolbarSet.RefreshItemsState() ;
	}
	else
	{
		// Refresh the buttons state.
		FCKToolbarSet.RefreshItemsState() ;

		// Disable all buttons that are available on WYSIWYG mode only.
		for ( var i = 0 ; i < FCKToolbarSet.ItemsWysiwygOnly.length ; i++ )
			FCKToolbarSet.ItemsWysiwygOnly[i].Disable() ;
	}	
}

FCKToolbarSet.RefreshItemsState = function()
{

	for ( var i = 0 ; i < FCKToolbarSet.ItemsContextSensitive.length ; i++ )
		FCKToolbarSet.ItemsContextSensitive[i].RefreshState() ;
/*
	TODO: Delete this commented block on stable version.
	for ( var i = 0 ; i < FCKToolbarSet.Toolbars.length ; i++ )
	{
		var oToolbar = FCKToolbarSet.Toolbars[i] ;
		for ( var j = 0 ; j < oToolbar.Items.length ; j++ )
		{
			oToolbar.Items[j].RefreshState() ;
		}
	}
*/
}

// Saves the current toolbar set (if any) 
FCKToolbarSet.SaveCurrentState = function()
{
	// Do we have a current toolbar?
	if (this.ToolbarSetName) {
		FCKToolbarSet.DisableItems();
		if (!this.LoadedSets[this.ToolbarSetName]) {
			// Save some toolbar set properties, this can be done only once..
			this.LoadedSets[this.ToolbarSetName] = new Object();
			this.LoadedSets[this.ToolbarSetName].Toolbars = this.Toolbars; 
			this.LoadedSets[this.ToolbarSetName].ItemsWysiwygOnly = this.ItemsWysiwygOnly; 
			this.LoadedSets[this.ToolbarSetName].ItemsContextSensitive = this.ItemsContextSensitive; 
		}
		// move all its elements to a hidden div.
		var oHiddenToolbarsDiv = document.getElementById('hiddenToolbars_' + this.ToolbarSetName);
		if (!oHiddenToolbarsDiv) {
			// create a hidden div with the name of the toolbarset..
			oHiddenToolbarsDiv = document.createElement('DIV');
			oHiddenToolbarsDiv.id = 'hiddenToolbars_' + this.ToolbarSetName;
			// NOTE: We need a hidden div, because some elements should be deinitialized, when the editor is unloaded.
			var oHiddenDiv = document.getElementById('hiddenDiv');
			if (!oHiddenDiv) {
				oHiddenDiv = document.createElement('DIV');
				oHiddenDiv.style.display = 'none';
				oHiddenDiv.style.visibility = 'hidden';
				oHiddenDiv.id = 'hiddenDiv';
				document.body.appendChild(oHiddenDiv);
			}
			oHiddenDiv.appendChild(oHiddenToolbarsDiv);
		}
		var f = true;
		var e = this.DOMElement.firstChild;
		while (e) {
			var oElementToMove = e;
			e = e.nextSibling;
			this.DOMElement.removeChild(oElementToMove);
			oHiddenToolbarsDiv.appendChild(oElementToMove);
		}
	}
}

// Try to restore one (if it has been already saved),
// returns true, if the restoration was succesfull else and false
FCKToolbarSet.RestoreState = function( toolbarSetName )
{
	// Do we have a saved state?
	if (this.LoadedSets[toolbarSetName]) {
		this.ToolbarSetName = toolbarSetName;
		// than simply restore it'state
		this.Toolbars = this.LoadedSets[toolbarSetName].Toolbars;
		this.ItemsWysiwygOnly = this.LoadedSets[toolbarSetName].ItemsWysiwygOnly;
		this.ItemsContextSensitive = this.LoadedSets[toolbarSetName].ItemsContextSensitive;
		// 
		var oHiddenToolbarsDiv = document.getElementById('hiddenToolbars_' + toolbarSetName);
		var e = oHiddenToolbarsDiv.firstChild;
		while (e) {
			var oElementToMove = e;
			e = e.nextSibling;
//			oHiddenToolbarsDiv.removeChild(oElementToMove);
			this.DOMElement.appendChild(oElementToMove);
		}
		// refresh its state...
		FCKToolbarSet.EnableItems();
		FCKToolbarSet.Restart();
		// that's all
		return true;
	}

	return false;
}

FCKToolbarSet.EnableItems = function()
{
	FCK.Focus();
	window.setTimeout('FCKToolbarSet.DoEnableItems();', 100);
}

FCKToolbarSet.DoEnableItems = function()
{
	for ( var i = 0 ; i < FCKToolbarSet.Toolbars.length ; i++ )
	{
		var oToolbar = FCKToolbarSet.Toolbars[i] ;
		if (oToolbar && oToolbar.Items) {
			for ( var j = 0 ; j < oToolbar.Items.length ; j++ )
			{
				oToolbar.Items[j].RefreshState() ;
			}
		}
	}

}
FCKToolbarSet.DisableItems = function()
{
	for ( var i = 0 ; i < FCKToolbarSet.Toolbars.length ; i++ )
	{
		var oToolbar = FCKToolbarSet.Toolbars[i] ;
		if (oToolbar && oToolbar.Items) {
			for ( var j = 0 ; j < oToolbar.Items.length ; j++ )
			{
				if (oToolbar.Items[j].Disable) {
					oToolbar.Items[j].Disable() ;
				}
			}
		}
	}

}
