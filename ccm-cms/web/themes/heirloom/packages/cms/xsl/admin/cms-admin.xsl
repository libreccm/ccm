<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
                  version="1.0">
                      
  <xsl:param name="context-prefix"/>
  <xsl:param name="dispatcher-prefix"/>
  <xsl:variable name="legacy-asset-prefix" select="//@assets"/>

  <xsl:output method="html" indent="yes"/>

  <xsl:include href="page/page.xsl"/>
  <xsl:include href="context-bar/context-bar.xsl"/>
  <xsl:include href="tabbed-pane/tabbed-pane.xsl"/>
  <xsl:include href="layout-panel/layout-panel.xsl"/>
  <xsl:include href="segmented-panel/segmented-panel.xsl"/>
  <xsl:include href="action-link/action-link.xsl"/>
  <xsl:include href="table/table.xsl"/>
  <xsl:include href="preformatted-label/preformatted-label.xsl"/>
  <xsl:include href="action-group/action-group.xsl"/>
  <xsl:include href="property-list/property-list.xsl"/>
  <xsl:include href="section/section.xsl"/>
  <xsl:include href="tree/tree.xsl"/>
  <xsl:include href="category-step/category-step.xsl"/>
  
</xsl:stylesheet>
