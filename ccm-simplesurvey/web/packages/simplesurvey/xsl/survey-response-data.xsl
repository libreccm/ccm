<?xml version="1.0"?>

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0" 
        xmlns:simplesurvey="http://www.arsdigita.com/simplesurvey/1.0"
        xmlns:bebop="http://www.arsdigita.com/bebop/1.0">

  <xsl:template match="bebop:page[@id='csvPage']">
    <xsl:apply-templates select="//*[@id='csvFile']"/>
  </xsl:template>

  <xsl:template match="bebop:label[@id='csvFile']">
    <xsl:value-of select="text()"/>
  </xsl:template>

</xsl:stylesheet>



