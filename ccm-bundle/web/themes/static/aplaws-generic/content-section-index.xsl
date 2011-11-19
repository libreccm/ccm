<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet 
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:bebop="http://www.arsdigita.com/bebop/1.0"
  xmlns:aplaws="http://www.arsdigita.com/aplaws/1.0"
  xmlns:ui="http://www.arsdigita.com/ui/1.0"
  xmlns:cms="http://www.arsdigita.com/cms/1.0"
  xmlns:nav="http://ccm.redhat.com/navigation"
  exclude-result-prefixes="xsl bebop aplaws ui cms nav"
  version="1.0">

<xsl:import href="../../../../ROOT/__ccm__/apps/content-section/xsl/index.xsl"/>
<xsl:import href="lib/header.xsl"/>
<xsl:import href="lib/lib.xsl"/>
<xsl:import href="lib/leftNav.xsl"/>
<xsl:import href="types/ContentTypes.xsl"/>
<xsl:import href="../../../../ROOT/packages/bebop/xsl/dcp.xsl"/>

<xsl:param name="context-prefix"/>
<xsl:param name="dispatcher-prefix"/>

<xsl:output 
	method="html"
	doctype-public="-//W3C//DTD HTML 4.01 Transitional//EN"
	doctype-system="http://www.w3.org/TR/html4/loose.dtd"
	indent="yes" 
/>

 


<xsl:template match="bebop:page[@class='simplePage']">
<html lang="en">
	<head>
		<xsl:call-template name="metaData"/>
		<title>APLAWS+: <xsl:call-template name="Title"/></title>
		<xsl:call-template name="cssStyles"/>
	</head>
	
	<body>
        <xsl:call-template name="bebop:dcpJavascript"/>
		<a class="navHide" href="#startcontent" title="Go directly to main content" accesskey="S">Skip over navigation</a>		
		<span class="hide">|</span>
		<xsl:call-template name="header"/>
		<xsl:call-template name="breadcrumb"/>
		<xsl:call-template name="mainContent"/>
	</body>
</html>
</xsl:template>



<xsl:template name="mainContent">
    
<!--MAIN LAYOUT -->

<table id="mainLayout" width="100%" border="0" cellspacing="0" cellpadding="0" summary="This table is used for a three-column page layout">
<tr>
    <td width="20%" rowspan="2" align="left" valign="top" id="LHS">
<!--LHS NAVIGATION -->
<xsl:call-template name="leftNav" />
    </td>
    <td colspan="2" align="left" valign="top" id="title">
<!--MAIN CONTENT -->
		<h1><xsl:call-template name="Title" /></h1>
	</td>
  </tr>
  <tr>
    <td width="60%" align="left" valign="top" id="mainContent">
		<span class="hide">|</span>
<!--CONTENT -->
		<a id="startcontent" title="Start of content"></a>
		<span class="hide">|</span>
		<xsl:call-template name="pageContent" />	
	</td>
	<td width="20%" height="400" align="left" valign="top" id="RHS">
		<div id="related">
		<xsl:call-template name="relatedItems" />
		</div>
    </td>
  </tr>
</table>




<xsl:call-template name="footer"/>


</xsl:template>

</xsl:stylesheet>

