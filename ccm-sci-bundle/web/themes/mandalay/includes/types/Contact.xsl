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

  <!-- DE Leadtext -->
  <!-- EN lead text view -->
  <xsl:template match="cms:item[objectType='com.arsdigita.cms.contenttypes.Contact']" mode="lead">
    <xsl:variable name="setLeadText">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="module"  select="'Contact'"/>
        <xsl:with-param name="setting" select="'setLeadText'"/>
        <xsl:with-param name="default" select="'true'"/>
      </xsl:call-template>
    </xsl:variable>
    <xsl:variable name="setFullname">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="module" select="'GenericContact'"/>
        <xsl:with-param name="setting" select="'setFullname'"/>
        <xsl:with-param name="default" select="'true'"/>
      </xsl:call-template>
    </xsl:variable>
    <xsl:variable name="setNameDetails">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="module" select="'GenericContact'"/>
        <xsl:with-param name="setting" select="'setNameDetails'"/>
        <xsl:with-param name="default" select="'false'"/>
      </xsl:call-template>
    </xsl:variable>
    <xsl:variable name="setGender">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="module" select="'GenericContact'"/>
        <xsl:with-param name="setting" select="'setGender'"/>
        <xsl:with-param name="default" select="'false'"/>
      </xsl:call-template>
    </xsl:variable>
    <xsl:variable name="setBirthdate">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="module" select="'GenericContact'"/>
        <xsl:with-param name="setting" select="'setBirthdate'"/>
        <xsl:with-param name="default" select="'false'"/>
      </xsl:call-template>
    </xsl:variable>
    <xsl:variable name="setAddressHeader">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="module" select="'GenericContact'"/>
        <xsl:with-param name="setting" select="'setAddressHeader'"/>
        <xsl:with-param name="default" select="'true'"/>
      </xsl:call-template>
    </xsl:variable>
    <xsl:variable name="setShowKeys">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="module" select="'GenericContact'"/>
        <xsl:with-param name="setting" select="'setShowKeys'"/>
        <xsl:with-param name="default" select="'true'"/>
      </xsl:call-template>
    </xsl:variable>
    <xsl:variable name="setContactEntriesHeader">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="module" select="'GenericContact'"/>
        <xsl:with-param name="setting" select="'setContactEntriesHeader'"/>
        <xsl:with-param name="default" select="'true'"/>
      </xsl:call-template>
    </xsl:variable>

    <xsl:call-template name="CT_GenericContact_details">
      <xsl:with-param name="setFullname" select="$setFullname"/>
      <xsl:with-param name="setNameDetails" select="$setNameDetails"/>
      <xsl:with-param name="setGender" select="$setGender"/>
      <xsl:with-param name="setBirthdate" select="$setBirthdate"/>
      <xsl:with-param name="setAddressHeader" select="$setAddressHeader"/>
      <xsl:with-param name="setShowKeys" select="$setShowKeys"/>
      <xsl:with-param name="setContactEntriesHeader" select="$setContactEntriesHeader"/>
    </xsl:call-template>

    <xsl:if test="./lead and $setLeadText = 'true'">
      <div class="lead">
        <xsl:value-of disable-output-escaping="yes" select="./lead"/>
      </div>
    </xsl:if>
  </xsl:template>

  <!-- DE Bild -->
  <!-- EN image -->
  <xsl:template match="cms:item[objectType='com.arsdigita.cms.contenttypes.Contact']" mode="image">

    <!-- DE Hole alle benötigten Einstellungen-->
    <!-- EN Getting all needed setting-->
    <xsl:variable name="setImage">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="module"  select="'Contact'"/>
        <xsl:with-param name="setting" select="'setImage'"/>
        <xsl:with-param name="default" select="'true'"/>
      </xsl:call-template>
    </xsl:variable>
    <xsl:variable name="setImageMaxHeight">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="module"  select="'Contact'"/>
        <xsl:with-param name="setting" select="'setImageMaxHeight'"/>
        <xsl:with-param name="default" select="''"/>
      </xsl:call-template>
    </xsl:variable>
    <xsl:variable name="setImageMaxWidth">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="module"  select="'Contact'"/>
        <xsl:with-param name="setting" select="'setImageMaxWidth'"/>
        <xsl:with-param name="default" select="''"/>
      </xsl:call-template>
    </xsl:variable>
    <xsl:variable name="setImageCaption">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="module"  select="'Contact'"/>
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
  <xsl:template name="CT_Contact_graphics" match="cms:item[objectType='com.arsdigita.cms.contenttypes.Contact']" mode="detailed_view">

    <!-- DE Hole alle benötigten Einstellungen-->
    <!-- EN Getting all needed setting-->

    <xsl:if test="./textAsset/content">
      <div id="mainBody">
        <xsl:value-of disable-output-escaping="yes" select="./textAsset/content"/>
        <div class="endFloat"/>
      </div>
    </xsl:if>
    <div class="endFloat"/>
  </xsl:template>

  <!-- DE Listenansicht -->
  <!-- EN List view -->
  <xsl:template name="CT_Contact_List" match="nav:item[nav:attribute[@name='objectType'] = 'com.arsdigita.cms.contenttypes.Contact']" mode="list_view">

    <!-- DE Hole alle benötigten Einstellungen-->
    <!-- EN Getting all needed setting-->
    <xsl:variable name="setLeadText">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="module" select="'GenericContact'"/>
        <xsl:with-param name="setting" select="'listView/setLeadText'"/>
        <xsl:with-param name="default" select="'true'"/>
      </xsl:call-template>
    </xsl:variable>
    <xsl:variable name="setLeadTextLength">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="module" select="'GenericContact'"/>
        <xsl:with-param name="setting" select="'listView/setLeadTextLength'"/>
        <xsl:with-param name="default" select="'0'"/>
      </xsl:call-template>
    </xsl:variable>
    <xsl:variable name="setImage">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="module"  select="'Contact'"/>
        <xsl:with-param name="setting" select="'listView/setImage'"/>
        <xsl:with-param name="default" select="'true'"/>
      </xsl:call-template>
    </xsl:variable>
    <xsl:variable name="setMoreButton">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="module"  select="'Contact'"/>
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
      <xsl:attribute name="href">
        <xsl:value-of select="nav:path"/>
      </xsl:attribute>
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

    <xsl:if test="nav:attribute[@name='lead'] and $setLeadText = 'true'">
      <br/>
      <span class="intro">
        <xsl:choose>
          <xsl:when test="$setLeadTextLength = '0'">
            <xsl:value-of disable-output-escaping="yes" select="nav:attribute[@name='lead']"/>
          </xsl:when>
          <xsl:otherwise>
            <xsl:value-of disable-output-escaping="yes"
              select="substring(nav:attribute[@name='lead'], 1, $setLeadTextLength)"/>
            <xsl:if test="string-length(nav:attribute[@name='lead']) > $setLeadTextLength">
              <xsl:text>...</xsl:text>
              <xsl:if test="$setMoreButton = 'auto'">
                <xsl:call-template name="mandalay:moreButton">
                  <xsl:with-param name="href" select="nav:path"/>
                  <xsl:with-param name="module" select="'Contact'"/>
                </xsl:call-template>
              </xsl:if>
            </xsl:if>
          </xsl:otherwise>
        </xsl:choose>
        <xsl:if test="$setMoreButton = 'true'">
          <xsl:call-template name="mandalay:moreButton">
            <xsl:with-param name="href" select="nav:path"/>
            <xsl:with-param name="module" select="'Contact'"/>
          </xsl:call-template>
        </xsl:if>
      </span>
    </xsl:if>
  </xsl:template>

  <xsl:template name="CT_Contact_Link" match="*/cms:item/links[targetItem/objectType = 'com.arsdigita.cms.contenttypes.Contact']" mode="link_view">

    <!-- DE Hole alle benötigten Einstellungen-->
    <!-- EN Getting all needed setting-->
    <xsl:variable name="setImageAndText">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="module" select="'GenericContact'"/>
        <xsl:with-param name="setting" select="'linkView/setImageAndText'"/>
        <xsl:with-param name="default" select="'true'"/>
      </xsl:call-template>
    </xsl:variable>
    <xsl:variable name="setImage">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="module" select="'GenericContact'"/>
        <xsl:with-param name="setting" select="'linkView/setImage'"/>
        <xsl:with-param name="default" select="'true'"/>
      </xsl:call-template>
    </xsl:variable>
    <xsl:variable name="setImageMaxHeight">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="module" select="'GenericContact'"/>
        <xsl:with-param name="setting" select="'linkView/setImageMaxHeight'"/>
        <xsl:with-param name="default" select="''"/>
      </xsl:call-template>
    </xsl:variable>
    <xsl:variable name="setImageMaxWidth">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="module" select="'GenericContact'"/>
        <xsl:with-param name="setting" select="'linkView/setImageMaxWidth'"/>
        <xsl:with-param name="default" select="''"/>
      </xsl:call-template>
    </xsl:variable>
    <xsl:variable name="setImageCaption">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="module" select="'GenericContact'"/>
        <xsl:with-param name="setting" select="'linkView/setImageCaption'"/>
        <xsl:with-param name="default" select="'true'"/>
      </xsl:call-template>
    </xsl:variable>
    <xsl:variable name="setDescription">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="module" select="'GenericContact'"/>
        <xsl:with-param name="setting" select="'linkView/setDescription'"/>
        <xsl:with-param name="default" select="'true'"/>
      </xsl:call-template>
    </xsl:variable>
    <xsl:variable name="setDescriptionLength">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="module" select="'GenericContact'"/>
        <xsl:with-param name="setting" select="'linkView/setDescriptionLength'"/>
        <xsl:with-param name="default" select="'0'"/>
      </xsl:call-template>
    </xsl:variable>
    <xsl:variable name="setMoreButton">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="module" select="'GenericContact'"/>
        <xsl:with-param name="setting" select="'linkView/setMoreButton'"/>
        <xsl:with-param name="default" select="'auto'"/>
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
              <xsl:if test="$setMoreButton = 'auto'">
                <xsl:call-template name="mandalay:moreButton">
                  <xsl:with-param name="href" select="./targetItem/@oid"/>
                  <xsl:with-param name="module" select="'Contact'"/>
                </xsl:call-template>
              </xsl:if>
            </xsl:if>
          </xsl:otherwise>
        </xsl:choose>
        <xsl:if test="$setMoreButton = 'true'">
          <xsl:call-template name="mandalay:moreButton">
            <xsl:with-param name="href" select="./targetItem/@oid"/>
            <xsl:with-param name="module" select="'Contact'"/>
          </xsl:call-template>
        </xsl:if>
      </xsl:if>
    </xsl:if>
  </xsl:template>

  <xsl:template name="CT_Contact_contactentries" match="contactentries">
    <div class="contactentry">
      <span class="key">
        <xsl:value-of select="key"/><xsl:text>: </xsl:text>
      </span>
      <span class="value">
        <xsl:choose>
          <xsl:when test="(substring(value, 1, 7) = 'http://') or (substring(value, 1, 3) = 'www') or (substring(value, 1, 1) = '/' ) or (contains(value, '@'))">
            <a>
              <xsl:attribute name="href">
                <xsl:choose>
                  <xsl:when test="contains(value, '@')">
                    <xsl:value-of select="concat('mailto:', value)"/>
                  </xsl:when>
                  <xsl:otherwise>
                    <xsl:value-of select="value"/>
                  </xsl:otherwise>
                </xsl:choose>
              </xsl:attribute>
              <xsl:value-of select="value"/>
            </a>
          </xsl:when>
          <xsl:otherwise>
            <xsl:value-of select="value"/>
          </xsl:otherwise>
        </xsl:choose>
      </span>
    </div>
  </xsl:template>

