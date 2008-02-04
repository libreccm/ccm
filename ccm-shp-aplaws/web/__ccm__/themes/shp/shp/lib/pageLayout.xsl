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
        <xsl:call-template name="shp:headMetaData"/>
<!--
        <xsl:call-template name="cssStyles"/>
-->
        <xsl:call-template name="shp:cssStyles"/>
        <title>Schädel-Hirnpatienten in Not e.V. - Deutsche Wachkoma Gesellschaft</title>
      </head>
      <body>

        <xsl:choose>
          <!-- Layout für Portal-->
          <xsl:when test="/bebop:page/@application = 'portal'">
<!-- Standardbehandlung der Portalseite
            <xsl:choose>
              <xsl:when test="count(/bebop:page/portal:homepageWorkspace) > 0">
                <xsl:call-template name="wsBody"/>
              </xsl:when>
              <xsl:otherwise>
                <xsl:apply-templates select="portal:workspace"/>
              </xsl:otherwise>
            </xsl:choose>
-->

            <div id="portal">
              <h1>
                Schädel-Hirnpatienten in Not e.V.
              </h1>
              <a href="{$dispatcher-prefix}/navigation/">
                <img src="{$theme-prefix}/images/big_logo.jpg" alt="Verbandslogo" title="Zum Bundesverband" />
              </a>
              <div id="links">
                <a href="{$dispatcher-prefix}/navigation/">Zum Bundesverband</a>
                |
                Zum Forum
              </div>
            </div>
          </xsl:when>

          <!-- Layout für Navigation, ContentSection...-->
          <xsl:otherwise>
            <a class="navHide" href="#startcontent" title="Direkt zum Inhalt" accesskey="S">Navigation überspringen</a>
            <span class="hide">|</span>
            <xsl:call-template name="shp:header"/>
            <xsl:call-template name="shp:menu"/>
            <xsl:call-template name="shp:subheader"/>

            <div id="content">
              <h1>
                <xsl:call-template name="shp:title"/>
              </h1>
              <xsl:call-template name="shp:content"/>
              <xsl:call-template name="shp:footer"/>
            </div>
            <xsl:call-template name="shp:info"/>
          </xsl:otherwise>
        </xsl:choose>

      </body>
    </html>
  </xsl:template>

<!-- Fehler im Parser, erkennt nicht, daß dieses Template nur aufgerufen wird, wenn /bebop:page/@application = 'portal' -->
<!-- in workspace-index.xsl ist das template vorhanden, hier ist es eigentlich überflüssig MIST -->
<xsl:template name="wsBody">
</xsl:template>


<!-- CSS-STYLES -->
  <xsl:template name="shp:cssStyles">

    <!-- Styles für alle Browser inkl. NS4 -->
    <link rel="stylesheet" type="text/css" href="{$theme-prefix}/css/screen/basic.css" media="screen" />
    <link rel="stylesheet" type="text/css" href="{$theme-prefix}/css/print/basic.css" media="print" />

    <!-- Style nur für moderne Browser (nicht NS4) -->
    <style type="text/css">
      <!--
-->
      @media screen {
        @import url("{$theme-prefix}/css/screen/chrome.css");
      }

      @media print {
        @import url("{$theme-prefix}/css/print/chrome.css");
      }
<!--
      -->
    </style>

    <!-- Conditional Comment: Styles nur für IE -->

    <!--[if IE]>
-->
      <link rel="stylesheet" type="text/css" href="{$theme-prefix}/css/screen/ie-hack.css" media="screen" />
      <link rel="stylesheet" type="text/css" href="{$theme-prefix}/css/print/ie-hack.css" media="print" />
<!--
    <![endif]-->

  </xsl:template>

<!-- METADATA -->
  <xsl:template name="shp:headMetaData">
    <xsl:if test="/bebop:page/@application = 'portal'">
      <meta http-equiv="refresh" content="5; url={$dispatcher-prefix}/navigation/" />
    </xsl:if>
  </xsl:template>

<!-- HEADER -->
  <xsl:template name="shp:header">
    <div id="header">
      <div class="logo">
        <a href="{$dispatcher-prefix}/navigation/">
          <img src="{$theme-prefix}/images/header_left.jpg" name="Page_Header" alt="[Schädel-Hirnpatienten in Not e.V. - Notrufzentrale: 09621 / 6 48 00 ]" />
        </a>
      </div>
      <div class="printlogo">
        <img src="{$theme-prefix}/images/header_left_print.jpg" name="Page_Header_Print" alt="[Schädel-Hirnpatienten in Not e.V. - Notrufzentrale: 09621 / 6 48 00 ]" />
      </div>
      <div class="head">
        <img src="{$theme-prefix}/images/header_right.jpg" name="Head_Logo" alt="[Head Logo]" />
      </div>
    </div>
  </xsl:template>

