<!DOCTYPE stylesheet [
<!ENTITY nbsp   "&#160;" ><!-- no-break space = non-breaking space, U+00A0 ISOnum -->
]>

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
                xmlns:cms="http://www.arsdigita.com/cms/1.0"
                version="1.0">

  <xsl:template match="cms:item[objectType='com.arsdigita.cms.contenttypes.HTMLForm']" mode="cms:CT_graphics"
    name="cms:CT_graphics_com_arsdigita_cms_contenttypes_HTMLForm">
    <table width="435" border="0" cellspacing="0" cellpadding="0">
      <tr>
        <td width="435" align="left" valign="top">
          <table width="435" border="0" cellspacing="1" cellpadding="0">
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
      </tr>
    </table>    
  </xsl:template>
  
  <xsl:template match="cms:item[objectType='com.arsdigita.cms.contenttypes.HTMLForm']" mode="cms:CT_text"
    name="cms:CT_text_com_arsdigita_cms_contenttypes_HTMLForm">
    <h1 class="mainTitle">HTMLForm <xsl:text disable-output-escaping="yes">&amp;</xsl:text>gt; <xsl:value-of select="./title"/></h1>
    <span class="synopsis"><xsl:value-of select="./lead" /></span><br />
    <br/>
    <span class="text"><xsl:value-of disable-output-escaping="yes" select="./textAsset/content"/></span>
  </xsl:template>
    
</xsl:stylesheet>
