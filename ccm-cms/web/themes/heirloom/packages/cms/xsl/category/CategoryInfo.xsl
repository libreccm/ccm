<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
                xmlns:cms="http://www.arsdigita.com/cms/1.0"
              xmlns:bebop="http://www.arsdigita.com/bebop/1.0"
  exclude-result-prefixes="cms"
                  version="1.0">
                      
  <xsl:param name="internal-theme"/>
                      
  <xsl:template match="cms:sortableList">
    <table>
      <xsl:for-each select="bebop:cell">
        <tr>
          <xsl:choose>
            <xsl:when test="@configure">
              <td align="left" valign="top"><xsl:apply-templates/></td>
              <td align="left" valign="top">
                <xsl:if test="@prevURL">
                  <a href="{@prevURL}">
                      <img src="{$internal-theme}/images/gray-triangle-up.gif" 
                           width="10" height="9" alt="Move Category Up" border="0"/>
                  </a>
                </xsl:if>
              </td>
              <td align="left" valign="top">
                <xsl:if test="@nextURL">
                  <a href="{@nextURL}">
                      <img src="{$internal-theme}/images/gray-triangle-down.gif" 
                           width="10" height="9" alt="Move Category Down" border="0"/>
                  </a>
                </xsl:if>
              </td>
            </xsl:when>

            <xsl:otherwise>
              <td align="left" valign="top" colspan="3"><xsl:apply-templates/></td>
            </xsl:otherwise>
          </xsl:choose>
        </tr>
      </xsl:for-each>
    </table>
  </xsl:template>

</xsl:stylesheet>
