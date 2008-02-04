<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

<xsl:output method="html" />

<xsl:template match="tree">
  <h1>Up My Street - Find My Nearest</h1>
  <xsl:apply-templates />
</xsl:template>

<xsl:template match="branch[@id = '0']">
<ul>
<xsl:apply-templates />
</ul>
</xsl:template>

<xsl:template match="branch">
<li>
<a href="">
  <xsl:attribute name="href">http://www.upmystreet.com/nrs/?cat=<xsl:value-of select="@id"/></xsl:attribute>
  <xsl:value-of select="@name" />
</a>
<ul>
<xsl:apply-templates />
</ul>
</li>
</xsl:template>


<xsl:template match="leaf">
<li>
  <a href="">
  <xsl:attribute name="href">http://www.upmystreet.com/nrs/?cat=<xsl:value-of select="../@id" />/<xsl:value-of select="@id" /></xsl:attribute>
  <xsl:value-of select="@name" />
  </a>
</li>
</xsl:template>

</xsl:stylesheet>