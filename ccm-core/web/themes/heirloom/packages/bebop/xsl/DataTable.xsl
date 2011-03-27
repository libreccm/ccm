<?xml version="1.0"?>

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
        xmlns:bebop="http://www.arsdigita.com/bebop/1.0"
        exclude-result-prefixes="bebop">

  <xsl:import href="TabbedPane.xsl"/>
  <xsl:import href="DimensionalNavbar.xsl"/>

<xsl:template match="bebop:table[@class='dataTable']">
<table cellpadding="1" cellspacing="2" border="0">
<xsl:for-each select="thead|bebop:thead"> 
<xsl:call-template name="dataTableHead">
  <xsl:with-param name="orderColumn" select="../@order"/>
  <xsl:with-param name="direction" select="../@direction"/> 
</xsl:call-template>
</xsl:for-each>
<xsl:for-each select="bebop:tbody">
<xsl:call-template name="dataTableBody"/>
</xsl:for-each>
</table>
</xsl:template>

<xsl:template name="dataTableHead">
  <xsl:param name="orderColumn">0</xsl:param>
  <xsl:param name="direction">asc</xsl:param>
  <tr class="table_header" bgcolor="#ffffff">
    <xsl:for-each select="cell|bebop:cell">
          <th class="table_header" nowrap="nowrap">
            <xsl:text>&#160;</xsl:text>
            <xsl:apply-templates/>
            <xsl:choose>
              <xsl:when test="(position()-1)=$orderColumn">
              <xsl:text>&#160;</xsl:text>   
              <img border="0">
                <xsl:attribute name="src"><xsl:choose><xsl:when test="$direction='asc'"><xsl:value-of select="//@assets"/>/assets/gray-triangle-up.gif</xsl:when><xsl:otherwise><xsl:value-of select="//@assets"/>/assets/gray-triangle-down.gif</xsl:otherwise></xsl:choose></xsl:attribute>
              </img>
             </xsl:when> 
             <xsl:otherwise>
               <xsl:text>&#160;</xsl:text> 
               <xsl:text>&#160;</xsl:text> 
             </xsl:otherwise>
           </xsl:choose>
            <xsl:text>&#160;</xsl:text>
           </th>
    </xsl:for-each>
  </tr>
</xsl:template>

<xsl:template match="thead/cell/bebop:link|bebop:thead/bebop:cell/bebop:link">
<a class="table_header" href="{@href}">
<xsl:apply-templates/>
</a>
</xsl:template>

<xsl:template name="dataTableBody">
<tbody>
<xsl:for-each select="bebop:trow">
<tr>
 <xsl:attribute name="class">
   <xsl:choose>
     <xsl:when test="position() mod 2">table_odd</xsl:when>
     <xsl:otherwise>table_even</xsl:otherwise>
   </xsl:choose>
 </xsl:attribute>
 <xsl:attribute name="bgcolor">
   <xsl:choose>
     <xsl:when test="position() mod 2">#eaded0</xsl:when>
     <xsl:otherwise>#ffffff</xsl:otherwise>
   </xsl:choose>
 </xsl:attribute>
      
 <xsl:for-each select="bebop:cell">
         <td class="table_cell" nowrap="nowrap">
           <xsl:for-each select="@align|@valign|@colspan|@width">
             <xsl:attribute name="{local-name()}">
              <xsl:value-of select="."/>
             </xsl:attribute>
           </xsl:for-each>
           <xsl:text>&#160;</xsl:text>
           <xsl:apply-templates/>
           <xsl:text>&#160;</xsl:text>
         </td>
 </xsl:for-each>
</tr>
</xsl:for-each>
</tbody>
</xsl:template>

</xsl:stylesheet>
