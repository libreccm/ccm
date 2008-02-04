<?xml version="1.0" encoding="utf-8"?>

<xsl:stylesheet 
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:bebop="http://www.arsdigita.com/bebop/1.0"
  xmlns:aplaws="http://www.arsdigita.com/aplaws/1.0"
  xmlns:ui="http://www.arsdigita.com/ui/1.0"
  xmlns:cms="http://www.arsdigita.com/cms/1.0"
  xmlns:nav="http://ccm.redhat.com/london/navigation"
  xmlns:shp="http://www.shp.de"
  exclude-result-prefixes="xsl bebop aplaws ui cms nav"
  version="1.0">

  <xsl:import href="../../../../ROOT/__ccm__/apps/content-section/xsl/index.xsl"/>
  <xsl:import href="lib/header.xsl"/>
  <xsl:import href="lib/lib.xsl"/>
  <xsl:import href="lib/leftNav.xsl"/>
  <xsl:import href="types/ContentTypes.xsl"/>

  <!-- Eigene XSL-Dateien importieren -->
  <xsl:import href="lib/pageLayout.xsl"/>

  <xsl:param name="context-prefix"/>
  <xsl:param name="dispatcher-prefix"/>

  <xsl:output 
    method="html"
    doctype-public="-//W3C//DTD HTML 4.01 Transitional//EN"
    doctype-system="http://www.w3.org/TR/html4/loose.dtd"
    indent="yes" 
  />

  <xsl:template match="bebop:page[@class='simplePage']">
    <xsl:call-template name="shp:pageLayout"/>
  </xsl:template>

<!--MAIN CONTENT -->
  <xsl:template name="mainContent">
    <h1><xsl:call-template name="Title" /></h1>
    <span class="hide">|</span>
    <!--CONTENT -->
    <a id="startcontent" title="Start of content"></a>
    <span class="hide">|</span>
    <xsl:call-template name="pageContent" />	
    <div id="related">
      <xsl:call-template name="relatedItems" />
    </div>
  </xsl:template>

</xsl:stylesheet>

