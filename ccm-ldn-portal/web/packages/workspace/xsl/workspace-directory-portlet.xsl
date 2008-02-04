<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet 
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:portal="http://www.uk.arsdigita.com/portal/1.0"
  xmlns:portlet="http://www.uk.arsdigita.com/portlet/1.0"  
  version="1.0">

  <xsl:template match="portlet:workspaceDirectory">
    <xsl:apply-templates select="*"/>
  </xsl:template>

  <xsl:template match="portal:workspaceDirectory">
    <xsl:apply-templates select="*">
      <xsl:with-param name="maxItems" select="100"/>
    </xsl:apply-templates>
  </xsl:template>

</xsl:stylesheet>
