// This config removes the options for setting font family,
// size and colours.

HTMLArea.Config.CMSStyled = function() {
   	this.toolbar = [
		[ "formatblock", "space",
		  "bold", "italic", "underline", "strikethrough", "separator",
		  "subscript", "superscript", "separator",
		  "copy", "cut", "paste", "space", "undo", "redo" ],
		
		[ "justifyleft", "justifycenter", "justifyright", "justifyfull", "separator",
          "lefttoright", "righttoleft", "separator",
		  "insertorderedlist", "insertunorderedlist", "outdent", "indent", "separator",
          "forecolor", "hilitecolor", "separator",
		  "textindicator", "separator",
		  "inserthorizontalrule", "insertlink", "insertimage", "inserttable", "htmlmode", "separator",
		  "popupeditor", "separator" ]
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



HTMLArea.Config.CMSStyled.prototype = new HTMLArea.Config;
HTMLArea.Config.CMSStyled.prototype.constructor = HTMLArea.Config.CMSStyled;
HTMLArea.Config.CMSStyled.superclass = HTMLArea.Config.prototype;
