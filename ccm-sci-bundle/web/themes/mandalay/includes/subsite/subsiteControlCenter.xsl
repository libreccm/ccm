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
  xmlns:subsite="http://ccm.redhat.com/subsite/1.0"
  xmlns:mandalay="http://mandalay.quasiweb.de"
  exclude-result-prefixes="xsl bebop subsite mandalay"
  version="1.0">

  <xsl:template match="subsite:controlCenter">
    <h3>
      <xsl:call-template name="mandalay:getStaticText">
        <xsl:with-param name="module" select="'subsite'"/>
        <xsl:with-param name="id" select="'controlCenter/heading'"/>
      </xsl:call-template>
    </h3>
    <xsl:apply-templates select="subsite:siteListing"/>
    <xsl:choose>
      <xsl:when test="subsite:siteListing/@selected">
        <h3>
          <xsl:call-template name="mandalay:getStaticText">
            <xsl:with-param name="module" select="'subsite'"/>
            <xsl:with-param name="id" select="'controlCenter/editSubsite'"/>
          </xsl:call-template>
        </h3>
      </xsl:when>
      <xsl:otherwise>
        <h3>
          <xsl:call-template name="mandalay:getStaticText">
            <xsl:with-param name="module" select="'subsite'"/>
            <xsl:with-param name="id" select="'controlCenter/newSubsite'"/>
          </xsl:call-template>
        </h3>
      </xsl:otherwise>
    </xsl:choose>
    <xsl:apply-templates select="bebop:form"/>
  </xsl:template>

</xsl:stylesheet>


