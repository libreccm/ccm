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

<!--
  Hier wird der Paginator erzeugt, der bei langen Listen ein seitenweises Anzeigen ermöglicht
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

  <xsl:template name="mandalay:paginator">
    <xsl:param name="header" select="'true'"/>
    <xsl:param name="navbar" select="'true'"/>

    <xsl:if test="$header = 'true'">
      <xsl:call-template name="mandalay:paginatorHeader"/>
    </xsl:if>

    <xsl:if test="$navbar = 'true'">
      <xsl:call-template name="mandalay:paginatorNavbar"/>
    </xsl:if>

  </xsl:template>

  <xsl:template name="mandalay:paginatorHeader" match="nav:paginator | search:paginator" mode="header">
    <xsl:param name="layoutTree" select="."/>

    <!-- DE Hole alle benötigten Einstellungen-->
    <!-- EN Getting all needed setting-->
    <xsl:variable name="setResultInfo">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="node"  select="$layoutTree/setResultInfo"/>
        <xsl:with-param name="module"  select="'paginator'"/>
        <xsl:with-param name="setting" select="'setResultInfo'"/>
        <xsl:with-param name="default" select="'true'"/>
      </xsl:call-template>
    </xsl:variable>
    <xsl:variable name="setResultBegin">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="node"  select="$layoutTree/setResultBegin"/>
        <xsl:with-param name="module"  select="'paginator'"/>
        <xsl:with-param name="setting" select="'setResultBegin'"/>
        <xsl:with-param name="default" select="'true'"/>
      </xsl:call-template>
    </xsl:variable>
    <xsl:variable name="setResultEnd">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="node"  select="$layoutTree/setResultEnd"/>
        <xsl:with-param name="module"  select="'paginator'"/>
        <xsl:with-param name="setting" select="'setResultEnd'"/>
        <xsl:with-param name="default" select="'true'"/>
      </xsl:call-template>
    </xsl:variable>
    <xsl:variable name="setMaxResults">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="node"  select="$layoutTree/setMaxResults"/>
        <xsl:with-param name="module"  select="'paginator'"/>
        <xsl:with-param name="setting" select="'setMaxResults'"/>
        <xsl:with-param name="default" select="'true'"/>
      </xsl:call-template>
    </xsl:variable>
    <xsl:variable name="setPageInfo">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="node"  select="$layoutTree/setPageInfo"/>
        <xsl:with-param name="module"  select="'paginator'"/>
        <xsl:with-param name="setting" select="'setPageInfo'"/>
        <xsl:with-param name="default" select="'true'"/>
      </xsl:call-template>
    </xsl:variable>

    <!-- DE Variablen aus dem ResultTree -->
    <xsl:variable name="objectBegin" select="@objectBegin"/>
    <xsl:variable name="objectCount" select="@objectCount"/>
    <xsl:variable name="objectEnd"   select="@objectEnd"/>
    <xsl:variable name="pageSize"    select="@pageSize"/>

    <xsl:if test="@pageCount &gt; 1">
      <div class="paginator header">
        <xsl:if test="$setResultInfo = 'true'">
          <xsl:call-template name="mandalay:getStaticText">
            <xsl:with-param name="module" select="'paginator'"/>
            <xsl:with-param name="id" select="'header/resultinfo/text/begin'"/>
          </xsl:call-template>
          <xsl:if test="$setResultBegin = 'true'">
            <xsl:value-of select="$objectBegin"/>
            <xsl:if test="$setResultEnd = 'true'">
              <xsl:call-template name="mandalay:getStaticText">
                <xsl:with-param name="module" select="'paginator'"/>
                <xsl:with-param name="id" select="'header/resultinfo/text/inbetween1'"/>
              </xsl:call-template>
              <xsl:value-of select="$objectEnd"/>
            </xsl:if>
          </xsl:if>

          <xsl:if test="$setMaxResults = 'true'">
            <xsl:call-template name="mandalay:getStaticText">
              <xsl:with-param name="module" select="'paginator'"/>
              <xsl:with-param name="id" select="'header/resultinfo/text/inbetween2'"/>
            </xsl:call-template>
            <xsl:value-of select="$objectCount"/>
          </xsl:if>
          <xsl:call-template name="mandalay:getStaticText">
            <xsl:with-param name="module" select="'paginator'"/>
            <xsl:with-param name="id" select="'header/resultinfo/text/end'"/>
          </xsl:call-template>
        </xsl:if>

        &nbsp;
        <xsl:if test="$setPageInfo = 'true'">
          <xsl:call-template name="mandalay:getStaticText">
            <xsl:with-param name="module" select="'paginator'"/>
            <xsl:with-param name="id" select="'header/pageinfo/text/begin'"/>
          </xsl:call-template>
          <xsl:value-of select="$pageSize"/>
          <xsl:call-template name="mandalay:getStaticText">
            <xsl:with-param name="module" select="'paginator'"/>
            <xsl:with-param name="id" select="'header/pageinfo/text/end'"/>
          </xsl:call-template>
        </xsl:if>

      </div>
    </xsl:if>

  </xsl:template>

  <xsl:template name="mandalay:paginatorNavbar" match="nav:paginator | search:paginator" mode="navbar">
    <xsl:param name="layoutTree" select="."/>

    <!-- DE Hole alle benötigten Einstellungen-->
    <!-- EN Getting all needed setting-->
    <xsl:variable name="separator">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="node"  select="$layoutTree/separator"/>
        <xsl:with-param name="module"  select="'paginator'"/>
        <xsl:with-param name="setting" select="'navbar/separator'"/>
        <xsl:with-param name="default" select="''"/>
      </xsl:call-template>
    </xsl:variable>
    <xsl:variable name="setPageSizeAtNavLinks">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="node"  select="$layoutTree/setPageSizeAtNavLinks"/>
        <xsl:with-param name="module"  select="'paginator'"/>
        <xsl:with-param name="setting" select="'setPageSizeAtNavLinks'"/>
        <xsl:with-param name="default" select="'false'"/>
      </xsl:call-template>
    </xsl:variable>
    <xsl:variable name="setPage">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="node"  select="$layoutTree/setPage"/>
        <xsl:with-param name="module"  select="'paginator'"/>
        <xsl:with-param name="setting" select="'setPage'"/>
        <xsl:with-param name="default" select="'true'"/>
      </xsl:call-template>
    </xsl:variable>
    <xsl:variable name="setMaxPage">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="node"  select="$layoutTree/setMaxPage"/>
        <xsl:with-param name="module"  select="'paginator'"/>
        <xsl:with-param name="setting" select="'setMaxPage'"/>
        <xsl:with-param name="default" select="'true'"/>
      </xsl:call-template>
    </xsl:variable>

    <!-- DE Variablen aus dem ResultTree -->
    <xsl:variable name="pageCount"  select="@pageCount"/>
    <xsl:variable name="pageNumber" select="@pageNumber"/>
    <xsl:variable name="pageSize"   select="@pageSize"/>
    
    <xsl:variable name="pageParam">
      <xsl:choose>
        <xsl:when test="@pageParam">
          <xsl:value-of select="@pageParam"/>
        </xsl:when>
        <xsl:otherwise>
          <xsl:text>pageNumber=</xsl:text>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:variable>

    <xsl:if test="@pageCount &gt; 1">
      <div class="paginator navbar">

        <xsl:variable name="url">
          <xsl:choose>

            <xsl:when test="@baseURL and contains(@baseURL, '?')">
              <xsl:value-of select="concat(@baseURL, '&amp;')"/>
            </xsl:when>

            <xsl:when test="@baseURL and not(contains(@baseURL, '?'))">
              <xsl:value-of select="concat(@baseURL, '?')"/>
            </xsl:when>

            <xsl:when test="not(@baseURL) and //nav:letter and contains(//nav:letter[@selected = '1']/@url, '?')">
              <xsl:value-of select="concat(//nav:letter[@selected = '1']/@url, '&amp;')"/>
            </xsl:when>

            <xsl:when test="not(@baseURL) and //nav:letter and not(contains(//nav:letter[@selected = '1']/@url, '?'))">
              <xsl:value-of select="concat(//nav:letter[@selected = '1']/@url, '?')"/>
            </xsl:when>

            <xsl:when test="not(@baseURL) and not(//nav:letter) and not(contains(/bebop:page/@url, '?'))">
              <xsl:value-of select="concat(/bebop:page/@url, '?')"/>
            </xsl:when>

            <xsl:otherwise/>
          </xsl:choose>
        </xsl:variable>

        <!-- DE Zurück-Button -->
        <!-- EN back button -->
        <xsl:if test="$pageNumber &gt; 1">
          <a class="prev">
            <xsl:attribute name="href"><xsl:value-of select="concat($url, $pageParam, '=', $pageNumber - 1)"/></xsl:attribute>
            <xsl:attribute name="accesskey">
              <xsl:call-template name="mandalay:getStaticText">
                <xsl:with-param name="module" select="'paginator'"/>
                <xsl:with-param name="setting" select="'navbar/last/accesskey'"/>
              </xsl:call-template>
            </xsl:attribute>
            <xsl:attribute name="title">
              <xsl:call-template name="mandalay:getStaticText">
                <xsl:with-param name="module" select="'paginator'"/>
                <xsl:with-param name="id" select="'navbar/last/title'"/>
              </xsl:call-template>
            </xsl:attribute>
            <xsl:call-template name="mandalay:getStaticText">
              <xsl:with-param name="module" select="'paginator'"/>
              <xsl:with-param name="id" select="'navbar/last/link'"/>
            </xsl:call-template>
            <xsl:if test="$setPageSizeAtNavLinks = 'true'">
              <xsl:value-of select="$pageSize"/>
            </xsl:if>
          </a>
          <!-- DE Separator -->
          <!-- EN Separator -->
          <xsl:if test="$setPage = 'true' or $pageNumber &lt; $pageCount">
            <xsl:value-of select="$separator"/>
          </xsl:if>
        </xsl:if>
  
        <!-- DE Seitenanzeige -->
        <!-- EN Pagenumber -->
        <xsl:if test="$setPage = 'true'">
          <span class="pages">
            <xsl:call-template name="mandalay:getStaticText">
              <xsl:with-param name="module" select="'paginator'"/>
              <xsl:with-param name="id" select="'navbar/pageNumber/prefix'"/>
            </xsl:call-template>
            <xsl:value-of select="$pageNumber"/>
            <xsl:if test="$setMaxPage = 'true'">
              <xsl:call-template name="mandalay:getStaticText">
                <xsl:with-param name="module" select="'paginator'"/>
                <xsl:with-param name="id" select="'navbar/pageNumber/separator'"/>
              </xsl:call-template>
              <xsl:value-of select="$pageCount"/>
            </xsl:if>
          </span>
        </xsl:if>
  
        <!-- DE Weiter-Button -->
        <!-- EN next button -->
        <xsl:if test="$pageNumber &lt; $pageCount">
          <!-- DE Separator -->
          <!-- EN Separator -->
          <xsl:if test="$setPage = 'true'">
            <xsl:value-of select="$separator"/>
          </xsl:if>
          <a class="next">
            <xsl:attribute name="href"><xsl:value-of select="concat($url, $pageParam, '=', $pageNumber + 1)"/></xsl:attribute>
            <xsl:attribute name="accesskey">
              <xsl:call-template name="mandalay:getStaticText">
                <xsl:with-param name="module" select="'paginator'"/>
                <xsl:with-param name="setting" select="'navbar/next/accesskey'"/>
              </xsl:call-template>
            </xsl:attribute>
            <xsl:attribute name="title">
              <xsl:call-template name="mandalay:getStaticText">
                <xsl:with-param name="module" select="'paginator'"/>
                <xsl:with-param name="id" select="'navbar/next/title'"/>
              </xsl:call-template>
            </xsl:attribute>
            <xsl:call-template name="mandalay:getStaticText">
              <xsl:with-param name="module" select="'paginator'"/>
              <xsl:with-param name="id" select="'navbar/next/link'"/>
            </xsl:call-template>
            <xsl:if test="$setPageSizeAtNavLinks = 'true'">
              <!-- DE Besondere Behandlung für die vorletzte Seite -->
              <!-- EN special handling for the page second to last -->
              <xsl:choose>
                <xsl:when test="$pageNumber = ($pageCount - 1)">
                  <xsl:value-of select="@objectCount - @objectEnd"/>
                </xsl:when>
                <xsl:otherwise>
                  <xsl:value-of select="$pageSize" />
                </xsl:otherwise>
              </xsl:choose>
            </xsl:if>
          </a>
        </xsl:if>
        <div class="endFloat"/>
      </div>
    </xsl:if>

  </xsl:template>

</xsl:stylesheet>