<!-- SUBHEADER -->
  <xsl:template name="shp:subheader">
    <div id="subheader">
      <xsl:call-template name="shp:breadcrumb"/>
      <xsl:call-template name="shp:suche"/>
    </div>
  </xsl:template>

  <!-- TITEL -->
  <xsl:template name="shp:title">
    <xsl:choose>

      <!-- Titel für ContentItems -->
      <xsl:when test="cms:contentPanel">
        <xsl:choose>

          <!-- Glossar -->
          <xsl:when test="/bebop:page/cms:contentPanel/cms:item/type/label = 'Glossary Item'">
            Glossar
          </xsl:when>

          <!-- FAQ -->
          <xsl:when test="/bebop:page/cms:contentPanel/cms:item/type/label = 'FAQ Item'">
            FAQ
          </xsl:when>

          <xsl:otherwise>
            <xsl:value-of select="cms:contentPanel/cms:item/title"/>
          </xsl:otherwise>

        </xsl:choose>
      </xsl:when>

      <!-- Titel für A-Z Liste -->
      <xsl:when test="/bebop:page/bebop:title = 'AtoZ'">
        A-Z Liste
      </xsl:when>

      <!-- Titel für Suche -->
      <xsl:when test="/bebop:page/bebop:title = 'Search'">
        Suche
      </xsl:when>

      <!-- Sitemap -->
      <xsl:when test="/bebop:page/bebop:title = 'Sitemap'">
        Sitemap
      </xsl:when>

      <!-- Titel für die ContentSection-->
      <xsl:otherwise>
        <xsl:for-each select="/bebop:page/nav:categoryMenu//nav:category[@isSelected='true']">
          <xsl:choose>

            <!-- Spezielle Regel: Für die Willkommen-Seite die Überschrift des Artikels anzeigen -->
            <xsl:when test="position() = last() and position() = 1">
              <xsl:value-of select="/bebop:page//title"/>
            </xsl:when>

            <!-- Sonst zeige als Überschrift den Namen der Kategorie an -->
            <xsl:when test="position() = last()">
              <xsl:value-of select="@title"/>
            </xsl:when>
          </xsl:choose>
        </xsl:for-each>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

<!-- FOOTER -->
  <xsl:template name="shp:footer">
    <div id="footer">
      <a href="{$dispatcher-prefix}/navigation/" title="Start" accesskey="1">Start</a>
      |
<!--
      <a href="{$dispatcher-prefix}/atoz" title="A-Z">A-Z</a>
      |
-->
      <a href="{$dispatcher-prefix}/navigation/sitemap.jsp" title="Sitemap" accesskey="3">Sitemap</a>
      |
      <a href="/contact" title="Kontakt" accesskey="9">Kontakt</a>
      |
      <a href="/webmaster" title="Webmaster" accesskey="0">Webmaster</a>
      |
      <a href="/help" title="Help" accesskey="6">Hilfe</a>
    </div>
  </xsl:template>

<!-- MENU -->
  <xsl:template name="shp:menu">
      <xsl:call-template name="shp:navigation"/>
<!--
      <xsl:apply-templates select="//portal:homepageWorkspace[@id='left']/portal:portal"/>
      <xsl:for-each select="//portal:homepageWorkspace[@id='left']/bebop:link">
        <div class="custLink"><xsl:apply-templates select="."/></div>
      </xsl:for-each>
-->
  </xsl:template>

<!-- INFO -->
  <xsl:template name="shp:info">
    <div id="info">
<!--
      <xsl:call-template name="contentLinks"/>
-->
      <xsl:call-template name="notes"/>
      <xsl:apply-templates select="nav:simpleObjectList[@id='newsList']" />
      <xsl:call-template name="fileAttachments"/>
      <xsl:call-template name="associatedLinks"/>
      <xsl:call-template name="relatedItems"/>
    </div>
  </xsl:template>

  <xsl:template name="shp:content">
    <xsl:call-template name="mainContent"/>
  </xsl:template>

<!-- SCHNELLSUCHE -->
  <xsl:template name="shp:suche">
    <form id="quicksearch" name="search" method="get" action="{$dispatcher-prefix}/search/">
      <label for="topSearch" accesskey="4">Suche:</label> 
      <input id="box" name="terms" value="" />
      <input id="go" type="submit" name="Submit" value=">" class="go" />
      <xsl:apply-templates select="bebop:pageState" />
    </form>
  </xsl:template>
</xsl:stylesheet>
