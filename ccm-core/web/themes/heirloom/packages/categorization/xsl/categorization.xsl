<?xml version="1.0"?>
<xsl:stylesheet  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"  
      xmlns:categorization="http://www.arsdigita.com/categorization/1.0"
               xmlns:bebop="http://www.arsdigita.com/bebop/1.0"
                   version="1.0">

<xsl:template match="categorization:objectCategories">
  <b><xsl:value-of select="bebop:name"/></b>
  <blockquote>
    <xsl:apply-templates select="categorization:qualifiedCategoryName" />
  </blockquote>
</xsl:template>

<xsl:template match="categorization:qualifiedCategoryName">
   <xsl:value-of select="@qualifiedName"/><br />
</xsl:template>

</xsl:stylesheet>