<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:cms="http://www.arsdigita.com/cms/1.0"
                version="1.0">

  <xsl:template name="DublinMetaDataHeader">
    <xsl:for-each select="dublinCore/*">
      <xsl:if test="starts-with(name(),'dc')">
        <meta>
          <xsl:attribute name="name"><xsl:text>DC.</xsl:text><xsl:value-of select="substring(name(),3)" /></xsl:attribute>
          <xsl:attribute name="content"><xsl:value-of select="." /></xsl:attribute>
        </meta>
      </xsl:if>
    </xsl:for-each>
  </xsl:template>

</xsl:stylesheet>
