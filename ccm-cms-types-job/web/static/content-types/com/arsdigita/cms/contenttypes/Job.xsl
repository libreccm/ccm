<!DOCTYPE stylesheet [
<!ENTITY nbsp   "&#160;" ><!-- no-break space = non-breaking space, U+00A0 ISOnum -->
]>

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
                xmlns:cms="http://www.arsdigita.com/cms/1.0"
                version="1.0">

  <xsl:template match="cms:item[objectType='com.arsdigita.cms.contenttypes.Job']" mode="cms:CT_graphics"
    name="cms:CT_graphics_com_arsdigita_cms_contenttypes_Job">
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
                    <td class="contentSynopsis" valign="top" align="left">Job Description:</td>
                  </tr>
                  <tr>
                    <td class="contentText" valign="top" align="left">
                      <xsl:value-of disable-output-escaping="yes" select="./jobDescription"/>
                    </td>
                  </tr>
                </table>
              </td>
            </tr>
            <tr>
              <td bgcolor="#eeeeee" align="left" valign="top">
                <table width="100%" border="0" cellspacing="1" cellpadding="0">
                  <tr>
                    <td class="contentSynopsis" align="left" valign="top">Person Specification:</td>
                  </tr>
                  <tr>
                    <td class="contentText" align="left" valign="top">
                      <xsl:value-of disable-output-escaping="yes" select="./personSpecification"/> 
                    </td>
                  </tr>
                </table>
              </td>
            </tr>
            <tr>
              <td class="contentText" align="left" valign="top">
                <xsl:value-of disable-output-escaping="yes" select="./body"/>
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
                    <td width="40%" class="contentSynopsis" valign="top" align="left">Grade:</td>
                    <td width="60%" class="contentText" align="right" valign="top">
                      <xsl:value-of select="./grade"/>
                    </td>
                  </tr>
                </table>
              </td>
            </tr>
            
            <tr>
              <td bgcolor="#eeeeee" width="38%">
                <table width="100%" border="0" cellspacing="1" cellpadding="0">
                  <tr>
                    <td class="contentSynopsis" valign="top" align="left">Department</td>
                  </tr>
                  <tr>
                    <td class="contentText" valign="top" align="left">
                      <xsl:value-of select="./department"/>
                    </td>
                  </tr>
                </table>
              </td>
            </tr>
            
            <tr>
              <td bgcolor="#eeeeee" width="38%">
                <table width="100%" border="0" cellspacing="1" cellpadding="0">
                  <tr>
                    <td class="contentSynopsis" valign="top" align="left">Salary :</td>
                  </tr>
                  <tr>
                    <td class="contentText" valign="top" align="left">
                      <xsl:value-of select="./salary"/>
                    </td>
                  </tr>
                </table>
              </td>
            </tr>
            
            <tr>
              <td bgcolor="#eeeeee" width="38%">
                <table width="100%" border="0" cellspacing="1" cellpadding="0">
                  <tr>
                    <td class="contentSynopsis" valign="top" align="left">Ref :</td>
                  </tr>
                  <tr>
                    <td class="contentText" valign="top" align="left">
                      <xsl:value-of select="./refNumber"/>
                    </td>
                  </tr>
                </table>
              </td>
            </tr>
            
            <tr>
              <td bgcolor="#eeeeee" width="38%">
                <table width="100%" border="0" cellspacing="1" cellpadding="0">
                  <tr>
                    <td class="contentSynopsis" valign="top" align="left">Closing Date: </td>
                  </tr>
                  <tr>
                    <td class="contentText" valign="top" align="left">
                      <xsl:value-of select="./closingDate"/>
                    </td>
                  </tr>
                </table>
              </td>
            </tr>
            
            <tr>
              <td bgcolor="#eeeeee" width="38%">
                <table width="100%" border="0" cellspacing="1" cellpadding="0">
                  <tr>
                    <td class="contentSynopsis" valign="top" align="left">Contact :</td>
                  </tr>
                  <tr>
                    <td class="contentText" valign="top" align="left">
                      <xsl:value-of disable-output-escaping="yes" select="./contactDetails"/>
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

  <xsl:template match="cms:item[objectType='com.arsdigita.cms.contenttypes.Job']" mode="cms:CT_text"
    name="cms:CT_text_com_arsdigita_cms_contenttypes_Job">
    <h1 class="mainTitle">JOB <xsl:text disable-output-escaping="yes">&amp;</xsl:text>gt; <xsl:value-of select="./title"/></h1>
    <span class="subtitle">grade <xsl:text disable-output-escaping="yes">&amp;</xsl:text>gt; </span><span class="textCap"><xsl:value-of select="./grade"/></span><br/>
    <span class="subtitle">department <xsl:text disable-output-escaping="yes">&amp;</xsl:text>gt; </span><span class="textCap"><xsl:value-of select="./department"/></span><br/>
    <span class="subtitle">salary <xsl:text disable-output-escaping="yes">&amp;</xsl:text>gt; </span><span class="textCap"><xsl:value-of disable-output-escaping="yes" select="./salary"/></span><br/>
    <span class="subtitle">reference <xsl:text disable-output-escaping="yes">&amp;</xsl:text>gt; </span><span class="textCap"><xsl:value-of select="./refNumber"/></span><br/>
    <span class="subtitle">closing date <xsl:text disable-output-escaping="yes">&amp;</xsl:text>gt; </span><span class="textCap"><xsl:value-of select="./closingDate"/></span><br/>
    <span class="subtitle">contact <xsl:text disable-output-escaping="yes">&amp;</xsl:text>gt; </span><span class="textCap"><xsl:value-of disable-output-escaping="yes" select="./contactDetails"/></span><br/>
    <span class="subtitle">job description <xsl:text disable-output-escaping="yes">&amp;</xsl:text>gt; </span><span class="textCap"><xsl:value-of disable-output-escaping="yes" select="./jobDescription"/></span><br/>
    <span class="subtitle">person specification <xsl:text disable-output-escaping="yes">&amp;</xsl:text>gt; </span><span class="textCap"><xsl:value-of disable-output-escaping="yes" select="./personSpecification"/></span><br/>
    <br/>
    <span class="text"><xsl:value-of disable-output-escaping="yes" select="./body"/></span><br/>
    
  </xsl:template>
</xsl:stylesheet>