<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:esd="http://www.esd.org.uk/standards"
  xmlns:terms="http://xmlns.redhat.com/london/terms/1.0"
  exclude-result-prefixes="esd terms"
  version="1.0">

  <xsl:template match="esd:ControlledList">
    <terms:hierarchy>
      <terms:domain resource="{@HomeLocation}"/>
      <xsl:apply-templates select="esd:Item"/>

    </terms:hierarchy>
  </xsl:template>

  <xsl:template match="esd:Item">
    <terms:term id="{@Id}"/>
  </xsl:template>

</xsl:stylesheet>
