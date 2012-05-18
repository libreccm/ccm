<?xml version="1.0" encoding="utf-8"?>
<!DOCTYPE stylesheet [<!ENTITY nbsp '&#160;'>]>

<!-- 
     Copyright 2010, Jens Pelzetter

         
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
  xmlns:nav="http://ccm.redhat.com/navigation"
  xmlns:cms="http://www.arsdigita.com/cms/1.0"
  xmlns:mandalay="http://mandalay.quasiweb.de"
  exclude-result-prefixes="xsl bebop cms nav"
  version="1.0"
>

  <!-- 
       ***********************************************************
       ** Templates commonly used by the other templates of the **
       ** SciPublications module                                **
       ***********************************************************
  -->

  <!-- 
       List view of publications (basic type)
  -->
    <xsl:template 
    name="CT_Publication_List" 
    match="nav:item[nav:attribute[@name='objectType'] = 'com.arsdigita.cms.contenttypes.Publication']"
    mode="list_view">
    <!-- DE Hole alle benötigten Einstellungen -->
    <!-- EN Get all settings needed -->
    <xsl:variable name="setLinkToDetails">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="module"  select="'SciPublications'"/>
        <xsl:with-param name="setting" select="'listView/setLinkToDetails'"/>
        <xsl:with-param name="default" select="'true'"/>
      </xsl:call-template>
    </xsl:variable>
    <xsl:variable name="setLeadText">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="module"  select="'SciPublications'"/>
        <xsl:with-param name="setting" select="'listView/setLeadText'"/>
        <xsl:with-param name="default" select="'true'"/>
      </xsl:call-template>
    </xsl:variable>
    <xsl:variable name="setLeadTextLength">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="module"  select="'SciPublications'"/>
        <xsl:with-param name="setting" select="'listView/setLeadTextLength'"/>
        <xsl:with-param name="default" select="'0'"/>
      </xsl:call-template>
    </xsl:variable>
    <xsl:variable name="setMoreButton">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="module"  select="'SciPublications'"/>
        <xsl:with-param name="setting" select="'listView/setMoreButton'"/>
        <xsl:with-param name="default" select="'false'"/>
      </xsl:call-template>
    </xsl:variable>

    <xsl:choose>
      <xsl:when test="$setLinkToDetails = 'true' or (string-length(nav:attribute[@name='lead']) > $setLeadTextLength and $setLeadTextLength != '0')">
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
    <xsl:if test="nav:attribute[@name='lead'] and $setLeadText = 'true'">
      <br />
      <span class="intro">
        <xsl:choose>
          <xsl:when test="$setLeadTextLength = '0'">
            <xsl:value-of disable-output-escaping="yes" select="nav:attribute[@name='lead']" />
          </xsl:when>
          <xsl:otherwise>
            <xsl:value-of disable-output-escaping="yes" select="substring(nav:attribute[@name='lead'], 1, $setLeadTextLength)" />
            <xsl:if test="string-length(nav:attribute[@name='lead']) > $setLeadTextLength">
              <xsl:text>...</xsl:text>
              <xsl:if test="$setMoreButton = 'true'">
                <span class="moreButton">
                  <a>
                    <xsl:attribute name="href"><xsl:value-of select="nav:path"/></xsl:attribute>
                    <xsl:attribute name="title">
                      <xsl:call-template name="mandalay:getStaticText">
                        <xsl:with-param name="module" select="'SciPublications'"/>
                        <xsl:with-param name="id" select="'moreButtonTitle'"/>
                      </xsl:call-template>
                    </xsl:attribute>
                    <xsl:call-template name="mandalay:getStaticText">
                      <xsl:with-param name="module" select="'SciPublications'"/>
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

  <!--
      List view of publications with publisher
  -->
  <xsl:template 
    name="CT_PublicationWithPublisher_List" 
    match="nav:item[nav:attribute[@name='objectType'] = 'com.arsdigita.cms.contenttypes.PublicationWithPublisher']"
    mode="list_view">
    <!-- Simply call template for Publications, because there is not difference for list view between these
         two types -->
    <xsl:call-template name="CT_Publication_List"/>
  </xsl:template>

  <!-- List view of a publisher -->
  <xsl:template 
    name="CT_Publisher_List" 
    match="nav:item[nav:attribute[@name='objectType'] = 'com.arsdigita.cms.contenttypes.Publisher']"
    mode="list_view">
    <!-- DE Hole alle benötigten Einstellungen -->
    <!-- EN Get all settings needed -->
    <xsl:variable name="setLinkToDetails">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="module"  select="'SciPublications_Publisher'"/>
        <xsl:with-param name="setting" select="'listView/setLinkToDetails'"/>
        <xsl:with-param name="default" select="'true'"/>
      </xsl:call-template>
    </xsl:variable>
    <xsl:variable name="setLeadText">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="module"  select="'SciPublications_Publisher'"/>
        <xsl:with-param name="setting" select="'listView/setLeadText'"/>
        <xsl:with-param name="default" select="'true'"/>
      </xsl:call-template>
    </xsl:variable>
    <xsl:variable name="setLeadTextLength">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="module"  select="'SciPublications_Publisher'"/>
        <xsl:with-param name="setting" select="'listView/setLeadTextLength'"/>
        <xsl:with-param name="default" select="'0'"/>
      </xsl:call-template>
    </xsl:variable>
    <xsl:variable name="setMoreButton">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="module"  select="'SciPublications_Publisher'"/>
        <xsl:with-param name="setting" select="'listView/setMoreButton'"/>
        <xsl:with-param name="default" select="'false'"/>
      </xsl:call-template>
    </xsl:variable>

    <xsl:choose>
      <xsl:when test="$setLinkToDetails = 'true' or (string-length(nav:attribute[@name='lead']) > $setLeadTextLength and $setLeadTextLength != '0')">
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
    <xsl:if test="nav:attribute[@name='lead'] and $setLeadText = 'true'">
      <br />
      <span class="intro">
        <xsl:choose>
          <xsl:when test="$setLeadTextLength = '0'">
            <xsl:value-of disable-output-escaping="yes" select="nav:attribute[@name='lead']" />
          </xsl:when>
          <xsl:otherwise>
            <xsl:value-of disable-output-escaping="yes" select="substring(nav:attribute[@name='lead'], 1, $setLeadTextLength)" />
            <xsl:if test="string-length(nav:attribute[@name='lead']) > $setLeadTextLength">
              <xsl:text>...</xsl:text>
              <xsl:if test="$setMoreButton = 'true'">
                <span class="moreButton">
                  <a>
                    <xsl:attribute name="href"><xsl:value-of select="nav:path"/></xsl:attribute>
                    <xsl:attribute name="title">
                      <xsl:call-template name="mandalay:getStaticText">
                        <xsl:with-param name="module" select="'SciPublications_Publisher'"/>
                        <xsl:with-param name="id" select="'moreButtonTitle'"/>
                      </xsl:call-template>
                    </xsl:attribute>
                    <xsl:call-template name="mandalay:getStaticText">
                      <xsl:with-param name="module" select="'SciPublications_Publisher'"/>
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

  <!-- Link view for publications -->
    <xsl:template 
    name="CT_Publication_Link" 
    match="*/cms:item/links[targetItem/objectType = 'com.arsdigita.cms.contenttypes.Publication']" 
    mode="link_view">

    <!-- DE Hole alle benötigten Einstellungen-->
    <!-- EN Getting all needed setting-->
    <xsl:variable name="setLinkToDetails">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="module"  select="'SciPublications'"/>
        <xsl:with-param name="setting" select="'linkView/setLinkToDetails'"/>
        <xsl:with-param name="default" select="'true'"/>
      </xsl:call-template>
    </xsl:variable>
    <xsl:variable name="setImageAndText">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="module"  select="'SciPublications'"/>
        <xsl:with-param name="setting" select="'linkView/setImageAndText'"/>
        <xsl:with-param name="default" select="'true'"/>
      </xsl:call-template>
    </xsl:variable>
    <xsl:variable name="setImage">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="module"  select="'SciPublications'"/>
        <xsl:with-param name="setting" select="'linkView/setImage'"/>
        <xsl:with-param name="default" select="'true'"/>
      </xsl:call-template>
    </xsl:variable>
    <xsl:variable name="setImageMaxHeight">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="module"  select="'SciPublications'"/>
        <xsl:with-param name="setting" select="'linkView/setImageMaxHeight'"/>
        <xsl:with-param name="default" select="''"/>
      </xsl:call-template>
    </xsl:variable>
    <xsl:variable name="setImageMaxWidth">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="module"  select="'SciPublications'"/>
        <xsl:with-param name="setting" select="'linkView/setImageMaxWidth'"/>
        <xsl:with-param name="default" select="''"/>
      </xsl:call-template>
    </xsl:variable>
    <xsl:variable name="setImageCaption">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="module"  select="'SciPublications'"/>
        <xsl:with-param name="setting" select="'linkView/setImageCaption'"/>
        <xsl:with-param name="default" select="'true'"/>
      </xsl:call-template>
    </xsl:variable>
    <xsl:variable name="setDescription">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="module"  select="'SciPublications'"/>
        <xsl:with-param name="setting" select="'linkView/setDescription'"/>
        <xsl:with-param name="default" select="'true'"/>
      </xsl:call-template>
    </xsl:variable>
    <xsl:variable name="setDescriptionLength">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="module"  select="'SciPublications'"/>
        <xsl:with-param name="setting" select="'linkView/setDescriptionLength'"/>
        <xsl:with-param name="default" select="'0'"/>
      </xsl:call-template>
    </xsl:variable>
    <xsl:variable name="setMoreButton">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="module"  select="'SciPublications'"/>
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
	  <a>
	    <xsl:attribute name="href">/redirect/?oid=<xsl:value-of select="./targetItem/@oid"/></xsl:attribute>
	    <xsl:call-template name="mandalay:shying">
	      <xsl:with-param name="title">
		<xsl:value-of disable-output-escaping="yes" select="./linkTitle"/>
	      </xsl:with-param>
	      <xsl:with-param name="mode">dynamic</xsl:with-param>
	    </xsl:call-template>
	  </a>
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
                        <xsl:with-param name="module" select="'SciPublications'"/>
                        <xsl:with-param name="id" select="'moreButtonTitle'"/>
                      </xsl:call-template>
                    </xsl:attribute>
                    <xsl:call-template name="mandalay:getStaticText">
                      <xsl:with-param name="module" select="'SciPublications'"/>
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

  <!-- Link view for publications with publisher -->
  <xsl:template 
    name="CT_PublicationWithPublisher_Link" 
    match="*/cms:item/links[targetItem/objectType = 'com.arsdigita.cms.contenttypes.PublicationWithPublisher']"
    mode="link_view">
    <!-- Simply call template for Publications, because there is not difference for list view between these
         two types -->
    <xsl:call-template name="CT_Publication_Link"/>
  </xsl:template>

  <!-- Link view for publishers -->
    <xsl:template 
    name="CT_Publisher_Link" 
    match="*/cms:item/links[targetItem/objectType = 'com.arsdigita.cms.contenttypes.Publisher']" 
    mode="link_view">

    <!-- DE Hole alle benötigten Einstellungen-->
    <!-- EN Getting all needed setting-->
    <xsl:variable name="setLinkToDetails">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="module"  select="'SciPublications_Publisher'"/>
        <xsl:with-param name="setting" select="'linkView/setLinkToDetails'"/>
        <xsl:with-param name="default" select="'true'"/>
      </xsl:call-template>
    </xsl:variable>
    <xsl:variable name="setImageAndText">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="module"  select="'SciPublications_Publisher'"/>
        <xsl:with-param name="setting" select="'linkView/setImageAndText'"/>
        <xsl:with-param name="default" select="'true'"/>
      </xsl:call-template>
    </xsl:variable>
    <xsl:variable name="setImage">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="module"  select="'SciPublications_Publisher'"/>
        <xsl:with-param name="setting" select="'linkView/setImage'"/>
        <xsl:with-param name="default" select="'true'"/>
      </xsl:call-template>
    </xsl:variable>
    <xsl:variable name="setImageMaxHeight">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="module"  select="'SciPublications_Publisher'"/>
        <xsl:with-param name="setting" select="'linkView/setImageMaxHeight'"/>
        <xsl:with-param name="default" select="''"/>
      </xsl:call-template>
    </xsl:variable>
    <xsl:variable name="setImageMaxWidth">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="module"  select="'SciPublications_Publisher'"/>
        <xsl:with-param name="setting" select="'linkView/setImageMaxWidth'"/>
        <xsl:with-param name="default" select="''"/>
      </xsl:call-template>
    </xsl:variable>
    <xsl:variable name="setImageCaption">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="module"  select="'SciPublications_Publisher'"/>
        <xsl:with-param name="setting" select="'linkView/setImageCaption'"/>
        <xsl:with-param name="default" select="'true'"/>
      </xsl:call-template>
    </xsl:variable>
    <xsl:variable name="setDescription">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="module"  select="'SciPublications_Publisher'"/>
        <xsl:with-param name="setting" select="'linkView/setDescription'"/>
        <xsl:with-param name="default" select="'true'"/>
      </xsl:call-template>
    </xsl:variable>
    <xsl:variable name="setDescriptionLength">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="module"  select="'SciPublications_Publisher'"/>
        <xsl:with-param name="setting" select="'linkView/setDescriptionLength'"/>
        <xsl:with-param name="default" select="'0'"/>
      </xsl:call-template>
    </xsl:variable>
    <xsl:variable name="setMoreButton">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="module"  select="'SciPublications_Publisher'"/>
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
                        <xsl:with-param name="module" select="'SciPublications_Publisher'"/>
                        <xsl:with-param name="id" select="'moreButtonTitle'"/>
                      </xsl:call-template>
                    </xsl:attribute>
                    <xsl:call-template name="mandalay:getStaticText">
                      <xsl:with-param name="module" select="'SciPublications_Publisher'"/>
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

  <xsl:template name="scipublicationsAuthors">
    <xsl:param name="authors"/>
    <xsl:param name="authorText"/>
    <xsl:param name="authorsText"/>

    <xsl:variable name="formatDefFile">
      <xsl:call-template name="mandalay:getSetting">
	<xsl:with-param name="module" select="'SciPublications'"/>
	<xsl:with-param name="setting" select="'formatDefFile'"/>
	<xsl:with-param name="default" select="'SciPublicationsDefaultFormat.xml'"/>
      </xsl:call-template>
    </xsl:variable>

    <xsl:variable name="authorListMode">
      <xsl:call-template name="mandalay:getSetting">
	<xsl:with-param name="module" select="'SciPublications'"/>
	<xsl:with-param name="setting" select="'authorListMode'"/>
	<xsl:with-param name="default" select="'list'"/>
      </xsl:call-template>
    </xsl:variable>
    
    <dt>
      <xsl:choose>
	<xsl:when test="count($authors) &gt; 1">
	  <xsl:value-of select="$authorsText"/>	  
	</xsl:when>
	<xsl:otherwise>
	  <xsl:value-of select="$authorText"/>	  
	</xsl:otherwise>
      </xsl:choose>
    </dt>
    <dd>
      <xsl:choose>
	<xsl:when test="$authorListMode = 'ul'">
	  <ul class="publicationsAuthorsList">
	    <xsl:for-each select="$authors">
	      <xsl:sort select="@order" data-type="number"/>
	      <li>
		<xsl:apply-templates select="document(concat($theme-prefix, '/settings/', $formatDefFile))/bibrefFormat/bibrefAuthorFormat">
		  <xsl:with-param name="surname" select="./surname"/>
		  <xsl:with-param name="givenName" select="./givenname"/>		    
		  <xsl:with-param name="isEditor" select="@isEditor"/>
		  <xsl:with-param name="authorCount" select="'1'"/>
		  <xsl:with-param name="position" select="'1'"/>
		</xsl:apply-templates>	    		
	      </li>
	    </xsl:for-each>
	  </ul>
      </xsl:when>
      <xsl:otherwise>
	  <xsl:for-each select="$authors">
	    <xsl:sort select="@order" data-type="number"/>
	    <span>
	      <xsl:apply-templates select="document(concat($theme-prefix, '/settings/', $formatDefFile))/bibrefFormat/bibrefAuthorFormat">
		<xsl:with-param name="surname" select="./surname"/>
		<xsl:with-param name="givenName" select="./givenname"/>		    
		<xsl:with-param name="isEditor" select="@isEditor"/>
		<xsl:with-param name="authorsCount" select="count($authors)"/>
		<xsl:with-param name="position" select="position()"/>
	      </xsl:apply-templates>	    			
	    </span>
	  </xsl:for-each>
	</xsl:otherwise>
      </xsl:choose>
    </dd>

  </xsl:template>
 
