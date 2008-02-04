<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                version="1.0"
  xmlns:cms="http://www.arsdigita.com/cms/1.0">

<!-- FR@runtime: maybe we should remove this -->

  <xsl:import href="../../navigation/xsl/navigation.xsl"/>
  <xsl:import href="../../content-section/xsl/cms-item.xsl"/>
  <xsl:import href="../../portalserver/xsl/index.xsl"/>

  <!-- so we don't need to import the default RH public CMS stylesheet, cms-public.xsl -->
  <xsl:template match="cms:contentPanel">
    <xsl:apply-templates />
  </xsl:template>



</xsl:stylesheet>
