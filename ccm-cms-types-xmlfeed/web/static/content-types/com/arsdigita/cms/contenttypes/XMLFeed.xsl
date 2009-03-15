<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet 
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:cms="http://www.arsdigita.com/cms/1.0"
  xmlns:bebop="http://www.arsdigita.com/bebop/1.0"
  version="1.0">

  <xsl:template match="cms:item[objectType='com.arsdigita.cms.contenttypes.xmlfeed.XMLFeed']" mode="cms:CT_graphics"
    name="cms:CT_graphics_com_arsdigita_cms_contenttypes_xmlfeed_XMLFeed">
    <xsl:call-template name="cms:CT_graphics_com_arsdigita_cms_formbuilder_FormItem"/>
    <xsl:apply-templates select="bebop:label"/>
  </xsl:template>

  <xsl:template match="cms:item[objectType='com.arsdigita.cms.contenttypes.xmlfeed.XMLFeed']" mode="cms:CT_text"
    name="cms:CT_text_com_arsdigita_cms_contenttypes_xmlfeed_XMLFeed">
    <xsl:call-template name="cms:CT_text_com_arsdigita_cms_formbuilder_FormItem"/>
    <xsl:apply-templates select="bebop:label"/>
  </xsl:template>
</xsl:stylesheet>
