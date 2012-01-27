<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet  xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
               xmlns:bebop="http://www.arsdigita.com/bebop/1.0"
                 xmlns:cms="http://www.arsdigita.com/cms/1.0"
                   version="1.0">
                       
  <xsl:template match="bebop:segmentedPanel">
    <div class="segmented-panel">
      <xsl:for-each select="bebop:segment">
        <div class="segment">
          <div class="header">
            <xsl:apply-templates select="bebop:segmentHeader"/>
          </div>

          <div class="body">
            <xsl:apply-templates select="bebop:segmentBody"/>
          </div>
        </div>
      </xsl:for-each>
    </div>
  </xsl:template>
  
</xsl:stylesheet>