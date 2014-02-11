<?xml version="1.0" encoding="utf-8"?>
<!DOCTYPE stylesheet [<!ENTITY nbsp '<xsl:text disable-output-escaping="yes">&amp;</xsl:text>nbsp;'>]>

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
  Hier werden die Suchergebnisse verarbeitet 
-->

<!-- EN
  Processing search results
-->

<!-- Autor: Sören Bernstein -->

<xsl:stylesheet 
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:bebop="http://www.arsdigita.com/bebop/1.0" 
  xmlns:cms="http://www.arsdigita.com/cms/1.0"
  xmlns:nav="http://ccm.redhat.com/navigation" 
  xmlns:search="http://rhea.redhat.com/search/1.0"
  xmlns:mandalay="http://mandalay.quasiweb.de"
  exclude-result-prefixes="xsl bebop cms nav mandalay" 
  version="1.0">
  
  <!-- DE Suchergebisse für die Webseiten werden als UL dargestellt -->
  <!-- EN Search result for webpages are translated to a ul -->
  <xsl:template match="search:documents">
    <xsl:param name="layoutTree" select="."/>
    
    <ul>
      <xsl:apply-templates>
        <xsl:with-param name="layoutTree" select="$layoutTree"/>
      </xsl:apply-templates>
    </ul>
  </xsl:template>
  
  <!-- DE Zeige das Suchergebis für die Admin-Oberfläche an -->
  <!-- EN Show search result for the admin pages -->
  <xsl:template match="search:documents" mode="admin">
    <xsl:param name="layoutTree" select="."/>
    
    <!-- DE Hole alle benötigten Einstellungen-->
    <!-- EN Getting all needed setting-->
    <xsl:variable name="setSummary">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="node"  select="$layoutTree/setSummary"/>
        <xsl:with-param name="module"  select="'search'"/>
        <xsl:with-param name="setting" select="'setSummary'"/>
        <xsl:with-param name="default" select="'true'"/>
      </xsl:call-template>
    </xsl:variable>
    <xsl:variable name="setAdminResultMode">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="node"  select="$layoutTree/setAdminResultMode"/>
        <xsl:with-param name="module"  select="'search'"/>
        <xsl:with-param name="setting" select="'setAdminResultMode'"/>
        <xsl:with-param name="default" select="'table'"/>
      </xsl:call-template>
    </xsl:variable>
    
    <xsl:choose>
      <xsl:when test="$setAdminResultMode = 'table'">
        <!-- DE Erzeuge Tabellenkopf für die Suchergebnisse -->
        <!-- EN Create table header for search results -->
        <table id="resultList">
          <tr class="resultListHeader">
            <th>
              <xsl:call-template name="mandalay:getStaticText">
                <xsl:with-param name="module" select="'search'"/>
                <xsl:with-param name="id" select="'listheader/score'"/>
              </xsl:call-template>
            </th>
            <th>
              <xsl:call-template name="mandalay:getStaticText">
                <xsl:with-param name="module" select="'search'"/>
                <xsl:with-param name="id" select="'listheader/title'"/>
              </xsl:call-template>
            </th>
            <xsl:if test="$setSummary = 'true'">
              <th class="summary">
                <xsl:call-template name="mandalay:getStaticText">
                  <xsl:with-param name="module" select="'search'"/>
                  <xsl:with-param name="id" select="'listheader/summary'"/>
                </xsl:call-template>
              </th>
            </xsl:if>
            <th style="width: 10em;">
              <xsl:call-template name="mandalay:getStaticText">
                <xsl:with-param name="module" select="'search'"/>
                <xsl:with-param name="id" select="'listheader/addlink'"/>
              </xsl:call-template>
            </th>
          </tr>
          <xsl:apply-templates mode="admin"/>
        </table>

<!-- Alternative Version ohne Tabellen. Funktioniert noch nicht
        <div class="resultListHeader">
          <span style="width: 4em;">
            <xsl:call-template name="mandalay:getStaticText">
              <xsl:with-param name="module" select="'search'"/>
              <xsl:with-param name="id" select="'listheader/score'"/>
            </xsl:call-template>
          </span>
          <span>
            <xsl:call-template name="mandalay:getStaticText">
              <xsl:with-param name="module" select="'search'"/>
              <xsl:with-param name="id" select="'listheader/title'"/>
            </xsl:call-template>
          </span>
          <span>
            <xsl:call-template name="mandalay:getStaticText">
              <xsl:with-param name="module" select="'search'"/>
              <xsl:with-param name="id" select="'listheader/summary'"/>
            </xsl:call-template>
          </span>
          <span style="width: 10em;">
            <xsl:call-template name="mandalay:getStaticText">
              <xsl:with-param name="module" select="'search'"/>
              <xsl:with-param name="id" select="'listheader/addlink'"/>
            </xsl:call-template>
          </span>
        </div>
-->
      
      </xsl:when>
      <xsl:when test="$setAdminResultMode = 'list'">
        <div id="resultList">
          <div class="resultListHeader">
            <xsl:call-template name="mandalay:getStaticText">
              <xsl:with-param name="module" select="'search'"/>
              <xsl:with-param name="id" select="'resultlist/header'"/>
            </xsl:call-template>
          </div>
          <ul>
            <xsl:apply-templates mode="admin"/>
          </ul>
        </div>
      </xsl:when>
    </xsl:choose>
  </xsl:template>
  
</xsl:stylesheet>
