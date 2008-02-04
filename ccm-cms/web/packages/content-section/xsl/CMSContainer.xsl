<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
	xmlns:cms="http://www.arsdigita.com/cms/1.0"
	xmlns:bebop="http://www.arsdigita.com/bebop/1.0"
  exclude-result-prefixes="cms">

  <xsl:output method="html" indent="yes"/>

  <!-- CMS container: Wraps each subelement in a paragraph tag -->
  <xsl:template match="cms:container">
    <xsl:for-each select="*">
      <p><xsl:apply-templates select="." /></p>
    </xsl:for-each>
  </xsl:template>

  <!-- CMS container: But no wrapping when just inside the page -->
  <xsl:template match="bebop:currentPane/cms:container">
    <xsl:for-each select="*">
      <xsl:apply-templates select="."/>
    </xsl:for-each>
  </xsl:template>

  <xsl:template match="cms:container[@id='navigation']">
    <table width="100%" cellspacing="0" cellpadding="0" border="0">
      <tr>
        <td valign="top">
          <xsl:choose>
            <xsl:when test="cms:breadCrumbTrail">
              <xsl:attribute name="class">bread_crumbs</xsl:attribute>
              <xsl:apply-templates select="cms:breadCrumbTrail"/>
            </xsl:when>
            <xsl:otherwise>
              <xsl:apply-templates select="bebop:label"/>
              <xsl:apply-templates select="bebop:link"/>
            </xsl:otherwise>
          </xsl:choose>
        </td>
        <td valign="top" align="right" class="top_right_links">
          <xsl:apply-templates select="*[@id='global-navbar']"/>
        </td>
      </tr>
    </table>
</xsl:template>


</xsl:stylesheet>
