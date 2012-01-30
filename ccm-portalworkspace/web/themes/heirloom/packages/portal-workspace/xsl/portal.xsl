<?xml version="1.0"?>
<xsl:stylesheet  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
               xmlns:admin="http://www.arsdigita.com/admin-ui/1.0"
               xmlns:bebop="http://www.arsdigita.com/bebop/1.0"
              xmlns:portal="http://www.uk.arsdigita.com/portal/1.0"
             xmlns:portlet="http://www.uk.arsdigita.com/portlet/1.0"
                   version="1.0">

  <xsl:import href="../../bebop/xsl/bebop.xsl"/>
  <xsl:import href="../../ui/xsl/ui.xsl"/>
  <xsl:import href="lib.xsl"/>
  <xsl:import href="portlets.xsl"/>
  
  <xsl:output method="html" indent="yes"/>
  
  <xsl:template name="bebop:pageCSS">
    <xsl:call-template name="bebop:pageCSSMain"/>
    <xsl:call-template name="portal:pageCSSMain"/>
  </xsl:template>
  
  <xsl:template name="portal:pageCSSMain">
    <!--
    <link href="/themes/heirloom/packages/forum/table.css" rel="stylesheet" type="text/css"/>
    -->
  </xsl:template>
  
    
</xsl:stylesheet>
