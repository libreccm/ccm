<?xml version="1.0"?>
<xsl:stylesheet version="1.0"
              xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
               xmlns:ui="http://www.arsdigita.com/ui/1.0"
            xmlns:bebop="http://www.arsdigita.com/bebop/1.0">
  
  <xsl:import href="../../bebop/xsl/dcp.xsl"/>
  <xsl:output method="html" indent="yes"/>

  <xsl:param name="context-prefix"/>
  
  <xsl:template name="ui:simplePage" match="bebop:page[@class='simplePage']" >
    <html>
      <head>
        <title><xsl:call-template name="bebop:pageTitle"/></title>
        <xsl:call-template name="bebop:pageCSS"/>
      </head>
      <body>
        <xsl:call-template name="bebop:dcpJavascript"/>
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
               <xsl:call-template name="bebop:pageTitle"/>
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

  <xsl:template match="bebop:tabbedPane[@id='page-body']">
    <div class="tabbed-pane">
      <table class="tab-set">
        <tr>
          <xsl:for-each select="bebop:tabStrip/bebop:tab">
            <xsl:choose>
              <xsl:when test="@current">
                <td class="current-tab-label"><xsl:apply-templates/></td>

                <td class="current-tab-end"/>
              </xsl:when>

              <xsl:otherwise>
                <td class="tab-label"><a href="{@href}"><xsl:apply-templates/></a></td>

                <td class="tab-end"/>
              </xsl:otherwise>
            </xsl:choose>

            <td class="tab-spacer"/>
          </xsl:for-each>
        </tr>
      </table>

      <table class="rule">
        <tr><td></td></tr>
      </table>

    </div>
      <div class="current-pane">
        <xsl:apply-templates select="bebop:currentPane/*"/>
      </div>
  </xsl:template>


</xsl:stylesheet>
