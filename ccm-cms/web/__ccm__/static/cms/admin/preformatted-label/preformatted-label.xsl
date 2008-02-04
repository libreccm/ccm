<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
  xmlns:bebop="http://www.arsdigita.com/bebop/1.0"
  xmlns:cms="http://www.arsdigita.com/cms/1.0"
  xmlns:ui="http://www.arsdigita.com/ui/1.0"
>

  <xsl:import href="../../../../../packages/bebop/xsl/Label.xsl"/>

 <xsl:template match="bebop:label[@class='preformatted']">
   <pre>
     <xsl:call-template name="bebop-label-text">
        <xsl:with-param name="text" select="text()"/>
        <xsl:with-param name="escape" select="@escape"/>
     </xsl:call-template>
   </pre>
 </xsl:template>

</xsl:stylesheet>