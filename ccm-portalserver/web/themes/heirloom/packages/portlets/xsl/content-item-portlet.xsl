<?xml version="1.0"?>

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
  xmlns:portlet="http://www.arsdigita.com/portlet/1.0">

  <xsl:import href="../../content-section/xsl/types/ContentTypes.xsl"/>
  
  <xsl:template match="portlet:contentItem">
    <xsl:apply-templates/>
  </xsl:template>
    
</xsl:stylesheet>
