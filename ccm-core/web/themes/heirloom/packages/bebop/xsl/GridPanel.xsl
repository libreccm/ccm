<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

 <xsl:output method="html" indent="yes"/>

 <xsl:template match="bebop:gridPanel"
               xmlns:bebop="http://www.arsdigita.com/bebop/1.0">
   <table width="100%">
     <xsl:for-each select="@bottomborder">
       <xsl:attribute name="border">
         <xsl:value-of select="."/>
       </xsl:attribute>
     </xsl:for-each>
     <xsl:apply-templates />
   </table>
 </xsl:template>

</xsl:stylesheet>
