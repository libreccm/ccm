<!DOCTYPE stylesheet [
<!ENTITY nbsp   "&#160;" ><!-- no-break space = non-breaking space, U+00A0 ISOnum -->
]>

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
                xmlns:cms="http://www.arsdigita.com/cms/1.0"
                version="1.0">

  <xsl:template match="cms:item[objectType='com.arsdigita.cms.contenttypes.MultiPartArticle']" mode="cms:CT_graphics"
    name="cms:CT_graphics_com_arsdigita_cms_contenttypes_MultiPartArticle">
    <table width="435" border="0" cellspacing="0" cellpadding="0">
      <tr>
        <td width="285" colspan="2" align="left" valign="top">
          <table width="285" border="0" cellspacing="1" cellpadding="0">
            <tr>
              <td class="contentTitle" align="left" valign="top">
                <xsl:value-of select="./title"/>
              </td>
            </tr>
            
            <tr>
              <td class="contentSynopsis" align="left" valign="top">
                <xsl:value-of select="./summary"/>
              </td>
            </tr>
            
          </table>
        </td>
        <td width="150" align="right" valign="top">
          <table width="150" border="0" cellspacing="1" cellpadding="0">
            <tr>
              <td class="contentTitle" align="left" valign="top">Sections</td>
            </tr>
            <xsl:for-each select="./sections">
              <xsl:sort select="./rank" data-type="number"/>

              <xsl:variable name="currentRank" select="./rank"/>
              <xsl:variable name="currentPage" select="count(../sections[./pageBreak ='true' and ./rank &lt; $currentRank]) + 1"/>
              <xsl:if test="(./rank = 1) or (../sections[./rank = ($currentRank - 1)]/pageBreak = 'true')">
              <tr>
                <td width="150" align="left" valign="top">
                  <span class="contentSynopsis">
                    <xsl:value-of select="$currentPage"/>.
                    <xsl:choose>
                      <xsl:when test="@oid = ../../../cms:articleSectionPanel/cms:item[position() = 1]/@oid">
                        <xsl:value-of select="title"/>
                      </xsl:when>
                      <xsl:otherwise>
                        <a href="?page={$currentPage}">
                          <xsl:value-of select="title"/>
                        </a>
                      </xsl:otherwise>
                    </xsl:choose>
                  </span>
                </td>
              </tr>
              </xsl:if>
            </xsl:for-each>
          </table>
        </td>
      </tr>
    </table>       
  </xsl:template>
  
  <xsl:template match="cms:item[objectType='com.arsdigita.cms.contenttypes.MultiPartArticle']" mode="cms:CT_text"
    name="cms:CT_text_com_arsdigita_cms_contenttypes_MultiPartArticle">
    <h1 class="mainTitle">MULTI-PART ARTICLE <xsl:text disable-output-escaping="yes">&amp;</xsl:text>gt; <xsl:value-of select="./title"/></h1>
    <span class="text">
      <p>
       Summary: <xsl:value-of select="./summary"/>
      </p>
    </span>
    <h3 class="contentTitle">Sections</h3>
    <xsl:for-each select="./sections">
      <xsl:sort select="./rank" data-type="number"/>

      <xsl:variable name="currentRank" select="./rank"/>
      <xsl:variable name="currentPage" select="count(../sections[./pageBreak ='true' and ./rank &lt; $currentRank]) + 1"/>
      <xsl:if test="(./rank = 1) or (../sections[./rank = ($currentRank - 1)]/pageBreak = 'true')">
        <div class="text">
          <b><xsl:value-of select="$currentPage"/></b>
          <a href="?page={$currentPage}">
            <b><xsl:value-of select="title"/></b>
          </a>
        </div>
      </xsl:if>
    </xsl:for-each>    
  </xsl:template>
</xsl:stylesheet>
