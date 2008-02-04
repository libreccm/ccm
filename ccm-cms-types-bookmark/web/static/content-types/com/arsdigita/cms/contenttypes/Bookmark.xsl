<!DOCTYPE stylesheet [
<!ENTITY nbsp   "&#160;" ><!-- no-break space = non-breaking space, U+00A0 ISOnum -->
]>

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
                xmlns:cms="http://www.arsdigita.com/cms/1.0"
                version="1.0">

  <xsl:template match="cms:item[objectType='com.arsdigita.cms.contenttypes.FAQItem']" mode="cms:CT_graphics"
    name="cms:CT_graphics_com_arsdigita_cms_contenttypes_FAQItem">
    <table width="435" border="0" cellspacing="0" cellpadding="0">
      <tr>
        <td class="contentTitle" align="left" valign="top">
          <xsl:value-of select="./question"/>
        </td>
      </tr>            
      <tr>
        <td class="contentText" align="left" valign="top"><xsl:value-of disable-output-escaping="yes" select="./answer"/></td>
      </tr>
    </table>
  </xsl:template>

  <xsl:template match="cms:item[objectType='com.arsdigita.cms.contenttypes.FAQItem']" mode="cms:CT_text"
    name="cms:CT_text_com_arsdigita_cms_contenttypes_FAQItem">
    <span class="synopsis"><xsl:value-of select="./question"/></span><br/>
    <span class="text"><xsl:value-of disable-output-escaping="yes" select="./answer"/></span><br/>
  </xsl:template>

</xsl:stylesheet>