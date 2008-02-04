<?xml version="1.0" encoding="utf-8" ?>
<xsl:stylesheet  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:bebop="http://www.arsdigita.com/bebop/1.0"
  xmlns:aplaws="http://www.arsdigita.com/aplaws/1.0"
  xmlns:ui="http://www.arsdigita.com/ui/1.0"
  xmlns:cms="http://www.arsdigita.com/cms/1.0"
  xmlns:nav="http://ccm.redhat.com/london/navigation"
  xmlns:search="http://rhea.redhat.com/search/1.0"
  xmlns:portal="http://www.uk.arsdigita.com/portal/1.0"
  xmlns:shp="http://www.shp.de"
  exclude-result-prefixes="xsl bebop aplaws ui cms nav search portal"
  version="1.0">

  <xsl:import href="navigation.xsl"/>

  <xsl:param name="context-prefix"/>
  <xsl:param name="dispatcher-prefix"/>

<!-- PAGE LAYOUT -->
  <xsl:template name="shp:pageLayout">
    <html xmlns="http://www.w3.org/1999/xhtml" xml:lang="de" lang="de">
      <head>
        <title>Schädel-Hirnpatienten in Not e.V. - Deutsche Wachkoma Gesellschaft</title>
        <xsl:call-template name="shp:headMetaData"/>
<!--
        <xsl:call-template name="cssStyles"/>
-->
        <xsl:call-template name="shp:cssStyles"/>
      </head>
      <body>
        <a class="navHide" href="#startcontent" title="Go directly to main content" accesskey="S">Skip over navigation</a>
        <span class="hide">|</span>
        <xsl:call-template name="shp:header"/>
        <xsl:call-template name="shp:menu"/>
<!--
        <xsl:choose>
          <xsl:when test="count(/bebop:page/portal:homepageWorkspace) > 0">
            <xsl:call-template name="wsBody"/>
          </xsl:when>
          <xsl:otherwise>
            <xsl:apply-templates select="portal:workspace"/>
          </xsl:otherwise>
        </xsl:choose>
-->
        <div id="content">
          <xsl:call-template name="shp:content"/>
          <xsl:call-template name="shp:footer"/>
        </div>
        <xsl:call-template name="shp:info"/>
      </body>
    </html>
  </xsl:template>

<!-- CSS-STYLES -->
  <xsl:template name="shp:cssStyles">

    <link rel="stylesheet" type="text/css" href="{$theme-prefix}/css/screen/basic.css" media="screen" />
    <link rel="stylesheet" type="text/css" href="{$theme-prefix}/css/screen/chrome.css" media="screen" />

<!--    <link rel="stylesheet"  type="text/css" href="{$theme-prefix}/css/screen/corrective.css" media="screen" /> -->
<!-- Testen des Print-Layouts
    <link rel="stylesheet" type="text/css" href="{$theme-prefix}/css/print/basic.css" media="screen" />
    <link rel="stylesheet" type="text/css" href="{$theme-prefix}/css/print/chrome.css" media="screen" />
-->
    <link rel="stylesheet" type="text/css" href="{$theme-prefix}/css/print/basic.css" media="print" />
    <link rel="stylesheet" type="text/css" href="{$theme-prefix}/css/print/chrome.css" media="print" />
<!--    <link rel="stylesheet"  type="text/css" href="{$theme-prefix}/css/print/corrective.css" media="print" /> -->
  </xsl:template>

<!-- METADATA -->
  <xsl:template name="shp:headMetaData">
  </xsl:template>

<!-- HEADER -->
  <xsl:template name="shp:header">
    <div id="header">
      <div class="logo">
        <img src="{$theme-prefix}/images/header_left.jpg" name="Page_Header" alt="[Schädel-Hirnpatienten in Not e.V. - Notrufzentrale: 09621 / 6 48 00 ]" />
      </div>
      <div class="printlogo">
        <img src="{$theme-prefix}/images/header_left_print.jpg" name="Page_Header_Print" alt="[Schädel-Hirnpatienten in Not e.V. - Notrufzentrale: 09621 / 6 48 00 ]" />
      </div>
      <div class="head">
        <img src="{$theme-prefix}/images/header_right.jpg" name="Head_Logo" alt="[Head Logo]" />
      </div>
      <div id="subheader">
        <xsl:call-template name="shp:breadcrumb"/>
        <xsl:call-template name="shp:suche"/>
      </div>
    </div>
  </xsl:template>

<!-- FOOTER -->
  <xsl:template name="shp:footer">
    <div id="footer">
      <span class="hide">|</span>
      <a href="{$dispatcher-prefix}/navigation/" title="Start" accesskey="1">Start</a>
      |
      <a href="{$dispatcher-prefix}/atoz" title="A-Z">A-Z</a>
      |
      <a href="{$dispatcher-prefix}/navigation/sitemap.jsp" title="Sitemap" accesskey="3">Sitemap</a>
      |
      <a href="/contact" title="Kontakt" accesskey="9">Kontakt</a>
      |
      <a href="/help" title="Help" accesskey="6">Hilfe</a>
    </div>
  </xsl:template>

<!-- MENU -->
  <xsl:template name="shp:menu">
<!--    <div id="menu"> -->
      <xsl:call-template name="shp:navigation"/>
<!--
      <xsl:apply-templates select="//portal:homepageWorkspace[@id='left']/portal:portal"/>
      <xsl:for-each select="//portal:homepageWorkspace[@id='left']/bebop:link">
        <div class="custLink"><xsl:apply-templates select="."/></div>
      </xsl:for-each>
-->
<!--      <br />
      <img id="menuEnd" src="{$theme-prefix}/images/menu_bottom.jpg" name="Menu_Bottom" alt="[Menu Ende]" />
    </div> -->
  </xsl:template>

<!-- INFO -->
  <xsl:template name="shp:info">
    <div id="info">
      <xsl:call-template name="contentLinks"/>
      <xsl:call-template name="associatedLinks"/>
    </div>
  </xsl:template>

  <xsl:template name="shp:content">
    <xsl:call-template name="mainContent"/>
  </xsl:template>

<!-- SUCHE -->
  <xsl:template name="shp:suche">
    <div class="quicksearch">
      <p>
        <form name="search" method="get" action="{$dispatcher-prefix}/search/">
          <label for="topSearch" accesskey="4">Suche:</label> 
          <input class="quicksearchBox" id="topSearch" name="terms" value="" />
          <input type="submit" name="Submit" id="topGo" value=">" class="go" />
          <xsl:apply-templates select="bebop:pageState" />
        </form>
      </p>
    </div>
  </xsl:template>
</xsl:stylesheet>