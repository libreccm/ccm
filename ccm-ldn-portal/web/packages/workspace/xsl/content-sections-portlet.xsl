<?xml version="1.0"?>

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
  xmlns:portlet="http://www.uk.arsdigita.com/portlet/1.0">

  <xsl:param name="root-context-prefix"/>

<xsl:template match="portlet:contentSection">
  <td class="table_cell">
    <a href="{@url}index">
      <xsl:value-of select="@name"/>
    </a>
  </td>
  <td class="table_cell">
    <a href="{@url}admin/index.jsp">
      <img border="0" width="11" height="11" alt="admin" src="{$root-context-prefix}/assets/images/arrow-box.gif"/>
    </a>
    <a href="{@url}admin/index.jsp" class="action_link">admin</a>
  </td>
</xsl:template>

<xsl:template match="portlet:contentSections">
  <table cellpadding="4" cellspacing="2" border="0">
    <xsl:for-each select="portlet:contentSection">
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
