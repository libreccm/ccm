<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet  xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
               xmlns:bebop="http://www.arsdigita.com/bebop/1.0"
                 xmlns:cms="http://www.arsdigita.com/cms/1.0"
                   version="1.0">
                       
  <xsl:template match="bebop:section">
    <div class="section">
      <div class="heading">
        <xsl:apply-templates select="bebop:heading/*"/>
      </div>

      <div class="body">
        <xsl:apply-templates select="bebop:body/*"/>
      </div>
    </div>
  </xsl:template>
  
</xsl:stylesheet>