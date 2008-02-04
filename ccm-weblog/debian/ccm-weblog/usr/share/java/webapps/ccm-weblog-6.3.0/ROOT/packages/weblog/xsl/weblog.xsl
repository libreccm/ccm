<?xml version="1.0"?>

<xsl:stylesheet
    version="1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:bebop="http://www.arsdigita.com/bebop/1.0"
    xmlns:weblog="http://www.undp.org/weblog/1.0"
    xmlns:unport="http://www.undp.org/unport/1.0"
>

<xsl:import href="../../bebop/xsl/bebop.xsl"/>


<!-- IMPORT MAIN UNPORT STYLING TEMPLATE -->
<!--
<xsl:import href="../../unport/xsl/unport.xsl"/>
-->
<!-- IMPORT UNDP WEBLOG APP -->
<!--
<xsl:import href="../../unport/xsl/apps/weblog/weblog.xsl"/>
-->


<xsl:output method="html" indent="yes"/>

<!--<xsl:variable name="css-dir">/packages/portalserver/www/css</xsl:variable>-->










<!--<xsl:template match="bebop:page[@class='weblog']">
  <html>
    <head>
      <title><xsl:value-of select="bebop:title"/></title>
      <link href="{$css-dir}/portalserver.css" rel="stylesheet" type="text/css"/>

      <xsl:for-each select="bebop:stylesheet">
        <link href="{@href}" rel="stylesheet" type="{@type}"/>
      </xsl:for-each>

    </head>
    <body>
      <xsl:apply-templates select="weblog:header"/>

      <xsl:apply-templates select="weblog:body"/>

      <xsl:apply-templates select="weblog:footer"/>

      <xsl:apply-templates select="*[position()>3]"/>
    </body>
  </html>
</xsl:template>-->








<xsl:template match="weblog:header">
  <!--
  <xsl:call-template name="unport:topHeaderApp"/> -->

  <table class="setInside"><tr><td class="setInside">

  <table class="localHeader">
    <tr>
      <td class="localTitle">
        <xsl:value-of select="../bebop:title"/>
      </td>
    </tr>
  </table>

  </td></tr></table>
</xsl:template>










<xsl:template match="weblog:body">
  <table class="setInside"><tr><td class="setInside">
    <xsl:apply-templates/>
  </td></tr></table>
</xsl:template>







<xsl:template match="weblog:footer">
</xsl:template>









</xsl:stylesheet>
