<!DOCTYPE stylesheet [
<!ENTITY nbsp   "&#160;" ><!-- no-break space = non-breaking space, U+00A0 ISOnum -->
]>

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:cms="http://www.arsdigita.com/cms/1.0"
                exclude-result-prefixes="cms"
                version="1.0">

  <xsl:template match="cms:item[objectType='com.arsdigita.coventry.cms.contenttypes.Councillor']"
    name="cms:CT_graphics_com_arsdigita_coventry_cms_contenttypes_Councillor">
    <h1><xsl:value-of select="title"/></h1>

    <table border="0" cellpadding="0" cellspacing="0" class="councillorList"
        summary="Details for councillor {title}">
      <tr>
        <xsl:if test="imageAttachments">
          <td rowspan="3" class="councillorIMG">
            <xsl:for-each select="imageAttachments">
              <img border="1" width="75" height="115">
               <xsl:attribute name="src">
                 <xsl:value-of select="$dispatcher-prefix"/>/cms-service/stream/image/?image_id=<xsl:value-of select="image/id" />
               </xsl:attribute>
               <!--
               <xsl:if test="image/width">
                 <xsl:attribute name="width"><xsl:value-of select="image/width" /></xsl:attribute>
               </xsl:if>
               <xsl:if test="image/height">
                 <xsl:attribute name="height"><xsl:value-of select="image/height" /></xsl:attribute>
               </xsl:if>
               -->
               <xsl:attribute name="alt"><xsl:value-of select="caption" /></xsl:attribute>
              </img>
            </xsl:for-each>
          </td>
        </xsl:if>

        <th>Position</th>
        <td>
          <xsl:choose>
            <xsl:when test="position">
              <xsl:value-of select="position"/>
            </xsl:when>
            <xsl:otherwise>
              &nbsp;
            </xsl:otherwise>
          </xsl:choose>
        </td>
      </tr>

      <tr>
        <th>Political party</th>
        <td>
          <xsl:choose>
            <xsl:when test="politicalParty">
              <xsl:value-of select="politicalParty"/>
            </xsl:when>
            <xsl:otherwise>
              &nbsp;
            </xsl:otherwise>
          </xsl:choose>
        </td>
      </tr>

      <tr>
        <th>Term of office</th>
        <td>
          <xsl:choose>
            <xsl:when test="termOfOffice">
              <xsl:value-of select="termOfOffice"/>
            </xsl:when>
            <xsl:otherwise>
              &nbsp;
            </xsl:otherwise>
          </xsl:choose>
        </td>
      </tr>
    </table>
    <xsl:if test="textAsset/content">
      <p><xsl:value-of select="textAsset/content" disable-output-escaping="yes"/></p>
    </xsl:if>

    <xsl:if test="contactDetails">
      <h2>Contact details</h2>
      <p><xsl:value-of select="contactDetails" disable-output-escaping="yes"/></p>
    </xsl:if>

    <xsl:if test="surgeryDetails">
      <h2>Surgery details</h2>
      <p><xsl:value-of select="surgeryDetails" disable-output-escaping="yes"/></p>
    </xsl:if>

  </xsl:template>
</xsl:stylesheet>
