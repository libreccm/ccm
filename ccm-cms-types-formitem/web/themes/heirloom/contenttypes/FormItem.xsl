<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
                xmlns:cms="http://www.arsdigita.com/cms/1.0"
                xmlns:bebop="http://www.arsdigita.com/bebop/1.0"
                exclude-result-prefixes="cms bebop">

<!--  <xsl:import href="../../../../../../packages/formbuilder/xsl/formbuilder.xsl"/> -->
  <xsl:import href="../packages/formbuilder/xsl/formbuilder.xsl"/>

  <xsl:template match="cms:item[objectType='com.arsdigita.cms.formbuilder.FormItem']" 
                 mode="cms:CT_text"
                 name="cms:CT_text_com_arsdigita_cms_formbuilder_FormItem">
    <xsl:apply-templates select="." mode="cms:CT_graphics"/>
    <xsl:apply-templates select="form"/>
  </xsl:template>

  <xsl:template match="cms:item[objectType='com.arsdigita.cms.formbuilder.FormItem']" 
                 mode="cms:CT_graphics"
                 name="cms:CT_graphics_com_arsdigita_cms_formbuilder_FormItem">
    <h1><xsl:value-of select="title"/></h1>
    <div class="description">
      <xsl:value-of select="form/description"/>
    </div>
    <div>
      <xsl:if test="css">
        <xsl:attribute name="class">
          <xsl:value-of select="css"/>
        </xsl:attribute>
      </xsl:if>

      <xsl:apply-templates select="form"/>
    </div>
  </xsl:template>

</xsl:stylesheet>
