<?xml version="1.0" encoding="utf-8"?>
<!DOCTYPE stylesheet [<!ENTITY nbsp '&#160;'>]>

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

<!-- DE
  Hier werden die CA-Notes verarbeitet 
-->

<!-- EN
  Processing CA-Notes
-->

<!-- Autor: Sören Bernstein -->

<xsl:stylesheet 
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:bebop="http://www.arsdigita.com/bebop/1.0"
  xmlns:cms="http://www.arsdigita.com/cms/1.0" 
  xmlns:nav="http://ccm.redhat.com/navigation"
  xmlns:mandalay="http://mandalay.quasiweb.de" 
  exclude-result-prefixes="xsl bebop cms nav mandalay"
  version="1.0">

  <xsl:template name="mandalay:notes">
    <xsl:param name="layoutTree" select="."/>
    
    <xsl:if test="$resultTree//cms:item/ca_notes">

      <!-- DE Hole alle benötigten Einstellungen-->
      <!-- EN Getting all needed setting-->
      <xsl:variable name="setHeading">
        <xsl:call-template name="mandalay:getSetting">
          <xsl:with-param name="node"  select="$layoutTree/setHeading"/>
          <xsl:with-param name="module"  select="'notes'"/>
          <xsl:with-param name="setting" select="'setHeading'"/>
          <xsl:with-param name="default" select="'true'"/>
        </xsl:call-template>
      </xsl:variable>
      <xsl:variable name="setHeadingPerItem">
        <xsl:call-template name="mandalay:getSetting">
          <xsl:with-param name="node"  select="$layoutTree/setHeadingPerItem"/>
          <xsl:with-param name="module"  select="'notes'"/>
          <xsl:with-param name="setting" select="'setHeadingPerItem'"/>
          <xsl:with-param name="default" select="'false'"/>
        </xsl:call-template>
      </xsl:variable>

      <div class="notes">
        <xsl:if test="$setHeading='true' and $setHeadingPerItem='false'">
          <h2>
            <xsl:call-template name="mandalay:getStaticText">
              <xsl:with-param name="module" select="'notes'"/>
              <xsl:with-param name="id" select="'heading'"/>
            </xsl:call-template>
          </h2>
        </xsl:if>
        <ul>
          <xsl:for-each select="$resultTree//cms:item/ca_notes">
            <xsl:sort data-type="number" select="./rank"/>
            <li>
              <xsl:if test="$setHeading='true' and $setHeadingPerItem='true'">
                <h2>
                  <xsl:call-template name="mandalay:getStaticText">
                    <xsl:with-param name="module" select="'notes'"/>
                    <xsl:with-param name="id" select="'heading'"/>
                  </xsl:call-template>
                </h2>
              </xsl:if>
              <div class="text">
                <xsl:value-of disable-output-escaping="yes" select="./content"/>
              </div>
              <div class="endFloat"/>
            </li>
          </xsl:for-each>
        </ul>
      </div>
    </xsl:if>
  </xsl:template>

</xsl:stylesheet>
