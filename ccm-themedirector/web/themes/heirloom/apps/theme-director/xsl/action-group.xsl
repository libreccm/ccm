<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
               xmlns:bebop="http://www.arsdigita.com/bebop/1.0"
                 xmlns:cms="http://www.arsdigita.com/cms/1.0"
                   version="1.0">
                       
  <xsl:param name="internal-theme"/>
  
<!-- TODO: this is a copy of the file at 
           /ccm-cms/web/themes/heirloom/packages/cms/xml/admin/layout-panel/layout-panel.xsl
           and should be deleted when that file is moved in to core. -->
           
  <xsl:template match="bebop:actionGroup">
    <div class="action-group">
      <div class="subject">
        <xsl:apply-templates select="bebop:subject/*"/>
      </div>

      <table class="actions">
        <xsl:for-each select="bebop:action">
          <tr class="action">
            <td class="action-icon">
              <xsl:choose>
                <xsl:when test="@class">
                  <img src="{$internal-theme}/images/action-{@class}.png"
                       height="14" width="14"/>
                </xsl:when>

                <xsl:otherwise>
                  <img src="{$internal-theme}/images/action-generic.png"
                       height="14" width="14"/>
                </xsl:otherwise>
              </xsl:choose>
            </td>

            <td class="action-widget"><xsl:apply-templates/></td>
          </tr>
        </xsl:for-each>
      </table>
    </div>
  </xsl:template>
</xsl:stylesheet>