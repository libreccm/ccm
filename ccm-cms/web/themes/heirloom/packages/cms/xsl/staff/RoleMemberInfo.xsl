<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet  xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
                 xmlns:cms="http://www.arsdigita.com/cms/1.0"
               xmlns:bebop="http://www.arsdigita.com/bebop/1.0"
   exclude-result-prefixes="cms"
                   version="1.0">
  
  <xsl:output method="html"/>


  <!-- Role member info: name, email, roles -->
  <xsl:template match="cms:roleMemberInfo">
  <table width="100%" cellspacing="0" cellpadding="0" border="0">
    <tr>
      <td>
        <table width="100%" cellspacing="0" cellpadding="2" border="0">
          <tr>
            <th class="split_pane_header">
              Role Member Information
            </th>
          </tr>
        </table>
      </td>
    </tr> 
    <tr>
      <td bgcolor="#ffffff">
        <table cellspacing="0" cellpadding="0" border="0">
          <tr>
            <td height="1"></td>
          </tr>
        </table>
      </td>
    </tr>
    <tr>
      <td class="split_pane_right_body">
        <table border="0" cellspacing="0" cellpadding="0">
          <tr>
            <td valign="top" class="form_label">
              Name:
            </td>
            <td valign="top" class="form_value">
              <xsl:value-of select="@name"/>
            </td>
          </tr>
          <tr>
            <td colspan="2">
              <xsl:text>&#160;</xsl:text>
            </td>
          </tr>
          <tr>
            <td valign="top" class="form_label">
              Email address:
            </td>
            <td valign="top" class="form_value">
              <xsl:value-of select="@email"/>
            </td>
          </tr>
          <tr>
            <td colspan="2">
              <xsl:text>&#160;</xsl:text>
            </td>
          </tr>
          <tr>
            <td valign="top" colspan="2" class="form_value">
              This <xsl:value-of select="@partyType"/> has the following roles:
            </td>
          </tr>
          <tr>
            <td colspan="2">
              <xsl:text>&#160;</xsl:text>
            </td>
          </tr>
          <tr>
            <td>
              <xsl:text>&#160;</xsl:text>
            </td>
            <td valign="top" class="form_value">
              <xsl:apply-templates select="bebop:list[@id='party_roles_list']"/>
            </td>
          </tr>
        </table>
      </td>
    </tr>
    <tr>
      <td>
        <xsl:apply-templates select="bebop:link[@id='back_link']"/>
      </td>
    </tr>
  </table>
  </xsl:template>



  <!-- A list or roles to which this party belongs -->
  <xsl:template match="bebop:list[@id='party_roles_list']">
    <xsl:for-each select="bebop:cell">
      <xsl:apply-templates/><br/>
    </xsl:for-each>
  </xsl:template>

</xsl:stylesheet>
