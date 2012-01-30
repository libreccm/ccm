<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                  version="1.0">

  <xsl:import href="application-directory-portlet.xsl"/>
  <xsl:import href="content-directory-portlet.xsl"/>
  <xsl:import href="flash-portlet.xsl"/>
  <xsl:import href="freeform-html-portlet.xsl"/>
  <xsl:import href="login-portlet.xsl"/>
  <!-- MyWorkspacesPortlet - xsl file missing   -->
  <xsl:import href="rss-feed-portlet.xsl"/>
  
  <!-- time of day in separate portlet collection package -->
<!--   <xsl:import href="time-of-day-portlet.xsl"/>  -->
  <!-- WorkspaceNavigatorPortlet - xsl file missing   -->
  <!-- WorkspaceSummaryPortlet - xsl file missing   -->

<!-- stuff from/for portlets not part of the portal package
     (ccm-cms, ldn-rss)
  <xsl:import href="content-sections-portlet.xsl"/>
  <xsl:import href="content-item-portlet.xsl"/>
  <xsl:import href="tasklist-portlet.xsl"/>
  <xsl:import href="workspace-directory-portlet.xsl"/>
-->
  <!-- We want no dependency from forum package in the portal package -->
  <!-- import statement, if forum is installed in a separate webapp context
  <xsl:import href="../../../../ccm-forum/packages/forum/xsl/recent-postings-portlet.xsl"/>
  -->
  <!-- import statement, if forum is installed in the main CCM webapp context
  <xsl:import href="../../../../packages/forum/xsl/recent-postings-portlet.xsl"/>
  -->
  <xsl:import href="/themes/servlet/portlet-type/index.xsl"/>
</xsl:stylesheet>
