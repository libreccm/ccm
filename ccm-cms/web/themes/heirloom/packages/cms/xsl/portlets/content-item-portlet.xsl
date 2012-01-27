<?xml version="1.0"?>
<xsl:stylesheet  xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
             xmlns:portlet="http://www.uk.arsdigita.com/portlet/1.0"
                   version="1.0">

  <xsl:import href="../cms-item.xsl"/>
  
  <xsl:template match="portlet:contentItem">
    <xsl:apply-templates/>
  </xsl:template>
    
</xsl:stylesheet>
