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
  exclude-result-prefixes="xsl bebop aplaws ui cms nav search atoz"
  version="1.0">

<xsl:import href="../../../../ROOT/__ccm__/apps/content-section/xsl/index.xsl"/>
	
<xsl:import href="lib/header.xsl"/>
<xsl:import href="lib/lib.xsl"/>
<xsl:import href="../../../../ROOT/packages/bebop/xsl/dcp.xsl"/>
   
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
<html lang="en">
	<head>
		<title>APLAWS+: A to Z</title>
		<xsl:call-template name="cssStyles"/>
	</head>
	
	<body>
        <xsl:call-template name="bebop:dcpJavascript"/>
		<a class="navHide" href="#startcontent" title="Go directly to main content" accesskey="S">Skip over navigation</a>		
		<span class="hide">|</span>
		<xsl:call-template name="header"/>
		<xsl:call-template name="azBreadcrumb"/>
		<xsl:call-template name="azBody"/>
  </body>
</html>
</xsl:template>




<xsl:template name="azBody">
<table id="mainLayout" width="100%" border="0" cellspacing="0" cellpadding="0" summary="This table is used for a three-column page layout">
<tr>
    <td width="20%" rowspan="2" align="left" valign="top" id="LHS">
		<!--LHS NAVIGATION -->
		<xsl:call-template name="azNav" />
		</td>
    <td colspan="2" align="left" valign="top" id="title">
		<!--MAIN CONTENT -->
		<h1>A to Z List</h1>
		</td>
	</tr>
  <tr>
    <td width="60%" align="left" valign="top" id="mainContent">
		<span class="hide">|</span>
		<!--CONTENT -->
		<a id="startcontent" title="Start of content"></a>
		<span class="hide">|</span>
		<xsl:call-template name="azMain" />	
	</td>
	<td width="20%" height="400" align="left" valign="top" id="RHS">
  </td>
</tr>
</table>
<xsl:call-template name="footer"/>
</xsl:template>








<xsl:template name="azBreadcrumb">
<div id="bread">
<p>
<b><a href="{$dispatcher-prefix}/portal/" title="home">home</a></b>
<xsl:text disable-output-escaping="yes">&amp;</xsl:text>gt;
<span class="breadHi">A to Z</span>
</p>
</div>
</xsl:template>




<xsl:template name="azNav">
<div id="nav">
<div class="navUp">
<a href="{$dispatcher-prefix}/portal/">
<xsl:attribute name="title">up to homepage</xsl:attribute>
<xsl:text disable-output-escaping="yes">&amp;</xsl:text>#094;
<xsl:text disable-output-escaping="yes">&amp;</xsl:text>nbsp;home</a>
</div>
<div class="navHere"><p>A to Z</p></div>
<a href="/atozhelp" class="navChild" title="A to Z explained" >A to Z explained</a><span class="hide">|</span>
</div>
</xsl:template>


<xsl:template name="azMain">
<a class="navHide" href="#startAZResults" title="Go directly to A-Z results">Skip to A-Z results</a>
<xsl:for-each select="atoz:atoz">
	<div id="azSub">Browse the alphabetically listed information and services below</div>
	<div id="azArea">
	<div id="azInfo">
	<xsl:for-each select="atoz:letter">
	<a>
	<xsl:attribute name="href">?letter=<xsl:value-of select="." /></xsl:attribute>
	<xsl:value-of select="." /></a>
	<xsl:if test="position()!='last'"> | </xsl:if>
	</xsl:for-each>
	</div>
	<div id="azPlace">Categories starting with the letter <span class="letterSelected"><xsl:value-of select="atoz:letter[@isSelected]" /></span></div>
</div>
<a id="startAZResults" title="Start of A-Z results"></a>
<xsl:call-template name="azResults" />
</xsl:for-each>
</xsl:template>






<xsl:template name="azResults">
<xsl:for-each select="./atoz:provider">
<div id="azList">
	<h2><xsl:value-of select="@title" /><span class="azTitleDescription"><xsl:text>[</xsl:text><xsl:value-of select="@description" /><xsl:text>]</xsl:text></span></h2>
	<xsl:for-each select="atoz:atomicEntry">
	<div class="azResult">
	<a href="{@url}" title="{@description}"><xsl:value-of select="@title" /></a>
	</div>
	</xsl:for-each>
</div>
</xsl:for-each>
</xsl:template>




</xsl:stylesheet>
