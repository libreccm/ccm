<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
  
  <xsl:output method="html" indent="yes"/>
  
  <xsl:template match="bebop:dhtmleditor"
    xmlns:bebop="http://www.arsdigita.com/bebop/1.0">

    <xsl:variable name="firstMatch"><xsl:value-of select="//bebop:dhtmleditor/@name"/></xsl:variable>
    <xsl:if test="@name=$firstMatch">    

      <script type="text/javascript">
        _editor_url = "/assets/htmlarea/";
        _editor_lang = "en";
	var numEd = 0;
      </script>

      <script type="text/javascript" src="/assets/htmlarea/htmlarea.js"/>

      <script type="text/javascript">
        <xsl:for-each select="bebop:plugin">
          HTMLArea.loadPlugin("<xsl:value-of select="@name"/>");
        </xsl:for-each>

	// Using Styled.js didn't seem to work anymore with htmlarea 3.0rc3,
	// so instead we configure the editor here

	var css_plugin_args = {
            combos : [
                { label: "Style",
                  options: { "None" : "",
                             "Main" : "main",
                             "Dark" : "dark",
                             "Medium" : "medium",
                             "Light" : "light"
                           }
                }
            ]
        };

	<xsl:for-each select="//bebop:dhtmleditor">
	  var config_<xsl:value-of select="@name"/> = null;
	  var editor_<xsl:value-of select="@name"/> = null;
	</xsl:for-each>

        function initDocument() {
          <xsl:for-each select="//bebop:dhtmleditor">
	    config_<xsl:value-of select="@name"/> = new HTMLArea.Config();
            editor_<xsl:value-of select="@name"/> = new HTMLArea("ta_<xsl:value-of select="@name"/>", config_<xsl:value-of select="@name"/>);

	    config_<xsl:value-of select="@name"/>.registerButton("insertlink", "Insert link", _editor_url + "images/ed_link.gif", false, function(editor) {
              <!-- Modified to add the open in new window button NJ-20062403-->
              <!--  editor._popupDialog("insert_link.html", function(param) {
                if (!param) {   // user must have pressed Cancel
                        return false;
                }
                var furl = param["f_url"];
                var sel = editor._getSelection();
                var range = editor._createRange(sel);
                editor._doc.execCommand("createlink", false, furl);
                }, null); -->
                var sel = editor._getSelection();
                var range = editor._createRange(sel);
                var compare = 0;
                if (HTMLArea.is_ie) {
                  compare = range.compareEndPoints("StartToEnd", range);
                }
                else {
                  compare = range.compareBoundaryPoints(range.START_TO_END, range);
                }
                if (compare == 0) {
                  alert("You need to select some text before creating a link");
                  return;
                }
                editor._popupDialog("insert_link.html", function(param) {
                if (!param) {   // user must have pressed Cancel
                  return false;
                }
                var sel = editor._getSelection();
                var range = editor._createRange(sel);
                if (range.insertNode) { // Standards compliant version
                  var link = document.createElement("a");
                  var linkText = range.extractContents();
                  link.href=param["f_url"];
                  if (param["f_external"])
                    link.target="_blank";
                  link.appendChild(linkText);
                  range.insertNode(link);
                }
                else if (range.pasteHTML) { // Alternative non standards version
                  var target = "";
                  if (param["f_external"]) {
                    target='target="_blank"';
                  }
                  range.pasteHTML('&lt;a href="' + param["f_url"] + '"' + target + '&gt;' + range.text + '&lt;/a&gt;');
                }
                }, null);
	    });
            config_<xsl:value-of select="@name"/>.sizeIncludesToolbar = false;
            config_<xsl:value-of select="@name"/>.statusBar = false;
            config_<xsl:value-of select="@name"/>.toolbar = [
                [ "formatblock", "space",
                  "bold", "italic", "underline", "strikethrough", "separator",
                  "subscript", "superscript", "separator",
                  "copy", "cut", "paste", "space", "undo", "redo", "space", "removeformat", "killword"  ],
                [ "justifyleft", "justifycenter", "justifyright", "justifyfull", "separator",
                  "lefttoright", "righttoleft", "separator",
                  "orderedlist", "unorderedlist", "outdent", "indent", "separator",
                  "textindicator", "separator",
                  "inserthorizontalrule", "insertlink", "insertimage", "inserttable", "htmlmode", "separator",
                  "popupeditor", "separator" ]
            ];
       <xsl:if test="bebop:config/@hidden-buttons">
           config_<xsl:value-of select="@name"/>.hideSomeButtons("<xsl:value-of select="bebop:config/@hidden-buttons"/>");
       </xsl:if>
	    editor_<xsl:value-of select="@name"/>.config.pageStyle = "@import url(/assets/htmlarea/htmlarea.css);";

            <xsl:for-each select="bebop:plugin">
	      <xsl:choose>
		<xsl:when test="@name = 'CSS'">
		  editor_<xsl:value-of select="../@name"/>.registerPlugin(<xsl:value-of select="@name"/>, css_plugin_args);
		</xsl:when>
		<xsl:otherwise>
		  editor_<xsl:value-of select="../@name"/>.registerPlugin(<xsl:value-of select="@name"/>);
		</xsl:otherwise>
	      </xsl:choose>
	    </xsl:for-each>
	    
            setTimeout(function() {
	      editor_<xsl:value-of select="@name"/>.generate();
	    }, 500*numEd);
	    numEd++;
          </xsl:for-each>
        }

	HTMLArea.init();
	HTMLArea.onload = initDocument;

	function wordClean_<xsl:value-of select="@name"/>() {
	  editor_<xsl:value-of select="@name"/>._wordClean();
	}
      </script>
      <style type="text/css">
        textarea { background-color: #fff; border: 1px solid 00f; }
      </style>

    </xsl:if> 

    <div style="width:560px; border:1px outset #666;">
      <textarea id="ta_{@name}" name="{@name}" rows="{@rows}" cols="{@cols}" wrap="{@wrap}" style="width:100%">
      <xsl:value-of disable-output-escaping="no" select="text()"/>
    </textarea>
    </div>
    
  </xsl:template>
</xsl:stylesheet>
