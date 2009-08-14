<!DOCTYPE stylesheet [
<!ENTITY nbsp   "&#160;" ><!-- no-break space = non-breaking space, U+00A0 ISOnum -->
]>

<xsl:stylesheet 
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:cms="http://www.arsdigita.com/cms/1.0"
  version="1.0">

  <xsl:template match="cms:item[objectType='com.arsdigita.cms.contenttypes.ResearchNetwork']"
    mode="cms:CT_graphics"
    name="cms:CT_graphics_com_arsdigita_cms_contenttypes_ResearchNetwork">

    <h2><xsl:value-of select="./researchNetworkTitle"/></h2>

    <p>
      <xsl:value-of select="./researchNetworkDirection"/>
    </p>

    <p>
      <xsl:value-of select="./researchNetworkCoordination"/>
    </p>

    <p>
      <xsl:value-of select="./researchNetworkDescription"/>      
    </p>

    <p>
      <a>
        <xsl:attribute name="href">
          <xsl:value-of select="researchNetworkWebsite"/>        
        </xsl:attribute>
        <xsl:value-of select="researchNetworkWebsite"/>        
      </a>
    </p>

    <ul>
      <xsl:for-each select="memberships">
        <li>
          <a>
            <xsl:attribute name="href">
              <xsl:text>/redirect?oid=</xsl:text>
              <xsl:value-of select="./targetItem/@oid"/>
            </xsl:attribute>
            <xsl:value-of select="./targetItem/titlePre"/>&nbsp;<xsl:value-of select="./targetItem/givenname"/>&nbsp;<xsl:value-of select="./targetItem/surname"/>&nbsp;<xsl:value-of select="./targetItem/titlePost"/>
          </a>
        </li>
      </xsl:for-each>
    </ul>


  </xsl:template>
  
  <xsl:template match="cms:item[objectType='com.arsdigita.cms.contenttypes.ResearchNetwork']"
    mode="cms:CT_text"
    name="cms:CT_text_com_arsdigita_cms_contenttypes_ResearchNetwork">

    <p>
      <xsl:value-of select="./title" />
      <xsl:value-of select="./description" />
    </p>

  </xsl:template>



</xsl:stylesheet>