var FCKSwitchToolbarCommand = function(name, toolbar){
	this.Name = name;
	this.Toolbar = toolbar ? toolbar : name;
}
FCKSwitchToolbarCommand.prototype.Execute = function(){
     FCKToolbarSet.Load(this.Toolbar);
	 return true;
}
FCKSwitchToolbarCommand.prototype.GetState = function(){
	// the button is simply allways off
	return FCK_TRISTATE_OFF;
}

// Register the related commands.
FCKCommands.RegisterCommand( 'SimpleToolbar', new FCKSwitchToolbarCommand('SimpleToolbar','Basic')) ;
FCKCommands.RegisterCommand( 'AdvToolbar', new FCKSwitchToolbarCommand('AdvToolbar','Advanced')) ;

// Create the "Simple" toolbar button.
var oToolbarItem		= new FCKToolbarButton( 'SimpleToolbar', FCKLang['SimpleToolbar'],null, FCK_TOOLBARITEM_ICONTEXT , false, false  ) ;
oToolbarItem.IconPath	= FCKConfig.PluginsPath + 'toolbarswitch/simpletoolbar.gif' ;
FCKToolbarItems.RegisterItem( 'SimpleToolbar', oToolbarItem) ;			

var oToolbarItem		= new FCKToolbarButton( 'AdvToolbar', FCKLang['AdvToolbar'], null, FCK_TOOLBARITEM_ICONTEXT , false, false  ) ;
oToolbarItem.IconPath	= FCKConfig.PluginsPath + 'toolbarswitch/advtoolbar.gif' ;
FCKToolbarItems.RegisterItem( 'AdvToolbar', oToolbarItem) ;			