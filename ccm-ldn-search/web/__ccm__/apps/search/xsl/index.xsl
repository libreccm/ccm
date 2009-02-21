<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:search="http://www.arsdigita.com/search/1.0" 
  xmlns:bebop="http://www.arsdigita.com/bebop/1.0"
  version="1.0">

  <!-- IMPORT DEFINITIONS ccm-ldn-search installed as separate web application
  <xsl:import href="../../../../../ROOT/packages/bebop/xsl/bebop.xsl"/>
  <xsl:import href="../../../../../ROOT/packages/ui/xsl/ui.xsl"/>
  <xsl:import href="../../../../../ROOT/packages/search/xsl/search.xsl"/>
  <xsl:import href="../../../../../ROOT/__ccm__/static/cms/admin/search/search.xsl"/>
  -->

  <!-- IMPORT DEFINITIONS ccm-ldn-shortcuts installed into the main CCM webapp
  -->
  <xsl:import href="../../../../packages/bebop/xsl/bebop.xsl"/>
  <xsl:import href="../../../../packages/ui/xsl/ui.xsl"/>
  <xsl:import href="../../../../packages/search/xsl/search.xsl"/>
  <xsl:import href="../../../../__ccm__/static/cms/admin/search/search.xsl"/>


  <xsl:output method="html"/>

  <xsl:template match="search:filter[@type='host']">
    <th>
      <xsl:text>Sites:</xsl:text>
    </th>
    <td>
      <select size="10" name="{@param}" multiple="multiple">
        <xsl:for-each select="search:remoteHost">
          <option value="{@oid}">
            <xsl:if test="@isSelected">
              <xsl:attribute name="selected">selected</xsl:attribute>
            </xsl:if>
            <xsl:value-of select="@title"/>
          </option>
        </xsl:for-each>
      </select>
    </td>
  </xsl:template>


</xsl:stylesheet>
