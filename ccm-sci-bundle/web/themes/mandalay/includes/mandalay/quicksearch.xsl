<?xml version="1.0" encoding="utf-8"?>
<!DOCTYPE stylesheet [<!ENTITY nbsp '&#160;'>]>

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
  Erzeuge die QuickSearch-Leiste
-->

<!-- EN
  build quicksearch box
-->

<!-- Autor: Sören Bernstein -->

<xsl:stylesheet 
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:bebop="http://www.arsdigita.com/bebop/1.0"
  xmlns:mandalay="http://mandalay.quasiweb.de"
  exclude-result-prefixes="xsl bebop mandalay"
  version="1.0">

  <xsl:template name="mandalay:quicksearch">
    <xsl:param name="layoutTree" select="."/>
    
    <!-- DE Hole alle benötigten Einstellungen-->
    <!-- EN Getting all needed setting-->
    <xsl:variable name="setPrefix">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="node"  select="$layoutTree/setPrefix"/>
        <xsl:with-param name="module"  select="'quicksearch'"/>
        <xsl:with-param name="setting" select="'setPrefix'"/>
        <xsl:with-param name="default" select="'true'"/>
      </xsl:call-template>
    </xsl:variable>
    <xsl:variable name="setSearchLink">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="node"  select="$layoutTree/setSearchLink"/>
        <xsl:with-param name="module"  select="'quicksearch'"/>
        <xsl:with-param name="setting" select="'setSearchLink'"/>
        <xsl:with-param name="default" select="'/search'"/>
      </xsl:call-template>
    </xsl:variable>
    <xsl:variable name="setPrefixAsLink">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="node"  select="$layoutTree/setPrefixAsLink"/>
        <xsl:with-param name="module"  select="'quicksearch'"/>
        <xsl:with-param name="setting" select="'setPrefixAsLink'"/>
        <xsl:with-param name="default" select="'true'"/>
      </xsl:call-template>
    </xsl:variable>
    <xsl:variable name="setSearchButton">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="node"  select="$layoutTree/setSearchButton"/>
        <xsl:with-param name="module"  select="'quicksearch'"/>
        <xsl:with-param name="setting" select="'setSearchButton'"/>
        <xsl:with-param name="default" select="'true'"/>
      </xsl:call-template>
    </xsl:variable>

    <form id="quicksearch" name="search" method="get" action="{$dispatcher-prefix}/search/">
      <p>
        <label for="quicksearchbox" accesskey="4">
          <xsl:if test="$setPrefix='false'">
            <xsl:attribute name="class">
              <xsl:value-of select="'hide'"/>
            </xsl:attribute>
          </xsl:if>
          <xsl:if test="$setPrefix='true' and $setPrefixAsLink = 'true'">
            <a>
              <xsl:attribute name="href">
                <xsl:call-template name="mandalay:linkParser">
                  <xsl:with-param name="link" select="$setSearchLink"/>
                </xsl:call-template>
              </xsl:attribute>
              <xsl:attribute name="title">
                <xsl:call-template name="mandalay:getStaticText">
                  <xsl:with-param name="module" select="'quicksearch'"/>
                  <xsl:with-param name="id" select="'prefix'"/>
                </xsl:call-template>
              </xsl:attribute>
              <xsl:call-template name="mandalay:getStaticText">
                <xsl:with-param name="module" select="'quicksearch'"/>
                <xsl:with-param name="id" select="'prefix'"/>
              </xsl:call-template>
            </a>
          </xsl:if>
          <xsl:if test="$setPrefix='true' and $setPrefixAsLink != 'true'">
            <xsl:call-template name="mandalay:getStaticText">
              <xsl:with-param name="module" select="'quicksearch'"/>
              <xsl:with-param name="id" select="'prefix'"/>
            </xsl:call-template>
          </xsl:if>
        </label>
        <div class="quicksearchWrapper">
          <input id="quicksearchbox" class="searchbox" name="terms">
            <xsl:attribute name="alt">
              <xsl:call-template name="mandalay:getStaticText">
                <xsl:with-param name="module" select="'quicksearch'"/>
                <xsl:with-param name="id" select="'prefix'"/>
              </xsl:call-template>
            </xsl:attribute>
            <xsl:attribute name="value">
              <xsl:call-template name="mandalay:getStaticText">
                <xsl:with-param name="module" select="'quicksearch'"/>
                <xsl:with-param name="id" select="'searchbox/value'"/>
              </xsl:call-template>
            </xsl:attribute>
          </input>
          <xsl:if test="$setSearchButton='true'">
            <input type="submit" class="go">
              <xsl:attribute name="name">
                <xsl:call-template name="mandalay:getStaticText">
                  <xsl:with-param name="module" select="'quicksearch'"/>
                  <xsl:with-param name="id" select="'submitButton/name'"/>
                </xsl:call-template>
              </xsl:attribute>
              <xsl:attribute name="value">
                <xsl:call-template name="mandalay:getStaticText">
                  <xsl:with-param name="module" select="'quicksearch'"/>
                  <xsl:with-param name="id" select="'submitButton/value'"/>
                </xsl:call-template>
              </xsl:attribute>
            </input>
          </xsl:if>
        </div>
      </p>
    </form>
  </xsl:template>

  <xsl:template match="useQuicksearch">
    <xsl:call-template name="mandalay:quicksearch"/>
  </xsl:template>

</xsl:stylesheet>
