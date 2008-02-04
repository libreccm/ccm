<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
 <xsl:output method="html" indent="yes"/>

 <!-- The make-attribute template used here is defined in BoxPanel.xsl
      TODO: We need to move such utilities to one place, to make finding
      them easier
   -->

 <xsl:template match="bebop:table"
               xmlns:bebop="http://www.arsdigita.com/bebop/1.0">
   <table>
     <xsl:for-each select="@*">
       <xsl:attribute name="{name()}">
         <xsl:value-of select="."/>
       </xsl:attribute>
     </xsl:for-each>
     <xsl:apply-templates select="bebop:thead"/>
     <xsl:apply-templates select="bebop:tbody"/>
   </table>
 </xsl:template>

 <xsl:template match="bebop:thead"
               xmlns:bebop="http://www.arsdigita.com/bebop/1.0">
   <xsl:element name="thead">
     <tr>
       <xsl:for-each select="bebop:cell">
         <th>
             <xsl:for-each select="@class|@style|@align|@valign|@colspan|@width|@rowspan|@height">
               <xsl:attribute name="{local-name()}">
                <xsl:value-of select="."/>
               </xsl:attribute>
             </xsl:for-each>
         <xsl:apply-templates/>
         </th>
       </xsl:for-each>
     </tr>
   </xsl:element>
 </xsl:template>

 <xsl:template match="bebop:tbody"
               xmlns:bebop="http://www.arsdigita.com/bebop/1.0">
   <tbody>
     <xsl:for-each select="bebop:trow">
       <tr>
         <xsl:if test="../@striped and (position() mod 2) = 1">
           <xsl:attribute name="class">rowOdd</xsl:attribute>
         </xsl:if>
         <xsl:for-each select="bebop:cell">
           <td>
             <xsl:for-each select="@class|@style|@align|@valign|@width|@height">
               <xsl:attribute name="{local-name()}">
                <xsl:value-of select="."/>
               </xsl:attribute>
             </xsl:for-each>
             <xsl:apply-templates/>
           </td>
         </xsl:for-each>
       </tr>
     </xsl:for-each>
   </tbody>
 </xsl:template>

</xsl:stylesheet>
