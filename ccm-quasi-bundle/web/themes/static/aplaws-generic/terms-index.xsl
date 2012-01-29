<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet 
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:aplaws="http://www.arsdigita.com/aplaws/1.0"
  version="1.0">

  <!-- path if installed in its own web context ccm-ldn-terms
  <xsl:import href="../../../../ccm-ldn-terms/themes/heirloom/apps/terms/xsl/index.xsl"/>
  -->
  <!-- path to xsl if installed in the main (common) web context   
  -->
  <xsl:import href="../../../themes/heirloom/apps/terms/xsl/index.xsl"/>
  <xsl:import href="lib/page.xsl"/>

  <xsl:param name="theme-prefix"/>

  <xsl:template name="aplaws:headerStyleSheets">
    <link rel="stylesheet" href="{$theme-prefix}/terms-index.css" type="text/css" media="screen"/>
  </xsl:template>

</xsl:stylesheet>
