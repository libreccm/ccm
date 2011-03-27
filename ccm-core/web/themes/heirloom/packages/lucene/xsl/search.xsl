<?xml version="1.0"?>

<!-- 
This is the main stylesheet for Bebop.  It imports all of the .xsl
files with xsl.import.  This stylesheet is locale-independent. 
--> 

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
                xmlns:search="http://arsdigita.com/search/1.0">

<xsl:import href="../../bebop/xsl/bebop.xsl"/>

<xsl:template match="search:search">
  <xsl:apply-templates/>
</xsl:template>

<xsl:template match="search:results">
<xsl:if test="@size > 0">
<table class="tabular">
  <tr>
    <th>Title</th>
    <th>Summary</th>
    <th>Created On</th>
    <th>Modified On</th>
    <th>Score</th>
  </tr>
  <xsl:for-each select="search:hit">
    <tr>
      <td>
        <a>
          <xsl:attribute name="href">
             <xsl:value-of select="@url"/>
          </xsl:attribute>
          <xsl:value-of select="@title"/>
        </a>
      </td>
      <td>
        <xsl:value-of select="@summary"/>
      </td>
      <td>
        <xsl:value-of select="@creationDate"/>
      </td>
      <td>
        <xsl:value-of select="@lastModifiedDate"/>
      </td>
      <td>
        <xsl:value-of select="@score"/>
      </td>
    </tr>
  </xsl:for-each>
</table>
</xsl:if>
</xsl:template>

</xsl:stylesheet>




