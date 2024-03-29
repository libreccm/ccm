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
  Hier wird der Bebeop ContextBar verarbeitet 
-->

<!-- EN
  Processing bebop contextbar
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
    
  <!-- DE Verarbeite de Contextbar -->
  <!-- EN Processing contextbar -->
  <xsl:template match="bebop:contextBar">
    <xsl:param name="layoutTree" select="."/>
    
    <xsl:variable name="separator">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="node" select="$layoutTree/separator" />
        <xsl:with-param name="module" select="'contextBar'" />
        <xsl:with-param name="setting" select="'separator'" />
        <xsl:with-param name="default" select="' -> '" />
      </xsl:call-template>
    </xsl:variable>
    
    <div class="bebopContextBar">
      <xsl:apply-templates>
        <xsl:with-param name="separator">
          <xsl:value-of select="$separator"/>
        </xsl:with-param>
      </xsl:apply-templates>
    </div>
  </xsl:template>
  
  <!-- DE Setze die einzelnen Einträge in der Leiste-->
  <!-- EN Set the contextbar entries -->
  <xsl:template match="bebop:entry">
    <xsl:param name="separator"/>

      <xsl:choose>
      <xsl:when test="position() = last()">
        <span class="bebopContextBarCurrentElement">
          <xsl:value-of select="@title"/>
        </span>
      </xsl:when>
      <xsl:otherwise>
        <span class="bebopContextBarElement">
          <a href="{@href}">
            <xsl:value-of select="@title"/>
          </a>
          <span class="bebopContextBarSeperator">
            <xsl:value-of select="$separator"/>
          </span>
        </span>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>
    
</xsl:stylesheet>