<!--  <xsl:template name="scipublicationsDownload">
    <xsl:if test="./masterVersion/id">
      <h3 class="publicationDownloads">
	<xsl:call-template name="mandalay:getStaticText">
	  <xsl:with-param name="module" select="'SciPublications'"/>
	  <xsl:with-param name="id" select="'downloadReference'"/>
	</xsl:call-template>
      </h3>
      <ul class="publicationDownloads">
	<li>
	  <a>
	    <xsl:attribute name="href">/ccm/scipublications/export/?format=bibtex&amp;publication=<xsl:value-of select="./masterVersion/id"/></xsl:attribute>
	    <xsl:attribute name="title">
	      <xsl:call-template name="mandalay:getStaticText">
		<xsl:with-param name="module" select="'SciPublications'"/>
		<xsl:with-param name="id" select="'downloadAsBibtex'"/>	      
	      </xsl:call-template>
	    </xsl:attribute>
	    <xsl:call-template name="mandalay:getStaticText">
	      <xsl:with-param name="module" select="'SciPublications'"/>
	      <xsl:with-param name="id" select="'bibtex'"/>
	    </xsl:call-template>
	  </a>
	</li>
	<li>
	  <a>
	    <xsl:attribute name="href">/ccm/scipublications/export/?format=ris&amp;publication=<xsl:value-of select="./masterVersion/id"/></xsl:attribute>
	    <xsl:attribute name="title">
	      <xsl:call-template name="mandalay:getStaticText">
		<xsl:with-param name="module" select="'SciPublications'"/>
		<xsl:with-param name="id" select="'downloadAsRis'"/>	      
	      </xsl:call-template>
	    </xsl:attribute>
	    <xsl:call-template name="mandalay:getStaticText">
	      <xsl:with-param name="module" select="'SciPublications'"/>
	      <xsl:with-param name="id" select="'ris'"/>
	    </xsl:call-template>
	</a>
      </li>
    </ul>
    </xsl:if>
  </xsl:template>-->

</xsl:stylesheet>