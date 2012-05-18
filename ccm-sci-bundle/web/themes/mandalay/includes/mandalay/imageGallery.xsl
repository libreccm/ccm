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
  Dies ist die Importdatei für {Name}. Sie importiert alle XSL-Dateien aus
  dem Unterverzeichnis {Dir}. Dies ist der einzige Ort in diesem Theme,
  indem die Dateien importiert werden dürfen.
-->

<!-- EN
  This is the import file for {Name}. It is importing all xsl files from
  the {Dir} subfolder. This is the only place in this theme where these
  files are allowed to be imported.
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
  
  <xsl:template name="mandalay:imageGallery">

    <xsl:if test="count($resultTree//cms:item/image | $resultTree//cms:item/imageAttachments/image) > 1">
      <script type="text/javascript" src="/assets/jquery.js"/>
      <script type="text/javascript" src="/assets/fancybox/jquery.fancybox.pack.js"/>
      <script type="text/javascript" src="/assets/fancybox/jquery.easing.pack.js"/>
      <script type="text/javascript" src="/assets/fancybox/jquery.mousewheel.pack.js"/>
      <link rel="stylesheet" href="/assets/fancybox/jquery.fancybox-1.3.4.css" type="text/css" media="screen"/>
    </xsl:if>
<!--
    <xsl:variable name="firstMatch">
      <xsl:value-of select="//imageAttachment/@name"/>
    </xsl:variable>
    
    <xsl:if test="@name=$firstMatch">
      <script type="text/javascript" src="/assets/fancyBox/jquery.fancybox.pack.js"/>
  
      <xsl:if test="$setTransition != 'linear' and $setTransition != 'swing'">
        <script type="text/javascript" src="/assets/fancyBox/jquery.easing.pack.js"/>
      </xsl:if>
  
      <xsl:if test="setMouseWheel = 'true'">
        <script type="text/javascript" src="/assets/fancyBox/jquery.mousewheel.pack.js"/>
      </xsl:if>
      
    </xsl:if>
-->    
  </xsl:template>
  
  <xsl:template name="mandalay:imageGallerySetup">
    <xsl:param name="imageGallery" select="'imageGallery'"/>

    <xsl:variable name="firstMatch">
      <xsl:choose>
        <xsl:when test="$imageGallery = 'imageGallery'">
          <xsl:value-of select="//imageAttachments[not(useContext)]/id"/>
        </xsl:when>
        <xsl:otherwise>
          <xsl:value-of select="//imageAttachments[useContext = substring-after($imageGallery, '_')]/id"/>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:variable>
    
    <xsl:if test="../id = $firstMatch">
      <script type="text/javascript">
        $(document).ready(function() {
          $("a.<xsl:value-of select="$imageGallery"/>").fancybox({
            <xsl:variable name="config">
              <xsl:choose>
                <xsl:when test="document(concat($theme-prefix, '/settings/', $imageGallery, '.xml'))">
                  <xsl:value-of select="concat($theme-prefix, '/settings/', $imageGallery, '.xml')"/>
                </xsl:when>
                <xsl:otherwise>
                  <xsl:value-of select="concat($theme-prefix, '/settings/imageGallery.xml')"/>
                </xsl:otherwise>
              </xsl:choose>
            </xsl:variable>
            
            <xsl:for-each select="document($config)/settings/setting">
              <xsl:call-template name="mandalay:setImageGalleryConfigParam">
                <xsl:with-param name="settingName" select="@id"/>
                <xsl:with-param name="value" select="."/>
              </xsl:call-template>
            </xsl:for-each>
          
            <!-- DE Hack, um ein korrektes Array zu erzeugen. Es gibt leider keine Möglichkeit
                 das Komma hinter den letzten Eintrag wegzulassen, daher wird hier ein "leerer"
                 Eintrag angelegt, den fancybox ignoriert. -->
            <xsl:text>'dummyEntry' : 'ignoreMe'</xsl:text>
          });
        });
      </script>
    </xsl:if>
  </xsl:template>

  <xsl:template name="mandalay:setImageGalleryConfigParam">
    <xsl:param name="settingName" select="''"/>
    <xsl:param name="value" select="''"/>

    <xsl:variable name="defaultValue">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="module"  select="'imageGalleryDefaults'"/>
        <xsl:with-param name="setting" select="$settingName"/>
        <xsl:with-param name="default" select="''"/>
      </xsl:call-template>
    </xsl:variable>
    
    <xsl:variable name="type">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="module"  select="'imageGalleryDefaults'"/>
        <xsl:with-param name="setting" select="concat($settingName, '/type')"/>
      </xsl:call-template>
    </xsl:variable>
<!--    
    <xsl:variable name="setting">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="module"  select="'imageGallery'"/>
        <xsl:with-param name="setting" select="$settingName"/>
        <xsl:with-param name="default" select="$defaultValue"/>
      </xsl:call-template>
    </xsl:variable>
-->

    <xsl:if test="$value != '' and $value != $defaultValue">
      <xsl:choose>
        <xsl:when test="$type = 'boolean' or $type = 'number'">
          <xsl:text>'</xsl:text>
          <xsl:value-of select="$settingName"/>
          <xsl:text>' : </xsl:text>
          <xsl:value-of select="$value"/>
          <xsl:text>,</xsl:text>
        </xsl:when>
        <xsl:otherwise>
          <xsl:text>'</xsl:text>
          <xsl:value-of select="$settingName"/>
          <xsl:text>' : '</xsl:text>
          <xsl:value-of select="$value"/>
          <xsl:text>',</xsl:text>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:if>

  </xsl:template>
</xsl:stylesheet>