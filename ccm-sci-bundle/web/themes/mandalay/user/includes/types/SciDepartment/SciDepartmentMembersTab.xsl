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
       ** Template for the Members Tab for a SciProject  **
       ****************************************************
  -->

  <xsl:template name="CT_SciDepartment_graphics"
		match="departmentMembers"
		mode="tabs">

    <xsl:variable name="setShowHeading">
      <xsl:call-template name="mandalay:getSetting">
	<xsl:with-param name="module" select="'SciDepartment'"/>
	<xsl:with-param name="setting" select="'tabs/setShowHeading'"/>
	<xsl:with-param name="default" select="'false'"/>
      </xsl:call-template>      
    </xsl:variable>
    <xsl:variable name="linkMembers">
      <xsl:call-template name="mandalay:getSetting">
	<xsl:with-param name="module" select="'SciDepartment'"/>
	<xsl:with-param name="setting" select="'membersTab/linkMembers'"/>
	<xsl:with-param name="default" select="'true'"/>
      </xsl:call-template>
    </xsl:variable>
    <xsl:variable name="selectedStatus">
      <xsl:value-of select="filters/filter[@label='memberStatus']/@selected"/>
    </xsl:variable>
    <xsl:variable name="showContactData">
      <xsl:call-template name="mandalay:getSetting">
	<xsl:with-param name="module" select="'SciInstitute'"/>
	<xsl:with-param name="setting" select="concat('membersTab/', $selectedStatus, '/showContactData')"/>
	<xsl:with-param name="default" select="'true'"/>
      </xsl:call-template>            
    </xsl:variable>
    <xsl:variable name="showAddress">
      <xsl:call-template name="mandalay:getSetting">
	<xsl:with-param name="module" select="'SciInstitute'"/>
	<xsl:with-param name="setting" select="concat('membersTab/', $selectedStatus, '/showAddress')"/>
	<xsl:with-param name="default" select="'false'"/>
      </xsl:call-template>            
    </xsl:variable>

    <div class="activeTab">

    <xsl:if test="$setShowHeading = 'true'">
      <h2>
	<xsl:call-template name="mandalay:getStaticText">
	  <xsl:with-param name="module" select="'SciDepartment'"/>
	  <xsl:with-param name="id" select="'membersTab/heading'"/>
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
	      <xsl:with-param name="module" select="'SciDepartment'"/>
	      <xsl:with-param name="id" select="'membersTab/filters/heading'"/>
	    </xsl:call-template>            	    
	  </legend>

	  <xsl:for-each select="./filters/filter">
	    <xsl:choose>
	      <xsl:when test="./@type = 'compare'">
		<label>
		  <xsl:attribute name="for">
		    <xsl:value-of select="./@label"/>
		  </xsl:attribute>		  
		  <xsl:call-template name="mandalay:getStaticText">
		    <xsl:with-param name="module" select="'SciDepartment'"/>
		    <xsl:with-param name="id" select="concat('membersTab/filters/', ./@label, '/label')"/>
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
		      <xsl:call-template name="mandalay:getStaticText">
			<xsl:with-param name="module" select="'SciDepartment'"/>
			<xsl:with-param name="id" select="concat('membersTab/filters/', ../@label, '/', ./@label)"/>
		      </xsl:call-template>            	    		      
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
		    <xsl:with-param name="module" select="'SciDepartment'"/>
		    <xsl:with-param name="id" select="concat('membersTab/filters/', ./@label, '/label')"/>
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
		      <xsl:with-param name="module" select="'SciDepartment'"/>
		      <xsl:with-param name="setting" select="concat('membersTab/filters/', ./@label, '/size')"/>
		      <xsl:with-param name="default" select="'16'"/>
		    </xsl:call-template>      
		  </xsl:attribute>
		  <xsl:attribute name="maxlength">
		    <xsl:call-template name="mandalay:getSetting">
		      <xsl:with-param name="module" select="'SciDepartment'"/>
		      <xsl:with-param name="setting" select="concat('membersTab/filters/', ./@label, '/maxlength')"/>
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
		<xsl:with-param name="module" select="'SciDepartment'"/>
		<xsl:with-param name="id" select="'membersTab/filters/submit'"/>
		</xsl:call-template>            	    	      
	      </xsl:attribute>
	    </input>
	    <a class="completeResetButtonLink">
	      <xsl:attribute name="href">?selectedTab=<xsl:value-of select="../../availableTabs/availableTab[@selected='true']/@label"/></xsl:attribute>
	      <xsl:call-template name="mandalay:getStaticText">
		<xsl:with-param name="module" select="'SciInstitute'"/>
		<xsl:with-param name="id" select="'membersTab/filters/reset'"/>
	      </xsl:call-template>            	    	      	    
	    </a>
	    <!--<input type="reset">
	      <xsl:attribute name="value">
		<xsl:call-template name="mandalay:getStaticText">
		  <xsl:with-param name="module" select="'SciDepartment'"/>
		  <xsl:with-param name="id" select="'membersTab/filters/reset'"/>
		</xsl:call-template>            	    	      
	      </xsl:attribute>
	    </input>-->
	  </div>
	</fieldset>
      </form>
    </xsl:if>

    <xsl:apply-templates select="./nav:paginator" mode="header"/>
    <xsl:apply-templates select="./nav:paginator" mode="navbar"/>
    
    <ul class="memberList departmentMemberList">
      <xsl:for-each select="./member">
	<li class="sciMember">
	  <xsl:choose>
	    <xsl:when test="($linkMembers = 'true') and ($showContactData = 'true') and ./contacts/contact[@contactType='commonContact']/contactentries[keyId='homepage']/value">
	      <a class="CIname memberName">
		<xsl:attribute name="href">
		  <xsl:value-of select="./contacts/contact[@contactType='commonContact']/contactentries[keyId='homepage']/value"/>
		</xsl:attribute>
		  <xsl:if test="string-length(./titlePre) &gt; 0">
		    <xsl:value-of select="./titlePre"/>
		    <xsl:text> </xsl:text>
		  </xsl:if>
		  <xsl:if test="string-length(./givenname) &gt; 0">
		    <xsl:value-of select="./givenname"/>
		    <xsl:text> </xsl:text>		    
		  </xsl:if>
		  <xsl:if test="string-length(./givenname) &gt; 0">
		    <xsl:value-of select="./surname"/>
		  </xsl:if>
		  <xsl:if test="string-length(./titlePost) &gt; 0">
		    <xsl:text>, </xsl:text>
		    <xsl:value-of select="./titlePost"/>
		  </xsl:if>	 
	      </a>
	    </xsl:when>
	    <xsl:otherwise>
	      <span class="CIname memberName">
		<xsl:if test="string-length(./titlePre) &gt; 0">
		  <xsl:value-of select="./titlePre"/>
		  <xsl:text> </xsl:text>
		</xsl:if>
		<xsl:if test="string-length(./givenname) &gt; 0">
		  <xsl:value-of select="./givenname"/>
		  <xsl:text> </xsl:text>		    
		</xsl:if>
		<xsl:if test="string-length(./givenname) &gt; 0">
		  <xsl:value-of select="./surname"/>
		</xsl:if>
		<xsl:if test="string-length(./titlePost) &gt; 0">
		  <xsl:text>, </xsl:text>
		    <xsl:value-of select="./titlePost"/>
		</xsl:if>	 	      
	      </span>
	    </xsl:otherwise>
	  </xsl:choose>

	  <xsl:if test="$showContactData = 'true'">
	  <span class="contact">	    
	    
	    <xsl:for-each select="./contacts/contact[@contactType='commonContact']/contactentries">
	      <xsl:sort select="key"/>
	      <xsl:variable name="showContactEntry">
		<xsl:call-template name="mandalay:getSetting">
		  <xsl:with-param name="module" select="'SciDepartment'"/>
		  <xsl:with-param name="setting" select="concat('membersTab/',$selectedStatus,'/contactentry/',./keyId,'/show')"/>
		  <xsl:with-param name="default" select="false"/>
		</xsl:call-template>
	      </xsl:variable>
	      
	      <xsl:if test="$showContactEntry = 'true'">
		<span class="contactentry">
		  <span class="contactentryKey">
		    <xsl:value-of select="./key"/>
		    <xsl:variable name="separator">
		      <xsl:call-template name="mandalay:getStaticText">
			<xsl:with-param name="module" select="'GenericContact'"/>
			<xsl:with-param name="id" select="'separator'"/>
		      </xsl:call-template>
		      </xsl:variable>
		      <xsl:call-template name="mandalay:string-replace">
			<xsl:with-param name="string" select="$separator"/>
			<xsl:with-param name="from" select="' '"/>
			<xsl:with-param name="to" select="'&nbsp;'"/>
		      </xsl:call-template>
		    </span>		    
		    <span class="contactentryValue">
		      <xsl:choose>
			<xsl:when test="contains(./value, '@')">
			  <a>
			    <xsl:attribute name="href">
			      <xsl:value-of select="concat('mailto:', ./value)"/>
			    </xsl:attribute>
			    <xsl:value-of select="./value"/>
			  </a>
			</xsl:when>
			<xsl:otherwise>
			  <xsl:call-template name="mandalay:string-replace">
			    <xsl:with-param name="string" select="./value"/>
			    <xsl:with-param name="from" select="' '"/>
			    <xsl:with-param name="to" select="'&nbsp;'"/>
			  </xsl:call-template>
			  <!--<xsl:value-of select="./value"/>-->
			</xsl:otherwise>
		      </xsl:choose>
		    </span>		      
		  </span>
		  <xsl:text> </xsl:text>
		</xsl:if>
	      </xsl:for-each>
	      <xsl:if test="($showAddress = 'true') and (string-length(./contacts/address/address) &gt; 0)">
		<br/>
		<span class="address">
		  <xsl:variable name="addressTxt">
		    <xsl:call-template name="mandalay:string-replace">
		      <xsl:with-param name="string" select="./contacts/address/address"/>
		      <xsl:with-param name="from" select="'&#xA;'"/>
		    <xsl:with-param name="to" select="', '"/>
		    </xsl:call-template>
		  </xsl:variable>			
		  <span class="addressTxt">
		    <xsl:value-of select="$addressTxt"/>
		  </span>
		  <xsl:text>, </xsl:text>
		  <span class="postalCode">
		    <xsl:value-of select="./contacts/address/postalCode"/>
		  </span>
		  <xsl:text> </xsl:text>
		  <span class="city">
		    <xsl:value-of select="./contacts/address/city"/>
		  </span>
		</span>
	      </xsl:if>	   


