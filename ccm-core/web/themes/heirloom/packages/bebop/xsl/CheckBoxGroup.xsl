<?xml version="1.0"?>
<xsl:stylesheet version="1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:bebop="http://www.arsdigita.com/bebop/1.0"
>
  <xsl:output method="html" indent="yes"/>

  <xsl:template match="bebop:checkboxGroup">
    <xsl:apply-templates select="bebop:checkbox"/>
  </xsl:template>

  <xsl:template match="bebop:checkbox">
    <input id="{@name}:{@value}" type="checkbox">
      <xsl:copy-of select="@*"/>

      <xsl:if test="../@readonly">
        <xsl:attribute name="readonly">readonly</xsl:attribute>
      </xsl:if>

      <xsl:if test="../@disabled">
        <xsl:attribute name="disabled">disabled</xsl:attribute>
      </xsl:if>
      <xsl:if test="../@title">
        <xsl:attribute name="title"><xsl:value-of select="../@title"/></xsl:attribute>
      </xsl:if>
      <xsl:if test="../@onclick">
        <xsl:attribute name="onclick"><xsl:value-of select="../@onclick"/></xsl:attribute>
      </xsl:if>
    </input>

    <label for="{@name}:{@value}">
      <xsl:apply-templates select="bebop:label"/>
    </label>

    <br/>
  </xsl:template>
</xsl:stylesheet>
