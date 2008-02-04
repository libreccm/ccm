<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

<xsl:output method="html" />

<xsl:template match="education">
  <html>
  <head><title>Up My Street - Education Example</title></head>
  <body>
  <h1>Up My Street - Education Example</h1>
  <xsl:apply-templates />
  </body>
  </html>
</xsl:template>

<xsl:template match="school">
  <table>
  <tr>
    <td>
    <xsl:apply-templates />
    </td>
  </tr>
  </table>
</xsl:template>

<xsl:template match="name">
  <b><xsl:apply-templates /></b><br />
</xsl:template>

<xsl:template match="address">
  <xsl:apply-templates /><br />
</xsl:template>

<xsl:template match="postcode">
  <xsl:apply-templates /><br />
</xsl:template>

<xsl:template match="telephone">
  <xsl:apply-templates /><br />
</xsl:template>

<xsl:template match="type">
  <xsl:apply-templates /><br />
</xsl:template>

<xsl:template match="gender">
  <xsl:apply-templates /><br />
</xsl:template>

<xsl:template match="item">
  <xsl:value-of select="@heading" />
  <xsl:text> (</xsl:text>
  <xsl:value-of select="@year" />
  <xsl:text>) </xsl:text>
  <xsl:apply-templates /><br />
</xsl:template>

</xsl:stylesheet>