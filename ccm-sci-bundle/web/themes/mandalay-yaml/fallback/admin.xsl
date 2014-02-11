<?xml version="1.0" encoding="utf-8"?>
<!DOCTYPE stylesheet [<!ENTITY nbsp '<xsl:text disable-output-escaping="yes">&amp;</xsl:text>nbsp;'>]>

<!-- 
    Copyright: 2006, 2007, 2008 Sören Bernstein
  
    This file is part of Mandalay.

    Mandalay is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 2 of the License, or
    (at your option) any later version.

    Mandalay is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with Mandalay.  If not, see <http://www.gnu.org/licenses/>.
-->

<xsl:stylesheet version="1.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:admin="http://www.arsdigita.com/admin-ui/1.0"
                xmlns:bebop="http://www.arsdigita.com/bebop/1.0"
                exclude-result-prefixes="admin">

<xsl:import href="../../../../../ROOT/packages/acs-admin/xsl/split-panel.xsl"/>

<!-- Importiert eine Menge anderer XSL-Dateien. Ich bin mir nicht sicher, wofür die gebraucht werden. Daher lasse ich sie erstmal weg.
<xsl:import href="../../../../../ROOT/packages/content-section/xsl/cms.xsl"/>
-->
<!-- Anzeige der Bilder bei AddImage -->
<xsl:import href="../../../../../ROOT/packages/content-section/xsl/CaptionedImage.xsl"/>


  <!-- this rule matches one user -->
  <xsl:template match="admin:userInfo">
    
    <!-- Feature #166221 capitalize form labels -->
    <ul>
      <li> Name: <xsl:value-of select="@name"/> </li>
      <li> Email: 
	<table>
	  <xsl:apply-templates select="admin:email" />
	</table>
      </li>
      <li> Screen Name:  <xsl:value-of select="@screenName"/> </li>
      <li> URL: <a href="{@URI}"><xsl:value-of select="@URI"/> </a> </li>
      <li> User ID:  <xsl:value-of select="@id"/> </li>
      <li> Member State: <xsl:value-of select="@memberState"/> 
      </li>
    </ul>
    
  </xsl:template>
  
  <!-- used to format the list of actions available for one user -->
  <xsl:template match="admin:userActions">
    
    <h3>Adminstrative Actions</h3>
    
    <ul>
      <li><a href="password-update?user_id={@id}">Update password</a></li>
      <li><a href="login?user_id={@id}">Become this user</a></li>
    </ul>
    
  </xsl:template>
  
  <!-- used to format the header of the password update page -->
  <xsl:template match="bebop:label[@class='UserPasswordHeader']">
    For <xsl:value-of select="text()"/>
    <hr />
  </xsl:template>
  
  <!-- usage note displayed on the password-update page -->
  <xsl:template match="admin:PasswordNote">
    <h4>Note</h4>
    <p>If this user does not currently have an authentication record,
      one will be created when you submit this form and the account
      will be enabled for login to the system.</p>
  </xsl:template>

  <!-- used to format primary email for display -->
  <xsl:template match="admin:email[@primary='t']">
    <tr>
      <td>
        <a href="mailto:{@address}"><xsl:value-of select="@address"/></a>
      </td>
      <td>
        (primary)
      </td>
    </tr>
  </xsl:template>
  
  <!-- used to format non-primary email for display -->
  <xsl:template match="admin:email">
    <tr>
      <td>
        <a href="mailto:{@address}"><xsl:value-of select="@address"/></a>
      </td>
      <td>
      </td>
    </tr>
  </xsl:template>


<!-- Access Denied Page -->
<xsl:template match="bebop:label[@class='AccessDenied']">
  <hr />
  <p>You don't have permission to perform the requested action.</p>
  <hr />
</xsl:template>

<!-- Display search error message -->
<xsl:template match="bebop:list[@class='SearchResultList'][count(bebop:cell)=0]">
   <p><font color="red">Your search returned no results.</font></p>
</xsl:template>

<!-- Display search error message -->
<xsl:template match="bebop:list[@class='SearchResultList'][count(bebop:cell)>0]">
    <p>Results matching your query:</p>
    <ul>
      <xsl:for-each select="bebop:cell">
        <li><xsl:apply-templates/></li>
      </xsl:for-each>
    </ul>
</xsl:template>

<!-- used to format the basic information and actions available for one group -->
<xsl:template match="admin:groupInfo">

<ul>
  <li> Group Name: <xsl:value-of select="@name"/> </li>
  <li> Primary Email: <xsl:value-of select="@email"/> </li>
</ul>

</xsl:template>

<xsl:template match="bebop:list[@class='UserGroupsResultList'][count(bebop:cell)=0]">
   <p><font color="red">None</font></p>
</xsl:template>
  
<xsl:template match="bebop:link[@class='deleteLink']">
  <xsl:text>&#160;</xsl:text>
  (<a href="{@href}" onclick="{@onclick}" class="action_link">
    <xsl:apply-templates/>
  </a>)
</xsl:template>

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
 
