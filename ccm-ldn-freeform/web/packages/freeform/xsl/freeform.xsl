<!DOCTYPE stylesheet [
<!ENTITY nbsp   "&#160;" ><!-- no-break space = non-breaking space, U+00A0 ISOnum -->
]>

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
                xmlns:bebop="http://www.arsdigita.com/bebop/1.0"
                xmlns:cms="http://www.arsdigita.com/cms/1.0"
                version="1.0">

<xsl:template name="CT_FreeForm">
  <table width="435" border="0" cellspacing="0" cellpadding="0">
    <tr>
      <td width="435" align="left" valign="top">
        <table width="435" border="0" cellspacing="1" cellpadding="0">
          <tr>
            <td class="contentTitle" align="left" valign="top">
              <xsl:value-of select="@title"/>
            </td>
          </tr>
        </table>
      </td>
    </tr>
    <xsl:for-each select="cms:assets/*">
      <tr>
        <td width="435" align="left" valign="top">
          <xsl:choose>
            <xsl:when test="name() = 'cms:textAsset'">
              <table width="435" border="0" cellspacing="1" cellpadding="0">
                <tr>
                  <td class="contentSynopsis" align="left" valign="top">
                    <xsl:value-of select="@description"/>
                  </td>
                </tr>
                <tr>
                  <td class="contentText" align="left" valign="top">
                    <xsl:value-of disable-output-escaping="yes" select="."/>
                  </td>
                </tr>
              </table>
            </xsl:when>
            <xsl:when test="name() = 'cms:binaryAsset' and substring(@mimeType, 0, 7) = 'image/'">
              <table width="435" border="0" cellspacing="1" cellpadding="0">
                <tr>
                  <td class="contentSynopsis" align="left" valign="top">
                    <xsl:value-of select="@description"/>
                  </td>
                </tr>
                <tr>
                  <td class="contentText" align="left" valign="top">
                    <img alt="{@name}">
                      <xsl:attribute name="src">/cms-service/stream/asset/?asset_id=<xsl:value-of select="@id"/></xsl:attribute>
                    </img>
                  </td>
                </tr>
              </table>
            </xsl:when>
            <xsl:when test="name() = 'cms:binaryAsset' and substring(@mimeType, 0, 7) != 'image/'">
              <table width="435" border="0" cellspacing="1" cellpadding="0">
                <tr>
                  <td class="contentSynopsis" align="left" valign="top">
                    <xsl:value-of select="@description"/>
                  </td>
                </tr>
                <tr>
                  <td class="contentText" align="left" valign="top">
                    <a class="contentText">
                      <xsl:attribute name="href">/cms-service/download/asset/?asset_id=<xsl:value-of select="@id"/></xsl:attribute>
                      Click here to view the file (<xsl:value-of select="@name"/>)
                    </a>
                  </td>
                </tr>
              </table>
            </xsl:when>
          </xsl:choose>
          <br/>
        </td>
      </tr>
    </xsl:for-each>
  </table>
</xsl:template>

<xsl:template name="CT_FreeForm_text">
  <h1 class="mainTitle">Article <xsl:text disable-output-escaping="yes">&amp;</xsl:text>gt; <xsl:value-of select="@title"/></h1>
  <xsl:for-each select="cms:assets/*">
    <xsl:choose>
      <xsl:when test="name() = 'cms:textAsset'">
        <span class="synopsis"><xsl:value-of select="@description"/></span>
        <span class="text"><xsl:value-of disable-output-escaping="yes" select="."/></span>
        <br/>
      </xsl:when>
      <xsl:when test="name() = 'cms:binaryAsset'">
        <span class="synopsis"><xsl:value-of select="@description"/></span>
        <span class="text">
          <a class="contentText">
            <xsl:attribute name="href">/cms-service/download/asset/?asset_id=<xsl:value-of select="@id"/></xsl:attribute>
            Click here to view the file (<xsl:value-of select="@name"/>)
          </a>
        </span>
      </xsl:when>
    </xsl:choose>
    <br/>
  </xsl:for-each>
</xsl:template>

<xsl:template match="cms:item[objectType='com.arsdigita.london.cms.freeform.FreeformContentItem' and not(@useContext = 'itemAdminSummary')]">
  <xsl:call-template name="CT_FreeForm"/>
  <xsl:call-template name="links"/>
</xsl:template>

<xsl:template match="cms:item[objectType='com.arsdigita.london.cms.freeform.FreeformContentItem' and @useContext = 'itemAdminSummary']">
  <xsl:call-template name="CT_FreeForm_text"/>
  <xsl:call-template name="links"/>
</xsl:template>



</xsl:stylesheet>