<!--	    <xsl:if test="($showPhone = 'true') and (./contacts/contactentries[keyId='phoneOffice'])">
	      <span class="contactentry phoneOffice">
	      <span class="contactentryKey">
		<xsl:value-of select="./contacts/contactentries[keyId='phoneOffice']/key"/>
		<xsl:call-template name="mandalay:getStaticText">
		  <xsl:with-param name="module" select="'GenericContact'"/>
		  <xsl:with-param name="id" select="'separator'"/>
		</xsl:call-template>
	      </span>
	      <span class="contactentryValue phone">
		<xsl:value-of select="./contacts/contactentries[keyId='phoneOffice']/value"/>
	      </span>
	      </span>
	    </xsl:if>
	    <xsl:if test="($showEmail = 'true') and (./contacts/contactentries[keyId='email'])">
	      <span class="contactentry email">
	      <span class="contactentryKey">
		<xsl:value-of select="./contacts/contactentries[keyId='email']/key"/>
		<xsl:call-template name="mandalay:getStaticText">
		  <xsl:with-param name="module" select="'GenericContact'"/>
		  <xsl:with-param name="id" select="'separator'"/>
		</xsl:call-template>
	      </span>
	      <span class="contactentryValue email">
		<xsl:value-of select="./contacts/contactentries[keyId='email']/value"/>
	      </span>
	      </span>
	  </xsl:if>-->
	  </span>
	  </xsl:if>
	</li>
      </xsl:for-each>
    </ul>

    <xsl:apply-templates select="./nav:paginator" mode="navbar"/>

    <div class="endFloat"/>
    </div>

  </xsl:template>

</xsl:stylesheet>
