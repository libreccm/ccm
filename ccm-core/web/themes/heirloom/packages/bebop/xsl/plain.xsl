<?xml version="1.0"?>

<!--  The templates associated with any bebop component are in a file
      named after the component.  This file contains only sundry
      leftovers: a catch-all and some unused tags.
  -->

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

 <xsl:output method="html" indent="yes"/>

 <!-- catch-all -->
 <xsl:template match="*|@*|text()">
   <xsl:copy>
    <xsl:apply-templates/>
   </xsl:copy>
 </xsl:template>

 <!-- XXX unused -->
 <xsl:template match="bebop:sortableTable"
               xmlns:bebop="http://www.arsdigita.com/bebop/1.0">
    <xsl:apply-templates />
 </xsl:template>

</xsl:stylesheet>
