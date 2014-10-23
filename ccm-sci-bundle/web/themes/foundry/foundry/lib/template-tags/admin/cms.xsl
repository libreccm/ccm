<?xml version="1.0" encoding="utf-8"?>
<!--
    Copyright 2014 Jens Pelzetter for the LibreCCM Foundation
    
    This file is part of the Foundry Theme Engine for LibreCCM
    
    Foundry is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 2 of the License, or
    (at your option) any later version.

    Foundry is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with Foundry  If not, see <http://www.gnu.org/licenses/>.

-->

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xs="http://www.w3.org/2001/XMLSchema"
                xmlns:bebop="http://www.arsdigita.com/bebop/1.0"
                xmlns:cms="http://www.arsdigita.com/cms/1.0"
                xmlns:foundry="http://foundry.libreccm.org"
                xmlns:ui="http://www.arsdigita.com/ui/1.0"
                exclude-result-prefixes="xsl xs bebop cms foundry ui"
                version="2.0">

    <xsl:template match="show-cms-global-navigation">
        <div class="cms-global-navigation">
            <xsl:choose>
                <xsl:when test="*">
                    <xsl:apply-templates/>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:apply-templates select="$data-tree/cms:globalNavigation"/>
                </xsl:otherwise>
            </xsl:choose>
        </div>
    </xsl:template>
    
    <xsl:template match="show-cms-global-navigation/show-contentcenter-link">
        <xsl:apply-templates select="$data-tree/cms:globalNavigation/cms:contentCenter"/>
    </xsl:template>
  
    <xsl:template match="show-cms-global-navigation/show-admincenter-link">
        <xsl:apply-templates select="$data-tree/cms:globalNavigation/cms:adminCenter"/>
    </xsl:template>
  
    <xsl:template match="show-cms-global-navigation/show-workspace-link">
        <xsl:apply-templates select="$data-tree/cms:globalNavigation/cms:workspace"/>
    </xsl:template>
    
    <xsl:template match="show-cms-global-navigation/show-change-password-link">
        <xsl:choose>
            <xsl:when test="$data-tree/cms:globalNavigation">
                <xsl:apply-templates select="$data-tree/cms:globalNavigation/cms:changePassword"/>
            </xsl:when>
            <xsl:when test="$data-tree/ui:userBanner">
                <span class="cms-global-navigation-change-password">
                    <a href="{$data-tree/ui:userBanner/@changePasswordURL}">
                        <xsl:apply-templates select="$data-tree/ui:userBanner/@changePasswordLabel"/>
                    </a>
                </span>
            </xsl:when>
        </xsl:choose>
    </xsl:template>
  
    <xsl:template match="show-cms-global-navigation/show-logout-link">
        <xsl:choose>
            <xsl:when test="$data-tree/cms:globalNavigation">
                <xsl:apply-templates select="$data-tree/cms:globalNavigation/cms:signOut"/>
            </xsl:when>
            <xsl:when test="$data-tree/ui:userBanner">
                <span class="cms-global-navigation-signout">
                    <a href="{$data-tree/ui:userBanner/@logoutURL}">
                        <xsl:apply-templates select="$data-tree/ui:userBanner/@signoutLabel"/>
                    </a>
                </span>
            </xsl:when>
        </xsl:choose>
    </xsl:template>
  
    <xsl:template match="show-global-navigation/show-help-link">
        <xsl:apply-templates select="$data-tree/cms:globalNavigation/cms:help"/>
    </xsl:template>

    <xsl:template match="show-global-navigation/show-preview-link">
        <span class="cms-preview">
            <xsl:apply-templates select="$data-tree/bebop:link[@id='preview_link']"/>
        </span>
    </xsl:template>
    
    <xsl:template match="cms:contentCenter">
        <span class="cms-global-navigation-contentcenter">
            <xsl:call-template name="cms:globalNavigationEntry"/>
        </span>
    </xsl:template>
  
    <xsl:template match="cms:adminCenter">
        <span class="cms-global-navigation-admin-center">
            <xsl:call-template name="cms:globalNavigationEntry"/>
        </span>
    </xsl:template>
  
    <xsl:template match="cms:workspace">
        <span class="cms-global-navigation-workspace">
            <xsl:call-template name="cms:globalNavigationEntry"/>
        </span>
    </xsl:template>

    <xsl:template match="cms:changePassword">
        <span class="cms-global-navigation-change-password">
            <xsl:call-template name="cms:globalNavigationEntry"/>
        </span>
    </xsl:template>

    <xsl:template match="cms:signOut">
        <span class="cms-global-navigation-signout">
            <xsl:call-template name="cms:globalNavigationEntry"/>
        </span>
    </xsl:template>

    <xsl:template match="cms:help">
        <span class="cms-global-navigation-help">
            <xsl:call-template name="cms:globalNavigationEntry"/>
        </span>
    </xsl:template>

    <xsl:template name="cms:globalNavigationEntry">
        <a href="{@href}">
            <xsl:value-of select="@title"/>
        </a>
    </xsl:template>
    
    <!-- _______________________________________________________________________________________ -->
    
    <xsl:template match="show-content-type">
        <xsl:if test="$data-tree/bebop:contentType">
            <span id="contenttype">
                <xsl:value-of select="$data-tree/bebop:contentType"/>
            </span>
        </xsl:if>
    </xsl:template>
    
    <!-- _______________________________________________________________________________________ -->
    
    <xsl:template match="cms-greeting">
        <xsl:apply-templates/>
    </xsl:template>
    
    <xsl:template match="cms-greeting//cms-greeting-static-text">
        <xsl:value-of select="foundry:get-internal-static-text('cms', 'greeting')"/>
    </xsl:template>
    
    <xsl:template match="cms-greeting//user-name">
        <xsl:choose>
            <xsl:when test="$data-tree/@name">
                <xsl:value-of select="$data-tree/@name"/>
            </xsl:when>
            <xsl:when test="$data-tree//ui:userBanner/@screenName">
                <xsl:value-of select="concat($data-tree//ui:userBanner/@givenName, ' ', 
                                             $data-tree//ui:userBanner/@familyName)"/>
            </xsl:when>
        </xsl:choose>
    </xsl:template>
    
</xsl:stylesheet>