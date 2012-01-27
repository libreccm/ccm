<?xml version="1.0"?>

<xsl:stylesheet
    version="1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:bebop="http://www.arsdigita.com/bebop/1.0"
    xmlns:ui="http://www.arsdigita.com/ui/1.0">

<xsl:import href="../../bebop/xsl/DimensionalNavbar.xsl"/>
<xsl:import href="../../bebop/xsl/bebop.xsl"/>
<xsl:import href="../../toolbox/xsl/ControlBar.xsl"/>
<xsl:import href="ui.xsl"/>

<xsl:output method="html" indent="yes"/>

<xsl:variable name="assets-dir">/packages/portalserver/www/assets</xsl:variable><xsl:variable name="css-dir">/packages/portalserver/www/css</xsl:variable>

  <xsl:template name="ui:simplePageCSS">
    <xsl:call-template name="ui:simplePageCSSMain"/>
    <link href="{$css-dir}/portal-sitemap.css" rel="stylesheet" type="text/css"/>
  </xsl:template>

  <xsl:template name="ui:simplePageCSSMain">
    <xsl:for-each select="bebop:stylesheet">
      <link href="{@href}" rel="stylesheet" type="{@type}"/>
    </xsl:for-each>
  </xsl:template>


<!--  XXX: Next Project - commented out for now
 <xsl:template match="bebop:tree[@class = 'portalsitemap_tree']">
   <table>
     <xsl:for-each select="bebop:t_node">
       <xsl:call-template name="write-node">
         <xsl:with-param name="node" select="."/>
       </xsl:call-template>
     </xsl:for-each>
   </table>
 </xsl:template>

 <xsl:template name="write-node">
   <xsl:param name="node"/>
   <xsl:param name="total-indent"/>
   <xsl:param name="level-indent"><img href="ti_extends_horiz.gif"></img></xsl:param>
   <xsl:for-each select="$node">
     <tr>
       <td>
         <xsl:value-of disable-output-escaping="yes" select="$total-indent"/>
         <xsl:choose>
           <xsl:when test="@collapsed='t'">
             <a href="{@href}">+</a>
           </xsl:when>
           <xsl:when test="@expanded='t'">
             <a href="{@href}">-</a>
           </xsl:when>
           <xsl:otherwise>
             <xsl:text>&#160;</xsl:text>
           </xsl:otherwise>
         </xsl:choose>
         <xsl:text>&#160;</xsl:text>
         <xsl:apply-templates select="*[position()=1]"/>
       </td>
     </tr>
     <xsl:for-each select="bebop:t_node">
       <xsl:call-template name="write-node">
         <xsl:with-param name="node" select="."/>
         <xsl:with-param name="total-indent">
           <xsl:copy-of select="$total-indent"/>
           <xsl:copy-of select="$level-indent"/>
         </xsl:with-param>
       </xsl:call-template>
     </xsl:for-each>
   </xsl:for-each>
 </xsl:template>
-->

 <xsl:template match="bebop:gridPanel[@class='propertiesform']"
               xmlns:bebop="http://www.arsdigita.com/bebop/1.0">
   <xsl:variable name="cellPath" select="bebop:panelRow/bebop:cell"/>
   <table width="100%">
     <tr>
       <td class="heading_cell">
         <xsl:value-of select="$cellPath/bebop:label[@id='propnameselected']"/>
       </td>
     </tr> 
     <tr>
       <td width="25%"></td>
       <td>
         <table class="details" width="50%">
           <xsl:for-each select="@bottomborder">
             <xsl:attribute name="border">
               <xsl:value-of select="."/>
             </xsl:attribute>
           </xsl:for-each>


           <xsl:apply-templates select="bebop:formWidget"/>
           <tr>
             <th>
               <xsl:value-of select="$cellPath/bebop:label[@id='propnamelabel']"/>
             </th>
             <td>
               <xsl:value-of select="$cellPath/bebop:label[@id='propname']"/>
             </td>
           </tr>
           <tr>
             <th>
               <xsl:value-of select="$cellPath/bebop:label[@id='propurllabel']"/>
             </th>
             <td>
               <xsl:value-of select="$cellPath/bebop:label[@id='propurl']"/>
             </td>
           </tr>
           <tr>
             <th>
               <xsl:value-of select="$cellPath/bebop:label[@id='propdescriptionlabel']"/>
             </th>
             <td>
               <xsl:value-of select="$cellPath/bebop:label[@id='propdescription']"/>
             </td>
           </tr>
           <tr>
             <th>
               <xsl:value-of select="$cellPath/bebop:label[@id='propcreationdatelabel']"/>
             </th>
             <td>
               <xsl:value-of select="$cellPath/bebop:label[@id='propcreationdate']"/>
             </td>
           </tr>
           <tr>
             <th>
               <xsl:value-of select="$cellPath/bebop:label[@id='propstatuslabel']"/>
             </th>
             <td>
               <xsl:value-of select="$cellPath/bebop:label[@id='propstatus']"/>
             </td>
           </tr>
          </table>
       </td>
       <td width="25%"></td>
      </tr>
    </table>
    <xsl:apply-templates select="$cellPath/bebop:link[@id='propedittheseprops']"/>

 </xsl:template>

