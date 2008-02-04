<?xml version="1.0"?>

<xsl:stylesheet version="1.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

<xsl:output method="html" />

<xsl:template match="find_my_nearest">
  <h1>Up My Street - Find My Nearest</h1>
  <xsl:apply-templates />
</xsl:template>

<xsl:template match="results">
<h2><xsl:value-of select="@dataset_name" /> Banks - <xsl:value-of select="@location_value" /></h2>
<table>
<tr>
  <th>Position</th>
  <th>Name</th>
  <th>Address</th>
  <th>Telephone</th>
  <th>Distance</th>
  <th>Map</th>
</tr>
<xsl:apply-templates />
</table>
</xsl:template>

<xsl:template match="item">
<tr>
  <td><xsl:value-of select="@position" /></td>
  <td><xsl:value-of select="name" /></td>
  <td>
    <xsl:value-of select="address" /><br />
    <xsl:value-of select="town" /><br />
    <xsl:value-of select="locality" /><br />    
    <xsl:value-of select="county"/><br />
    <xsl:value-of select="postcode"/><br />
  </td>
  <td><xsl:value-of select="telephone"/></td>
  <td><xsl:value-of select="distance"/></td>
  <td><a href=""><xsl:attribute name="href">http://www.streetmap.co.uk/newmap.srf?x=<xsl:value-of select="x"/>&amp;y=<xsl:value-of select="y"/></xsl:attribute>Map</a></td>
</tr>
</xsl:template>


</xsl:stylesheet>