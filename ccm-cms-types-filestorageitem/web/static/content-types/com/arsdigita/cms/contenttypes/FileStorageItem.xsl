<!DOCTYPE stylesheet [
<!ENTITY nbsp   "&#160;" ><!-- no-break space = non-breaking space, U+00A0 ISOnum -->
]>

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
                xmlns:cms="http://www.arsdigita.com/cms/1.0"
                version="1.0">

  <xsl:template match="cms:item[objectType='com.arsdigita.cms.contenttypes.FileStorageItem']" mode="cms:CT_graphics"
    name="cms:CT_graphics_com_arsdigita_cms_contenttypes_FileStorageItem">
    <table width="435" border="0" cellspacing="0" cellpadding="0">
      <tr>
        <td class="contentTitle" align="left" valign="top">
          <xsl:value-of select="./title"/>
        </td>
      </tr>            
      <tr>
        <td class="contentText" align="left" valign="top"><xsl:value-of disable-output-escaping="yes" select="./description"/></td>
      </tr>
      <tr>
        <td class="contentText" align="left" valign="top"><a href="{$dispatcher-prefix}/cms-service/stream/asset/?asset_id={./file/id}">View in browser</a></td>
      </tr>
      <tr>
        <td class="contentText" align="left" valign="top"><a href="{$dispatcher-prefix}/cms-service/download/asset/?asset_id={./file/id}">Save to disk</a></td>
      </tr>
    </table>
  </xsl:template>

  <xsl:template match="cms:item[objectType='com.arsdigita.cms.contenttypes.FileStorageItem']" mode="cms:CT_text"
    name="cms:CT_text_com_arsdigita_cms_contenttypes_FileStorageItem">
    <h1 class="mainTitle"><xsl:value-of select="./title"/></h1>
    <span class="synopsis"><xsl:value-of disable-output-escaping="yes" select="./description"/></span><br/>
    <span class="text"><a href="{$dispatcher-prefix}/cms-service/stream/asset/?asset_id={./file/id}">View in browser</a></span><br/>
    <span class="text"><a href="{$dispatcher-prefix}/cms-service/download/asset/?asset_id={./file/id}">Save to disk</a></span><br/>
  </xsl:template>

</xsl:stylesheet>
