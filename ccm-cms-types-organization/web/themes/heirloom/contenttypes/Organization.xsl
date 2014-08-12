<!DOCTYPE stylesheet [
<!ENTITY nbsp   "&#160;" ><!-- no-break space = non-breaking space, U+00A0 ISOnum -->
]>

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
                xmlns:cms="http://www.arsdigita.com/cms/1.0"
                version="1.0">

  <xsl:template match="cms:item[objectType='com.arsdigita.cms.contenttypes.ldn.Organization']" mode="cms:CT_graphics"
    name="cms:CT_graphics_com_arsdigita_cms_contenttypes_ldn_Organization">
    <p><xsl:value-of select="title"/></p>
    <p>Contact: <xsl:value-of select="contact"/></p>
    <p><a><xsl:attribute name="href"><xsl:value-of select="link"/></xsl:attribute><xsl:value-of select="link"/></a></p>
  </xsl:template>

  <xsl:template match="cms:item[objectType='com.arsdigita.cms.contenttypes.ldn.Organization']" mode="cms:CT_text"
    name="cms:CT_text_com_arsdigita_cms_contenttypes_ldn_Organization">
    <span class="text"><xsl:value-of select="title"/></span><br/>
    <span class="text"><xsl:value-of select="contact"/></span><br/>
    <span class="text"><a><xsl:attribute name="href"><xsl:value-of select="link"/></xsl:attribute><xsl:value-of select="link"/></a></span><br/>
    <span class="text"><xsl:value-of disable-output-escaping="yes" select="./answer"/></span><br/>
  </xsl:template>

</xsl:stylesheet>
