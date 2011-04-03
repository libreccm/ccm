<?xml version="1.0"?>

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
  xmlns:portlet="http://www.uk.arsdigita.com/portlet/1.0">


<xsl:template match="portlet:timeOfDay">
  <xsl:value-of select="@date"/>
</xsl:template>

</xsl:stylesheet>
