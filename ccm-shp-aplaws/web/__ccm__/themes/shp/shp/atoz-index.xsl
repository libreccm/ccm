<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet 
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:bebop="http://www.arsdigita.com/bebop/1.0"
  xmlns:aplaws="http://www.arsdigita.com/aplaws/1.0"
  xmlns:ui="http://www.arsdigita.com/ui/1.0"
  xmlns:cms="http://www.arsdigita.com/cms/1.0"
  xmlns:nav="http://ccm.redhat.com/london/navigation"
  xmlns:search="http://rhea.redhat.com/search/1.0"
  xmlns:atoz="http://xmlns.redhat.com/atoz/1.0"
  xmlns:shp="http://www.shp.de"
  exclude-result-prefixes="xsl bebop aplaws ui cms nav search atoz"
  version="1.0">

  <xsl:import href="../../../../ROOT/__ccm__/apps/content-section/xsl/index.xsl"/>

  <xsl:import href="lib/header.xsl"/>
  <xsl:import href="lib/lib.xsl"/>
  <xsl:import href="lib/pageLayout.xsl"/>
  <xsl:import href="lib/leftNav.xsl"/>

  <xsl:param name="context-prefix"></xsl:param>
  <xsl:param name="dispatcher-prefix" />
  <xsl:param name="theme-prefix" />

  <xsl:output 
    method="html"
    doctype-public="-//W3C//DTD HTML 4.01 Transitional//EN"
    doctype-system="http://www.w3.org/TR/html4/loose.dtd"
    indent="yes" 
  />

  <xsl:template match="bebop:page[@application='atoz']">
    <xsl:call-template name="shp:pageLayout"/>
  </xsl:template>

  <xsl:template name="mainContent">
    <a id="startcontent" title="Start of content"></a>
    <span class="hide">|</span>
    <xsl:call-template name="azMain" />
  </xsl:template>


  <!-- Callback Funktion -->
  <xsl:template name="shp:navAddOn">
    <li id="menulevel_1" class="selected">
      A-Z Liste
    </li>

    <!-- HACK -->
    <li id="menulevel_1">
      <a href="{$dispatcher-prefix}/navigation/unterstuetzen">... unterstützen</a>
    </li>
    <li id="menulevel_1">
      <a href="{$dispatcher-prefix}/navigation/informieren">... informieren</a>
    </li>
    <li id="menulevel_1">
      <a href="{$dispatcher-prefix}/navigation/organisieren">... organisieren</a>
    </li>

  </xsl:template>

  <xsl:template name="azMain">
    <a class="navHide" href="#startAZResults" title="Go directly to A-Z results">Direkt zur A-Z Liste</a>
    <xsl:for-each select="atoz:atoz">
      <h2>Alphabetische Liste der Seiten</h2>
      <div id="azArea">
        <div id="azInfo">
          <xsl:for-each select="atoz:letter">
            <xsl:choose>
              <xsl:when test="@isSelected">
                <span class="letterSelected">
                  <xsl:value-of select="." />
                </span>
              </xsl:when>

              <xsl:otherwise>
                <a>
                  <xsl:attribute name="href">?letter=<xsl:value-of select="." /></xsl:attribute>
                  <xsl:value-of select="." />
                </a>
              </xsl:otherwise>
            </xsl:choose>
            <xsl:if test="position()!=last()"> | </xsl:if>
          </xsl:for-each>
        </div>
<!--
        <div id="azPlace">Inhalt beginnend mit dem Buchstaben: <span class="letterSelected"><xsl:value-of select="atoz:letter[@isSelected]" /></span></div>
-->
      </div>
      <a id="startAZResults" title="Start of A-Z results"></a>
      <xsl:call-template name="azResults" />
    </xsl:for-each>
  </xsl:template>

  <xsl:template name="azResults">
    <xsl:for-each select="./atoz:provider">
      <!-- Nur Anzeigen, wenn es auch Ergebnisse gibt -->
      <xsl:if test="./atoz:atomicEntry">
        <div id="azList">
          <h2><xsl:value-of select="@description" /></h2>
          <ul>
            <xsl:for-each select="atoz:atomicEntry">
              <li id="azResult" >
                <a href="{@url}" title="{@description}"><xsl:value-of select="@title" /></a>
              </li>
            </xsl:for-each>
          </ul>
        </div>
      </xsl:if>
    </xsl:for-each>
    <!-- Wenn ein keine Ergebnisse gibt, dann zeige das an -->
    <xsl:if test="not(/bebop:page//atoz:atomicEntry)">
      <h2>Es gibt keine Einträge zu diesem Anfangsbuchstaben</h2>
    </xsl:if>
  </xsl:template>

</xsl:stylesheet>
