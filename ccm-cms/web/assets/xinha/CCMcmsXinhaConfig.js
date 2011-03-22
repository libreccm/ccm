/* This is the OpenCCM default Xinha configuration file. It provides basic funcionality *
 * which might be used by any CCM module. A module may provide its own configuration    *
 * file during Xinha loading or may extend com.arsdigita.bebop.form.DHTMLEditor as      *
 * ccm-cms does (com.arsdigita.cms.CMSDHTMLEditor) to provide extensive configuration   *
 * options.                                                                             */
/* It is based on the original configuration file XinhaConfig.js (compressed version)   */
/* http://svn.xinha.webfactional.com/trunk/examples/XinhaConfig.js                      */
xinha_init=null;
xinha_config=null;
xinha_init=xinha_init?xinha_init:function(){
xinha_editors=xinha_editors?xinha_editors:["myTextArea","anotherOne"];
xinha_plugins=xinha_plugins?xinha_plugins:["CharacterMap","ContextMenu","SmartReplace","Stylist","TableOperations"];
if(!Xinha.loadPlugins(xinha_plugins,xinha_init)){
return;
}
xinha_config=xinha_config?xinha_config():new Xinha.Config();
  //this is the standard toolbar for CCM, feel free to remove buttons as you like
  xinha_config.toolbar =
  [
    ["popupeditor"],
    ["separator","formatblock","bold","italic","underline","strikethrough"],
    ["separator","subscript","superscript"],
    ["separator","justifyleft","justifycenter","justifyright","justifyfull"],
    ["separator","insertorderedlist","insertunorderedlist","outdent","indent"],
    ["separator","inserthorizontalrule","createlink"],
    ["separator","undo","redo","selectall"], (Xinha.is_gecko ? [] : ["cut","copy","paste","overwrite"]),
    ["separator","clearfonts","removeformat"],
    ["separator","htmlmode","showhelp","about"]
  ];

xinha_config.pageStyleSheets=[_editor_url+"examples/full_example.css"];
xinha_editors=Xinha.makeEditors(xinha_editors,xinha_config,xinha_plugins);
Xinha.startEditors(xinha_editors);
};
Xinha.addOnloadHandler(xinha_init);

