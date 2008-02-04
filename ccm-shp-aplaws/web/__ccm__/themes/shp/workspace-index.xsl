<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet 
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
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

  <xsl:import href="../../../../ROOT/__ccm__/apps/workspace/xsl/index.xsl"/>
  <xsl:import href="portal/lib.xsl"/>
  <xsl:import href="portal/portlets.xsl"/>

  <xsl:import href="lib/header.xsl"/>
  <xsl:import href="lib/lib.xsl"/>
  <xsl:import href="lib/leftNav.xsl"/>

<!-- Eigene XSL-Dateien importieren -->
  <xsl:import href="lib/pageLayout.xsl"/>

  <xsl:param name="context-prefix"></xsl:param>
  <xsl:param name="dispatcher-prefix" />
  <xsl:param name="theme-prefix" />

  <xsl:output 
    method="html"
    doctype-public="-//W3C//DTD HTML 4.01 Strict//EN"
    doctype-system="http://www.w3.org/TR/html4/strict.dtd"
    indent="yes" 
  />

  <xsl:template match="bebop:page[@application='portal']">
    <xsl:call-template name="shp:pageLayout"/>
  </xsl:template>

<!-- BODY -->
  <xsl:template name="wsBody">
    <table id="mainLayout" width="100%" border="0" cellspacing="0" cellpadding="5" summary="This table is used for a three-column page layout">
      <tr>
        <td width="20%" align="left" valign="top" >
          <!--VOID LHS Column -->
        </td>
        <td colspan="2" align="left" valign="top" id="portalTitle">
          <!--TITLE -->
        </td>
      </tr>
      <tr>
        <td width="20%" align="left" valign="top" id="portalLHS">
          <!--LHS Column -->
          <a id="startcontent" title="Start of content"></a>
          <xsl:apply-templates select="//portal:homepageWorkspace[@id='left']/portal:portal"/>
          <xsl:for-each select="//portal:homepageWorkspace[@id='left']/bebop:link">
            <div class="custLink"><xsl:apply-templates select="."/></div>
          </xsl:for-each>
        </td>
        <td width="60%" height="100%" align="left" valign="top" id="mainContent">
          <!--Middle Column -->
          <xsl:apply-templates select="//portal:homepageWorkspace[@id='middle']/portal:portal"/>
          <xsl:for-each select="//portal:homepageWorkspace[@id='middle']/bebop:link">
            <div class="custLink"><xsl:apply-templates select="."/></div>
          </xsl:for-each>
        </td>
        <td width="20%" height="400" align="left" valign="top" id="RHS">
          <!--RHS Column -->
          <xsl:apply-templates select="//portal:homepageWorkspace[@id='right']/portal:portal"/>
          <xsl:for-each select="//portal:homepageWorkspace[@id='right']/bebop:link">
            <div class="custLink"><xsl:apply-templates select="."/></div>
          </xsl:for-each>
        </td>
      </tr>
    </table>
    <xsl:call-template name="shp:footer"/>
  </xsl:template>

<!-- ???????? -->
  <xsl:template match="portal:homepageWorkspace/bebop:link">
    <xsl:choose>
      <xsl:when test="./bebop:label='browse'"><a href="{@href}"  class="adminReturn" title="Return to the standard page view">back to user's view</a></xsl:when>
      <xsl:when test="./bebop:label='reset'"><a href="{@href}" class="adminWarning"  title="WARNING!!: This link will delete the whole area!">delete area</a></xsl:when>
      <xsl:when test="./bebop:label='customize'"><a href="{@href}" title="Customise this part of the page">customise area</a></xsl:when>
      <xsl:otherwise><xsl:apply-templates /></xsl:otherwise>
    </xsl:choose>
  </xsl:template>

<xsl:template name="mainContent">
</xsl:template>

<!-- ???????? -->
  <xsl:template match="bebop:link">
    <a href="{@href}"><xsl:value-of select="bebop:label"/>
      <xsl:apply-templates select="bebop:image"/>
    </a>
  </xsl:template>

<!-- ???????? -->
  <xsl:template match="bebop:label">
    <xsl:value-of select="."/>
  </xsl:template>

<!-- ???????? -->
  <xsl:template match="bebop:image">
    <img>
      <xsl:attribute name="alt"><xsl:value-of select="@alt"/></xsl:attribute>
      <xsl:attribute name="src">
        <xsl:call-template name="imagePath">
          <xsl:with-param name="link" select="@src" />
        </xsl:call-template>
      </xsl:attribute>
    </img>
  </xsl:template>

<!-- ???????? -->
  <xsl:template name="imagePath">
    <xsl:param name="link" />
    <xsl:value-of select="$context-prefix"/><xsl:value-of select="$link"/>
  </xsl:template>

  <!-- Callback-Funktion -->
  <xsl:template name="shp:navAddOn">
  
  </xsl:template>

</xsl:stylesheet>
