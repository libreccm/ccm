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

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:bebop="http://www.arsdigita.com/bebop/1.0"
  xmlns:terms="http://xmlns.redhat.com/london/terms/1.0"
  version="1.0">

  <xsl:import href="../../../../../ROOT/packages/ui/xsl/ui.xsl"/>

  <xsl:import href="../../../../../../ccm-ldn-terms/__ccm__/apps/terms/lib/domain-listing.xsl"/>
  <xsl:import href="../../../../../../ccm-ldn-terms/__ccm__/apps/terms/lib/domain-panel.xsl"/>
  <xsl:import href="../../../../../../ccm-ldn-terms/__ccm__/apps/terms/lib/domain-form.xsl"/>
  <xsl:import href="../../../../../../ccm-ldn-terms/__ccm__/apps/terms/lib/domain-details.xsl"/>
  <xsl:import href="../../../../../../ccm-ldn-terms/__ccm__/apps/terms/lib/domain-usage.xsl"/>

  <xsl:import href="../../../../../../ccm-ldn-terms/__ccm__/apps/terms/lib/term-listing.xsl"/>
  <xsl:import href="../../../../../../ccm-ldn-terms/__ccm__/apps/terms/lib/term-panel.xsl"/>
  <xsl:import href="../../../../../../ccm-ldn-terms/__ccm__/apps/terms/lib/term-form.xsl"/>
  <xsl:import href="../../../../../../ccm-ldn-terms/__ccm__/apps/terms/lib/term-details.xsl"/>
  <xsl:import href="../../../../../../ccm-ldn-terms/__ccm__/apps/terms/lib/term-picker.xsl"/>
  <xsl:import href="../../../../../../ccm-ldn-terms/__ccm__/apps/terms/lib/term-name-search.xsl"/>

  <xsl:param name="contextPath"/>

  <xsl:template name="bebop:pageCSS">
    <xsl:call-template name="bebop:pageCSSMain"/>
    <xsl:call-template name="terms:pageCSSMain"/>
  </xsl:template>

  <xsl:template name="terms:pageCSSMain">
    <link href="{$contextPath}/__ccm__/apps/terms/xsl/index.css" rel="stylesheet" type="text/css"/>
  </xsl:template>
</xsl:stylesheet>
 
