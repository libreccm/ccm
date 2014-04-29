<?xml version="1.0" encoding="utf-8"?>
<!DOCTYPE stylesheet [<!ENTITY nbsp '&#160;'>]>

<!-- 
    Copyright: 2006, 2007, 2008 Sören Bernstein
  
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

<!-- DE
  Hier wird die globale Navigation des CMS verarbeitet 
-->

<!-- EN
  Processing global navigation for cms
-->

<!-- Autor: Sören Bernstein -->

<xsl:stylesheet 
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
    xmlns:bebop="http://www.arsdigita.com/bebop/1.0"
    xmlns:cms="http://www.arsdigita.com/cms/1.0" 
    xmlns:nav="http://ccm.redhat.com/navigation"
    xmlns:mandalay="http://mandalay.quasiweb.de" 
    exclude-result-prefixes="xsl bebop cms nav mandalay"
    version="1.0">
  
    <!-- DE Das Menü -->
    <!-- EN The menu -->
    <xsl:template match="cms:globalNavigation">
        <xsl:param name="layoutTree" select="."/>
    
        <xsl:variable name="setLayout">
            <xsl:call-template name="mandalay:getSetting">
                <xsl:with-param name="node"  select="$layoutTree/setLayout"/>
                <xsl:with-param name="module"  select="'cms'"/>
                <xsl:with-param name="setting" select="'globalNavigation/setLayout'"/>
                <xsl:with-param name="default" select="'horizontal'"/>
            </xsl:call-template>
        </xsl:variable>

        <div class="cmsGlobalNavigation">
            <xsl:choose>
                <xsl:when test="$setLayout = 'horizontal'">
                    <xsl:apply-templates/>
                </xsl:when>
                <xsl:otherwise>
                    <ul>
                        <xsl:for-each select="*">
                            <li>
                                <xsl:apply-templates/>
                            </li>
                        </xsl:for-each>
                    </ul>
                </xsl:otherwise>
            </xsl:choose>
        </div>
    </xsl:template>
  
    <!-- DE -->
    <!-- EN -->
    <xsl:template match="cms:contentCenter">
        <span class="cmsGlobalNavigationContentCenter">
            <xsl:call-template name="cms:globalNavigationEntry"/>
        </span>
    </xsl:template>
  
    <!-- DE -->
    <!-- EN -->
    <xsl:template match="cms:adminCenter">
        <span class="cmsGlobalNavigationAdminCenter">
            <xsl:call-template name="cms:globalNavigationEntry"/>
        </span>
    </xsl:template>
  
    <!-- DE -->
    <!-- EN -->
    <xsl:template match="cms:workspace">
        <span class="cmsGlobalNavigationWorkspace">
            <xsl:call-template name="cms:globalNavigationEntry"/>
        </span>
    </xsl:template>

    <xsl:template match="cms:changePassword">
        <span class="cmsGlobalNavigationChangePassword">
            <xsl:call-template name="cms:globalNavigationEntry"/>
        </span>
    </xsl:template>

    <!-- DE -->
    <!-- EN -->
    <xsl:template match="cms:signOut">
        <span class="cmsGlobalNavigationSignOut">
            <xsl:call-template name="cms:globalNavigationEntry"/>
        </span>
    </xsl:template>

    <!-- DE -->
    <!-- EN -->
    <xsl:template match="cms:help">
        <span class="cmsGlobalNavigationHelp">
            <xsl:call-template name="cms:globalNavigationEntry"/>
        </span>
    </xsl:template>

    <!-- DE Erzeuge den Link -->
    <!-- EN Create the link -->
    <xsl:template name="cms:globalNavigationEntry">
        <a href="{@href}">
            <xsl:value-of select="@title"/>
        </a>
    </xsl:template>

</xsl:stylesheet>
