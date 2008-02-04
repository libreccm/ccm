<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet 
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:bebop="http://www.arsdigita.com/bebop/1.0"
  xmlns:terms="http://xmlns.redhat.com/london/terms/1.0"
  version="1.0">

  <xsl:template match="terms:termPanel">
    <table class="termPanel">
      <tr>
        <td class="panel">
          <xsl:apply-templates select="terms:termListing"/>
          <xsl:apply-templates select="bebop:link[@id='createTerm']"/>
        </td>
        <td class="body">
          <xsl:apply-templates select="terms:termDetails"/>
          <xsl:apply-templates select="bebop:link[@id='addRootTerm']"/>
          <xsl:apply-templates select="bebop:link[@id='removeRootTerm']"/>
          <xsl:apply-templates select="terms:narrowerTermListing"/>
          <xsl:apply-templates select="bebop:link[@id='addNarrowerTerm']"/>
          <xsl:apply-templates select="terms:relatedTermListing"/>
          <xsl:apply-templates select="terms:termPicker"/>
          <xsl:apply-templates select="terms:termFilteredListing"/>
          <xsl:apply-templates select="bebop:form"/>
        </td>
      </tr>
    </table>
  </xsl:template>

</xsl:stylesheet>