<!-- ++++++++++++++++++++++++++++++++++++++++++ -->
<!-- This template match is for the Edit Props form -->
 <xsl:template match="bebop:gridPanel[@class='editpropscontainer']"
               xmlns:bebop="http://www.arsdigita.com/bebop/1.0">
   <xsl:variable name="cellPath" select="bebop:panelRow/bebop:cell"/>
   <table width="100%">  <!-- Overall table with three columns -->
     <tr>
       <td class="heading_cell">
         <xsl:value-of select="$cellPath/bebop:label[@id='editpropnameselected']"/>
       </td>
     </tr> 
     <tr>
       <td width="25%"></td> <!-- First, empty column -->
       <td>
        <table>
         <tr>
          <td> <!-- Top table for props, bottom for buttons -->
           <table class="details" width="50%">
            <xsl:for-each select="@bottomborder">
             <xsl:attribute name="border">
               <xsl:value-of select="."/>
             </xsl:attribute>
            </xsl:for-each>


           <xsl:apply-templates select="bebop:formWidget"/>
           <tr>
             <th>
               <xsl:value-of select="$cellPath/bebop:label[@id='editpropnamelabel']"/>
             </th>
             <td>
               <xsl:apply-templates select="$cellPath/bebop:formWidget[@id='editpropname']"/>
             </td>
           </tr>
           <tr>
             <th>
               <xsl:value-of select="$cellPath/bebop:label[@id='editpropdescriptionlabel']"/>
             </th>
             <td>
               <xsl:apply-templates select="$cellPath/bebop:textarea[@id='editpropdescription']"/>
             </td>
           </tr>
          </table> <!-- End of details table -->
         </td>
        </tr>
        <tr>
         <td>
          <table> <!-- this is the start of two cell button table -->
           <tr>
            <td>
             <xsl:apply-templates select="$cellPath/bebop:formWidget[@id='editproptextinput']"/>
            </td>
            <td>
             <xsl:apply-templates select="$cellPath/bebop:formWidget[@id='editproptextcancel']"/>
            </td>
           </tr>
          </table>
         </td>
        </tr>
       </table> <!-- end of middle column outer table -->  
       </td>
       <td width="25%"></td>
      </tr>
    </table>

 </xsl:template>

 <xsl:template match="bebop:gridPanel[@class='portalsitemaplinkpanel']"
               xmlns:bebop="http://www.arsdigita.com/bebop/1.0">
   <xsl:variable name="cellPath" select="bebop:panelRow/bebop:cell"/>
   <xsl:variable name="cellDeepPath" select="bebop:panelRow/bebop:cell/bebop:gridPanel/bebop:panelRow/bebop:cell"/>
   <table class="sitemaplinks" width="100%">
     <tr>
       <td width="25%" valign="top">
         <table width="100%" height="100%">  <!-- Create Panel -->
           <tr>
             <td class="heading_cell">
               <xsl:value-of select="$cellPath/bebop:label[@id='createpanelheader']"/>
             </td>
           </tr>
           <tr>
             <td>
               <xsl:apply-templates select="$cellPath/bebop:link[@class='createtoplevel']"/>
             </td>
           </tr>
           <xsl:if test="$cellPath/bebop:link[@class='createchild']">
           <tr>
             <td>
               <xsl:apply-templates select="$cellPath/bebop:link[@class='createchild']"/>
             </td>
           </tr>
           </xsl:if>
         </table>
       </td>
       <xsl:if test="$cellPath/bebop:gridPanel[@class='portalsitemaplinkpanelvmd']">
       <td width="25%" valign="top">
         <table width="100%" height="100%">  <!-- View Panel -->
           <tr>
             <td class="heading_cell">
               <xsl:value-of select="$cellDeepPath/bebop:label[@id='viewpanelheader']"/>
             </td>
           </tr>
           <tr>
             <td>
               <xsl:apply-templates select="$cellDeepPath/bebop:link[@class='portalvisitlink']"/>
             </td>
           </tr>
           <tr>
             <td>
               <xsl:apply-templates select="$cellDeepPath/bebop:link[@class='portaladminvisitlink']"/>
             </td>
           </tr>
         </table>
       </td>
       <td width="25%" valign="top">
         <table width="100%" height="100%">  <!-- Modify Panel -->
           <tr>
             <td class="heading_cell">
               <xsl:value-of select="$cellDeepPath/bebop:label[@id='modifypanelheader']"/>
             </td>
           </tr>
           <tr>
             <td>
               <xsl:apply-templates select="$cellDeepPath/bebop:link[@class='portalarchivelink']"/>
             </td>
           </tr>
         </table>
       </td>
       <td width="25%" valign="top">
         <table width="100%" height="100%">  <!-- Delete Panel -->
           <tr>
             <td class="heading_cell">
               <xsl:value-of select="$cellDeepPath/bebop:label[@id='deletepanelheader']"/>
             </td>
           </tr>
           <tr>
             <td>
               <xsl:apply-templates select="$cellDeepPath/bebop:link[@class='portaldeletelink']"/>
             </td>
           </tr>
         </table>
       </td>
      </xsl:if>
     </tr>
    </table>

 </xsl:template>

 <xsl:template match="bebop:gridPanel[@class='deleteform']"
               xmlns:bebop="http://www.arsdigita.com/bebop/1.0">
   <xsl:variable name="cellPath" select="bebop:panelRow/bebop:cell"/>
   <xsl:variable name="listPath" select="bebop:panelRow/bebop:cell/bebop:list"/>
           
   <xsl:apply-templates select="bebop:formWidget"/>
   <table class="sitemapdelete" width="100%">
    <tr>
     <td>
      <xsl:value-of select="$cellPath/bebop:label[@id='delete_instruction1']"/>
     </td>
    </tr>
    <tr>
     <td>
      <xsl:if test="count($listPath/bebop:cell) > 0">
       <table class="sitemapdeleteappstable">
         <tr class="deleteodd">
           <th><xsl:value-of select="$cellPath/bebop:label[@id='deletetableheader1']"/></th>
           <th><xsl:value-of select="$cellPath/bebop:label[@id='deletetableheader2']"/></th>
           <th><xsl:value-of select="$cellPath/bebop:label[@id='deletetableheader3']"/></th>
         </tr>
         <xsl:apply-templates select="$listPath[@id='deleteappslist']"/>
       </table>
      </xsl:if>
     </td>
    </tr>
    <tr>
      <td><xsl:apply-templates select="$cellPath/bebop:label[@id='delete_instruction3']"/></td>
    </tr>
    <tr>
      <td><xsl:apply-templates select="$cellPath/bebop:formWidget[@id='deleteformbutton']"/></td>
      <td><xsl:apply-templates select="$cellPath/bebop:formWidget[@id='deleteformcancelbutton']"/></td>
    </tr>
     
