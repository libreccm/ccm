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
  Hier wird der ImageBrowser verarbeitet 
-->

<!-- EN
  Processing ImageBrowser
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
  
  <!-- DE Erzeugt die Liste mit den zugewiesenen Aufgaben -->
  <!-- EN Create a list of tasks -->
  <xsl:template match="bebop:table[@class='imageBrowser']">
    <div id="cmsImageBrowser">
      <xsl:apply-templates select="bebop:tbody/bebop:trow" mode="imageBrowser"/>
    </div>
  </xsl:template>
  
  <xsl:template match="bebop:trow" mode="imageBrowser">
    <div class="tile">
      <xsl:apply-templates select="bebop:cell" mode="imageBrowser"/>
    </div>
  </xsl:template>
  
  <xsl:template match="bebop:cell" mode="imageBrowser">
    <div>
      <xsl:if test="bebop:label">
        <xsl:attribute name="alt">
          <xsl:value-of select="bebop:label"/>
        </xsl:attribute>
        <xsl:attribute name="title">
          <xsl:value-of select="bebop:label"/>
        </xsl:attribute>
      </xsl:if>
      <xsl:apply-templates/>
    </div>  
  </xsl:template>

  <!--  
  <xsl:template match="bebop:link" mode="imageBrowser">
    <a>
      <xsl:attribute name="href">
        <xsl:value-of select=""/>
      </xsl:attribute>
      <xsl:apply-templates mode="imageBrowser"/>
    </a>
  </xsl:template>
  
  <xsl:template match="bebop:image" mode="imageBrowser">
    <img src="" alt=""/>
  </xsl:template>
  -->
  <!--
  <xsl:template match="bebop:label" mode="imageBrowser">
    
  </xsl:template>
  -->
  
</xsl:stylesheet>
