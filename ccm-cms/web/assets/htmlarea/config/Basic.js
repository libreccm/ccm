// This config changes the link popup to use the item browser

HTMLArea.Config.CMSBasic = function() {
   	this.toolbar = [
		[ "fontname", "space",
		  "fontsize", "space",
		  "formatblock", "space",
		  "bold", "italic", "underline", "strikethrough", "separator",
		  "subscript", "superscript", "separator",
		  "copy", "cut", "paste", "space", "undo", "redo" ],
		
		[ "justifyleft", "justifycenter", "justifyright", "justifyfull", "separator",
          "lefttoright", "righttoleft", "separator",
		  "insertorderedlist", "insertunorderedlist", "outdent", "indent", "separator",
		  "forecolor", "hilitecolor", "textindicator", "separator",
		  "inserthorizontalrule", "insertlink", "insertimage", "inserttable", "htmlmode", "separator",
		  "popupeditor", "separator" ]
		  // "popupeditor", "separator", "showhelp", "about" ]
    ];

    this.registerButton("insertlink", "Insert link", _editor_url + "images/ed_link.gif", false, function(editor) {
  	  editor._popupDialog("insert_link.html", function(param) {
		if (!param) {	// user must have pressed Cancel
			return false;
		}
        var furl = param["f_url"];
		var sel = editor._getSelection();
		var range = editor._createRange(sel);
		editor._doc.execCommand("createlink", false, furl);
	}, null);
    });

	this.sizeIncludesToolbar = false;
	this.statusBar = false;


    this.combos = [
        { label: "Style",
          options: { "None"           : "",
                     "Main" : "main",
                     "Dark" : "dark",
                     "Medium" : "medium",
                     "Light" : "light"
                   }//,
          //context: "pre"
        //},
        //{ label: "Info",
        //  options: { "None"           : "",
        //             "Quote"          : "quote",
        //             "Highlight"      : "highlight",
        //             "Deprecated"     : "deprecated"
        //           }
        }
      ]
}

HTMLArea.Config.CMSBasic.prototype = new HTMLArea.Config;
HTMLArea.Config.CMSBasic.prototype.constructor = HTMLArea.Config.CMSBasic;
HTMLArea.Config.CMSBasic.superclass = HTMLArea.Config.prototype;
