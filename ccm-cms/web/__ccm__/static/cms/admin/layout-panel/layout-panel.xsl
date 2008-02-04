<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
  xmlns:bebop="http://www.arsdigita.com/bebop/1.0"
>
  <xsl:template match="bebop:layoutPanel">
    <div class="layout-panel">
      <xsl:if test="bebop:top">
        <div class="top">
          <xsl:apply-templates select="bebop:top/*"/>
        </div>
      </xsl:if>

      <table class="middle">
        <tr>
          <xsl:choose>
            <xsl:when test="bebop:left and bebop:body and bebop:right">
              <td style="width: 20%" class="left"><xsl:apply-templates select="bebop:left/*"/></td>
              <td style="width: 60%" class="body"><xsl:apply-templates select="bebop:body/*"/></td>
              <td style="width: 20%" class="right"><xsl:apply-templates select="bebop:right/*"/></td>
            </xsl:when>

            <xsl:when test="bebop:left and bebop:body">
              <td style="width: 25%" class="left"><xsl:apply-templates select="bebop:left/*"/></td>
              <td style="width: 75%" class="body"><xsl:apply-templates select="bebop:body/*"/></td>
            </xsl:when>

            <xsl:when test="bebop:body and bebop:right">
              <td style="width: 75%" class="body"><xsl:apply-templates select="bebop:body/*"/></td>
              <td style="width: 25%" class="right"><xsl:apply-templates select="bebop:right/*"/></td>
            </xsl:when>

            <xsl:otherwise>
              <xsl:if test="bebop:left">
                <td class="left"><xsl:apply-templates select="bebop:left/*"/></td>
              </xsl:if>

              <xsl:if test="bebop:body">
                <td class="body"><xsl:apply-templates select="bebop:body/*"/></td>
              </xsl:if>

              <xsl:if test="bebop:right">
                <td class="right"><xsl:apply-templates select="bebop:right/*"/></td>
              </xsl:if>
            </xsl:otherwise>
          </xsl:choose>
        </tr>
      </table>

      <xsl:if test="bebop:bottom">
        <div class="bottom">
          <xsl:apply-templates select="bebop:bottom/*"/>
        </div>
      </xsl:if>
    </div>
  </xsl:template>
</xsl:stylesheet>