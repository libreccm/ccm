<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet 
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:forum="http://www.arsdigita.com/forum/1.0"
  xmlns:bebop="http://www.arsdigita.com/bebop/1.0"
  xmlns:portlet="http://www.uk.arsdigita.com/portlet/1.0"
  version="1.0">

  <xsl:output method="html"/>

  <!-- default template -->
  <xsl:template match="bebop:portlet[@bebop:classname='com.arsdigita.cms.webpage.ui.WebpagePortletRenderer']">
    <table width="100%" border="0" cellspacing="2" cellpadding="0">
      <tr>
        <td bgcolor="#cccccc">
          <table width="100%" cellspacing="2" cellpadding="4" border="0">
            <tr>
              <th class="split_pane_header" width="100%">
                <xsl:value-of select="@title"/>
              </th>
            </tr>
            <tr>
              <td class="table_cell" bgcolor="#ffffff" colspan="20">
                <xsl:apply-templates select="*"/>
              </td>
            </tr>
          </table>
        </td>
      </tr>
    </table>
  </xsl:template>

</xsl:stylesheet>
