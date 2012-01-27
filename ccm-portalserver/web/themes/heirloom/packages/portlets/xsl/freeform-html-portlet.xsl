<?xml version="1.0"?>

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
  xmlns:portlet="http://www.arsdigita.com/portlet/1.0">

<xsl:template match="portlet:freeformHTML">
  <div>
    <xsl:value-of select="." disable-output-escaping="yes" />
  </div>
</xsl:template>

</xsl:stylesheet>
