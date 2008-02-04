<?xml version="1.0"?>

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
	xmlns:bebop="http://www.arsdigita.com/bebop/1.0"
	exclude-result-prefixes="bebop">


<xsl:template match="bebop:tabStrip">
  <table border="0" cellpadding="0" cellspacing="0" width="100%">
    <tr>
      <td align="left">
        <table border="0" cellpadding="0" cellspacing="0" width="100%">
          <tr>
            <td align="left">
              <table border="0" cellpadding="0" cellspacing="0">
                <tr>
                  <td width="8"/>
                  <xsl:for-each select="bebop:tab">
                    
                    <!-- selected tab -->
                    <xsl:if test="count(@current)">
                      <td class="tab_selected">
                        <table border="0" cellpadding="2" cellspacing="0" width="100%">
                          <tr class="tab_selected">
                            <td width="6"/>
                            <td class="tab_selected">
                              <xsl:apply-templates/>
                            </td>
                            <td width="6"/>
                          </tr>
                        </table>
                      </td>
                    </xsl:if>

                    <!-- unselected tabs -->
                    <xsl:if test="count(@href)">
                      <td>
                        <table border="0" cellpadding="2" cellspacing="0" width="100%">
                          <tr>
                            <td width="6"/>
                            <td>
                              <a class="tab_unselected" href="{@href}">
                                <xsl:apply-templates/>
                              </a>
                            </td>
                            <td width="6"/>
                          </tr>
                        </table>
                      </td>
                    </xsl:if>
                  </xsl:for-each>

                </tr>
              </table>
            </td>

            <!-- MP: The preview link sits on the right end of the tabstrip. 
                 It is normally suppressed, but here we explicitly render it. -->
            <td align="right">
              <table border="0" cellpadding="2" cellspacing="0" width="100%">
                <tr>
                  <td width="6"/>
                  <td align="right">
                    <xsl:comment>Item Preview</xsl:comment>
                    <xsl:for-each select="//bebop:link[@id='preview_link']">
                      <a target="preview" class="tab_unselected" href="{@href}">
                        <xsl:apply-templates/>
                      </a>
                    </xsl:for-each>          
                  </td>
                  <td width="6"/>
                </tr>
              </table>
            </td>

          </tr>
        </table>
      </td>
    </tr>
    <tr bgcolor="#878175">
      <td>
        <table border="0" cellpadding="0" cellspacing="0">
          <tr>
            <td height="1"/>
          </tr>
        </table>
      </td>
    </tr>
    <tr bgcolor="white">
      <td>
        <table border="0" cellpadding="0" cellspacing="0">
          <tr>
            <td height="10"/>
          </tr>
        </table>
      </td>
    </tr>
  </table>
</xsl:template>

<!-- currentPane: This holds the body of the pages. -->
<xsl:template match="bebop:currentPane">
  <table border="0" cellpadding="0" cellspacing="0" width="100%">
    <tr>
      <td>
        <xsl:apply-templates/>
      </td>
    </tr>
  </table>
</xsl:template>

<!-- MP: This is a hack to place the Preview Link [@id='preview_link'] -->
<xsl:template match="*[@id='preview_link']">
  <!-- do nothing. invisible tag. -->
</xsl:template>


</xsl:stylesheet>
