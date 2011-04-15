<?xml version="1.0"?>

<!--
A convenient collection of UI XSL templates
-->

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
                xmlns:bebop="http://www.arsdigita.com/bebop/1.0"
                xmlns:ui="http://www.arsdigita.com/ui/1.0">

  <!-- templates for each component -->
  <xsl:import href="../../ui/xsl/ui.xsl"/>
  <xsl:import href="../../ui/xsl/user-banner.xsl"/>
  <xsl:import href="simple-page.xsl"/>

 <xsl:template name="ui:userBanner" match="ui:userBanner" >
   <xsl:choose>
    <!-- If a userID is set, then we know user is logged in. If not, -->
    <!-- Log in message is presented...  -->
     <xsl:when test="@userID">
      <table class="globalAdminHeader">
       <tr>
        <!-- Shadow man logo by default -->
        <a href="http://www.redhat.com"><td class="globalLogo"></td></a>
         <td><xsl:text>&#160;&#160;&#160;&#160;&#160;&#160;</xsl:text></td>
          <td style="margin: 0; border: 0; padding: 0;">
           <table align="center" style="margin: 0; border: 0; padding: 0;">
            <tr>
             <td style="margin: 0; color: #3f3f3f; border: 0; padding: 0; padding-right: 18px;">
             <xsl:value-of select="@greeting"/>
             <xsl:text>&#160;</xsl:text>
              <strong><xsl:value-of select="@givenName"/>
               <xsl:text>&#160;</xsl:text>
               <xsl:value-of select="@familyName"/>
              </strong>
             </td>
            </tr>
           </table>
          </td>
          <td style="margin: 0; border: 0; padding: 0;">
           <table align="right" style="margin: 0; border: 0; padding: 0;">
            <tr>
<!-- Help going away for now, but retaining code -->
<!--             <td class="global-link-icon">
               <a href="/assets/help/toc_main.html"><img src="/assets/lifesaver.png" height="18" width="21"/></a>
             </td>
             <td class="global-link">
               <a href="/assets/help/toc_main.html">
                <xsl:value-of select="@helpLabel"/>
               </a>
             </td>
-->
             <td class="global-link-icon">
              <a>
               <xsl:attribute name="href">
                <xsl:value-of select="@workspaceURL"/>
               </xsl:attribute>
               <img src="/assets/home.png" height="18" width="18"/>
              </a>
             </td>
             <td class="global-link">
               <a>
                 <xsl:attribute name="href">
                  <xsl:value-of select="@workspaceURL"/>
                 </xsl:attribute>
                <xsl:value-of select="@portalLabel"/>
               </a>
             </td>

             <td class="global-link-icon">
              <a>
               <xsl:attribute name="href">
                 <xsl:value-of select="@logoutURL"/>
               </xsl:attribute>
               <img src="/assets/lock.png" height="18" width="14"/>
              </a>
            </td>
            <td class="global-link">
              <a>
               <xsl:attribute name="href">
                 <xsl:value-of select="@logoutURL"/>
               </xsl:attribute>
               <xsl:value-of select="@signoutLabel"/>
              </a>
            </td>
         </tr>
       </table>
      </td>
     </tr>
    </table>
     </xsl:when>
     <xsl:otherwise>
      <table class="globalAdminHeader">
       <tr>
        <!-- Shadow man logo by default -->
        <a href="http://www.redhat.com"><td class="globalLogo"></td></a>
        <td><xsl:text>&#160;&#160;&#160;&#160;&#160;&#160;</xsl:text></td>
        <td style="margin: 0; border: 0; padding: 0;">
         <table align="center" style="margin: 0; border: 0; padding: 0;">
          <tr>
           <td style="margin: 0; color: #3f3f3f; border: 0; padding: 0; padding-right: 18px;">
            You are not currently logged in
           </td>
          </tr>
         </table>
        </td>
        <td style="font-size: smaller; text-align: right">
         <a>
          <xsl:attribute name="href">
           <xsl:value-of select="@loginURL"/>
          </xsl:attribute>
          Login
         </a>
       </td>
       </tr>
      </table>
     </xsl:otherwise>
   </xsl:choose>
 </xsl:template>

</xsl:stylesheet>

