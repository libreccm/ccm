<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:esd="http://www.esd.org.uk/standards"
  xmlns:terms="http://xmlns.redhat.com/london/terms/1.0"
  exclude-result-prefixes="esd terms"
  version="1.0">

  <xsl:output method="xml"/>

  <xsl:template match="esd:ItemsHierarchy">
    <terms:hierarchy>
      <terms:domain resource="{@Resource}"/>
      <xsl:apply-templates select="esd:Item" mode="root"/>
      <xsl:apply-templates select="esd:Item" mode="narrower"/>
    </terms:hierarchy>
  </xsl:template>

  <xsl:template match="esd:Item" mode="root">
    <terms:term id="{@Id}"/>
  </xsl:template>

  <xsl:template match="esd:Item" mode="narrower">
    <xsl:if test="esd:NarrowerItems/esd:Item">
      <xsl:variable name="sourceID">
        <xsl:value-of select="@Id"/>
      </xsl:variable>
      <xsl:for-each select="esd:NarrowerItems/esd:Item">
        <terms:orderedPair>
          <terms:source>
            <terms:term id="{$sourceID}"/>
          </terms:source>
          <terms:destination isDefault="{@Default}" isPreferred="{@Preferred}">
            <terms:term id="{@Id}"/>
          </terms:destination>
        </terms:orderedPair>
      </xsl:for-each>
    </xsl:if>
    <xsl:apply-templates select="esd:NarrowerItems/esd:Item" mode="narrower"/>
  </xsl:template>
  
</xsl:stylesheet>
