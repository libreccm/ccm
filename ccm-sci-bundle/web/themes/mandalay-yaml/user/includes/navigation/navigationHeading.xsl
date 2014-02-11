<?xml version="1.0" encoding="utf-8"?>
<!DOCTYPE stylesheet [<!ENTITY nbsp '&#160;'>
<!ENTITY shy '&#173;'>]>

<!-- 
    Copyright: 2006, 2007, 2008 Sören Bernstein
  
    This file is part of Mandalay.

    Mandalay is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 2 of the License, or
    (at your option) any later version.

    Mandalay is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with Mandalay.  If not, see <http://www.gnu.org/licenses/>.
-->

<!-- DE 
  Hier wird das Menu von Navigation erzeugt
  Dazu wird eine rekursive Funktion verwendet, so daß die Schachtelungstiefe beliebig ist.
  Erzeugt wird eine <ul><li>-Struktur die mittels CSS formatiert werden kann.
  Zudem wird eine Call-Back-Funktion aufgerufen, mit deren Hilfe zusätzliche Menüpunkte
  dem Menü vorangestellt werden können.
  
  Hinweis:
  Hier kann die Funktion mandalay:getColorset nicht verwendet werden wenn die verschiedenen
  Menüpunkte in ihren jeweiligen Farben eingefärbt werden sollen.
-->

<!-- EN
  Processing navigation-tags to build menu
  Using a recursive function to build an <ul><li>-structur of any levels formated by css.
  Also, a call back function is used to manually prepend some menu entries
-->

<!-- Autor: Sören Bernstein -->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:bebop="http://www.arsdigita.com/bebop/1.0"
  xmlns:nav="http://ccm.redhat.com/navigation" xmlns:mandalay="http://mandalay.quasiweb.de"
  exclude-result-prefixes="xsl bebop nav mandalay" version="1.0">

  <xsl:template name="mandalay:navigationHeading">
    <xsl:param name="layoutTree" select="."/>

    <xsl:variable name="setDescription">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="node" select="$layoutTree/setDescription"/>
        <xsl:with-param name="module" select="'navigation'"/>
        <xsl:with-param name="setting" select="'menu/setDescription'"/>
        <xsl:with-param name="default" select="'true'"/>
      </xsl:call-template>
    </xsl:variable>
    
    <xsl:variable name="setLevel">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="node" select="$layoutTree/setLevel"/>
        <xsl:with-param name="module" select="'navigation'"/>
        <xsl:with-param name="setting" select="'menu/heading/setLevel'"/>
        <xsl:with-param name="default" select="'0'"/>
      </xsl:call-template>
    </xsl:variable>

    <xsl:apply-templates select="$resultTree/nav:categoryMenu/nav:category[@isSelected='true']" mode="heading">
      <xsl:with-param name="headingLevel" select="$setLevel"/>
      <xsl:with-param name="setDescription" select="$setDescription"/>
    </xsl:apply-templates>
  </xsl:template>

  <xsl:template match="nav:category" mode="heading">
    <xsl:param name="level" select="0"/>
    <xsl:param name="headingLevel" select="0"/>
    <xsl:param name="setDescription" select="'true'"/>
    
    <xsl:choose>
      <xsl:when test="$level = $headingLevel and nav:category">
        <xsl:variable name="cleanTitle">
          <xsl:call-template name="mandalay:shying">
            <xsl:with-param name="title">
              <xsl:choose>
                <xsl:when test="$setDescription = 'true' and @description != ''">
                  <xsl:value-of select="@description"/>
                </xsl:when>
                <xsl:otherwise>
                  <xsl:value-of select="@title"/>
                </xsl:otherwise>
              </xsl:choose>
            </xsl:with-param>
            <xsl:with-param name="mode">dynamic</xsl:with-param>
          </xsl:call-template>
        </xsl:variable>

        <!-- DE Titel unverändert oder mit manuell ersetztem &shy; -->
        <xsl:variable name="title">
          <xsl:call-template name="mandalay:shying">
            <xsl:with-param name="title">
              <xsl:value-of select="@title"/>
            </xsl:with-param>
            <xsl:with-param name="mode">force</xsl:with-param>
          </xsl:call-template>
        </xsl:variable>

        <div class="menuHeading">
          <a href="{@url}" title="{$cleanTitle}">
            <span>
              <xsl:value-of select="$title"/>
            </span>
          </a>
        </div>
      </xsl:when>
      <xsl:otherwise>
        <xsl:apply-templates select="nav:category[@isSelected='true']" mode="heading">
          <xsl:with-param name="level" select="$level + 1"/>
          <xsl:with-param name="headingLevel" select="$headingLevel"/>
        </xsl:apply-templates>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

</xsl:stylesheet>
