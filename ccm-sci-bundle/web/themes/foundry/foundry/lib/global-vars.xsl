<?xml version="1.0" encoding="utf-8"?>
<!DOCTYPE stylesheet [<!ENTITY nbsp '&#160;'>
                      <!ENTITY shy '&#173;'>
                      <!ENTITY hellip '&#8230;'>
                    ]>
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

<!-- 
This file defines several global variables (constants). Some are provided by CCM thorough the XSL
processor, some are read from the configuration files of Foundry and some are defined here.
-->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:bebop="http://www.arsdigita.com/bebop/1.0"
                xmlns:foundry="http://foundry.libreccm.org"
                xmlns:ui="http://www.arsdigita.com/ui/1.0"
                version="2.0">
    
    <!-- Foundry internal variables -->
    <foundry:doc section="devel">
        <foundry:doc-desc>
            The version of Foundry. Kept in sync with the version of CCM, so the first version
            was be 2.2.3.
        </foundry:doc-desc>
    </foundry:doc>
    <xsl:variable name="foundry-version" select="'2.2.3-SNAPSHOT'"/>
    
    <!-- **************************************************************************** -->
    
    <!-- CCM Environment variables -->
    <foundry:doc section="devel">
        <foundry:doc-desc>
            The path the to theme file. This path is used at several points to load files which are
            part of the theme, like CSS files, images and fonts.
        </foundry:doc-desc>
    </foundry:doc>
    <xsl:param name="theme-prefix"/>
    
    <foundry:doc section="devel">
        <foundry:doc-desc>
            The context prefix in which CCM is installed. If CCM is installed into the ROOT context
            of the servlet container, this variable will be empty.
        </foundry:doc-desc>
    </foundry:doc>
    <xsl:param name="context-prefix"/>
    
    <foundry:doc section="devel">
        <foundry:doc-desc>
            The path on which the CCM dispatcher Servlet is mounted. Usually this is <code>CCM</code>
        </foundry:doc-desc>
    </foundry:doc>
    <xsl:param name="dispatcher-prefix"/>
    
    <xsl:variable name="username"> 
        <xsl:choose>
            <xsl:when test="/bebop:page/ui:userBanner/@screenName">
                <xsl:value-of select="concat(/bebop:page/ui:userBanner/@givenName, ' ', /bebop:page/ui:userBanner/@familyName)"/>
            </xsl:when>
        </xsl:choose>
    </xsl:variable>
    
    <!-- System variables -->
    
    <foundry:doc section="devel">
        <foundry:doc-desc>
            This variables stores the XML created by CCM for later access.
        </foundry:doc-desc>
    </foundry:doc>
    <xsl:variable name="data-tree" select="/bebop:page"/>
    
    <!-- **************************************************************************** -->
    
    <!-- Double click protection -->
    <foundry:doc section="devel">
        <foundry:doc-desc>
            Activate double click protection on buttons?
        </foundry:doc-desc>
    </foundry:doc>
    <xsl:param name="dcp-on-buttons"/>
    
    <foundry:doc section="devel">
        <foundry:doc-desc>
            Activate double click protection on links?
        </foundry:doc-desc>
    </foundry:doc>
    <xsl:param name="dcp-on-links"/>
    
    <!-- **************************************************************************** -->
    
    
    <!-- Language related variables -->
    <foundry:doc section="devel">
        <foundry:doc-desc>
            The language to use as negotiated by CCM.
        </foundry:doc-desc>
    </foundry:doc>
    <xsl:param name="negotiated-language"/>
    
    <!-- Temporary workaround until https://redmine.libreccm.org/issues/2186 is decided -->
    <xsl:variable name="lang">
        <xsl:value-of select="negoitated-language"/>
    </xsl:variable>
    
    <!--<foundry:doc section="devel">
        <foundry:doc-desc>
            The languages supported by this theme. Set in the <code>global.xml</code> configuration 
            file.
        </foundry:doc-desc>
    </foundry:doc>
    <xsl:variable name="languages">
        
    </xsl:variable>-->
    
    <foundry:doc section="devel">
        <foundry:doc-desc>
            The languages supported by this theme. They are configured in 
            <code>conf/global.xml</code> using the <code>&lt;supported-languages&gt;</code>
            element. Example for german and english:
            <pre>
                &lt;?xml version="1.0"?&gt;
                &lt;foundry:configuration&gt;
                    &hellip;
                    &lt;supported-languages default="de"&gt;
                        &lt;language locale=de"&gt;
                        &lt;language locale=en"&gt;
                    &lt;/supported-languages&gt;
                    &hellip;
                &lt;/foundry:configuration&gt;
            </pre>
        </foundry:doc-desc>
    </foundry:doc>
    <xsl:variable name="supported-languages"
                  select="document(concat($theme-prefix, '/conf/global.xml'))/foundry:configuration/supported-languages"/>
    
    <foundry:doc section="devel">
        <foundry:doc-desc>
            The language to use by theming engine for static texts etc. The language is determined
            as follows:
            <ul>
                <li>If the negotiated language is also in the <code>supported-languages</code></li>
                <li>If not the language which set by the default attribute of the 
                    <code>&lt;supported-languages&gt;</code> is used, but only if this language
                    is in the supported languages.</li>
                <li>Otherwise the first of the supported languages is used.</li>
            </ul>
        </foundry:doc-desc>
    </foundry:doc>
    <xsl:variable name="language">
        <xsl:choose>
            <xsl:when test="$supported-languages/language[@locale=$negotiated-language]">
                <xsl:value-of select="$negotiated-language"/>
            </xsl:when>
            <xsl:when test="not($supported-languages/language[@locale=$negotiated-language]) and $supported-languages/language[$supported-languages/@default]">
                <xsl:value-of select="$supported-languages/language[$supported-languages/@default]"/>
            </xsl:when>
            <xsl:otherwise>
                <xsl:value-of select="$supported-languages/language[1]/@locale"/>
            </xsl:otherwise>
        </xsl:choose>
        
    </xsl:variable>
    
    <!-- **************************************************************************** -->
    
    <!-- 
        Variables describing the user agent.
        ToDo: Check if we still need them.
    -->
    <foundry:doc section="devel">
        <foundry:doc-desc>
            The name of the user agent (browser) which is used to access CCM.
        </foundry:doc-desc>
    </foundry:doc>
    <xsl:param name="user-agent"/>
    
    <xsl:variable name="mozilla-version">
        <xsl:value-of select="substring(substring-after($user-agent, 'Mozilla/'), 1, 1)"/>
    </xsl:variable>
    
    <!-- Firefox -->
    <xsl:variable name="firefox-version">
        <xsl:value-of select="substring(substring-after($user-agent, 'Firefox/'), 1, 1)"/>
    </xsl:variable>
  
    <!-- Konqueror -->
    <xsl:variable name="konqueror-version">
        <xsl:value-of select="substring(substring-after($user-agent, 'Konqueror/'), 1, 1)"/>
    </xsl:variable>
  
    <!-- Opera -->
    <xsl:variable name="opera-version1">
        <xsl:value-of select="substring(substring-after($user-agent, 'Opera/'), 1, 1)"/>
    </xsl:variable>
  
    <xsl:variable name="opera-version2">
        <xsl:value-of select="substring(substring-after($user-agent, 'Opera '), 1, 1)"/>
    </xsl:variable>
  
    <!-- MSIE -->
    <xsl:variable name="msie_version">
        <xsl:value-of select="substring(substring-after($user-agent, 'MSIE '), 1, 1)"/>
    </xsl:variable>
  
    <!-- AppleWebKit -->
    <xsl:variable name="webkit_version">
        <xsl:value-of select="substring(substring-after($user-agent, 'AppleWebKit/'), 1, 3)"/>
    </xsl:variable>
    
</xsl:stylesheet>