<?xml version="1.0"?>
<xsl:stylesheet version="1.0"
              xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
               xmlns:ui="http://www.arsdigita.com/ui/1.0">
  
 <xsl:output method="html" indent="yes"/>
 
 
 <xsl:template name="ui:siteBanner" match="ui:siteBanner" >
   <div style="text-align: right; font-size: smaller">
     If you encounter any problems using
     <a>
       <xsl:attribute name="href">http://<xsl:value-of select="@hostname"/>/</xsl:attribute>
       <xsl:value-of select="@sitename"/></a>
     please contact the
     <a><xsl:attribute name="href">mailto:<xsl:value-of select="@admin"/></xsl:attribute>
       site administrator</a>.
   </div>
 </xsl:template>

</xsl:stylesheet>


