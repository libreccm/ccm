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
         ******************************************************
         ** Template for the SummaryTab for a SciDepartment  **
         ******************************************************
    -->

    <xsl:template name="CT_SciDepartment_Graphics_Summary"
                  match="departmentSummary"
                  mode="tabs">

        <xsl:variable name="setImage">
            <xsl:call-template name="mandalay:getSetting">
                <xsl:with-param name="module"  select="'SciDepartment'"/>
                <xsl:with-param name="setting" select="'summaryTab/setImage'"/>
                <xsl:with-param name="default" select="'true'"/>
            </xsl:call-template>
        </xsl:variable>
        <xsl:variable name="setImageCaption">
            <xsl:call-template name="mandalay:getSetting">
                <xsl:with-param name="module"  select="'SciDepartment'"/>
                <xsl:with-param name="setting" select="'tabs/setImageCaption'"/>
                <xsl:with-param name="default" select="'true'"/>
            </xsl:call-template>
        </xsl:variable>
        <xsl:variable name="setImageMaxHeight">
            <xsl:call-template name="mandalay:getSetting">
                <xsl:with-param name="module"  select="'SciDepartment'"/>
                <xsl:with-param name="setting" select="'tabs/setImageMaxHeight'"/>
                <xsl:with-param name="default" select="''"/>
            </xsl:call-template>
        </xsl:variable>
        <xsl:variable name="setImageMaxWidth">
            <xsl:call-template name="mandalay:getSetting">
                <xsl:with-param name="module"  select="'SciDepartment'"/>
                <xsl:with-param name="setting" select="'tabs/setImageMaxWidth'"/>
                <xsl:with-param name="default" select="''"/>
            </xsl:call-template>
        </xsl:variable>
        <xsl:variable name="setShowHeading">
            <xsl:call-template name="mandalay:getSetting">
                <xsl:with-param name="module" select="'SciDepartment'"/>
                <xsl:with-param name="setting" select="'tabs/setShowHeading'"/>
                <xsl:with-param name="default" select="'false'"/>
            </xsl:call-template>      
        </xsl:variable>
        <xsl:variable name="linkSpecialRoles">
            <xsl:call-template name="mandalay:getSetting">
                <xsl:with-param name="module" select="'SciDepartment'"/>
                <xsl:with-param name="setting" select="'summaryTab/linkSpecialRoles'"/>
                <xsl:with-param name="default" select="'true'"/>
            </xsl:call-template>
        </xsl:variable>
        <xsl:variable name="showHead">
            <xsl:call-template name="mandalay:getSetting">
                <xsl:with-param name="module" select="'SciDepartment'"/>
                <xsl:with-param name="setting" select="'summaryTab/showHead'"/>
                <xsl:with-param name="default" select="'true'"/>
            </xsl:call-template>
        </xsl:variable>
        <xsl:variable name="showViceHead">
            <xsl:call-template name="mandalay:getSetting">
                <xsl:with-param name="module" select="'SciDepartment'"/>
                <xsl:with-param name="setting" select="'summaryTab/showViceHead'"/>
                <xsl:with-param name="default" select="'true'"/>
            </xsl:call-template>
        </xsl:variable>
        <xsl:variable name="showSecretariat">
            <xsl:call-template name="mandalay:getSetting">
                <xsl:with-param name="module" select="'SciDepartment'"/>
                <xsl:with-param name="setting" select="'summaryTab/showSecretariat'"/>
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
                        <xsl:with-param name="module" select="'SciDepartment'"/>
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
    
            <xsl:if test="(($showHead = 'true') or ($showViceHead = 'true') or ($showSecretariat = 'true')) and ((count(./heads/head) &gt; 0) or (count(./viceheads/head) &gt; 0) or (count(./secretariats/secretariat) &gt; 0))">
                <dl>
                    <xsl:if test="count(./heads/head) &gt; 0">
                        <dt>
                            <xsl:call-template name="mandalay:getStaticText">
                                <xsl:with-param name="module" select="'SciDepartment'"/>
                                <xsl:with-param name="id" select="'summaryTab/headsHeading'"/>
                            </xsl:call-template>
                        </dt>
                        <dd>
                            <ul>
                                <xsl:for-each select="./heads/head">
                                    <xsl:call-template name="SciDepartmentSummaryTabSpecialRoles">
                                        <xsl:with-param name="linkSpecialRoles" select="$linkSpecialRoles"/>
                                        <xsl:with-param name="role" select="'head'"/>
                                    </xsl:call-template>
                                </xsl:for-each>
                            </ul>
                        </dd>
                    </xsl:if>
                    
                    <xsl:if test="count(./viceheads/vicehead)">
                        <dt>
                            <xsl:call-template name="mandalay:getStaticText">
                                <xsl:with-param name="module" select="'SciDepartment'"/>
                                <xsl:with-param name="id" select="'summaryTab/viceHeadsHeading'"/>
                            </xsl:call-template>
                        </dt>
                        <dd>
                            <ul>
                                <xsl:for-each select="./viceheads/vicehead">
                                    <xsl:call-template name="SciDepartmentSummaryTabSpecialRoles">
                                        <xsl:with-param name="linkSpecialRoles" select="$linkSpecialRoles"/>
                                        <xsl:with-param name="role" select="'vicehead'"/>
                                    </xsl:call-template>
                                </xsl:for-each>
                            </ul>
                        </dd>
                    </xsl:if>
                    
                    <xsl:if test="count(./secretariats/secretariat)">
                        <dt>
                            <xsl:call-template name="mandalay:getStaticText">
                                <xsl:with-param name="module" select="'SciDepartment'"/>
                                <xsl:with-param name="id" select="'summaryTab/secretariatHeading'"/>
                            </xsl:call-template>
                        </dt>
                        <dd>
                            <ul>
                                <xsl:for-each select="./secretariats/secretariat">
                                    <xsl:call-template name="SciDepartmentSummaryTabSpecialRoles">
                                        <xsl:with-param name="linkSpecialRoles" select="$linkSpecialRoles"/>
                                        <xsl:with-param name="role" select="'secretariat'"/>
                                    </xsl:call-template>
                                </xsl:for-each>
                            </ul>
                        </dd>
                    </xsl:if>
                    
                </dl>
            </xsl:if>
    
            <xsl:if test="count(./viceheads/vicehead) &gt; 0">
                <h3>
                    <xsl:call-template name="mandalay:getStaticText">
                        <xsl:with-param name="module" select="'SciDepartment'"/>
                        <xsl:with-param name="id" select="'summaryTab/viceheadsHeading'"/>
                    </xsl:call-template>
                </h3>
            </xsl:if>

            <xsl:if test="count(./subDepartments/subDepartment) &gt; 0">
                <h3>
                    <xsl:call-template name="mandalay:getStaticText">
                        <xsl:with-param name="module" select="'SciDepartment'"/>
                        <xsl:with-param name="id" select="'summaryTab/subDepartmentsHeading'"/>
                    </xsl:call-template>
                </h3>
                <ul>
                    <xsl:for-each select="./subDepartments/subDepartment">
                        <li>
                            <a class="departmentName">
                                <xsl:attribute name="href">/redirect?oid=<xsl:value-of select="@oid"/></xsl:attribute>
                                <xsl:value-of select="./title"/>
                            </a>
                            <xsl:text> </xsl:text>
                            <xsl:for-each select="./heads/head">
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
                                <xsl:if test="(count(../head) &gt; 1) and (position() != last())">
                                    <xsl:text>, </xsl:text>
                                </xsl:if>
                            </xsl:for-each>
                        </li>
                    </xsl:for-each>
                </ul>
      
            </xsl:if>

            <div class="endFloat"/>
        </div>

    </xsl:template>
    
    <xsl:template name="SciDepartmentSummaryTabSpecialRoles">
        
        <xsl:param name="linkSpecialRoles" select="'true'" />
        <xsl:param name="role" select="''"/>
        
        <xsl:choose>
            <xsl:when test="($linkSpecialRoles = 'true') and (./contacts/contact/contactentries[keyId='homepage'])">
                <a class="sciMember">
                    <xsl:attribute name="href">
                        <xsl:value-of select="./contacts/contact/contactentries[keyId='homepage']/value"/>
                    </xsl:attribute>
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
                </a>
            </xsl:when>
            <xsl:otherwise>
                <span class="sciMember">
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
                </span>
            </xsl:otherwise>
        </xsl:choose>
        
        <xsl:if test="position() != last()">
            <xsl:text>, </xsl:text>
        </xsl:if>
    </xsl:template>

    <xsl:template name="SciDepartmentSummaryTabSpecialContact">
        
        <xsl:param name="linkSpecialRoles" select="'true'" />
        <xsl:param name="role" select="''"/>
        
        <xsl:choose>
            <xsl:when test="($linkSpecialRoles = 'true') and (./contacts/contact/contactentries[keyId=$role]) and  $role='email'">
                <a class="contact">
                    <xsl:if test="string-length(./contacts/contact/contactentries[keyId=$role]/value) &gt; 0">
                        <xsl:attribute name="href">
                            <xsl:value-of select="concat('mailto:', ./contacts/contact/contactentries[keyId=$role]/value)"/>
                        </xsl:attribute>
                        <xsl:value-of select="./contacts/contact/contactentries[keyId=$role]/value"/>
                    </xsl:if>
                </a>
            </xsl:when>
            <xsl:otherwise>
                <span class="contact">
                    <xsl:if test="string-length(./contacts/contact/contactentries[keyId=$role]/value) &gt; 0">
                        <xsl:value-of select="./contacts/contact/contactentries[keyId=$role]/value"/>
                    </xsl:if>
                </span>
            </xsl:otherwise>
        </xsl:choose>
        
        <xsl:if test="position() != last()">
            <xsl:text>, </xsl:text>
        </xsl:if>
    </xsl:template>
        
</xsl:stylesheet>
