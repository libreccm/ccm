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
Template für die Verabeitung von imageAttachments in Contenttypen
Parameter showCaption: boolean zum Anzeigen von ImageCaptions, falls vorhanden
-->

<!-- EN
Processing imageAttachments in contenttypes
Parameter showCaption: boolean to sshow caption
-->

<!-- Autor: Sören Bernstein -->

<xsl:stylesheet
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:cms="http://www.arsdigita.com/cms/1.0" 
  xmlns:mandalay="http://mandalay.quasiweb.de"
  exclude-result-prefixes="cms mandalay xsl"
  version="1.0">

  <xsl:template name="mandalay:imageAttachment">
    <xsl:param name="showCaption"/>
    <xsl:param name="maxWidth"/>
    <xsl:param name="maxHeight"/>
    <xsl:param name="setZoomLink" select="'false'"/>
    <xsl:param name="node" select="./imageAttachments"/>
    <xsl:param name="useContext"/>
    
    <xsl:choose>
      <xsl:when test="$useContext">
        <xsl:for-each select="$node[useContext = $useContext]">
          <xsl:apply-templates select="image">
            <xsl:with-param name="maxWidth" select="$maxWidth"/>
            <xsl:with-param name="maxHeight" select="$maxHeight"/>
            <xsl:with-param name="showCaption" select="$showCaption"/>
            <xsl:with-param name="setZoomLink" select="$setZoomLink"/>
          </xsl:apply-templates>
        </xsl:for-each>
      </xsl:when>

      <xsl:otherwise>
        <xsl:for-each select="$node[not(useContext)]">
          <xsl:apply-templates select="image">
            <xsl:with-param name="maxWidth" select="$maxWidth"/>
            <xsl:with-param name="maxHeight" select="$maxHeight"/>
            <xsl:with-param name="showCaption" select="$showCaption"/>
            <xsl:with-param name="setZoomLink" select="$setZoomLink"/>
          </xsl:apply-templates>
        </xsl:for-each>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  <xsl:template match="image | thumbnail">
    <xsl:param name="maxWidth"/>
    <xsl:param name="maxHeight"/>
    <xsl:param name="showCaption"/>
    <xsl:param name="setZoomLink"/>
    
    <!-- DE Beschränke Bildgröße proportional auf maxWidth und maxHeight -->
    <!-- EN Limiting image size to maxWidth and maxHeight. Keep aspect ratio -->
    <xsl:variable name="width">
      <xsl:choose>
        <xsl:when
          test="$maxWidth != '' and $maxHeight != '' and width > $maxWidth and height > $maxHeight">
          <xsl:if test="$maxWidth div width > $maxHeight div height">
            <xsl:value-of select="$maxHeight div height * width"/>
          </xsl:if>
          <xsl:if test="$maxHeight div height >= $maxWidth div width">
            <xsl:value-of select="$maxWidth"/>
          </xsl:if>
        </xsl:when>
        <xsl:when test="$maxWidth != '' and width > $maxWidth">
          <xsl:value-of select="$maxWidth"/>
        </xsl:when>
        <xsl:when test="$maxHeight != '' and height > $maxHeight ">
          <xsl:value-of select="$maxHeight div height * width"/>
        </xsl:when>
        <xsl:otherwise>
          <xsl:value-of select="width"/>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:variable>

    <xsl:variable name="height">
      <xsl:choose>
        <xsl:when
          test="$maxWidth != '' and $maxHeight != '' and width > $maxWidth and height > $maxHeight">
          <xsl:if test="$maxHeight div height > $maxWidth div width">
            <xsl:value-of select="$maxWidth div width * height"/>
          </xsl:if>
          <xsl:if test="$maxWidth div width >= $maxHeight div height">
            <xsl:value-of select="$maxHeight"/>
          </xsl:if>
        </xsl:when>
        <xsl:when test="$maxHeight != '' and height > $maxHeight">
          <xsl:value-of select="$maxHeight"/>
        </xsl:when>
        <xsl:when test="$maxWidth != '' and width > $maxWidth ">
          <xsl:value-of select="$maxWidth div width * height"/>
        </xsl:when>
        <xsl:otherwise>
          <xsl:value-of select="height"/>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:variable>

    <div class="image">
      <a>
        <xsl:attribute name="href">
          <xsl:value-of select="$dispatcher-prefix"/>/cms-service/stream/image/?image_id=<xsl:value-of select="id"/>
        </xsl:attribute>
        <xsl:variable name="imageGallery">
          <xsl:choose>
            <xsl:when test="count(//cms:item/image | //cms:item/imageAttachments/image) > 1">
              <xsl:text>imageGallery</xsl:text>
              <xsl:if test="../useContext and ../useContext != ''">
                <xsl:value-of select="concat('_', ../useContext)"/>
              </xsl:if>
            </xsl:when>
            <xsl:otherwise>
              <xsl:text>imageZoom</xsl:text>
            </xsl:otherwise>
          </xsl:choose>
        </xsl:variable>
        <xsl:attribute name="class">
          <xsl:value-of select="$imageGallery"/>
        </xsl:attribute>
        <xsl:if test="$imageGallery != 'imageZoom'">
          <xsl:attribute name="rel">
            <xsl:value-of select="$imageGallery"/>
          </xsl:attribute>
        </xsl:if>
        <xsl:call-template name="mandalay:imageGallerySetup">
          <xsl:with-param name="imageGallery" select="$imageGallery"/>
        </xsl:call-template>
        <img>
          <xsl:attribute name="src">
            <xsl:value-of select="$dispatcher-prefix"/>/cms-service/stream/image/?image_id=<xsl:value-of select="id"/>
          </xsl:attribute>
          <xsl:attribute name="alt">
            <xsl:value-of select="../caption"/>
          </xsl:attribute>
          <xsl:attribute name="title">
            <xsl:value-of select="../caption"/>
          </xsl:attribute>
          <xsl:attribute name="width">
            <xsl:value-of select="$width"/>
          </xsl:attribute>
          <xsl:attribute name="height">
            <xsl:value-of select="$height"/>
          </xsl:attribute>
        </img>
      </a>
      <xsl:if
        test="($showCaption='true' and ../caption) or ($setZoomLink = 'true' and (width != $width or height != $height))">
        <span class="caption" style="width: {$width}px">
          <xsl:choose>
            <xsl:when test="$setZoomLink = 'true' and (width != $width or height != $height)">
              <a>
                <xsl:attribute name="href">
                  <xsl:value-of select="$dispatcher-prefix"/>/cms-service/stream/image/?image_id=<xsl:value-of select="id"/>
                </xsl:attribute>
                <xsl:choose>
                  <xsl:when test="not(../caption) or $showCaption='false'">
                    <xsl:text>ZOOM</xsl:text>
                  </xsl:when>
                  <xsl:otherwise>
                    <xsl:value-of select="../caption"/>
                  </xsl:otherwise>
                </xsl:choose>
              </a>
            </xsl:when>
            <xsl:otherwise>
              <xsl:value-of select="../caption"/>
            </xsl:otherwise>
          </xsl:choose>
        </span>
      </xsl:if>
    </div>
  </xsl:template>

</xsl:stylesheet>
