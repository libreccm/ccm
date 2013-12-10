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
      Detail view
      ===========
  -->
  <!-- DE Leadtext -->
  <!-- EN lead text view -->
  <xsl:template match="cms:item[objectType='com.arsdigita.cms.contenttypes.Publisher']" mode="lead">
    <xsl:variable name="setLeadText">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="module"  select="'SciPublication'"/>
        <xsl:with-param name="setting" select="'publisher/setLeadText'"/>
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
  <xsl:template match="cms:item[objectType='com.arsdigita.cms.contenttypes.Publisher']" mode="image">

    <!-- DE Hole alle benötigten Einstellungen-->
    <!-- EN Getting all needed setting-->
    <xsl:variable name="setImage">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="module"  select="'SciPublication'"/>
        <xsl:with-param name="setting" select="'publisher/setImage'"/>
        <xsl:with-param name="default" select="'true'"/>
      </xsl:call-template>
    </xsl:variable>
    <xsl:variable name="setImageMaxHeight">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="module"  select="'SciPublication'"/>
        <xsl:with-param name="setting" select="'publisher/setImageMaxHeight'"/>
        <xsl:with-param name="default" select="''"/>
      </xsl:call-template>
    </xsl:variable>
    <xsl:variable name="setImageMaxWidth">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="module"  select="'SciPublication'"/>
        <xsl:with-param name="setting" select="'publisher/setImageMaxWidth'"/>
        <xsl:with-param name="default" select="''"/>
      </xsl:call-template>
    </xsl:variable>
    <xsl:variable name="setImageCaption">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="module"  select="'SciPublication'"/>
        <xsl:with-param name="setting" select="'publisher/setImageCaption'"/>
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

  <xsl:template name="CT_Publisher_graphics"
		match="cms:item[objectType='com.arsdigita.cms.contenttypes.Publisher']"
		mode="detailed_view">

    <div class="publication publicationDetails publisherDetails">

      <dl>
	<dt>
	  <xsl:call-template name="mandalay:getStaticText">
	      <xsl:with-param name="module" select="'SciPublications'"/>
	      <xsl:with-param name="id" select="'publisher/name'"/>	      
	  </xsl:call-template>	  
	</dt>
	<dd>
	  <xsl:value-of select="./publisherName"/>	  
	</dd>
	<dt>
	  <xsl:call-template name="mandalay:getStaticText">
	      <xsl:with-param name="module" select="'SciPublications'"/>
	      <xsl:with-param name="id" select="'publisher/place'"/>	      
	  </xsl:call-template>
	</dt>
	<dd>
	  <xsl:value-of select="./place"/>
	</dd>

      </dl>

    </div>
    

  </xsl:template>

</xsl:stylesheet>
