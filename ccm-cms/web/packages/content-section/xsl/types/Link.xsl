<?xml version="1.0" encoding="utf-8"?>
<!-- Main template for com.arsdigita.cms.contenttype.Link  -->

<xsl:stylesheet 
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
  exclude-result-prefixes="bebop"
  xmlns:bebop="http://www.arsdigita.com/bebop/1.0"
  xmlns:cms="http://www.arsdigita.com/cms/1.0"  >
  
  <xsl:template name="cms:links" >
    <xsl:if test="count(./links[targetType != 'internalLink' or targetItem/@oid])>0">
      
      <h3>Related links</h3>
      <table border="0">
        
        <xsl:for-each select="links[targetType != 'internalLink' or targetItem/@oid]">
          <xsl:sort select="linkOrder"/>
          <tr>
            <td>
              <a>
                <xsl:attribute name="href">
                  <xsl:choose>
                    <xsl:when test="targetType='internalLink'">
                      <xsl:text>/redirect/?oid=</xsl:text><xsl:value-of select="targetItem/@oid"/>
                    </xsl:when>
                    <xsl:otherwise>
                      <xsl:value-of select="targetURI"/>
                    </xsl:otherwise>
                  </xsl:choose>
                </xsl:attribute>
                <xsl:value-of select="linkTitle"/>
              </a>
            </td>
            <td>
              <xsl:value-of select="linkDescription"/>
            </td>
          </tr>
        </xsl:for-each>
      </table>
    </xsl:if>
  </xsl:template>

  <xsl:template name="cms:links_text" >
    <xsl:if test="count(./links)>0">
      <h3 class="linkTitle">Related Links</h3>
      
      <xsl:for-each select="links">
        <span class="contentText">
          <a>
            <xsl:attribute name="href">
              <xsl:choose>
                <xsl:when test="targetType='internalLink'">
                      <xsl:text>/redirect/?oid=</xsl:text><xsl:value-of select="targetItem/@oid"/>
                </xsl:when>
                <xsl:otherwise>
                  <xsl:value-of select="targetURI"/>
                </xsl:otherwise>
              </xsl:choose>
            </xsl:attribute>
            <xsl:value-of select="linkTitle"/>
          </a>
          <xsl:if test="linkDescription">
            <span class="contentDescription"><xsl:value-of select="linkDescription"/></span>
          </xsl:if>
        </span>
      </xsl:for-each>
    </xsl:if>
  </xsl:template>
</xsl:stylesheet> 
