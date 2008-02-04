<!DOCTYPE stylesheet [
<!ENTITY nbsp   "&#160;" ><!-- no-break space = non-breaking space, U+00A0 ISOnum -->
]>

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
                xmlns:cms="http://www.arsdigita.com/cms/1.0"
                version="1.0">

  <xsl:template match="cms:item[objectType='com.arsdigita.cms.contenttypes.InlineSite']" mode="cms:CT_graphics"
    name="cms:CT_graphics_com_arsdigita_cms_contenttypes_InlineSite">
    <iframe src="{./url}" width="100%" height="500" border="0" name="inlinesite" id="inlinesite">
      <table width="435" border="0" cellspacing="0" cellpadding="0">
        <tr>
          <td class="contentTitle" align="left" valign="top">
            <a href="{./url}"><xsl:value-of select="./title"/></a>
          </td>
        </tr>            
      </table>
    </iframe>
  </xsl:template>

  <xsl:template match="cms:item[objectType='com.arsdigita.cms.contenttypes.InlineSite']" mode="cms:CT_text"
    name="cms:CT_text_com_arsdigita_cms_contenttypes_InlineSite">
    <iframe src="{./url}" width="100%" height="500" border="0" name="inlinesite">
      <h1 class="mainTitle"><a href="{./url}"><xsl:value-of select="./title"/></a></h1>
    </iframe>
  </xsl:template>

</xsl:stylesheet>