<!-- put in orig form stuff here -->
   </table>

 </xsl:template>

<xsl:template match="bebop:list[@id='deleteappslist']">
  <xsl:for-each select="bebop:cell">
    <xsl:choose>
      <xsl:when test="position() mod 2 = 1">
       <tr class="deleteeven">
        <td><xsl:apply-templates select="bebop:checkbox"/></td>
        <td><xsl:apply-templates select="bebop:label[@id='deleteappname']"/></td>
        <xsl:if test="bebop:label[@id='deleteapptype']">
       <td><xsl:apply-templates select="bebop:label[@id='deleteapptype']"/></td>
        </xsl:if>
       </tr>
      </xsl:when>
      <xsl:otherwise>
       <tr class="deleteodd">
        <td><xsl:apply-templates select="bebop:checkbox"/></td>
        <td><xsl:apply-templates select="bebop:label[@id='deleteappname']"/></td>
        <xsl:if test="bebop:label[@id='deleteapptype']">
          <td><xsl:apply-templates select="bebop:label[@id='deleteapptype']"/></td>
        </xsl:if>
       </tr>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:for-each>
</xsl:template>

<xsl:template match="bebop:link[@id='propedittheseprops']">

       <a href="{@href}"><img src="/assets/portalserver/edit.gif" height="16" width="16" border="0"/><xsl:value-of select="./bebop:label"/></a>
</xsl:template>

<xsl:template match="bebop:link[@class='createtoplevel']">

       <a href="{@href}"><img src="/assets/portalserver/action-add.gif" height="16" width="16" border="0"/><xsl:value-of select="./bebop:label"/></a>
</xsl:template>

<xsl:template match="bebop:link[@class='createchild']">

       <a href="{@href}"><img src="/assets/portalserver/action-add.gif" height="16" width="16" border="0"/><xsl:value-of select="./bebop:label"/></a>
</xsl:template>

<xsl:template match="bebop:link[@class='portalvisitlink']">

   <a href="#" onclick="window.open('{@href}', 'Visit', 'resizable=1', 'width=400, height=500,scrollbars=1')"><img src="/assets/portalserver/move.gif" height="16" width="16" border="0"/><xsl:value-of select="./bebop:label"/></a>
</xsl:template>

<xsl:template match="bebop:link[@class='portaladminvisitlink']">

   <a href="#" onclick="window.open('{@href}', 'Visit', 'resizable=1', 'width=400, height=500,scrollbars=1')"><img src="/assets/portalserver/move.gif" height="16" width="16" border="0"/><xsl:value-of select="./bebop:label"/></a>
</xsl:template>

<xsl:template match="bebop:link[@class='portalarchivelink']">

       <a href="{@href}"><img src="/assets/portalserver/archive.gif" height="16" width="16" border="0"/><xsl:value-of select="./bebop:label"/></a>
</xsl:template>


<xsl:template match="bebop:link[@class='portaldeletelink']">

       <a href="{@href}"><img src="/assets/portalserver/delete.gif" height="12" width="12" border="0"/><xsl:value-of select="./bebop:label"/></a>
</xsl:template>

</xsl:stylesheet>
