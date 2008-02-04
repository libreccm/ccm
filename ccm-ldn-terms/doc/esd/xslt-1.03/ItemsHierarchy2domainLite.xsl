<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:esd="http://www.esd.org.uk/standards"
  xmlns:terms="http://xmlns.redhat.com/london/terms/1.0"
  exclude-result-prefixes="esd terms"
  version="1.0">

  <xsl:output method="xml"/>

  <xsl:template match="esd:ItemsHierarchy">
    <terms:domain 
      about="{@Resource}" 
      key="LGCL" 
      title="Local Government Category List"
      version="{@Version}"
      released="{esd:Metadata/esd:Date.Issued}">

      <xsl:apply-templates select="esd:Item"/>
      <xsl:apply-templates select="esd:Item/esd:NarrowerItems/esd:Item"/>
    </terms:domain>
  </xsl:template>

  <xsl:template match="esd:Item">
    <terms:term
      id="{@Id}"
      name="{esd:Name}"
      inAtoZ="false"/>
  </xsl:template>
  
</xsl:stylesheet>
