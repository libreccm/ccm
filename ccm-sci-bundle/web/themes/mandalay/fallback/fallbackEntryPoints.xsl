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

<xsl:stylesheet 
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:bebop="http://www.arsdigita.com/bebop/1.0"
  xmlns:nav="http://ccm.redhat.com/navigation"
  xmlns:mandalay="http://mandalay.quasiweb.de"
  xmlns:cms="http://www.arsdigita.com/cms/1.0"
  xmlns:ui="http://www.arsdigita.com/ui/1.0"
  xmlns:aplaws="http://www.arsdigita.com/aplaws/1.0"
  exclude-result-prefixes="xsl bebop nav cms ui aplaws mandalay"
  version="1.0">

  <xsl:import href="../../../../../ROOT/packages/content-section/xsl/cms.xsl"/>
  <xsl:import href="../../../../../ROOT/packages/content-section/xsl/content-center.xsl"/>
  <xsl:import href="../../../../../ROOT/__ccm__/assets/notes/index.xsl"/>

  <xsl:import href="../fallback/admin.xsl"/>
  <xsl:import href="../fallback/admin-navigation.xsl"/>
  <xsl:import href="../fallback/admin-search.xsl"/>
  <xsl:import href="../fallback/admin-subsite.xsl"/>
  <xsl:import href="../fallback/admin-terms.xsl"/>
  <xsl:import href="../fallback/admin-themes.xsl"/>

  <xsl:import href="../fallback/admin-category-step.xsl"/> <!-- Support for AJAXed load-cat.jsp -->
  <xsl:import href="../../../../../ROOT/packages/bebop/xsl/dcp.xsl"/>
  <xsl:variable name="here"><xsl:value-of select="$static-prefix"/>/cms/admin/page</xsl:variable>

  <!-- DE: Einstiegspunkt -->
  <xsl:template name="mandalay:fallbackEntryPoint">

    <xsl:choose>
      <!-- DE Adminoberfläche  -->
      <xsl:when test="@class='cms-admin'">
        <xsl:call-template name="mandalay:fallbackAdminLayout"/>
      </xsl:when>

      <xsl:otherwise>
        <xsl:call-template name="mandalay:fallbackAplawsPage"/>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>
  
  <!-- DE: Ab hier folgen die "geliehenen" Templates des Standard-Aplaws-Theme -->
    <xsl:template name="mandalay:fallbackAdminLayout">
    <html xmlns:deditor="http://www.arsdigita.com/deditor/1.0">
      <head>
        <title><xsl:value-of select="bebop:title"/></title>
        <link rel="stylesheet" type="text/css" href="{$legacy-asset-prefix}/css/acs-master.css"/>
        <link rel="stylesheet" type="text/css" href="{$static-prefix}/cms/admin/cms-admin.css"/>
        <xsl:call-template name="mandalay:fallbackHeaderStyleSheets"/>
      </head>
      <body>
        <xsl:call-template name="bebop:dcpJavascript"/>
        <table id="global-header">
          <tr>
            <td id="logo"><img src="{$theme-prefix}/images/aplaws-logo-small.png" height="30" width="30"/></td>
            <td id="context"><xsl:apply-templates select="bebop:contextBar"/></td>
            <td style="margin: 0; color: #3f3f3f; border: 0; padding: 0; padding-right: 18px;">Welcome <strong><xsl:value-of select="@name" /></strong></td>
            <td>
              <table id="global-links">
                <tr>
                  <td class="global-link-icon"><a href="{cms:globalNavigation/cms:workspace/@href}"><img src="{$here}/home.png" height="18" width="18"/></a></td>
                  <td class="global-link"><a href="{cms:globalNavigation/cms:workspace/@href}">Home</a></td>

                  <td class="global-link-icon"><a href="{cms:globalNavigation/cms:signOut/@href}"><img src="{$here}//lock.png" height="18" width="14"/></a></td>
                  <td class="global-link"><a href="{cms:globalNavigation/cms:signOut/@href}">Logout</a></td>
                </tr>
              </table>
            </td>
          </tr>
        </table>
        <div id="page-title"><xsl:value-of select="bebop:title"/></div>
        <xsl:if test="bebop:link[@id = 'preview_link']">
          <xsl:choose>
            <xsl:when test="bebop:link[@target]">
              <div style="float: right; margin-right: 12px"><a target="{bebop:link/@target}" href="{bebop:link[@id = 'preview_link']/@href}"><xsl:value-of select="bebop:link[@id = 'preview_link']/bebop:label"/></a></div>
            </xsl:when>
            <xsl:otherwise>
              <div style="float: right; margin-right: 12px"><a href="{bebop:link[@id = 'preview_link']/@href}"><xsl:value-of select="bebop:link[@id = 'preview_link']/bebop:label"/></a></div>
            </xsl:otherwise>
          </xsl:choose>
        </xsl:if>
        <div><xsl:apply-templates select="*[@id = 'page-body']"/></div>
        <xsl:choose>
          <xsl:when test="bebop:structure">
            <div><xsl:apply-templates select="bebop:structure"/></div>
          </xsl:when>
          <xsl:when test="ui:debugPanel">
            <div><xsl:apply-templates select="ui:debugPanel"/></div>            
          </xsl:when>
        </xsl:choose>
      </body>
    </html>
  </xsl:template>

  <xsl:template name="mandalay:fallbackAplawsPage">
    <html>
      <head>
        <title><xsl:value-of select="/bebop:page/bebop:title"/></title>
        <meta name="title">
          <xsl:attribute name="content">
            <xsl:value-of select="/bebop:page/bebop:title"/>
          </xsl:attribute>
        </meta>
        <link rel="stylesheet" href="{$legacy-asset-prefix}/css/acs-master.css" type="text/css" media="screen"/>
        <xsl:call-template name="mandalay:fallbackHeaderStyleSheets"/>
      </head>

      <body style="margin: 0em">
        <xsl:call-template name="bebop:dcpJavascript"/>
        <div class="bodyHeader">
          <xsl:apply-templates select="ui:userBanner"/>
        </div>
        <div class="bodyBreadcrumb">
          <a href="/">APLAWS</a>&gt;<xsl:value-of select="/bebop:page/bebop:title"/>
        </div>
        <h1><xsl:value-of select="/bebop:page/bebop:title"/></h1>
        <div class="bodyContent">
          <xsl:apply-templates select="*[not(@metadata.tag)]"/>
        </div>
        <div class="bodyFooter">
          <xsl:apply-templates select="ui:siteBanner"/>
        </div>
        <div class="bodyDebug">
          <xsl:apply-templates select="ui:debugPanel"/>
          <xsl:apply-templates select="bebop:structure"/>
        </div>
      </body>
    </html>
  </xsl:template>

  <xsl:template name="cat-widget-cat-name">
    <xsl:value-of select="@name"/>

    <xsl:if test="@pid and @domain='LGSL'">
        (<xsl:value-of select="@pid"/>)
    </xsl:if>
  </xsl:template>

  <xsl:template name="mandalay:fallbackHeaderStyleSheets">
    <xsl:choose>
      <xsl:when test="@application='navigation'">
        <link rel="stylesheet" href="/__ccm__/apps/navigation/xsl/admin.css" type="text/css" media="screen"/>
      </xsl:when>
      <xsl:when test="@application='terms'">
        <link rel="stylesheet" href="/ccm-ldn-terms/__ccm__/apps/terms/xsl/index.css" type="text/css" media="screen"/>
      </xsl:when>
<!--
      <xsl:when test="@application=''">
        <link rel="stylesheet" href="{$theme-prefix}/css/fallback/terms-index.css" type="text/css" media="screen"/>
      </xsl:when>
-->
    </xsl:choose>
  </xsl:template>

</xsl:stylesheet>
