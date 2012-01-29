<!DOCTYPE stylesheet [
<!ENTITY nbsp   "&#160;" ><!-- no-break space = non-breaking space, U+00A0 ISOnum -->
]>

<xsl:stylesheet 
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
  xmlns:cms="http://www.arsdigita.com/cms/1.0"
  version="1.0">

  <xsl:template match="cms:item[objectType='com.arsdigita.cms.contenttypes.Member']"
		mode="cms:CT_graphics"
		name="cms:CT_graphics_com_arsdigita_cms_contenttypes_Member">
    <!-- <xsl:if test="./surname and ./givenName"> -->
      <p>
        <xsl:value-of select="./titlepre"/>&nbsp;
        <xsl:value-of select="./givenname"/>&nbsp;
        <xsl:value-of select="./surname"/>&nbsp;
        <xsl:value-of select="./titlepost"/>
      </p>      
      <!-- </xsl:if> -->
  </xsl:template>

  <xsl:template match="cms:item[objectType='com.arsdigita.cms.contenttypes.Member']"
		mode="cms:CT_text"
		name="cms:CT_text_com_arsdigita_cms_contenttypes_Member">
      <p>
        <xsl:value-of select="./titlepre"/>
        <xsl:value-of select="./givenname"/>
        <xsl:value-of select="./surname"/>
        <xsl:value-of select="./titlepost"/>
      </p>      
    
  </xsl:template>

</xsl:stylesheet>