<xsl:stylesheet 
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">


<xsl:output method="html" indent="yes"/>

<xsl:template match="use_case">
  <xsl:value-of select="id/@number"/>
    <xsl:text disable-output-escaping="yes">
      &amp;nbsp;&amp;nbsp;
    </xsl:text>
  <xsl:value-of select="title"/>
</xsl:template>

</xsl:stylesheet>	
