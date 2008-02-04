<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

  <xsl:output method="html" indent="yes"/>

  <xsl:template match="bebop:datetime"
    xmlns:bebop="http://www.arsdigita.com/bebop/1.0">
    <xsl:for-each select="*">
      <xsl:if test="position()=4"><br/></xsl:if>
      <xsl:if test="position()=5"> : </xsl:if>
      <xsl:if test="position()=6 and last()=7"> : </xsl:if>

      <xsl:apply-templates select="."/>  
    </xsl:for-each>
  </xsl:template>

  <xsl:template match="bebop:date"
    xmlns:bebop="http://www.arsdigita.com/bebop/1.0">
      <xsl:copy>
        <xsl:apply-templates/>
      </xsl:copy>
  </xsl:template>

</xsl:stylesheet>
