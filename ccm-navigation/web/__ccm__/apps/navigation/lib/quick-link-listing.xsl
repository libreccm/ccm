<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:bebop="http://www.arsdigita.com/bebop/1.0"
  xmlns:nav="http://ccm.redhat.com/navigation"
  version="1.0">

  <xsl:template match="nav:quickLinkListing">
    <table class="quickLinkListing">
      <xsl:for-each select="nav:object">
        <xsl:variable name="class">
          <xsl:choose>
            <xsl:when test="position() mod 2 = 0">
              <xsl:text>even</xsl:text>
            </xsl:when>
            <xsl:otherwise>
              <xsl:text>odd</xsl:text>
            </xsl:otherwise>
          </xsl:choose>
        </xsl:variable>

        <tr class="{$class}">
          <td rowspan="3" class="icon">
            <xsl:if test="nav:icon">
              <img src="{nav:icon}" border="1"/>
            </xsl:if>
          </td>
          <td colspan="2"><a href="{nav:url}"><xsl:value-of select="nav:title"/></a></td>
        </tr>
        <tr class="{$class}">
          <td colspan="2"><xsl:value-of select="nav:description"/></td>
        </tr>
        <tr class="{$class}">
          <td colspan="2">
              <xsl:choose>
                  <xsl:when test="nav:cascade = 'true'">
                      <xsl:text>Cascades to subcategories</xsl:text>
                  </xsl:when>
                  <xsl:otherwise>
                      <xsl:text>Displayed for this category only</xsl:text>
                  </xsl:otherwise>
              </xsl:choose>
              </td>
        </tr>
        <tr class="{$class}">
          <td>
            <a href="{nav:action[@name='edit']/@url}"><img src="/__ccm__/static/cms/admin/action-group/action-generic.png" width="14" height="14" border="0"/></a>
            <a href="{nav:action[@name='edit']/@url}">Edit</a>
          </td>
          <td>
            <a href="{nav:action[@name='delete']/@url}"><img src="/__ccm__/static/cms/admin/action-group/action-delete.png" width="14" height="14" border="0"/></a>
            <a href="{nav:action[@name='delete']/@url}">Delete</a>
          </td>
        </tr>
      </xsl:for-each>
    </table>
  </xsl:template>
</xsl:stylesheet>
