<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:portlet="http://www.uk.arsdigita.com/portlet/1.0" version="1.0">

  <xsl:template match="portlet:applicationDirectoryEntry">
    <td class="table_cell">
      <a href="{@url}">
        <xsl:value-of select="@title"/>
      </a>
      <xsl:value-of select="@description"/>
    </td>
  </xsl:template>
  
  <xsl:template match="portlet:applicationDirectory">
    <table cellpadding="4" cellspacing="2" border="0">
      <xsl:for-each select="portlet:applicationDirectoryEntry">
        <tr>
          <xsl:attribute name="class">
            <xsl:choose>
              <xsl:when test="position() mod 2">table_row_odd</xsl:when>
              <xsl:otherwise>table_row_even</xsl:otherwise>
            </xsl:choose>
          </xsl:attribute>
          <xsl:apply-templates select="."/>
        </tr>
      </xsl:for-each>
    </table>
  </xsl:template>

</xsl:stylesheet>
