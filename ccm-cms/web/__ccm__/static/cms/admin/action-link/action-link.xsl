<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
  xmlns:bebop="http://www.arsdigita.com/bebop/1.0"
  xmlns:cms="http://www.arsdigita.com/cms/1.0"
>
  <xsl:template match="bebop:link[@class = 'actionLink']">
    <div class="action-link">
      <a href="{@href}" onclick="dcp_disable_link(this);">
        <div class="link-icon"/>

        <xsl:apply-templates/>
      </a>
    </div>
  </xsl:template>
</xsl:stylesheet>
