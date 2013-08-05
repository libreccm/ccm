<?xml version="1.0" encoding="utf-8"?>
<!DOCTYPE stylesheet [<!ENTITY nbsp '&#160;'>]>

<!-- 
     Copyright 2011, Jens Pelzetter
         
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

<xsl:stylesheet  xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
  xmlns:bebop="http://www.arsdigita.com/bebop/1.0"
  xmlns:nav="http://ccm.redhat.com/navigation"
  xmlns:cms="http://www.arsdigita.com/cms/1.0"
  xmlns:ppp="http://www.arsdigita.com/PublicPersonalProfile/1.0"
  xmlns:mandalay="http://mandalay.quasiweb.de"
  exclude-result-prefixes="xsl bebop cms nav"
  version="1.0"
  >

  <!-- Detail view -->
  <xsl:template name="CT_PublicPersonalProfile_graphics" match="cms:item[objectType='com.arsdigita.cms.contenttypes.PublicPersonalProfile']" mode="detailed_view">
    <xsl:variable name="setImage">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="module"  select="'PublicPersonalProfile'"/>
        <xsl:with-param name="setting" select="'setImage'"/>
        <xsl:with-param name="default" select="'true'"/>
      </xsl:call-template>
    </xsl:variable>
    <xsl:variable name="setImageMaxHeight">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="module"  select="'PublicPersonalProfile'"/>
        <xsl:with-param name="setting" select="'setImageMaxHeight'"/>
        <xsl:with-param name="default" select="''"/>
      </xsl:call-template>
    </xsl:variable>
    <xsl:variable name="setImageMaxWidth">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="module"  select="'PublicPersonalProfile'"/>
        <xsl:with-param name="setting" select="'setImageMaxWidth'"/>
        <xsl:with-param name="default" select="''"/>
      </xsl:call-template>
    </xsl:variable>
    <xsl:variable name="setImageCaption">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="module"  select="'PublicPersonalProfile'"/>
        <xsl:with-param name="setting" select="'setImageCaption'"/>
        <xsl:with-param name="default" select="'false'"/>
      </xsl:call-template>
    </xsl:variable>
    <xsl:variable name="contentPos1">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="module"  select="'PublicPersonalProfile'"/>
        <xsl:with-param name="setting" select="'contentPos1'"/>
        <xsl:with-param name="default" select="'address'"/>
      </xsl:call-template>      
    </xsl:variable>
    <xsl:variable name="contentPos2">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="module"  select="'PublicPersonalProfile'"/>
        <xsl:with-param name="setting" select="'contentPos2'"/>
        <xsl:with-param name="default" select="'ownerimage'"/>
      </xsl:call-template>      
    </xsl:variable>
    <xsl:variable name="contentPos3">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="module"  select="'PublicPersonalProfile'"/>
        <xsl:with-param name="setting" select="'contentPos3'"/>
        <xsl:with-param name="default" select="'contact'"/>
      </xsl:call-template>      
    </xsl:variable>

    <xsl:if test="count(./ppp:profile) = 0">
      <ul class="publicPersonalProfileNavigation">
        <xsl:for-each select="./profileNavigation/nav:categoryMenu/nav:category/nav:category">
          <li>
            <a>
              <xsl:choose>
                <xsl:when test="string-length(@navItem) &gt; 0">
                  <xsl:attribute name="href">?showItem=<xsl:value-of select="@navItem"/></xsl:attribute>
                </xsl:when>
                <xsl:otherwise>
                  <xsl:attribute name="href">?showItem=</xsl:attribute>		  
                </xsl:otherwise>
              </xsl:choose>
              <xsl:value-of select="@title"/>
            </a>
          </li>
        </xsl:for-each>
      </ul>
    </xsl:if>

    <xsl:if test="count(./profileContent) = 0">

      <div id="mainBody">
        <xsl:call-template name="pppSetContentPos">
          <xsl:with-param name="contentKey" select="$contentPos1"/>
        </xsl:call-template>

        <xsl:call-template name="pppSetContentPos">
          <xsl:with-param name="contentKey" select="$contentPos2"/>
        </xsl:call-template>

        <xsl:call-template name="pppSetContentPos">
          <xsl:with-param name="contentKey" select="$contentPos3"/>
        </xsl:call-template>

	<xsl:if test="./position and (string-length(normalize-space(./position)) &gt; 0)">
	  <div id="pppOwnerPosition">
	    <h3>
	      <xsl:call-template name="mandalay:getStaticText">
		<xsl:with-param name="module" select="'PublicPersonalProfile'"/>
		<xsl:with-param name="id" select="'position'"/>
	      </xsl:call-template>
	    </h3>
	    <xsl:value-of select="./position"/>
	  </div>	  	  
	</xsl:if>

	<xsl:if test="./interests">
	  <div id="pppInterests">
	    <h3>
	      <xsl:call-template name="mandalay:getStaticText">
		<xsl:with-param name="module" select="'PublicPersonalProfile'"/>
		<xsl:with-param name="id" select="'interests'"/>
	      </xsl:call-template>
	    </h3>
	    <xsl:value-of disable-output-escaping="yes" select="./interests"/>
	  </div>	  	  
	</xsl:if>

	<xsl:if test="./misc">
	  <div id="pppOwnerMisc">
	    <h3>
	      <xsl:call-template name="mandalay:getStaticText">
		<xsl:with-param name="module" select="'PublicPersonalProfile'"/>
		<xsl:with-param name="id" select="'misc'"/>
	      </xsl:call-template>
	    </h3>
	    <xsl:value-of disable-output-escaping="yes" select="./misc"/>
	  </div>	  	  
	</xsl:if>

      </div>

      <div class="endFloat"/>

    </xsl:if>

  </xsl:template>

  <!-- These templates are only for internal use in *this* file -->
  <xsl:template name="pppSetContentPos">
    <xsl:param name="contentKey"/>
    <xsl:choose>
      <xsl:when test="$contentKey = 'address'">
          <xsl:call-template name="pppAddress"/>
      </xsl:when>
      <xsl:when test="$contentKey = 'contact'">
          <xsl:call-template name="pppContact"/>
      </xsl:when>
      <xsl:when test="$contentKey = 'ownerimage'">
        <xsl:call-template name="pppOwnerImage"/>
      </xsl:when>
    </xsl:choose>
  </xsl:template>

  <xsl:template name="pppContact">
    <dl class="contactEntries">
      <xsl:for-each select="./profileOwner/contacts/contact[@contactType='commonContact']/contactEntryKeys/entryKey">
	<xsl:if test="current() != 'homepage'">
	  <xsl:apply-templates select="../../contactentries[keyId=current()]" mode="dl"/>
	</xsl:if>
      </xsl:for-each>

