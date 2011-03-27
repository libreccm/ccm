<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
    xmlns:bebop="http://www.arsdigita.com/bebop/1.0">

 <xsl:output method="html" indent="yes"/>

 <xsl:template match="bebop:boxPanel[@axis='1']"
               xmlns:bebop="http://www.arsdigita.com/bebop/1.0">
   <table>
     <xsl:if test="string-length(@width)>0">
       <xsl:attribute name="width">
         <xsl:value-of select="@width"/>
       </xsl:attribute>
     </xsl:if>
     <xsl:if test="string-length(@border)>0">
       <xsl:attribute name="border">
         <xsl:value-of select="@border"/>
       </xsl:attribute>
     </xsl:if>
     <tr>
       <xsl:for-each select="bebop:cell">
         <td>
           <xsl:for-each select="*/@class|*/@style">
             <xsl:attribute name="{name()}">
               <xsl:value-of select="." />
             </xsl:attribute>
           </xsl:for-each>
           <xsl:apply-templates/>
         </td>
       </xsl:for-each>
     </tr>
   </table>  
 </xsl:template>

 <xsl:template match="bebop:boxPanel[@axis='2']"
               xmlns:bebop="http://www.arsdigita.com/bebop/1.0">
   <table>
     <xsl:if test="string-length(@width)>0">
       <xsl:attribute name="width">
         <xsl:value-of select="@width"/>
       </xsl:attribute>
     </xsl:if>
     <xsl:if test="string-length(@border)>0">
       <xsl:attribute name="border">
         <xsl:value-of select="@border"/>
       </xsl:attribute>
     </xsl:if>
     <xsl:for-each select="bebop:cell">
       <tr>
         <td>
           <xsl:for-each select="*/@class|*/@style">
             <xsl:attribute name="{name()}">
               <xsl:value-of select="." />
             </xsl:attribute>
           </xsl:for-each>
           <xsl:apply-templates/>
         </td>
       </tr>
     </xsl:for-each>
   </table>  
 </xsl:template>


</xsl:stylesheet>
