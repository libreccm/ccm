<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
              xmlns:bebop="http://www.arsdigita.com/bebop/1.0"
                xmlns:nav="http://ccm.redhat.com/navigation"
                  version="1.0">

  <xsl:template match="nav:quickLinkPanel">
    <xsl:apply-templates select="*"/>
  </xsl:template>

</xsl:stylesheet>
