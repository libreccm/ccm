<!DOCTYPE stylesheet [
<!ENTITY nbsp   "&#160;" ><!-- no-break space = non-breaking space, U+00A0 ISOnum -->
]>

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
                xmlns:cms="http://www.arsdigita.com/cms/1.0"
                version="1.0">

  <xsl:template match="cms:item[objectType='com.arsdigita.cms.contenttypes.Agenda']" mode="cms:CT_graphics"
    name="cms:CT_graphics_com_arsdigita_cms_contenttypes_Agenda">
    <table width="435" border="0" cellspacing="0" cellpadding="0">
      <tr>
        <td width="285" align="left" valign="top">
          <table width="285" border="0" cellspacing="1" cellpadding="0">
            <tr>
              <td class="contentTitle" align="left" valign="top">
                <xsl:value-of select="./title"/>
              </td>
            </tr>
            <tr>
              <td bgcolor="#eeeeee" align="left" valign="top">
                <table width="100%" border="0" cellspacing="1" cellpadding="0">
                  <tr>
                    <td width="40%" class="contentSynopsis" valign="top" align="left">Subject Items:</td>
                    <td width="60%" class="contentSynopsis" align="right" valign="top">&nbsp;</td>
                  </tr>
                  <tr>
                    <td colspan="2" class="contentText" valign="top" align="left">
                      <xsl:value-of select="./subjectItems"/>
                    </td>
                  </tr>
                </table>
              </td>
            </tr>
            <tr>
              <td bgcolor="#eeeeee" align="left" valign="top">
                <table width="100%" border="0" cellspacing="1" cellpadding="0">
                  <tr>
                    <td width="40%" class="contentSynopsis" align="left" valign="top">Summary</td>
                    <td width="60%" class="contentSynopsis" align="right" valign="top">&nbsp;</td>
                  </tr>
                  <tr>
                    <td colspan="2" class="contentText" align="left" valign="top">
                      <xsl:value-of select="./summary"/>
                    </td>
                  </tr>
                </table>
              </td>
            </tr>
            <tr>
              <td class="contentText" align="left" valign="top">
                <xsl:value-of disable-output-escaping="yes" select="./textAsset/content"/>
              </td>
            </tr>
          </table>
        </td>
        <td width="150" height="100" align="right" valign="top">
          <table width="100%" border="0" cellspacing="1" cellpadding="0">
            <tr>
              <td bgcolor="#dddddd" align="left" valign="top" width="38%">
                <table width="100%" border="0" cellspacing="0" cellpadding="0">
                  <tr>
                    <td width="40%" class="contentSynopsis" valign="top" align="left">&nbsp;Date:</td>
                    <td width="60%" class="contentText" align="right" valign="top">
                      <xsl:value-of select="./agendaDate"/>
                    </td>
                  </tr>
                </table>
              </td>
            </tr>
            <tr>
              <td bgcolor="#eeeeee" width="38%">
                <table width="100%" border="0" cellspacing="1" cellpadding="0">
                  <tr>
                    <td class="contentSynopsis" valign="top" align="left">Location:</td>
                  </tr>
                  <tr>
                    <td class="contentText" valign="top" align="left">
                      <xsl:value-of select="./location"/>
                    </td>
                  </tr>
                </table>
              </td>
            </tr>
            <tr>
              <td bgcolor="#eeeeee" width="38%">
                <table width="100%" border="0" cellspacing="1" cellpadding="0">
                  <tr>
                    <td class="contentSynopsis" valign="top" align="left">Attendees:</td>
                  </tr>
                  <tr>
                    <td class="contentText" valign="top" align="left">
                      <xsl:value-of select="./attendees"/>
                    </td>
                  </tr>
                </table>
              </td>
            </tr>
            <tr>
              <td bgcolor="#eeeeee" width="38%">
                <table width="100%" border="0" cellspacing="1" cellpadding="0">
                  <tr>
                    <td class="contentSynopsis" valign="top" align="left">Contact Info:</td>
                  </tr>
                  <tr>
                    <td class="contentText" valign="top" align="left">
                      <xsl:value-of select="./contactInfo"/>
                    </td>
                  </tr>
                </table>
              </td>
            </tr>
            <tr>
              <td bgcolor="#eeeeee" width="38%">
                <table width="100%" border="0" cellspacing="1" cellpadding="0">
                  <tr>
                    <td class="contentSynopsis" valign="top" align="left">Creation Date:</td>
                  </tr>
                  <tr>
                    <td class="contentText" valign="top" align="left">
                      <xsl:value-of select="./creationDate"/>
                    </td>
                  </tr>
                </table>
              </td>
            </tr>
          </table>
        </td>
      </tr>
    </table>
  </xsl:template>
  
  <xsl:template match="cms:item[objectType='com.arsdigita.cms.contenttypes.Agenda']" mode="cms:CT_text"
    name="cms:CT_text_com_arsdigita_cms_contenttypes_Agenda">
    <h1 class="mainTitle">AGENDA <xsl:text disable-output-escaping="yes">&amp;</xsl:text>gt; <xsl:value-of select="./title"/></h1>
    <span class="subtitle">date <xsl:text disable-output-escaping="yes">&amp;</xsl:text>gt; </span><span class="textCap"><xsl:value-of select="./agendaDate"/></span><br/>
    <span class="subtitle">location <xsl:text disable-output-escaping="yes">&amp;</xsl:text>gt; </span><span class="textCap"><xsl:value-of select="./location"/></span><br/>
    <span class="subtitle">attendees <xsl:text disable-output-escaping="yes">&amp;</xsl:text>gt; </span><span class="textCap"><xsl:value-of select="./attendees"/></span><br/>
    <span class="subtitle">contact info <xsl:text disable-output-escaping="yes">&amp;</xsl:text>gt; </span><span class="textCap"><xsl:value-of select="./contactInfo"/></span><br/>
    <span class="subtitle">creation date <xsl:text disable-output-escaping="yes">&amp;</xsl:text>gt; </span><span class="textCap"><xsl:value-of select="./creationDate"/></span><br/>
    <span class="subtitle">subject items <xsl:text disable-output-escaping="yes">&amp;</xsl:text>gt; </span><span class="textCap"><xsl:value-of select="./subjectItems"/></span><br/>
    <span class="subtitle">summary <xsl:text disable-output-escaping="yes">&amp;</xsl:text>gt; </span><span class="textCap"><xsl:value-of select="./summary"/></span><br/>
    <br/>
    <span class="text"><xsl:value-of disable-output-escaping="yes" select="./textAsset/content"/></span><br/>
  </xsl:template>


</xsl:stylesheet>