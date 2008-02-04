<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
  
  <xsl:output method="html" indent="yes"/>
  
  <xsl:template match="bebop:multiSelect"
    xmlns:bebop="http://www.arsdigita.com/bebop/1.0">
    <select name="{@name}" multiple="{@multiple}">
      <xsl:for-each select="@disabled|@size|@title|@*[starts-with(name(), 'on')]">
        <xsl:attribute name="{name()}">
          <xsl:value-of select="."/>
        </xsl:attribute>
      </xsl:for-each>
      <xsl:apply-templates/>
    </select>
  </xsl:template>
  
</xsl:stylesheet>
