<?xml version="1.0"?>


<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
                xmlns:cms="http://www.arsdigita.com/cms/1.0"
                xmlns:bebop="http://www.arsdigita.com/bebop/1.0"
                exclude-result-prefixes="cms">

  <xsl:import href="types/default.xsl"/>
  <xsl:import href="types/udct.xsl"/>
  <xsl:import href="types/FileAttachments.xsl"/>
  <xsl:import href="types/Link.xsl"/>

  <xsl:import href="/__ccm__/servlet/content-type/index.xsl"/>

  <!-- The styling rules for the live view of an UserDefinedContentItem -->
  <xsl:template match="cms:item[@javaClass='com.arsdigita.cms.UserDefinedContentItem']">
    <xsl:call-template name="UDItemPublic"/>
  </xsl:template>

  <!-- The styling rules for the Summary pane for an UserDefinedContentItem -->
  <xsl:template match="cms:item[@javaClass='com.arsdigita.cms.UserDefinedContentItem' and @useContext='itemAdminSummary']">
    <xsl:call-template name="UDItemSummary"/>
  </xsl:template>
  
  <!-- Default match does full graphical view -->
  <xsl:template match="cms:item">
    <xsl:apply-templates select="." mode="cms:CT_graphics"/>

    <xsl:if test="count(fileAttachments) > 0">
      <h3>Related files</h3>
      <xsl:call-template name="cms:fileAttachments"/>
    </xsl:if>

    <xsl:call-template name="cms:links"/>
  </xsl:template>

  <!-- Summary page does plain text view -->
  <xsl:template match="cms:item[@useContext='itemAdminSummary']">
    <xsl:apply-templates select="." mode="cms:CT_text"/>

    <xsl:if test="count(fileAttachments) > 0">
      <h3>Related files</h3>
      <xsl:call-template name="cms:fileAttachments_text"/>
    </xsl:if>
    <xsl:call-template name="cms:links_text"/>
  </xsl:template>

</xsl:stylesheet>
