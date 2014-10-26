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

<!-- This file was copied from Mandalay and edited to work with Foundry. -->

<!-- EN
  Processing search results
-->

<!-- Autor: Sören Bernstein -->

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:bebop="http://www.arsdigita.com/bebop/1.0" 
                xmlns:cms="http://www.arsdigita.com/cms/1.0"
                xmlns:foundry="http://foundry.libreccm.org" 
                xmlns:nav="http://ccm.redhat.com/navigation" 
                xmlns:search="http://rhea.redhat.com/search/1.0"
                exclude-result-prefixes="xsl bebop cms foundry nav" 
                version="2.0">
  
    <!-- DE Suchergebisse für die Webseiten werden als UL dargestellt -->
    <!-- EN Search result for webpages are translated to a ul -->
    <xsl:template match="search:documents">
        <xsl:param name="layout-tree" select="."/>
    
        <ul>
            <xsl:apply-templates>
                <xsl:with-param name="layout-tree" select="$layout-tree"/>
            </xsl:apply-templates>
        </ul>
    </xsl:template>
  
    <!-- DE Zeige das Suchergebis für die Admin-Oberfläche an -->
    <!-- EN Show search result for the admin pages -->
    <xsl:template match="search:documents" mode="admin">
        <xsl:param name="layout-tree" select="."/>
    
        <!-- DE Hole alle benötigten Einstellungen-->
        <!-- EN Getting all needed setting-->
        <xsl:variable name="show-summary" 
                      select="foundry:get-setting('search', 
                                                  'show-summary', 
                                                  'true', $layout-tree/show-summary)"/>
        <xsl:variable name="admin-result-mode" 
                  select="foundry:get-setting('search', 
                                              'admin-result-mode', 
                                              'table', 
                                              $layout-tree/admin-result-mode)"/>
    
        <xsl:choose>
            <xsl:when test="$admin-result-mode = 'table'">
                <!-- DE Erzeuge Tabellenkopf für die Suchergebnisse -->
                <!-- EN Create table header for search results -->
                <table id="result-list">
                    <tr class="result-list-header">
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
                        <xsl:if test="$show-summary = 'true'">
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
                        <div class="result-list-header">
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
            <xsl:when test="$admin-result-mode = 'list'">
                <div id="result-list">
                    <div class="result-list-header">
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
