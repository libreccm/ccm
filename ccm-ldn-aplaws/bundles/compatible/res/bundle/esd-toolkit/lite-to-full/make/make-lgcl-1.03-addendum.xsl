<?xml version="1.0" encoding="ISO-8859-1"?>

<!-- run this on combined-lgcl-and-lgcllite.xml -->

<xsl:stylesheet version="1.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:terms="http://xmlns.redhat.com/london/terms/1.0">

  <xsl:template match="combined">

    <terms:domain xmlns:terms="http://xmlns.redhat.com/london/terms/1.0" about="http://www.esd.org.uk/standards/lgcl/1.03/termslist.xml" key="LGCL" title="Local Government Category List" version="1.03" released="2004-03-19">
          <xsl:text>
          </xsl:text>

      <xsl:for-each select="./lgcl/terms:domain/terms:term">

        <xsl:variable name="termId" select="@id" />

        <xsl:if test="not(/combined/lgcllite/terms:domain/terms:term[@id = $termId])">

          <terms:term>
            <xsl:attribute name="id"><xsl:value-of select="@id" /></xsl:attribute>
            <xsl:attribute name="name"><xsl:value-of select="@name" /></xsl:attribute>
            <xsl:attribute name="inAtoZ"><xsl:value-of select="@inAtoZ" /></xsl:attribute>
          </terms:term>
          <xsl:text>
          </xsl:text>

        </xsl:if>

      </xsl:for-each>
    
    </terms:domain>

  </xsl:template>

</xsl:stylesheet>
