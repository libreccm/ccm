<?xml version="1.0"?>

<xsl:stylesheet version="1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
    xmlns:bebop="http://www.arsdigita.com/bebop/1.0">

<xsl:import href="../../bebop/xsl/bebop.xsl"/>

 <xsl:template match="bebop:page">
   <!-- DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" -->
   <html>
     <head>
       <title><xsl:value-of select="bebop:title"/></title>
     </head>
     <body bgcolor="#336666" text="white">
       <h2><xsl:value-of select="bebop:title"/></h2>
       You are in the bebop-demo-2 subsite.  The page format has been
       overridden.
       <xsl:apply-templates select="*[not(name()='bebop:title')]"/>
     </body>
   </html>
 </xsl:template>

<xsl:template match="bebop:list[@class='user-list']">
 <table>
  <tr bgcolor="#cccc99">
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