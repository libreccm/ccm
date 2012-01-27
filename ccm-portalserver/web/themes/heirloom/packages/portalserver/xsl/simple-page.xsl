<?xml version="1.0"?>
<xsl:stylesheet version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
  xmlns:ui="http://www.arsdigita.com/ui/1.0"
  xmlns:bebop="http://www.arsdigita.com/bebop/1.0">
  
 <xsl:output method="html" indent="yes"/>
 
 <xsl:param name="context-prefix"/>
 
 <xsl:template name="ui:simplePage" match="bebop:page[@class='simplePage']" >
   <html>
     <head>
       <title><xsl:value-of select="bebop:title"/></title>
        <xsl:call-template name="ui:simplePageCSS"/>
     </head>
     <body>
       <table width="100%" cellspacing="0" cellpadding="0" border="0">
         <tr>
           <td>
             <xsl:apply-templates select="*[@metadata.tag='top']"/>
           </td>
         </tr>
       </table>
       

       <table width="100%" cellspacing="0" cellpadding="0" border="0">
         <tr>
           <td bgcolor="#878175">
             <table cellspacing="0" cellpadding="0" border="0">
               <tr>
                 <td height="1"></td>
               </tr>
             </table>
           </td>
         </tr>
       </table>

       <table width="100%" cellspacing="0" cellpadding="0" border="0">
         <tr>
           <td>
             <table cellspacing="0" cellpadding="0" border="0">
               <tr>
                 <td height="4"></td>
               </tr>
             </table>
           </td>
         </tr>
       </table>

       <table width="100%" cellspacing="0" cellpadding="0" border="0">
         <tr>
           <td>
             <div class="page-title">
               <xsl:apply-templates select="/bebop:page/bebop:title"/>
             </div>
           </td>
         </tr>
       </table>

       <table width="100%" cellspacing="0" cellpadding="0" border="0">
         <tr>
           <td>
             <table cellspacing="0" cellpadding="0" border="0">
               <tr>
                 <td height="4"></td>
               </tr>
             </table>
           </td>
         </tr>
       </table>

       <table width="100%" cellspacing="0" cellpadding="0" border="0">
         <tr>
           <td>
             <xsl:apply-templates select="*[@metadata.tag='left']"/>
           </td>
           <td>
             <xsl:apply-templates select="*[not(@metadata.tag)]"/>
           </td>
           <td>
             <xsl:apply-templates select="*[@metadata.tag='right']"/>
           </td>
         </tr>
       </table>

       <table width="100%" cellspacing="0" cellpadding="0" border="0">
         <tr>
           <td>
             <table cellspacing="0" cellpadding="0" border="0">
               <tr>
                 <td height="4"></td>
               </tr>
             </table>
           </td>
         </tr>
       </table>

       <table width="100%" cellspacing="0" cellpadding="0" border="0">
         <tr>
           <td bgcolor="#878175">
             <table cellspacing="0" cellpadding="0" border="0">
               <tr>
                 <td height="1"></td>
               </tr>
             </table>
           </td>
         </tr>
       </table>

       <table width="100%" cellspacing="0" cellpadding="0" border="0">
         <tr>
           <td>
             <xsl:apply-templates select="*[@metadata.tag='bottom']"/>
           </td>
         </tr>
       </table>
       
       <xsl:apply-templates select="bebop:structure"/>
     </body>
   </html>
 </xsl:template>

 <xsl:template match="ui:simplePagePanel">
   <xsl:apply-templates select="*"/>
 </xsl:template>

 <xsl:template match="ui:simplePageContent">
   <xsl:apply-templates select="*"/>
 </xsl:template>

</xsl:stylesheet>
