<!DOCTYPE stylesheet [
<!ENTITY nbsp   "&#160;" ><!-- no-break space = non-breaking space, U+00A0 ISOnum -->
]>

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
                xmlns:cms="http://www.arsdigita.com/cms/1.0"
                  version="1.0">

  <xsl:template match="cms:contentLists">
    <xsl:apply-templates />
  </xsl:template>

  <xsl:template match="cms:contentList">

    <p>
      ContentList for <xsl:value-of select="@@type" /> (default template).
    </p>

    <!-- FIXME: hardcode this for categories -->
    <xsl:variable name="dispatcher-prefix">/ccm</xsl:variable>

    <table border="1">

      <!-- display headers -->
      <xsl:for-each select="cms:item[position()=1]">
        <tr>
          <xsl:for-each select="*">
            <xsl:choose>
              <xsl:when test="name() = 'creationDate'">
              </xsl:when>
              <xsl:when test="name() = 'type'">
              </xsl:when>
              <xsl:when test="name() = 'name'">
              </xsl:when>
              <xsl:when test="name() = 'language'">
              </xsl:when>
              <xsl:when test="name() = 'objectType'">
              </xsl:when>
              <xsl:when test="name() = 'fileAttachments'">
              </xsl:when>
              <xsl:when test="name() = 'images'">
              </xsl:when>
              <xsl:when test="name() = 'imageCaptions'">
              </xsl:when>
              <xsl:when test="name() = 'textAsset'">
              </xsl:when>
              <xsl:when test="name() = 'links'">
              </xsl:when>
              <xsl:otherwise>
                <th><xsl:value-of select="name()" /></th>
              </xsl:otherwise>
            </xsl:choose>
          </xsl:for-each>
          <th>Text Asset</th>
          <th>File attachment(s)</th>
          <th>Image(s)</th>
          <th>Link(s)</th>
        </tr>
      </xsl:for-each>

      <!-- display rows -->
      <xsl:for-each select="cms:item">

        <tr>
          <xsl:for-each select="*">
            <xsl:choose>
              <xsl:when test="name() = 'creationDate'">
              </xsl:when>
              <xsl:when test="name() = 'type'">
              </xsl:when>
              <xsl:when test="name() = 'name'">
              </xsl:when>
              <xsl:when test="name() = 'language'">
              </xsl:when>
              <xsl:when test="name() = 'objectType'">
              </xsl:when>
              <xsl:when test="name() = 'fileAttachments'">
              </xsl:when>
              <xsl:when test="name() = 'images'">
              </xsl:when>
              <xsl:when test="name() = 'imageCaptions'">
              </xsl:when>
              <xsl:when test="name() = 'textAsset'">
              </xsl:when>
              <xsl:when test="name() = 'links'">
              </xsl:when>
              <xsl:otherwise>
                <td><xsl:apply-templates /></td>
              </xsl:otherwise>
            </xsl:choose>
          </xsl:for-each>
          <td>
            <xsl:for-each select="textAsset">
              <xsl:value-of select="content" disable-output-escaping="yes" />
            </xsl:for-each>
          </td>
          <td>
            <xsl:for-each select="fileAttachments">
              <a>
                <xsl:attribute name="href"><xsl:value-of select="$dispatcher-prefix"/>/cms-service/stream/asset/?asset_id=<xsl:value-of select="id" /></xsl:attribute>
                <xsl:value-of select="description" />
              </a><br />
            </xsl:for-each>
          </td>
          <td>
            <xsl:for-each select="imageCaptions">
              <a>
               <xsl:attribute name="href"><xsl:value-of select="$dispatcher-prefix"/>/cms-service/stream/image/?image_id=<xsl:value-of select="imageAsset/id" /></xsl:attribute>
                <xsl:value-of select="caption" />
              </a><br />
            </xsl:for-each>
            <xsl:for-each select="images">
              <a>
               <xsl:attribute name="href"><xsl:value-of select="$dispatcher-prefix"/>/cms-service/stream/image/?image_id=<xsl:value-of select="id" /></xsl:attribute>
                <xsl:value-of select="description" />
              </a><br />
            </xsl:for-each>
          </td>
          <td>
            <xsl:for-each select="links[targetType='externalLink']">
              <a>
                <xsl:attribute name="href"><xsl:value-of select="targetURI" /></xsl:attribute>
                <xsl:value-of select="linkTitle" /> : <xsl:value-of select="linkDescription" />
              </a><br />
            </xsl:for-each>
            <xsl:for-each select="links[targetType='internalLink']">
              <a>
                <xsl:attribute name="href">/redirect/?oid=<xsl:value-of select="targetItem/@@oid" /></xsl:attribute>
                <xsl:value-of select="linkTitle" /> : <xsl:value-of select="linkDescription" />
              </a><br />
            </xsl:for-each>
          </td>
        </tr>

      </xsl:for-each>

    </table>

  </xsl:template>
  
</xsl:stylesheet>
