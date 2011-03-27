<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

 <xsl:output method="html" indent="yes"/>


 <xsl:template match="bebop:padFrame|bebop:border"
               xmlns:bebop="http://www.arsdigita.com/bebop/1.0">
   <table>
     <xsl:for-each select="@*">
         <xsl:attribute name="{name()}">
             <xsl:value-of select="."/>
         </xsl:attribute>
        </xsl:for-each>
     <tr><td><xsl:apply-templates /></td></tr>
   </table>
 </xsl:template>

 <xsl:template match="bebop:pad"
               xmlns:bebop="http://www.arsdigita.com/bebop/1.0">
   <table>
     <xsl:for-each select="@*">
         <xsl:attribute name="{name()}">
             <xsl:value-of select="."/>
         </xsl:attribute>
        </xsl:for-each>
     <xsl:apply-templates />
   </table>
 </xsl:template>

 <xsl:template match="bebop:panelRow"
               xmlns:bebop="http://www.arsdigita.com/bebop/1.0">
   <tr>
     <xsl:for-each select="bebop:cell">
       <td>
         <xsl:for-each select="@align|@valign|@colspan|@width">
           <xsl:attribute name="{local-name()}">
             <xsl:value-of select="."/>
           </xsl:attribute>
         </xsl:for-each>
         <xsl:apply-templates /></td>
     </xsl:for-each>
   </tr>
 </xsl:template>

 <!-- all bebop:cells should pass through attributes -->
 <xsl:template match="bebop:cell"
               xmlns:bebop="http://www.arsdigita.com/bebop/1.0">
   <xsl:for-each select="@*">
     <xsl:attribute name="{name()}">
       <xsl:value-of select="."/>
     </xsl:attribute>
   </xsl:for-each>
   <xsl:apply-templates />
 </xsl:template>
 
 <xsl:template match="bebop:columnPanel"
               xmlns:bebop="http://www.arsdigita.com/bebop/1.0">
   <xsl:apply-templates />
 </xsl:template>

</xsl:stylesheet>
