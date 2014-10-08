<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet 
  xmlns:bebop="http://www.arsdigita.com/bebop/1.0"
  xmlns:terms="http://xmlns.redhat.com/london/terms/1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  
  version="1.0">

  <xsl:template match="terms:domainForm">
    <table class="domainForm">
      <thead>
        <tr>
          <th colspan="2">Domain properties</th>
        </tr>
      </thead>
      <tbody>
        <xsl:apply-templates select="bebop:formWidget[@type='hidden']"/>
        <xsl:for-each select="*[not(name() = 'bebop:formWidget' and (@type='submit' or @type='hidden')) and not(name() = 'bebop:formErrors')]">
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
          <xsl:variable name="widgetName">
            <xsl:value-of select="@name"/>
          </xsl:variable>
          <tr class="{$class}">
            <th align="right"><xsl:value-of select="@metadata.label"/>:</th>
            <td>
              <xsl:apply-templates select="."/>
              <xsl:if test="../bebop:formErrors[@id=$widgetName]">
                <br/>
                <xsl:apply-templates select="../bebop:formErrors[@id=$widgetName]"/>
              </xsl:if>
            </td>
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
