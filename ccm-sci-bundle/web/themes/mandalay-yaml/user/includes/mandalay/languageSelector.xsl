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
		xmlns:ui="http://www.arsdigita.com/ui/1.0"
		xmlns:nav="http://ccm.redhat.com/navigation"
		xmlns:mandalay="http://mandalay.quasiweb.de"
		exclude-result-prefixes="xsl bebop cms ui nav"
		version="1.0">

  <xsl:template name="mandalay:languageSelector">
    <xsl:param name="layoutTree" select="."/>
    <xsl:param name="supportedLanguages">
      <!-- DE Entferne die Markierung für die Default-Sprache -->
      <!-- EN Remove the marking for default language -->
      <xsl:call-template name="mandalay:string-replace">
        <xsl:with-param name="string">
          <xsl:call-template name="mandalay:string-replace">
            <xsl:with-param name="string" select="$languages"/>
            <xsl:with-param name="from" select="'['"/>
            <xsl:with-param name="to" select="''"/>
          </xsl:call-template>
        </xsl:with-param>
        <xsl:with-param name="from" select="']'"/>
        <xsl:with-param name="to" select="''"/>
      </xsl:call-template>
    </xsl:param>
    
    <xsl:variable name="separator">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="node"    select="$layoutTree/separator"/>
        <xsl:with-param name="module"  select="'languageSelector'"/>
        <xsl:with-param name="setting" select="'separator'"/>
        <xsl:with-param name="default" select="' | '"/>
      </xsl:call-template>
    </xsl:variable>
    
    <xsl:variable name="langIter">
      <xsl:choose>
        <xsl:when test="contains($supportedLanguages, ' ')">
          <xsl:value-of select="substring-before($supportedLanguages, ' ')"/>
        </xsl:when>
        <xsl:otherwise>
          <xsl:value-of select="$supportedLanguages"/>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:variable>

    <xsl:variable name="localizedLanguageText">
      <xsl:call-template name="mandalay:getStaticText">
        <xsl:with-param name="module" select="''"/>
        <xsl:with-param name="id" select="'lang'"/>
        <xsl:with-param name="lang" select="$langIter"/>
      </xsl:call-template>
    </xsl:variable>
    
    <span class="languageEntry">
      <xsl:choose>
        <xsl:when test="$langIter = $negotiated-language">
          <xsl:value-of select="$localizedLanguageText"/>
        </xsl:when>
        <xsl:otherwise>
          <a>
            <xsl:attribute name="href">
              <xsl:value-of select="concat('http://', $langIter, '.')"/>
              <xsl:value-of select="substring-after($resultTree/ui:siteBanner/@sitename, '.')"/>
              <xsl:value-of select="$resultTree/@url"/>
            </xsl:attribute>
            <xsl:attribute name="title">
              <xsl:value-of select="$localizedLanguageText"/>
            </xsl:attribute>
            <xsl:value-of select="$localizedLanguageText"/>
          </a>
        </xsl:otherwise>
      </xsl:choose>
      <!-- DE Wenn das nicht der letzte Eintrag in der Liste ist, dann füge den Seperator hinzu -->
      <!-- EN If this is not the last entry of the list, add the seperator -->
      <xsl:if test="string-length($supportedLanguages) > 3">
        <xsl:value-of select="$separator"/>
      </xsl:if>
    </span>

    <!-- DE Wenn mehr als 3 Zeichen im String sind, d.h. mehr als ein Spracheintrag,
            dann entferne den aktuellen Eintrag und gehe in Rekursion -->
    <!-- EN If there more then 3 chars in the string, it means there is more then
            one language entry, so remove current entry and go recursiv -->

    <xsl:if test="string-length($supportedLanguages) > 3">
      <xsl:call-template name="mandalay:languageSelector">
        <xsl:with-param name="supportedLanguages">
          <xsl:call-template name="mandalay:string-replace">
            <xsl:with-param name="string" select="$supportedLanguages"/>
            <xsl:with-param name="from" select="concat($langIter, ' ')"/>
            <xsl:with-param name="to" select="''"/>
          </xsl:call-template>
        </xsl:with-param>
      </xsl:call-template>
    </xsl:if>
  </xsl:template>
</xsl:stylesheet>
