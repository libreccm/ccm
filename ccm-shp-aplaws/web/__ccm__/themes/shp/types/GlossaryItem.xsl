<!DOCTYPE stylesheet [<!ENTITY nbsp   "&#160;" ><!-- no-break space = non-breaking space, U+00A0 ISOnum -->]>

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
                xmlns:cms="http://www.arsdigita.com/cms/1.0"
                xmlns:shp="http://www.shp.de"
                version="1.0">

  <xsl:template name="CT_GlossaryItem_graphics">
    <div id="greeting">
      <xsl:call-template name="shp:imageAttachments">
        <xsl:with-param name="showCaption" select="'true'" />
      </xsl:call-template>
      <div id="lead">
        <xsl:value-of select="./title"/>
      </div>
      <div id="mainBody">
        <xsl:value-of disable-output-escaping="yes" select="./definition"/>
      </div>
    </div>
  </xsl:template>

</xsl:stylesheet>

