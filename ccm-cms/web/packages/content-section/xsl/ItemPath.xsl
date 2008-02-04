<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

 <xsl:output method="html" indent="yes"/>

 <xsl:template match="bebop:list[@type='item-path']"
               xmlns:bebop="http://www.arsdigita.com/bebop/1.0">
      <xsl:for-each select="bebop:cell">
        <xsl:apply-templates/>
        <xsl:if test="position()!=last()">&#160;&gt;&#160;</xsl:if>
      </xsl:for-each>
 </xsl:template>


</xsl:stylesheet>
