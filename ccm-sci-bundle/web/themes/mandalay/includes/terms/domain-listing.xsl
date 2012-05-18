<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet 
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:terms="http://xmlns.redhat.com/london/terms/1.0"
  version="1.0">

  <xsl:template match="terms:domainListing">
    <table class="domainListing">
      <thead>
        <tr><th>Domains</th></tr>
      </thead>
      <tbody>
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
          
          <xsl:variable name="hint">
            <xsl:value-of select="terms:key"/>
            <xsl:text> (</xsl:text>
            <xsl:value-of select="terms:url"/>
            <xsl:text>)</xsl:text>
            <xsl:text> Version </xsl:text>
            <xsl:value-of select="terms:version"/>
            <xsl:text> released on </xsl:text>
            <xsl:value-of select="terms:released"/>
          </xsl:variable>
          
          <tr class="{$class}">
            <xsl:choose>
              <xsl:when test="@isSelected">
                <th><xsl:value-of select="terms:title"/></th>
              </xsl:when>
              <xsl:otherwise>
                <td>
                  <a title="{$hint}" href="{terms:action[@name='view']/@url}"><xsl:value-of select="terms:title"/></a>
                </td>
              </xsl:otherwise>
            </xsl:choose>
          </tr>
        </xsl:for-each>
      </tbody>
    </table>
  </xsl:template>

</xsl:stylesheet>
