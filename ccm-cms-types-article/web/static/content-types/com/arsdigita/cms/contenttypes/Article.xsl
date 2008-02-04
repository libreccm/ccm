<!DOCTYPE stylesheet [
<!ENTITY nbsp   "&#160;" ><!-- no-break space = non-breaking space, U+00A0 ISOnum -->
]>

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
                xmlns:cms="http://www.arsdigita.com/cms/1.0"
                version="1.0">

  <xsl:template match="cms:item[objectType='com.arsdigita.cms.contenttypes.Article']" mode="cms:CT_graphics"
    name="cms:CT_graphics_com_arsdigita_cms_contenttypes_Article">
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
              <td class="contentSynopsis" align="left" valign="top">
                <xsl:value-of select="./lead"/>
              </td>
            </tr>
            
            <tr>
              <td class="contentText" align="left" valign="top">
                <br/><xsl:value-of disable-output-escaping="yes" select="./textAsset/content"/>
              </td>
            </tr>
          </table>
        </td>
        <td width="150" height="100" align="right" valign="top">
          <xsl:choose>
            <xsl:when test="./imageCaptions">
	      <xsl:for-each select="./imageCaptions">
              <img>
                <xsl:attribute name="src"><xsl:value-of select="$dispatcher-prefix"/>/cms-service/stream/image/?image_id=<xsl:value-of select="imageAsset/id" /></xsl:attribute>
                <xsl:if test="imageAsset/width">
                  <xsl:attribute name="width"><xsl:value-of select="imageAsset/width" /></xsl:attribute>
                </xsl:if>
                <xsl:if test="imageAsset/height">
                  <xsl:attribute name="height"><xsl:value-of select="imageAsset/height" /></xsl:attribute>
                </xsl:if>
                <xsl:attribute name="alt"><xsl:value-of select="caption" /></xsl:attribute>
		<xsl:attribute name="oid"><xsl:value-of select="imageAsset/id" /></xsl:attribute>
              </img>
              <br />
              <xsl:value-of select="caption" />
              <hr />
              </xsl:for-each>
            </xsl:when>
            <xsl:otherwise>
              <table width="100%" border="0" cellspacing="0" cellpadding="0">
                <tr>
                  <td class="contentTitle"><xsl:text disable-output-escaping="yes">&amp;</xsl:text>nbsp;</td>
                </tr>
              </table>
            </xsl:otherwise>
          </xsl:choose>
        </td>
      </tr>
    </table>    
  </xsl:template>
  
  <xsl:template match="cms:item[objectType='com.arsdigita.cms.contenttypes.Article']" mode="cms:CT_text"
    name="cms:CT_text_com_arsdigita_cms_contenttypes_Article">
    <h1 class="mainTitle">ARTICLE <xsl:text disable-output-escaping="yes">&amp;</xsl:text>gt; <xsl:value-of select="./title"/></h1>
    <span class="text">
     <xsl:for-each select="imageCaptions">
      <img width="185">
        <xsl:attribute name="src"><xsl:value-of select="$dispatcher-prefix"/>/cms-service/stream/image/?image_id=<xsl:value-of select="imageAsset/id" /></xsl:attribute>
        <xsl:attribute name="alt"><xsl:value-of select="caption" /></xsl:attribute>
	<xsl:attribute name="oid"><xsl:value-of select="imageAsset/id" /></xsl:attribute>
      </img>
     </xsl:for-each>
      <br/>
      <br/>
    </span>
    <span class="synopsis"><xsl:value-of select="./lead" /></span><br />
    <br/>
    <span class="text"><xsl:value-of disable-output-escaping="yes" select="./textAsset/content"/></span>
  </xsl:template>
    
</xsl:stylesheet>
