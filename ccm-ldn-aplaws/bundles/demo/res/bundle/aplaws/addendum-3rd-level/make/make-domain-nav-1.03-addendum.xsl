<?xml version="1.0" encoding="ISO-8859-1"?>

<!-- run this on lgcl-1.03.xml -->

<xsl:stylesheet version="1.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:terms="http://xmlns.redhat.com/london/terms/1.0"
		xmlns:lgcl="http://www.esd.org.uk/standards">

  <xsl:template match="lgcl:ControlledList">

    <terms:domain xmlns:terms="http://xmlns.redhat.com/london/terms/1.0" about="http://www.aplaws.org.uk/standards/nav/1.03/termslist.xml" key="APLAWS-NAV" title="APLAWS Navigation List" version="1.03" released="2004-01-07">
          <xsl:text>
          </xsl:text>

      <xsl:for-each select="lgcl:Item">

        <xsl:variable name="termParent" select="lgcl:BroaderItem/@Id" />

	<!-- check if it has a grand-parent -->
	<xsl:if test="/lgcl:ControlledList/lgcl:Item[@Id = $termParent]/lgcl:BroaderItem/@Id">

	  <xsl:variable name="termGrandParent" select="/lgcl:ControlledList/lgcl:Item[@Id = $termParent]/lgcl:BroaderItem/@Id" />

	  <!-- check that it doesn't have a great-grand-parent -->
	  <xsl:if test="not(/lgcl:ControlledList/lgcl:Item[@Id = $termGrandParent]/lgcl:BroaderItem/@Id)">

	    <terms:term>
	      <xsl:attribute name="id"><xsl:value-of select="@Id" /></xsl:attribute>
	      <xsl:attribute name="name"><xsl:value-of select="lgcl:Name" /></xsl:attribute>
	      <xsl:attribute name="inAtoZ"><xsl:value-of select="@AToZ" /></xsl:attribute>
	    </terms:term>
	    <xsl:text>
	    </xsl:text>

	  </xsl:if>
	  
	</xsl:if>
	
      </xsl:for-each>
      
    </terms:domain>
  
  </xsl:template>
  
</xsl:stylesheet>
