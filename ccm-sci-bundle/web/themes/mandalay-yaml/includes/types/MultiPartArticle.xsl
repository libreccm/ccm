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

<xsl:stylesheet 
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
  xmlns:bebop="http://www.arsdigita.com/bebop/1.0"
  xmlns:cms="http://www.arsdigita.com/cms/1.0"
  xmlns:nav="http://ccm.redhat.com/navigation"
  xmlns:ui="http://www.arsdigita.com/ui/1.0"
  xmlns:mandalay="http://mandalay.quasiweb.de"
  exclude-result-prefixes="xsl bebop cms ui"
  version="1.0">

  <!-- DE Leadtext -->
  <!-- EN lead text view -->
  <xsl:template match="cms:item[objectType='com.arsdigita.cms.contenttypes.MultiPartArticle']" mode="lead">
    <xsl:variable name="setLeadText">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="module"  select="'MultiPartArticle'"/>
        <xsl:with-param name="setting" select="'setLeadText'"/>
        <xsl:with-param name="default" select="'true'"/>
      </xsl:call-template>
    </xsl:variable>
    <xsl:variable name="setSummary">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="module"  select="'MultiPartArticle'"/>
        <xsl:with-param name="setting" select="'setSummary'"/>
        <xsl:with-param name="default" select="'true'"/>
      </xsl:call-template>
    </xsl:variable>
    <xsl:variable name="setSummaryFirstPageOnly">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="module"  select="'MultiPartArticle'"/>
        <xsl:with-param name="setting" select="'setSummaryFirstPageOnly'"/>
        <xsl:with-param name="default" select="'true'"/>
      </xsl:call-template>
    </xsl:variable>

    <!-- DE Berechne lokale Variablen -->
    <!-- EN Calculate local variables -->
    <xsl:variable name="currentRank" select="//cms:articleSectionPanel/cms:item/rank"/>
    <xsl:variable name="currentPage" select="count(//cms:item/sections[pageBreak = 'true' and rank &lt; $currentRank]) + 1"/>

    <xsl:if test="./summary and $setSummary = 'true' and ($currentPage = '1' or ($currentPage > '1' and $setSummaryFirstPageOnly = 'false'))">
      <div id="lead">
        <xsl:value-of disable-output-escaping="yes" select="./summary"/>
      </div>
    </xsl:if>
  </xsl:template>

  <!-- DE Bild -->
  <!-- EN image -->
  <xsl:template match="cms:item[objectType='com.arsdigita.cms.contenttypes.MultiPartArticle']" mode="image">

    <!-- DE Hole alle benötigten Einstellungen-->
    <!-- EN Getting all needed setting-->
    <xsl:variable name="setImage">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="module"  select="'MultiPartArticle'"/>
        <xsl:with-param name="setting" select="'setImage'"/>
        <xsl:with-param name="default" select="'true'"/>
      </xsl:call-template>
    </xsl:variable>
    <xsl:variable name="setImageMaxHeight">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="module"  select="'MultiPartArticle'"/>
        <xsl:with-param name="setting" select="'setImageMaxHeight'"/>
        <xsl:with-param name="default" select="''"/>
      </xsl:call-template>
    </xsl:variable>
    <xsl:variable name="setImageMaxWidth">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="module"  select="'MultiPartArticle'"/>
        <xsl:with-param name="setting" select="'setImageMaxWidth'"/>
        <xsl:with-param name="default" select="''"/>
      </xsl:call-template>
    </xsl:variable>
    <xsl:variable name="setImageCaption">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="module"  select="'MultiPartArticle'"/>
        <xsl:with-param name="setting" select="'setImageCaption'"/>
        <xsl:with-param name="default" select="'true'"/>
      </xsl:call-template>
    </xsl:variable>

    <xsl:if test="$setImage = 'true'">
      <xsl:call-template name="mandalay:imageAttachment">
        <xsl:with-param name="showCaption" select="$setImageCaption"/>
        <xsl:with-param name="maxHeight" select="$setImageMaxHeight" />
        <xsl:with-param name="maxWidth" select="$setImageMaxWidth" />
      </xsl:call-template>
    </xsl:if>
  </xsl:template>

  <!-- DE Vollansicht -->
  <!-- EN Detailed view -->
  <xsl:template name="CT_MultiPartArticle_graphics" match="cms:item[objectType='com.arsdigita.cms.contenttypes.MultiPartArticle']" mode="detailed_view">

    <!-- DE Hole alle benötigten Einstellungen-->
    <!-- EN Getting all needed setting-->
    <xsl:variable name="separator">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="module"  select="'MultiPartArticle'"/>
        <xsl:with-param name="setting" select="'separator'"/>
        <xsl:with-param name="default" select="' | '"/>
      </xsl:call-template>
    </xsl:variable>
    <xsl:variable name="setSectionListHeading">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="module"  select="'MultiPartArticle'"/>
        <xsl:with-param name="setting" select="'setSectionListHeading'"/>
        <xsl:with-param name="default" select="'true'"/>
      </xsl:call-template>
    </xsl:variable>

    <!-- DE Berechne lokale Variablen -->
    <!-- EN Calculate local variables -->
    <xsl:variable name="currentRank" select="//cms:articleSectionPanel/cms:item/rank"/>
    <xsl:variable name="currentPage" select="count(//cms:item/sections[pageBreak = 'true' and rank &lt; $currentRank]) + 1"/>

    <xsl:if test="count(./sections) > 1">
      <div class="sectionList">
        <xsl:if test="$setSectionListHeading = 'true'">
          <div id="caption">
            <xsl:call-template name="mandalay:getStaticText">
              <xsl:with-param name="module" select="'MultiPartArticle'" />
              <xsl:with-param name="id" select="'sections'"/>
            </xsl:call-template>
          </div>
        </xsl:if>
        <xsl:for-each select="./sections">
          <xsl:sort select="./rank" data-type="number"/>
          <xsl:variable name="curSection_Rank" select="./rank"/>
          <xsl:variable name="curSection_PageNumber" select="count(../sections[./pageBreak = 'true' and ./rank &lt; $curSection_Rank]) + 1"/>

          <a>
            <xsl:attribute name="href">
              <xsl:choose>
                <xsl:when test="$currentPage = $curSection_PageNumber">
                  <xsl:value-of select="concat('#section_', $curSection_Rank)"/>
                </xsl:when>
                <xsl:otherwise>
                  <xsl:value-of select="concat('?page=', $curSection_PageNumber, '#section_', $curSection_Rank)"/>
                </xsl:otherwise>
              </xsl:choose>
            </xsl:attribute>
            <xsl:attribute name="title">
              <xsl:call-template name="mandalay:getStaticText">
                <xsl:with-param name="module" select="'MultiPartArticle'"/>
                <xsl:with-param name="id" select="'page'"/>
              </xsl:call-template>
              <xsl:value-of select="$curSection_PageNumber"/>
              <xsl:call-template name="mandalay:getSetting">
                <xsl:with-param name="module" select="'MultiPartArticle'"/>
                <xsl:with-param name="setting" select="'separator'"/>
                <xsl:with-param name="default" select="' : '"/>
              </xsl:call-template>
              <xsl:value-of select="title"/>
            </xsl:attribute>
            <xsl:value-of select="title"/>
          </a>
          <xsl:if test="position() != last()">
            <span class="separator">
              <xsl:value-of select="$separator"/>
            </span>
          </xsl:if>
        </xsl:for-each>
      </div>
      <div class="endFloat"/>
    </xsl:if>
  </xsl:template>


  <xsl:template match="cms:item[objectType='com.arsdigita.cms.contenttypes.ArticleSection']" mode="lead">
  </xsl:template>

  <xsl:template match="cms:item[objectType='com.arsdigita.cms.contenttypes.ArticleSection']" mode="image">
  </xsl:template>

  <!-- DE Vollansicht Text-Section -->
  <!-- EN Detailed view text section -->
  <xsl:template name="CT_MultiPartArticle_Section" match="cms:item[objectType='com.arsdigita.cms.contenttypes.ArticleSection']" mode="detailed_view">

    <!-- DE Hole alle benötigten Einstellungen-->
    <!-- EN Getting all needed setting-->
    <xsl:variable name="separator">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="module"  select="'MultiPartArticle'"/>
        <xsl:with-param name="setting" select="'separator'"/>
        <xsl:with-param name="default" select="' | '"/>
      </xsl:call-template>
    </xsl:variable>
    <xsl:variable name="setImage">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="module"  select="'MultiPartArticle'"/>
        <xsl:with-param name="setting" select="'setImage'"/>
        <xsl:with-param name="default" select="'true'"/>
      </xsl:call-template>
    </xsl:variable>
    <xsl:variable name="setImageMaxHeight">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="module"  select="'MultiPartArticle'"/>
        <xsl:with-param name="setting" select="'setImageMaxHeight'"/>
        <xsl:with-param name="default" select="''"/>
      </xsl:call-template>
    </xsl:variable>
    <xsl:variable name="setImageMaxWidth">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="module"  select="'MultiPartArticle'"/>
        <xsl:with-param name="setting" select="'setImageMaxWidth'"/>
        <xsl:with-param name="default" select="''"/>
      </xsl:call-template>
    </xsl:variable>
    <xsl:variable name="setImageCaption">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="module"  select="'MultiPartArticle'"/>
        <xsl:with-param name="setting" select="'setImageCaption'"/>
        <xsl:with-param name="default" select="'true'"/>
      </xsl:call-template>
    </xsl:variable>
    <xsl:variable name="setLinkToTopBefore">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="module"  select="'MultiPartArticle'"/>
        <xsl:with-param name="setting" select="'setLinkToTopBefore'"/>
        <xsl:with-param name="default" select="'false'"/>
      </xsl:call-template>
    </xsl:variable>
    <xsl:variable name="setLinkToTopAfter">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="module"  select="'MultiPartArticle'"/>
        <xsl:with-param name="setting" select="'setLinkToTopAfter'"/>
        <xsl:with-param name="default" select="'true'"/>
      </xsl:call-template>
    </xsl:variable>
    <xsl:variable name="setLastLinkToTopAfter">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="module"  select="'MultiPartArticle'"/>
        <xsl:with-param name="setting" select="'setLastLinkToTopAfter'"/>
        <xsl:with-param name="default" select="'true'"/>
      </xsl:call-template>
    </xsl:variable>

    <!-- DE Berechne lokale Variablen -->
    <!-- EN Calculate local variables -->
    <xsl:variable name="currentRank" select="./rank"/>
    <xsl:variable name="currentPage" select="count(//cms:item/sections[pageBreak = 'true' and rank &lt; $currentRank]) + 1"/>
    <xsl:variable name="first" select="//cms:articleSectionPanel/cms:item[position()=1]/rank"/>
    <xsl:variable name="last" select="//cms:articleSectionPanel/cms:item[position()=last()]/rank"/>
    <xsl:variable name="maxRank" select="count(//cms:item/sections)"/>
    <xsl:variable name="maxPage" select="1 + count(//cms:item/sections[pageBreak = 'true' and position() != last()])"/>

    <div class="MPASection">

      <xsl:if test="$setImage = 'true'">
