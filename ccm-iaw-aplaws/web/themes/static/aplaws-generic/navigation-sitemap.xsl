<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                 xmlns:bebop="http://www.arsdigita.com/bebop/1.0"
                xmlns:aplaws="http://www.arsdigita.com/aplaws/1.0"
                    xmlns:ui="http://www.arsdigita.com/ui/1.0"
                   xmlns:cms="http://www.arsdigita.com/cms/1.0"
                   xmlns:nav="http://ccm.redhat.com/london/navigation"
                xmlns:search="http://rhea.redhat.com/search/1.0"
                  xmlns:atoz="http://xmlns.redhat.com/atoz/1.0"
     exclude-result-prefixes="xsl bebop aplaws ui cms nav search atoz"
                     version="1.0">


	
<xsl:import href="lib/header.xsl"/>
<xsl:import href="lib/lib.xsl"/>
<xsl:import href="../../heirloom/packages/bebop/xsl/dcp.xsl"/>

<xsl:param name="context-prefix"></xsl:param>
<xsl:param name="dispatcher-prefix" />
<xsl:param name="theme-prefix" />

<xsl:output
	method="html"
	doctype-public="-//W3C//DTD HTML 4.01 Transitional//EN"
	doctype-system="http://www.w3.org/TR/html4/loose.dtd"
	indent="yes"
/>



<xsl:template match="bebop:page">
<html lang="en">
	<head>
		<title>APLAWS+: Site Map</title>
		<xsl:call-template name="cssStyles"/>
	</head>
	
	<body>
        <xsl:call-template name="bebop:dcpJavascript"/>
		<a class="navHide" href="#startcontent" title="Go directly to main content" accesskey="S">Skip over navigation</a>		
		<span class="hide">|</span>
		<xsl:call-template name="header"/>
		<xsl:call-template name="sitemapBreadcrumb"/>
		<xsl:call-template name="sitemapBody"/>
  </body>
</html>
</xsl:template>




<xsl:template name="sitemapBody">
<table id="mainLayout" width="100%" border="0" cellspacing="0" cellpadding="0" summary="This table is used for a three-column page layout">
<tr>
    <td width="20%" rowspan="2" align="left" valign="top" id="LHS">
		<!--LHS NAVIGATION -->
		<xsl:call-template name="sitemapNav" />
		</td>
    <td colspan="2" align="left" valign="top" id="title">
		<!--MAIN CONTENT -->
		<h1>Site Map</h1>
		</td>
	</tr>
  <tr>
    <td width="60%" align="left" valign="top" id="mainContent">
		<span class="hide">|</span>
		<!--CONTENT -->
		<a id="startcontent" title="Start of content"></a>
		<span class="hide">|</span>
		<a class="intLink" name="top" />
		<xsl:call-template name="sitemapMain" />	
	</td>
	<td width="20%" height="400" align="left" valign="top" id="RHS">
  </td>
</tr>
</table>
<xsl:call-template name="footer"/>
</xsl:template>








<xsl:template name="sitemapBreadcrumb">
<div id="bread">
<p>
<b><a href="{$dispatcher-prefix}/portal/" title="home">home</a></b>
<xsl:text disable-output-escaping="yes">&amp;</xsl:text>gt;
<span class="breadHi">Site Map</span>
</p>
</div>
</xsl:template>




<xsl:template name="sitemapNav">
<div id="nav">
<div class="navUp">
<a href="{$dispatcher-prefix}/portal/">
<xsl:attribute name="title">up to homepage</xsl:attribute>
<xsl:text disable-output-escaping="yes">&amp;</xsl:text>#094;
<xsl:text disable-output-escaping="yes">&amp;</xsl:text>nbsp;home</a>
</div>
<div class="navHere"><p>Sitemap</p></div>
<a href="/sitemaphelp" class="navChild" title="Sitemap explained" >Site Map explained</a><span class="hide">|</span>
</div>
</xsl:template>








<xsl:template name="sitemapMain">
<div id="siteArea">

			<div id="siteInfo">Select an area to view</div>
			
			<div id="topSiteList">
			<xsl:for-each select="nav:categoryHierarchy/nav:category[@depth='0']">
			<a href="#{@title}" title="jump to {@title}"><xsl:value-of select="@title" /></a><span class="hide">|</span>
			</xsl:for-each>
			</div>
</div>

<xsl:call-template name="sitemapFullList" />

</xsl:template>






<xsl:template name="sitemapFullList">
<div id="siteList">
		
		<xsl:for-each select="nav:categoryHierarchy">
		<ul class="L1">
		<xsl:for-each select="./nav:category[@depth='0']">
		<li class="cat1"><a name="{@title}" href="{@url}" class="anchor"><xsl:value-of select="@title" /></a>

			<ul class="L2">
			<xsl:for-each select="./nav:category[@depth='1']">
			<li class="cat2"><a href="{@url}"><xsl:value-of select="@title" /></a>
				
				<ul class="L3">
				<xsl:for-each select="./nav:category[@depth='2']">
				<li class="cat3"><a href="{@url}" class="item"><xsl:value-of select="@title" /></a>
					
					<xsl:if test="./nav:category[@depth='3']">
					<ul class="L4">
						<xsl:for-each select="./nav:category[@depth='3']">
						<li class="cat4"><a href="{@url}" class="item"><xsl:value-of select="@title" /></a></li>
						</xsl:for-each>
					</ul>
					</xsl:if>

				</li>
				</xsl:for-each>
				</ul>

			</li>
			</xsl:for-each>
			</ul>
		</li>
		
		<li class="toTop"><a href="#top">top</a></li>
		</xsl:for-each>
		</ul>
		</xsl:for-each>

		
			

			
		</div>
</xsl:template>




</xsl:stylesheet>