<xsl:template name="CT_Contact_details">
  <xsl:param name="setFullname" select="'true'"/>
  <xsl:param name="setNameDetails" select="'false'"/>
  <xsl:param name="setGender" select="'false'"/>
  <xsl:param name="setBirthdate" select="'false'"/>
  <xsl:param name="setAddressHeader" select="'true'"/>
  <xsl:param name="setAddress" select="'true'"/>
  <xsl:param name="setShowKeys" select="'true'"/>
  <xsl:param name="setContactEntriesHeader" select="'true'"/>
  <xsl:param name="setContactEntries" select="'true'"/>
  
  <xsl:for-each select="person">
    <xsl:call-template name="CT_GenericPerson_details">
      <xsl:with-param name="setFullname" select="$setFullname"/>
      <xsl:with-param name="setNameDetails" select="$setNameDetails"/>
      <xsl:with-param name="setGender" select="$setGender"/>
      <xsl:with-param name="setBirthdate" select="$setBirthdate"/>
    </xsl:call-template>
  </xsl:for-each>
  
  <xsl:if test="$setAddress = 'true'">
  <xsl:for-each select="address">
    <xsl:if test="$setAddressHeader = 'true'">
      <h3>
        <xsl:call-template name="mandalay:getStaticText">
          <xsl:with-param name="module" select="'GenericContact'"/>
          <xsl:with-param name="id" select="addressHeader"/>
        </xsl:call-template>
      </h3>
    </xsl:if>
    <xsl:call-template name="CT_GenericAddress_details">
      <xsl:with-param name="setShowKeys" select="$setShowKeys"/>
    </xsl:call-template>
  </xsl:for-each>
  </xsl:if>
  
  <xsl:if test="contactentries and ($setContactEntries = 'true')">
    <xsl:if test="$setContactEntriesHeader">
      <h3>
        <xsl:call-template name="mandalay:getStaticText">
          <xsl:with-param name="module" select="'GenericContact'"/>
          <xsl:with-param name="id" select="'contactentriesHeader'"/>
        </xsl:call-template>    
      </h3>
    </xsl:if>
    <xsl:for-each select="./contactEntryKeys/entryKey">
      <xsl:apply-templates select="../../contactentries[keyId=current()]">
	<xsl:with-param name="key" select="entryKey"/>
      </xsl:apply-templates>
    </xsl:for-each>      
  </xsl:if>
</xsl:template>

</xsl:stylesheet>
