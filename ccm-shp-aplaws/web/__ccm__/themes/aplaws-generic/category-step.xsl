<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet 
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:bebop="http://www.arsdigita.com/bebop/1.0"
  xmlns:cms="http://www.arsdigita.com/cms/1.0"
  xmlns:ui="http://www.arsdigita.com/ui/1.0"
  xmlns:aplaws="http://www.arsdigita.com/aplaws/1.0"
  version="1.0" exclude-result-prefixes="bebop cms ui aplaws xsl">

  <xsl:import href="../../../../ROOT/__ccm__/static/cms/admin/category-step/category-step.xsl"/>
  
  <xsl:param name="theme-prefix" />

  <xsl:template match="cms:emptyPage[@title='childCategories']">
    <xsl:choose>
      <xsl:when test="cms:category/@order='sortKey'">
    <xsl:apply-templates select="cms:category/cms:category" mode="cms:javascriptCat">
      <xsl:with-param name="expand" select="'none'"/>
          <xsl:sort data-type="number" select="@sortKey"/>
    </xsl:apply-templates>
      </xsl:when>
      <xsl:otherwise>
        <xsl:apply-templates select="cms:category/cms:category" mode="cms:javascriptCat">
          <xsl:sort data-type="text" select="@name"/>
          <xsl:with-param name="expand" select="'none'"/>
        </xsl:apply-templates>
      </xsl:otherwise>
    </xsl:choose>
    
  </xsl:template>

  <xsl:template match="cms:emptyPage[@title='autoCategories']">
    <xsl:choose>
      <xsl:when test="cms:category/@order='sortKey'">
        <xsl:apply-templates select="cms:category" mode="cms:javascriptCat" >
        <xsl:with-param name="expand" select="'none'"/>
          <xsl:sort data-type="number" select="@sortKey"/>
        </xsl:apply-templates>
      </xsl:when>
      <xsl:otherwise>
        <xsl:apply-templates select="cms:category" mode="cms:javascriptCat" >
          <xsl:sort data-type="text" select="@name"/>
          <xsl:with-param name="expand" select="'none'"/>
        </xsl:apply-templates>
      </xsl:otherwise>
    </xsl:choose>
    
  </xsl:template>

</xsl:stylesheet>
