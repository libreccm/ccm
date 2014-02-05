<?xml version="1.0" encoding="utf-8"?>
<!DOCTYPE stylesheet [<!ENTITY nbsp '<xsl:text disable-output-escaping="yes">&amp;</xsl:text>nbsp;'>]>

<!-- 
    Copyright: 2006, 2007, 2008 Sören Bernstein
  
    This file is part of Mandalay.

    Mandalay is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 2 of the License, or
    (at your option) any later version.

    Mandalay is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with Mandalay.  If not, see <http://www.gnu.org/licenses/>.
-->

<xsl:stylesheet 
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:bebop="http://www.arsdigita.com/bebop/1.0"
  xmlns:subsite="http://ccm.redhat.com/london/subsite/1.0"
  version="1.0">

  <xsl:import href="../../../../../ROOT/packages/ui/xsl/ui.xsl"/>

  <xsl:param name="dispatcher-prefix"/>

  <xsl:template match="bebop:form[@class='simpleForm']">
    <form>
      <xsl:for-each select="@*[not(self::method)]">
        <xsl:attribute name="{name()}">
          <xsl:value-of select="."/>
        </xsl:attribute>
        <xsl:attribute name="method">
          <xsl:choose> 
            <xsl:when test="string-length(../@method)=0">post</xsl:when>
            <xsl:otherwise><xsl:value-of select="../@method"/></xsl:otherwise>
          </xsl:choose> 
        </xsl:attribute>
      </xsl:for-each>
      <table>
        <xsl:for-each select="*[not(name() = 'bebop:pageState') and not(name() = 'bebop:formWidget' and @type = 'hidden')]">
          <tr>
            <th align="right"><xsl:if test="@metadata.title"><xsl:value-of select="@metadata.title"/>:</xsl:if></th>
            <td><xsl:apply-templates select="."/></td>
          </tr>
        </xsl:for-each>
      </table>
      <xsl:apply-templates select="bebop:formWidget[@type='hidden']"/>
      <xsl:apply-templates select="bebop:pageState"/>
    </form>

  </xsl:template>

  <xsl:template match="subsite:controlCenter">
    <h3>Subsite listing</h3>
    <xsl:apply-templates select="subsite:siteListing"/>
    <xsl:choose>
      <xsl:when test="subsite:siteListing/@selected">
        <h3>Edit subsite details</h3>
      </xsl:when>
      <xsl:otherwise>
        <h3>Create new subsite</h3>
      </xsl:otherwise>
    </xsl:choose>
    <xsl:apply-templates select="bebop:form"/>
  </xsl:template>

  <xsl:template match="subsite:siteListing">
    <table>
      <xsl:for-each select="object">
        <tr>
          <td><xsl:value-of select="title"/></td>
          <td><a title="View the site" href="http://{hostname}/"><xsl:value-of select="hostname"/></a></td>
          <td><a href="?site={id}">[edit]</a></td>
        </tr>
        <tr>
          <td colspan="3" style="font-size: smaller; font-style: italic; text-indent: 2em"><xsl:value-of select="description"/></td>
        </tr>
      </xsl:for-each>
    </table>
  </xsl:template>

</xsl:stylesheet>
 
