<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:cms="http://www.arsdigita.com/cms/1.0"
                version="1.0">

  <!-- Format a whole userdefiendcontentitem  -->
  <xsl:template name="UDItemPublic">
    <xsl:param name="title-bgcolor">#f5f5cc</xsl:param>
    <xsl:param name="image-bgcolor">#ffffff</xsl:param>
    <xsl:param name="text-bgcolor">#ffffff</xsl:param>

    <table cellspacing="0" cellpadding="0" border="0" width="100%">
      <tr>
        <td align="center" valign="top">
          <table cellspacing="0" cellpadding="4" border="0" width="65%">
            <tr bgcolor="{$title-bgcolor}">
              <th>
                <font face="Helvetica,Arial" size="4">
                  <xsl:value-of select="@title"/>
                </font>
              </th>
            </tr>
            <tr>
              <td height="4"/>
            </tr>
          </table>
        </td>
      </tr>
    </table>
  </xsl:template>

  <!-- format a whole userdefinedcontentitem for the item summary pane -->
  <xsl:template name="UDItemSummary">

    <table cellspacing="0" cellpadding="4" border="0" width="100%">
      <tr>
        <td>
          <table width="100%" cellspacing="0" cellpadding="2" border="0">
            <tr>
              <th class="split_pane_header">
                <xsl:value-of select="@type"/> - Basic Properties
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
          <table cellspacing="0" cellpadding="0" border="0">
            <tr>
              <td valign="top" nowrap="nowrap" class="form_label">
                Name:
                <xsl:text>&#160;</xsl:text>
                <xsl:text>&#160;</xsl:text>
                <xsl:text>&#160;</xsl:text>
              </td>
              <td valign="top" class="form_value">
                <xsl:value-of select="@name"/>
              </td>
            </tr>
            <tr>
              <td colspan="2"><xsl:text>&#160;</xsl:text></td>
            </tr>
            <tr>
              <td valign="top" nowrap="nowrap" class="form_label">
                Title:
                <xsl:text>&#160;</xsl:text>
                <xsl:text>&#160;</xsl:text>
                <xsl:text>&#160;</xsl:text>
              </td>
              <td valign="top" class="form_value">
                <xsl:value-of select="@title"/>
              </td>
            </tr>
        <xsl:for-each select="cms:UDItemAttribute">
            <tr>
              <td colspan="2"><xsl:text>&#160;</xsl:text></td>
            </tr> 
            <tr>
              <td valign="top" nowrap="nowrap" class="form_label">
                <xsl:value-of select="@UDItemAttrName"/>:
                <xsl:text>&#160;</xsl:text>
                <xsl:text>&#160;</xsl:text>
                <xsl:text>&#160;</xsl:text>
              </td>
              <td valign="top" class="form_value">
                <xsl:value-of select="@UDItemAttrValue"/>
              </td>
            </tr>
        </xsl:for-each>
            <tr>
              <td colspan="2"><xsl:text>&#160;</xsl:text></td>
            </tr>
          </table>
        </td>
      </tr>
    </table>
  </xsl:template>

  <xsl:template match="bebop:link[@id='previewInTable']"
    xmlns:bebop="http://www.arsdigita.com/bebop/1.0">
    <a target="preview">
      <xsl:for-each select="@*">
        <xsl:attribute name="{name()}">
          <xsl:value-of select="."/>
        </xsl:attribute>
      </xsl:for-each>
      <xsl:apply-templates/>
    </a>
  </xsl:template>


</xsl:stylesheet>
