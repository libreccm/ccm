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
  xmlns:cms="http://www.arsdigita.com/cms/1.0"
  xmlns:nav="http://ccm.redhat.com/navigation"
  xmlns:mandalay="http://mandalay.quasiweb.de"
  version="1.0">

  <!-- DE Vollansicht -->
  <!-- EN Detailed view -->
  <xsl:template name="CT_GlossaryItem_graphics" match="cms:item[objectType='com.arsdigita.cms.contenttypes.GlossaryItem']" mode="detailed_view">

    <!-- DE Hole alle benötigten Einstellungen-->
    <!-- EN Getting all needed setting-->
    <xsl:variable name="setImage">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="module"  select="'GlossaryItem'"/>
        <xsl:with-param name="setting" select="'setImage'"/>
        <xsl:with-param name="default" select="'true'"/>
      </xsl:call-template>
    </xsl:variable>
    <xsl:variable name="setImageMaxHeight">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="module"  select="'GlossaryItem'"/>
        <xsl:with-param name="setting" select="'setImageMaxHeight'"/>
        <xsl:with-param name="default" select="''"/>
      </xsl:call-template>
    </xsl:variable>
    <xsl:variable name="setImageMaxWidth">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="module"  select="'GlossaryItem'"/>
        <xsl:with-param name="setting" select="'setImageMaxWidth'"/>
        <xsl:with-param name="default" select="''"/>
      </xsl:call-template>
    </xsl:variable>
    <xsl:variable name="setImageCaption">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="module"  select="'GlossaryItem'"/>
        <xsl:with-param name="setting" select="'setImageCaption'"/>
        <xsl:with-param name="default" select="'true'"/>
      </xsl:call-template>
    </xsl:variable>

    <div id="greeting">
      <xsl:if test="./title">
        <div id="lead">
          <xsl:value-of disable-output-escaping="yes" select="./title"/>
        </div>
      </xsl:if>
    </div>
    <xsl:if test="$setImage = 'true'">
      <xsl:call-template name="mandalay:imageAttachment">
        <xsl:with-param name="showCaption" select="$setImageCaption"/>
        <xsl:with-param name="maxHeight" select="$setImageMaxHeight" />
        <xsl:with-param name="maxWidth" select="$setImageMaxWidth" />
      </xsl:call-template>
    </xsl:if>

    <xsl:if test="./definition">
      <div id="mainBody">
        <xsl:value-of disable-output-escaping="yes" select="./definition"/>
        <div class="endFloat"/>
      </div>
    </xsl:if>
    <div class="endFloat"/>
  </xsl:template>

  <!-- DE Listenansicht -->
  <!-- EN List view -->
  <xsl:template name="CT_GlossaryItem_List" match="nav:item[nav:attribute[@name='objectType'] = 'com.arsdigita.cms.contenttypes.GlossaryItem']" mode="list_view">

    <!-- DE Hole alle benötigten Einstellungen-->
    <!-- EN Getting all needed setting-->
    <xsl:variable name="setLinkToDetails">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="module"  select="'GlossaryItem'"/>
        <xsl:with-param name="setting" select="'listView/setLinkToDetails'"/>
        <xsl:with-param name="default" select="'false'"/>
      </xsl:call-template>
    </xsl:variable>
    <xsl:variable name="setDefinition">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="module"  select="'GlossaryItem'"/>
        <xsl:with-param name="setting" select="'listView/setDefinition'"/>
        <xsl:with-param name="default" select="'true'"/>
      </xsl:call-template>
    </xsl:variable>
    <xsl:variable name="setDefinitionLength">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="module"  select="'GlossaryItem'"/>
        <xsl:with-param name="setting" select="'listView/setDefinitionLength'"/>
        <xsl:with-param name="default" select="'0'"/>
      </xsl:call-template>
    </xsl:variable>
    <xsl:variable name="setMoreButton">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="module"  select="'GlossaryItem'"/>
        <xsl:with-param name="setting" select="'listView/setMoreButton'"/>
        <xsl:with-param name="default" select="'false'"/>
      </xsl:call-template>
    </xsl:variable>

    <xsl:choose>
      <xsl:when test="$setLinkToDetails = 'true' or $setDefinition = 'false' or (string-length(nav:attribute[@name='definition']) > $setDefinitionLength and $setDefinitionLength != '0') or not(nav:attribute[@name='definition'])">
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
      </xsl:when>
      <xsl:otherwise>
        <xsl:value-of disable-output-escaping="yes" select="nav:attribute[@name='title']" />
      </xsl:otherwise>
    </xsl:choose>
    <xsl:if test="nav:attribute[@name='definition'] and $setDefinition = 'true'">
      <br />
      <span class="intro">
        <xsl:choose>
          <xsl:when test="$setDefinitionLength = '0'">
            <xsl:value-of disable-output-escaping="yes" select="nav:attribute[@name='definition']" />
          </xsl:when>
          <xsl:otherwise>
            <xsl:value-of disable-output-escaping="yes" select="substring(nav:attribute[@name='definition'], 1, $setDefinitionLength)" />
            <xsl:if test="string-length(nav:attribute[@name='definition']) > $setDefinitionLength">
              <xsl:text>...</xsl:text>
              <xsl:if test="$setMoreButton = 'true'">
                <span class="moreButton">
                  <a>
                    <xsl:attribute name="href"><xsl:value-of select="nav:path"/></xsl:attribute>
                    <xsl:attribute name="title">
                      <xsl:call-template name="mandalay:getStaticText">
                        <xsl:with-param name="module" select="'GlossaryItem'"/>
                        <xsl:with-param name="id" select="'moreButtonTitle'"/>
                      </xsl:call-template>
                    </xsl:attribute>
                    <xsl:call-template name="mandalay:getStaticText">
                      <xsl:with-param name="module" select="'GlossaryItem'"/>
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

  <xsl:template name="CT_GlossaryItem_Link" match="*/cms:item/links[targetItem/objectType = 'com.arsdigita.cms.contenttypes.GlossaryItem']" mode="link_view">

    <!-- DE Hole alle benötigten Einstellungen-->
    <!-- EN Getting all needed setting-->
    <xsl:variable name="setLinkToDetails">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="module"  select="'GlossaryItem'"/>
        <xsl:with-param name="setting" select="'linkView/setLinkToDetails'"/>
        <xsl:with-param name="default" select="'false'"/>
      </xsl:call-template>
    </xsl:variable>
    <xsl:variable name="setImageAndText">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="module"  select="'GlossaryItem'"/>
        <xsl:with-param name="setting" select="'linkView/setImageAndText'"/>
        <xsl:with-param name="default" select="'true'"/>
      </xsl:call-template>
    </xsl:variable>
    <xsl:variable name="setImage">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="module"  select="'GlossaryItem'"/>
        <xsl:with-param name="setting" select="'linkView/setImage'"/>
        <xsl:with-param name="default" select="'true'"/>
      </xsl:call-template>
    </xsl:variable>
    <xsl:variable name="setImageMaxHeight">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="module"  select="'GlossaryItem'"/>
        <xsl:with-param name="setting" select="'linkView/setImageMaxHeight'"/>
        <xsl:with-param name="default" select="''"/>
      </xsl:call-template>
    </xsl:variable>
    <xsl:variable name="setImageMaxWidth">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="module"  select="'GlossaryItem'"/>
        <xsl:with-param name="setting" select="'linkView/setImageMaxWidth'"/>
        <xsl:with-param name="default" select="''"/>
      </xsl:call-template>
    </xsl:variable>
    <xsl:variable name="setImageCaption">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="module"  select="'GlossaryItem'"/>
        <xsl:with-param name="setting" select="'linkView/setImageCaption'"/>
        <xsl:with-param name="default" select="'true'"/>
      </xsl:call-template>
    </xsl:variable>
    <xsl:variable name="setDescription">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="module"  select="'GlossaryItem'"/>
        <xsl:with-param name="setting" select="'linkView/setDescription'"/>
        <xsl:with-param name="default" select="'true'"/>
      </xsl:call-template>
    </xsl:variable>
    <xsl:variable name="setDescriptionLength">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="module"  select="'GlossaryItem'"/>
        <xsl:with-param name="setting" select="'linkView/setDescriptionLength'"/>
        <xsl:with-param name="default" select="'0'"/>
      </xsl:call-template>
    </xsl:variable>
    <xsl:variable name="setMoreButton">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="module"  select="'GlossaryItem'"/>
        <xsl:with-param name="setting" select="'linkView/setMoreButton'"/>
        <xsl:with-param name="default" select="'false'"/>
      </xsl:call-template>
    </xsl:variable>

    <!-- DE Wenn es Bilder gibt, dann soll das erste hier als Link angezeigt werden -->
    <!-- EN -->
    <xsl:if test="./targetItem/imageAttachments and $setImage = 'true'">
      <xsl:choose>
        <xsl:when test="$setLinkToDetails = 'true' or (string-length(./linkDescription) > $setDescriptionLength and $setDescriptionLength != '0')">
          <a>
            <xsl:attribute name="href"><xsl:text>/redirect/?oid=</xsl:text><xsl:value-of select="./targetItem/@oid"/></xsl:attribute>
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
        </xsl:when>
        <xsl:otherwise>
          <xsl:for-each select="./targetItem">
            <xsl:call-template name="mandalay:imageAttachment">
              <xsl:with-param name="showCaption" select="$setImageCaption" />
              <xsl:with-param name="maxHeight" select="$setImageMaxHeight" />
              <xsl:with-param name="maxWidth" select="$setImageMaxWidth" />
            </xsl:call-template>
          </xsl:for-each>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:if>
    <xsl:if test="$setImageAndText = 'true' or not(./targetItem/imageAttachments) or $setImage = 'false'">
      <xsl:choose>
        <xsl:when test="$setLinkToDetails = 'true' or (string-length(./linkDescription) > $setDescriptionLength and $setDescriptionLength != '0')">
          <a>
            <xsl:attribute name="href"><xsl:text>/redirect/?oid=</xsl:text><xsl:value-of select="./targetItem/@oid"/></xsl:attribute>
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
        </xsl:when>
        <xsl:otherwise>
          <xsl:call-template name="mandalay:shying">
            <xsl:with-param name="title">
              <xsl:value-of disable-output-escaping="yes" select="./linkTitle"/>
            </xsl:with-param>
            <xsl:with-param name="mode">dynamic</xsl:with-param>
          </xsl:call-template>
        </xsl:otherwise>
      </xsl:choose>
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
              <xsl:if test="$setMoreButton = 'true'">
                <span class="moreButton">
                  <a>
                    <xsl:attribute name="href"><xsl:text>/redirect/?oid=</xsl:text><xsl:value-of select="./targetItem/@oid"/></xsl:attribute>
                    <xsl:attribute name="title">
                      <xsl:call-template name="mandalay:getStaticText">
                        <xsl:with-param name="module" select="'GlossaryItem'"/>
                        <xsl:with-param name="id" select="'moreButtonTitle'"/>
                      </xsl:call-template>
                    </xsl:attribute>
                    <xsl:call-template name="mandalay:getStaticText">
                      <xsl:with-param name="module" select="'GlossaryItem'"/>
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

