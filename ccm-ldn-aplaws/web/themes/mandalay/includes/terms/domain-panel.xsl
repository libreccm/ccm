<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet 
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:bebop="http://www.arsdigita.com/bebop/1.0"
  xmlns:terms="http://xmlns.redhat.com/london/terms/1.0"
  version="1.0">

  <xsl:template match="terms:domainPanel">
    <table class="domainPanel">
      <tr>
        <td class="panel">
          <xsl:apply-templates select="terms:domainListing"/>
          <xsl:apply-templates select="bebop:link[@id='createDomain']"/>
        </td>
        <td class="body">
          <xsl:apply-templates select="terms:domainDetails"/>
          <xsl:apply-templates select="bebop:form"/>
          <xsl:apply-templates select="terms:domainUsage"/>
          <xsl:apply-templates select="bebop:link[@id='addDomainMapping']"/>
          <xsl:apply-templates select="terms:termPanel"/>
        </td>
      </tr>
    </table>
  </xsl:template>

</xsl:stylesheet>
