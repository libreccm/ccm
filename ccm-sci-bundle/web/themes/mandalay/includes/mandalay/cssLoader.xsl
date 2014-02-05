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
Hier wird die Browserweiche und die Funktione zum Einbinden der CSS-Dateien definiert
-->

<!-- EN
Setting up CSSLoader with browser switch
-->

<!-- Autor: Sören Bernstein -->

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
                xmlns:bebop="http://www.arsdigita.com/bebop/1.0"
                xmlns:cms="http://www.arsdigita.com/cms/1.0"
                xmlns:nav="http://ccm.redhat.com/navigation"
                xmlns:mandalay="http://mandalay.quasiweb.de"
                exclude-result-prefixes="xsl bebop cms nav mandalay"
                version="1.0">

  <xsl:template name="mandalay:cssLoaderNew">
    <xsl:variable name="application">
      <xsl:choose>
        <xsl:when test="$resultTree/@application">
          <xsl:value-of select="$resultTree/@application"/>
        </xsl:when>
        <xsl:when test="$resultTree/@class">
          <xsl:value-of select="$resultTree/@class"/>
        </xsl:when>
        <xsl:otherwise>
          <xsl:value-of select="'none'"/>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:variable>
    
    <xsl:variable name="cssfiles">
      <xsl:choose>
        <xsl:test when="document(concat($theme-prefix, '/settings/css-files.xml'))/css-files/application/@name=$application">
          <xsl:value-of select="document(concat($theme-prefix, '/settings/css-files.xml'))/css-files/application/@name=$application"/>
        </xsl:test>
        <xsl:otherwise>
          <xsl:value-of select="document(concat($theme-prefix, '/settings/css-files.xml'))/css-files/default"/>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:variable>
    
    <xsl:for-each select="$cssfiles/css-file">
      
    </xsl:for-each>
    
    <!-- Iterate over the defined files and create link elements for them-->
    
    <!-- if an iehacks file is included and the ie version is 7 or lower include this file -->
    
    
    
    <xsl:call-template name="mandalay:cssLoader"/>
  </xsl:template>
  
  
  <!-- DE Lade die CSS-Dateien abhänging von Media, Contenttype, Browsertyp und useContext (admin) -->
  <!-- EN Load CSS files by media, contenttype, browser and context (admin) -->
  <xsl:template name="mandalay:cssLoaderOld">

    <!-- DE Unterscheide Browser und speichere das Ergebnis -->
    <!-- EN Get the browser type-->
    <xsl:variable name="mode">

      <!-- DE Verarbeite den User-Agent-String -->
      <!-- EN processing user-agent-string-->
      <xsl:choose>

        <!-- DE Wenn sich MSIE > 5 finden läßt, dann handelt es sich um einen MS Internet Explorer 5+, d.h. CSS2 conform mit Fehlern -->
        <!-- EN If there is MSIE > 5, the we've have to avoid some bugs in MS Internet Explorer 5+ in CSS2 -->
        <xsl:when test="$msie_version >= '5' and $msie_version &lt; '7'">
          <xsl:text>msie</xsl:text>
        </xsl:when>

        <!-- DE Hier sind die gute Browser. Firefox, Netscape 6+, Opera 7+, Konqueror 2+ (Safari meldet sich als Mozilla/5.0)-->
        <!-- EN These are the good browsers: Firefox, Netscape 6+, Opera 7+, Konqueror 2+ (Safari) -->
        <xsl:when test="$mozilla_version >= '5' or $konqueror_version >= '2' or $opera_version1 >= '7' or $opera_version2 >= '7'">
          <xsl:text>css2conform</xsl:text>
        </xsl:when>

        <!-- DE In allen anderen Fällen, gehe davon aus, daß der Browser nur CSS 1 versteht -->
        <!-- EN Otherwise use css1 only-->
        <xsl:otherwise>
          <xsl:text>basic</xsl:text>
        </xsl:otherwise>

      </xsl:choose>

    </xsl:variable>

    <!-- DE Für alle Mediantypen -->
    <!-- EN For all media types -->
    <xsl:for-each select="document(concat($theme-prefix, '/settings/global.xml'))/settings/setting[@id='css/media']">

      <xsl:variable name="media" select="."/>

      <!-- DE Für alle Contenttypen -->
      <!-- DE ACHTUNG:
           ca_notes und com.arsdigita.cms.ReusableImageAsset werden im Rahmen dieses Themes nicht als 
           eigenständiger Contenttyp angesehen. Das Styling geschieht in den CSS-Dateien des übergeordneten
           Contenttypen, also zum Beispiel in Article
      -->
      <!-- EN For all contenttypes -->
      <!-- EN NOTICE:
           ca_notes and com.arsdigita.cms.ReusableImageAsset are not treated as contenttypes. Styling will be made vie css-files
           of contenttypes.
      -->
      <!-- ????????????????????????????????????????????????????????????????????????? -->
      <!--      <xsl:for-each select="'global' | //objectType[. != 'com.arsdigita.cms.ReusableImageAsset'] | //nav:item/nav:attribute[@name='objectType']"> -->

      <!-- DE Binde CSS-Dateien ein -->
      <!-- EN Loading CSS files -->
      <xsl:call-template name="mandalay:loadCSSFile">

        <!-- DE Setze den Mediantyp -->
        <!-- EN Set mediatype -->
        <xsl:with-param name="media" select="$media"/>

        <!-- DE Setze den Contenttyp -->
        <!-- EN Set content type -->
        <xsl:with-param name="content">
          <xsl:text>global</xsl:text>
          <!--
          <xsl:call-template name="concat('mandalay:CT_', substring-after(., 'contenttypes.'),'_getCSSPath')"/>
          -->
          <!--
                      <xsl:call-template name="mandalay:getSetting">
                        <xsl:with-param name="module" select="substring-after(., 'contenttypes.')"/>
                        <xsl:with-param name="setting" select="'csspath'"/>
                      </xsl:call-template>
          -->
        </xsl:with-param>

        <!-- DE Setze Modus -->
        <!-- EN Set mode -->
        <xsl:with-param name="mode" select="$mode"/>

        <!-- DE Lade admin.css bei Bedarf (useContext) -->
        <!-- EN Load admin.css if needed (useContext) -->

        <xsl:with-param name="admin">
          <xsl:choose>
            <xsl:when test="$resultTree/@class = 'cms-admin' or $resultTree/@application = 'admin' or $resultTree/@application = 'terms' or $resultTree/@application = 'sitemap'">
              <xsl:text>true</xsl:text>
            </xsl:when>
            <xsl:otherwise>
              <xsl:text>false</xsl:text>
            </xsl:otherwise>
          </xsl:choose>
        </xsl:with-param>

      </xsl:call-template>

      <!--      </xsl:for-each>-->
    </xsl:for-each>
  </xsl:template>

  <!-- DE Lade die nötigen CSS-Dateien abhängig vom Modus -->
  <!-- DE Parameter:
        media: css-Medientyp (entspricht Verzeichnisnamen)
        content: globale bzw. Contenttyp-bezogene CSS-Datei (enspricht Verzeichnisnamen)
        mode: Browsertyp bzw. Anzeigemode (css1conform, css2conform, msie)
        admin: boolean - spezielle CSS-Styles für die Admin-Oberfläche
  -->
  <!-- EN Loading css files depending on modus -->
  <!-- EN Parameter:
        media: css media type (directory name)
        content: global or content type css file (directory name)
        mode: browser type / view mode (css1conform, css2conform, msie)
        admin: boolean - special css styles for admin ui
  -->
  <xsl:template name="mandalay:loadCSSFile">
    <xsl:param name="media"/>
    <xsl:param name="content"/>
    <xsl:param name="mode"/>
    <xsl:param name="admin"/>

    <!-- DE Lade basic.css für alle Modes-->
    <!-- EN Load basic.css for all modes-->
    <link rel="stylesheet" type="text/css" href="{$theme-prefix}/css/{$media}/{$content}/basic.css" media="{$media}" />

    <!-- DE Lade css-Dateien für standardkonforme Browser und für MSIE-->
    <!-- EN Load css files for standard conform browser and MSIE-->
    <xsl:if test="$mode = 'css2conform' or $mode = 'msie'">
      <link rel="stylesheet" type="text/css" href="{$theme-prefix}/css/{$media}/{$content}/enhanced.css" media="{$media}" />
    </xsl:if>

    <!-- DE Für MSIE lade die ie-hack.css -->
    <!-- EN For MSIE load special ie-hack.css -->
    <xsl:if test="$mode = 'msie'">
      <link rel="stylesheet" type="text/css" href="{$theme-prefix}/css/{$media}/{$content}/ie-hacks.css" media="{$media}" />
    </xsl:if>

    <!-- DE Für Admin lade die admin.css -->
    <!-- EN For admin load admin.css -->
    <xsl:if test="$admin = 'true'">
      <link rel="stylesheet" type="text/css" href="{$theme-prefix}/css/{$media}/{$content}/admin.css" media="{$media}" />
    </xsl:if>

  </xsl:template>

</xsl:stylesheet>
