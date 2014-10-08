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

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
		xmlns:bebop="http://www.arsdigita.com/bebop/1.0"
		xmlns:cms="http://www.arsdigita.com/cms/1.0"
                xmlns:nav="http://ccm.redhat.com/navigation"
		xmlns:mandalay="http://mandalay.quasiweb.de"
		xmlns:dabin="http://dabin.quasiweb.de"
		exclude-result-prefixes="xsl bebop cms"
		version="1.0">
  <xsl:template match="dabin:search" mode="list_view">
    <form action="" method="get">
      <fieldset>
        <legend>
          <xsl:call-template name="mandalay:getStaticText">
            <xsl:with-param name="module" select="'DaBIn'" />
            <xsl:with-param name="id" select="'search'" />
          </xsl:call-template>
        </legend>
        <xsl:element name="input">
          <xsl:attribute name="name">search</xsl:attribute>
          <xsl:attribute name="type">text</xsl:attribute>
          <xsl:attribute name="size">50</xsl:attribute>
          <xsl:attribute name="maxlength">512</xsl:attribute>
          <xsl:attribute name="value">
            <xsl:value-of select="."/>
          </xsl:attribute>
        </xsl:element>
        <input type="submit" name="Finden">
          <xsl:attribute name="value">
            <xsl:call-template name="mandalay:getStaticText">
              <xsl:with-param name="module" select="'DaBIn'" />
              <xsl:with-param name="id" select="'find'" />
            </xsl:call-template>
          </xsl:attribute>
        </input>
        <xsl:if test="string-length(.) > 0">
          <br />
          <a class="backLink" href="?limit=year">
            <xsl:choose>
              <xsl:when test="../dabin:arbeitspapier">
                <xsl:call-template name="mandalay:getStaticText">
                  <xsl:with-param name="module" select="'DaBIn'" />
                  <xsl:with-param name="id" select="'backToYearViewWorkingPapers'" />
                </xsl:call-template>                
              </xsl:when>
              <xsl:otherwise>
                <xsl:call-template name="mandalay:getStaticText">
                  <xsl:with-param name="module" select="'DaBIn'" />
                  <xsl:with-param name="id" select="'backToYearViewPublications'" />                
                </xsl:call-template>
              </xsl:otherwise>
            </xsl:choose>
          </a>          
        </xsl:if>
      </fieldset>
    </form>
  </xsl:template>
</xsl:stylesheet>
