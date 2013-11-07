<?xml version="1.0" encoding="utf-8"?>
<!DOCTYPE stylesheet [<!ENTITY nbsp '&#160;'>]>

<!-- 
    Copyright: 2010, Jens Pelzetter
  
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

<xsl:stylesheet   xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
		  xmlns:bebop="http://www.arsdigita.com/bebop/1.0"
		  xmlns:nav="http://ccm.redhat.com/navigation"
		  xmlns:cms="http://www.arsdigita.com/cms/1.0"
		  xmlns:mandalay="http://mandalay.quasiweb.de"
		  exclude-result-prefixes="xsl bebop cms nav"
		  version="1.0">
  
  <!-- DE Leadtext -->
  <!-- EN lead text view -->
  <xsl:template name="CT_GenericOrganizationalUnit_lead" match="cms:item[objectType='com.arsdigita.cms.contenttypes.GenericOrganizationalUnit']" mode="lead">
    <xsl:variable name="setLeadText">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="module"  select="'GenericOrganizationalUnit'"/>
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
  <xsl:template name="CT_GenericOrganizationalUnit_image" match="cms:item[objectType='com.arsdigita.cms.contenttypes.GenericOrganizationalUnit']" mode="image">

    <!-- DE Hole alle benötigten Einstellungen-->
    <!-- EN Getting all needed setting-->
    <xsl:variable name="setImage">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="module"  select="'GenericOrganizationalUnit'"/>
        <xsl:with-param name="setting" select="'setImage'"/>
        <xsl:with-param name="default" select="'true'"/>
      </xsl:call-template>
    </xsl:variable>
    <xsl:variable name="setImageMaxHeight">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="module"  select="'GenericOrganizationalUnit'"/>
        <xsl:with-param name="setting" select="'setImageMaxHeight'"/>
        <xsl:with-param name="default" select="''"/>
      </xsl:call-template>
    </xsl:variable>
    <xsl:variable name="setImageMaxWidth">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="module"  select="'GenericOrganizationalUnit'"/>
        <xsl:with-param name="setting" select="'setImageMaxWidth'"/>
        <xsl:with-param name="default" select="''"/>
      </xsl:call-template>
    </xsl:variable>
    <xsl:variable name="setImageCaption">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="module"  select="'GenericOrganizationalUnit'"/>
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
  <xsl:template name="CT_GenericOrganizationalUnit_graphics" 
		match="cms:item[objectType='com.arsdigita.cms.contenttypes.GenericOrganizationalUnit']"
		mode="detailed_view">

    <!-- DE Hole alle benötigten Einstellungen-->
    <!-- EN Getting all needed setting-->
    <xsl:variable name="setAddendum">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="module"  select="'GenericOrganizationalUnit'"/>
        <xsl:with-param name="setting" select="'setAddendum'"/>
        <xsl:with-param name="default" select="'true'"/>
      </xsl:call-template>      
    </xsl:variable>
    <xsl:variable name="setChildren">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="module"  select="'GenericOrganizationalUnit'"/>
        <xsl:with-param name="setting" select="'setChildren'"/>
        <xsl:with-param name="default" select="'true'"/>
      </xsl:call-template>            
    </xsl:variable>
    <xsl:variable name="setChildrenLinks">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="module"  select="'GenericOrganizationalUnit'"/>
        <xsl:with-param name="setting" select="'setChildrenLinks'"/>
        <xsl:with-param name="default" select="'true'"/>
      </xsl:call-template>            
    </xsl:variable>    
    <xsl:variable name="setPersons">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="module"  select="'GenericOrganizationalUnit'"/>
        <xsl:with-param name="setting" select="'setPersons'"/>
        <xsl:with-param name="default" select="'true'"/>
      </xsl:call-template>            
    </xsl:variable>
    <xsl:variable name="setPersonsLink">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="module"  select="'GenericOrganizationalUnit'"/>
        <xsl:with-param name="setting" select="'setPersonsLink'"/>
        <xsl:with-param name="default" select="'true'"/>
      </xsl:call-template>            
    </xsl:variable>

    <div class="genericOrganizationalUnitDetails">
      <xsl:if test="($setAddendum = 'true') and (string-length(./addendum) &gt; 0)">
        <div class="addendum">
          <xsl:value-of select="./addendum"/>
        </div>
      </xsl:if>

      <xsl:if test="($setChildren = 'true') and (count(./orgaunit_children) &gt; 0)">
	<div class="children">
	  <h3>
	    <xsl:call-template name="mandalay:getStaticText">
	      <xsl:with-param name="module" select="'GenericOrganizationalUnit'"/>
	      <xsl:with-param name="id" select="'children'"/>
	    </xsl:call-template>
	  </h3>
	  <xsl:for-each select="./orgaunit_children">
	    <xsl:sort select="./orgaunit_children_order" 
		      order="ascending"
		      data-type="number"/>
	    <xsl:choose>
	      <xsl:when  test="$setChildrenLinks = 'true'">
		<a>
		  <xsl:attribute name="href">
		    /redirect/?oid=<xsl:value-of select="./@oid"/>
		  </xsl:attribute>
		  <xsl:value-of select="./title"/>
	      </a>
	      </xsl:when>
	      <xsl:otherwise>
		<xsl:value-of select="./title"/>
	      </xsl:otherwise>
	    </xsl:choose>	  
	  </xsl:for-each>
	</div>
      </xsl:if>
      
      <xsl:if test="($setPersons = 'true') and (count(./persons) &gt; 0)">
	<div class="persons">
	  <h3>
	    <xsl:call-template name="mandalay:getStaticText">
	      <xsl:with-param name="module" select="'GenericOrganizationalUnit'"/>
	      <xsl:with-param name="id" select="'persons'"/>	      
	    </xsl:call-template>			       
	  </h3>
	  <xsl:for-each select="./persons">
	    <xsl:sort select="./persons/surname"
		      order="ascending"
		      data-type="text"
		      case-order="upper-first"/>
	    <xsl:sort select="./persons/givenname"
		      order="ascending"
		      data-type="text"
		      case-order="upper-first"/>	  
	    <xsl:choose>
	      <xsl:when test="$setPersonsLink = 'true'">
		<a>
		  <xsl:attribute name="href">
		    /redirect/?oid=<xsl:value-of select="./@oid"/>
		  </xsl:attribute>
		  <xsl:value-of select="./titlepre"/> <xsl:value-of select="./givenname"/> <xsl:value-of select="./surname"/> <xsl:value-of select="./titlepost"/>
		</a>
	      </xsl:when>
	      <xsl:otherwise>
		<xsl:value-of select="./titlepre"/> <xsl:value-of select="./givenname"/> <xsl:value-of select="./surname"/> <xsl:value-of select="./titlepost"/>
	      </xsl:otherwise>
	    </xsl:choose>
	  </xsl:for-each>
	</div>
      </xsl:if>
      
    </div>
    <div class="endFloat"/>
  </xsl:template>

  <!-- DE Listenansicht -->
  <!-- EN List view -->
  <xsl:template name="CT_GenericOrganizationalUnit_List" match="nav:item[nav:attribute[@name='objectType'] = 'com.arsdigita.cms.contenttypes.GenericOrganizationalUnit']" mode="list_view">
    <!-- DE Hole alle benötigten Einstellungen-->
    <!-- EN Getting all needed setting-->
    <xsl:variable name="setLinkToDetails">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="module"  select="'GenericOrganizationalUnit'"/>
        <xsl:with-param name="setting" select="'listView/setLinkToDetails'"/>
        <xsl:with-param name="default" select="'true'"/>
      </xsl:call-template>
    </xsl:variable>
    <xsl:variable name="setLeadText">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="module"  select="'GenericOrganizationalUnit'"/>
        <xsl:with-param name="setting" select="'listView/setLeadText'"/>
        <xsl:with-param name="default" select="'true'"/>
      </xsl:call-template>
    </xsl:variable>
    <xsl:variable name="setLeadTextLength">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="module"  select="'GenericOrganizationalUnit'"/>
        <xsl:with-param name="setting" select="'listView/setLeadTextLength'"/>
        <xsl:with-param name="default" select="'0'"/>
      </xsl:call-template>
    </xsl:variable>
    <xsl:variable name="setImage">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="module"  select="'GenericOrganizationalUnit'"/>
        <xsl:with-param name="setting" select="'listView/setImage'"/>
        <xsl:with-param name="default" select="'true'"/>
      </xsl:call-template>
    </xsl:variable>
    <xsl:variable name="setMoreButton">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="module"  select="'GenericOrganizationalUnit'"/>
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
                <xsl:call-template name="mandalay:moreButton">
                  <xsl:with-param name="href" select="nav:path"/>
                  <xsl:with-param name="module" select="'GenericOrganizationalUnit'"/>
                </xsl:call-template>
              </xsl:if>
            </xsl:if>
          </xsl:otherwise>
        </xsl:choose>
        <xsl:if test="$setMoreButton = 'true'">
          <xsl:call-template name="mandalay:moreButton">
            <xsl:with-param name="href" select="nav:path"/>
            <xsl:with-param name="module" select="'GenericOrganizationalUnit'"/>
          </xsl:call-template>
        </xsl:if>
      </span>
    </xsl:if>    

  </xsl:template>

  <xsl:template name="CT_GenericOrganizationalUnit_Link" match="*/cms:item/links[targetItem/objectType = 'com.arsdigita.cms.contenttypes.GenericOrganizationalUnit']" mode="link_view">

    <!-- DE Hole alle benötigten Einstellungen-->
    <!-- EN Getting all needed setting-->
    <xsl:variable name="setLinkToDetails">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="module"  select="'GenericOrganizationalUnit'"/>
        <xsl:with-param name="setting" select="'linkView/setLinkToDetails'"/>
        <xsl:with-param name="default" select="'true'"/>
      </xsl:call-template>
    </xsl:variable>
    <xsl:variable name="setImageAndText">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="module"  select="'GenericOrganizationalUnit'"/>
        <xsl:with-param name="setting" select="'linkView/setImageAndText'"/>
        <xsl:with-param name="default" select="'true'"/>
      </xsl:call-template>
    </xsl:variable>
    <xsl:variable name="setImage">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="module"  select="'GenericOrganizationalUnit'"/>
        <xsl:with-param name="setting" select="'linkView/setImage'"/>
        <xsl:with-param name="default" select="'true'"/>
      </xsl:call-template>
    </xsl:variable>
    <xsl:variable name="setImageMaxHeight">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="module"  select="'GenericOrganizationalUnit'"/>
        <xsl:with-param name="setting" select="'linkView/setImageMaxHeight'"/>
        <xsl:with-param name="default" select="''"/>
      </xsl:call-template>
    </xsl:variable>
    <xsl:variable name="setImageMaxWidth">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="module"  select="'GenericOrganizationalUnit'"/>
        <xsl:with-param name="setting" select="'linkView/setImageMaxWidth'"/>
        <xsl:with-param name="default" select="''"/>
      </xsl:call-template>
    </xsl:variable>
    <xsl:variable name="setImageCaption">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="module"  select="'GenericOrganizationalUnit'"/>
        <xsl:with-param name="setting" select="'linkView/setImageCaption'"/>
        <xsl:with-param name="default" select="'true'"/>
      </xsl:call-template>
    </xsl:variable>
    <xsl:variable name="setDescription">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="module"  select="'GenericOrganizationalUnit'"/>
        <xsl:with-param name="setting" select="'linkView/setDescription'"/>
        <xsl:with-param name="default" select="'true'"/>
      </xsl:call-template>
    </xsl:variable>
    <xsl:variable name="setDescriptionLength">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="module"  select="'GenericOrganizationalUnit'"/>
        <xsl:with-param name="setting" select="'linkView/setDescriptionLength'"/>
        <xsl:with-param name="default" select="'0'"/>
      </xsl:call-template>
    </xsl:variable>
    <xsl:variable name="setMoreButton">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="module"  select="'GenericOrganizationalUnit'"/>
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
                  <xsl:with-param name="module" select="'GenericOrganizationalUnit'"/>
                </xsl:call-template>
              </xsl:if>
            </xsl:if>
          </xsl:otherwise>
        </xsl:choose>
        <xsl:if test="$setMoreButton = 'true'">
          <xsl:call-template name="mandalay:moreButton">
            <xsl:with-param name="href" select="./targetItem/@oid"/>
            <xsl:with-param name="module" select="'GenericOrganizationalUnit'"/>
          </xsl:call-template>
        </xsl:if>
      </xsl:if>
    </xsl:if>
  </xsl:template>



</xsl:stylesheet>

