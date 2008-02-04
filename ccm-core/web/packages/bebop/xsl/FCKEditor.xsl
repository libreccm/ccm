<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
  
  <xsl:output method="html" indent="yes"/>
  
  <xsl:template match="bebop:fckeditor"
    xmlns:bebop="http://www.arsdigita.com/bebop/1.0">

    <xsl:variable name="firstMatch"><xsl:value-of select="//bebop:fckeditor/@name"/></xsl:variable>
    <!-- Start of the FCKeditor component code -->
    <xsl:if test="@name=$firstMatch">    
       
      <script type="text/javascript">
        _editor_url = "/assets/fckeditor/";
        _editor_lang = "en";
        var numEd = 0;
      </script>
      
      <script type="text/javascript" src="/assets/fckeditor/fckeditor.js"/>
      
      <script type="text/javascript">
        <xsl:for-each select="//bebop:fckeditor">
           var editor_<xsl:value-of select="@name"/> = null;
       </xsl:for-each>

        window.onload = function() {
        <xsl:for-each select="//bebop:fckeditor">
          editor_<xsl:value-of select="@name"/> = new FCKeditor("ta_<xsl:value-of select="@name"/>") ;
          editor_<xsl:value-of select="@name"/>.Width = 
          <xsl:choose>
          		<xsl:when test="@metadata.width">
          			'<xsl:value-of select="@metadata.width"/>'; 
          		</xsl:when>
          		<xsl:otherwise>
          			'100%';
          		</xsl:otherwise>
          </xsl:choose>
          editor_<xsl:value-of select="@name"/>.Height = 
          <xsl:choose>
          		<xsl:when test="@metadata.height">
          			'<xsl:value-of select="@metadata.height"/>'; 
          		</xsl:when>
          		<xsl:otherwise>
          			'400';
          		</xsl:otherwise>
          </xsl:choose>
          
          editor_<xsl:value-of select="@name"/>.BasePath = "/assets/fckeditor/" ;
          editor_<xsl:value-of select="@name"/>.PluginsPath = editor_<xsl:value-of select="@name"/>.BasePath + "editor/plugins/" ;
          <xsl:if test="bebop:config/@path">
           editor_<xsl:value-of select="//bebop:fckeditor/@name"/>.Config['CustomConfigurationsPath'] = "<xsl:value-of select="//bebop:fckeditor/bebop:config/@path"/>";
          </xsl:if>
          editor_<xsl:value-of select="@name"/>.ToolbarSet = "Basic";
          editor_<xsl:value-of select="@name"/>.ReplaceTextarea();
        }
        </xsl:for-each>       

      </script>

      <style type="text/css">
        textarea { background-color: #fff; border: 1px solid 00f; }
      </style>
      
    </xsl:if>
    <!-- End of fckeditor setup -->
      
    <textarea id="ta_{@name}" name="{@name}" style="width:100%" rows="{@rows}" cols="{@cols}" wrap="{@wrap}">
      <xsl:value-of disable-output-escaping="no" select="text()"/>
    </textarea>
    
  </xsl:template>
</xsl:stylesheet>
