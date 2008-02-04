<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

 <xsl:output method="html" indent="yes"/>

 <xsl:template match="bebop:splitPanel"
               xmlns:bebop="http://www.arsdigita.com/bebop/1.0">
   <table width="{@width}" border="{@border}" cellpadding="{@cellpadding}" cellspacing="{@cellspacing}">
     <tr width="100%">
       <th width="100%" nowrap="1" valign="middle" align="left" colspan="2">
         <xsl:apply-templates select="bebop:cell[position()=1]"/>
       </th>
     </tr>
     <tr width="100%">
       <td width="{@divider_left}" nowrap="1" valign="top">
         <xsl:apply-templates select="bebop:cell[position()=2]"/>
       </td>
       <td width="{@divider_right}" valign="top">
         <xsl:apply-templates select="bebop:cell[position()>2]"/>
       </td>
     </tr>
   </table>  
 </xsl:template>

</xsl:stylesheet>

