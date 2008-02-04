<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:cms="http://www.arsdigita.com/cms/1.0"
                version="1.0">

  <xsl:output method="html"/>

  <xsl:param name="dispatcher-prefix"/>

  <xsl:template name="cms:fileAttachments">
    <table border="0">
      <xsl:for-each select="fileAttachments">
        <xsl:sort select="fileOrder" data-type="number" />
        <tr>
          <td class="contentText">
            <xsl:value-of select="name"/>
          </td>
          <td class="contentText" align="left" valign="top"><a  href="{$dispatcher-prefix}/cms-service/stream/asset/?asset_id={./id}">[View]</a></td>
          <td class="contentText" align="left" valign="top"><a  href="{$dispatcher-prefix}/cms-service/download/asset/?asset_id={./id}">[Save]</a></td>
        </tr>
        <tr>
          <td colspan="3" class="contentSynopsis"><xsl:value-of select="description"/></td>
        </tr>
      </xsl:for-each>
    </table>
  </xsl:template>

  <xsl:template name="cms:fileAttachments_text" >
    <xsl:for-each select="fileAttachments">
      <xsl:sort select="fileOrder" data-type="number" />
      <span class="contentText">
        <xsl:value-of select="name"/>
      </span>
      <span class="contentSynopsis"><xsl:value-of select="description"/></span>
      <span class="contentText" align="left" valign="top"><a  href="{$dispatcher-prefix}/cms-service/stream/asset/?asset_id={./id}">[View]</a></span>
      <span class="contentText" align="left" valign="top"><a  href="{$dispatcher-prefix}/cms-service/download/asset/?asset_id={./id}">[Save]</a></span>
      <br/>
    </xsl:for-each>
  </xsl:template>

</xsl:stylesheet>
