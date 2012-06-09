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
      **************************************************************************
      ** Templates for an Expertise publication                               **
      **************************************************************************
  -->

  <!-- 
       Detail view 
       ===========
  -->
    <xsl:template name="CT_Expertise_graphics"
		match="cms:item[objectType='com.arsdigita.cms.contenttypes.Expertise']"
		mode="detailed_view">
    <xsl:variable name="setImage">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="module"  select="'SciPublications'"/>
        <xsl:with-param name="setting" select="'expertise/setImage'"/>
        <xsl:with-param name="default" select="'true'"/>
      </xsl:call-template>
    </xsl:variable>
    <xsl:variable name="setImageCaption">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="module"  select="'SciPublications'"/>
        <xsl:with-param name="setting" select="'expertise/setImageCaption'"/>
        <xsl:with-param name="default" select="'true'"/>
      </xsl:call-template>
    </xsl:variable>
    <xsl:variable name="setImageMaxHeight">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="module"  select="'SciPublications'"/>
        <xsl:with-param name="setting" select="'expertise/setImageMaxHeight'"/>
        <xsl:with-param name="default" select="''"/>
      </xsl:call-template>
    </xsl:variable>
    <xsl:variable name="setImageMaxWidth">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="module"  select="'SciPublications'"/>
        <xsl:with-param name="setting" select="'expertise/setImageMaxWidth'"/>
        <xsl:with-param name="default" select="''"/>
      </xsl:call-template>
    </xsl:variable>

    <xsl:variable name="setAbstract">
      <xsl:call-template name="mandalay:getSetting">
	<xsl:with-param name="module" select="'SciPublications'"/>
	<xsl:with-param name="setting" select="'expertise/setAbstract'"/>
	<xsl:with-param name="default" select="'true'"/>
      </xsl:call-template>
    </xsl:variable>
    <xsl:variable name="setAuthors">
      <xsl:call-template name="mandalay:getSetting">
	<xsl:with-param name="module" select="'SciPublications'"/>
	<xsl:with-param name="setting" select="'expertise/setAuthors'"/>
	<xsl:with-param name="default" select="'true'"/>	
      </xsl:call-template>
    </xsl:variable>
    <xsl:variable name="setMisc">
      <xsl:call-template name="mandalay:getSetting">
	<xsl:with-param name="module" select="'SciPublications'"/>
	<xsl:with-param name="setting" select="'expertise/setMisc'"/>
	<xsl:with-param name="default" select="'true'"/>	
      </xsl:call-template>
    </xsl:variable>
    <xsl:variable name="setNumberOfPages">
      <xsl:call-template name="mandalay:getSetting">
	<xsl:with-param name="module" select="'SciPublications'"/>
	<xsl:with-param name="setting" select="'expertise/setNumberOfPages'"/>
	<xsl:with-param name="default" select="'true'"/>	
      </xsl:call-template>
    </xsl:variable>
    <xsl:variable name="setOrderer">
      <xsl:call-template name="mandalay:getSetting">
	<xsl:with-param name="module" select="'SciPublications'"/>
	<xsl:with-param name="setting" select="'expertise/setOrderer'"/>
	<xsl:with-param name="default" select="'true'"/>
      </xsl:call-template>
    </xsl:variable>
    <xsl:variable name="setOrganization">
      <xsl:call-template name="mandalay:getSetting">
	<xsl:with-param name="module" select="'SciPublications'"/>
	<xsl:with-param name="setting" select="'expertise/setOrganization'"/>
	<xsl:with-param name="default" select="'true'"/>
      </xsl:call-template>      
    </xsl:variable>
    <xsl:variable name="setPlace">
      <xsl:call-template name="mandalay:getSetting">
	<xsl:with-param name="module" select="'SciPublications'"/>
	<xsl:with-param name="setting" select="'expertise/setPlace'"/>
	<xsl:with-param name="default" select="'true'"/>
      </xsl:call-template>      
    </xsl:variable>
    <xsl:variable name="setSeries">
      <xsl:call-template name="mandalay:getSetting">
	<xsl:with-param name="module" select="'SciPublications'"/>
	<xsl:with-param name="setting" select="'expertise/setSeries'"/>
	<xsl:with-param name="default" select="'false'"/>	
      </xsl:call-template>
    </xsl:variable>
    <xsl:variable name="setUrl">
      <xsl:call-template name="mandalay:getSetting">
	<xsl:with-param name="module" select="'SciPublications'"/>
	<xsl:with-param name="setting" select="'expertise/setUrl'"/>
	<xsl:with-param name="default" select="'true'"/>	
      </xsl:call-template>
    </xsl:variable>
    <xsl:variable name="setYear">
      <xsl:call-template name="mandalay:getSetting">
	<xsl:with-param name="module" select="'SciPublications'"/>
	<xsl:with-param name="setting" select="'expertise/setYear'"/>
	<xsl:with-param name="default" select="'true'"/>	
      </xsl:call-template>
    </xsl:variable>

    <div class="mainBody publication publicationDetails publicationExpertiseDetails">
      <xsl:if test="$setImage = 'true'">
        <xsl:call-template name="mandalay:imageAttachment">
          <xsl:with-param name="showCaption" select="$setImageCaption" />
          <xsl:with-param name="maxHeight" select="$setImageMaxHeight" />
          <xsl:with-param name="maxWidth" select="$setImageMaxWidth" />
        </xsl:call-template>
      </xsl:if>

      <dl>
	<xsl:if test="($setAuthors = 'true') and (string-length(./authors) &gt; 0)">
	  <xsl:call-template name="scipublicationsAuthors">
	    <xsl:with-param name="authors" select="./authors/author"/>
	    <xsl:with-param name="authorText">
	      <xsl:call-template name="mandalay:getStaticText">
		<xsl:with-param name="module" select="'SciPublications'"/>
		<xsl:with-param name="id" select="'expertise/author'"/>
	      </xsl:call-template>	      
	    </xsl:with-param>
	    <xsl:with-param name="authorsText">
	      <xsl:call-template name="mandalay:getStaticText">
		<xsl:with-param name="module" select="'SciPublications'"/>
		<xsl:with-param name="id" select="'expertise/authors'"/>
	      </xsl:call-template>
	    </xsl:with-param>
	  </xsl:call-template>
	</xsl:if>

	<xsl:if test="($setYear = 'true') and (string-length(./yearOfPublication) &gt; 0)">
	  <dt>
	    <xsl:call-template name="mandalay:getStaticText">
	      <xsl:with-param name="module" select="'SciPublications'"/>
	      <xsl:with-param name="id" select="'expertise/year'"/>
	    </xsl:call-template>	    
	  </dt>
	  <dd>
	    <xsl:value-of select="./yearOfPublication"/>
	  </dd>
	</xsl:if>

	<xsl:if test="($setPlace = 'true') and (string-length(./place) &gt; 0)">
	  <dt>
	    <xsl:call-template name="mandalay:getStaticText">
	      <xsl:with-param name="module" select="'SciPublications'"/>
	      <xsl:with-param name="id" select="'expertise/place'"/>
	    </xsl:call-template>	    	    
	  </dt>
	  <dd>
	    <xsl:value-of select="./place"/>
	  </dd>
	</xsl:if>

	<xsl:if test="($setUrl = 'true') and (string-length(./url) &gt; 0)">
	  <dt>
	    <xsl:call-template name="mandalay:getStaticText">
	      <xsl:with-param name="module" select="'SciPublications'"/>
	      <xsl:with-param name="id" select="'expertise/url'"/>	      
	    </xsl:call-template>
	  </dt>
	  <dd>
	    <a>
	      <xsl:attribute name="href">
		<xsl:value-of select="./url"/>x
	      </xsl:attribute>
	      <xsl:value-of select="./url"/>
	    </a>
	  </dd>
	</xsl:if>

	<xsl:if test="($setNumberOfPages = 'true') and (string-length(./numberOfPages) &gt; 0)">
	  <dt>
	    <xsl:call-template name="mandalay:getStaticText">
	      <xsl:with-param name="module" select="'SciPublications'"/>
	      <xsl:with-param name="id" select="'expertise/numberOfPages'"/>
	    </xsl:call-template>	    
	  </dt>
	  <dd>
	    <xsl:value-of select="./numberOfPages"/>
	  </dd>
	</xsl:if>

	<xsl:if test="($setSeries = 'true') and (string-length(./series) &gt; 0)">
	  <dt>
	    <xsl:call-template name="mandalay:getStaticText">
	      <xsl:with-param name="module" select="'SciPublications'"/>
	      <xsl:with-param name="id" select="'expertise/series'"/>
	    </xsl:call-template>	    	    
	  </dt>
	  <dd>
	    <xsl:value-of select="./series/title"/>
	  </dd>
	</xsl:if>	

	<xsl:if test="($setOrderer = 'true') and (string-length(./orderer) &gt; 0)">
	  <dt>
	    <xsl:call-template name="mandalay:getStaticText">
	      <xsl:with-param name="module" select="'SciPublications'"/>
	      <xsl:with-param name="id" select="'expertise/orderer'"/>	      
	    </xsl:call-template>
	  </dt>
	  <dd>
	    <xsl:value-of select="./orderer/title"/>
	  </dd>
	</xsl:if>

	<xsl:if test="($setOrganization = 'true') and (string-length(./organization) &gt; 0)">
	  <dt>
	    <xsl:call-template name="mandalay:getStaticText">
	      <xsl:with-param name="module" select="'SciPublications'"/>
	      <xsl:with-param name="id" select="'expertise/organization'"/>	      
	    </xsl:call-template>
	  </dt>
	  <dd>
	    <xsl:value-of select="./organization/title"/>
	  </dd>
	</xsl:if>
      </dl>

      <xsl:if test="($setAbstract = 'true') and (string-length(./abstract) &gt; 0)">
	<div class="publicationAbstract">
	  <h3>
	    <xsl:call-template name="mandalay:getStaticText">
	      <xsl:with-param name="module" select="'SciPublications'"/>
	      <xsl:with-param name="id" select="'expertise/abstract'"/>	    
	    </xsl:call-template>
	  </h3>
	  <div class="abstract">
	    <xsl:variable name="abstract">
	      <xsl:call-template name="mandalay:string-replace">
		<xsl:with-param name="string" select="./abstract"/>
		<xsl:with-param name="from" select="'&#xA;'"/>
		<xsl:with-param name="to" select="'&lt;br/>'"/>
	      </xsl:call-template>
	    </xsl:variable>	
	    <xsl:value-of disable-output-escaping="yes" select="$abstract"/>    
	  </div>
	</div>
      </xsl:if>

      <xsl:if test="($setMisc = 'true') and (string-length(normalize-space(./misc)) &gt; 0)">
	<div class="publicationMisc">
	  <h3>
	    <xsl:call-template name="mandalay:getStaticText">
	      <xsl:with-param name="module" select="'SciPublications'"/>
	      <xsl:with-param name="id" select="'expertise/misc'"/>	    
	    </xsl:call-template>
	  </h3>
	  <xsl:value-of select="./misc"/>
	</div>
      </xsl:if>      

      <!-- <xsl:call-template name="scipublicationsDownload"/> -->
    </div>

  </xsl:template>



  <!--
      List view
      =========
  -->

  <xsl:template match="publications[objectType='com.arsdigita.cms.contenttypes.Expertise']"
		mode="list_view">
    <xsl:param name="useRelativeUrl" select="'false'"/>
    <xsl:call-template name="CT_Expertise_List">
      <xsl:with-param name="useRelativeUrl" select="$useRelativeUrl"/>
    </xsl:call-template>
  </xsl:template>

  <xsl:template match="publication[objectType='com.arsdigita.cms.contenttypes.Expertise']"
		mode="list_view">
    <xsl:param name="useRelativeUrl" select="'false'"/>
    <xsl:call-template name="CT_Expertise_List">
      <xsl:with-param name="useRelativeUrl" select="$useRelativeUrl"/>
    </xsl:call-template>
  </xsl:template>

  <xsl:template 
    name="CT_Expertise_List" 
    match="nav:item[nav:attribute[@name='objectType'] = 'com.arsdigita.cms.contenttypes.Expertise']"
    mode="list_view">
    <xsl:param name="useRelativeUrl" select="'false'"/>
    <xsl:variable name="formatDefFile">
      <xsl:call-template name="mandalay:getSetting">
	<xsl:with-param name="module" select="'SciPublications'"/>
	<xsl:with-param name="setting" select="'formatDefFile'"/>
	<xsl:with-param name="default" select="'SciPublicationsDefaultFormat.xml'"/>
      </xsl:call-template>
    </xsl:variable>
    
    <xsl:variable name="authors">
      <xsl:for-each select="./authors/author">                   
	<xsl:sort select="./@order" data-type="number"/>
	<xsl:apply-templates select="document(concat($theme-prefix, '/settings/', $formatDefFile))/bibrefFormat/bibrefAuthorFormat">
	  <xsl:with-param name="surname" select="./surname"/>
	  <xsl:with-param name="givenName" select="./givenname"/>
	  <xsl:with-param name="isEditor" select="./@isEditor"/>
	  <xsl:with-param name="authorsCount" select="count(../author)"/>
	  <xsl:with-param name="position" select="position()"/>
	</xsl:apply-templates>
      </xsl:for-each>
    </xsl:variable>

    <!-- Call template for standard format -->
    <xsl:apply-templates select="document(concat($theme-prefix, '/settings/', $formatDefFile))/bibrefFormat/bibrefExpertiseFormat">
      <xsl:with-param name="authors" select="$authors"/>
      <xsl:with-param name="misc" select="./misc"/>
      <xsl:with-param name="numberOfPages" select="./numberofpages"/>
      <xsl:with-param name="place" select="./place"/>
      <xsl:with-param name="title" select="./title"/>
      <xsl:with-param name="year" select="./yearOfPublication"/>
      <xsl:with-param name="oid" select="./@oid"/>     
      <xsl:with-param name="useRelativeUrl" select="$useRelativeUrl"/>
    </xsl:apply-templates>
  </xsl:template>

  <!-- Link view -->
  <xsl:template 
    name="CT_Expertise_Link" 
    match="*/cms:item/links[targetItem/objectType = 'com.arsdigita.cms.contenttypes.Expertise']"
    mode="link_view">
    <!-- Simply call template for Publications, because there is not difference for list view between these
         two types -->
    <xsl:call-template name="CT_Publication_Link"/>
  </xsl:template>

</xsl:stylesheet>