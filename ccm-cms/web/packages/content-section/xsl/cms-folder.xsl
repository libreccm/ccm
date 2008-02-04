<?xml version="1.0"?>

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
	xmlns:cms="http://www.arsdigita.com/cms/1.0"
	xmlns:bebop="http://www.arsdigita.com/bebop/1.0"
	exclude-result-prefixes="cms">


  <!-- Folder information -->
  <xsl:template match="cms:folderInfo">
    <table cellspacing="0" cellpadding="0" border="0" width="100%">
      <tr>
        <td>
          <table width="100%" cellspacing="0" cellpadding="2" border="0">
            <tr>
              <th class="split_pane_header">
                Folder Properties
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
                Label:
              </td>
              <td valign="top" class="form_value">
                <xsl:value-of select="@label"/>
              </td>
            </tr>
            <tr>
              <td colspan="2">
                <xsl:text>&#160;</xsl:text>
              </td>
            </tr>
          </table>

          <p>
            <xsl:apply-templates select="bebop:link[@id='edit_link']"/>
          </p>

        </td>
      </tr>
    </table>
  </xsl:template>


</xsl:stylesheet>
