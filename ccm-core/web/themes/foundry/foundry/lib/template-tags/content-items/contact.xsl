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
    
    <xsl:template match="content-item-layout//contact-person">
        <xsl:param name="contentitem-tree" tunnel="yes"/>
        
        <xsl:apply-templates>
            <xsl:with-param name="person"
                            tunnel="yes"
                            select="$contentitem-tree/person"/>
        </xsl:apply-templates>
    </xsl:template>
    
    <!--<xsl:template match="content-item-layout//contact-person-givenname">
        <xsl:param name="contentitem-tree" tunnel="yes"/>
        
        <xsl:value-of select="$contentitem-tree/person/givenname"/>
    </xsl:template>
    
    <xsl:template match="content-item-layout//contact-person-surname">
        <xsl:param name="contentitem-tree" tunnel="yes"/>
        
        <xsl:value-of select="$contentitem-tree/person/surname"/>
    </xsl:template>
    
    <xsl:template match="content-item-layout//contact-person-titlepre">
        <xsl:param name="contentitem-tree" tunnel="yes"/>
        
        <xsl:value-of select="$contentitem-tree/person/titlepre"/>
    </xsl:template>
    
    <xsl:template match="content-item-layout//contact-person-titlepost">
        <xsl:param name="contentitem-tree" tunnel="yes"/>
        
        <xsl:value-of select="$contentitem-tree/person/titlepost"/>
    </xsl:template>
    
    <xsl:template match="content-item-layout//if-contact-person-givenname">
        <xsl:param name="contentitem-tree" tunnel="yes"/>
        
        <xsl:if test="string-length($contentitem-tree/person/givenname) &gt; 0">
            <xsl:apply-templates/>
        </xsl:if>
    </xsl:template>
    
    <xsl:template match="content-item-layout//if-contact-person-surname">
        <xsl:param name="contentitem-tree" tunnel="yes"/>
        
        <xsl:if test="string-length($contentitem-tree/person/surname) &gt; 0">
            <xsl:apply-templates/>
        </xsl:if>
    </xsl:template>
    
    <xsl:template match="content-item-layout//if-contact-person-titlepre">
        <xsl:param name="contentitem-tree" tunnel="yes"/>
        
        <xsl:if test="string-length($contentitem-tree/person/titlepre) &gt; 0">
            <xsl:apply-templates/>
        </xsl:if>
    </xsl:template>
    
    <xsl:template match="content-item-layout//if-contact-person-titlepost">
        <xsl:param name="contentitem-tree" tunnel="yes"/>
        
        <xsl:if test="string-length($contentitem-tree/person/titlepost) &gt; 0">
            <xsl:apply-templates/>
        </xsl:if>
    </xsl:template>-->
    
    <xsl:template match="content-item-layout//contact-entries">
        <xsl:param name="contentitem-tree" tunnel="yes"/>
        
        <xsl:if test="$contentitem-tree/contactentries">
            <xsl:apply-templates>
                <xsl:with-param name="contact-entries" 
                            tunnel="yes"
                            select="$contentitem-tree/contactentries"/>
            </xsl:apply-templates>
        </xsl:if>
    </xsl:template>
    
    <xsl:template match="content-item-layout//*[ends-with(name(), 'contact-entries')]//contact-entry">
        <xsl:param name="contact-entries" tunnel="yes"/>
        
        <xsl:variable name="keyId" select="./@key"/>
        
        <xsl:if test="$contact-entries[./keyId = $keyId]">
            <xsl:apply-templates>
                <xsl:with-param name="label" 
                                tunnel="yes"  
                                select="$contact-entries[./keyId = $keyId]/key"/>
                <xsl:with-param name="value" 
                                tunnel="yes"  
                                select="$contact-entries[./keyId = $keyId]/value"/>
            </xsl:apply-templates>
        </xsl:if>
    </xsl:template>
    
    <xsl:template match="content-item-layout//*[ends-with(name(), 'contact-entries')]//contact-entry//contact-entry-label">
        <xsl:param name="label" tunnel="yes"/>
        
        <xsl:value-of select="$label"/>
    </xsl:template>
    
    <xsl:template match="content-item-layout//*[ends-with(name(), 'contact-entries')]//contact-entry//contact-entry-value">
        <xsl:param name="value" tunnel="yes"/>
        
        <xsl:choose>
            <xsl:when test="(starts-with($value, 'http://')
                             or starts-with($value, 'https://')
                             or starts-with($value, 'www'))
                            and not(foundry:boolean(./@autolink))">
                <a href="{$value}">
                    <xsl:value-of select="$value"/>
                </a>
            </xsl:when> 
            <xsl:when test="contains($value, '@') 
                            and not(foundry:boolean(./@autolink))">
                <a href="{concat('mailto:', $value)}">
                    <xsl:value-of select="$value"/>
                </a>
            </xsl:when>
            <xsl:otherwise>
                <xsl:value-of select="$value"/>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>
    
    <xsl:template match="content-item-layout//*[ends-with(name(), 'contact-entries')]//contact-entry//contact-entry-value-as-link">
        <xsl:param name="value" tunnel="yes"/>
        
        <xsl:apply-templates>
            <xsl:with-param name="href" tunnel="yes" select="$value"/>
        </xsl:apply-templates>
    </xsl:template>

    <xsl:template match="content-item-layout//contact-address">
        <xsl:param name="contentitem-tree" tunnel="yes"/>
        
        <xsl:if test="$contentitem-tree/address">
            <xsl:apply-templates>
                <xsl:with-param name="address"
                                tunnel="yes"
                                select="$contentitem-tree/address"/>
            </xsl:apply-templates>
        </xsl:if>
    </xsl:template>
    
    <xsl:template match="content-item-layout//*[ends-with(name(),'-address')]//address-text">
        <xsl:param name="address" tunnel="yes"/>
        
        <xsl:value-of select="$address/address"/>
    </xsl:template>
    
    <xsl:template match="content-item-layout//*[ends-with(name(),'-address')]//postal-code">
        <xsl:param name="address" tunnel="yes"/>
        
        <xsl:value-of select="$address/postalCode"/>
    </xsl:template>
    
    <xsl:template match="content-item-layout//*[ends-with(name(),'-address')]//city">
        <xsl:param name="address" tunnel="yes"/>
        
        <xsl:value-of select="$address/city"/>
    </xsl:template>
    
    <xsl:template match="content-item-layout//*[ends-with(name(),'-address')]//state">
        <xsl:param name="address" tunnel="yes"/>
        
        <xsl:value-of select="$address/state"/>
    </xsl:template>
    
    <xsl:template match="content-item-layout//*[ends-with(name(),'-address')]//country">
        <xsl:param name="address" tunnel="yes"/>
        
        <xsl:value-of select="$address/country"/>
    </xsl:template>
    
    <xsl:template match="content-item-layout//*[ends-with(name(),'-address')]//iso-country-code">
        <xsl:param name="address" tunnel="yes"/>
        
        <xsl:value-of select="$address/isoCountryCode"/>
    </xsl:template>
    
</xsl:stylesheet>