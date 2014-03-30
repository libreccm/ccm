<?xml version="1.0" encoding="utf-8"?>
<!DOCTYPE stylesheet [<!ENTITY nbsp '<xsl:text disable-output-escaping="yes">&amp;</xsl:text>nbsp;'>]>

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

<xsl:stylesheet  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                 xmlns:bebop="http://www.arsdigita.com/bebop/1.0"
                 xmlns:aplaws="http://www.arsdigita.com/aplaws/1.0"
                 xmlns:ui="http://www.arsdigita.com/ui/1.0"
                 xmlns:cms="http://www.arsdigita.com/cms/1.0"
                 xmlns:nav="http://ccm.redhat.com/navigation"
                 xmlns:search="http://rhea.redhat.com/search/1.0"
                 xmlns:portal="http://www.uk.arsdigita.com/portal/1.0"
                 xmlns:ppp="http://www.arsdigita.com/PublicPersonalProfile/1.0"
                 xmlns:mandalay="http://mandalay.quasiweb.de"
                 exclude-result-prefixes="xsl bebop aplaws ui cms nav search portal ppp mandalay"
                 version="1.0">

    <!-- Autor: Sören Bernstein -->

    <!-- DE Hier können Erweiterungen für den LayoutParser integriert werden, z.B. für eigene Module -->
    <!-- DE Die Templates werden über xsl:apply-templates aufgerufen, also müssen match-Angaben vorhanden sein -->

    <!-- EN this file is for integrating user coded extentions for layoutParser -->
    <!-- EN templates are called by xsl:apply-templates, so there must be a match-attribute for the template -->

    <!-- DE Beispiel template: Zum testen, hier Kommentar entfernen und in der Layout-Datei <userFunction/> eintragen-->
    <!-- EN Example template : to test, remove comment and insert <userFunction/> in layout file-->
  
    <!--<xsl:template match="userFunction">
      <h2>UserFunction</h2>
    </xsl:template>
    -->

    <xsl:template match="fragmentLayout">
        <div class="ccmFragment">
            <xsl:apply-templates/>
        </div>
    </xsl:template>

    <xsl:template match="showDynamicImage">
        <div>
            <xsl:call-template name="mandalay:setIdAndClass"/>
            <xsl:call-template name="mandalay:dynamicImage"/>
        </div>
    </xsl:template> 

    <xsl:template match="showSubSiteBanner">
        <xsl:call-template name="subsiteBanner"/>
    </xsl:template>

    <xsl:template match="useEditLink">
        <xsl:choose>
            <xsl:when test="$resultTree//cms:contentPanel/cms:item/editLink">
                <xsl:call-template name="mandalay:itemEditLink">
                    <xsl:with-param name="editUrl" select="$resultTree//cms:contentPanel/cms:item/editLink"/>
                    <xsl:with-param name="itemTitle" select="$resultTree//cms:contentPanel/cms:item/title"/>
                </xsl:call-template>
            </xsl:when>
            <xsl:when test="$resultTree//nav:greetingItem/cms:item/editLink">
                <xsl:call-template name="mandalay:itemEditLink">
                    <xsl:with-param name="editUrl" select="$resultTree//nav:greetingItem/cms:item/editLink"/>  
                    <xsl:with-param name="itemTitle" select="$resultTree//nav:greetingItem/cms:item/title"/>
                </xsl:call-template>
            </xsl:when>
        </xsl:choose>      
        <!--<div class="itemEditLink">
          <a>
            <xsl:attribute name="href">
                <xsl:value-of select="concat('/ccm/', $resultTree//cms:contentPanel/cms:item/editLink)"/>
            </xsl:attribute>
            <xsl:text>
              CLICK TO EDIT
            </xsl:text>
          </a>
        </div>-->
    </xsl:template>

    <xsl:template match="useHomepageTitle">
        <xsl:call-template name="mandalay:homepageTitle"/>
    </xsl:template>

    <xsl:template match="useNavigationHeading">
        <xsl:call-template name="mandalay:navigationHeading"/>
    </xsl:template>

    <xsl:template match="showPublicationExportLinks">
        <xsl:call-template name="showPublicationExportLinks"/>
    </xsl:template>

    <xsl:template match="showPublicationLibrarySignatures">
        <xsl:call-template name="showPublicationLibrarySignatures"/>
    </xsl:template>

    <xsl:template match="showPPPOwnerName">
        <xsl:apply-templates select="$resultTree//ppp:ownerName"/>
    </xsl:template>

    <xsl:template match="showPPPOwnerImage">
        <xsl:apply-templates select="$resultTree//ppp:profileImage"/>
    </xsl:template>

    <xsl:template match="useOrgaUnitTab">
        <xsl:apply-templates select="$resultTree/orgaUnitTabs/selectedTab/." mode="tabs"/>
    </xsl:template>

    <xsl:template match="piwikJsTracker">
        <xsl:call-template name="piwikJsTracker">
            <xsl:with-param name="piwikUrl" select="./@piwikUrl"/>
            <xsl:with-param name="idSite" select="./@idSite"/>
        </xsl:call-template>
    </xsl:template>
   
    <xsl:template match="piwikImageTracker">
        <xsl:call-template name="piwikImageTracker">
            <xsl:with-param name="piwikUrl" select="./@piwikUrl"/>
            <xsl:with-param name="idSite" select="./@idSite"/>
        </xsl:call-template>       
    </xsl:template>

    <xsl:template match="showSocialMedia">
        <xsl:call-template name="mandalay:socialMedia"/>
    </xsl:template>

    <xsl:template match="script">
        <script type="text/javascript">
            <xsl:attribute name="src">
                <xsl:choose>
                    <xsl:when test="./@absolute = 'true'">
                        <!-- Path is absolute, use path with no modifications -->
                        <xsl:value-of select="." />
                    </xsl:when>
                    <xsl:otherwise>
                        <!-- Path is relative to theme, add theme-prefix to create absolute path -->
                        <xsl:value-of select="concat($theme-prefix, .)"/>
                    </xsl:otherwise>
                </xsl:choose>
            </xsl:attribute>
        </script>
    </xsl:template>
  
    <xsl:template match="showMinimizeImageLink">
        <xsl:call-template name="mandalay:minimizeHeader">
            <xsl:with-param name="imageClass" select="./imageClass"/>
            <xsl:with-param name="linkClass" select="./linkClass"/>
            <xsl:with-param name="minimizeText" select="./minimizeText"/>
            <xsl:with-param name="maximizeText" select="./maximizeText"/>
        </xsl:call-template>
    </xsl:template>

    <xsl:template match="useYAML">
        <link rel="stylesheet" type="text/css" href="{$theme-prefix}/yaml/core/base.min.css"/>
        <xsl:text disable-output-escaping="yes">
