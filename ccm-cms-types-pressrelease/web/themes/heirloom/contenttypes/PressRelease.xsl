<!DOCTYPE stylesheet [
<!ENTITY nbsp   "&#160;" ><!-- no-break space = non-breaking space, U+00A0 ISOnum -->
]>

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
                xmlns:cms="http://www.arsdigita.com/cms/1.0"
                version="1.0">

  <xsl:template match="cms:item[objectType='com.arsdigita.cms.contenttypes.PressRelease']" mode="cms:CT_graphics"
    name="cms:CT_graphics_com_arsdigita_cms_contenttypes_PressRelease">
    <table width="435" border="0" cellspacing="0" cellpadding="0">
      <tr>
        <td width="285" align="left" valign="top">
          <table width="285" border="0" cellspacing="1" cellpadding="0">
            <tr>
              <td class="contentTitle" align="left" valign="top"><xsl:value-of select="./displayName"/></td>
            </tr>
            <tr>
              <td align="left" valign="top">
                <table width="100%" border="0" cellspacing="1" cellpadding="0">
                  <tr>
                    <td width="40%" class="contentSynopsis" align="left" valign="top"><xsl:value-of select="./summary"/>&nbsp;<BR></BR><BR></BR></td>
                  </tr>
                </table>
              </td>
            </tr>
            <tr>
              <td class="contentText" align="left" valign="top"><xsl:value-of disable-output-escaping="yes" select="./textAsset/content"/></td>
            </tr>
          </table>
        </td>
        <td width="150" height="100" align="right" valign="top">
          <table width="100%" border="0" cellspacing="1" cellpadding="0">
            <tr>
              <td bgcolor="#eeeeee" width="38%">
                <table width="100%" border="0" cellspacing="1" cellpadding="0">
                  <tr>
                    <td class="contentSynopsis" valign="top" align="left">Ref Code:</td>
                  </tr>
                  <tr>
                    <td class="contentText" valign="top" align="left"><xsl:value-of select="./referenceCode"/></td>
                  </tr>
                </table>
              </td>
            </tr>
            <tr>
              <td bgcolor="#eeeeee" width="38%">
                <table width="100%" border="0" cellspacing="1" cellpadding="0">
                  <tr>
                    <td class="contentSynopsis" valign="top" align="left">Contact Info :</td>
                  </tr>
                  <tr>
                    <td class="contentText" valign="top" align="left"><xsl:value-of disable-output-escaping="yes" select="./contactInfo"/></td>
                  </tr>
                </table>
              </td>
            </tr>
          </table>
        </td>
      </tr>
    </table>
  </xsl:template>
  
  <xsl:template match="cms:item[objectType='com.arsdigita.cms.contenttypes.PressRelease']" mode="cms:CT_text"
    name="cms:CT_text_com_arsdigita_cms_contenttypes_PressRelease">
    <h1 class="mainTitle">PRESS RELEASE <xsl:text disable-output-escaping="yes">&amp;</xsl:text>gt; <xsl:value-of select="./displayName"/></h1>
    <span class="subtitle">reference code <xsl:text disable-output-escaping="yes">&amp;</xsl:text>gt; </span><span class="textCap"><xsl:value-of select="./referenceCode"/></span><br/>
    <span class="subtitle">contact info <xsl:text disable-output-escaping="yes">&amp;</xsl:text>gt; </span><span class="textCap"><xsl:value-of disable-output-escaping="yes" select="./contactInfo"/></span><br/>
    <br/>
    <p/><span class="synopsis"><xsl:value-of select="./summary"/></span>
    <span class="text"><xsl:value-of disable-output-escaping="yes" select="./textAsset/content"/></span><br />
  </xsl:template>

</xsl:stylesheet>