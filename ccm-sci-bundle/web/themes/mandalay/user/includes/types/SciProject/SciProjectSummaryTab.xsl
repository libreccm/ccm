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
         ***************************************************
         ** Template for the SummaryTab for a SciProject  **
         ***************************************************
    -->

    <xsl:template name="CT_SciProject_graphics"
                  match="projectSummary"
                  mode="tabs">   

        <xsl:variable name="membersLimit">
            <xsl:call-template name="mandalay:getSetting">
                <xsl:with-param name="module" select="'SciProject'"/>
                <xsl:with-param name="setting" select="'summaryTab/membersLimit'"/>
                <xsl:with-param name="default" select="'2'"/>
            </xsl:call-template>
        </xsl:variable>
        <xsl:variable name="setImage">
            <xsl:call-template name="mandalay:getSetting">
                <xsl:with-param name="module"  select="'SciProject'"/>
                <xsl:with-param name="setting" select="'summaryTab/setImage'"/>
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
        <xsl:variable name="showSubProjects">
            <xsl:call-template name="mandalay:getSetting">
                <xsl:with-param name="module" select="'SciProject'"/>
                <xsl:with-param name="setting" select="'summaryTab/showSubProjects'"/>
                <xsl:with-param name="default" select="'true'"/>	
            </xsl:call-template>
        </xsl:variable>
        <xsl:variable name="linkMembers">
            <xsl:call-template name="mandalay:getSetting">
                <xsl:with-param name="module" select="'SciProject'"/>
                <xsl:with-param name="setting" select="'summaryTab/linkMembers'"/>
                <xsl:with-param name="default" select="'true'"/>
            </xsl:call-template>
        </xsl:variable>
        <xsl:variable name="setHeadPreText">
            <xsl:call-template name="mandalay:getSetting">
                <xsl:with-param name="module" select="'SciProject'"/>
                <xsl:with-param name="setting" select="'summaryTab/setHeadPreText'"/>
                <xsl:with-param name="default" select="'true'"/>
            </xsl:call-template>
        </xsl:variable>
        <xsl:variable name="setHeadPostText">
            <xsl:call-template name="mandalay:getSetting">
                <xsl:with-param name="module" select="'SciProject'"/>
                <xsl:with-param name="setting" select="'summaryTab/setHeadPostText'"/>
                <xsl:with-param name="default" select="'true'"/>
            </xsl:call-template>
        </xsl:variable>
        <xsl:variable name="setFormerMemberText">
            <xsl:call-template name="mandalay:getSetting">
                <xsl:with-param name="module" select="'SciProject'"/>
                <xsl:with-param name="setting" select="'summaryTab/setFormerMemberText'"/>
                <xsl:with-param name="default" select="'true'"/>
            </xsl:call-template>
        </xsl:variable>
        <xsl:variable name="setLifespan">
            <xsl:call-template name="mandalay:getSetting">
                <xsl:with-param name="module"  select="'SciProject'"/>
                <xsl:with-param name="setting" select="'summaryTab/setLifespan'"/>
                <xsl:with-param name="default" select="'false'"/>
            </xsl:call-template>
        </xsl:variable>
        <xsl:variable name="setSponsor">
            <xsl:call-template name="mandalay:getSetting">
                <xsl:with-param name="module" select="'SciProject'"/>
                <xsl:with-param name="setting" select="'summaryTab/setSponsor'"/>
                <xsl:with-param name="default" select="'true'"/>
            </xsl:call-template>
        </xsl:variable>
        <xsl:variable name="setContactDetails">
            <xsl:call-template name="mandalay:getSetting">
                <xsl:with-param name="module" select="'SciProject'"/>
                <xsl:with-param name="setting" select="'summaryTab/setContactDetails'"/>
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
                        <xsl:with-param name="id" select="'summaryTab/heading'"/>	      	    
                    </xsl:call-template>      
                </h2>
            </xsl:if>

            <div class="shortDesc">
                <xsl:variable name="shortDesc">
                    <xsl:call-template name="mandalay:string-replace">
                        <xsl:with-param name="string" select="./shortDesc"/>
                        <xsl:with-param name="from" select="'&#xA;'"/>
                        <xsl:with-param name="to" select="'&lt;br/>'"/>
                    </xsl:call-template>
                </xsl:variable>
                <xsl:value-of disable-output-escaping="yes" select="$shortDesc"/>
            </div>
    
            <xsl:if test="count(./members/member) &gt; 0">
                <h3>
                    <xsl:choose>
                        <xsl:when test="count(./members/member) &gt; $membersLimit">
                            <xsl:call-template name="mandalay:getStaticText">
                                <xsl:with-param name="module" select="'SciProject'"/>
                                <xsl:with-param name="id" select="'summaryTab/membersHeading'"/>
                            </xsl:call-template>
                        </xsl:when>
                        <xsl:otherwise>
                            <xsl:call-template name="mandalay:getStaticText">
                                <xsl:with-param name="module" select="'SciProject'"/>
                                <xsl:with-param name="id" select="'summaryTab/memberHeading'"/>
                            </xsl:call-template>	    
                        </xsl:otherwise>
                    </xsl:choose>
                </h3>
                <ul>
                    <xsl:for-each select="./members/member">
                        <xsl:sort select="./surname" data-type="text" order="ascending"/>
                        <xsl:sort select="./givenname" data-type="text" order="ascending"/>
                        <li class="sciMember">
                            <xsl:choose>
                                <xsl:when test="($linkMembers = 'true') and (./contacts/contact[@contactType='commonContact']/contactentries[keyId='homepage'])">
                                    <a class="memberName">
                                        <xsl:attribute name="href">
                                            <xsl:value-of select="normalize-space(./contacts/contact[@contactType='commonContact']/contactentries[keyId='homepage']/value)"/>
                                        </xsl:attribute>		
                                        <xsl:if test="(./@role = 'head') and ($setHeadPreText = 'true') and ((string-length(./@status) = 0) or (./@status != 'former'))">
                                            <xsl:call-template name="mandalay:getStaticText">
                                                <xsl:with-param name="module" select="'SciProject'"/>
                                                <xsl:with-param name="id" select="'summaryTab/headPreText'"/>
                                            </xsl:call-template>
                                        </xsl:if>
                                        <xsl:if test="string-length(./titlepre) &gt; 0">
                                            <xsl:value-of select="./titlepre"/>
                                            <xsl:text> </xsl:text>
                                        </xsl:if>
                                        <xsl:if test="string-length(./givenname) &gt; 0">
                                            <xsl:value-of select="./givenname"/>
                                            <xsl:text> </xsl:text>		    
                                        </xsl:if>
                                        <xsl:if test="string-length(./givenname) &gt; 0">
                                            <xsl:value-of select="./surname"/>
                                        </xsl:if>
                                        <xsl:if test="string-length(./titlepost) &gt; 0">
                                            <xsl:text>, </xsl:text>
                                            <xsl:value-of select="./titlepost"/>
                                        </xsl:if>	 
                                        <xsl:if test="(./@role = 'head') and ($setHeadPostText = 'true') and ((string-length(./@status) = 0) or (./@status != 'former'))">
                                            <xsl:text> </xsl:text>
                                            <xsl:call-template name="mandalay:getStaticText">
                                                <xsl:with-param name="module" select="'SciProject'"/>
                                                <xsl:with-param name="id" select="'summaryTab/headPostText'"/>
                                            </xsl:call-template>
                                        </xsl:if>
                                        <xsl:if test="(./@status = 'former') and ($setFormerMemberText = 'true')">
                                            <xsl:text> </xsl:text>
                                            <xsl:call-template name="mandalay:getStaticText">
                                                <xsl:with-param name="module" select="'SciProject'"/>
                                                <xsl:with-param name="id" select="'summaryTab/formerMemberText'"/>
                                            </xsl:call-template>
                                        </xsl:if>
                                    </a>
                                </xsl:when>
                                <xsl:otherwise>
                                    <span class="memberName">
                                        <xsl:if test="(./@role = 'head') and ($setHeadPreText = 'true') and ((string-length(./@status) = 0) or (./@status != 'former'))">
                                            <xsl:call-template name="mandalay:getStaticText">
                                                <xsl:with-param name="module" select="'SciProject'"/>
                                                <xsl:with-param name="id" select="'summaryTab/headPreText'"/>
                                            </xsl:call-template>
                                        </xsl:if>
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
                                        <xsl:if test="(./@role = 'head') and ($setHeadPostText = 'true') and ((string-length(./@status) = 0) or (./@status != 'former'))">
                                            <xsl:text> </xsl:text>
                                            <xsl:call-template name="mandalay:getStaticText">
                                                <xsl:with-param name="module" select="'SciProject'"/>
                                                <xsl:with-param name="id" select="'summaryTab/headPostText'"/>
                                            </xsl:call-template>
                                        </xsl:if>
                                        <xsl:if test="(./@status = 'former') and ($setFormerMemberText = 'true')">
                                            <xsl:text> </xsl:text>
                                            <xsl:call-template name="mandalay:getStaticText">
                                                <xsl:with-param name="module" select="'SciProject'"/>
                                                <xsl:with-param name="id" select="'summaryTab/formerMemberText'"/>
                                            </xsl:call-template>
                                        </xsl:if>
                                    </span>
                                </xsl:otherwise>
                            </xsl:choose>
                        </li>
                    </xsl:for-each>
                </ul>
            </xsl:if>

            <xsl:if test="count(./contacts/contact) &gt; 0">
                <xsl:for-each select="./contacts/contact">
                    <h3>
                        <xsl:value-of select="./@contactType"/>
                    </h3>
                    <xsl:call-template name="CT_Contact_details">
                        <xsl:with-param name="setFullname" select="'true'"/>
                        <xsl:with-param name="setAddress" select="'false'"/>
                        <xsl:with-param name="setAddressHeader" select="'false'"/>
                        <xsl:with-param name="setShowKeys" select="'false'"/>
                        <xsl:with-param name="setContactEntries" select="$setContactDetails"/>
                    </xsl:call-template>
                </xsl:for-each>
            </xsl:if>       

            <xsl:if test="(count(./funding) &gt; 0) or (count(./sponsors/sponsor) &gt; 0)">
                <h3>
                    <xsl:call-template name="mandalay:getStaticText">
                        <xsl:with-param name="module" select="'SciProject'"/>
                        <xsl:with-param name="id" select="'summaryTab/fundingHeading'"/>
                    </xsl:call-template>
                </h3>
                
                <xsl:if test="count(./sponsors/sponsor) &gt; 0">
                    <xsl:variable name="sponsorsText">
                        <xsl:call-template name="mandalay:getStaticText">
                            <xsl:with-param name="module" select="'SciProject'"/>
                            <xsl:with-param name="id" select="'summaryTab/sponsors'"/>
                        </xsl:call-template>
                    </xsl:variable>
                    <xsl:if test="string-length($sponsorsText) &gt; 0">
                        <h4>
                            <xsl:value-of select="$sponsorsText"/>
                        </h4>
                    </xsl:if>
                    <ul>
                        <xsl:for-each select="sponsors/sponsor">
                            <xsl:sort/>
                            <li>
                                <xsl:value-of select="."/>
                            </li>
                        </xsl:for-each>
                    </ul>
                </xsl:if>
                
                <xsl:if test="string-length(./sponsors/sponsor/@fundingCode) &gt; 1">
                    <h4>
                        <xsl:call-template name="mandalay:getStaticText">
                            <xsl:with-param name="module" select="'SciProject'"/>
                            <xsl:with-param name="id" select="'summaryTab/fundingCode'"/>
                        </xsl:call-template>
                    </h4>
                    <p>
                        <xsl:value-of select="./sponsors/sponsor/@fundingCode"/>
                    </p>
                </xsl:if>
                
                <xsl:value-of select="./funding" disable-output-escaping="yes"/>

                <xsl:if test="./fundingVolume">
                    <h4>
                        <xsl:call-template name="mandalay:getStaticText">
                            <xsl:with-param name="module" select="'SciProject'"/>
                            <xsl:with-param name="id" select="'summaryTab/fundingVolumeHeading'"/>
                        </xsl:call-template>	  
                    </h4>
                    <xsl:value-of select="./fundingVolume"/>
                </xsl:if>
            </xsl:if>


            <!-- Für ITB hinzugefügt -->
            <xsl:if test="($setLifespan = 'true') and (string-length(./lifeSpan/begin/@longDate &gt; 0))">
                <h3>
                    <xsl:call-template name="mandalay:getStaticText">
                        <xsl:with-param name="module" select="'SciProject'"/>
                        <xsl:with-param name="id" select="'lifespan'"/>
                    </xsl:call-template>
                </h3>
      
                <xsl:if test="not(./lifeSpan/end)">
                    <xsl:call-template name="mandalay:getStaticText">
                        <xsl:with-param name="module" select="'SciProject'"/>
                        <xsl:with-param name="id" select="'lifespan/from'"/>
                    </xsl:call-template>
                </xsl:if>

                <xsl:choose>
                    <xsl:when test="./lifeSpan/beginSkipMonth = 'true'">
                        <xsl:value-of select="./lifeSpan/begin/@year"/>
                    </xsl:when>
                    <xsl:when test="./lifeSpan/BeginSkipDay = 'true'">
                        <xsl:call-template name="mandalay:getStaticText">
                            <xsl:with-param name="module" select="'monthNames'"/>
                            <xsl:with-param name="id" select="./lifespan/@month"/>
                        </xsl:call-template>
                        <xsl:text> </xsl:text>
                        <xsl:value-of select="./lifeSpan/begin/@year"/>		  
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:value-of select="./lifeSpan/begin/@longDate"/>
                    </xsl:otherwise>
                </xsl:choose>
                <xsl:if test="./lifeSpan/end and string-length(./lifeSpan/end/@longDate) &gt; 0">
                    <xsl:call-template name="mandalay:getStaticText">
                        <xsl:with-param name="module" select="'SciProject'"/>
                        <xsl:with-param name="id" select="'lifespan/until'"/>
                    </xsl:call-template>
                    <xsl:choose>
                        <xsl:when test="./lifeSpan/endSkipDay = 'true'">
                            <xsl:value-of select="./lifeSpan/end/@year"/>
                        </xsl:when>
                        <xsl:when test="./lifeSpan/endSkipMonth = 'true'">
                            <xsl:call-template name="mandalay:getStaticText">
                                <xsl:with-param name="module" select="'monthNames'"/>
                                <xsl:with-param name="id" select="./projectEnd/@month"/>
                            </xsl:call-template>
                            <xsl:text> </xsl:text>
                            <xsl:value-of select="./lifeSpan/end/@year"/>
                        </xsl:when>
                        <xsl:otherwise>
                            <xsl:value-of select="./lifeSpan/end/@longDate"/>
                        </xsl:otherwise>
                    </xsl:choose>
                </xsl:if>
            </xsl:if>
            <!-- Ende -->

            <xsl:if test="(count(./subProjects/subProject) &gt; 0) and ($showSubProjects = 'true')">
                <h4>
                    <xsl:call-template name="mandalay:getStaticText">
                        <xsl:with-param name="module" select="'SciProject'"/>
                        <xsl:with-param name="id" select="'summaryTab/subProjectsHeading'"/>
                    </xsl:call-template>	  		
                </h4>
                <ul>
                    <xsl:for-each select="./subProjects/subProject">
                        <xsl:call-template name="CT_SciProject_UlList">
                            <xsl:with-param name="showMembers" select="'true'"/>
                        </xsl:call-template>
                    </xsl:for-each>
                </ul>
            </xsl:if>

            <xsl:if test="count(./involvedOrganizations) &gt; 0">
                <h3>
                    <xsl:call-template name="mandalay:getStaticText">
                        <xsl:with-param name="module" select="'SciProject'"/>
                        <xsl:with-param name="id" select="'summaryTab/involvedOrganizatinsHeading'"/>
                    </xsl:call-template>	  	
                </h3>
                <xsl:for-each select="./involvedOrganizations/organization">
                    <xsl:value-of select="./title"/>
                    <xsl:if test="position() != last()">
                        <xsl:text>, </xsl:text>
                    </xsl:if>
                </xsl:for-each>
            </xsl:if>

            <div class="endFloat"/>
        </div>

    </xsl:template>

</xsl:stylesheet>
