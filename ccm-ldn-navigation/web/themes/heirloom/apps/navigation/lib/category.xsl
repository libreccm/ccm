<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                 xmlns:nav="http://ccm.redhat.com/london/navigation"
                   version="1.0">


  <xsl:template match="nav:categoryPath">
    <h3>Path</h3>
    <ul class="navCategoryPath">
      <xsl:for-each select="nav:category">
        <li><xsl:apply-templates select="." mode="item"/></li>
        <xsl:if test="position() != last()">
          <li><span>--&gt;</span></li>
        </xsl:if>
      </xsl:for-each>
    </ul>
  </xsl:template>

  <xsl:template match="nav:categoryMenu">
    <h3>Menu</h3>
    <ul class="navCategoryMenu">
      <xsl:apply-templates select="nav:category" mode="list"/>
    </ul>
  </xsl:template>

  <xsl:template match="nav:categoryHierarchy">
    <h3>Hierarchy</h3>
    <!-- XXX items too -->
    <ul class="navCategoryHierarchy">
      <xsl:apply-templates select="nav:category" mode="list"/>
    </ul>
  </xsl:template>

  <xsl:template match="nav:categoryChildren">
    <h3>Children</h3>
    <ul class="navCategoryChildren">
      <xsl:apply-templates select="nav:category" mode="list"/>
    </ul>
  </xsl:template>

  <xsl:template match="nav:categorySiblings">
    <h3>Siblings</h3>
    <ul class="navCategorySiblings">
      <xsl:apply-templates select="nav:category" mode="list"/>
    </ul>
  </xsl:template>

  <xsl:template match="nav:categoryTopLevel">
    <h3>Top Level</h3>
    <ul class="navCategoryTopLevel">
      <xsl:apply-templates select="nav:category" mode="list"/>
    </ul>
  </xsl:template>

  <xsl:template match="nav:categoryRoot">
    <h3>Root</h3>
    <ul class="navCategoryRoot">
      <xsl:apply-templates select="nav:category" mode="item"/>
    </ul>
  </xsl:template>


  <xsl:template match="nav:category">
    <xsl:apply-templates select="." mode="item"/>
  </xsl:template>

  <xsl:template match="nav:category" mode="item">
    <a class="navCategory" href="{@url}" title="{@description}"><xsl:value-of select="@title"/></a>
  </xsl:template>

  <xsl:template match="nav:category" mode="list">
    <li>
      <xsl:apply-templates select="." mode="item"/>
      <xsl:if test="nav:category">
        <ul>
          <xsl:apply-templates select="nav:category" mode="list"/>
        </ul>
      </xsl:if>
    </li>
  </xsl:template>

</xsl:stylesheet>
