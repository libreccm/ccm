<!DOCTYPE stylesheet [
<!ENTITY nbsp   "&#160;" ><!-- no-break space = non-breaking space, U+00A0 ISOnum -->
]>

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
                xmlns:bebop="http://www.arsdigita.com/bebop/1.0"
                xmlns:cms="http://www.arsdigita.com/cms/1.0"
                version="1.0">

  <xsl:template match="cms:item[objectType='com.arsdigita.cms.contenttypes.NewsItem']" mode="cms:CT_graphics"
    name="cms:CT_graphics_com_arsdigita_cms_contenttypes_NewsItem">
    <table width="435" border="0" cellspacing="0" cellpadding="0">
      <tr>
        <td width="285" align="left" valign="top">
          <table width="285" border="0" cellspacing="1" cellpadding="0">
            <tr>
              <td class="contentTitle" align="left" valign="top">
                <xsl:value-of select="./title"/>
              </td>
            </tr>
            
            <tr>
              <td align="left" valign="top">
                <table width="100%" border="0" cellspacing="1" cellpadding="0">
                  <tr>
                    <td width="40%" class="contentSynopsis" align="left" valign="top"><xsl:value-of select="./lead"/>&nbsp;<br/><br/></td>
                  </tr>
                </table>
              </td>
            </tr>
            <tr>
              <td class="contentText" align="left" valign="top"><xsl:value-of disable-output-escaping="yes" select="./textAsset/content"/></td>
            </tr>
          </table>
        </td>
        <td width="150" height="100" align="right" valign="top">
          <table width="100%" border="0" cellspacing="1" cellpadding="0">
            <xsl:choose>
              <xsl:when test="./imageCaptions">
                <tr>
                  <td align="right" valign="top">
                    <img>
                      <xsl:attribute name="src"><xsl:value-of select="$dispatcher-prefix"/>/cms-service/stream/image/?image_id=<xsl:value-of select="imageCaptions/imageAsset/id" /></xsl:attribute>
                      <xsl:if test="imageCaptions/imageAssets/width">
                        <xsl:attribute name="width"><xsl:value-of select="imageCaptions/imageAssets/width" /></xsl:attribute>
                      </xsl:if>
                      <xsl:if test="imageCaptions/imageAssets/height">
                        <xsl:attribute name="height"><xsl:value-of select="imageCaptions/imageAssets/height" /></xsl:attribute>
                      </xsl:if>
                      <xsl:attribute name="alt"><xsl:value-of select="imageCaptions/caption" /></xsl:attribute>
                      <xsl:attribute name="oid"><xsl:value-of select="imageCaptions/imageAsset/id" /></xsl:attribute>
                    </img>
                  </td>
                </tr>
              </xsl:when>
              <xsl:otherwise>
              </xsl:otherwise>
            </xsl:choose>
            <tr>
              <td bgcolor="#eeeeee">
                <table width="100%" border="0" cellspacing="0" cellpadding="0">
                  <tr>
                    <td width="40%" class="contentSynopsis" align="left" valign="top">&nbsp;Date:</td>
                    <td width="60%" class="contentText" align="right" valign="top">
                      <xsl:value-of select="./newsDate"/>
                    </td>
                  </tr>
                </table>
              </td>
            </tr>
          </table>
        </td>
      </tr>
    </table>
  </xsl:template>

  <xsl:template match="cms:item[objectType='com.arsdigita.cms.contenttypes.NewsItem']" mode="cms:CT_text"
    name="cms:CT_text_com_arsdigita_cms_contenttypes_NewsItem">
    <h1 class="mainTitle">NEWS <xsl:text disable-output-escaping="yes">&amp;</xsl:text>gt; <xsl:value-of select="./title"/></h1>
    <xsl:choose>
    <xsl:when test="./imageCaptions">
    <span class="text">
      <img>
        <xsl:attribute name="src"><xsl:value-of select="$dispatcher-prefix"/>/cms-service/stream/image/?image_id=<xsl:value-of select="imageCaptions/imageAsset/id" /></xsl:attribute>
        <xsl:attribute name="width"><xsl:value-of select="imageCaptions/imageAsset/width" /></xsl:attribute>
        <xsl:attribute name="height"><xsl:value-of select="imageCaptions/imageAsset/height" /></xsl:attribute>
        <xsl:attribute name="alt"><xsl:value-of select="imageCaptions/caption" /></xsl:attribute>
	<xsl:attribute name="oid"><xsl:value-of select="imageCaptions/imageAsset/id" /></xsl:attribute>
      </img>
      <br/>
      <br/>
    </span>
    </xsl:when>
    <xsl:otherwise>
    </xsl:otherwise>
    </xsl:choose>
    <span class="synopsis"><xsl:value-of select="./lead" /></span><br />
    <br/>
    <span class="text"><xsl:value-of disable-output-escaping="yes" select="./textAsset/content"/></span>
  </xsl:template>
</xsl:stylesheet>