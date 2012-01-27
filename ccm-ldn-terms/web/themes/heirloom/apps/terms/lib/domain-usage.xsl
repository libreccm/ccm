<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet 
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:terms="http://xmlns.redhat.com/london/terms/1.0"
  version="1.0">
  
  <xsl:template match="terms:domainUsage">
    <table class="domainUsage">
      <thead>
        <tr>
          <th colspan="3">Domain usage</th>
        </tr>
      </thead>
      <tbody>
        <tr>
          <th>Context</th>
          <th>Application</th>
          <th>Action</th>
        </tr>
        <xsl:for-each select="terms:object">
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
            <td>
              <xsl:if test="not(terms:useContext)">
                <xsl:text>Default</xsl:text>
              </xsl:if>
              <xsl:value-of select="terms:useContext"/>
            </td>
            <td><xsl:value-of select="terms:categoryOwner/terms:primaryURL"/></td>
            <td>
              <xsl:for-each select="terms:action">
                <a href="{@url}"><xsl:value-of select="@name"/></a>
              </xsl:for-each>
            </td>
          </tr>
        </xsl:for-each>
      </tbody>
    </table>
  </xsl:template>

</xsl:stylesheet>
