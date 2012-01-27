<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
                xmlns:km="http://www.arsdigita.com/km/1.0"
                xmlns:bebop="http://www.arsdigita.com/bebop/1.0">
 
 <xsl:param name="internal-theme"/>
 

<xsl:template match="bebop:table[@class='abstractCollectionTable']" xmlns:bebop="http://www.arsdigita.com/bebop/1.0">
<table class="fancy" cellpadding="1" cellspacing="2" border="0">
     <xsl:for-each select="@*[@name!='class']">
       <xsl:attribute name="{name()}">
         <xsl:value-of select="."/>
       </xsl:attribute>
     </xsl:for-each>
    <xsl:for-each select="thead|bebop:thead"> 
    <xsl:call-template name="abstractCollectionTableHead">
      <xsl:with-param name="orderColumn" select="../@order"/>
      <xsl:with-param name="direction" select="../@direction"/> 
    </xsl:call-template>
    </xsl:for-each>
    <xsl:apply-templates select="bebop:tbody"/>
</table>
</xsl:template>

<xsl:template name="abstractCollectionTableHead">
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
                <xsl:attribute name="src"><xsl:choose><xsl:when test="$direction='asc'"><xsl:value-of select="//@assets"/>{$internal-theme}/images/gray-triangle-up.gif</xsl:when><xsl:otherwise><xsl:value-of select="//@assets"/>{$internal-theme}/images/gray-triangle-down.gif</xsl:otherwise></xsl:choose></xsl:attribute>
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


</xsl:stylesheet>
