<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:esd="http://www.esd.org.uk/standards"
  xmlns:terms="http://xmlns.redhat.com/london/terms/1.0"
  exclude-result-prefixes="esd terms"
  version="1.0">

  <xsl:output method="xml"/>

  <xsl:template match="esd:ItemsHierarchy">
    <terms:mixedHierarchy>
      <terms:source>
        <terms:domain resource="{@LgdlResource}"/>
      </terms:source>
      <terms:destination>
        <terms:domain resource="{@LgslResource}"/>
      </terms:destination>
      <xsl:apply-templates select="esd:LgdlItem"/>
    </terms:mixedHierarchy>
  </xsl:template>

  <xsl:template match="esd:LgdlItem">
    <xsl:if test="esd:NarrowerItems/esd:LgslItem">
      <xsl:variable name="sourceID">
        <xsl:value-of select="@Id"/>
      </xsl:variable>
      <xsl:for-each select="esd:NarrowerItems/esd:LgslItem">
        <terms:orderedPair>
          <terms:source>
            <terms:term id="{$sourceID}"/>
          </terms:source>
          <terms:destination isDefault="{@Default}" isPreferred="true">
            <terms:term id="{@Id}"/>
          </terms:destination>
        </terms:orderedPair>
      </xsl:for-each>
    </xsl:if>
    <xsl:apply-templates select="esd:NarrowerItems/esd:LgdlItem"/>
  </xsl:template>
  
</xsl:stylesheet>
