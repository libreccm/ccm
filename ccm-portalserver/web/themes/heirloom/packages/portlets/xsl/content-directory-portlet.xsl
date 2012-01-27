<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:portlet="http://www.uk.arsdigita.com/portlet/1.0" version="1.0">

  <xsl:template match="portlet:contentDirectoryEntry">
    <table border="0" cellspacing="2" cellpadding="2">
      <tr>
        <th><a href="/content/category.jsp?categoryID={@categoryID}"><xsl:value-of select="@name" /></a></th>
      </tr>
      <tr>
        <td align="left" valign="top">
          <xsl:for-each select="portlet:contentDirectorySubentry">
            <a href="/content/category.jsp?categoryID={@categoryID}"><xsl:value-of select="@name" /></a>
            <xsl:if test="not(position() = last())">
              |
            </xsl:if>
          </xsl:for-each>
        </td>
      </tr>
    </table>
  </xsl:template>


  <xsl:template match="portlet:contentDirectory">
    <table border="0" cellspacing="0" cellpadding="0">
      <xsl:for-each select="portlet:contentDirectoryEntry">
        <xsl:if test="position() mod 3 = 1">
          <xsl:text disable-output-escaping="yes">&lt;tr&gt;</xsl:text>
        </xsl:if>
        <td valign="top">
          <xsl:apply-templates select="."/>
        </td>
        <xsl:if test="position() mod 3 = 0 or position() = last()">
          <xsl:text disable-output-escaping="yes">&lt;/tr&gt;</xsl:text>
        </xsl:if>
      </xsl:for-each>
    </table>
  </xsl:template>

</xsl:stylesheet>
