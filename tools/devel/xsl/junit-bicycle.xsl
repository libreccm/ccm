<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
  <xsl:template match="/testsuite">
    <cycle>
      <key><xsl:value-of select="@name"/></key>

      <xsl:for-each select="properties/property">
        <xsl:sort select="@name"/>

        <property>
          <name><xsl:value-of select="@name"/></name>
          <value><xsl:value-of select="@value"/></value>
        </property>
      </xsl:for-each>

      <notice><xsl:value-of select="system-out"/></notice>
      <warning><xsl:value-of select="system-err"/></warning>

      <xsl:for-each select="testcase">
        <cycle>
          <key><xsl:value-of select="@name"/></key>

          <xsl:for-each select="error|failure">
            <failure><xsl:value-of select="."/></failure>
          </xsl:for-each>
        </cycle>
      </xsl:for-each>
    </cycle>
  </xsl:template>
</xsl:stylesheet>