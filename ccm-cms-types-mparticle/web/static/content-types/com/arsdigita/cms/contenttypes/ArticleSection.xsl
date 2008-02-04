<!DOCTYPE stylesheet [
<!ENTITY nbsp   "&#160;" ><!-- no-break space = non-breaking space, U+00A0 ISOnum -->
]>

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
                xmlns:bebop="http://www.arsdigita.com/bebop/1.0"
                xmlns:cms="http://www.arsdigita.com/cms/1.0"
                version="1.0">

  <xsl:param name="dispatcher-prefix"/>

  <xsl:template match="cms:articleSectionPanel">
    <xsl:apply-templates/>
  </xsl:template>

  <xsl:template match="cms:item[objectType='com.arsdigita.cms.contenttypes.ArticleSection']" mode="cms:CT_graphics"
    name="cms:CT_graphics_com_arsdigita_cms_contenttypes_ArticleSection">
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
              <td class="contentText" align="left" valign="top">
                <br/><xsl:value-of disable-output-escaping="yes" select="./text/content"/>
              </td>
            </tr>
          </table>
        </td>
        <td width="150" height="100" align="right" valign="top">
          <xsl:choose>
            <xsl:when test="./image">
              <img>
                <xsl:attribute name="src"><xsl:value-of select="$dispatcher-prefix"/>/cms-service/stream/image/?image_id=<xsl:value-of select="image/id" /></xsl:attribute>
                <xsl:if test="image/width">
                  <xsl:attribute name="width"><xsl:value-of select="image/width" /></xsl:attribute>
                </xsl:if>
                <xsl:if test="image/height">
                  <xsl:attribute name="height"><xsl:value-of select="image/height" /></xsl:attribute>
                </xsl:if>
                <xsl:attribute name="alt"><xsl:value-of select="image/description" /></xsl:attribute>
              </img>
              <br/>
              <xsl:value-of select="image/description"/>
              <hr/>
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
  
  <xsl:template match="cms:item[objectType='com.arsdigita.cms.contenttypes.ArticleSection']" mode="cms:CT_text"
    name="cms:CT_text_com_arsdigita_cms_contenttypes_ArticleSection">
    <h1 class="mainTitle">ARTICLE <xsl:text disable-output-escaping="yes">&amp;</xsl:text>gt; <xsl:value-of select="./title"/></h1>
    <span class="text">
      <xsl:if test="./image">
        <img width="185">
          <xsl:attribute name="src"><xsl:value-of select="$dispatcher-prefix"/>/cms-service/stream/image/?image_id=<xsl:value-of select="image/id" /></xsl:attribute>
          <xsl:attribute name="alt"><xsl:value-of select="caption" /></xsl:attribute>
        </img>
      </xsl:if>
      <br/>
      <br/>
    </span>
    <br/>
    <span class="text"><xsl:value-of disable-output-escaping="yes" select="./text/content"/></span>
  </xsl:template>
    
</xsl:stylesheet>
