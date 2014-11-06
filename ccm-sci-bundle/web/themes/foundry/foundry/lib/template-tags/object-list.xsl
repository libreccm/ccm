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
                xmlns:bebop="http://www.arsdigita.com/bebop/1.0"
                xmlns:foundry="http://foundry.libreccm.org"
                xmlns:nav="http://ccm.redhat.com/navigation"
                xmlns:ui="http://www.arsdigita.com/ui/1.0"
                xmlns:xs="http://www.w3.org/2001/XMLSchema"
                xmlns="http://www.w3.org/1999/xhtml"
                exclude-result-prefixes="xsl xs bebop foundry ui"
                version="2.0">

    <xsl:template match="object-list">
        
        <xsl:variable name="object-list-id" select="./@id"/>
        
        <xsl:if test="$data-tree//nav:simpleObjectList[@id = $object-list-id]
                      | $data-tree//nav:complexObjectList[@id = $object-list-id]
                      | $data-tree//nav:customizableObjectList[@id = $object-list-id]
                      | $data-tree//nav:atozObjectList[@id = $object-list-id]
                      | $data-tree//nav:filterObjectList[@id = $object-list-id]">
            
            <xsl:variable name="object-list-datatree">
                <xsl:choose>
                    <xsl:when test="$data-tree//nav:simpleObjectList[@id = $object-list-id]">
                        <xsl:copy-of select="$data-tree//nav:simpleObjectList[@id = $object-list-id]/*"/>
                    </xsl:when>
                    <xsl:when test="$data-tree//nav:complexObjectList[@id = $object-list-id]">
                        <xsl:copy-of select="$data-tree//nav:complexObjectList[@id = $object-list-id]/*"/>
                    </xsl:when>
                    <xsl:when test="$data-tree//nav:customizableObjectList[@id = $object-list-id]">
                        <xsl:copy-of select="$data-tree//nav:customizableObjectList[@id = $object-list-id]/*"/>
                    </xsl:when>
                    <xsl:when test="$data-tree//nav:atozObjectList[@id = $object-list-id]">
                        <xsl:copy-of select="$data-tree//nav:atozObjectList[@id = $object-list-id]/*"/>
                    </xsl:when>
                    <xsl:when test="$data-tree//nav:filterObjectList[@id = $object-list-id]">
                        <xsl:copy-of select="$data-tree//nav:filterObjectList[@id = $object-list-id]/*"/>
                    </xsl:when>
                </xsl:choose>
            </xsl:variable>
            
            <xsl:apply-templates>
                <xsl:with-param name="object-list-datatree" 
                                tunnel="yes" 
                                select="$object-list-datatree"/>
            </xsl:apply-templates>
        </xsl:if>
    </xsl:template>

    <xsl:template match="object-list//object-list-item">
        <xsl:param name="object-list-datatree" tunnel="yes"/>
        
        <!--<pre>Object-list-item</pre>
            <pre>
                <xsl:value-of select="concat('count(object-list-datatree) = ', count($object-list-datatree))"/>
            </pre>
            <pre>
                <xsl:value-of select="concat('count(object-list-datatree/*) = ', count($object-list-datatree/*))"/>
            </pre>
            <pre>
                <xsl:value-of select="concat('name(object-list-datatree/*[1]) = ', name($object-list-datatree/*[1]))"/>
            </pre>
        </xsl:template>-->
    
        <xsl:variable name="object-list-item-layouttree" select="current()"/>
    
        <xsl:for-each select="$object-list-datatree/nav:objectList/nav:item">
            <xsl:apply-templates select="$object-list-item-layouttree/*">
                <xsl:with-param name="contentitem-tree" tunnel="yes" select="current()"/>
            </xsl:apply-templates>
        </xsl:for-each>
        
    </xsl:template>

</xsl:stylesheet>