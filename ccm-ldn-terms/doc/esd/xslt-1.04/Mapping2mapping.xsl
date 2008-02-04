<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:esd="http://www.esd.org.uk/standards"
  xmlns:terms="http://xmlns.redhat.com/london/terms/1.0"
  exclude-result-prefixes="esd terms"
  version="1.0">

  <xsl:output method="xml"/>

  <xsl:template match="/esd:ItemMappings">
    <terms:mapping>
      <terms:source>
        <terms:domain resource="{@FromResource}"/>
      </terms:source>
      <terms:destination>
        <terms:domain resource="{@ToResource}"/>
      </terms:destination>
      <xsl:apply-templates select="esd:ItemMapping"/>
    </terms:mapping>
  </xsl:template>

  <xsl:template match="esd:ItemMapping">
    <xsl:for-each select="esd:From/esd:Item">
      <xsl:variable name="sourceID">
        <xsl:value-of select="@Id"/>
      </xsl:variable>
      <xsl:for-each select="../../esd:To/esd:Item">
        <terms:orderedPair>
          <terms:source>
            <terms:term id="{$sourceID}"/>
          </terms:source>
          <terms:destination>
            <terms:term id="{@Id}"/>
          </terms:destination>
        </terms:orderedPair>
      </xsl:for-each>
    </xsl:for-each>
  </xsl:template>

</xsl:stylesheet>
