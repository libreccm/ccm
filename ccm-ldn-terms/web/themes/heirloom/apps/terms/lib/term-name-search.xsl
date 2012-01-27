<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet 
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:bebop="http://www.arsdigita.com/bebop/1.0"
  xmlns:terms="http://xmlns.redhat.com/london/terms/1.0"
  version="1.0">

  <xsl:template match="terms:termNameSearch">
    <xsl:apply-templates select="bebop:formWidget[@type='hidden']"/>
    <table>
      <tr>
        <th>Search:</th>
        <td><xsl:apply-templates select="bebop:formWidget[@name='name']"/></td>
        <td><xsl:apply-templates select="bebop:formWidget[@type='submit']"/></td>
      </tr>
    </table>
  </xsl:template>

</xsl:stylesheet>
