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

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:bebop="http://www.arsdigita.com/bebop/1.0" 
  xmlns:cms="http://www.arsdigita.com/cms/1.0"
  xmlns:nav="http://ccm.redhat.com/navigation" 
  xmlns:mandalay="http://mandalay.quasiweb.de"
  exclude-result-prefixes="xsl bebop cms" version="1.0">
  
  <!-- DE Vollansicht -->
  <!-- EN Detailed view -->
  <xsl:template name="CT_Image_graphics"
    match="cms:item[objectType='com.arsdigita.cms.contenttypes.Image']" mode="detailed_view">
    
    <!-- DE Hole alle benötigten Einstellungen-->
    <!-- EN Getting all needed setting-->
    <xsl:variable name="setLeadText">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="module" select="'Image'"/>
        <xsl:with-param name="setting" select="'setLeadText'"/>
        <xsl:with-param name="default" select="'true'"/>
      </xsl:call-template>
    </xsl:variable>
    <xsl:variable name="setImage">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="module" select="'Image'"/>
        <xsl:with-param name="setting" select="'setImage'"/>
        <xsl:with-param name="default" select="'true'"/>
      </xsl:call-template>
    </xsl:variable>
    <xsl:variable name="setImageMaxHeight">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="module" select="'Image'"/>
        <xsl:with-param name="setting" select="'setImageMaxHeight'"/>
        <xsl:with-param name="default" select="''"/>
      </xsl:call-template>
    </xsl:variable>
    <xsl:variable name="setImageMaxWidth">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="module" select="'Image'"/>
        <xsl:with-param name="setting" select="'setImageMaxWidth'"/>
        <xsl:with-param name="default" select="''"/>
      </xsl:call-template>
    </xsl:variable>
    <xsl:variable name="setImageCaption">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="module" select="'Image'"/>
        <xsl:with-param name="setting" select="'setImageCaption'"/>
        <xsl:with-param name="default" select="'true'"/>
      </xsl:call-template>
    </xsl:variable>
    <xsl:variable name="setShowKeys">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="module" select="'Image'"/>
        <xsl:with-param name="setting" select="'setShowKeys'"/>
        <xsl:with-param name="default" select="'true'"/>
      </xsl:call-template>
    </xsl:variable>
    <xsl:variable name="setShowEmptyEntry">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="module" select="'Image'"/>
        <xsl:with-param name="setting" select="'setShowEmptyEntry'"/>
        <xsl:with-param name="default" select="'true'"/>
      </xsl:call-template>
    </xsl:variable>
    
    <div id="greeting">
      <xsl:if test="$setImage = 'true'">
        <xsl:call-template name="mandalay:imageAttachment">
          <xsl:with-param name="showCaption" select="$setImageCaption"/>
          <xsl:with-param name="maxWidth" select="$setImageMaxWidth"/>
          <xsl:with-param name="maxHeight" select="$setImageMaxHeight"/>
          <xsl:with-param name="setZoomLink" select="'true'"/>
          <xsl:with-param name="node" select=". | ./imageAttachments"/>
        </xsl:call-template>
      </xsl:if>
      
      <xsl:if test="./description and $setLeadText = 'true'">
        <div id="lead">
          <xsl:variable name="description">
            <xsl:call-template name="mandalay:string-replace">
              <xsl:with-param name="string" select="./description"/>
              <xsl:with-param name="from" select="'&#xA;'"/>
              <xsl:with-param name="to" select="'&lt;br/>'"/>
            </xsl:call-template>
          </xsl:variable>
          <xsl:value-of disable-output-escaping="yes" select="$description"/>
        </div>
      </xsl:if>
    </div>
    
    <div id="mainBody">
      <div class="details table">
        <xsl:if test="(image/width and image/height) or $setShowEmptyEntry = 'true'">
          <div class="tableRow">
            <xsl:if test="$setShowKeys = 'true'">
              <span class="key">
                <xsl:call-template name="mandalay:getStaticText">
                  <xsl:with-param name="module" select="'Image'"/>
                  <xsl:with-param name="id" select="'dimensions'"/>
                </xsl:call-template>
              </span>
            </xsl:if>
            <span class="value">
              <xsl:value-of select="image/width"/>
                x
              <xsl:value-of select="image/height"/>
            </span>
          </div>
        </xsl:if>
  <!--
        <xsl:if test="caption or $setShowEmptyEntry = 'true'">
          <div class="tableRow">
            <xsl:if test="$setShowKeys='true'">
              <span class="key">
                <xsl:call-template name="mandalay:getStaticText">
                  <xsl:with-param name="module" select="'Image'"/>
                  <xsl:with-param name="id" select="'caption'"/>
                </xsl:call-template>
              </span>
            </xsl:if>
            <span class="value">
              <xsl:value-of select="caption"/>
            </span>
          </div>
        </xsl:if>
  -->
        <xsl:if test="artist or $setShowEmptyEntry = 'true'">
          <div class="tableRow">
            <xsl:if test="$setShowKeys='true'">
              <span class="key">
                <xsl:call-template name="mandalay:getStaticText">
                  <xsl:with-param name="module" select="'Image'"/>
                  <xsl:with-param name="id" select="'artist'"/>
                </xsl:call-template>
              </span>
            </xsl:if>
            <span class="value">
              <xsl:value-of select="artist"/>
            </span>
          </div>
        </xsl:if>
        <xsl:if test="publishDate or $setShowEmptyEntry = 'true'">
          <div class="tableRow">
            <xsl:if test="publishDate or $setShowKeys='true'">
              <span class="key">
                <xsl:call-template name="mandalay:getStaticText">
                  <xsl:with-param name="module" select="'Image'"/>
                  <xsl:with-param name="id" select="'publishDate'"/>
                </xsl:call-template>
              </span>
            </xsl:if>
            <span class="value">
              <xsl:choose>
                <xsl:when test="skipDay = 'true' or skipMonth = 'true'">
                  <xsl:call-template name="mandalay:getStaticText">
                    <xsl:with-param name="module" select="'Image'"/>
                    <xsl:with-param name="id" select="'approx'"/>
                  </xsl:call-template>
		  <xsl:value-of select="publishDate/@year"/>
                </xsl:when>
                <xsl:otherwise>
                  <xsl:value-of select="publishDate"/>
                </xsl:otherwise>
              </xsl:choose>
            </span>
          </div>
        </xsl:if>
        <xsl:if test="source or $setShowEmptyEntry = 'true'">
          <div class="tableRow">
            <xsl:if test="$setShowKeys='true'">
              <span class="key">
                <xsl:call-template name="mandalay:getStaticText">
                  <xsl:with-param name="module" select="'Image'"/>
                  <xsl:with-param name="id" select="'source'"/>
                </xsl:call-template>
              </span>
            </xsl:if>
            <span class="value">
              <xsl:value-of select="source"/>
            </span>
          </div>
        </xsl:if>
        <xsl:if test="media or $setShowEmptyEntry = 'true'">
          <div class="tableRow">
            <xsl:if test="$setShowKeys='true'">
              <span class="key">
                <xsl:call-template name="mandalay:getStaticText">
                  <xsl:with-param name="module" select="'Image'"/>
                  <xsl:with-param name="id" select="'media'"/>
                </xsl:call-template>
              </span>
            </xsl:if>
            <span class="value">
              <xsl:value-of select="media"/>
            </span>
          </div>
        </xsl:if>
        <xsl:if test="copyright or $setShowEmptyEntry = 'true'">
          <div class="tableRow">
            <xsl:if test="$setShowKeys='true'">
              <span class="key">
                <xsl:call-template name="mandalay:getStaticText">
                  <xsl:with-param name="module" select="'Image'"/>
                  <xsl:with-param name="id" select="'copyright'"/>
                </xsl:call-template>
              </span>
            </xsl:if>
            <span class="value">
              <xsl:value-of select="copyright"/>
            </span>
          </div>
        </xsl:if>
        <xsl:if test="site or $setShowEmptyEntry = 'true'">
          <div class="tableRow">
            <xsl:if test="$setShowKeys='true'">
              <span class="key">
                <xsl:call-template name="mandalay:getStaticText">
                  <xsl:with-param name="module" select="'Image'"/>
                  <xsl:with-param name="id" select="'site'"/>
                </xsl:call-template>
              </span>
            </xsl:if>
            <span class="value">
              <xsl:value-of select="site"/>
            </span>
          </div>
        </xsl:if>
        <xsl:if test="license or $setShowEmptyEntry = 'true'">
          <div class="tableRow">
            <xsl:if test="$setShowKeys='true'">
              <span class="key">
                <xsl:call-template name="mandalay:getStaticText">
                  <xsl:with-param name="module" select="'Image'"/>
                  <xsl:with-param name="id" select="'license'"/>
                </xsl:call-template>
              </span>
            </xsl:if>
            <span class="value">
              <xsl:value-of select="license"/>
            </span>
          </div>
        </xsl:if>
        <xsl:if test="material or $setShowEmptyEntry = 'true'">
          <div class="tableRow">
            <xsl:if test="$setShowKeys='true'">
              <span class="key">
                <xsl:call-template name="mandalay:getStaticText">
                  <xsl:with-param name="module" select="'Image'"/>
                  <xsl:with-param name="id" select="'material'"/>
                </xsl:call-template>
              </span>
            </xsl:if>
            <span class="value">
              <xsl:value-of select="material"/>
            </span>
          </div>
        </xsl:if>
        <xsl:if test="technique or $setShowEmptyEntry = 'true'">
          <div class="tableRow">
            <xsl:if test="$setShowKeys='true'">
              <span class="key">
                <xsl:call-template name="mandalay:getStaticText">
                  <xsl:with-param name="module" select="'Image'"/>
                  <xsl:with-param name="id" select="'technique'"/>
                </xsl:call-template>
              </span>
            </xsl:if>
            <span class="value">
              <xsl:value-of select="technique"/>
            </span>
          </div>
        </xsl:if>
        <xsl:if test="origin or $setShowEmptyEntry = 'true'">
          <div class="tableRow">
            <xsl:if test="$setShowKeys='true'">
              <span class="key">
                <xsl:call-template name="mandalay:getStaticText">
                  <xsl:with-param name="module" select="'Image'"/>
                  <xsl:with-param name="id" select="'origin'"/>
                </xsl:call-template>
              </span>
            </xsl:if>
            <span class="value">
              <xsl:value-of select="origin"/>
            </span>
          </div>
        </xsl:if>
        <xsl:if test="origSize or $setShowEmptyEntry = 'true'">
          <div class="tableRow">
            <xsl:if test="$setShowKeys='true'">
              <span class="key">
                <xsl:call-template name="mandalay:getStaticText">
                  <xsl:with-param name="module" select="'Image'"/>
                  <xsl:with-param name="id" select="'origSize'"/>
                </xsl:call-template>
              </span>
            </xsl:if>
            <span class="value">
              <xsl:value-of select="origSize"/>
            </span>
          </div>
        </xsl:if>
      </div>
      <xsl:value-of disable-output-escaping="yes" select="./textAsset/content"/>
    </div>
    <div class="endFloat"/>
  </xsl:template>
  
  <!-- DE Listenansicht -->
  <!-- EN List view -->
  <xsl:template name="CT_Image_List"
    match="nav:item[nav:attribute[@name='objectType'] = 'com.arsdigita.cms.contenttypes.Image']"
    mode="list_view">
    
    <!-- DE Hole alle benötigten Einstellungen-->
    <!-- EN Getting all needed setting-->
    <xsl:variable name="setLeadText">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="module" select="'Image'"/>
        <xsl:with-param name="setting" select="'listView/setLeadText'"/>
        <xsl:with-param name="default" select="'true'"/>
      </xsl:call-template>
    </xsl:variable>
    <xsl:variable name="setLeadTextLength">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="module" select="'Image'"/>
        <xsl:with-param name="setting" select="'listView/setLeadTextLength'"/>
        <xsl:with-param name="default" select="'0'"/>
      </xsl:call-template>
    </xsl:variable>
    <xsl:variable name="setMoreButton">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="module" select="'Image'"/>
        <xsl:with-param name="setting" select="'listView/setMoreButton'"/>
        <xsl:with-param name="default" select="'false'"/>
      </xsl:call-template>
    </xsl:variable>
    <xsl:variable name="setThumbnail">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="module" select="'Image'"/>
        <xsl:with-param name="setting" select="'listView/setThumbnail'"/>
        <xsl:with-param name="default" select="'true'"/>
      </xsl:call-template>
    </xsl:variable>
    <xsl:variable name="setShowKeys">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="module" select="'Image'"/>
        <xsl:with-param name="setting" select="'listView/setShowKeys'"/>
        <xsl:with-param name="default" select="'true'"/>
      </xsl:call-template>
    </xsl:variable>
    <xsl:variable name="setShowEmptyEntry">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="module" select="'Image'"/>
        <xsl:with-param name="setting" select="'listView/setShowEmptyEntry'"/>
        <xsl:with-param name="default" select="'false'"/>
      </xsl:call-template>
    </xsl:variable>
    
    <a class="CIname">
      <xsl:attribute name="href">
        <xsl:value-of select="nav:path"/>
      </xsl:attribute>
      <xsl:attribute name="title">
        <xsl:call-template name="mandalay:shying">
          <xsl:with-param name="title"><xsl:value-of select="nav:attribute[@name='title']"/></xsl:with-param>
          <xsl:with-param name="mode">dynamic</xsl:with-param>
        </xsl:call-template>
      </xsl:attribute>
      <xsl:if test="nav:attribute[@name='thumbnail.id']!='' and $setThumbnail='true'">
        <img>
          <xsl:attribute name="width">
            <xsl:value-of select="thumbnail.width"/>
          </xsl:attribute>
          <xsl:attribute name="height">
            <xsl:value-of select="thumbnail.height"/>
          </xsl:attribute>
          <xsl:attribute name="title">
            <xsl:value-of select="title"/>
          </xsl:attribute>
          <xsl:attribute name="src">
            <xsl:value-of select="$dispatcher-prefix"/>/cms-service/stream/image/?image_id=<xsl:value-of select="nav:attribute[@name='thumbnail.id']"/>
          </xsl:attribute>
        </img>
      </xsl:if>
      <xsl:call-template name="mandalay:shying">
        <xsl:with-param name="title"><xsl:value-of disable-output-escaping="yes" select="nav:attribute[@name='title']"/></xsl:with-param>
        <xsl:with-param name="mode">dynamic</xsl:with-param>
      </xsl:call-template>
    </a>
    
    <xsl:if test="nav:attribute[@name='artist'] or $setShowEmptyEntry = 'true'">
      <div class="tableRow">
        <xsl:if test="$setShowKeys='true'">
          <span class="key">
            <xsl:call-template name="mandalay:getStaticText">
              <xsl:with-param name="module" select="'Image'"/>
              <xsl:with-param name="id" select="'artist'"/>
            </xsl:call-template>
          </span>
        </xsl:if>
        <span class="value">
          <xsl:value-of select="nav:attribute[@name='artist']"/>
        </span>
      </div>
    </xsl:if>
    <xsl:if test="nav:attribute[@name='source'] or $setShowEmptyEntry = 'true'">
      <div class="tableRow">
        <xsl:if test="$setShowKeys='true'">
          <span class="key">
            <xsl:call-template name="mandalay:getStaticText">
              <xsl:with-param name="module" select="'Image'"/>
              <xsl:with-param name="id" select="'source'"/>
            </xsl:call-template>
          </span>
        </xsl:if>
        <span class="value">
          <xsl:value-of select="nav:attribute[@name='source']"/>
        </span>
      </div>
    </xsl:if>
    <xsl:if test="nav:attribute[@name='publishDate'] or $setShowEmptyEntry = 'true'">
      <div class="tableRow">
        <xsl:if test="$setShowKeys='true'">
          <span class="key">
            <xsl:call-template name="mandalay:getStaticText">
              <xsl:with-param name="module" select="'Image'"/>
              <xsl:with-param name="id" select="'publishDate'"/>
            </xsl:call-template>
          </span>
        </xsl:if>
        <span class="value">
          <xsl:choose>
            <xsl:when test="nav:attribute[@name='skipDay'] = 'true' or nav:attribute[@name='skipMonth'] = 'true'">
              <xsl:call-template name="mandalay:getStaticText">
                <xsl:with-param name="module" select="'Image'"/>
                <xsl:with-param name="id" select="'approx'"/>
              </xsl:call-template>
              <xsl:value-of select="nav:attribute[@name='publishDate']/@year"/>
            </xsl:when>
            <xsl:otherwise>
              <xsl:value-of select="nav:attribute[@name='publishDate']/@date"/>
            </xsl:otherwise>
          </xsl:choose>
        </span>
      </div>
    </xsl:if>
    
    <xsl:if test="nav:attribute[@name='description'] and $setLeadText = 'true'">
      <br/>
      <span class="intro">
        <xsl:choose>
          <xsl:when test="$setLeadTextLength = '0'">
            <xsl:value-of disable-output-escaping="yes" select="nav:attribute[@name='description']"/>
          </xsl:when>
          <xsl:otherwise>
            <xsl:value-of disable-output-escaping="yes" select="substring(nav:attribute[@name='description'], 1, $setLeadTextLength)"/>
            <xsl:if test="string-length(nav:attribute[@name='description']) > $setLeadTextLength">
              <xsl:text>...</xsl:text>
              <xsl:if test="$setMoreButton = 'true'">
                <span class="moreButton">
                  <a>
                    <xsl:attribute name="href">
                      <xsl:value-of select="nav:path"/>
                    </xsl:attribute>
                    <xsl:attribute name="title">
                      <xsl:call-template name="mandalay:getStaticText">
                        <xsl:with-param name="module" select="'Image'"/>
                        <xsl:with-param name="id" select="'moreButtonTitle'"/>
                      </xsl:call-template>
                    </xsl:attribute>
                    <xsl:call-template name="mandalay:getStaticText">
                      <xsl:with-param name="module" select="'Image'"/>
                      <xsl:with-param name="id" select="'moreButton'"/>
                    </xsl:call-template>
                  </a>
                </span>
              </xsl:if>
            </xsl:if>
          </xsl:otherwise>
        </xsl:choose>
      </span>
    </xsl:if>
  </xsl:template>
  
  <xsl:template name="CT_Image_Link"
    match="*/cms:item/links[targetItem/objectType = 'com.arsdigita.cms.contenttypes.Image']"
    mode="link_view">
    
    <!-- DE Hole alle benötigten Einstellungen-->
    <!-- EN Getting all needed setting-->
    <xsl:variable name="setImageAndText">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="module" select="'Image'"/>
        <xsl:with-param name="setting" select="'linkView/setImageAndText'"/>
        <xsl:with-param name="default" select="'true'"/>
      </xsl:call-template>
    </xsl:variable>
    <xsl:variable name="setImage">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="module" select="'Image'"/>
        <xsl:with-param name="setting" select="'linkView/setImage'"/>
        <xsl:with-param name="default" select="'true'"/>
      </xsl:call-template>
    </xsl:variable>
    <xsl:variable name="setImageMaxHeight">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="module" select="'Image'"/>
        <xsl:with-param name="setting" select="'linkView/setImageMaxHeight'"/>
        <xsl:with-param name="default" select="''"/>
      </xsl:call-template>
    </xsl:variable>
    <xsl:variable name="setImageMaxWidth">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="module" select="'Image'"/>
        <xsl:with-param name="setting" select="'linkView/setImageMaxWidth'"/>
        <xsl:with-param name="default" select="''"/>
      </xsl:call-template>
    </xsl:variable>
    <xsl:variable name="setImageCaption">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="module" select="'Image'"/>
        <xsl:with-param name="setting" select="'linkView/setImageCaption'"/>
        <xsl:with-param name="default" select="'true'"/>
      </xsl:call-template>
    </xsl:variable>
    <xsl:variable name="setDescription">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="module" select="'Image'"/>
        <xsl:with-param name="setting" select="'linkView/setDescription'"/>
        <xsl:with-param name="default" select="'true'"/>
      </xsl:call-template>
    </xsl:variable>
    <xsl:variable name="setDescriptionLength">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="module" select="'Image'"/>
        <xsl:with-param name="setting" select="'linkView/setDescriptionLength'"/>
        <xsl:with-param name="default" select="'0'"/>
      </xsl:call-template>
    </xsl:variable>
    <xsl:variable name="setMoreButton">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="module" select="'Image'"/>
        <xsl:with-param name="setting" select="'linkView/setMoreButton'"/>
        <xsl:with-param name="default" select="'false'"/>
      </xsl:call-template>
    </xsl:variable>
    
    <!-- DE Wenn es Bilder gibt, dann soll das erste hier als Link angezeigt werden -->
    <!-- EN -->
    <xsl:if test="./targetItem/imageAttachments and $setImage = 'true'">
      <a class="CIname">
        <xsl:attribute name="href">
          <xsl:text>/redirect/?oid=</xsl:text>
          <xsl:value-of select="./targetItem/@oid"/>
        </xsl:attribute>
        <xsl:attribute name="title">
          <xsl:call-template name="mandalay:shying">
            <xsl:with-param name="title">
              <xsl:value-of select="./linkTitle"/>
            </xsl:with-param>
            <xsl:with-param name="mode">dynamic</xsl:with-param>
          </xsl:call-template>
        </xsl:attribute>
        <xsl:for-each select="./targetItem">
          <xsl:call-template name="mandalay:imageAttachment">
            <xsl:with-param name="showCaption" select="$setImageCaption"/>
            <xsl:with-param name="maxHeight" select="$setImageMaxHeight"/>
            <xsl:with-param name="maxWidth" select="$setImageMaxWidth"/>
          </xsl:call-template>
        </xsl:for-each>
      </a>
    </xsl:if>
    <xsl:if
      test="$setImageAndText = 'true' or not(./targetItem/imageAttachments) or $setImage = 'false'">
      <a class="CIname">
        <xsl:attribute name="href">
          <xsl:text>/redirect/?oid=</xsl:text>
          <xsl:value-of select="./targetItem/@oid"/>
        </xsl:attribute>
        <xsl:attribute name="title">
          <xsl:call-template name="mandalay:shying">
            <xsl:with-param name="title">
              <xsl:value-of select="./linkTitle"/>
            </xsl:with-param>
            <xsl:with-param name="mode">dynamic</xsl:with-param>
          </xsl:call-template>
        </xsl:attribute>
        <xsl:call-template name="mandalay:shying">
          <xsl:with-param name="title">
            <xsl:value-of disable-output-escaping="yes" select="./linkTitle"/>
          </xsl:with-param>
          <xsl:with-param name="mode">dynamic</xsl:with-param>
        </xsl:call-template>
      </a>
      <xsl:if test="./linkDescription and $setDescription">
        <br/>
        <xsl:choose>
          <xsl:when test="$setDescriptionLength = '0'">
            <xsl:value-of disable-output-escaping="yes" select="./linkDescription"/>
          </xsl:when>
          <xsl:otherwise>
            <xsl:value-of disable-output-escaping="yes"
              select="substring(./linkDescription, 1, $setDescriptionLength)"/>
            <xsl:if test="string-length(./linkDescription) > $setDescriptionLength">
              <xsl:text>...</xsl:text>
              <xsl:if test="$setMoreButton = 'true'">
                <span class="moreButton">
                  <a>
                    <xsl:attribute name="href">
                      <xsl:text>/redirect/?oid=</xsl:text>
                      <xsl:value-of select="./targetItem/@oid"/>
                    </xsl:attribute>
                    <xsl:attribute name="title">
                      <xsl:call-template name="mandalay:getStaticText">
                        <xsl:with-param name="module" select="'Image'"/>
                        <xsl:with-param name="id" select="'moreButtonTitle'"/>
                      </xsl:call-template>
                    </xsl:attribute>
                    <xsl:call-template name="mandalay:getStaticText">
                      <xsl:with-param name="module" select="'Image'"/>
                      <xsl:with-param name="id" select="'moreButton'"/>
                    </xsl:call-template>
                  </a>
                </span>
              </xsl:if>
            </xsl:if>
          </xsl:otherwise>
        </xsl:choose>
      </xsl:if>
    </xsl:if>
  </xsl:template>
  
</xsl:stylesheet>
