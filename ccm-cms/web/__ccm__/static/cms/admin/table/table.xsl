<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
  xmlns:bebop="http://www.arsdigita.com/bebop/1.0"
  xmlns:cms="http://www.arsdigita.com/cms/1.0"
>
  <xsl:template match="bebop:table">
    <table class="data">
      <xsl:if test="bebop:thead">
        <thead>
          <tr>
            <xsl:for-each select="bebop:thead/bebop:cell">
              <th><xsl:apply-templates/></th>
            </xsl:for-each>
          </tr>
        </thead>
      </xsl:if>

      <tbody>
        <xsl:for-each select="bebop:tbody/bebop:trow">
          <tr>
            <xsl:if test="position() mod 2 = 0">
              <xsl:attribute name="class">even</xsl:attribute>
            </xsl:if>

            <xsl:for-each select="bebop:cell">
              <td><xsl:apply-templates/></td>
            </xsl:for-each>
          </tr>
        </xsl:for-each>
      </tbody>
    </table>
  </xsl:template>
</xsl:stylesheet>