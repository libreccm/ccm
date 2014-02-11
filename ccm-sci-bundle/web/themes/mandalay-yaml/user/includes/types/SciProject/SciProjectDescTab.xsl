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

<xsl:stylesheet
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
  xmlns:bebop="http://www.arsdigita.com/bebop/1.0"
  xmlns:nav="http://ccm.redhat.com/navigation"
  xmlns:cms="http://www.arsdigita.com/cms/1.0"
  xmlns:mandalay="http://mandalay.quasiweb.de"
  exclude-result-prefixes="xsl bebop cms nav mandalay"
  version="1.0"
  >

  <!-- 
       ************************************************
       ** Template for the DescTab for a SciProject  **
       ************************************************
  -->

  <xsl:template name="CT_SciProject_graphics"
		match="projectDescription"
		mode="tabs">   

    <xsl:variable name="setImage">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="module"  select="'SciProject'"/>
        <xsl:with-param name="setting" select="'descTab/setImage'"/>
        <xsl:with-param name="default" select="'true'"/>
      </xsl:call-template>
    </xsl:variable>
    <xsl:variable name="setImageCaption">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="module"  select="'SciProject'"/>
        <xsl:with-param name="setting" select="'tabs/setImageCaption'"/>
        <xsl:with-param name="default" select="'true'"/>
      </xsl:call-template>
    </xsl:variable>
    <xsl:variable name="setImageMaxHeight">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="module"  select="'SciProject'"/>
        <xsl:with-param name="setting" select="'tabs/setImageMaxHeight'"/>
        <xsl:with-param name="default" select="''"/>
      </xsl:call-template>
    </xsl:variable>
    <xsl:variable name="setImageMaxWidth">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="module"  select="'SciProject'"/>
        <xsl:with-param name="setting" select="'tabs/setImageMaxWidth'"/>
        <xsl:with-param name="default" select="''"/>
      </xsl:call-template>
    </xsl:variable>
    <xsl:variable name="setShowHeading">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="module" select="'SciProject'"/>
        <xsl:with-param name="setting" select="'tabs/setShowHeading'"/>
        <xsl:with-param name="default" select="'false'"/>
      </xsl:call-template>      
    </xsl:variable>
    <xsl:variable name="setShortDesc">
      <xsl:call-template name="mandalay:getSetting">
	<xsl:with-param name="module" select="'SciProject'"/>
	<xsl:with-param name="setting" select="'descTab/setShortDesc'"/>
	<xsl:with-param name="default" select="'true'"/>
      </xsl:call-template>
    </xsl:variable>
    <xsl:variable name="setFunding">
      <xsl:call-template name="mandalay:getSetting">
	<xsl:with-param name="module" select="'SciProject'"/>
	<xsl:with-param name="setting" select="'descTab/setFunding'"/>
	<xsl:with-param name="default" select="'false'"/>
      </xsl:call-template>
    </xsl:variable>
    <xsl:variable name="setFundingVolume">
      <xsl:call-template name="mandalay:getSetting">
	<xsl:with-param name="module" select="'SciProject'"/>
	<xsl:with-param name="setting" select="'descTab/setFundingVolume'"/>
	<xsl:with-param name="default" select="'true'"/>
      </xsl:call-template>
    </xsl:variable>

    <div class="activeTab">

    <xsl:if test="$setImage = 'true'">
      <xsl:for-each  select="$resultTree//cms:item">
        <xsl:call-template name="mandalay:imageAttachment">
          <xsl:with-param name="showCaption" select="$setImageCaption" />
          <xsl:with-param name="maxHeight" select="$setImageMaxHeight" />
          <xsl:with-param name="maxWidth" select="$setImageMaxWidth" />
        </xsl:call-template>
      </xsl:for-each>
    </xsl:if>

    <xsl:if test="$setShowHeading = 'true'">
      <h2>
      <xsl:call-template name="mandalay:getStaticText">
	<xsl:with-param name="module" select="'SciProject'"/>
	<xsl:with-param name="id" select="'descTab/heading'"/>
      </xsl:call-template>      
      </h2>
    </xsl:if>

    <xsl:if test="$setShortDesc = 'true'">
      <div id="lead">
	<xsl:value-of disable-output-escaping="yes" select="./shortDescription"/>
      </div>
    </xsl:if>
    
    <div class="mainBody projectDesc">
      <xsl:value-of disable-output-escaping="yes" select="./description"/>
    </div>

    <h3>
      <xsl:call-template name="mandalay:getStaticText">
	<xsl:with-param name="module" select="'SciProject'"/>
	<xsl:with-param name="id" select="'descTab/fundingHeading'"/>
      </xsl:call-template>      
    </h3>    
    <div class="mainBody projectFunding">
      <xsl:value-of disable-output-escaping="yes" select="./funding"/>
    </div>
    
	<xsl:if test="(string-length(./fundingVolume) &gt; 0) and ($setFundingVolume = 'true')">
      <div class="projectFundingVolume">
	<span>
	  <xsl:call-template name="mandalay:getStaticText">
	    <xsl:with-param name="module" select="'SciProject'"/>
	    <xsl:with-param name="id" select="'descTab/fundingVolume'"/>
	  </xsl:call-template>      	  
	</span>	
		<span>
		  <xsl:value-of select="./fundingVolume"/>
		</span>
      </div>
      
      <xsl:if test="(string-length(./fundingVolume) &gt; 0) and ($setFundingVolume = 'true')">
	<div class="projectFundingVolume">
	  <span>
	    <xsl:call-template name="mandalay:getStaticText">
	      <xsl:with-param name="module" select="'SciProject'"/>
	      <xsl:with-param name="id" select="'descTab/fundingVolume'"/>
	    </xsl:call-template>      	  
	  </span>	
	  <span>
	    <xsl:value-of select="./fundingVolume"/>
	  </span>
	</div>
      </xsl:if>
      
      <div class="endFloat"/>
    </xsl:if>
    
    <div class="endFloat"/>
    </div>

  </xsl:template>

</xsl:stylesheet>
