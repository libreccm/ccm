<?xml version="1.0"?>
<xsl:stylesheet  xmlns:bebop="http://www.arsdigita.com/bebop/1.0"
                   xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
               xmlns:deditor="http://www.arsdigita.com/deditor/1.0"
     exclude-result-prefixes="bebop"
                     version="1.0">
  
  <xsl:output method="html" indent="yes"
    doctype-public="-//W3C//DTD HTML 4.01 Transitional//EN"/>

  <xsl:param name="internal-theme"/>
  
  <!-- The contract for this template is that
       it can be called from any place in the DOM -->
  <xsl:template name="bebop:pageTitle">
    <xsl:value-of select="/bebop:page/bebop:title"/>
  </xsl:template>
  
  <!-- The contract for this template is that
       it must be called in context of bebop:page 
       element -->
  <xsl:template name="bebop:pageCSS">
    <xsl:call-template name="bebop:pageCSSMain"/>
  </xsl:template>
  
  <xsl:template name="bebop:pageCSSMain">
    <link href="{$internal-theme}/css/acs-master.css" rel="stylesheet" type="text/css"/>
    <xsl:for-each select="bebop:stylesheet">
      <link href="{@href}" rel="stylesheet" type="{@type}"/>
    </xsl:for-each>
  </xsl:template>

  <xsl:template match="bebop:title">
    <!-- Nada -->
  </xsl:template>
  
  <xsl:template match="bebop:page" 
    xmlns:bebop="http://www.arsdigita.com/bebop/1.0">
    <html>
      <head>
        <title><xsl:call-template name="bebop:pageTitle"/></title>       
        <xsl:call-template name="bebop:pageCSS"/>
      </head>
      <body>
        <xsl:call-template name="bebop:dcpJavascript"/>
        <h2><xsl:call-template name="bebop:pageTitle"/></h2>
        <xsl:apply-templates select="*[position()>1]"/>
      </body>
    </html>
  </xsl:template>
  
  <!-- Display the page structure; used by debugging -->
  <xsl:template match="bebop:structure">
    <h1>Bebop Page Structure</h1>
    <xsl:value-of disable-output-escaping="yes" select="text()"/>
  </xsl:template>

</xsl:stylesheet>
