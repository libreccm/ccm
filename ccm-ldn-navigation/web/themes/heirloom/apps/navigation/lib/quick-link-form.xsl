<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
               xmlns:bebop="http://www.arsdigita.com/bebop/1.0"
                 xmlns:nav="http://ccm.redhat.com/london/navigation"
                   version="1.0">

  <xsl:template match="nav:quickLinkForm">
    <table class="quickLinkForm">
      <thead>
        <tr>
          <th colspan="2">Quick link properties</th>
        </tr>
      </thead>
      <tbody>
        <xsl:apply-templates select="bebop:formWidget[@type='hidden']"/>
        <xsl:for-each select="*[not(name() = 'bebop:formWidget' and (@type='submit' or @type='hidden'))]">
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
            <th align="right">
                <xsl:if test="@metadata.label">
                    <xsl:value-of select="@metadata.label"/>:
                </xsl:if>
            </th>
            <td><xsl:apply-templates select="."/></td>
          </tr>
        </xsl:for-each>
      </tbody>
      <tfoot>
        <tr>
          <td></td>
          <td><xsl:apply-templates select="bebop:formWidget[@type='submit']"/></td>
        </tr>
      </tfoot>
    </table>
  </xsl:template>

</xsl:stylesheet>
