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
                xmlns:nav="http://ccm.redhat.com/navigation"
                xmlns:ui="http://www.arsdigita.com/ui/1.0"
                exclude-result-prefixes="xsl xs bebop cms foundry nav ui"
                version="2.0">

        <xsl:template match="content-item-layout//profile-owner">
        <xsl:param name="contentitem-tree" tunnel="yes"/>

        <xsl:if test="$contentitem-tree/profileOwner">
            <xsl:apply-templates/>
        </xsl:if>
    </xsl:template>

    <xsl:template match="content-item-layout//profile-owner//surname">
        <xsl:param name="contentitem-tree" tunnel="yes"/>

        <xsl:value-of select="$contentitem-tree/profileOwner/surname"/>
    </xsl:template>
  
    <xsl:template match="content-item-layout//profile-owner//givenname">
        <xsl:param name="contentitem-tree" tunnel="yes"/>
        
        <xsl:value-of select="$contentitem-tree/profileOwner/givenname"/>
    </xsl:template>

    <xsl:template match="content-item-layout//profile-owner//titlepre">
        <xsl:param name="contentitem-tree" tunnel="yes"/>
        
        <xsl:value-of select="$contentitem-tree/profileOwner/titlepre"/>
    </xsl:template>

    <xsl:template match="content-item-layout//profile-owner//titlepost">
        <xsl:param name="contentitem-tree" tunnel="yes"/>
        
        <xsl:value-of select="$contentitem-tree/profileOwner/titlepost"/>
    </xsl:template>

    <xsl:template match="content-item-layout//profile-owner//contact">
        <xsl:param name="contentitem-tree" tunnel="yes"/>
        <xsl:variable name="contact-type" select="./@contact-type"/>

        <xsl:if test="$contentitem-tree/profileOwner/contacts/contact[@contactType=$contact-type]">
            <xsl:apply-templates>
                <xsl:with-param name="contact-tree" 
                                tunnel="yes"
                                select="$contentitem-tree/profileOwner/contacts/contact[@contactType=$contact-type][1]"/>
            </xsl:apply-templates>
        </xsl:if>
    </xsl:template>

    <xsl:template match="content-item-layout//profile-owner//contact//owner-address">
        <xsl:param name="contact-tree" tunnel="yes"/>

        <xsl:if test="$contact-tree/address">
            <xsl:apply-templates>
                <xsl:with-param name="address"
                               tunnel="yes"
                               select="$contact-tree/address"/>
            </xsl:apply-templates>
        </xsl:if>
    </xsl:template>

    <!--<xsl:template match="content-item-layout//profile-owner//contact//owner-address//address-text">
        <xsl:param name="contact-tree" tunnel="yes"/>
        
        <xsl:if test="$contact-tree/address">
            <xsl:apply-templates/>
        </xsl:if>
    </xsl:template>

    <xsl:template match="content-item-layout//profile-owner//contact//owner-address//address-text">
        <xsl:param name="contact-tree" tunnel="yes"/>

        <xsl:value-of select="$contact-tree/address"/>
    </xsl:template>
 
    <xsl:template match="content-item-layout//profile-owner//contact//owner-address//postal-code">
        <xsl:param name="contact-tree" tunnel="yes"/>

        <xsl:value-of select="$contact-tree/postalCode"/>
    </xsl:template>

    <xsl:template match="content-item-layout//profile-owner//contact//owner-address//city">
        <xsl:param name="contact-tree" tunnel="yes"/>

        <xsl:value-of select="$contact-tree/city"/>
    </xsl:template>

    <xsl:template match="content-item-layout//profile-owner//contact//owner-address//state">

        <xsl:param name="contact-tree" tunnel="yes"/>

        <xsl:value-of select="$contact-tree/state"/>
    </xsl:template>

    <xsl:template match="content-item-layout//profile-owner//contact//owner-address//iso-country-code">
        <xsl:param name="contact-tree" tunnel="yes"/>

        <xsl:value-of select="$contact-tree/isoCountryCode"/>
    </xsl:template>

    <xsl:template match="content-item-layout//profile-owner//contact//owner-address//country">
        <xsl:param name="contact-tree" tunnel="yes"/>

        <xsl:value-of select="$contact-tree/country"/>
    </xsl:template>-->

    <xsl:template match="content-item-layout//profile-owner//contact//contact-entries">
        <xsl:param name="contact-tree" tunnel="yes"/>
        
        <xsl:if test="$contact-tree/contactentries">
            <xsl:apply-templates>
                <xsl:with-param name="contact-entries"
                                tunnel="yes"
                                select="$contact-tree/contactentries"/>
            </xsl:apply-templates>
        </xsl:if>
    </xsl:template>

    
</xsl:stylesheet>