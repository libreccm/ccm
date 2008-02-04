<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
  xmlns:bebop="http://www.arsdigita.com/bebop/1.0"
  xmlns:cms="http://www.arsdigita.com/cms/1.0"
>
  <xsl:template match="bebop:propertyList">
    <table class="property-list">
      <xsl:for-each select="bebop:property">
        <tr>
          <th><xsl:value-of select="@title"/></th>
          <td><xsl:value-of select="@value"/></td>
        </tr>
      </xsl:for-each>
    </table>
  </xsl:template>
</xsl:stylesheet>