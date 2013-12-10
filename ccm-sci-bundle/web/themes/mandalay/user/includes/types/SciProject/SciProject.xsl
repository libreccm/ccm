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
    exclude-result-prefixes="xsl bebop cms nav"
    version="1.0"
>

    <!-- 
         ***************************************************
         ** Template for the detail view of a SciProject  **
         ***************************************************
    -->

    <xsl:template name="CT_SciProject_graphics"
                  match="cms:item[objectType='com.arsdigita.cms.contenttypes.SciProject']"
                  mode="detailed_view">

        <xsl:variable name="setImage">
            <xsl:call-template name="mandalay:getSetting">
                <xsl:with-param name="module"  select="'SciProject'"/>
                <xsl:with-param name="setting" select="'setImage'"/>
                <xsl:with-param name="default" select="'true'"/>
            </xsl:call-template>
        </xsl:variable>
        <xsl:variable name="setImageCaption">
            <xsl:call-template name="mandalay:getSetting">
                <xsl:with-param name="module"  select="'SciProject'"/>
                <xsl:with-param name="setting" select="'setImageCaption'"/>
                <xsl:with-param name="default" select="'true'"/>
            </xsl:call-template>
        </xsl:variable>
        <xsl:variable name="setImageMaxHeight">
            <xsl:call-template name="mandalay:getSetting">
                <xsl:with-param name="module"  select="'SciProject'"/>
                <xsl:with-param name="setting" select="'setImageMaxHeight'"/>
                <xsl:with-param name="default" select="''"/>
            </xsl:call-template>
        </xsl:variable>
        <xsl:variable name="setImageMaxWidth">
            <xsl:call-template name="mandalay:getSetting">
                <xsl:with-param name="module"  select="'SciProject'"/>
                <xsl:with-param name="setting" select="'setImageMaxWidth'"/>
                <xsl:with-param name="default" select="''"/>
            </xsl:call-template>
        </xsl:variable>
        <xsl:variable name="setAddendum">
            <xsl:call-template name="mandalay:getSetting">
                <xsl:with-param name="module"  select="'SciProject'"/>
                <xsl:with-param name="setting" select="'setAddendum'"/>
                <xsl:with-param name="default" select="'true'"/>
            </xsl:call-template>
        </xsl:variable>
        <xsl:variable name="setShowHeading">
            <xsl:call-template name="mandalay:getSetting">
                <xsl:with-param name="module"  select="'SciProject'"/>
                <xsl:with-param name="setting" select="'setShowHeading'"/>
                <xsl:with-param name="default" select="'false'"/>
            </xsl:call-template>
        </xsl:variable>

        <xsl:variable name="subNavSeparator">
            <xsl:call-template name="mandalay:getSetting">
                <xsl:with-param name="module"  select="'SciProject'"/>
                <xsl:with-param name="setting" select="'subNavSeparator'"/>
                <xsl:with-param name="default" select="' | '"/>
            </xsl:call-template>
        </xsl:variable>

        <xsl:variable name="setLifespan">
            <xsl:call-template name="mandalay:getSetting">
                <xsl:with-param name="module"  select="'SciProject'"/>
                <xsl:with-param name="setting" select="'setLifespan'"/>
                <xsl:with-param name="default" select="'true'"/>	
            </xsl:call-template>
        </xsl:variable>
    
        <xsl:if test="$setAddendum = 'true'">
            <span class="addendum">
                <xsl:value-of select="./addendum"/>
            </span>
        </xsl:if>

        <xsl:if test="$setImage = 'true'">
            <xsl:call-template name="mandalay:imageAttachment">
                <xsl:with-param name="showCaption" select="$setImageCaption" />
                <xsl:with-param name="maxHeight" select="$setImageMaxHeight" />
                <xsl:with-param name="maxWidth" select="$setImageMaxWidth" />
            </xsl:call-template>
        </xsl:if>

        <div id="mainBody" class="sciTypesOrganization sciProject">
            <xsl:if test="($setLifespan = 'true') and (string-length(./projectBegin/@longDate &gt; 0))">
                <div class="lifespan">
                    <span>
                        <xsl:call-template name="mandalay:getStaticText">
                            <xsl:with-param name="module" select="'SciProject'"/>
                            <xsl:with-param name="id" select="'lifespan'"/>
                        </xsl:call-template>
                    </span>

                    <xsl:if test="not(./projectEnd)">
                        <xsl:call-template name="mandalay:getStaticText">
                            <xsl:with-param name="module" select="'SciProject'"/>
                            <xsl:with-param name="id" select="'lifespan/from'"/>
                        </xsl:call-template>
                    </xsl:if>

                    <xsl:choose>
                        <xsl:when test="./projectBeginSkipMonth = 'true'">
                            <xsl:value-of select="./projectBegin/@year"/>
                        </xsl:when>
                        <xsl:when test="./projectBeginSkipDay = 'true'">
                            <xsl:call-template name="mandalay:getStaticText">
                                <xsl:with-param name="module" select="'monthNames'"/>
                                <xsl:with-param name="id" select="./projectBegin/@month"/>
                            </xsl:call-template>
                            <xsl:text> </xsl:text>
                            <xsl:value-of select="./projectBegin/@year"/>		  
                        </xsl:when>
                        <xsl:otherwise>
                            <xsl:value-of select="./projectBegin/@longDate"/>
                        </xsl:otherwise>
                    </xsl:choose>
                    <xsl:if test="./projectEnd and string-length(./projectEnd/@longDate) &gt; 0">
                        <xsl:call-template name="mandalay:getStaticText">
                            <xsl:with-param name="module" select="'SciProject'"/>
                            <xsl:with-param name="id" select="'lifespan/until'"/>
                        </xsl:call-template>
                        <xsl:choose>
                            <xsl:when test="./projectEndSkipDay = 'true'">
                                <xsl:value-of select="./projectEnd/@year"/>
                            </xsl:when>
                            <xsl:when test="./projectEndSkipMonth = 'true'">
                                <xsl:call-template name="mandalay:getStaticText">
                                    <xsl:with-param name="module" select="'monthNames'"/>
                                    <xsl:with-param name="id" select="./projectEnd/@month"/>
                                </xsl:call-template>
                                <xsl:text> </xsl:text>
                                <xsl:value-of select="./projectEnd/@year"/>
                            </xsl:when>
                            <xsl:otherwise>
                                <xsl:value-of select="./projectEnd/@longDate"/>
                            </xsl:otherwise>
                        </xsl:choose>
                    </xsl:if>
                </div>
            </xsl:if>

            <ul class="availableData">
                <xsl:for-each select="./orgaUnitTabs/availableTabs/availableTab">
                    <li>
                        <xsl:if test="./@selected = 'true'">
                            <xsl:attribute name="class">selectedTab active</xsl:attribute>
                        </xsl:if>
                        <a>
                            <xsl:attribute name="href">
                                <xsl:call-template name="mandalay:linkParser">
                                    <xsl:with-param name="link" select="concat('?selectedTab=', ./@label)"/>
                                </xsl:call-template>
                            </xsl:attribute>
                            <xsl:if test="./@selected = 'true'">
                                <xsl:attribute name="class">selectedTab active</xsl:attribute>
                            </xsl:if>
                            <xsl:call-template name="mandalay:getStaticText">
                                <xsl:with-param name="module" select="'SciProject'"/>
                                <xsl:with-param name="id" select="concat('tabs/', ./@label)"/>
                            </xsl:call-template>
                        </a>
                    </li>
                </xsl:for-each>
            </ul>

            <div class="activeTab">
                <xsl:apply-templates select="./orgaUnitTabs/selectedTab" mode="tabs"/>
            </div>

        </div>

    </xsl:template>

    <xsl:template name="CT_SciProject_List"
                  match="nav:item[nav:attribute[@name='objectType'] = 'com.arsdigita.cms.contenttypes.SciProject']"
                  mode="list_view">
        <xsl:variable name="linkProject">
            <xsl:call-template name="mandalay:getSetting">
                <xsl:with-param name="module" select="'SciProject'"/>
                <xsl:with-param name="setting" select="'listView/linkProject'"/>
                <xsl:with-param name="default" select="'true'"/>	
            </xsl:call-template>
        </xsl:variable>
        <xsl:variable name="showLifespan">
            <xsl:call-template name="mandalay:getSetting">
                <xsl:with-param name="module" select="'SciProject'"/>
                <xsl:with-param name="setting" select="'listView/showLifespan'"/>
                <xsl:with-param name="default" select="'true'"/>
            </xsl:call-template>
        </xsl:variable>
        <xsl:variable name="showMembers">
            <xsl:call-template name="mandalay:getSetting">
                <xsl:with-param name="module" select="'SciProject'"/>
                <xsl:with-param name="setting" select="'listView/showMembers'"/>
                <xsl:with-param name="default" select="'true'"/>	
            </xsl:call-template>
        </xsl:variable>
        <xsl:variable name="emphHead">
            <xsl:call-template name="mandalay:getSetting">
                <xsl:with-param name="module" select="'SciProject'"/>
                <xsl:with-param name="setting" select="'listView/emphHead'"/>
                <xsl:with-param name="default" select="'true'"/>	
            </xsl:call-template>
        </xsl:variable>
        <xsl:variable name="projectHeadText">
            <xsl:call-template name="mandalay:getStaticText">
                <xsl:with-param name="module" select="'SciProject'"/>
                <xsl:with-param name="id" select="'listView/projectHeadText'"/>	
            </xsl:call-template>
        </xsl:variable>
        <xsl:variable name="projectFormerMemberText">
            <xsl:call-template name="mandalay:getStaticText">
                <xsl:with-param name="module" select="'SciProject'"/>
                <xsl:with-param name="id" select="'listView/projectFormerMemberText'"/>	
            </xsl:call-template>
        </xsl:variable>
        <xsl:variable name="projectMemberSeparator">
            <xsl:call-template name="mandalay:getSetting">
                <xsl:with-param name="module" select="'SciProject'"/>
                <xsl:with-param name="setting" select="'listView/projectMemberSeparator'"/>
                <xsl:with-param name="default" select="', '"/>	
            </xsl:call-template>
        </xsl:variable>
        <xsl:variable name="showShortDesc">
            <xsl:call-template name="mandalay:getSetting">
                <xsl:with-param name="module" select="'SciProject'"/>
                <xsl:with-param name="setting" select="'listView/showShortDesc'"/>
                <xsl:with-param name="default" select="'true'"/>	
            </xsl:call-template>
        </xsl:variable>

        <xsl:call-template name="CT_SciProject_ListItem">
            <xsl:with-param name="linkProject" select="$linkProject"/>
            <xsl:with-param name="showLifespan" select="$showLifespan"/>
            <xsl:with-param name="showMembers" select="$showMembers"/>
            <xsl:with-param name="emphHead" select="$emphHead"/>
            <xsl:with-param name="projectHeadText" select="$projectHeadText"/>
            <xsl:with-param name="projectFormerMemberText" select="$projectFormerMemberText"/>
            <xsl:with-param name="projectMemberSeparator" select="$projectMemberSeparator"/>
            <xsl:with-param name="showShortDesc" select="$showShortDesc"/>
        </xsl:call-template>

    </xsl:template>

    <xsl:template name="CT_SciProject_UlList"
                  mode="list_view">
        <xsl:param name="linkProject" select="'true'"/>
        <xsl:param name="showLifespan" select="'true'"/>
        <xsl:param name="showMembers" select="'true'"/>
        <xsl:param name="emphHead" select="'true'"/>
        <xsl:param name="projectHeadText" select="''"/>
        <xsl:param name="projectFormerMemberText" select="''"/>
        <xsl:param name="projectMemberSeparator" select="', '"/>
        <xsl:param name="showShortDesc" select="'false'"/>
        <xsl:param name="useRelativeUrl" select="'false'"/>
    
        <li>
            <xsl:call-template name="CT_SciProject_ListItem">
                <xsl:with-param name="linkProject" select="$linkProject"/>
                <xsl:with-param name="showLifespan" select="$showLifespan"/>
                <xsl:with-param name="showMembers" select="$showMembers"/>
                <xsl:with-param name="emphHead" select="$emphHead"/>
                <xsl:with-param name="projectHeadText" select="$projectHeadText"/>
                <xsl:with-param name="projectFormerMemberText" select="$projectFormerMemberText"/>
                <xsl:with-param name="projectMemberSeparator" select="$projectMemberSeparator"/>
                <xsl:with-param name="showShortDesc" select="$showShortDesc"/>
                <xsl:with-param name="useRelativeUrl" select="$useRelativeUrl"/>
            </xsl:call-template>
        </li>
    </xsl:template>

    <xsl:template name="CT_SciProject_ListItem"
                  mode="list_view">
        <xsl:param name="linkProject" select="'true'"/>
        <xsl:param name="showLifespan" select="'true'"/>
        <xsl:param name="showMembers" select="'true'"/>
        <xsl:param name="emphHead" select="'true'"/>
        <xsl:param name="projectHeadText" select="''"/>
        <xsl:param name="projectFormerMemberText" select="''"/>
        <xsl:param name="projectMemberSeparator" select="', '"/>
        <xsl:param name="showShortDesc" select="'false'"/>
        <xsl:param name="useRelativeUrl" select="'false'"/>

        <xsl:choose>
            <xsl:when test="$linkProject = 'true'">
                <a class="CIname">
                    <xsl:choose>
                        <xsl:when test="$useRelativeUrl = 'true'">
                            <xsl:attribute name="href">
                                <xsl:value-of select="@oid"/>
                            </xsl:attribute>
                        </xsl:when>
                        <xsl:otherwise>
                            <xsl:attribute name="href">/redirect/?oid=<xsl:value-of select="@oid"/></xsl:attribute>
                        </xsl:otherwise>
                    </xsl:choose>	 
                    <xsl:value-of select="./title"/>
                </a>
            </xsl:when>
            <xsl:otherwise>
                <em>
                    <xsl:value-of select="./title"/>
                </em>	
            </xsl:otherwise>
        </xsl:choose>
        <xsl:if test="($showLifespan = 'true') and (string-length(./projectBegin/@longDate) &gt; 0)">
            <div class="sciProjectLifespan">
                <span>
                    <xsl:call-template name="mandalay:getStaticText">
                        <xsl:with-param name="module" select="'SciProject'"/>
                        <xsl:with-param name="id" select="'lifespan'"/>
                    </xsl:call-template>
                </span>
                <xsl:if test="not(./projectEnd)">
                    <xsl:call-template name="mandalay:getStaticText">
                        <xsl:with-param name="module" select="'SciProject'"/>
                        <xsl:with-param name="id" select="'lifespan/from'"/>
                    </xsl:call-template>
                </xsl:if>
      	  
                <xsl:choose>
                    <xsl:when test="./projectBeginSkipMonth = 'true'">
                        <xsl:value-of select="./projectBegin/@year"/>
                    </xsl:when>
                    <xsl:when test="./projectBeginSkipDay = 'true'">
                        <xsl:call-template name="mandalay:getStaticText">
                            <xsl:with-param name="module" select="'monthNames'"/>
                            <xsl:with-param name="id" select="./projectBegin/@month"/>
                        </xsl:call-template>
                        <xsl:text> </xsl:text>
                        <xsl:value-of select="./projectBegin/@year"/>             
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:value-of select="./projectBegin/@longDate"/>
                    </xsl:otherwise>
                </xsl:choose>
                <xsl:if test="./projectEnd and string-length(./projectEnd/@longDate) &gt; 0">
                    <xsl:call-template name="mandalay:getStaticText">
                        <xsl:with-param name="module" select="'SciProject'"/>
                        <xsl:with-param name="id" select="'lifespan/until'"/>
                    </xsl:call-template>
                    <xsl:choose>
                        <xsl:when test="./projectEndSkipDay = 'true'">
                            <xsl:value-of select="./projectEnd/@year"/>
                        </xsl:when>
                        <xsl:when test="./projectEndSkipMonth = 'true'">
                            <xsl:call-template name="mandalay:getStaticText">
                                <xsl:with-param name="module" select="'monthNames'"/>
                                <xsl:with-param name="id" select="./projectEnd/@month"/>
                            </xsl:call-template>
                            <xsl:text> </xsl:text>
                            <xsl:value-of select="./projectEnd/@year"/>
                        </xsl:when>
                        <xsl:otherwise>
                            <xsl:value-of select="./projectEnd/@longDate"/>
                        </xsl:otherwise>
                    </xsl:choose>
                </xsl:if>
            </div>
        </xsl:if>
        <xsl:if test="$showMembers = 'true'">
            <div class="members">
                <xsl:for-each select="./members/member">
                    <xsl:if test="string-length(./givenName) &gt; 0">
                        <xsl:value-of select="./givenName"/>
                        <xsl:text> </xsl:text>		    
                    </xsl:if>
                    <xsl:if test="string-length(./surname) &gt; 0">
                        <xsl:value-of select="./surname"/>
                        <xsl:if test="($emphHead = 'true') and (./@role = 'head') and ((./@status != 'former') or (string-length(./@status) = 0))">
                            <xsl:text> </xsl:text>
                            <xsl:value-of select="$projectHeadText"/>
                        </xsl:if>
                        <xsl:if test="./@status = 'former' and (string-length($projectFormerMemberText) &gt; 1)">
                            <xsl:text> </xsl:text>
                            <xsl:value-of select="$projectFormerMemberText"/>
                        </xsl:if>
                        <xsl:if test="(position() &lt; count(../persons)) or (position() &lt; count(../member))">
                            <xsl:value-of select="$projectMemberSeparator"/>			
                        </xsl:if>		    
                    </xsl:if>
                </xsl:for-each>
            </div>
        </xsl:if>
        <xsl:if test="($showShortDesc = 'true')">
            <div class="shortDesc">
                <xsl:value-of select="./projectShortDesc"/>
            </div>
        </xsl:if> 
    </xsl:template>

    <xsl:template name="CT_SciProject_Link" 
                  match="*/cms:item/links[targetItem/objectType = 'com.arsdigita.cms.contenttypes.SciProject']" 
                  mode="link_view">
        <!-- DE Hole alle benötigten Einstellungen-->
        <!-- EN Getting all needed setting-->
        <xsl:variable name="setImageAndText">
            <xsl:call-template name="mandalay:getSetting">
                <xsl:with-param name="module"  select="'SciMember'"/>
                <xsl:with-param name="setting" select="'linkView/setImageAndText'"/>
                <xsl:with-param name="default" select="'true'"/>
            </xsl:call-template>
        </xsl:variable>
        <xsl:variable name="setImage">
            <xsl:call-template name="mandalay:getSetting">
                <xsl:with-param name="module"  select="'SciMember'"/>
                <xsl:with-param name="setting" select="'linkView/setImage'"/>
                <xsl:with-param name="default" select="'true'"/>
            </xsl:call-template>
        </xsl:variable>
        <xsl:variable name="setImageMaxHeight">
            <xsl:call-template name="mandalay:getSetting">
                <xsl:with-param name="module"  select="'SciMember'"/>
                <xsl:with-param name="setting" select="'linkView/setImageMaxHeight'"/>
                <xsl:with-param name="default" select="''"/>
            </xsl:call-template>
        </xsl:variable>
        <xsl:variable name="setImageMaxWidth">
            <xsl:call-template name="mandalay:getSetting">
                <xsl:with-param name="module"  select="'SciMember'"/>
                <xsl:with-param name="setting" select="'linkView/setImageMaxWidth'"/>
                <xsl:with-param name="default" select="''"/>
            </xsl:call-template>
        </xsl:variable>
        <xsl:variable name="setImageCaption">
            <xsl:call-template name="mandalay:getSetting">
                <xsl:with-param name="module"  select="'SciMember'"/>
                <xsl:with-param name="setting" select="'linkView/setImageCaption'"/>
                <xsl:with-param name="default" select="'true'"/>
            </xsl:call-template>
        </xsl:variable>
        <xsl:variable name="setDescription">
            <xsl:call-template name="mandalay:getSetting">
                <xsl:with-param name="module"  select="'SciMember'"/>
                <xsl:with-param name="setting" select="'linkView/setDescription'"/>
                <xsl:with-param name="default" select="'true'"/>
            </xsl:call-template>
        </xsl:variable>
        <xsl:variable name="setDescriptionLength">
            <xsl:call-template name="mandalay:getSetting">
                <xsl:with-param name="module"  select="'SciMember'"/>
                <xsl:with-param name="setting" select="'linkView/setDescriptionLength'"/>
                <xsl:with-param name="default" select="'0'"/>
            </xsl:call-template>
        </xsl:variable>
        <xsl:variable name="setMoreButton">
            <xsl:call-template name="mandalay:getSetting">
                <xsl:with-param name="module"  select="'SciMember'"/>
                <xsl:with-param name="setting" select="'linkView/setMoreButton'"/>
                <xsl:with-param name="default" select="'false'"/>
            </xsl:call-template>
        </xsl:variable>

        <!-- DE Wenn es Bilder gibt, dann soll das erste hier als Link angezeigt werden -->
        <!-- EN -->
        <xsl:if test="./targetItem/imageAttachments and $setImage = 'true'">
            <a>
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
                        <xsl:with-param name="showCaption" select="$setImageCaption" />
                        <xsl:with-param name="maxHeight" select="$setImageMaxHeight" />
                        <xsl:with-param name="maxWidth" select="$setImageMaxWidth" />
                    </xsl:call-template>
                </xsl:for-each>
            </a>
        </xsl:if>
        <xsl:if test="$setImageAndText = 'true' or not(./targetItem/imageAttachments) or $setImage = 'false'">
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
                                        <xsl:attribute name="href">
                                            <xsl:text>/redirect/?oid=</xsl:text>
                                            <xsl:value-of select="./targetItem/@oid"/>
                                        </xsl:attribute>
                                        <xsl:attribute name="title">
                                            <xsl:call-template name="mandalay:getStaticText">
                                                <xsl:with-param name="module" select="'SciMember'"/>
                                                <xsl:with-param name="id" select="'moreButtonTitle'"/>
                                            </xsl:call-template>
                                        </xsl:attribute>
                                        <xsl:call-template name="mandalay:getStaticText">
                                            <xsl:with-param name="module" select="'SciMember'"/>
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
    
    <xsl:template name="CT_SciProject_image"
                      match="cms:item[objectType='com.arsdigita.cms.contenttypes.SciProject']"
                      mode="image">
    </xsl:template>
    
    <xsl:template name="CT_SciProject_lead"
                      match="cms:item[objectType='com.arsdigita.cms.contenttypes.SciProject']"
                      mode="lead">
    </xsl:template>

</xsl:stylesheet>
