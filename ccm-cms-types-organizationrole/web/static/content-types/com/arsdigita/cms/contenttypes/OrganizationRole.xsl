<!DOCTYPE stylesheet [
<!ENTITY nbsp   "&#160;" ><!-- no-break space = non-breaking space, U+00A0 ISOnum -->
]>
<xsl:stylesheet 
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
  xmlns:cms="http://www.arsdigita.com/cms/1.0"
  version="1.0">
  
  <xsl:template match="cms:item[objectType='com.arsdigita.cms.contenttypes.OrganizationRole']"
    mode="cms:CT_graphics"
    name="cms:CT_graphics_com_arsdigita_cms_contenttypes_OrganizationRole">
    <xsl:if test="./rolename">
      <p>
        <xsl:value-of select="./rolename"/>
      </p>
    </xsl:if>
  </xsl:template>

  <xsl:template match="cms:item[objectType='com.arsdigita.cms.contenttypes.OrganizationRole']"
    mode="cms:CT_text"
    name="cms:CT_text_com_arsdigita_cms_contenttypes_OrganizationRole">
      <xsl:if test="./rolename">
      <p>
        <xsl:value-of select="./rolename"/>
      </p>      
    </xsl:if>
  </xsl:template>
   
</xsl:stylesheet>