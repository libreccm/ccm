<!DOCTYPE stylesheet [
<!ENTITY nbsp   "&#160;" ><!-- no-break space = non-breaking space, U+00A0 ISOnum -->
]>

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
                xmlns:cms="http://www.arsdigita.com/cms/1.0"
                version="1.0">

  <xsl:template match="cms:item[objectType='com.arsdigita.cms.contenttypes.Service']" mode="cms:CT_graphics"
    name="cms:CT_graphics_com_arsdigita_cms_contenttypes_Service">
    <table width="435" border="0" cellspacing="0" cellpadding="0">
      <tr>
        <td width="285" align="left" valign="top">
          <table width="285" border="0" cellspacing="1" cellpadding="0">
            <tr>
              <td class="contentTitle" align="left" valign="top"><xsl:value-of select="./title"/></td>
            </tr>
            <tr>
              <td bgcolor="#eeeeee" align="left" valign="top">
                <table width="100%" border="0" cellspacing="1" cellpadding="0">
                  <tr>
                    <td width="40%" class="contentSynopsis" valign="top" align="left">Services Provided:</td>
                    <td width="60%" class="contentSynopsis" align="right" valign="top">&nbsp;</td>
                  </tr>
                  <tr>
                    <td colspan="2" class="contentText" valign="top" align="left">
                      <xsl:value-of select="./servicesProvided"/>
                    </td>
                  </tr>
                </table>
              </td>
            </tr>
            <tr>
              <td align="left" valign="top">
                <table width="100%" border="0" cellspacing="1" cellpadding="0">
                  <tr>
                    <td width="40%" class="contentSynopsis" align="left" valign="top"><xsl:value-of select="./summary" /></td>
                  </tr>
                </table>
              </td>
            </tr>      
          </table>
        </td>
        <td width="150" height="100" align="right" valign="top">
          <table width="100%" border="0" cellspacing="1" cellpadding="0">
            <tr>
              <td bgcolor="#eeeeee" width="38%">
                <table width="100%" border="0" cellspacing="1" cellpadding="0">
                  <tr>
                    <td class="contentSynopsis" valign="top" align="left">Opening Times:</td>
                  </tr>
                  <tr>
                    <td class="contentText" valign="top" align="left"><xsl:value-of select="./openingTimes" /></td>
                  </tr>
                </table>
              </td>
            </tr>
            <tr>
              <td bgcolor="#eeeeee" width="38%">
                <table width="100%" border="0" cellspacing="1" cellpadding="0">
                  <tr>
                    <td class="contentSynopsis" valign="top" align="left">Contacts:</td>
                  </tr>
                  <tr>
                    <td class="contentText" valign="top" align="left"><xsl:value-of select="./contacts" /></td>
                  </tr>
                </table>
              </td>
            </tr>
            <tr>
              <td bgcolor="#eeeeee" width="38%">
                <table width="100%" border="0" cellspacing="1" cellpadding="0">
                  <tr>
                    <td class="contentSynopsis" valign="top" align="left">Address:</td>
                  </tr>
                  <tr>
                    <td class="contentText" valign="top" align="left"><xsl:value-of select="./address" /></td>
                  </tr>
                </table>
              </td>
            </tr>
          </table>
        </td>
      </tr>
    </table>
  </xsl:template>
  
  <xsl:template match="cms:item[objectType='com.arsdigita.cms.contenttypes.Service']" mode="cms:CT_text"
    name="cms:CT_text_com_arsdigita_cms_contenttypes_Service">
    <h1 class="mainTitle">SERVICE <xsl:text disable-output-escaping="yes">&amp;</xsl:text>gt; <xsl:value-of select="./title"/></h1>
    <span class="subtitle">services provided <xsl:text disable-output-escaping="yes">&amp;</xsl:text>gt; </span><span class="textCap"><xsl:value-of select="./servicesProvided"/></span><br/>
    <span class="subtitle">opening times <xsl:text disable-output-escaping="yes">&amp;</xsl:text>gt; </span><span class="textCap"><xsl:value-of select="./openingTimes"/></span><br/>
    <span class="subtitle">contacts <xsl:text disable-output-escaping="yes">&amp;</xsl:text>gt; </span><span class="textCap"><xsl:value-of select="./contacts"/></span><br/>
    <span class="subtitle">address <xsl:text disable-output-escaping="yes">&amp;</xsl:text>gt; </span><span class="textCap"><xsl:value-of select="./address"/></span><br/>
    <p/>
    <span class="text"><xsl:value-of select="./summary"/></span>
  </xsl:template>
</xsl:stylesheet>