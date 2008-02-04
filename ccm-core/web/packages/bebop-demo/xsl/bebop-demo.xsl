<?xml version="1.0"?>

<xsl:stylesheet version="1.0" 
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:bebop="http://www.arsdigita.com/bebop/1.0">

<xsl:import href="../../bebop/xsl/bebop.xsl"/>

<xsl:template match="bebop:list[@class='user-list']">
 <table>
  <tr bgcolor="#00cccc">
   <th>Email</th>
   <th>First Name</th>
   <th>Last Name</th>
  </tr>
  <xsl:for-each select="descendant::demo:user"         
                xmlns:demo="http://www.arsdigita.com/demo/1.0">
    <tr bgcolor="#cccccc">
     <td><xsl:value-of select="@email"/></td>
     <td><xsl:value-of select="@first-name"/></td>
     <td><xsl:value-of select="@last-name"/></td>
    </tr>
  </xsl:for-each>
 </table>
</xsl:template>

</xsl:stylesheet>
