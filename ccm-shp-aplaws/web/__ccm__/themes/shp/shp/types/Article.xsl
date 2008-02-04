<!DOCTYPE stylesheet [<!ENTITY nbsp   "&#160;" ><!-- no-break space = non-breaking space, U+00A0 ISOnum -->]>

<!-- Die ist eine auf Box-Layout angepaßte Version des Aplaws+ Styles für Article                                 -->
<!-- Der Titel des Artikels wird hier nicht mehr verarbeitet, da es eine spezielle Routine in /lib/pageLayout.xsl -->
<!-- dafür gibt. Auf diese Weise wird sichergestellt, daß ContentTypes, die als Index-Seite verwendet werden, den -->
<!-- Titel des Menüeintrages übernehemn. -->

<!-- Autor: Sören Bernstein -->

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
                xmlns:bebop="http://www.arsdigita.com/bebop/1.0"
                xmlns:cms="http://www.arsdigita.com/cms/1.0"
                xmlns:shp="http://www.shp.de"
                exclude-result-prefixes="xsl bebop cms"
                version="1.0">

  <xsl:template name="CT_Article_graphics">
    <div id="greeting">
<!--
      <xsl:for-each select="./imageAttachments">
        <img id="image" align="top">
      </xsl:for-each>
-->
      <xsl:if test="./lead">
        <div id="lead">
          <xsl:value-of disable-output-escaping="yes" select="./lead"/>
        </div>
      </xsl:if>
    </div>
    <xsl:call-template name="shp:imageAttachments">
      <xsl:with-param name="showCaption" select="'true'" />
    </xsl:call-template>

    <div id="mainBody">
      <xsl:value-of disable-output-escaping="yes" select="./textAsset/content"/>
    </div>
  </xsl:template>
</xsl:stylesheet>

