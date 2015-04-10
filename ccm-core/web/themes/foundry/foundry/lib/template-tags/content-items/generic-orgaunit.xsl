<?xml version="1.0" encoding="utf-8"?>
<!--
    Copyright 2015 Jens Pelzetter for the LibreCCM Foundation
    
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
                xmlns:nav="http://ccm.redhat.com/navigation"
                xmlns:ui="http://www.arsdigita.com/ui/1.0"
                exclude-result-prefixes="xsl xs bebop cms foundry nav ui"
                version="2.0">
    
    <xsl:template match="content-item-layout//orgaunit-available-tabs">
        <xsl:param name="contentitem-tree" tunnel="yes"/>
        
        <xsl:apply-templates>
            <xsl:with-param name="available-tabs"
                            tunnel="yes"
                            select="$contentitem-tree/orgaUnitTabs/availableTabs"/>
            <xsl:with-param name="orgaunit-type-name" 
                            tunnel="yes" 
                            select="$contentitem-tree/type/label"/>
        </xsl:apply-templates>
    </xsl:template>
    
    <xsl:template match="content-item-layout//orgaunit-available-tabs//available-tab">
        <xsl:param name="available-tabs" tunnel="yes"/>
        
        <xsl:variable name="selected-classes" select="./@selected-classes"/>
        <xsl:variable name="layout-tree" select="./*"/>
                
        <xsl:for-each select="$available-tabs/availableTab">
            <xsl:apply-templates select="$layout-tree">
                <xsl:with-param name="label" 
                                tunnel="yes" 
                                select="./@label"/>
                <xsl:with-param name="selected" 
                                tunnel="yes" 
                                select="foundry:boolean(./@selected)"/>
                <xsl:with-param name="class" select="if(foundry:boolean(./@selected))
                                                     then $selected-classes
                                                     else ''"/>
                <xsl:with-param name="href"
                                tunnel="yes"
                                select="foundry:parse-link(concat('?selectedTab=', ./@label))"/>
            </xsl:apply-templates>
        </xsl:for-each>
    </xsl:template>
    
    <xsl:template match="content-item-layout//orgaunit-available-tabs//available-tab//tab-name">
        <xsl:param name="label" tunnel="yes"/>
        
        <xsl:value-of select="$label"/>
    </xsl:template>
    
    <xsl:template match="content-item-layout//orgaunit-available-tabs//available-tab//tab-label">
        <xsl:param name="label" tunnel="yes"/>
        <xsl:param name="orgaunit-type-name" tunnel="yes"/>
        
        <xsl:value-of select="foundry:get-static-text(lower-case($orgaunit-type-name), $label)"/>
    </xsl:template>
    
    <xsl:template match="content-item-layout//orgaunit">
        <xsl:param name="contentitem-tree" tunnel="yes"/>
        
        <xsl:apply-templates>
            <xsl:with-param name="orgaunit-data" 
                            tunnel="yes"
                            select="$contentitem-tree"/>
            <xsl:with-param name="orgaunit-type-name"
                            tunnel="yes"
                            select="$contentitem-tree/type/label"/>
        </xsl:apply-templates>
    </xsl:template>
    
    <xsl:template match="content-item-layout//orgaunit-current-tab">
        <xsl:param name="contentitem-tree" tunnel="yes"/>
        
        <xsl:variable name="selected-tab" 
                      select="$contentitem-tree/orgaUnitTabs/availableTabs/availableTab[@selected='true']/@label"/>
        
        <xsl:apply-templates select="./tab[@name=$selected-tab]/*">
            <xsl:with-param name="orgaunit-data" 
                            tunnel="yes"
                            select="$contentitem-tree/orgaUnitTabs/selectedTab/*"/>
            <xsl:with-param name="orgaunit-type-name"
                            tunnel="yes"
                            select="$contentitem-tree/type/label"/>
        </xsl:apply-templates>
    </xsl:template>
    
    <xsl:template match="content-item-layout//*[starts-with(name(), 'orgaunit')]//addendum">
        <xsl:param name="orgaunit-data" tunnel="yes"/>
        
        <xsl:value-of select="$orgaunit-data/addendum"/>
    </xsl:template>
    
    <xsl:template match="content-item-layout//*[starts-with(name(), 'orgaunit')]//members">
        <xsl:param name="orgaunit-data" tunnel="yes"/>
        
        <xsl:apply-templates>
            <xsl:with-param name="members" 
                            tunnel="yes" 
                            select="$orgaunit-data/members"/>
        </xsl:apply-templates>
    </xsl:template>
    
    <xsl:template match="content-item-layout//*[starts-with(name(), 'orgaunit')]//members//member">
        <xsl:param name="members" tunnel="yes"/>
        
        <xsl:variable name="layout-tree" select="./*"/>
        
        <xsl:for-each select="$members/member">
            <xsl:apply-templates select="$layout-tree">
                <xsl:with-param name="person" 
                                tunnel="yes" 
                                select="."/>
                <xsl:with-param name="member-role" 
                                tunnel="yes" 
                                select="./@role"/>
                <xsl:with-param name="member-status" 
                                tunnel="yes" 
                                select="./@status"/>
            </xsl:apply-templates>
        </xsl:for-each>
    </xsl:template>
    
    <xsl:template match="content-item-layout//*[starts-with(name(), 'orgaunit')]//members//member//member-status">
        <xsl:param name="member-status" tunnel="yes"/>
        <xsl:param name="orgaunit-type-name" tunnel="yes"/>
        
        <xsl:if test="string-length($member-status) &gt; 0">
            <xsl:value-of select="foundry:get-static-text($orgaunit-type-name, lower-case($member-status))"/>
        </xsl:if>
    </xsl:template>
    
    <xsl:template match="content-item-layout//*[starts-with(name(), 'orgaunit')]//members//member//member-role">
        <xsl:param name="member-role" tunnel="yes"/>
        <xsl:param name="orgaunit-type-name" tunnel="yes"/>
        
        <xsl:if test="string-length($member-role) &gt; 0">
            <xsl:value-of select="foundry:get-static-text(lower-case($orgaunit-type-name), $member-role)"/>
        </xsl:if>
    </xsl:template>
    
    <xsl:template match="content-item-layout//*[starts-with(name(), 'orgaunit')]//members//member//if-member-role-is">
        <xsl:param name="member-role" tunnel="yes"/>
        
        <xsl:if test="$member-role = ./@role">
            <xsl:apply-templates/>
        </xsl:if>
    </xsl:template>
    
    <xsl:template match="content-item-layout//*[starts-with(name(), 'orgaunit')]//orgaunit-contact">
        <xsl:param name="orgaunit-data" tunnel="yes"/>
        
        <xsl:if test="$orgaunit-data/contacts/contact[1]">
            <xsl:apply-templates/>
        </xsl:if>
    </xsl:template>
    
    <xsl:template match="content-item-layout//*[starts-with(name(), 'orgaunit')]//orgaunit-contact//orgaunit-contact-person">
        <xsl:param name="orgaunit-data" tunnel="yes"/>
        
        <xsl:apply-templates>
            <xsl:with-param name="person" 
                            tunnel="yes"
                            select="$orgaunit-data/contacts/contact[1]/person"/>
        </xsl:apply-templates>
    </xsl:template>
    
    <xsl:template match="content-item-layout//*[starts-with(name(), 'orgaunit')]//orgaunit-contact//orgaunit-contact-entries">
        <xsl:param name="orgaunit-data" tunnel="yes"/>
        
        <xsl:if test="$orgaunit-data/contacts/contact[1]/contactentries">
            <xsl:apply-templates>
                <xsl:with-param name="contact-entries"
                                tunnel="yes"
                                select="$orgaunit-data/contacts/contact[1]/contactentries"/>
            </xsl:apply-templates>
        </xsl:if>
    </xsl:template>
    
</xsl:stylesheet>