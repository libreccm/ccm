<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet 
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:aplaws="http://www.arsdigita.com/aplaws/1.0"
  version="1.0">

  <!-- IMPORT DEFINITIONS ccm-forum installed as separate web application ccm-forum
  <xsl:import href="../../../../ccm-forum/__ccm__/apps/forum/xsl/index.xsl"/>
  -->
  <!-- IMPORT DEFINITIONS ccm-ldn-shortcuts installed into the main CCM webapp
  -->
  <xsl:import href="../../../__ccm__/apps/forum/xsl/index.xsl"/>

  <xsl:import href="lib/page.xsl"/>

  <xsl:param name="theme-prefix" />

  <xsl:template name="aplaws:headerStyleSheets">
    <link rel="stylesheet" href="{$theme-prefix}/forum-index.css" type="text/css" media="screen"/>
  </xsl:template>

</xsl:stylesheet>
