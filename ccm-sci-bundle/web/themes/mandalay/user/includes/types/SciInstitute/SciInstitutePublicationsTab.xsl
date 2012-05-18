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
       ****************************************************
       ** Template for the Members Tab for a SciInstitute  **
       ****************************************************
  -->

  <xsl:template name="CT_SciInstitute_graphics"
		match="institutePublications"
		mode="tabs">

    <xsl:variable name="setShowHeading">
      <xsl:call-template name="mandalay:getSetting">
	<xsl:with-param name="module" select="'SciInstitute'"/>
	<xsl:with-param name="setting" select="'tabs/setShowHeading'"/>
	<xsl:with-param name="default" select="'false'"/>
      </xsl:call-template>      
    </xsl:variable>

    <div class="activeTab">

    <xsl:if test="$setShowHeading = 'true'">
      <h2>
	<xsl:call-template name="mandalay:getStaticText">
	<xsl:with-param name="module" select="'SciInstitute'"/>
	<xsl:with-param name="id" select="'publicationsTab/heading'"/>
	</xsl:call-template>            
      </h2>
    </xsl:if>

    <xsl:if test="./filters">
      <form action="" method="get" accept-charset="UTF-8">
	<input type="hidden" name="selectedTab">
	  <xsl:attribute name="value">
	    <xsl:value-of select="../../availableTabs/availableTab[@selected='true']/@label"/>
	  </xsl:attribute>
	</input>
	<fieldset>
	  <legend>
	    <xsl:call-template name="mandalay:getStaticText">
	      <xsl:with-param name="module" select="'SciInstitute'"/>
	      <xsl:with-param name="id" select="'publicationsTab/filters/heading'"/>
	    </xsl:call-template>
	  </legend>

	  <xsl:for-each select="./filters/filter">
	    <xsl:choose>
	      <xsl:when test="./@type = 'select'">
		<label>
		  <xsl:attribute name="for">
		    <xsl:value-of select="./@label"/>
		  </xsl:attribute>
		  <xsl:call-template name="mandalay:getStaticText">
		    <xsl:with-param name="module" select="'SciInstitute'"/>
		    <xsl:with-param name="id" select="concat('publicationsTab/filters/', ./@label, '/label')"/>
		  </xsl:call-template>
		</label>
		<select size="1">
		  <xsl:attribute name="id">
		    <xsl:value-of select="./@label"/>
		  </xsl:attribute>
		  <xsl:attribute name="name">
		    <xsl:value-of select="./@label"/>
		  </xsl:attribute>
		  <xsl:for-each select="./option">
		    <option>
		      <xsl:if test="./@label = ../@selected">
			<xsl:attribute name="selected">selected</xsl:attribute>
		      </xsl:if>
		      <xsl:attribute name="value">
			<xsl:value-of select="./@label"/>
		      </xsl:attribute>
		      <xsl:choose>
			<xsl:when test="./@label = '--ALL--'">
			  <xsl:call-template name="mandalay:getStaticText">
			    <xsl:with-param name="module" select="'SciInstitute'"/>
			    <xsl:with-param name="id" select="concat('publicationsTab/filters/', ../@label, '/all')"/>
			  </xsl:call-template>
			</xsl:when>
			<xsl:when test="./@label = '--NONE--'">
			  <xsl:text> </xsl:text>
			</xsl:when>
			<xsl:otherwise>
			  <xsl:value-of select="@label"/>
			</xsl:otherwise>
		      </xsl:choose>
		    </option>
		  </xsl:for-each>
		</select>
	      </xsl:when>
	      <xsl:when test="./@type = 'text'">
		<label>
		  <xsl:attribute name="for">
		    <xsl:value-of select="./@label"/>
		  </xsl:attribute>		  
		  <xsl:call-template name="mandalay:getStaticText">
		    <xsl:with-param name="module" select="'SciInstitute'"/>
		    <xsl:with-param name="id" select="concat('publicationsTab/filters/', ./@label, '/label')"/>
		  </xsl:call-template>            	    		  
		</label>
		<input type="text">
		  <xsl:attribute name="id">
		    <xsl:value-of select="./@label"/>
		  </xsl:attribute>
		  <xsl:attribute name="name">
		    <xsl:value-of select="./@label"/>
		  </xsl:attribute>
		  <xsl:attribute name="size">
		    <xsl:call-template name="mandalay:getSetting">
		      <xsl:with-param name="module" select="'SciInstitute'"/>
		      <xsl:with-param name="setting" select="concat('publicationsTab/filters/', ./@label, '/size')"/>
		      <xsl:with-param name="default" select="'16'"/>
		    </xsl:call-template>      
		  </xsl:attribute>
		  <xsl:attribute name="maxlength">
		    <xsl:call-template name="mandalay:getSetting">
		      <xsl:with-param name="module" select="'SciInstitute'"/>
		      <xsl:with-param name="setting" select="concat('publicationsTab/filters/', ./@label, '/maxlength')"/>
		      <xsl:with-param name="default" select="'256'"/>
		    </xsl:call-template>      
		  </xsl:attribute>
		  <xsl:if test="./@value">
		    <xsl:attribute name="value">
		      <xsl:value-of select="./@value"/>
		    </xsl:attribute>
		  </xsl:if>
		</input>
	      </xsl:when>
	    </xsl:choose>
	  </xsl:for-each>

	  <div class="filterSubmitResetSection">
	    <input type="submit">
	      <xsl:attribute name="value">
		<xsl:call-template name="mandalay:getStaticText">
		  <xsl:with-param name="module" select="'SciInstitute'"/>
		  <xsl:with-param name="id" select="'publicationsTab/filters/submit'"/>
	      </xsl:call-template>            	    	      
	      </xsl:attribute>
	  </input>
	  <a class="completeResetButtonLink">
	    <xsl:attribute name="href">?selectedTab=<xsl:value-of select="../../availableTabs/availableTab[@selected='true']/@label"/></xsl:attribute>
	    <xsl:call-template name="mandalay:getStaticText">
	      <xsl:with-param name="module" select="'SciInstitute'"/>
	      <xsl:with-param name="id" select="'projectsTab/filters/reset'"/>
	    </xsl:call-template>            	    	      	    
	  </a>
	  <!--	  <input type="reset">
	      <xsl:attribute name="value">
	      <xsl:call-template name="mandalay:getStaticText">
	      <xsl:with-param name="module" select="'SciInstitute'"/>
	      <xsl:with-param name="id" select="'publicationsTab/filters/reset'"/>
	      </xsl:call-template>            	    	      
	      </xsl:attribute>
	      </input> -->
	  </div>
	</fieldset>
      </form>
    </xsl:if>

    <xsl:if test="./greeting">
      <xsl:call-template name="mandalay:getStaticText">
	<xsl:with-param name="module" select="'SciInstitute'"/>
	<xsl:with-param name="id" select="'publicationsTab/greeting'"/>
      </xsl:call-template>
    </xsl:if>

    <xsl:apply-templates select="./nav:paginator" mode="header"/>
    <xsl:apply-templates select="./nav:paginator" mode="navbar"/>

    <xsl:if test="./publications">
      <ul class="publicationList">
	<xsl:for-each select="publications">
	  <li class="sciPublication">
	    <xsl:apply-templates select="." mode="list_view"/>
	  </li>
	</xsl:for-each>
      </ul>
    </xsl:if>
   
    <xsl:apply-templates select="./nav:paginator" mode="navbar"/>

    <xsl:if test="./noPublications">
      <div class="noProjects noResult">
	<em>
	  <xsl:call-template name="mandalay:getStaticText">
	    <xsl:with-param name="module" select="'SciInstitute'"/>
	    <xsl:with-param name="id" select="'publicationsTab/noPublications'"/>
	  </xsl:call-template>
	</em>
      </div>
    </xsl:if>

    <div class="endFloat"/>
    </div>
      
  </xsl:template>

</xsl:stylesheet>
