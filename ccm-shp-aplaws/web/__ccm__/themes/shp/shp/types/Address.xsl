<?xml version="1.0" encoding="utf-8"?>

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
                xmlns:bebop="http://www.arsdigita.com/bebop/1.0"
                xmlns:cms="http://www.arsdigita.com/cms/1.0"
                xmlns:shp="http://www.shp.de"
                exclude-result-prefixes="xsl bebop cms"
version="1.0">




  <xsl:template name="CT_Address_graphics">
  
    <div id="details">
      <xsl:call-template name="shp:imageAttachments">
        <xsl:with-param name="showCaption" select="'false'" />
      </xsl:call-template>
    
      <xsl:if test="./address">
        <p>
          <b>Adresse:</b><br />
          <xsl:value-of disable-output-escaping="yes" select="./address"/>
        </p>
      </xsl:if>
      <xsl:if test="./postalCode">
        <p>
          <b>Postleitzahl:</b><br />
          <xsl:value-of disable-output-escaping="yes" select="./postalCode"/>
        </p>
      </xsl:if>
      <xsl:if test="./isoCountryCode/countryName">
        <p>
          <b>Land:</b><br />
          <xsl:value-of disable-output-escaping="yes" select="./isoCountryCode/countryName"/>
        </p>
      </xsl:if>
      <xsl:if test="./phone">
        <p>
          <b>Telefon:</b><br />
          <xsl:value-of disable-output-escaping="yes" select="./phone"/>
        </p>
      </xsl:if>
      <xsl:if test="./mobile">
        <p>
          <b>Handy:</b><br />
          <xsl:value-of disable-output-escaping="yes" select="./mobile"/>
        </p>
      </xsl:if>
      <xsl:if test="./fax">
        <p>
          <b>Fax:</b><br />
          <xsl:value-of disable-output-escaping="yes" select="./fax"/>
        </p>
      </xsl:if>
      <xsl:if test="./email">
        <p>
          <b>Email:</b><br />
          <a>
          <xsl:attribute name="href">mailto:<xsl:value-of disable-output-escaping="yes" select="./email"/></xsl:attribute>
          <xsl:value-of disable-output-escaping="yes" select="./email"/>
          </a>
        </p>
      </xsl:if>
      <xsl:if test="./notes">
        <p>
          <b>Bemerkungen:</b><br />
          <xsl:value-of disable-output-escaping="yes" select="./notes"/>
        </p>
      </xsl:if>
    </div>

    <div id="mainBody">
      <xsl:if test="./lead">
        <div id="lead">
          <xsl:value-of disable-output-escaping="yes" select="./lead"/>
        </div>
      </xsl:if>
      <xsl:value-of disable-output-escaping="yes" select="./textAsset/content"/>
    </div>
  
  </xsl:template>
</xsl:stylesheet>
