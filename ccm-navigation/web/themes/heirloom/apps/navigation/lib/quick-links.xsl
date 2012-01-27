<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
              xmlns:bebop="http://www.arsdigita.com/bebop/1.0"
                xmlns:nav="http://ccm.redhat.com/navigation"
                  version="1.0">

  <xsl:template match="nav:quickLinks">
    <table class="quickLinksImportant">
      <xsl:for-each select="nav:quickLink[nav:icon]">
        <tr>
          <td rowspan="2" class="icon">
            <xsl:if test="nav:icon">
              <img src="{nav:icon}" border="1"/>
            </xsl:if>
          </td>
          <td><a href="{nav:url}"><xsl:value-of select="nav:title"/></a></td>
        </tr>
        <tr>
          <td><xsl:value-of select="nav:description"/></td>
        </tr>
      </xsl:for-each>
    </table>
    <table class="quickLinksNormal">
      <xsl:for-each select="nav:quickLink[nav:icon]">
        <tr>
          <td><a href="{nav:url}"><xsl:value-of select="nav:title"/></a></td>
        </tr>
        <tr>
          <td><xsl:value-of select="nav:description"/></td>
        </tr>
      </xsl:for-each>
    </table>
  </xsl:template>

</xsl:stylesheet>
