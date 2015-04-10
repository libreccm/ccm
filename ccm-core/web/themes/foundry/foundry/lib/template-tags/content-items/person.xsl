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
    
    <xsl:template match="/content-item-layout//person">
        <xsl:param name="contentitem-tree" tunnel="yes"/>
        
        <xsl:apply-templates>
            <xsl:with-param name="person"
                            tunnel="yes"
                            select="$contentitem-tree"/>
        </xsl:apply-templates>
    </xsl:template>
    
    <xsl:template match="/content-item-layout//*[ends-with(name(), 'person')]//surname">
        <xsl:param name="person" tunnel="yes"/>
        
        <xsl:value-of select="$person/surname"/>
    </xsl:template>
    
    <xsl:template match="/content-item-layout//*[ends-with(name(), 'person')]//givenname">
        <xsl:param name="person" tunnel="yes"/>
        
        <xsl:value-of select="$person/givenname"/>
    </xsl:template>
    
    <xsl:template match="/content-item-layout//*[ends-with(name(), 'person')]//titlepre">
        <xsl:param name="person" tunnel="yes"/>
        
        <xsl:value-of select="$person/titlepre"/>
    </xsl:template>
    
    <xsl:template match="/content-item-layout//*[ends-with(name(), 'person')]//titlepost">
        <xsl:param name="person" tunnel="yes"/>
        
        <xsl:value-of select="$person/titlepost"/>
    </xsl:template>
    
    <xsl:template match="/content-item-layout//*[ends-with(name(), 'person')]//if-surname">
        <xsl:param name="person" tunnel="yes"/>
        
        <xsl:if test="string-length($person/surname) &gt; 0">
            <xsl:apply-templates/>
        </xsl:if>
    </xsl:template>
    
    <xsl:template match="/content-item-layout//*[ends-with(name(), 'person')]//if-givenname">
        <xsl:param name="person" tunnel="yes"/>
        
        <xsl:if test="string-length($person/givenname) &gt; 0">
            <xsl:apply-templates/>
        </xsl:if>
    </xsl:template>
    
    <xsl:template match="/content-item-layout//*[ends-with(name(), 'person')]//if-titlepre">
        <xsl:param name="person" tunnel="yes"/>
        
        <xsl:if test="string-length($person/titlepre) &gt; 0">
            <xsl:apply-templates/>
        </xsl:if>
    </xsl:template>
    
    <xsl:template match="/content-item-layout//*[ends-with(name(), 'person')]//if-titlepost">
        <xsl:param name="person" tunnel="yes"/>
        
        <xsl:if test="string-length($person/titlepost) &gt; 0">
            <xsl:apply-templates/>
        </xsl:if>
    </xsl:template>
    
    <!--<xsl:template match="/content-item-layout//person-surname">
        <xsl:param name="contentitem-tree" tunnel="yes"/>
        
        <xsl:if test="string-length($contentitem-tree/surname) &gt; 0 and ./@pre">
            <xsl:value-of select="./@pre"/>
        </xsl:if>
        <xsl:value-of select="$contentitem-tree/surname"/>
        <xsl:if test="string-length($contentitem-tree/surname) &gt; 0 and ./@post">
            <xsl:value-of select="./@post"/>
        </xsl:if>
    </xsl:template>
    
    <xsl:template match="/content-item-layout//person-givenname">
        <xsl:param name="contentitem-tree" tunnel="yes"/>
        
        <xsl:if test="string-length($contentitem-tree/givenname) &gt; 0 and ./@pre">
            <xsl:value-of select="./@pre"/>
        </xsl:if>
        <xsl:value-of select="$contentitem-tree/givenname"/>
        <xsl:if test="string-length($contentitem-tree/givenname) &gt; 0 and ./@post">
            <xsl:value-of select="./@post"/>
        </xsl:if>
    </xsl:template>
    
    <xsl:template match="/content-item-layout//person-titlepre">
        <xsl:param name="contentitem-tree" tunnel="yes"/>
        
        <xsl:if test="string-length($contentitem-tree/titlepre) &gt; 0 and ./@pre">
            <xsl:value-of select="./@pre"/>
        </xsl:if>
        <xsl:value-of select="$contentitem-tree/titlepre"/>
        <xsl:if test="string-length($contentitem-tree/titlepre) &gt; 0 and ./@post">
            <xsl:value-of select="./@post"/>
        </xsl:if>
    </xsl:template>
    
    <xsl:template match="/content-item-layout//person-titlepost">
        <xsl:param name="contentitem-tree" tunnel="yes"/>
        
        <xsl:if test="string-length($contentitem-tree/titlepost) &gt; 0 and ./@pre">
            <xsl:value-of select="./@pre"/>
        </xsl:if>
        <xsl:value-of select="$contentitem-tree/titlepost"/>
        <xsl:if test="string-length($contentitem-tree/titlepost) &gt; 0 and ./@post">
            <xsl:value-of select="./@post"/>
        </xsl:if>
    </xsl:template>-->
    
    <xsl:template match="/content-item-layout//person-homepage-link">
        <xsl:param name="contentitem-tree" tunnel="yes"/>
       
        <xsl:variable name="homepage-contact-type"
                      select="if (./@contact-type)
                              then ./@contact-type
                              else 'commonContact'"/>
        
        <xsl:variable name="homepage-contact-entry-key"
                      select="if (./@contact-entry-key)
                              then ./@contact-entry-key
                              else 'homepage'"/>

        <xsl:choose>
            <xsl:when test="$contentitem-tree/contacts/contact[./@contactType = $homepage-contact-type]/contactentries[./keyId = $homepage-contact-entry-key]">
                <xsl:apply-templates>
                    <xsl:with-param name="href"
                                    tunnel="yes"
                                    select="$contentitem-tree/contacts/contact[@contactType = $homepage-contact-type]/contactentries[./keyId = $homepage-contact-entry-key]/value"/>
                </xsl:apply-templates>
            </xsl:when>
            <xsl:otherwise>
                <xsl:apply-templates/>
            </xsl:otherwise>
        </xsl:choose>
        
    </xsl:template>
    
    <xsl:template match="content-item-layout//person-contact-entries">
        <xsl:param name="contentitem-tree" tunnel="yes"/>
        
        <xsl:variable name="contact-type" 
                      select="if(./@contact-type)
                              then ./@contact-type
                              else 'commonContact'"/>

        <xsl:if test="$contentitem-tree/contacts/contact[@contactType=$contact-type]/contactentries">
            <xsl:apply-templates>
                <xsl:with-param name="contact-entries" 
                                tunnel="yes"
                                select="$contentitem-tree/contacts/contact[@contactType=$contact-type]/contactentries"/>
            </xsl:apply-templates>
        </xsl:if>
    </xsl:template>
    
    <xsl:template match="content-item-layout//person-address">
        <xsl:param name="contentitem-tree" tunnel="yes"/>
        
        <xsl:variable name="contact-type" 
                      select="if(./@contact-type)
                              then ./@contact-type
                              else 'commonContact'"/>
        
        <xsl:if test="$contentitem-tree/contacts/contact[@contactType=$contact-type]/address">
            <xsl:apply-templates>
                <xsl:with-param name="address"
                                tunnel="yes"
                                select="$contentitem-tree/contacts/contact[@contactType=$contact-type]/address"/>
            </xsl:apply-templates>
        </xsl:if>
    </xsl:template>

</xsl:stylesheet>