<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
  xmlns:bebop="http://www.arsdigita.com/bebop/1.0"
  xmlns:cms="http://www.arsdigita.com/cms/1.0"
>
  <xsl:template match="bebop:contextBar">
    <div class="context-bar">
      <xsl:for-each select="bebop:entry">
        <xsl:choose>
          <xsl:when test="position() = last()">
            <div><xsl:value-of select="@title"/></div>
          </xsl:when>

          <xsl:otherwise>
            <div><a href="{@href}"><xsl:value-of select="@title"/></a></div>
            <div class="separator"><xsl:text>&gt;</xsl:text></div>
          </xsl:otherwise>
        </xsl:choose>
      </xsl:for-each>
    </div>
  </xsl:template>
</xsl:stylesheet>