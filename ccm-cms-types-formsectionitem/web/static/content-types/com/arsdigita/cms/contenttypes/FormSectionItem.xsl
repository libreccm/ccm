<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
                xmlns:cms="http://www.arsdigita.com/cms/1.0"
                xmlns:bebop="http://www.arsdigita.com/bebop/1.0"
                exclude-result-prefixes="cms">

  <xsl:template match="cms:item[objectType='com.arsdigita.cms.formbuilder.FormSectionItem']" mode="cms:CT_text"
    name="cms:CT_text_com_arsdigita_cms_formbuilder_FormSectionItem">
    <xsl:apply-templates select="." mode="cms:CT_graphics"/>
  </xsl:template>

  <xsl:template match="cms:item[objectType='com.arsdigita.cms.formbuilder.FormSectionItem']" mode="cms:CT_graphics"
    name="cms:CT_graphics_com_arsdigita_cms_formbuilder_FormSectionItem">
    <h1><xsl:value-of select="title"/></h1>
    <div class="description">
      <xsl:value-of select="form/description"/>
    </div>

    <xsl:apply-templates select="formSection"/>
  </xsl:template>
</xsl:stylesheet>
