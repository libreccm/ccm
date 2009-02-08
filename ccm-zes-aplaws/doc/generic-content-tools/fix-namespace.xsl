<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:esd="http://www.esd.org.uk/standards"
  xmlns:esdbody="http://www.esd.org.uk/standards/esdbody"
  version="1.0">

  <xsl:template match="/">
    <xsl:apply-templates select="esd:ServiceContent"/>
  </xsl:template>
  
  <xsl:template match="esd:ServiceContent">
    <xsl:element name="ServiceContent" namespace="http://www.esd.org.uk/standards">
      <xsl:apply-templates select="@*"/>
      <xsl:apply-templates select="node()"/>
    </xsl:element>
  </xsl:template>

  <xsl:template match="esd:Body">
    <xsl:element name="{name()}" namespace="{namespace-uri()}">
      <xsl:apply-templates select="@*"/>
      <xsl:apply-templates select="node()" mode="html"/>
    </xsl:element>
  </xsl:template>

  <xsl:template match="text()|comment()|processing-instruction()">
    <xsl:copy/>
  </xsl:template>

  <xsl:template match="*">
    <xsl:element name="{name()}" namespace="{namespace-uri()}">
      <xsl:apply-templates select="@*"/>
      <xsl:apply-templates select="node()"/>
    </xsl:element>
  </xsl:template>

  <xsl:template match="@*">
    <xsl:attribute name="{name()}">
      <xsl:value-of select="."/>
    </xsl:attribute>
  </xsl:template>

  <xsl:template match="text()|comment()|processing-instruction()" mode="html">
    <xsl:copy/>
  </xsl:template>

  <xsl:template match="*" mode="html">
    <xsl:element name="{name()}" namespace="http://www.esd.org.uk/standards/esdbody">
      <xsl:apply-templates select="@*" mode="html"/>
      <xsl:apply-templates select="node()" mode="html"/>
    </xsl:element>
  </xsl:template>

  <xsl:template match="@*" mode="html">
    <xsl:attribute name="{name()}">
      <xsl:value-of select="."/>
    </xsl:attribute>
  </xsl:template>


</xsl:stylesheet>
