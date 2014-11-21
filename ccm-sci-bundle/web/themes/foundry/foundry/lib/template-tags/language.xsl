<?xml version="1.0" encoding="utf-8"?>
<!DOCTYPE stylesheet [<!ENTITY nbsp '&#160;'>
                      <!ENTITY shy '&#173;'>]>
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

    <xsl:template match="language-selector">
        
        <xsl:apply-templates/>
    </xsl:template>
    
    <xsl:template match="language-selector//language">
        <xsl:variable name="language-layout-tree" select="./*"/>
        
        <xsl:for-each select="document(foundry:gen-path('conf/global.xml'))/foundry:configuration/supported-languages/language">
            <xsl:apply-templates select="$language-layout-tree">
                <xsl:with-param name="id" select="concat('language-selector-', ./@locale)"/>
                <xsl:with-param name="href" tunnel="yes" select="concat('?lang=', ./@locale)"/>
                <xsl:with-param name="language-name" 
                                tunnel="yes" 
                                select="foundry:get-static-text('', concat('language/', ./@locale))"/>
            </xsl:apply-templates>
        </xsl:for-each>
    </xsl:template>
    
    <xsl:template match="language-selector//language//language-name">
        <xsl:param name="language-name" tunnel="yes"/>
        
        <xsl:value-of select="$language-name"/>
    </xsl:template>

</xsl:stylesheet>