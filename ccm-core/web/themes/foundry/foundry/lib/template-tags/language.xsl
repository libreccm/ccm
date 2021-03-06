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

    <foundry:doc-file>
        <foundry:doc-file-title>Language selector</foundry:doc-file-title>
        <foundry:doc-file-desc>
            <p>
                The tags provided by this file can be used to create a language
                selector control which allow the visitor of a site to switch
                the language of the site manually. As usual, the tags itself
                to not generate much HTML. Instead they only extract several
                parameters from the data tree XML from CCM and pass it to their
                child tags. The HTML for the language selector is completly
                definied by the designer.
            </p>
            <p>
                An example for a language selector:
            </p>
            <pre>
                &lt;language-selector&gt;
                    &lt;ul&gt;
                        &lt;language&gt;
                            &lt;li&gt;
                                &lt;a&gt;
                                    &lt;span&gt;
                                        &lt;language-name&gt;
                                    &lt;/span&gt;
                                &lt;/a&gt;
                            &lt;/li&gt;
                        &lt;/language&gt;
                    &lt;/ul&gt;
                &lt;/language-selector&gt;
            </pre>
            <p>
                In the example above all available languages are put into a
                <code>&lt;ul&gt;</code>. The URL/value for the <code>href</code>
                of the <code>&lt;a&gt;</code> element in provided by the
                surrounding <code>&lt;language&gt;</code> tag, therefore the
                <code>&lt;a&gt;</code> element in the example has no
                <code>href</code> attribute. The name of the language is put
                into a <code>&lt;span&gt;</code> to make formatting easier.
            </p>
        </foundry:doc-file-desc>
    </foundry:doc-file>

    <foundry:doc section="user" type="template-tag">
        <foundry:doc-desc>
            <p>
                Root tag for a language selector control.
            </p>
        </foundry:doc-desc>
    </foundry:doc>
    <xsl:template match="language-selector">
        <xsl:if test="count($data-tree/cms:contentPanel/availableLanguages/language) != 1 and count($data-tree/nav:greetingItem/availableLanguages/language) != 1">
            <xsl:apply-templates/>
        </xsl:if>
    </xsl:template>

    <foundry:doc section="user" type="template-tag">
        <foundry:doc-desc>
            <p>
                Encloses the HTML for one specific language entry in a language
                selector.
            </p>
        </foundry:doc-desc>
    </foundry:doc>
    <xsl:template match="language-selector//language">
        <xsl:variable name="language-layout-tree" select="./*"/>

        <xsl:variable name="available-languages">
            <xsl:choose>
                <xsl:when test="$data-tree/cms:contentPanel">
                    <xsl:copy-of select="$data-tree/cms:contentPanel/availableLanguages/*" />
                </xsl:when>
                <xsl:when test="$data-tree/nav:greetingItem">
                    <xsl:copy-of select="$data-tree/nav:greetingItem/availableLanguages/*" />
                </xsl:when>
                <xsl:when test="$data-tree/ui:siteBanner/supportedLanguages">
                    <xsl:copy-of select="$data-tree/ui:siteBanner/supportedLanguages/*" />
                </xsl:when>
                <xsl:otherwise>
                    <xsl:copy-of select="document(foundry:gen-path('conf/global.xml'))/foundry:configuration/supported-languages" />
                </xsl:otherwise>
            </xsl:choose>
        </xsl:variable>

        <xsl:for-each select="$available-languages/language">
            <xsl:sort select="./@locale" />

            <xsl:apply-templates select="$language-layout-tree">
                <xsl:with-param name="class"
                                select="if (./@locale = $lang)
                                        then concat('language-selector-', ./@locale, ' selected')
                                        else concat('language-selector-', ./@locale)"/>
                <!--<xsl:with-param name="href" tunnel="yes" select="concat('?lang=', ./@locale)"/>-->
                <xsl:with-param name="href" tunnel="yes">
                    <xsl:choose>
                        <xsl:when test="substring($data-tree/@url, string-length($data-tree/@url) - 2, 1) = '.'">
                            <xsl:value-of select="concat(substring($data-tree/@url, 0, string-length($data-tree/@url) - 1), ./@locale)" />
                        </xsl:when>
                        <xsl:otherwise>
                            <xsl:value-of select="concat('?lang=', ./@locale)" />
                        </xsl:otherwise>
                    </xsl:choose>
                </xsl:with-param>
                <xsl:with-param name="selected-language"
                                tunnel="yes"
                                select="$lang" />
                <xsl:with-param name="current-language"
                                tunnel="yes"
                                select="./@locale" />
                <xsl:with-param name="language-name"
                                tunnel="yes"
                                select="foundry:get-static-text('', concat('language/', ./@locale))"/>
            </xsl:apply-templates>
        </xsl:for-each>

    </xsl:template>

    <foundry:doc section="user" type="template-tag">
        <foundry:doc-desc>
            <p>
                Outputs the name of the current language.
            </p>
        </foundry:doc-desc>
    </foundry:doc>
    <xsl:template match="language-selector//language//language-name">
        <xsl:param name="language-name" tunnel="yes"/>

        <xsl:value-of select="$language-name"/>
    </xsl:template>

    <xsl:template match="if-selected-language">
        <xsl:param name="current-language" 
                   tunnel="yes" />
        <xsl:param name="selected-language" 
                   tunnel="yes" />

       <xsl:if test="$current-language = $selected-language">
            <xsl:apply-templates />
        </xsl:if>
    </xsl:template>

    <xsl:template match="if-not-selected-language">
        <xsl:param name="current-language" 
                   tunnel="yes" />
        <xsl:param name="selected-language" 
                   tunnel="yes" />

        <xsl:if test="$current-language != $selected-language">
            <xsl:apply-templates />
        </xsl:if>
    </xsl:template>

</xsl:stylesheet>
