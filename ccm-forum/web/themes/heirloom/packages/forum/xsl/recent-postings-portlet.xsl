<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
               xmlns:forum="http://www.arsdigita.com/forum/1.0"
                   version="1.0">

  <xsl:output method="html"/>

  <xsl:template match="forum:recentPostingsPortlet">
    <table class="data">
      <thead>
        <tr>
          <th>Subject</th>
          <th>Replies</th>
          <th>Topic</th>
          <th>Last Post</th>
          <th>Status</th>
        </tr>
      </thead>
      <tbody>
        <xsl:if test="count(forum:thread) = 0">
          <td colspan="5"><em>No messages have been posted yet</em></td>
        </xsl:if>
        <xsl:for-each select="forum:thread">
          <xsl:variable name="class">
            <xsl:choose>
              <xsl:when test="position() mod 2">
                <xsl:text>odd</xsl:text>
              </xsl:when>
              <xsl:otherwise>
                <xsl:text>even</xsl:text>
              </xsl:otherwise>
            </xsl:choose>
          </xsl:variable>
          <tr class="{$class}">
            <td><a href="{@url}"><xsl:value-of select="root/subject"/></a></td>
            <td><xsl:value-of select="numReplies"/></td>
            <xsl:choose>
              <xsl:when test="root/categories">
                <td><xsl:value-of select="root/categories/name"/></td>
              </xsl:when>
              <xsl:otherwise>
                <td><em><xsl:text>None</xsl:text></em></td>
              </xsl:otherwise>
            </xsl:choose>
            <td><xsl:value-of select="lastUpdate"/></td>
            <td><xsl:value-of select="root/status"/></td>
          </tr>
        </xsl:for-each>
      </tbody>
    </table>
  </xsl:template>

</xsl:stylesheet>