<!--        <xsl:call-template name="mandalay:imageAttachment"> -->
        <xsl:apply-templates select="./image">
          <xsl:with-param name="showCaption" select="$setImageCaption"/>
          <xsl:with-param name="maxHeight" select="$setImageMaxHeight" />
          <xsl:with-param name="maxWidth" select="$setImageMaxWidth" />
        </xsl:apply-templates>
<!--        </xsl:call-template> -->
      </xsl:if>
      
      <h2>
        <xsl:attribute name="id">
          <xsl:value-of select="concat('section_', rank)"/>
        </xsl:attribute>
        <xsl:value-of disable-output-escaping="yes" select="title"/>
      </h2>
      <xsl:if test="$setLinkToTopBefore = 'true' and rank &gt; $first">
        <div class="top">
          <a href="#" class="topLink" title="Seitenanfang">
            <xsl:call-template name="mandalay:getStaticText">
              <xsl:with-param name="module" select="'MultiPartArticle'" />
              <xsl:with-param name="id" select="'top'" />
            </xsl:call-template>
          </a>
        </div>
      </xsl:if>
      <xsl:for-each select="text">
        <xsl:value-of disable-output-escaping="yes" select="content"/>
      </xsl:for-each>
      <xsl:if test="$setLinkToTopAfter = 'true' and ($setLastLinkToTopAfter = 'true' or rank &lt; $last)">
        <div class="top">
          <a href="#" class="topLink" title="Seitenanfang">
            <xsl:attribute name="title">
              <xsl:call-template name="mandalay:getStaticText">
                <xsl:with-param name="module" select="'MultiPartArticle'" />
                <xsl:with-param name="id" select="'top'" />
              </xsl:call-template>
            </xsl:attribute>
            <xsl:call-template name="mandalay:getStaticText">
              <xsl:with-param name="module" select="'MultiPartArticle'" />
              <xsl:with-param name="id" select="'top'" />
            </xsl:call-template>
          </a>
        </div>
      </xsl:if>
    </div>

    <!-- DE Zeige die untere Navigationsleiste an, wenn das aktuelle cms:item einen PageBreak auslöst und damit das letzte dieser Seite ist -->
    <xsl:if test="pageBreak = 'true' or ./rank = $maxRank">
      <xsl:if test="$maxPage > '1'">
        <div class="paginator navbar">
          <xsl:if test="$currentPage > '1'">
            <a class="prev" href="?page={$currentPage - 1}">
              <xsl:attribute name="title">
                <xsl:call-template name="mandalay:getStaticText">
                  <xsl:with-param name="module" select="'MultiPartArticle'" />
                  <xsl:with-param name="id" select="'prev'" />
                </xsl:call-template>
              </xsl:attribute>
              <xsl:call-template name="mandalay:getStaticText">
                <xsl:with-param name="module" select="'MultiPartArticle'" />
                <xsl:with-param name="id" select="'prev'" />
              </xsl:call-template>
            </a>
          </xsl:if>
          <xsl:if test="$currentPage > '1' and $maxPage > $currentPage">
            <xsl:value-of select="$separator"/>
          </xsl:if>
          <xsl:if test="$maxPage > $currentPage">
            <a class="next" href="?page={$currentPage + 1}">
              <xsl:attribute name="title">
                <xsl:call-template name="mandalay:getStaticText">
                  <xsl:with-param name="module" select="'MultiPartArticle'" />
                  <xsl:with-param name="id" select="'next'" />
                </xsl:call-template>
              </xsl:attribute>
              <xsl:call-template name="mandalay:getStaticText">
                <xsl:with-param name="module" select="'MultiPartArticle'" />
                <xsl:with-param name="id" select="'next'" />
              </xsl:call-template>
            </a>
          </xsl:if>
          <div class="endFloat"/>
        </div>
      </xsl:if>
    </xsl:if>
  </xsl:template>


  <!-- DE Listenansicht -->
  <!-- EN List view -->
  <xsl:template name="CT_MultiPartArticle_List" match="nav:item[nav:attribute[@name='objectType'] = 'com.arsdigita.cms.contenttypes.MultiPartArticle']" mode="list_view">

    <!-- DE Hole alle benötigten Einstellungen-->
    <!-- EN Getting all needed setting-->
    <xsl:variable name="setSummary">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="module"  select="'MultiPartArticle'"/>
        <xsl:with-param name="setting" select="'listView/setSummary'"/>
        <xsl:with-param name="default" select="'true'"/>
      </xsl:call-template>
    </xsl:variable>
    <xsl:variable name="setSummaryLength">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="module"  select="'MultiPartArticle'"/>
        <xsl:with-param name="setting" select="'listView/setSummaryLength'"/>
        <xsl:with-param name="default" select="'0'"/>
      </xsl:call-template>
    </xsl:variable>
    <xsl:variable name="setImage">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="module"  select="'MultiPartArticle'"/>
        <xsl:with-param name="setting" select="'listView/setImage'"/>
        <xsl:with-param name="default" select="'true'"/>
      </xsl:call-template>
    </xsl:variable>
    <xsl:variable name="setMoreButton">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="module"  select="'MultiPartArticle'"/>
        <xsl:with-param name="setting" select="'listView/setMoreButton'"/>
        <xsl:with-param name="default" select="'auto'"/>
      </xsl:call-template>
    </xsl:variable>

    <xsl:if test="$setImage = 'true' and nav:attribute[@name='imageAttachments.image.id']">
      <a>
        <xsl:attribute name="href"><xsl:value-of select="nav:path"/></xsl:attribute>
        <xsl:attribute name="title">
          <xsl:call-template name="mandalay:shying">
            <xsl:with-param name="title">
              <xsl:value-of select="nav:attribute[@name='title']"/>
            </xsl:with-param>
            <xsl:with-param name="mode">dynamic</xsl:with-param>
          </xsl:call-template>
        </xsl:attribute>

        <div class="image">
          <img>
            <xsl:attribute name="src">/ccm/cms-service/stream/image/?image_id=<xsl:value-of select="nav:attribute[@name='imageAttachments.image.id']"/>&amp;maxWidth=150&amp;maxHeight=100</xsl:attribute>
            <xsl:if test="nav:attribute[@name='imageAttachments.caption']">
              <xsl:attribute name="alt"><xsl:value-of select="nav:attribute[@name='imageAttachments.caption']"/></xsl:attribute>
              <xsl:attribute name="title"><xsl:value-of select="nav:attribute[@name='imageAttachments.caption']"/></xsl:attribute>
            </xsl:if>
          </img>
        </div>
      </a>
    </xsl:if>

    <a class="CIname">
      <xsl:attribute name="href"><xsl:value-of select="nav:path"/></xsl:attribute>
      <xsl:attribute name="title">
        <xsl:call-template name="mandalay:shying">
          <xsl:with-param name="title">
            <xsl:value-of select="nav:attribute[@name='title']"/>
          </xsl:with-param>
          <xsl:with-param name="mode">dynamic</xsl:with-param>
        </xsl:call-template>
      </xsl:attribute>
      <xsl:call-template name="mandalay:shying">
        <xsl:with-param name="title">
          <xsl:value-of disable-output-escaping="yes" select="nav:attribute[@name='title']"/>
        </xsl:with-param>
        <xsl:with-param name="mode">dynamic</xsl:with-param>
      </xsl:call-template>
    </a>
    <xsl:if test="nav:attribute[@name='summary'] and $setSummary = 'true'">
      <br />
      <span class="intro">
        <xsl:choose>
          <xsl:when test="$setSummaryLength = '0'">
            <xsl:value-of disable-output-escaping="yes" select="nav:attribute[@name='summary']" />
          </xsl:when>
          <xsl:otherwise>
            <xsl:value-of disable-output-escaping="yes" select="substring(nav:attribute[@name='summary'], 1, $setSummaryLength)" />
            <xsl:if test="string-length(nav:attribute[@name='summary']) > $setSummaryLength">
              <xsl:text>...</xsl:text>
              <xsl:if test="$setMoreButton = 'auto'">
                <xsl:call-template name="mandalay:moreButton">
                  <xsl:with-param name="href" select="nav:path"/>
                  <xsl:with-param name="module" select="'MultiPartArticle'"/>
                </xsl:call-template>
              </xsl:if>
            </xsl:if>
          </xsl:otherwise>
        </xsl:choose>
        <xsl:if test="$setMoreButton = 'true'">
          <xsl:call-template name="mandalay:moreButton">
            <xsl:with-param name="href" select="nav:path"/>
            <xsl:with-param name="module" select="'MultiPartArticle'"/>
          </xsl:call-template>
        </xsl:if>
      </span>
    </xsl:if>
  </xsl:template>

  <xsl:template name="CT_MultiPartArticle_Link" match="*/cms:item/links[targetItem/objectType = 'com.arsdigita.cms.contenttypes.MultiPartArticle']" mode="link_view">

    <!-- DE Hole alle benötigten Einstellungen-->
    <!-- EN Getting all needed setting-->
    <xsl:variable name="setImageAndText">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="module"  select="'MultiPartArticle'"/>
        <xsl:with-param name="setting" select="'linkView/setImageAndText'"/>
        <xsl:with-param name="default" select="'true'"/>
      </xsl:call-template>
    </xsl:variable>
    <xsl:variable name="setImage">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="module"  select="'MultiPartArticle'"/>
        <xsl:with-param name="setting" select="'linkView/setImage'"/>
        <xsl:with-param name="default" select="'true'"/>
      </xsl:call-template>
    </xsl:variable>
    <xsl:variable name="setImageMaxHeight">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="module"  select="'MultiPartArticle'"/>
        <xsl:with-param name="setting" select="'linkView/setImageMaxHeight'"/>
        <xsl:with-param name="default" select="''"/>
      </xsl:call-template>
    </xsl:variable>
    <xsl:variable name="setImageMaxWidth">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="module"  select="'MultiPartArticle'"/>
        <xsl:with-param name="setting" select="'linkView/setImageMaxWidth'"/>
        <xsl:with-param name="default" select="''"/>
      </xsl:call-template>
    </xsl:variable>
    <xsl:variable name="setImageCaption">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="module"  select="'MultiPartArticle'"/>
        <xsl:with-param name="setting" select="'linkView/setImageCaption'"/>
        <xsl:with-param name="default" select="'true'"/>
      </xsl:call-template>
    </xsl:variable>
    <xsl:variable name="setDescription">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="module"  select="'MultiPartArticle'"/>
        <xsl:with-param name="setting" select="'linkView/setDescription'"/>
        <xsl:with-param name="default" select="'true'"/>
      </xsl:call-template>
    </xsl:variable>
    <xsl:variable name="setDescriptionLength">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="module"  select="'MultiPartArticle'"/>
        <xsl:with-param name="setting" select="'linkView/setDescriptionLength'"/>
        <xsl:with-param name="default" select="'0'"/>
      </xsl:call-template>
    </xsl:variable>
    <xsl:variable name="setMoreButton">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="module"  select="'MultiPartArticle'"/>
        <xsl:with-param name="setting" select="'linkView/setMoreButton'"/>
        <xsl:with-param name="default" select="'auto'"/>
      </xsl:call-template>
    </xsl:variable>

    <xsl:variable name="params"> 
      <xsl:choose>
	      <xsl:when test="./targetURI">
      	  <xsl:value-of select="concat('&amp;', substring(./targetURI, 2))"/>
      	</xsl:when>
      	<xsl:otherwise>
      	  <xsl:value-of select="''"/>
      	</xsl:otherwise>
      </xsl:choose>
    </xsl:variable>

    <!-- DE Wenn es Bilder gibt, dann soll das erste hier als Link angezeigt werden -->
    <!-- EN -->
    <xsl:if test="./targetItem/imageAttachments and $setImage = 'true'">
      <a class="CIname">
        <xsl:attribute name="href"><xsl:text>/redirect/?oid=</xsl:text><xsl:value-of select="./targetItem/@oid"/><xsl:value-of select="$params"/></xsl:attribute>
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
            <xsl:with-param name="showCaption" select="$setImageCaption" />
            <xsl:with-param name="maxHeight" select="$setImageMaxHeight" />
            <xsl:with-param name="maxWidth" select="$setImageMaxWidth" />
          </xsl:call-template>
        </xsl:for-each>
      </a>
    </xsl:if>
    <xsl:if test="$setImageAndText = 'true' or not(./targetItem/imageAttachments) or $setImage = 'false'">
      <a class="CIname">
        <xsl:attribute name="href"><xsl:text>/redirect/?oid=</xsl:text><xsl:value-of select="./targetItem/@oid"/><xsl:value-of select="$params"/></xsl:attribute>
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
        <br />
        <xsl:choose>
          <xsl:when test="$setDescriptionLength = '0'">
            <xsl:value-of disable-output-escaping="yes" select="./linkDescription" />
          </xsl:when>
          <xsl:otherwise>
            <xsl:value-of disable-output-escaping="yes" select="substring(./linkDescription, 1, $setDescriptionLength)" />
            <xsl:if test="string-length(./linkDescription) > $setDescriptionLength">
              <xsl:text>...</xsl:text>
              <xsl:if test="$setMoreButton = 'auto'">
                <xsl:call-template name="mandalay:moreButton">
                  <xsl:with-param name="href" select="./targetItem/@oid"/>
                  <xsl:with-param name="module" select="'MultiPartArticle'"/>
                </xsl:call-template>
              </xsl:if>
            </xsl:if>
          </xsl:otherwise>
        </xsl:choose>
        <xsl:if test="$setMoreButton = 'true'">
          <xsl:call-template name="mandalay:moreButton">
            <xsl:with-param name="href" select="./targetItem/@oid"/>
            <xsl:with-param name="module" select="'MultiPartArticle'"/>
          </xsl:call-template>
        </xsl:if>
      </xsl:if>
    </xsl:if>
  </xsl:template>

</xsl:stylesheet>
