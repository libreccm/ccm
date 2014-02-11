<?xml version="1.0" encoding="utf-8"?>
<!DOCTYPE stylesheet [<!ENTITY nbsp '&#160;'>]>

<!-- 
    Copyright: 2009, Jens Pelzetter
  
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
  version="1.0">

  <!-- DE Leadtext -->
  <!-- EN lead text view -->
  <xsl:template match="cms:item[objectType='com.arsdigita.cms.contenttypes.ResearchNetwork']" mode="lead">
    <xsl:variable name="setLeadText">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="module"  select="'ResearchNetwork'"/>
        <xsl:with-param name="setting" select="'setLeadText'"/>
        <xsl:with-param name="default" select="'true'"/>
      </xsl:call-template>
    </xsl:variable>

    <xsl:if test="./lead and $setLeadText = 'true'">
      <div class="lead">
        <xsl:value-of disable-output-escaping="yes" select="./lead"/>
      </div>
    </xsl:if>
  </xsl:template>

  <!-- DE Bild -->
  <!-- EN image -->
  <xsl:template match="cms:item[objectType='com.arsdigita.cms.contenttypes.ResearchNetwork']" mode="image">

    <!-- DE Hole alle benötigten Einstellungen-->
    <!-- EN Getting all needed setting-->
    <xsl:variable name="setImage">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="module"  select="'ResearchNetwork'"/>
        <xsl:with-param name="setting" select="'setImage'"/>
        <xsl:with-param name="default" select="'true'"/>
      </xsl:call-template>
    </xsl:variable>
    <xsl:variable name="setImageMaxHeight">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="module"  select="'ResearchNetwork'"/>
        <xsl:with-param name="setting" select="'setImageMaxHeight'"/>
        <xsl:with-param name="default" select="''"/>
      </xsl:call-template>
    </xsl:variable>
    <xsl:variable name="setImageMaxWidth">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="module"  select="'ResearchNetwork'"/>
        <xsl:with-param name="setting" select="'setImageMaxWidth'"/>
        <xsl:with-param name="default" select="''"/>
      </xsl:call-template>
    </xsl:variable>
    <xsl:variable name="setImageCaption">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="module"  select="'ResearchNetwork'"/>
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
  <xsl:template name="CT_ResearchNetwork_graphics" match="cms:item[objectType='com.arsdigita.cms.contenttypes.ResearchNetwork']" mode="detailed_view">
    <!-- DE Hole alle benötigten Einstellungen-->
    <!-- EN Getting all needed setting-->
    <xsl:variable name="setDirection">      
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="module" select="'ResearchNetwork'"/>
        <xsl:with-param name="setting" select="'setDirection'"/>
        <xsl:with-param name="default" select="'true'"/>
      </xsl:call-template>
    </xsl:variable>
    <xsl:variable name="setCoordination">      
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="module" select="'ResearchNetwork'"/>
        <xsl:with-param name="setting" select="'setCoordination'"/>
        <xsl:with-param name="default" select="'true'"/>
      </xsl:call-template>
    </xsl:variable>
    <xsl:variable name="setDescription">      
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="module" select="'ResearchNetwork'"/>
        <xsl:with-param name="setting" select="'setDescription'"/>
        <xsl:with-param name="default" select="'true'"/>
      </xsl:call-template>
    </xsl:variable>
    <xsl:variable name="setWebsite">      
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="module" select="'ResearchNetwork'"/>
        <xsl:with-param name="setting" select="'setWebsite'"/>
        <xsl:with-param name="default" select="'true'"/>
      </xsl:call-template>
    </xsl:variable>
    <xsl:variable name="setMembers">      
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="module" select="'ResearchNetwork'"/>
        <xsl:with-param name="setting" select="'setMembers'"/>
        <xsl:with-param name="default" select="'true'"/>
      </xsl:call-template>
    </xsl:variable>
    <xsl:variable name="membersSetLink">      
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="module" select="'ResearchNetwork'"/>
        <xsl:with-param name="setting" select="'membersSetLink'"/>
        <xsl:with-param name="default" select="'true'"/>
      </xsl:call-template>
    </xsl:variable>
    
    <div id="details">
      <xsl:if test="$setDescription = 'true'">
        <p id="researchNetworkDescription">
          <xsl:value-of select="./researchNetworkDescription"/>
        </p>
      </xsl:if>

      <xsl:if test="$setWebsite = 'true'">
        <div id="researchNetworkWebsite">
          <h2>
            <xsl:call-template name="mandalay:getStaticText">
              <xsl:with-param name="module" select="'ResearchNetwork'" />
              <xsl:with-param name="id" select="'websiteTitle'" />
            </xsl:call-template>
          </h2>
          <p>
            <a>
              <xsl:attribute name="href">
                <xsl:value-of select="researchNetworkWebsite"/>        
              </xsl:attribute>
              <xsl:value-of select="researchNetworkWebsite"/>        
            </a>
          </p>
        </div>
      </xsl:if>


      <xsl:if test="$setDirection = 'true'">
        <div id="researchNetworkDirection">
          <h2>
            <xsl:call-template name="mandalay:getStaticText">
              <xsl:with-param name="module" select="'ResearchNetwork'" />
              <xsl:with-param name="id" select="'directionTitle'" />
            </xsl:call-template>
          </h2>
          <p>
            <xsl:value-of select="./researchNetworkDirection"/>          
          </p>
        </div>
      </xsl:if>

      <xsl:if test="$setCoordination = 'true'">
        <div id="researchNetworkCoordination">
          <h2>
            <xsl:call-template name="mandalay:getStaticText">
              <xsl:with-param name="module" select="'ResearchNetwork'" />
              <xsl:with-param name="id" select="'coordinationTitle'" />
            </xsl:call-template>
          </h2>
          <p>
            <xsl:value-of select="./researchNetworkCoordination"/>          
          </p>
        </div>
      </xsl:if>

      <xsl:if test="$setMembers = 'true'">        
        <div id="researchNetworkMembers">
          <h2>
            <xsl:call-template name="mandalay:getStaticText">
              <xsl:with-param name="module" select="'ResearchNetwork'" />
              <xsl:with-param name="id" select="'membersTitle'" />
            </xsl:call-template>
          </h2>
          <ul>
            <xsl:for-each select="memberships">
              <xsl:choose>
                <xsl:when test="$membersSetLink = 'true'">
                  <a>
                    <xsl:attribute name="href">
                      <xsl:text>/redirect?oid=</xsl:text>
                      <xsl:value-of select="./targetItem/@oid"/>
                    </xsl:attribute>
                    <xsl:value-of select="./targetItem/titlePre"/>&nbsp;<xsl:value-of select="./targetItem/givenname"/>&nbsp;<xsl:value-of select="./targetItem/surname"/>&nbsp;<xsl:value-of select="./targetItem/titlePost"/>
                  </a>       
                </xsl:when>
                <xsl:otherwise>
                  <xsl:value-of select="./targetItem/titlePre"/>&nbsp;<xsl:value-of select="./targetItem/givenname"/>&nbsp;<xsl:value-of select="./targetItem/surname"/>&nbsp;<xsl:value-of select="./targetItem/titlePost"/>                  
                </xsl:otherwise>
              </xsl:choose>
            </xsl:for-each>
          </ul>
        </div>
      </xsl:if>      
    </div>
    <div class="endFloat"/>
  </xsl:template>

  <!-- DE Listenansicht -->
  <!-- EN List view -->
  <xsl:template name="CT_ResearchNetwork_List" match="nav:item[nav:attribute[@name='objectType'] = 'com.arsdigita.cms.contenttypes.ResearchNetwork']" mode="list_view">
    <!-- DE Hole alle benötigten Einstellungen-->
    <!-- EN Getting all needed setting-->
    <xsl:variable name="setLinkToDetails">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="module"  select="'ResearchNetwork'"/>
        <xsl:with-param name="setting" select="'listView/setLinkToDetails'"/>
        <xsl:with-param name="default" select="'true'"/>
      </xsl:call-template>
    </xsl:variable>
    <xsl:variable name="setLeadText">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="module"  select="'ResearchNetwork'"/>
        <xsl:with-param name="setting" select="'listView/setLeadText'"/>
        <xsl:with-param name="default" select="'true'"/>
      </xsl:call-template>
    </xsl:variable>
    <xsl:variable name="setLeadTextLength">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="module"  select="'ResearchNetwork'"/>
        <xsl:with-param name="setting" select="'listView/setLeadTextLength'"/>
        <xsl:with-param name="default" select="'0'"/>
      </xsl:call-template>
    </xsl:variable>
    <xsl:variable name="setImage">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="module"  select="'ResearchNetwork'"/>
        <xsl:with-param name="setting" select="'listView/setImage'"/>
        <xsl:with-param name="default" select="'true'"/>
      </xsl:call-template>
    </xsl:variable>
    <xsl:variable name="setMoreButton">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="module"  select="'ResearchNetwork'"/>
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

    <xsl:choose>
      <xsl:when test="$setLinkToDetails = 'true' or (string-length(nav:attribute[@name='lead']) > $setLeadTextLength and $setLeadTextLength != '0')">
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
        <span class="CIname">
          <xsl:value-of disable-output-escaping="yes" select="nav:attribute[@name='title']" />
        </span>
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
              <xsl:if test="$setMoreButton = 'auto'">
                <xsl:call-template name="mandalay:moreButton">
                  <xsl:with-param name="href" select="nav:path"/>
                  <xsl:with-param name="module" select="'ResearchNetwork'"/>
                </xsl:call-template>
              </xsl:if>
            </xsl:if>
          </xsl:otherwise>
        </xsl:choose>
        <xsl:if test="$setMoreButton = 'true'">
          <xsl:call-template name="mandalay:moreButton">
            <xsl:with-param name="href" select="nav:path"/>
            <xsl:with-param name="module" select="'ResearchNetwork'"/>
          </xsl:call-template>
        </xsl:if>
      </span>
    </xsl:if>
  </xsl:template>

  <xsl:template name="CT_ResearchNetwork_Link" match="*/cms:item/links[targetItem/objectType = 'com.arsdigita.cms.contenttypes.ResearchNetwork']" mode="link_view">

    <!-- DE Hole alle benötigten Einstellungen-->
    <!-- EN Getting all needed setting-->
    <xsl:variable name="setLinkToDetails">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="module"  select="'ResearchNetwork'"/>
        <xsl:with-param name="setting" select="'linkView/setLinkToDetails'"/>
        <xsl:with-param name="default" select="'true'"/>
      </xsl:call-template>
    </xsl:variable>
    <xsl:variable name="setImageAndText">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="module"  select="'ResearchNetwork'"/>
        <xsl:with-param name="setting" select="'linkView/setImageAndText'"/>
        <xsl:with-param name="default" select="'true'"/>
      </xsl:call-template>
    </xsl:variable>
    <xsl:variable name="setImage">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="module"  select="'ResearchNetwork'"/>
        <xsl:with-param name="setting" select="'linkView/setImage'"/>
        <xsl:with-param name="default" select="'true'"/>
      </xsl:call-template>
    </xsl:variable>
    <xsl:variable name="setImageMaxHeight">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="module"  select="'ResearchNetwork'"/>
        <xsl:with-param name="setting" select="'linkView/setImageMaxHeight'"/>
        <xsl:with-param name="default" select="''"/>
      </xsl:call-template>
    </xsl:variable>
    <xsl:variable name="setImageMaxWidth">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="module"  select="'ResearchNetwork'"/>
        <xsl:with-param name="setting" select="'linkView/setImageMaxWidth'"/>
        <xsl:with-param name="default" select="''"/>
      </xsl:call-template>
    </xsl:variable>
    <xsl:variable name="setImageCaption">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="module"  select="'ResearchNetwork'"/>
        <xsl:with-param name="setting" select="'linkView/setImageCaption'"/>
        <xsl:with-param name="default" select="'true'"/>
      </xsl:call-template>
    </xsl:variable>
    <xsl:variable name="setDescription">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="module"  select="'ResearchNetwork'"/>
        <xsl:with-param name="setting" select="'linkView/setDescription'"/>
        <xsl:with-param name="default" select="'true'"/>
      </xsl:call-template>
    </xsl:variable>
    <xsl:variable name="setDescriptionLength">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="module"  select="'ResearchNetwork'"/>
        <xsl:with-param name="setting" select="'linkView/setDescriptionLength'"/>
        <xsl:with-param name="default" select="'0'"/>
      </xsl:call-template>
    </xsl:variable>
    <xsl:variable name="setMoreButton">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="module"  select="'ResearchNetwork'"/>
        <xsl:with-param name="setting" select="'linkView/setMoreButton'"/>
        <xsl:with-param name="default" select="'auto'"/>
      </xsl:call-template>
    </xsl:variable>

    <!-- DE Wenn es Bilder gibt, dann soll das erste hier als Link angezeigt werden -->
    <!-- EN -->
    <xsl:if test="./targetItem/imageAttachments and $setImage = 'true'">
      <xsl:choose>
        <xsl:when test="$setLinkToDetails = 'true' or (string-length(./linkDescription) > $setDescriptionLength and $setDescriptionLength != '0')">
          <a class="CIname">
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
          <a class="CIname">
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
              <xsl:if test="$setMoreButton = 'auto'">
                <xsl:call-template name="mandalay:moreButton">
                  <xsl:with-param name="href" select="./targetItem/@oid"/>
                  <xsl:with-param name="module" select="'ResearchNetwork'"/>
                </xsl:call-template>
              </xsl:if>
            </xsl:if>
          </xsl:otherwise>
        </xsl:choose>
        <xsl:if test="$setMoreButton = 'true'">
          <xsl:call-template name="mandalay:moreButton">
            <xsl:with-param name="href" select="./targetItem/@oid"/>
            <xsl:with-param name="module" select="'ResearchNetwork'"/>
          </xsl:call-template>
        </xsl:if>
      </xsl:if>
    </xsl:if>
  </xsl:template>


</xsl:stylesheet>
