<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:cms="http://www.arsdigita.com/cms/1.0"
                version="1.0">

  <!-- A fallback template for any cms:item. This template just makes sure
       that every cms:item contained in the input will be output in some
       form and is usually shadowed by the other template rules in this
       file.
    -->

  <xsl:template match="cms:item" mode="cms:CT_graphics">
    <table bgcolor="#FFCCCC" cellspacing="0" cellpadding="4" border="0">
      <tr>
        <th align="left">Name</th>
        <td><xsl:value-of select="./name"/></td>
      </tr>
      <tr>
        <th align="left">Pretty Name</th>
        <td><xsl:value-of select="./displayName"/></td>
      </tr>
      <tr>
        <th align="left">Content Type</th>
        <td><xsl:value-of select="./type/label"/></td>
      </tr>
    </table>
  </xsl:template>

  <xsl:template match="cms:item" mode="cms:CT_text">
    <span>Name</span>
    <span><xsl:value-of select="./name"/></span><br/>
    <span>Pretty Name</span>
    <span><xsl:value-of select="./displayName"/></span><br/>
    <span>Content Type</span>
    <span><xsl:value-of select="./type/label"/></span><br/>
  </xsl:template>

</xsl:stylesheet>
