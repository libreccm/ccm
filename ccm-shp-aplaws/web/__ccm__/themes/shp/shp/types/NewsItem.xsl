<!DOCTYPE stylesheet [
<!ENTITY nbsp   "&#160;" ><!-- no-break space = non-breaking space, U+00A0 ISOnum -->
]>

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
		xmlns:bebop="http://www.arsdigita.com/bebop/1.0"
		xmlns:cms="http://www.arsdigita.com/cms/1.0"
		xmlns:shp="http://www.shp.de"
		exclude-result-prefixes="xsl bebop cms"
		version="1.0">



  <xsl:template name="CT_NewsItem_graphics">
    <div id="greeting">
      <div id="newsDate">
        <xsl:value-of disable-output-escaping="yes" select="./newsDate"/>
      </div>
      <xsl:call-template name="shp:imageAttachments">
        <xsl:with-param name="showCaption" select="'true'" />
      </xsl:call-template>
      <xsl:if test="./lead">
        <div id="lead">
          <xsl:value-of disable-output-escaping="yes" select="./lead"/>
        </div>
      </xsl:if>
    </div>
    <div id="mainBody">
      <xsl:value-of disable-output-escaping="yes" select="./textAsset/content"/>
    </div>
  </xsl:template>
</xsl:stylesheet>