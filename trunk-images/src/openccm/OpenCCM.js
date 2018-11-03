/** xinha editor plugin for OpenCCM **/

OpenCCM._pluginInfo = {
  name          : "OpenCCM",
  version       : "0.3",
  developer     : "Sören Bernstein",
  developer_url : "http://",
  sponsor       : "",
  sponsor_url   : "",
  license       : "htmlArea"
}

function OpenCCM(editor)
{
  this.editor = editor;
  this.dialogs = new Array();
  
  var cfg = editor.config;
  var self = this;
  
  var hasLinkButton = false;
  var hasImageButton = false;
  
  cfg.registerButton(
    {
      id       : "ccmcreatelink",
      tooltip  : this._lc("OpenCCMInsertLink"),
      image    : [editor.imgURL("iconsets/Crystal/ed_buttons_main.png"), 6, 1],
      textMode : false,
      action   : function(e, objname, obj) { self.showLinkDialog(self._getSelectedAnchor()); }
    }
  );
  
  cfg.registerButton(
    {
      id       : "insertimage",
      tooltip  : this._lc("OpenCCMInsertImage"),
      image    : ["ed_buttons_main.png", 6, 3],
      textMode : false,
      action   : function(e, objname, obj) { self.showImageDialog(); }
    }
  );
  
  if(this.findButton("ccmcreatelink"))
  {
    hasLinkButton = true;
    cfg.addToolbarElement("ccmcreatelink", "ccmcreatelink", 0);
  } else {
    cfg.addToolbarElement("ccmcreatelink", "undo", -1);
  }

  if(this.findButton("insertimage"))
  {
    hasImageButton = true;
    cfg.addToolbarElement("insertimage", "insertimage", 0);
  }
  else
  {
    cfg.addToolbarElement("insertimage", "ccmcreatelink", 1);
  }
  
  if(!hasLinkButton && !hasImageButton)
  {
    cfg.addToolbarElement(["separator"], "insertimage", 1);
  }
  
  if(typeof editor._insertImage == 'undefined') 
  {
    editor._insertImage = function() { self.showImageDialog(); };
    // editor.config.btnList.insertimage[3] = function() { self.show(); }
  }
  
  // Register double click action
  cfg.dblclickList["a"][0] = function(e, target) { self.showLinkDialog(self._getSelectedAnchor()); };
}

OpenCCM.prototype.findButton = function(string)
{
  var toolbar = this.editor.config.toolbar;
  
  for(i = 0; i < toolbar.length; i++)
  {
    for(j = 0; j < toolbar[i].length; j++)
    {
      if(toolbar[i][j] == string)
      {
        return true;
      }
    }
  }
  
  return false;
}

OpenCCM.prototype._lc = function(string)
{
  return Xinha._lc(string, 'OpenCCM');
}

OpenCCM.prototype.onGenerateOnce = function ()
{
  this.loadAssets();
}

OpenCCM.prototype.loadAssets = function()
{
  var self = OpenCCM;

  if (self.loading)
  {
    return;
  }
  self.loading = true;
  // Image
  Xinha._getback(Xinha.getPluginDir("OpenCCM") + '/images/images.js', function(getback) { eval(getback); self.imagesMethodsReady = true; } );
  Xinha._getback(Xinha.getPluginDir("OpenCCM") + '/images/images.html', function(getback) { OpenCCM.imagesHtml = getback; self.imagesDialogReady = true; } );
}

