<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
               xmlns:bebop="http://www.arsdigita.com/bebop/1.0"
               xmlns:terms="http://xmlns.redhat.com/london/terms/1.0"
                   version="1.0">
  
  <!-- IMPORT DEFINITIONS ccm-ldn-terms installed as separate web application
  <xsl:import href="../../../../../ROOT/packages/bebop/xsl/bebop.xsl"/>
  <xsl:import href="../../../../../ROOT/packages/ui/xsl/ui.xsl"/>
   -->

  <!-- IMPORT DEFINITIONS ccm-ldn-terms installed into the main CCM webapp
  -->
  <xsl:import href="../../../packages/bebop/xsl/bebop.xsl"/>
  <xsl:import href="../../../packages/ui/xsl/ui.xsl"/>

  <xsl:import href="../lib/domain-listing.xsl"/>
  <xsl:import href="../lib/domain-panel.xsl"/>
  <xsl:import href="../lib/domain-form.xsl"/>
  <xsl:import href="../lib/domain-details.xsl"/>
  <xsl:import href="../lib/domain-usage.xsl"/>

  <xsl:import href="../lib/term-listing.xsl"/>
  <xsl:import href="../lib/term-panel.xsl"/>
  <xsl:import href="../lib/term-form.xsl"/>
  <xsl:import href="../lib/term-details.xsl"/>
  <xsl:import href="../lib/term-picker.xsl"/>
  <xsl:import href="../lib/term-name-search.xsl"/>

  <xsl:output method="html"/>
  
  <xsl:param name="contextPath"/>

  <xsl:template name="bebop:pageCSS">
    <xsl:call-template name="bebop:pageCSSMain"/>
    <xsl:call-template name="terms:pageCSSMain"/>
  </xsl:template>

  <xsl:template name="terms:pageCSSMain">
    <link href="index.css" rel="stylesheet" type="text/css"/>
  </xsl:template>
</xsl:stylesheet>
