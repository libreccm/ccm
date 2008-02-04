<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:esd="http://www.esd.org.uk/standards"
  xmlns:terms="http://xmlns.redhat.com/london/terms/1.0"
  exclude-result-prefixes="esd terms"
  version="1.0">

  <xsl:output method="xml" version="1.0" encoding="UTF-8" indent="yes"/>

  <xsl:template match="esd:ControlledList">
    <terms:synonyms>
      <terms:domain resource="{@HomeLocation}"/>
      <xsl:apply-templates select="esd:Item/esd:UseItem"/>
    </terms:synonyms>
  </xsl:template>

  <xsl:template match="esd:UseItem">
    <xsl:variable name="isDefault">
      <xsl:choose>
        <xsl:when test="@Id=../esd:UseItem[1]/@Id">true</xsl:when>
        <xsl:otherwise>false</xsl:otherwise>
      </xsl:choose>
    </xsl:variable>
    <terms:orderedPair>
      <terms:source>
        <terms:term id="{@Id}"/>
      </terms:source>
      <terms:destination isDefault="{$isDefault}" isPreferred="true">
        <terms:term id="{../@Id}"/>
      </terms:destination>
    </terms:orderedPair>
  </xsl:template>

</xsl:stylesheet>
