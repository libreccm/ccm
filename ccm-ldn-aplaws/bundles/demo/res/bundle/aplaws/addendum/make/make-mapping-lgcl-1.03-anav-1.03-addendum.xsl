<?xml version="1.0" encoding="ISO-8859-1"?>

<!-- run this on domain-nav-1.03-addendum.xml -->

<xsl:stylesheet version="1.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:terms="http://xmlns.redhat.com/london/terms/1.0">

  <xsl:template match="terms:domain">

    <terms:mapping xmlns:terms="http://xmlns.redhat.com/london/terms/1.0">
      <terms:source>
        <terms:domain resource="http://www.esd.org.uk/standards/lgcl/1.03/termslist.xml"/>
      </terms:source>
      <terms:destination>
        <terms:domain resource="http://www.aplaws.org.uk/standards/nav/1.03/termslist.xml"/>
      </terms:destination>
      
      <xsl:apply-templates />
    
    </terms:mapping>

  </xsl:template>

  <xsl:template match="terms:term">
    
    <terms:orderedPair>
      <terms:source>
        <terms:term>
          <xsl:attribute name="id">
            <xsl:value-of select="@id" />
          </xsl:attribute> 
        </terms:term>
      </terms:source>
      <terms:destination>
        <terms:term>
          <xsl:attribute name="id">
            <xsl:value-of select="@id" />
          </xsl:attribute> 
        </terms:term>
      </terms:destination>
    </terms:orderedPair>

  </xsl:template>
  
</xsl:stylesheet>
