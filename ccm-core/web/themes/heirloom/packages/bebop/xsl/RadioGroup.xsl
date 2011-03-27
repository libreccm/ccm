<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

  <xsl:output method="html" indent="yes"/>


  <xsl:template match="bebop:radioGroup"
    xmlns:bebop="http://www.arsdigita.com/bebop/1.0">
    <xsl:apply-templates/>
  </xsl:template>

  <!-- matching class attr is deprecated and preserved for compatibility -->
  <xsl:template match="bebop:radioGroup[@class='vertical' or @axis='2']"
    xmlns:bebop="http://www.arsdigita.com/bebop/1.0">
	<xsl:for-each select="bebop:radio">
      <xsl:apply-templates select="."/><br />
      <xsl:text disable-output-escaping="yes"> </xsl:text>
	</xsl:for-each>
  </xsl:template>

  <xsl:template match="bebop:radio"
    xmlns:bebop="http://www.arsdigita.com/bebop/1.0">
    <input id="{@name}:{@value}">
      <xsl:if test="../@readonly">
        <xsl:attribute name="readonly">readonly</xsl:attribute>        
      </xsl:if>
      <xsl:if test="../@disabled">
        <xsl:attribute name="disabled">disabled</xsl:attribute>        
      </xsl:if>
      <xsl:if test="../@title">
        <xsl:attribute name="title"><xsl:value-of select="../@title"/></xsl:attribute>
      </xsl:if>
      <xsl:attribute name="type">radio</xsl:attribute>
      <xsl:for-each select="@*[not(name()='label')]">
        <xsl:attribute name="{name()}">
          <xsl:value-of select="."/>
        </xsl:attribute>
      </xsl:for-each>
      <xsl:apply-templates />
    </input>
  </xsl:template>


</xsl:stylesheet>
