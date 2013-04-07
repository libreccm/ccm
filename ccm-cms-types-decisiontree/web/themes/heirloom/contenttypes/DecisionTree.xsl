<!DOCTYPE stylesheet [
<!ENTITY nbsp   "&#160;" ><!-- no-break space = non-breaking space, U+00A0 ISOnum -->
]>

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:cms="http://www.arsdigita.com/cms/1.0"
                version="1.0">

  <xsl:template match="cms:item[objectType='com.arsdigita.cms.contenttypes.DecisionTree']" mode="cms:CT_graphics"
    name="cms:CT_graphics_com_arsdigita_cms_contenttypes_DecisionTree">
    <script type="text/javascript" 
            src="/templates/ccm-cms-types-decisiontree/forms.js">
      // Placeholder to prevent the Camden templates from breaking this tag.
    </script>
    
    <h2><xsl:value-of select="./sections/title"/></h2>
    
    <div><xsl:value-of select="./sections/instructions/content" disable-output-escaping="yes"/></div>

    <form action="/templates/ccm-cms-types-decisiontree/form-handler.jsp" method="GET">
      <input type="hidden" name="section_oid">
        <xsl:attribute name="value">
          <xsl:value-of select="./sections[title]/@oid"/>
        </xsl:attribute>
      </input>
      <input type="hidden" name="return_url">
        <xsl:attribute name="value">
          <xsl:value-of select="./customInfo/@currentURL"/>
        </xsl:attribute>
      </input>

      <xsl:for-each select="./parameters">
        <input type="hidden">
          <xsl:attribute name="name">
            <xsl:value-of select="@name"/>
          </xsl:attribute>
          <xsl:attribute name="value">
            <xsl:value-of select="@value"/>
          </xsl:attribute>
        </input>
      </xsl:for-each>

      <xsl:for-each select="./sections/sectionOptions">
        <xsl:sort select="./rank" data-type="number"/>

        <div>
          <input type="radio">
            <xsl:attribute name="name">
              <xsl:value-of select="../parameterName"/>
            </xsl:attribute>
            <xsl:attribute name="value">
              <xsl:value-of select="./value"/>
            </xsl:attribute>
            <xsl:attribute name="id">
              <xsl:value-of select="@oid"/>
            </xsl:attribute>
          </input>
          <label>
            <xsl:attribute name="for">
              <xsl:value-of select="@oid"/>
            </xsl:attribute>
            <xsl:value-of select="./label"/>
          </label>
        </div>
      </xsl:for-each>

      <input type="submit" name="cancel" value="Cancel"/>
      <input type="submit" name="next" value="Next &gt;" onclick="return validate(this.form)"/>
    </form>
  </xsl:template>

  <xsl:template match="cms:item[objectType='com.arsdigita.cms.contenttypes.DecisionTree']" mode="cms:CT_text"
    name="cms:CT_text_com_arsdigita_cms_contenttypes_DecisionTree">
    <h1 class="mainTitle">DECISIONTREE <xsl:text disable-output-escaping="yes">&amp;</xsl:text>gt; <xsl:value-of select="./title"/></h1>
  </xsl:template>

</xsl:stylesheet>
