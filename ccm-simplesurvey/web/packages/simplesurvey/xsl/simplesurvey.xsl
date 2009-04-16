<?xml version="1.0"?>

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0" 
        xmlns:simplesurvey="http://www.arsdigita.com/simplesurvey/1.0"
        xmlns:bebop="http://www.arsdigita.com/bebop/1.0">

   <!-- IMPORT DEFINITIONS ccm-simplesurvey installed as separate web application
   <xsl:import href="../../../../ROOT/packages/content-section/xsl/cms.xsl"/> 
   -->

   <!-- IMPORT DEFINITIONS ccm-simplesurvey installed into the main CCM webapp
   -->
   <xsl:import href="../../../packages/content-section/xsl/cms.xsl"/> 

   <xsl:import href="view.xsl"/> <!-- Stylesheet for the page for submitting a survey -->
   <xsl:import href="survey-response-data.xsl"/> <!-- Stylesheet for the CSV file page -->

   <xsl:template match="bebop:label[@class='strong']">
     <span class="strong"><xsl:value-of select="text()"/></span>
   </xsl:template>

</xsl:stylesheet>
