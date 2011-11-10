<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet 
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:nav="http://ccm.redhat.com/navigation"
  version="1.0">

<!--<xsl:template name="contentLinks">-->
<xsl:template match="nav:simpleObjectList">
<xsl:for-each select="nav:objectList">
                 
  <div id="contentLinks">
    <xsl:for-each select="nav:item">
    <a>
    <xsl:attribute name="href"><xsl:value-of select="nav:path" /></xsl:attribute>
    <xsl:attribute name="title"><xsl:value-of select="nav:attribute[@name='displayName']" /></xsl:attribute>
    <xsl:value-of select="nav:attribute[@name='displayName']" />
    </a>
    <span class="hide">|</span>
    </xsl:for-each>
  </div>

</xsl:for-each>
</xsl:template>
</xsl:stylesheet>