&lt;!--[if lte IE 7]&gt;
&lt;link rel="stylesheet" type="text/css" href="{$theme-prefix}/yaml/core/iehacks.min.css"/&gt;
&lt;![endif]--&gt;
        </xsl:text>
        
        <xsl:if test="./accessibleTabs[@enabled='true']">
            <script type="text/javascript" 
                    href="{$theme-prefix}/yaml/add-ons/accessible-tabs/jquery.tabs.js"/>
            <link rel="stylesheet" 
                  type="text/css" 
                  href="{$theme-prefix}/yaml/add-ons/accessible-tabs/tabs.css"/>
        </xsl:if>

        <xsl:if test="./microformats[@enabled='true']">
            <link rel="stylesheet" 
                  type="text/css" 
                  href="{$theme-prefix}/yaml/add-ons/syncheight/jquery.syncheight.js" />
        </xsl:if>
                        
        <xsl:if test="./forms">
            <link rel="stylesheet" 
                  type="text/css">
                <xsl:choose>
                    <xsl:when test="./forms/@theme">
                        <xsl:attribute name="href">
                            <xsl:value-of select="concat($theme-prefix, '/', ./forms/@theme)"/>
                        </xsl:attribute>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:attribute name="href">
                            <xsl:value-of select="concat($theme-prefix, '/yaml/forms/gray-theme.css')"/>
                        </xsl:attribute>
                    </xsl:otherwise>
                </xsl:choose>
            </link>
        </xsl:if>
        
        <xsl:if test="not(./navigation[@enabled != 'false'])">
            <link rel="stylesheet" type="text/css" href="{$theme-prefix}/yaml/navigation/hlist.css"/>
            <link rel="stylesheet" type="text/css" href="{$theme-prefix}/yaml/navigation/vlist.css"/>
        </xsl:if>
        
        <xsl:if test="not(./print[@enabled != 'false'])">
            <link rel="stylesheet" type="text/css" href="{$theme-prefix}/yaml/print/print.css"/>
        </xsl:if>
        
        <xsl:if test="./grid">
            <link rel="stylesheet"
                  type="text/css"
                  href="{$theme-prefix}/yaml/screen/grid-{./grid}.css" />
        </xsl:if>
        
        <xsl:if test="./layout">
            <link rel="stylesheet" 
                  type="text/css"
                  href="{$theme-prefix}/yaml/screen/{concat('screen-', ./layout, '-layout.css')}"/>
        </xsl:if>
        
        <xsl:if test="./typography[@enabled='true']">
            <link rel="stylesheet" 
                  type="text/css" 
                  href="{$theme-prefix}/yaml/screen/typography.css"/>
        </xsl:if>
                        
        <xsl:if test="./rtlSupport[@enabled='true']">
            <link rel="stylesheet" 
                  type="text/css"
                  href="{$theme-prefix}/yaml/add-ons/rtl-support/core/base-rtl.min.css"/>
            <xsl:if test="./navigation[@enabled='true']">
                <link rel="stylesheet" 
                      type="text/css" 
                      href="{$theme-prefix}/yaml/add-ons/rtl-support/navigation/hlist-rtl.css"/>
                <link rel="stylesheet" 
                      type="text/css" 
                      href="{$theme-prefix}/yaml/add-ons/rtl-support/navigation/vlist-rtl.css"/>
            </xsl:if>
            <xsl:if test="./typography[@enabled='true']">
                <link rel="stylesheet" 
                      type="text/css" 
                      href="{$theme-prefix}/yaml/add-ons/rtl-support/typography/typography-rtl.css"/>
            </xsl:if>
        </xsl:if>
        
    </xsl:template>

</xsl:stylesheet> 
