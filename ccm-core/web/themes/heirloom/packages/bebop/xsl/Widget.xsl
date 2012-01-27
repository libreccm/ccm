<?xml version="1.0"?>
<xsl:stylesheet  xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
               xmlns:bebop="http://www.arsdigita.com/bebop/1.0"
   exclude-result-prefixes="xsl bebop"
                   version="1.0">

 <xsl:output method="html" indent="yes"/>


 <xsl:template match="bebop:formErrors">    
 	<font color="red">
    <xsl:value-of disable-output-escaping="yes" select="@message"/>
    </font>
    <br />
 </xsl:template>
 
 <xsl:template match="bebop:formWidget">
    <xsl:element name="input">
        <xsl:for-each select="@*">
            <xsl:attribute name="{name()}">
                <xsl:value-of select="."/>
            </xsl:attribute>
        </xsl:for-each>
    </xsl:element>
 </xsl:template>

 <xsl:template match="bebop:formWidget[@type='submit']">
    <input>
      <xsl:if test="boolean(@onclick) = false()">
        <xsl:attribute name="onclick">
             <xsl:text>dcp_hide(this);</xsl:text>
        </xsl:attribute>
      </xsl:if>
      <xsl:for-each select="@*">
        <xsl:attribute name="{name()}">
          <xsl:value-of select="."/>
        </xsl:attribute>
      </xsl:for-each>
    </input>
 </xsl:template>


</xsl:stylesheet>
