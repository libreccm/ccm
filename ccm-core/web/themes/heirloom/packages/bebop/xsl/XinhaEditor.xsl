<?xml version="1.0"?>
<xsl:stylesheet 
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:bebop="http://www.arsdigita.com/bebop/1.0" 
  exclude-result-prefixes="xsl bebop" 
  version="1.0">
  
  
  <!-- This is a backport from Mandalay v0.9 to support Xinha editor with the -->
  <!-- generic theme. Xinha is the follow-up of the discontinued HTMLArea. -->
  
  <!-- DE Benutze Xinha -->
  <!-- EN Use Xinha -->

  <xsl:template match="bebop:xinha">
    
    <xsl:variable name="firstMatch">
      <xsl:value-of select="//bebop:xinha/@name"/>
    </xsl:variable>

    <xsl:if test="@name=$firstMatch">    
 
      <script type="text/javascript">
        _editor_url = "<xsl:value-of select="@editor_url"/>";
        _editor_lang = "en";
<!--        _editor_skin = "silva";-->
        
        <!-- DE Definiere, welche Textareas zu Xinha-Editoren werden sollen -->
        <!-- EN Define all textares which should become xinha editors -->
        xinha_editors = [
        <xsl:for-each select="//bebop:xinha">
          'ta_<xsl:value-of select="@name"/>'<xsl:if test="position() != last()">, </xsl:if>
        </xsl:for-each>
        ];
        
        <!-- DE Lade die angegebenen Plugins falls angegeben -->
        <!-- EN Load the mentioned plugins if any-->
        xinha_plugins = null;
        <xsl:if test="bebop:plugin">
          xinha_plugins = [
          <xsl:for-each select="bebop:plugin">
            '<xsl:value-of select="@name"/>'<xsl:if test="position() != last()">, </xsl:if>
          </xsl:for-each>
          ];
        </xsl:if>
      </script>      
    
      <!-- DE Lade die externe JavaScript-Datei für Xinha -->
      <script type="text/javascript" src="{@editor_src}"/>
      
      <!-- DE Lade die angegebene Konfiguration -->
      <script type="text/javascript">
        <xsl:attribute name="src">
          <xsl:choose>
            <xsl:when test="bebop:config[@name='XinhaConfig']">
              <xsl:value-of select="bebop:config[@name='XinhaConfig']/@path"/>
            </xsl:when>
            <xsl:otherwise>
              <xsl:value-of select="bebop:config[@name='Xinha.Config']/@path"/>
            </xsl:otherwise>
          </xsl:choose>
        </xsl:attribute>
      </script>
    
    </xsl:if>
    
    <textarea id="ta_{@name}" name="{@name}" rows="{@rows}" cols="{@cols}" wrap="{@wrap}" style="width:100%">
      <xsl:value-of disable-output-escaping="no" select="text()"/>
    </textarea>
  </xsl:template>
</xsl:stylesheet>
