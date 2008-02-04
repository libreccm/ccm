<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
	xmlns:cms="http://www.arsdigita.com/cms/1.0"
    xmlns:ui="http://www.arsdigita.com/ui/1.0"
	xmlns:bebop="http://www.arsdigita.com/bebop/1.0"
	exclude-result-prefixes="cms">

  <xsl:template match="cms:contentPanel">
    <xsl:apply-templates />
  </xsl:template>

  <xsl:template name="bebop:pageTitle">
    <xsl:text>CMS: </xsl:text><xsl:value-of select="/bebop:page/cms:contentPanel/cms:item/displayName"/>
  </xsl:template>


</xsl:stylesheet>
