<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

 <xsl:output method="html" indent="yes"/>

 <xsl:template match="bebop:listPanel"
               xmlns:bebop="http://www.arsdigita.com/bebop/1.0">
    <ul>
      <xsl:for-each select="bebop:cell">
        <li><xsl:apply-templates /></li>
      </xsl:for-each>
    </ul>
 </xsl:template>

 <xsl:template match="bebop:listPanel[@ordered='true']"
               xmlns:bebop="http://www.arsdigita.com/bebop/1.0">
    <ol>
      <xsl:for-each select="bebop:cell">
        <li><xsl:apply-templates /></li>
      </xsl:for-each>
    </ol>
  </xsl:template>


</xsl:stylesheet>
