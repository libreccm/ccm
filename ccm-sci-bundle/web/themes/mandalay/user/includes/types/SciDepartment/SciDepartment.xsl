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
        ******************************************************
        ** Template for the detail view of a SciDepartment **
        *****************************************************
    -->

    <xsl:template name="CT_SciDepartment_graphics"
                  match="cms:item[objectType='com.arsdigita.cms.contenttypes.SciDepartment']"
                  mode="detailed_view">
        
        <xsl:variable name="setImage">
            <xsl:call-template name="mandalay:getSetting">
                <xsl:with-param name="module"  select="'SciDepartment'"/>
                <xsl:with-param name="setting" select="'setImage'"/>
                <xsl:with-param name="default" select="'true'"/>
            </xsl:call-template>
        </xsl:variable>
        <xsl:variable name="setImageCaption">
            <xsl:call-template name="mandalay:getSetting">
                <xsl:with-param name="module"  select="'SciDepartment'"/>
                <xsl:with-param name="setting" select="'setImageCaption'"/>
                <xsl:with-param name="default" select="'true'"/>
            </xsl:call-template>
        </xsl:variable>
        <xsl:variable name="setImageMaxHeight">
            <xsl:call-template name="mandalay:getSetting">
                <xsl:with-param name="module"  select="'SciDepartment'"/>
                <xsl:with-param name="setting" select="'setImageMaxHeight'"/>
                <xsl:with-param name="default" select="''"/>
            </xsl:call-template>
        </xsl:variable>
        <xsl:variable name="setImageMaxWidth">
            <xsl:call-template name="mandalay:getSetting">
                <xsl:with-param name="module"  select="'SciDepartment'"/>
                <xsl:with-param name="setting" select="'setImageMaxWidth'"/>
                <xsl:with-param name="default" select="''"/>
            </xsl:call-template>
        </xsl:variable>
        <xsl:variable name="setAddendum">
            <xsl:call-template name="mandalay:getSetting">
                <xsl:with-param name="module"  select="'SciDepartment'"/>
                <xsl:with-param name="setting" select="'setAddendum'"/>
                <xsl:with-param name="default" select="'true'"/>	
            </xsl:call-template>
        </xsl:variable>

        <xsl:variable name="subNavSeparator">
            <xsl:call-template name="mandalay:getSetting">
                <xsl:with-param name="module"  select="'SciDepartment'"/>
                <xsl:with-param name="setting" select="'subNavSeparator'"/>
                <xsl:with-param name="default" select="' | '"/>	
            </xsl:call-template>            
        </xsl:variable>
        
        <div id="mainBody" class="sciTypesOrganization sciDepartment">
            <xsl:if test="$setImage = 'true'">
                <xsl:call-template name="mandalay:imageAttachment">
                    <xsl:with-param name="showCaption" select="$setImageCaption" />
                    <xsl:with-param name="maxHeight" select="$setImageMaxHeight" />
                    <xsl:with-param name="maxWidth" select="$setImageMaxWidth" />
                </xsl:call-template>
            </xsl:if>

            <xsl:if test="$setAddendum = 'true'">
                <span class="addendum">
                    <xsl:value-of select="./addendum"/>
                </span>
            </xsl:if>

            <ul class="availableData">
                <xsl:for-each select="./orgaUnitTabs/availableTabs/availableTab">
                    <li>
                        <xsl:if test="./@selected = 'true'">
                            <xsl:attribute name="class">active</xsl:attribute>
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
                                <xsl:with-param name="module" select="'SciDepartment'"/>
                                <xsl:with-param name="id" select="concat('tabs/', ./@label)"/>
                            </xsl:call-template>
                        </a>
                    </li>
                </xsl:for-each>
            </ul>

            <xsl:apply-templates select="./orgaUnitTabs/selectedTab" mode="tabs"/>

        </div>
    </xsl:template>

    <!-- DE Listenansicht -->
    <!-- EN List view -->
    <xsl:template  name="CT_SciDepartment_List" match="nav:item[nav:attribute[@name='objectType'] = 'com.arsdigita.cms.contenttypes.SciDepartment']" mode="list_view">

        <!-- DE Hole alle benötigten Einstellungen-->
        <!-- EN Getting all needed setting-->
        <xsl:variable name="setLeadText">
            <xsl:call-template name="mandalay:getSetting">
                <xsl:with-param name="module"  select="'SciDepartment'"/>
                <xsl:with-param name="setting" select="'listView/setLeadText'"/>
                <xsl:with-param name="default" select="'true'"/>
            </xsl:call-template>
        </xsl:variable>
        <xsl:variable name="setLeadTextLength">
            <xsl:call-template name="mandalay:getSetting">
                <xsl:with-param name="module"  select="'SciDepartment'"/>
                <xsl:with-param name="setting" select="'listView/setLeadTextLength'"/>
                <xsl:with-param name="default" select="'0'"/>
            </xsl:call-template>
        </xsl:variable>
        <xsl:variable name="setMoreButton">
            <xsl:call-template name="mandalay:getSetting">
                <xsl:with-param name="module"  select="'SciDepartment'"/>
                <xsl:with-param name="setting" select="'listView/setMoreButton'"/>
                <xsl:with-param name="default" select="'false'"/>
            </xsl:call-template>
        </xsl:variable>
        <xsl:variable name="linkSpecialRoles">
            <xsl:call-template name="mandalay:getSetting">
                <xsl:with-param name="module" select="'SciDepartment'"/>
                <xsl:with-param name="setting" select="'listView/linkSpecialRoles'"/>
                <xsl:with-param name="default" select="'true'"/>
            </xsl:call-template>
        </xsl:variable>
        <xsl:variable name="showHead">
            <xsl:call-template name="mandalay:getSetting">
                <xsl:with-param name="module" select="'SciDepartment'"/>
                <xsl:with-param name="setting" select="'listView/showHead'"/>
                <xsl:with-param name="default" select="'true'"/>
            </xsl:call-template>
        </xsl:variable>
        <xsl:variable name="showViceHead">
            <xsl:call-template name="mandalay:getSetting">
                <xsl:with-param name="module" select="'SciDepartment'"/>
                <xsl:with-param name="setting" select="'listView/showViceHead'"/>
                <xsl:with-param name="default" select="'true'"/>
            </xsl:call-template>
        </xsl:variable>
        <xsl:variable name="showSecretariat">
            <xsl:call-template name="mandalay:getSetting">
                <xsl:with-param name="module" select="'SciDepartment'"/>
                <xsl:with-param name="setting" select="'listView/showSecretariat'"/>
                <xsl:with-param name="default" select="'true'"/>
            </xsl:call-template>
        </xsl:variable>

        <xsl:choose>
            <xsl:when test="nav:attribute[@name='title']">
                <!-- Not a specialising list -->
                <a class="CIname">
                    <xsl:attribute name="href">
                        <xsl:value-of select="nav:path"/>
                    </xsl:attribute>
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
                <!-- Specialising list -->
                <a class="CIname">
                    <xsl:attribute name="href">
                        <xsl:value-of select="nav:path"/>
                    </xsl:attribute>
                    <xsl:attribute name="title">
                        <xsl:call-template name="mandalay:shying">
                            <xsl:with-param name="title">
                                <xsl:value-of select="title"/>
                            </xsl:with-param>
                            <xsl:with-param name="mode">dynamic</xsl:with-param>
                        </xsl:call-template>
                    </xsl:attribute>
                    <xsl:call-template name="mandalay:shying">
                        <xsl:with-param name="title">
                            <xsl:value-of disable-output-escaping="yes" select="title"/>
                        </xsl:with-param>
                        <xsl:with-param name="mode">dynamic</xsl:with-param>
                    </xsl:call-template>
                </a>
            </xsl:otherwise>
        </xsl:choose>
        
        <xsl:if test="(($showHead = 'true') or ($showViceHead = 'true') or ($showSecretariat = 'true')) and ((count(./heads/head) &gt; 0) or (count(./viceheads/head) &gt; 0) or (count(./secretariats/secretariat) &gt; 0))">
            <dl>
                <xsl:if test="count(./heads/head) &gt; 0">
                    <dt>
                        <xsl:call-template name="mandalay:getStaticText">
                            <xsl:with-param name="module" select="'SciDepartment'"/>
                            <xsl:with-param name="id" select="'listView/headsHeading'"/>
                        </xsl:call-template>
                    </dt>
                    <dd>
                        <xsl:for-each select="./heads/head">
                            <xsl:call-template name="SciDepartmentSummaryTabSpecialRoles">
                                <xsl:with-param name="linkSpecialRoles" select="$linkSpecialRoles"/>
                                <xsl:with-param name="role" select="'head'"/>
                            </xsl:call-template>
                        </xsl:for-each>
                    </dd>
                </xsl:if>
                    
                <xsl:if test="count(./viceheads/vicehead)">
                    <dt>
                        <xsl:call-template name="mandalay:getStaticText">
                            <xsl:with-param name="module" select="'SciDepartment'"/>
                            <xsl:with-param name="id" select="'listView/viceHeadsHeading'"/>
                        </xsl:call-template>
                    </dt>
                    <dd>
                        <xsl:for-each select="./viceheads/vicehead">
                            <xsl:call-template name="SciDepartmentSummaryTabSpecialRoles">
                                <xsl:with-param name="linkSpecialRoles" select="$linkSpecialRoles"/>
                                <xsl:with-param name="role" select="'vicehead'"/>
                            </xsl:call-template>
                        </xsl:for-each>
                    </dd>
                </xsl:if>
                    
                <xsl:if test="count(./secretariats/secretariat)">
                    <dt>
                        <xsl:call-template name="mandalay:getStaticText">
                            <xsl:with-param name="module" select="'SciDepartment'"/>
                            <xsl:with-param name="id" select="'listView/secretariatHeading'"/>
                        </xsl:call-template>
                    </dt>
                    <dd>
                        <xsl:for-each select="./secretariats/secretariat">
                            <xsl:call-template name="SciDepartmentSummaryTabSpecialRoles">
                                <xsl:with-param name="linkSpecialRoles" select="$linkSpecialRoles"/>
                                <xsl:with-param name="role" select="'secretariat'"/>
                            </xsl:call-template>
                        </xsl:for-each>
                    </dd>
                    <dt>
                        <xsl:call-template name="mandalay:getStaticText">
                            <xsl:with-param name="module" select="'SciDepartment'"/>
                            <xsl:with-param name="id" select="'listView/emailHeading'"/>
                        </xsl:call-template>
                    </dt>
                    <dd>
                        <xsl:for-each select="./secretariats/secretariat">
                            <xsl:call-template name="SciDepartmentSummaryTabSpecialContact">
                                <xsl:with-param name="linkSpecialRoles" select="$linkSpecialRoles"/>
                                <xsl:with-param name="role" select="'email'"/>
                            </xsl:call-template>
                        </xsl:for-each>
                    </dd>
                    <dt>
                        <xsl:call-template name="mandalay:getStaticText">
                            <xsl:with-param name="module" select="'SciDepartment'"/>
                            <xsl:with-param name="id" select="'listView/phoneHeading'"/>
                        </xsl:call-template>
                    </dt>
                    <dd>
                        <xsl:for-each select="./secretariats/secretariat">
                            <xsl:call-template name="SciDepartmentSummaryTabSpecialContact">
                                <xsl:with-param name="linkSpecialRoles" select="$linkSpecialRoles"/>
                                <xsl:with-param name="role" select="'phone_office'"/>
                            </xsl:call-template>
                        </xsl:for-each>
                    </dd>
                </xsl:if>
                    
            </dl>
        </xsl:if>
        
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
                                        <xsl:attribute name="href">
                                            <xsl:value-of select="nav:path"/>
                                        </xsl:attribute>
                                        <xsl:attribute name="title">
                                            <xsl:call-template name="mandalay:getStaticText">
                                                <xsl:with-param name="module" select="'SciDepartment'"/>
                                                <xsl:with-param name="id" select="'moreButtonTitle'"/>
                                            </xsl:call-template>
                                        </xsl:attribute>
                                        <xsl:call-template name="mandalay:getStaticText">
                                            <xsl:with-param name="module" select="'SciDepartment'"/>
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
        
        <xsl:if test="./shortDescription and $setLeadText = 'true'">
            <br />
            <span class="intro">
                <xsl:choose>
                    <xsl:when test="$setLeadTextLength = '0'">
                        <xsl:value-of disable-output-escaping="yes" select="./shortDescription" />
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:value-of disable-output-escaping="yes" select="substring(./shortDescription, 1, $setLeadTextLength)" />
                        <xsl:if test="string-length(nav:attribute[@name='lead']) > $setLeadTextLength">
                            <xsl:text>...</xsl:text>
                            <xsl:if test="$setMoreButton = 'true'">
                                <span class="moreButton">
                                    <a>
                                        <xsl:attribute name="href">
                                            <xsl:value-of select="nav:path"/>
                                        </xsl:attribute>
                                        <xsl:attribute name="title">
                                            <xsl:call-template name="mandalay:getStaticText">
                                                <xsl:with-param name="module" select="'SciDepartment'"/>
                                                <xsl:with-param name="id" select="'moreButtonTitle'"/>
                                            </xsl:call-template>
                                        </xsl:attribute>
                                        <xsl:call-template name="mandalay:getStaticText">
                                            <xsl:with-param name="module" select="'SciDepartment'"/>
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

    <xsl:template name="CT_SciDepartment_Link" match="*/cms:item/links[targetItem/objectType = 'com.arsdigita.cms.contenttypes.SciDepartment']" mode="link_view">

        <!-- DE Hole alle benötigten Einstellungen-->
        <!-- EN Getting all needed setting-->
        <xsl:variable name="setImageAndText">
            <xsl:call-template name="mandalay:getSetting">
                <xsl:with-param name="module"  select="'SciDepartment'"/>
                <xsl:with-param name="setting" select="'linkView/setImageAndText'"/>
                <xsl:with-param name="default" select="'true'"/>
            </xsl:call-template>
        </xsl:variable>
        <xsl:variable name="setImage">
            <xsl:call-template name="mandalay:getSetting">
                <xsl:with-param name="module"  select="'SciDepartment'"/>
                <xsl:with-param name="setting" select="'linkView/setImage'"/>
                <xsl:with-param name="default" select="'true'"/>
            </xsl:call-template>
        </xsl:variable>
        <xsl:variable name="setImageMaxHeight">
            <xsl:call-template name="mandalay:getSetting">
                <xsl:with-param name="module"  select="'SciDepartment'"/>
                <xsl:with-param name="setting" select="'linkView/setImageMaxHeight'"/>
                <xsl:with-param name="default" select="''"/>
            </xsl:call-template>
        </xsl:variable>
        <xsl:variable name="setImageMaxWidth">
            <xsl:call-template name="mandalay:getSetting">
                <xsl:with-param name="module"  select="'SciDepartment'"/>
                <xsl:with-param name="setting" select="'linkView/setImageMaxWidth'"/>
                <xsl:with-param name="default" select="''"/>
            </xsl:call-template>
        </xsl:variable>
        <xsl:variable name="setImageCaption">
            <xsl:call-template name="mandalay:getSetting">
                <xsl:with-param name="module"  select="'SciDepartment'"/>
                <xsl:with-param name="setting" select="'linkView/setImageCaption'"/>
                <xsl:with-param name="default" select="'true'"/>
            </xsl:call-template>
        </xsl:variable>
        <xsl:variable name="setDescription">
            <xsl:call-template name="mandalay:getSetting">
                <xsl:with-param name="module"  select="'SciDepartment'"/>
                <xsl:with-param name="setting" select="'linkView/setDescription'"/>
                <xsl:with-param name="default" select="'true'"/>
            </xsl:call-template>
        </xsl:variable>
        <xsl:variable name="setDescriptionLength">
            <xsl:call-template name="mandalay:getSetting">
                <xsl:with-param name="module"  select="'SciDepartment'"/>
                <xsl:with-param name="setting" select="'linkView/setDescriptionLength'"/>
                <xsl:with-param name="default" select="'0'"/>
            </xsl:call-template>
        </xsl:variable>
        <xsl:variable name="setMoreButton">
            <xsl:call-template name="mandalay:getSetting">
                <xsl:with-param name="module"  select="'SciDepartment'"/>
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
                                                <xsl:with-param name="module" select="'SciDepartment'"/>
                                                <xsl:with-param name="id" select="'moreButtonTitle'"/>
                                            </xsl:call-template>
                                        </xsl:attribute>
                                        <xsl:call-template name="mandalay:getStaticText">
                                            <xsl:with-param name="module" select="'SciDepartment'"/>
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
    
    <xsl:template name="SciDepartmentImage" 
                  match="cms:item[objectType='com.arsdigita.cms.contenttypes.SciDepartment']"
                  mode="image">
        <!-- Nothing -->
    </xsl:template>
    
    <xsl:template name="SciDepartmentLead"
                  match="cms:item[objectType='com.arsdigita.cms.contenttypes.SciDepartment']"
                  mode="lead">
        <!-- Nothing -->
    </xsl:template>
    
    <xsl:template name="SciDepartmentListViewSpecialRoles">
        
        <xsl:param name="linkSpecialRoles" select="'true'" />
        <xsl:param name="role" select="''"/>
        
        <xsl:variable name="contactData">
            <xsl:call-template name="mandalay:getSetting">
                <xsl:with-param name="module" select="'SciDepartment'"/>
                <xsl:with-param name="setting" select="'listView/specialRolesContactData'"/>
            </xsl:call-template>
        </xsl:variable>
        
        <li class="sciMember">
            <xsl:choose>
                <xsl:when test="($linkSpecialRoles = 'true') and (./contacts/contactentries[keyId='homepage'])">
                    <a class="memberName">
                        <xsl:attribute name="href">
                            <xsl:value-of select="./contacts/contactentries[keyId='homepage']/value"/>
                        </xsl:attribute>
                        <xsl:if test="string-length(./titlePre) &gt; 0">
                            <xsl:value-of select="./titlePre"/>
                            <xsl:text> </xsl:text>
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
                        <xsl:if test="string-length(./titlePost) &gt; 0">
                            <xsl:text>, </xsl:text>
                            <xsl:value-of select="./titlePost"/>
                        </xsl:if>
                        <xsl:if test="string-length(./titlepost) &gt; 0">
                            <xsl:text>, </xsl:text>
                            <xsl:value-of select="./titlepost"/>
                        </xsl:if>	 
                    </a>
                </xsl:when>
                <xsl:otherwise>
                    <span class="sciMember">
                        <xsl:if test="string-length(./titlePre) &gt; 0">
                            <xsl:value-of select="./titlePre"/>
                            <xsl:text> </xsl:text>
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
                        <xsl:if test="string-length(./titlePost) &gt; 0">
                            <xsl:text>, </xsl:text>
                            <xsl:value-of select="./titlePost"/>
                        </xsl:if>	 
                        <xsl:if test="string-length(./titlepost) &gt; 0">
                            <xsl:text>, </xsl:text>
                            <xsl:value-of select="./titlepost"/>
                        </xsl:if>
                    </span>
                </xsl:otherwise>
            </xsl:choose>
            
            <dl>
                <xsl:for-each select="./contacts/contact[1]/contactentries">
                    <xsl:variable name="showData">
                        <xsl:call-template name="mandalay:getSetting">
                            <xsl:with-param name="module" select="'SciDepartment'"/>
                            <xsl:with-param name="setting" select="concat('listView/specialRolesContactData/', $role, '/show/', ./keyId)"/>
                            <xsl:with-param name="default" select="'false'"/>
                        </xsl:call-template>
                    </xsl:variable>
                    <xsl:if test="$showData = 'true'">
                        <dt>
                            <xsl:value-of select="./key"/>
                        </dt>
                        <dd>
                            <xsl:value-of select="./value"/>
                        </dd>
                    </xsl:if>
                </xsl:for-each>
            </dl>
            
        </li>
    </xsl:template>
</xsl:stylesheet>
