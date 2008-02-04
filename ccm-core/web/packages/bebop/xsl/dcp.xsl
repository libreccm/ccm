<?xml version="1.0"?>
<xsl:stylesheet
  xmlns:bebop="http://www.arsdigita.com/bebop/1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
  exclude-result-prefixes="bebop">

<xsl:output method="html" indent="yes"/>

<xsl:param name="dcp-on-buttons"/>
<xsl:param name="dcp-on-links"/>

<xsl:template name="bebop:dcpJavascript">
  <script language="javascript">
  <xsl:choose>
    <xsl:when test="$dcp-on-buttons">
      function dcp_hide(e) {
          if (e.form != null) {
              elements = e.form.elements;
              for (i=0; i &lt; elements.length; i++) {
                 if (elements[i].tagName == "INPUT" &amp;&amp; elements[i].type == "submit" &amp;&amp; elements[i] != e) {
                    elements[i].setAttribute("disabled", "disabled");
                 }
             }
        }
        try {
            // IE version - setAttribute("value", .... doesn't work in IE, so results in default browser value (submit query) 
            // also note, class attribute referred to as className in IE!??!
            
            proxybutton = document.createElement("&lt;input class='" + e.getAttribute("className") + "' id = '" + e.getAttribute("id") + "' type='submit' value='" +  e.getAttribute("value") + "' disabled /&gt;");
        	
        } catch (error) {
            // proper version
            proxybutton = document.createElement('input');
            proxybutton.setAttribute("disabled", "disabled");
            proxybutton.setAttribute("value", e.getAttribute("value"));
            proxybutton.setAttribute("class", e.getAttribute("class"));
            proxybutton.setAttribute("id", e.getAttribute("id"));
            proxybutton.setAttribute("type", "submit");
        }
        e.parentNode.insertBefore(proxybutton, e);
          e.style.display = 'none';
        
      }
    </xsl:when>
    <xsl:otherwise>
      function dcp_hide(e) { }
    </xsl:otherwise>
  </xsl:choose>
  <xsl:choose>
    <xsl:when test="$dcp-on-links">
      
         
      function dcp_disable_link(e) { 
      		proxylink = e.cloneNode(true);
            proxylink.removeAttribute("href");
            e.parentNode.insertBefore(proxylink, e);
            e.style.display = 'none';
       	
      }                                                 
    </xsl:when>
    <xsl:otherwise>
        function dcp_disable_link(e) { }
    </xsl:otherwise>
  </xsl:choose>
  </script>
</xsl:template>



</xsl:stylesheet>
