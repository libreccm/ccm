<!DOCTYPE stylesheet [
<!ENTITY nbsp   "&#160;" ><!-- no-break space = non-breaking space, U+00A0 ISOnum -->
]>

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
                xmlns:cms="http://www.arsdigita.com/cms/1.0"
                exclude-result-prefixes="cms"
                version="1.0">

  <xsl:template match="cms:item[objectType='com.arsdigita.coventry.cms.contenttypes.Person']"
    name="cms:CT_graphics_com_arsdigita_coventry_cms_contenttypes_Person">
    <h1><xsl:value-of select="title"/></h1>

        <table border="0" cellpadding="0" cellspacing="10" class=
        "councillorList" summary=
        "List of the councillor and summary details">
          <tr>
            <xsl:if test="imageAttachments">
            <td class="councillorIMG">
              <xsl:for-each select="imageAttachments">
            <img border="1">
               <xsl:attribute name="src"><xsl:value-of select="$dispatcher-prefix"/>/cms-service/stream/image/?image_id=<xsl:value-of select="image/id" /></xsl:attribute>
               <xsl:if test="image/width">
                 <xsl:attribute name="width"><xsl:value-of select="image/width" /></xsl:attribute>
               </xsl:if>
               <xsl:if test="image/height">
                 <xsl:attribute name="height"><xsl:value-of select="image/height" /></xsl:attribute>
               </xsl:if>
               <xsl:attribute name="alt"><xsl:value-of select="caption" /></xsl:attribute>
            </img>
              </xsl:for-each>
            </td>
            </xsl:if>

            <xsl:if test="textAsset/content">
            <td align="left" valign="top"><p><xsl:value-of select="textAsset/content" disable-output-escaping="yes"/></p></td>
            </xsl:if>

          </tr>
        </table>
        <p/>

        <xsl:if test="contactDetails">
        <h2>Contact details</h2>
          <p><xsl:value-of select="contactDetails" disable-output-escaping="yes"/></p>
        </xsl:if>

  </xsl:template>
</xsl:stylesheet>
