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
	xmlns:mandalay="http://mandalay.quasiweb.de"
	exclude-result-prefixes="xsl bebop cms"
	version="1.0">

  <!-- DE Vollansicht -->
  <!-- EN Detailed view -->
  <xsl:template name="CT_GenericPerson_graphics" match="cms:item[objectType='com.arsdigita.cms.contenttypes.GenericPerson']" mode="detailed_view">

    <!-- DE Hole alle benötigten Einstellungen-->
    <!-- EN Getting all needed setting-->
    <xsl:variable name="setFullname">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="module"  select="'GenericPerson'"/>
        <xsl:with-param name="setting" select="'setFullname'"/>
        <xsl:with-param name="default" select="'false'"/>
      </xsl:call-template>
    </xsl:variable>
    <xsl:variable name="setNameDetails">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="module"  select="'GenericPerson'"/>
        <xsl:with-param name="setting" select="'setNameDetails'"/>
        <xsl:with-param name="default" select="'true'"/>
      </xsl:call-template>
    </xsl:variable>
    <xsl:variable name="setBirthdate">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="module"  select="'GenericPerson'"/>
        <xsl:with-param name="setting" select="'setBirthdate'"/>
        <xsl:with-param name="default" select="'false'"/>
      </xsl:call-template>
    </xsl:variable>
    <xsl:variable name="setGender">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="module"  select="'GenericPerson'"/>
        <xsl:with-param name="setting" select="'setGender'"/>
        <xsl:with-param name="default" select="'false'"/>
      </xsl:call-template>
    </xsl:variable>
    <xsl:variable name="setDescription">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="module"  select="'GenericPerson'"/>
        <xsl:with-param name="setting" select="'setDescription'"/>
        <xsl:with-param name="default" select="'true'"/>
      </xsl:call-template>
    </xsl:variable>
    <xsl:variable name="setImage">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="module"  select="'GenericPerson'"/>
        <xsl:with-param name="setting" select="'setImage'"/>
        <xsl:with-param name="default" select="'true'"/>
      </xsl:call-template>
    </xsl:variable>
    <xsl:variable name="setImageMaxHeight">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="module"  select="'GenericPerson'"/>
        <xsl:with-param name="setting" select="'setImageMaxHeight'"/>
        <xsl:with-param name="default" select="''"/>
      </xsl:call-template>
    </xsl:variable>
    <xsl:variable name="setImageMaxWidth">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="module"  select="'GenericPerson'"/>
        <xsl:with-param name="setting" select="'setImageMaxWidth'"/>
        <xsl:with-param name="default" select="''"/>
      </xsl:call-template>
    </xsl:variable>
    <xsl:variable name="setImageCaption">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="module"  select="'GenericPerson'"/>
        <xsl:with-param name="setting" select="'setImageCaption'"/>
        <xsl:with-param name="default" select="'true'"/>
      </xsl:call-template>
    </xsl:variable>
    
    <div id="greeting">
      <xsl:call-template name="CT_GenericPerson_details">
        <xsl:with-param name="setNameDetails" select="$setNameDetails"/>
        <xsl:with-param name="setGender" select="$setGender"/>
        <xsl:with-param name="setBirthdate" select="$setBirthdate"/>
      </xsl:call-template>
      
      <xsl:if test="./pageDescription and $setDescription = 'true'">
        <div id="lead">
          <xsl:value-of disable-output-escaping="yes" select="./pageDescription"/>
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
    <div class="endFloat"/>
  </xsl:template>

  <!-- DE Listenansicht -->
  <!-- EN List view -->
  <xsl:template  name="CT_GenericPerson_List" match="nav:item[nav:attribute[@name='objectType'] = 'com.arsdigita.cms.contenttypes.GenericPerson']" mode="list_view">

    <!-- DE Hole alle benötigten Einstellungen-->
    <!-- EN Getting all needed setting-->
    <xsl:variable name="setLeadText">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="module"  select="'GenericPerson'"/>
        <xsl:with-param name="setting" select="'listView/setLeadText'"/>
        <xsl:with-param name="default" select="'true'"/>
      </xsl:call-template>
    </xsl:variable>
    <xsl:variable name="setLeadTextLength">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="module"  select="'GenericPerson'"/>
        <xsl:with-param name="setting" select="'listView/setLeadTextLength'"/>
        <xsl:with-param name="default" select="'0'"/>
      </xsl:call-template>
    </xsl:variable>
    <xsl:variable name="setMoreButton">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="module"  select="'GenericPerson'"/>
        <xsl:with-param name="setting" select="'listView/setMoreButton'"/>
        <xsl:with-param name="default" select="'false'"/>
      </xsl:call-template>
    </xsl:variable>

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
                        <xsl:with-param name="module" select="'GenericPerson'"/>
                        <xsl:with-param name="id" select="'moreButtonTitle'"/>
                      </xsl:call-template>
                    </xsl:attribute>
                    <xsl:call-template name="mandalay:getStaticText">
                      <xsl:with-param name="module" select="'GenericPerson'"/>
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

  <xsl:template name="CT_GenericPerson_Link" match="*/cms:item/links[targetItem/objectType = 'com.arsdigita.cms.contenttypes.GenericPerson']" mode="link_view">

    <!-- DE Hole alle benötigten Einstellungen-->
    <!-- EN Getting all needed setting-->
    <xsl:variable name="setImageAndText">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="module"  select="'GenericPerson'"/>
        <xsl:with-param name="setting" select="'linkView/setImageAndText'"/>
        <xsl:with-param name="default" select="'true'"/>
      </xsl:call-template>
    </xsl:variable>
    <xsl:variable name="setImage">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="module"  select="'GenericPerson'"/>
        <xsl:with-param name="setting" select="'linkView/setImage'"/>
        <xsl:with-param name="default" select="'true'"/>
      </xsl:call-template>
    </xsl:variable>
    <xsl:variable name="setImageMaxHeight">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="module"  select="'GenericPerson'"/>
        <xsl:with-param name="setting" select="'linkView/setImageMaxHeight'"/>
        <xsl:with-param name="default" select="''"/>
      </xsl:call-template>
    </xsl:variable>
    <xsl:variable name="setImageMaxWidth">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="module"  select="'GenericPerson'"/>
        <xsl:with-param name="setting" select="'linkView/setImageMaxWidth'"/>
        <xsl:with-param name="default" select="''"/>
      </xsl:call-template>
    </xsl:variable>
    <xsl:variable name="setImageCaption">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="module"  select="'GenericPerson'"/>
        <xsl:with-param name="setting" select="'linkView/setImageCaption'"/>
        <xsl:with-param name="default" select="'true'"/>
      </xsl:call-template>
    </xsl:variable>
    <xsl:variable name="setDescription">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="module"  select="'GenericPerson'"/>
        <xsl:with-param name="setting" select="'linkView/setDescription'"/>
        <xsl:with-param name="default" select="'true'"/>
      </xsl:call-template>
    </xsl:variable>
    <xsl:variable name="setDescriptionLength">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="module"  select="'GenericPerson'"/>
        <xsl:with-param name="setting" select="'linkView/setDescriptionLength'"/>
        <xsl:with-param name="default" select="'0'"/>
      </xsl:call-template>
    </xsl:variable>
    <xsl:variable name="setMoreButton">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="module"  select="'GenericPerson'"/>
        <xsl:with-param name="setting" select="'linkView/setMoreButton'"/>
        <xsl:with-param name="default" select="'false'"/>
      </xsl:call-template>
    </xsl:variable>

    <!-- DE Wenn es Bilder gibt, dann soll das erste hier als Link angezeigt werden -->
    <!-- EN -->
    <xsl:if test="./targetItem/imageAttachments and $setImage = 'true'">
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
    </xsl:if>
    <xsl:if test="$setImageAndText = 'true' or not(./targetItem/imageAttachments) or $setImage = 'false'">
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
                        <xsl:with-param name="module" select="'GenericPerson'"/>
                        <xsl:with-param name="id" select="'moreButtonTitle'"/>
                      </xsl:call-template>
                    </xsl:attribute>
                    <xsl:call-template name="mandalay:getStaticText">
                      <xsl:with-param name="module" select="'GenericPerson'"/>
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

  <xsl:template name="CT_GenericPerson_details">
    <xsl:param name="setFullname" select="'false'"/>
    <xsl:param name="setNameDetails" select="'true'"/>
    <xsl:param name="setGender" select="'false'"/>
    <xsl:param name="setBirthdate" select="'false'"/>
    <xsl:param name="setHomepage" select="'true'"/>

   <xsl:if test="$setFullname = 'true'">
      <div class="personFullName">
	<xsl:choose>
	  <xsl:when test="($setHomepage = 'true') and (string-length(../contactentries[keyId = 'homepage']/value) &gt; 1)">
	    <a>
	      <xsl:attribute name="href">
		<xsl:value-of select="../contactentries[keyId='homepage']/value"/>
	      </xsl:attribute>
        <xsl:if test="string-length(normalize-space(./titlepre)) &gt; 0">
          <xsl:value-of select="./titlepre"/><xsl:text> </xsl:text>
        </xsl:if>
        <xsl:value-of select="./givenname"/><xsl:text> </xsl:text>
        <xsl:value-of select="surname"/> 
        <xsl:if test="string-length(normalize-space(./titlepost)) &gt; 0">
          <xsl:text> </xsl:text>
          <xsl:value-of select="titlepost"/>
        </xsl:if>
	    </a>
	  </xsl:when>
	  <xsl:when test="($setHomepage = 'true') and string-length((./contacts/contact[@contactType='commonContact']/contactentries[keyId='homepage']/value) &gt; 1)">
	    <a>
	      <xsl:attribute name="href">
		<xsl:value-of select="./contacts/contact[@contactType='commonContact']/contactentries[keyId='homepage']/value"/>
	      </xsl:attribute>
	      <xsl:if test="string-length(normalize-space(./titlepre)) &gt; 0">
		<xsl:value-of select="./titlepre"/><xsl:text> </xsl:text>
	      </xsl:if>
	      <xsl:value-of select="./givenname"/><xsl:text> </xsl:text>
	      <xsl:value-of select="surname"/> 
	      <xsl:if test="string-length(normalize-space(./titlepost)) &gt; 0">
		<xsl:text> </xsl:text>
		<xsl:value-of select="titlepost"/>
	      </xsl:if>	      	      
	    </a>
	  </xsl:when>
	  <xsl:otherwise>
	    <xsl:if test="string-length(normalize-space(./titlepre)) &gt; 0">
	      <xsl:value-of select="./titlepre"/><xsl:text> </xsl:text>
	    </xsl:if>
	    <xsl:value-of select="./givenname"/><xsl:text> </xsl:text>
	    <xsl:value-of select="surname"/> 
	    <xsl:if test="string-length(normalize-space(./titlepost)) &gt; 0">
	      <xsl:text> </xsl:text>
	      <xsl:value-of select="titlepost"/>
	    </xsl:if>
	  </xsl:otherwise>
	</xsl:choose>
      </div>
    </xsl:if>

    <xsl:if test="$setNameDetails = 'true'">
      <h2 class="personNameDetails">
        <span class="personTitlePre">
          <xsl:value-of select="titlepre"/>
        </span>	
        <xsl:if test="string-length(titlepre) &gt; 0">
          <xsl:text> </xsl:text>
        </xsl:if>
        <span class="personGivenName">
          <xsl:value-of select="givenname"/>
        </span>	
        <xsl:if test="string-length(givenname) &gt; 0">
          <xsl:text> </xsl:text>
        </xsl:if>
        <span class="personSurname">
          <xsl:value-of select="surname"/>
        </span>
        <xsl:if test="(string-length(surname) &gt; 0) and (string-length(titlepost) &gt;0 )">
          <xsl:text>, </xsl:text>
        </xsl:if>
        <span class="personTitlePost">
          <xsl:value-of select="titlepost"/>
        </span>
      </h2>
      <div>
        <span class="key">
          <xsl:call-template name="mandalay:getStaticText">
            <xsl:with-param name="module" select="'GenericPerson'"/>
            <xsl:with-param name="id" select="'titlepre'"/>
          </xsl:call-template>
        </span>
        <span class="value">
          <xsl:value-of select="titlepre"/>
        </span>
      </div>
      <div>
        <span class="key">
          <xsl:call-template name="mandalay:getStaticText">
            <xsl:with-param name="module" select="'GenericPerson'"/>
            <xsl:with-param name="id" select="'givenname'"/>
          </xsl:call-template>
        </span>
        <span class="value">
          <xsl:value-of select="givenname"/>
        </span>
      </div>
      <div>
        <span class="key">
          <xsl:call-template name="mandalay:getStaticText">
            <xsl:with-param name="module" select="'GenericPerson'"/>
            <xsl:with-param name="id" select="'surname'"/>
          </xsl:call-template>
        </span>
        <span class="value">
          <xsl:value-of select="surname"/>
        </span>
      </div>
      <div>
        <span class="key">
          <xsl:call-template name="mandalay:getStaticText">
            <xsl:with-param name="module" select="'GenericPerson'"/>
            <xsl:with-param name="id" select="'titlepost'"/>
          </xsl:call-template>
        </span>
        <span class="value">
          <xsl:value-of select="titlepost"/>
        </span>
      </div> 
    </xsl:if> 
    <xsl:if test="$setGender = 'true'">
      <div>
        <span class="key">
          <xsl:call-template name="mandalay:getStaticText">
            <xsl:with-param name="module" select="'GenericPerson'"/>
            <xsl:with-param name="id" select="'gender'"/>
          </xsl:call-template>
        </span>
        <span class="value">
          <xsl:call-template name="mandalay:getStaticText">
            <xsl:with-param name="module" select="'GenericPerson'"/>
            <xsl:with-param name="id">
              <xsl:value-of select="gender"/>
            </xsl:with-param>
          </xsl:call-template>
        </span>
      </div>
    </xsl:if>
    <xsl:if test="$setBirthdate = 'true'">
      <div>
        <span class="key">
          <xsl:call-template name="mandalay:getStaticText">
            <xsl:with-param name="module" select="'GenericPerson'"/>
            <xsl:with-param name="id" select="'birthdate'"/>
          </xsl:call-template>
        </span>
        <span class="value">
          <xsl:value-of select="birthdate/@date"/>
        </span>
      </div>
    </xsl:if>
  </xsl:template>


</xsl:stylesheet>
