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

  <!-- DE DaBIn Arbeitspapier-Detailausgabe -->
  <xsl:template match="dabin:arbeitspapier" mode="detailed_view">
    <h2><xsl:value-of select="dabin:name"/></h2>
   <!-- Ohne Table -->
<!--    <dl>
      <dt>
        <xsl:call-template name="mandalay:getStaticText">
          <xsl:with-param name="module" select="'DaBIn'" />
          <xsl:with-param name="id" select="'authors'" />
        </xsl:call-template>
      </dt>
      <dd>
        <xsl:apply-templates select="dabin:autoren" mode="detailed_view"/>
      </dd>
      <dt>
        <xsl:call-template name="mandalay:getStaticText">
          <xsl:with-param name="module" select="'DaBIn'" />
          <xsl:with-param name="id" select="'wp-nr'" />
        </xsl:call-template>
      <xsl:text>:  </xsl:text>
      <xsl:value-of select="dabin:apnum"/>
      <xsl:text> / </xsl:text>
      <xsl:value-of select="dabin:jahr"/>
      </dt>
      <dt>
        <xsl:call-template name="mandalay:getStaticText">
          <xsl:with-param name="module" select="'DaBIn'" />
          <xsl:with-param name="id" select="'projlink'" />
        </xsl:call-template>
      </dt>
      <dd><xsl:value-of select="dabin:link" disable-output-escaping="yes"/></dd>
      <p>
        <xsl:value-of select="dabin:beschreibung" disable-output-escaping="yes"/>
      </p>
    </dl>-->
   <!-- Mit Table -->
    <table width="100%" class="dabinDesc">
      <tr>
      <td valign="top">
        <xsl:call-template name="mandalay:getStaticText">
          <xsl:with-param name="module" select="'DaBIn'" />
          <xsl:with-param name="id" select="'authors'"/>
        </xsl:call-template>:
      </td>
      <td>
        <xsl:apply-templates select="dabin:autoren" mode="detailed_view"/>
      </td>
      </tr>
      <tr>
      <td>
        <xsl:call-template name="mandalay:getStaticText">
          <xsl:with-param name="module" select="'DaBIn'" />
          <xsl:with-param name="id" select="'wp-nr'" />
        </xsl:call-template>
      <xsl:text>:  </xsl:text>
      </td>
      <td>
      <xsl:value-of select="dabin:apnum"/>
      <xsl:text> / </xsl:text>
      <xsl:value-of select="dabin:jahr"/>
      </td>
      </tr>
      <xsl:if test="string-length(dabin:link) &gt; 0">
         <tr>
         <td valign="top">
            <xsl:call-template name="mandalay:getStaticText">
               <xsl:with-param name="module" select="'DaBIn'" />
               <xsl:with-param name="id" select="'downlink'" />
            </xsl:call-template>
         </td>
         <td><xsl:value-of select="dabin:link" disable-output-escaping="yes"/></td>
         </tr>
      </xsl:if>
      </table>
      <p>
        <xsl:value-of select="dabin:beschreibung" disable-output-escaping="yes"/>
      </p>

  </xsl:template>

  <!-- DE DaBIn Arbeitspapier-Listausgabe -->
  <xsl:template match="dabin:arbeitspapier" mode="list_view">
    <li class="dabinArbeitspapier">
      <xsl:apply-templates select="dabin:autoren" mode="list_view"/>
      <xsl:if test="dabin:jahr">
        <span class="jahr">
          <xsl:text>(</xsl:text>
          <xsl:value-of select="dabin:jahr"/>
          <xsl:text>): </xsl:text>
        </span>
      </xsl:if>
      <span class="fullname">
        <a>
          <xsl:attribute name="title">
            <xsl:call-template name="mandalay:getStaticText">
              <xsl:with-param name="module" select="'DaBIn'" />
              <xsl:with-param name="id" select="'details'" />
            </xsl:call-template>
            <xsl:value-of select="dabin:name" disable-output-escaping="yes"/>
          </xsl:attribute>
          <xsl:attribute name="href">
            <xsl:text>?id=</xsl:text>
            <xsl:value-of select="dabin:apid"/>
            <xsl:text>&amp;lang=</xsl:text>
            <xsl:value-of select="//dabin:lang"/>
          </xsl:attribute>
          <xsl:value-of select="dabin:name" disable-output-escaping="yes"/>
        </a>
      </span>
    </li>
  </xsl:template>

</xsl:stylesheet>
