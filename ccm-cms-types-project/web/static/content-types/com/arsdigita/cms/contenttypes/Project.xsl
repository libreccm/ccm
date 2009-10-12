<!DOCTYPE stylesheet [
<!ENTITY nbsp   "&#160;" ><!-- no-break space = non-breaking space, U+00A0 ISOnum -->
]>

<xsl:stylesheet 
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
  xmlns:cms="http://www.arsdigita.com/cms/1.0"
  version="1.0">
  
  <xsl:template match="cms:item[objectType='com.arsdigita.cms.contenttypes.Project']"
    mode="cms:CT_graphics"
    name="cms:CT_graphics_com_arsdigita_cms_contenttypes_Project">
    <h2>
      <xsl:value-of select="./projectname"/>      
    </h2>
    <div class="projectDescription">
      <xsl:value-of select="./projectDescription" 
        disable-output-escaping="yes" />
    </div>
    <div>
      <xsl:value-of select="./funding"
        disable-output-escaping="yes" />
    </div>
    
    <ul class="projectTeam">
      <xsl:for-each select="project2Person">
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
    
    <ul>
      <xsl:for-each select="units">
        <li>
          <a>
            <xsl:attribute name="href">
              <xsl:text>/redirect?oid=</xsl:text>              
              <xsl:value-of select="./targetItem/@oid" />
            </xsl:attribute>            
            <xsl:value-of select="./targetItem/organizationalunitName"/>                        
          </a>
        </li>
      </xsl:for-each>
    </ul>

  </xsl:template>

</xsl:stylesheet>