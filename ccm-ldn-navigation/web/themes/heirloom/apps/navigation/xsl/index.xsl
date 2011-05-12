<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet   xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:bebop="http://www.arsdigita.com/bebop/1.0"
                  xmlns:nav="http://ccm.redhat.com/london/navigation"
                    version="1.0">

  <xsl:import href="../../../packages/bebop/xsl/bebop.xsl"/>
  <xsl:import href="../../../packages/ui/xsl/ui.xsl"/>

  <xsl:import href="../lib/category.xsl"/>
  <xsl:import href="../lib/quick-links.xsl"/>
  <xsl:import href="../lib/greeting-item.xsl"/>
  <xsl:import href="../lib/object-list.xsl"/>

  <xsl:output method="html"/>

  <xsl:template name="bebop:pageCSS">
    <xsl:call-template name="bebop:pageCSSMain"/>
    <xsl:call-template name="nav:pageCSSMain"/>
  </xsl:template>
  
  <xsl:template name="nav:pageCSSMain">
    <link href="/themes/heirloom/apps/navigation/lib/index.css" rel="stylesheet" type="text/css"/>
  </xsl:template>


</xsl:stylesheet>
