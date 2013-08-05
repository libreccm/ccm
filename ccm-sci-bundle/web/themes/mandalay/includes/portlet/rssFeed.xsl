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
  Hier wird das Portlet RSS-Feed und Teile von WorkspaceDirectory verarbeitet 
-->

<!-- EN
  Processing portlet RSS-Feed and parts of workspaceDirectory
-->

<!-- Autor: Sören Bernstein -->

<xsl:stylesheet 
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:bebop="http://www.arsdigita.com/bebop/1.0"
  xmlns:cms="http://www.arsdigita.com/cms/1.0" 
  xmlns:nav="http://ccm.redhat.com/navigation"
  xmlns:mandalay="http://mandalay.quasiweb.de" 
  xmlns:portal="http://www.uk.arsdigita.com/portal/1.0"
  xmlns:portlet="http://www.uk.arsdigita.com/portlet/1.0"
  xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#"
  xmlns:rss="http://purl.org/rss/1.0/"
  xmlns:backslash="http://slashdot.org/backslash.dtd"
  exclude-result-prefixes="xsl bebop cms nav mandalay portal portlet rdf rss"
  version="1.0">

  <xsl:template match="portlet:RSSFeed" mode="setHeading">
    <xsl:variable name="setHeading">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="module" select="'portletRssFeed'"/>
        <xsl:with-param name="setting" select="'setHeading'"/>
        <xsl:with-param name="default" select="'true'"/>
      </xsl:call-template>
    </xsl:variable>
    
    <xsl:value-of select="$setHeading"/>
  </xsl:template>
  
  <xsl:template match="portlet:RSSFeed">
    <div class="RSSFeed">
      <xsl:apply-templates/>
    </div>
  </xsl:template>

  <xsl:template match="rdf:RDF">
    <xsl:param name="maxItems">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="module" select="'portletRssFeed'"/>
        <xsl:with-param name="setting" select="'setMaxItems'"/>
        <xsl:with-param name="default" select="'10'"/>
      </xsl:call-template>
    </xsl:param>
    <xsl:apply-templates select="channel"/>
    <xsl:for-each select="item[position() &lt; $maxItems]">
      <dl>
        <xsl:apply-templates select="."/>
      </dl>
    </xsl:for-each>
  </xsl:template>
  
  <xsl:template match="rss">
    <xsl:variable name="maxItems">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="module" select="'portletRssFeed'"/>
        <xsl:with-param name="setting" select="'setMaxItems'"/>
        <xsl:with-param name="default" select="'10'"/>
      </xsl:call-template>
    </xsl:variable>
    <xsl:apply-templates select="channel">
      <xsl:with-param name="maxItems" select="$maxItems"/>
    </xsl:apply-templates>
  </xsl:template>
  
  <xsl:template match="channel">
    <xsl:param name="maxItems"/>
    <a>
      <xsl:attribute name="href">
        <xsl:value-of select="link"/>
      </xsl:attribute>
      <xsl:value-of select="title"/>
    </a>
    <!-- DE Rückwärtskompatibilität mit altem RSS Format -->
    <!-- EN Backwards compatibility with old RSS format -->
    <dl>
      <xsl:for-each select="item[position() &lt; $maxItems]">
        <xsl:apply-templates select="."/>
      </xsl:for-each>
    </dl>
  </xsl:template>
  
  <xsl:template match="item">
    <dt>
      <a>
        <xsl:attribute name="href">
          <xsl:value-of select="link"/>
        </xsl:attribute>
        <xsl:value-of select="title"/>
      </a>
    </dt>
    <dd>
      <xsl:value-of select="description"/>
    </dd>
  </xsl:template>
  
  <xsl:template match="portlet:RSSFeedError">
    <div>
      <xsl:value-of select="." disable-output-escaping="no"/>
    </div>
  </xsl:template>

</xsl:stylesheet>
