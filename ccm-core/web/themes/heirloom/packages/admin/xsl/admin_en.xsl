<?xml version="1.0"?>

<xsl:stylesheet version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:admin="http://www.arsdigita.com/admin-ui/1.0"
  xmlns:bebop="http://www.arsdigita.com/bebop/1.0"
  exclude-result-prefixes="admin">

<xsl:import href="admin.xsl"/>
 
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

</xsl:stylesheet>


