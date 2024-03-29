<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet 
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:bebop="http://www.arsdigita.com/bebop/1.0"
  xmlns:aplaws="http://www.arsdigita.com/aplaws/1.0"
  xmlns:ui="http://www.arsdigita.com/ui/1.0"
  version="1.0">

  <!--
     This file contains templates related
     to generating <head> tags in HTML
  -->
  <xsl:param name="theme-prefix" />

  <xsl:template name="aplaws:pageTitle">
    <xsl:value-of select="/bebop:page/bebop:title"/>
  </xsl:template>

  <xsl:template name="aplaws:headerMetaData">
    <meta name="title">
      <xsl:attribute name="content">
        <xsl:call-template name="aplaws:pageTitle"/>
      </xsl:attribute>
    </meta>
  </xsl:template>

<!-- The file ~/lib/page.css does not exist at least since version 1.0.2
     there exists a file of this name in
     ccm-cms/web/themes/packages/cms/admin/page/page.css
     may be a copy&paste error                                               -->
  <xsl:template name="aplaws:headerStyleSheets">
    <link rel="stylesheet" href="{$theme-prefix}/lib/page.css" type="text/css" media="screen"/>
  </xsl:template>

</xsl:stylesheet>
