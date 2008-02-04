<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:esd="http://www.esd.org.uk/standards"
  xmlns:terms="http://xmlns.redhat.com/london/terms/1.0"
  exclude-result-prefixes="esd terms"
  version="1.0">

  <xsl:output method="xml"/>

  <xsl:template match="/esd:RelatedItems">
    <terms:related>
      <terms:domain resource="{@Resource}"/>

      <xsl:apply-templates select="esd:RelatedItems"/>
    </terms:related>
  </xsl:template>

  <xsl:template match="esd:RelatedItems">
    <terms:unorderedPair>
      <terms:first>
        <terms:term id="{esd:Item[position()=1]/@Id}"/>
      </terms:first>
      <terms:second>
        <terms:term id="{esd:Item[position()=2]/@Id}"/>
      </terms:second>
    </terms:unorderedPair>
  </xsl:template>

</xsl:stylesheet>
