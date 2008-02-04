<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
  xmlns:bebop="http://www.arsdigita.com/bebop/1.0"
>
  <xsl:template match="bebop:tabbedPane">
    <div class="tabbed-pane">
      <table class="tab-set">
        <tr>
          <xsl:for-each select="bebop:tabStrip/bebop:tab">
            <xsl:choose>
              <xsl:when test="@current">
                <td class="current-tab-label"><xsl:apply-templates/></td>

                <td class="current-tab-end"/>
              </xsl:when>

              <xsl:otherwise>
                <td class="tab-label"><a href="{@href}"><xsl:apply-templates/></a></td>

                <td class="tab-end"/>
              </xsl:otherwise>
            </xsl:choose>

            <td class="tab-spacer"/>
          </xsl:for-each>
        </tr>
      </table>

      <table class="rule"><tr><td></td></tr></table>

      <div class="current-pane">
        <xsl:apply-templates select="bebop:currentPane/*"/>
      </div>
    </div>
  </xsl:template>
</xsl:stylesheet>