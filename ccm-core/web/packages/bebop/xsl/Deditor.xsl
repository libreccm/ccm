<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

 <xsl:output method="html" indent="yes"/>

 <xsl:template match="bebop:deditor"
               xmlns:bebop="http://www.arsdigita.com/bebop/1.0"
               xmlns:deditor="http://www.arsdigita.com/deditor/1.0">
  <xsl:variable name="submissionname" select="@name"/>
  <xsl:choose> 

      <xsl:when test="@isIE55='true'"> 
      <xsl:processing-instruction name="import">
      <xsl:text>namespace="deditor" implementation="/assets/editor.htc"</xsl:text>
      </xsl:processing-instruction>
       <deditor:editor name="{$submissionname}">
        <xsl:value-of select="bebop:textcontent" disable-output-escaping="yes"/>
         </deditor:editor>

      </xsl:when> 

      <xsl:otherwise>
    <textarea>
      <xsl:for-each select="@*[not(name()='value')]">
            <xsl:attribute name="{name()}">
          <xsl:value-of select="."/>
            </xsl:attribute>
      </xsl:for-each>
        <xsl:value-of select="bebop:textcontent" disable-output-escaping="yes"/>
    </textarea>
      </xsl:otherwise>

    </xsl:choose> 
 </xsl:template>

</xsl:stylesheet>
