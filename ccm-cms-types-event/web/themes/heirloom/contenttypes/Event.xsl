<!DOCTYPE stylesheet [
<!ENTITY nbsp   "&#160;" ><!-- no-break space = non-breaking space, U+00A0 ISOnum -->
]>

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
                xmlns:cms="http://www.arsdigita.com/cms/1.0"
                version="1.0">

  <xsl:template match="cms:item[objectType='com.arsdigita.cms.contenttypes.Event']" mode="cms:CT_graphics"
    name="cms:CT_graphics_com_arsdigita_cms_contenttypes_Event">
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
                    <td width="40%" class="contentSynopsis" valign="top" align="left">Location:</td>
                    <td width="60%" class="contentSynopsis" align="right" valign="top">&nbsp;</td>
                  </tr>
                  <tr>
                    <td colspan="2" class="contentText" valign="top" align="left">
                      <xsl:value-of disable-output-escaping="yes" select="./location"/>
                    </td>
                  </tr>
                </table>
              </td>
            </tr>
            <tr>
              <td align="left" valign="top">
                <table width="100%" border="0" cellspacing="1" cellpadding="0">
                  <tr>
                    <td width="40%" class="contentSynopsis" align="left" valign="top">
                      <xsl:value-of select="./lead"/>
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
                    <td class="contentText" valign="top" align="right">
                      <xsl:value-of select="./eventType"/>
                    </td>
                  </tr>
                </table>
              </td>
            </tr>
            <tr>
              <td bgcolor="#eeeeee" width="38%">
                <table width="100%" border="0" cellspacing="1" cellpadding="0">
                  <tr>
                    <td width="44%" class="contentSynopsis" valign="top" align="left">Start Date:</td>
                    <td width="56%" class="contentText" align="right" valign="top">
                      <xsl:value-of select="./startDate"/>
                    </td>
                  </tr>
                </table>
              </td>
            </tr>
            <tr>
              <td bgcolor="#eeeeee" width="38%">
                <table width="100%" border="0" cellspacing="1" cellpadding="0">
                  <tr>
                    <td width="44%" class="contentSynopsis" valign="top" align="left">End Date:</td>
                    <td width="56%" class="contentText" valign="top" align="right">
                      <xsl:value-of select="./endDate"/>
                    </td>
                  </tr>
                </table>
              </td>
            </tr>
            <tr>
              <td bgcolor="#eeeeee" width="38%">
                <table width="100%" border="0" cellspacing="1" cellpadding="0">
                  <tr>
                    <td class="contentSynopsis" valign="top" align="left">Contributor:</td>
                  </tr>
                  <tr>
                    <td class="contentText" valign="top" align="left">
                      <xsl:value-of disable-output-escaping="yes" select="./mainContributor"/>
                    </td>
                  </tr>
                </table>
              </td>
            </tr>
            
            <tr>
              <td bgcolor="#eeeeee" width="38%">
                <table width="100%" border="0" cellspacing="1" cellpadding="0">
                  <tr>
                    <td width="44%" class="contentSynopsis" valign="top" align="left">Cost:</td>
                    <td width="56%" class="contentText" align="right" valign="top">
                      <xsl:value-of select="./cost"/>
                    </td>
                  </tr>
                </table>
              </td>
            </tr>
            
            <tr>
              <td bgcolor="#eeeeee" width="38%">
                <table width="100%" border="0" cellspacing="1" cellpadding="0">
                  <tr>
                    <td width="44%" class="contentSynopsis" valign="top" align="left">Map:</td>
                    <td width="56%" class="contentText" valign="top" align="right">
                      <a class="CIDetail">
                        <xsl:attribute name="href">
                          <xsl:value-of select="./mapLink"/>
                        </xsl:attribute>Click Here
                      </a>
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
  
  <xsl:template match="cms:item[objectType='com.arsdigita.cms.contenttypes.Event']" mode="cms:CT_text"
    name="cms:CT_text_com_arsdigita_cms_contenttypes_Event">
    <h1 class="mainTitle">EVENT <xsl:text disable-output-escaping="yes">&amp;</xsl:text>gt; <xsl:value-of select="./title"/></h1>
    <span class="subtitle">event type <xsl:text disable-output-escaping="yes">&amp;</xsl:text>gt; </span><span class="textCap"><xsl:value-of select="./eventType"/></span><br/>
    <span class="subtitle">start date <xsl:text disable-output-escaping="yes">&amp;</xsl:text>gt; </span><span class="textCap"><xsl:value-of select="./startDate"/></span><br/>
    <span class="subtitle">end date <xsl:text disable-output-escaping="yes">&amp;</xsl:text>gt; </span><span class="textCap"><xsl:value-of select="./endDate"/></span><br/>
    <span class="subtitle">attendees <xsl:text disable-output-escaping="yes">&amp;</xsl:text>gt; </span><span class="textCap"><xsl:value-of select="./attendees"/></span><br/>
    <span class="subtitle">contributors <xsl:text disable-output-escaping="yes">&amp;</xsl:text>gt; </span><span class="textCap"><xsl:value-of disable-output-escaping="yes" select="./mainContributor"/></span><br/>
    <span class="subtitle">cost <xsl:text disable-output-escaping="yes">&amp;</xsl:text>gt; </span><span class="textCap"><xsl:value-of select="./cost"/></span><br/>
    <span class="subtitle">map <xsl:text disable-output-escaping="yes">&amp;</xsl:text>gt; </span><span class="textCap"><a class="link"><xsl:attribute name="href"><xsl:value-of select="./mapLink"/></xsl:attribute>click for map</a></span><br/>
    <span class="subtitle">location <xsl:text disable-output-escaping="yes">&amp;</xsl:text>gt; </span><span class="textCap"><xsl:value-of disable-output-escaping="yes" select="./location"/></span><br/>
    <br/>
    <p/><span class="synopsis"><xsl:value-of select="./lead"/></span>
    <span class="text"><xsl:value-of disable-output-escaping="yes" select="./textAsset/content"/></span><br/>
  </xsl:template>
</xsl:stylesheet>