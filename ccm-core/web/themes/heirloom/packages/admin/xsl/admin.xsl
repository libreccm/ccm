<?xml version="1.0"?>

<xsl:stylesheet version="1.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:admin="http://www.arsdigita.com/admin-ui/1.0"
                xmlns:bebop="http://www.arsdigita.com/bebop/1.0"
                exclude-result-prefixes="admin">

<xsl:import href="../../bebop/xsl/bebop.xsl"/>
<xsl:import href="../../bebop/xsl/DimensionalNavbar.xsl"/>
<xsl:import href="../../bebop/xsl/TabbedPane.xsl"/>
<xsl:import href="../../bebop/xsl/DataTable.xsl"/>
<xsl:import href="../../ui/xsl/ui.xsl"/>
<xsl:import href="split-panel.xsl"/>


<!-- ContextBar formatting -->
<xsl:template match="bebop:boxPanel[@class='ContextBar']">
  <xsl:apply-imports/>
  <hr />
</xsl:template>

<!-- Table with alternate color for each column. -->
 <xsl:template match="bebop:table[@class='AlternateTable']"
               xmlns:bebop="http://www.arsdigita.com/bebop/1.0">
   <table>
     <xsl:for-each select="@*">
       <xsl:attribute name="{name()}">
         <xsl:value-of select="."/>
       </xsl:attribute>
     </xsl:for-each>
     <xsl:apply-templates select="bebop:thead"/>
     <xsl:for-each select="bebop:tbody">
         <xsl:call-template name="AlternateTableBody"/>
     </xsl:for-each>
   </table>
 </xsl:template>

 <xsl:template name="AlternateTableBody"
               xmlns:bebop="http://www.arsdigita.com/bebop/1.0">
   <tbody>
     <xsl:for-each select="bebop:trow">
       <tr>
         <xsl:attribute name="bgcolor">
            <xsl:choose>
               <xsl:when test="position() mod 2">#e1d5b0</xsl:when>
               <xsl:otherwise>#ffffff</xsl:otherwise>
            </xsl:choose>
         </xsl:attribute>
         <xsl:for-each select="bebop:cell">
           <td>
             <xsl:for-each select="@align|@valign|@colspan|@width">
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
