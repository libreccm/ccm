<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

  <xsl:output method="html" indent="yes"/>
  
  
  <xsl:template match="bebop:form" name="bebopForm"
    xmlns:bebop="http://www.arsdigita.com/bebop/1.0">
    <xsl:value-of select="@message"/>
    <form>
      <xsl:for-each select="@*[not(self::method)]">
        <xsl:attribute name="{name()}">
          <xsl:value-of select="."/>
        </xsl:attribute>
        <xsl:attribute name="method">
          <xsl:choose> 
            <xsl:when test="string-length(../@method)=0">post</xsl:when>
            <xsl:otherwise><xsl:value-of select="../@method"/></xsl:otherwise>
          </xsl:choose> 
        </xsl:attribute>
      </xsl:for-each>
      <xsl:apply-templates />
    </form>
  </xsl:template>
  
  <!-- no-op (transparent) tags -->
  <xsl:template match="bebop:statel|bebop:pane"
    xmlns:bebop="http://www.arsdigita.com/bebop/1.0">
    <xsl:apply-templates />
  </xsl:template>
  

</xsl:stylesheet>