<!--      <xsl:apply-templates select="./profileOwner/contacts/contact[@contactType='commonContact']/contactentries[keyId='office']" mode="dl"/>
      <xsl:apply-templates select="./profileOwner/contacts/contact[@contactType='commonContact']/contactentries[keyId='phoneOffice']" mode="dl"/>
      <xsl:apply-templates select="./profileOwner/contacts/contact[@contactType='commonContact']/contactentries[keyId='fax']" mode="dl"/>
      <xsl:apply-templates select="./profileOwner/contacts/contact[@contactType='commonContact']/contactentries[keyId='email']" mode="dl"/>
      <xsl:apply-templates select="./profileOwner/contacts/contact[@contactType='commonContact']/contactentries[keyId='homepage']" mode="dl"/>-->
    </dl>
  </xsl:template>

  <xsl:template name="CT_GenericContact_contactentries" match="contactentries" mode="dl">
    <xsl:variable name="setLinks">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="module"  select="'PublicPersonalProfile'"/>
        <xsl:with-param name="setting" select="'setLinks'"/>
        <xsl:with-param name="default" select="'true'"/>
      </xsl:call-template>
    </xsl:variable>

    <dt class="contactEntryKey">
      <xsl:value-of select="./key"/>
    </dt>
    <dd class="contactEntryValue">
      <xsl:choose>
	<xsl:when test="$setLinks and contains(./value, '@')">
	  <a>
	    <xsl:attribute name="href">mailto:<xsl:value-of select="./value"/></xsl:attribute>
	    <xsl:value-of select="./value"/>
	  </a>
	</xsl:when>
	<xsl:when test="$setLinks and starts-with(./value, 'http')">
	  <a>
	    <xsl:attribute name="href"><xsl:value-of select="./value"/></xsl:attribute>
	    <xsl:value-of select="./value"/>
	  </a>
	</xsl:when>
          <xsl:otherwise>
	    <xsl:value-of select="./value"/>
	  </xsl:otherwise>
      </xsl:choose>
    </dd>
  </xsl:template>

  <xsl:template name="pppAddress">
    <div class="address">
      <span class="addressTxt">
        <xsl:variable name="addressTxt">
          <xsl:call-template name="mandalay:string-replace">
            <xsl:with-param name="string" select="./profileOwner/contacts/contact/address/address"/>
            <xsl:with-param name="from" select="'&#xA;'"/>
            <xsl:with-param name="to" select="'&lt;br/>'"/>
          </xsl:call-template>
        </xsl:variable>	
        <xsl:variable name="addressTxt2">
          <xsl:call-template name="mandalay:string-replace">
            <xsl:with-param name="string" select="$addressTxt"/>
            <xsl:with-param name="from" select="'&#x2C;&#x20;'"/>
            <xsl:with-param name="to" select="'&lt;br/>'"/>
          </xsl:call-template>
        </xsl:variable>
        <xsl:value-of disable-output-escaping="yes" select="$addressTxt2"/>
      </span>
      <span class="postalCodeCity">
        <span class="postalCode">
          <xsl:value-of select="./profileOwner/contacts/contact/address/postalCode"/>
        </span>
        <xsl:text> </xsl:text>
        <span class="city">
          <xsl:value-of select="./profileOwner/contacts/contact/address/city"/>
        </span>
      </span>
      <br/>
    </div>
  </xsl:template>

  <xsl:template name="pppOwnerImage">
    <xsl:if test="./profileOwner/owner/imageAttachments">
      <xsl:variable name="setOwnerImage">
        <xsl:call-template name="mandalay:getSetting">
          <xsl:with-param name="module" select="'PublicPersonalProfile'"/>
          <xsl:with-param name="setting" select="'setOwnerImage'"/>
          <xsl:with-param name="default" select="'true'"/>
        </xsl:call-template>
      </xsl:variable>
      <xsl:if test="$setOwnerImage = 'true'">
        <xsl:for-each select="./profileOwner/owner">
          <xsl:call-template name="mandalay:imageAttachment">
            <xsl:with-param name="maxWidth">
              <xsl:call-template name="mandalay:getSetting">
                <xsl:with-param name="module" select="'PublicPersonalProfile'"/>
                <xsl:with-param name="setting" select="'setOwnerImageMaxWidth'"/>
                 <xsl:with-param name="default" select="'200'"/>
              </xsl:call-template>
            </xsl:with-param>
            <xsl:with-param name="maxHeight">
              <xsl:call-template name="mandalay:getSetting">
                <xsl:with-param name="module" select="'PublicPersonalProfile'"/>
                <xsl:with-param name="setting" select="'setOwnerImageMaxHeight'"/>
                <xsl:with-param name="default" select="'200'"/>
              </xsl:call-template>
            </xsl:with-param>
            <xsl:with-param name="setZoomLinks">
              <xsl:call-template name="mandalay:getSetting">
                <xsl:with-param name="module" select="'PublicPersonalProfile'"/>
                <xsl:with-param name="setting" select="'setOwnerImageZoomLink'"/>
                <xsl:with-param name="default" select="'true'"/>
              </xsl:call-template>
            </xsl:with-param>
            <xsl:with-param name="setZoomLink" select="'false'"/>
            <xsl:with-param name="setTextZoomLink" select="'false'"/>
          </xsl:call-template>
        </xsl:for-each>
      </xsl:if>
    </xsl:if>
  </xsl:template>
  <!-- Internal templates end -->

  <!-- List view -->
  <xsl:template name="CT_PublicPersonalProfile_List" match="nav:item[nav:attribute[@name='objectType'] = 'com.arsdigita.cms.contenttypes.PublicPersonalProfile']" mode="list_view">
    <!-- EN Getting all needed setting-->
    <xsl:variable name="setLeadText">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="module"  select="'PublicPersonalProfile'"/>
        <xsl:with-param name="setting" select="'listView/setLeadText'"/>
        <xsl:with-param name="default" select="'true'"/>
      </xsl:call-template>
    </xsl:variable>
    <xsl:variable name="setLeadTextLength">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="module"  select="'PublicPersonalProfile'"/>
        <xsl:with-param name="setting" select="'listView/setLeadTextLength'"/>
        <xsl:with-param name="default" select="'0'"/>
      </xsl:call-template>
    </xsl:variable>
    <xsl:variable name="setMoreButton">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="module"  select="'PublicPersonalProfile'"/>
        <xsl:with-param name="setting" select="'listView/setMoreButton'"/>
        <xsl:with-param name="default" select="'false'"/>
      </xsl:call-template>
    </xsl:variable>

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
                        <xsl:with-param name="module" select="'PublicPersonalProfile'"/>
                        <xsl:with-param name="id" select="'moreButtonTitle'"/>
                      </xsl:call-template>
                    </xsl:attribute>
                    <xsl:call-template name="mandalay:getStaticText">
                      <xsl:with-param name="module" select="'PublicPersonalProfile'"/>
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

  <!-- Link view -->
  <xsl:template name="CT_PublicPersonalProfile_Link" match="*/cms:item/links[targetItem/objectType = 'com.arsdigita.cms.contenttypes.PublicPersonalProfile']" mode="link_view">    
    <!-- EN Getting all needed setting-->
    <xsl:variable name="setImageAndText">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="module"  select="'PublicPersonalProfile'"/>
        <xsl:with-param name="setting" select="'linkView/setImageAndText'"/>
        <xsl:with-param name="default" select="'true'"/>
      </xsl:call-template>
    </xsl:variable>
    <xsl:variable name="setImage">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="module"  select="'PublicPersonalProfile'"/>
        <xsl:with-param name="setting" select="'linkView/setImage'"/>
        <xsl:with-param name="default" select="'true'"/>
      </xsl:call-template>
    </xsl:variable>
    <xsl:variable name="setImageMaxHeight">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="module"  select="'PublicPersonalProfile'"/>
        <xsl:with-param name="setting" select="'linkView/setImageMaxHeight'"/>
        <xsl:with-param name="default" select="''"/>
      </xsl:call-template>
    </xsl:variable>
    <xsl:variable name="setImageMaxWidth">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="module"  select="'PublicPersonalProfile'"/>
        <xsl:with-param name="setting" select="'linkView/setImageMaxWidth'"/>
        <xsl:with-param name="default" select="''"/>
      </xsl:call-template>
    </xsl:variable>
    <xsl:variable name="setImageCaption">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="module"  select="'PublicPersonalProfile'"/>
        <xsl:with-param name="setting" select="'linkView/setImageCaption'"/>
        <xsl:with-param name="default" select="'true'"/>
      </xsl:call-template>
    </xsl:variable>
    <xsl:variable name="setDescription">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="module"  select="'PublicPersonalProfile'"/>
        <xsl:with-param name="setting" select="'linkView/setDescription'"/>
        <xsl:with-param name="default" select="'true'"/>
      </xsl:call-template>
    </xsl:variable>
    <xsl:variable name="setDescriptionLength">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="module"  select="'PublicPersonalProfile'"/>
        <xsl:with-param name="setting" select="'linkView/setDescriptionLength'"/>
        <xsl:with-param name="default" select="'0'"/>
      </xsl:call-template>
    </xsl:variable>
    <xsl:variable name="setMoreButton">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="module"  select="'PublicPersonalProfile'"/>
        <xsl:with-param name="setting" select="'linkView/setMoreButton'"/>
        <xsl:with-param name="default" select="'false'"/>
      </xsl:call-template>
    </xsl:variable>

    <!-- DE Wenn es Bilder gibt, dann soll das erste hier als Link angezeigt werden -->
    <!-- EN -->
    <xsl:if test="./targetItem/imageAttachments and $setImage = 'true'">
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
    </xsl:if>
    <xsl:if test="$setImageAndText = 'true' or not(./targetItem/imageAttachments) or $setImage = 'false'">
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
                        <xsl:with-param name="module" select="'PublicPersonalProfile'"/>
                        <xsl:with-param name="id" select="'moreButtonTitle'"/>
                      </xsl:call-template>
                    </xsl:attribute>
                    <xsl:call-template name="mandalay:getStaticText">
                      <xsl:with-param name="module" select="'PublicPersonalProfile'"/>
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
