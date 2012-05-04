<?xml version="1.0" encoding="ISO-8859-1"?>

<!-- run this on combined-hier-lgcl-and-lgcllite.xml -->

<xsl:stylesheet version="1.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:terms="http://xmlns.redhat.com/london/terms/1.0">

  <xsl:template match="combined">

    <terms:hierarchy xmlns:terms="http://xmlns.redhat.com/london/terms/1.0">
      <terms:domain resource="http://www.esd.org.uk/standards/lgcl/1.03/termslist.xml"/>
      <xsl:text>
      </xsl:text>

      <xsl:for-each select="./hierarchy-lgcl/terms:hierarchy/terms:term">

	<xsl:variable name="termId" select="@id" />

        <xsl:if test="not(/combined/hierarchy-lgcllite/terms:hierarchy/terms:term[@id = $termId])">

	  <terms:term>
	    <xsl:attribute name="id"><xsl:value-of select="@id" /></xsl:attribute>
	  </terms:term>
	  <xsl:text>
	  </xsl:text>

        </xsl:if>

      </xsl:for-each>

      <xsl:for-each select="./hierarchy-lgcl/terms:hierarchy/terms:orderedPair">

        <xsl:variable name="sourceId" select="terms:source/terms:term/@id" />
        <xsl:variable name="destinationId" select="terms:destination/terms:term/@id" />

        <xsl:if test="not(/combined/hierarchy-lgcllite/terms:hierarchy/terms:orderedPair[terms:source/terms:term/@id = $sourceId and terms:destination/terms:term/@id = $destinationId])">

          <terms:orderedPair>
            <terms:source>
              <terms:term>
                <xsl:attribute name="id"><xsl:value-of select="$sourceId" /></xsl:attribute>
              </terms:term>
            </terms:source>
            <terms:destination>
              <xsl:attribute name="isDefault"><xsl:value-of select="terms:destination/@isDefault" /></xsl:attribute>
              <xsl:attribute name="isPreferred"><xsl:value-of select="terms:destination/@isPreferred" /></xsl:attribute>
              <terms:term>
                <xsl:attribute name="id"><xsl:value-of select="$destinationId" /></xsl:attribute>
              </terms:term>
            </terms:destination>
          </terms:orderedPair>
          <xsl:text>
          </xsl:text>

        </xsl:if>

      </xsl:for-each>
    
    </terms:hierarchy>

  </xsl:template>

</xsl:stylesheet>
