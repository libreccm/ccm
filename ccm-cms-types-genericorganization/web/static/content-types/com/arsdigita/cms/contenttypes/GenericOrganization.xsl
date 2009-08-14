<!DOCTYPE stylesheet [
<!ENTITY nbsp   "&#160;" ><!-- no-break space = non-breaking space, U+00A0 ISOnum -->
]>

<xsl:stylesheet 
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
  xmlns:cms="http://www.arsdigita.com/cms/1.0"
  version="1.0">
  
  <xsl:template match="cms:item[objectType='com.arsdigita.cms.contenttypes.GenericOrganization']"
		mode="cms:CT_graphics"
		name="cms:CT_graphics_com_arsdigita_cms_contenttypes_GenericOrganization">
    <h2>
      <xsl:value-of select="./organizationname"/>
      <span class="organizationnameaddendum">        
        &nbsp;<xsl:value-of select="./organizationnameaddendum"/>
      </span>
    </h2>    
    <p>
      <xsl:value-of select="./pageDescription"/>
    </p>
    <dl>
      <xsl:for-each select="organizationroles">
        <dt>
          <xsl:value-of select="./roleName"/>
        </dt>
        <dd>
          <a>
            <xsl:attribute name="href">
              <xsl:text>/redirect?oid=</xsl:text>
              <xsl:value-of select="./targetItem/@oid"/>
            </xsl:attribute>
            <xsl:value-of select="./targetItem/titlePre"/>&nbsp;<xsl:value-of select="./targetItem/givenname"/>&nbsp;<xsl:value-of select="./targetItem/surname"/>&nbsp;<xsl:value-of select="./targetItem/titlePost"/>
          </a>
        </dd>        
      </xsl:for-each>
    </dl>

    <ul>
      <xsl:for-each select="subunits">
        <li>
          <a>
            <xsl:attribute name="href">
              <xsl:text>/redirect?oid=</xsl:text>
              <xsl:value-of select="./targetItem/@oid"/>
            </xsl:attribute>
            <xsl:value-of select="./targetItem/organizationalunitName"/>
          </a>
        </li>
      </xsl:for-each>
    </ul>
    
  </xsl:template>

  <xsl:template match="cms:item[objectType='com.arsdigita.cms.contenttypes.GenericOrganization']"
		mode="cms:CT_text"
		name="cms:CT_text_com_arsdigita_cms_contenttypes_GenericOrganization">

    <h2>
      <xsl:value-of select="./organizationname"/>
      <span class="organizationnameaddendum">        
        &nbsp;<xsl:value-of select="./organizationnameaddendum"/>
      </span>
    </h2>    
    <p>
      <xsl:value-of select="./pageDescription"/>
    </p>
    <dl>
      <xsl:for-each select="organizationroles">
        <dt>
          <xsl:value-of select="./roleName"/>
        </dt>
        <dd>
          <a>
            <xsl:attribute name="href">
              <xsl:text>/redirect?oid=</xsl:text>
              <xsl:value-of select="./targetItem/@oid"/>
            </xsl:attribute>
            <xsl:value-of select="./targetItem/titlePre"/>&nbsp;<xsl:value-of select="./targetItem/givenname"/>&nbsp;<xsl:value-of select="./targetItem/surname"/>&nbsp;<xsl:value-of select="./targetItem/titlePost"/>
          </a>
        </dd>        
      </xsl:for-each>
    </dl>

    <ul>
      <xsl:for-each select="subunits">
        <li>
          <a>
            <xsl:attribute name="href">
              <xsl:text>/redirect?oid=</xsl:text>
              <xsl:value-of select="./targetItem/@oid"/>
            </xsl:attribute>
            <xsl:value-of select="./targetItem/organizationalunitName"/>
          </a>
        </li>
      </xsl:for-each>
    </ul>
    
  </xsl:template>

</xsl:stylesheet>
  