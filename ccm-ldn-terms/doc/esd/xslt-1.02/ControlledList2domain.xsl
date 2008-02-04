<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:esd="http://www.esd.org.uk/standards"
  xmlns:terms="http://xmlns.redhat.com/london/terms/1.0"
  exclude-result-prefixes="esd terms"
  version="1.0">

  <xsl:output method="xml"/>

  <xsl:template match="esd:ControlledList">
    <terms:domain 
      about="{@HomeLocation}" 
      key="{@ItemName}" 
      title="{@ListName}"
      version="{@Version}"
      released="{@VersionDate}">

      <xsl:apply-templates select="esd:Item"/>
    </terms:domain>
  </xsl:template>

  <xsl:template match="esd:Item">
    <terms:term
      id="{@Id}"
      name="{esd:Name}"
      inAtoZ="{@AToZ}">
      <xsl:if test="esd:Shortcut">
        <xsl:attribute name="shortcut">
          <xsl:value-of select="esd:Shortcut"/>
        </xsl:attribute>
      </xsl:if>
    </terms:term>
  </xsl:template>

</xsl:stylesheet>
