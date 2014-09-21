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

<!--
    This file contains utility functions and templates for Foundry. Most of them are implemented as
EXSLT functions.
--> 
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:bebop="http://www.arsdigita.com/bebop/1.0"
                xmlns:cms="http://www.arsdigita.com/cms/1.0"
                xmlns:foundry="http://foundry.libreccm.org"
                xmlns:func="http://exslt.org/functions"
                xmlns:nav="http://ccm.redhat.com/navigation"
                version="1.0">
                
    
    <foundry:doc section="devel">
        <foundry:doc-param name="level"
                           mandatory="yes">
            The level of the message, indicating its severity 
        </foundry:doc-param>
        <foundry:doc-param name="message"
                           mandatory="yes">
            The message text.
        </foundry:doc-param>
        <foundry:doc-desc>
            <p>
                A helper function used by the other message functions like 
                <code>foundry:message-warn</code>. Concats the message level with the message. 
            </p>
            <p>
                This function should not be used directly. Use the other message functions instead.
            </p>
        </foundry:doc-desc>
        <foundry:doc-see-also>
            #foundry-message-info
        </foundry:doc-see-also>
        <foundry:doc-see-also>
            #foundry-message-warn
        </foundry:doc-see-also>
        <foundry:doc-see-also>
            #foundry-message-error
        </foundry:doc-see-also>
    </foundry:doc>
    <func:function name="foundry:message">
        <xsl:param name="level"/>
        <xsl:param name="message"/>
        
        <func:result>
            <xsl:value-of select="concat('[Foundry ', $level, '] ', $message)"/>
        </func:result>
    </func:function>
    
    <foundry:doc section="devel">
        <foundry:doc-param name="message"
                           mandatory="yes">
            The message text.
        </foundry:doc-param>
        <foundry:doc-desc>
            <p>
                Helper function to generate an info message. This function be used together with
                <code>&lt;xsl:message&gt;</code> to output a message in the CCM log. Example:
            </p>
            <pre>
                ...
                &lt;xsl:message&gt;
                    &lt;xsl:message select="foundry:message-info('Hello from Foundry')" /&gt;
                &lt;/xsl:message&gt;
            </pre>
        </foundry:doc-desc>
        <foundry:doc-see-also>
            #foundry-message-warn
        </foundry:doc-see-also>
        <foundry:doc-see-also>
            #foundry-message-error
        </foundry:doc-see-also>
    </foundry:doc>
    <func:function name="foundry:message-info">
        <xsl:param name="message"/>
        
        <func:result>
            <xsl:value-of select="foundry:message('INFO', $message)"/>
        </func:result>
    </func:function>
    
    <foundry:doc section="devel">
        <foundry:doc-param name="message"
                           mandatory="yes">
            The message text.
        </foundry:doc-param>
        <foundry:doc-desc>
            <p>
                Helper function to generate an info message. This function be used together with
                <code>&lt;xsl:message&gt;</code> to output a message in the CCM log warning
                the administrator about some things in the theme, for example a missing 
                configuration file. Example:
            </p>
            <pre>
                ...
                &lt;xsl:message&gt;
                    &lt;xsl:message select="foundry:message-info('Something is strange...')" /&gt;
                &lt;/xsl:message&gt;
            </pre>
        </foundry:doc-desc>
        <foundry:doc-see-also>
            #foundry-message-info
        </foundry:doc-see-also>
        <foundry:doc-see-also>
            #foundry-message-error
        </foundry:doc-see-also>
    </foundry:doc>
    <func:function name="foundry:message-warn">
        <xsl:param name="message"/>
        
        <func:result>
            <xsl:value-of select="foundry:message('WARNING', $message)"/>
        </func:result>
    </func:function>
    
    <foundry:doc section="devel">
        <foundry:doc-param name="message"
                           mandatory="yes">
            The message text.
        </foundry:doc-param>
        <foundry:doc-desc>
            <p>
                Helper function to generate an info message. This function be used together with
                <code>&lt;xsl:message&gt;</code> to output a message in the CCM log when 
                something goes wrong in the theme, for example when a layout file has a wrong 
                structure. Example:
            </p>
            <pre>
                ...
                &lt;xsl:message&gt;
                    &lt;xsl:message select="foundry:message-info('Some error has occurred...')" /&gt;
                &lt;/xsl:message&gt;
            </pre>
        </foundry:doc-desc>
        <foundry:doc-see-also>
            #foundry-message-info
        </foundry:doc-see-also>
        <foundry:doc-see-also>
            #foundry-message-warn
        </foundry:doc-see-also>
    </foundry:doc>
    <func:function name="foundry:message-error">
        <xsl:param name="message"/>
        <func:result>
            <xsl:value-of select="foundry:message('ERROR', $message)"/>
        </func:result>
    </func:function>
    
    <foundry:doc section="devel">
        <foundry:doc-param name="attribute-name"
                           mandatory="yes">
            The attribute to check for.
        </foundry:doc-param>
        <foundry:doc-param name="default-value"
                           mandatory="yes">
            The default value if the attribute is not set.
        </foundry:doc-param>
        <foundry:doc-result>
            The value of the attribute if it is set on the current element, the 
            <code>default-value</code> otherwise.
        </foundry:doc-result>
        <foundry:doc-desc>
            <p>
                A helper function for retrieving an attribute value from an element. If the attribute is
                set on the current element the value of the attribute is used as result. If the
                attribute is not set the <code>default-value</code> is used. This method is used
                by several layout tags with optional attributes. A common use pattern looks like this:
            </p>
            <pre>
                &lt;xsl:template match="example"&gt;
                    &lt;xsl:variable name="width" 
                select="foundry:get-attribute-value('width', '640')" /&gt;
                    &lt;xsl:variable name="height" 
                select="foundry:get-attribute-value('height', '480')" /&gt;
                /&lt;xsl:template&gt;
            </pre>
            <p>
                In this example, the element <code>example</code> has two optional attributes:
                <code>with</code> and <code>height</code>. If the attribute is set in processed XML,
                the value set there is used. Otherwise the default value (<code>640</code> 
                respectively <code>480</code>) is used. Without this function a code block like the
                one in the <code>xsl:choose</code> block of this function would be necessary for
                each of the variables.
            </p>
        </foundry:doc-desc>
    </foundry:doc>
    <func:function name="foundry:get-attribute-value">
        <xsl:param name="attribute-name"/>
        <xsl:param name="default-value"/>
        
        <func:result>
            <xsl:choose>
                <xsl:when test="./@*[name() = $attribute-name]">
                    <xsl:value-of select="./@*[name() = $attribute-name]"/>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:value-of select="$default-value"/>
                </xsl:otherwise>
            </xsl:choose>
        </func:result>
    </func:function>
    
    <foundry:doc section="devel">
        <foundry:doc-param name="module"
                           mandatory="yes">
            <p>
                The module of the settings. At the moment this corresponds to the name of the file
                in the <code>conf</code> directory. The empty string as value corresponds to the
                <code>global.xml</code> file.
            </p>
        </foundry:doc-param>
        <foundry:doc-param name="setting"
                           mandatory="yes">
            <p>
                The name of the setting to retrieve.
            </p>
        </foundry:doc-param>
        <foundry:doc-param name="default"
                           mandatory="no">
            <p>
                The value to use if there is no entry for the setting in the settings file.
            </p>
        </foundry:doc-param>
        <foundry:doc-param name="node"
                           mandatory="no">
            <p>
                A node from the layout template which overrides the value from the configuration.
            </p>
        </foundry:doc-param>
        <foundry:doc-result>
            The value of the requested setting or if no value has been set the provided default 
            value. If no default value has been provided the result is an empty string.
        </foundry:doc-result>
        <foundry:doc-desc>
            This EXSLT function retrieves the value of a setting from the theme configuration. For
            more informations about the configuration system of Foundry please refer to the 
            <em>configuration</em> section of the Foundry documentation.
        </foundry:doc-desc>
    </foundry:doc>
    <func:function name="foundry:get-setting">
        <xsl:param name="module"/>
        <xsl:param name="setting"/>
        <xsl:param name="default" select="''"/>
        <xsl:param name="node"/>
        
        <xsl:choose>
            <xsl:when test="$node and $node != ''">
                <func:result select="$node"/>
            </xsl:when>
            <xsl:when test="$module = ''">
                <func:result select="document(concat($theme-prefix, '/conf/global.xml'))/foundry:configuration/setting[@id=$setting]"/>
            </xsl:when>
            <xsl:when test="not($module = '') and document(concat($theme-prefix, '/conf/', $module, '.xml'))/foundry:configuration/setting[@id=$setting]">
                <func:result select="document(concat($theme-prefix, '/conf/', $module, '.xml'))/foundry:configuration/setting[@id=$setting]"/>
            </xsl:when>
            <xsl:otherwise>
                <xsl:message>
                    <xsl:choose>
                        <xsl:when test="$module=''">
                            <xsl:value-of select="foundry:message-warn(concat('Setting &quot;', $setting, '&quot; not found in global.xml'))"/>
                        </xsl:when>
                        <xsl:otherwise>
                            <xsl:value-of select="foundry:message-warn(concat('Setting &quot;', $setting, '&quot; not found in', $module, '.xml'))"/>
                        </xsl:otherwise>
                    </xsl:choose>
                </xsl:message>
                
                <func:result select="$default"/>
            </xsl:otherwise>
        </xsl:choose>
        
    </func:function>
    
    <foundry:doc section="devel">
        <foundry:doc-param name="module"
                           mandatory="yes">
            <p>
                he module of the settings. At the moment this corresponds to the name of the file
                in the <code>texts</code> directory. The empty string as value corresponds to the
                <code>global.xml</code> file.
            </p>
        </foundry:doc-param>
        <foundry:doc-param name="id"
                           mandatory="yes">
            The name of the text to retrieve.
        </foundry:doc-param>
        <foundry:doc-param name="lang"
                           mandatory="no">
            <p>
                The language to retrieve. Normally there is no need to set this parameter because
                it is determined automatically.
            </p>
        </foundry:doc-param>
        <foundry:doc-result>
            The requested static text. If there is no value for the requested static text in the
            module provided by the module parameter the value depends if the debug mode is 
            enabled or not. If the debug mode is <em>not</em> not enabled the result is an empty 
            string. If the debug mode is enabled, a identifier of the text (the value of the 
            <code>id</code> parameter) is displayed. If you point the mouse pointer of the 
            placeholder, the complete path of the text is shown as hovering box.
        </foundry:doc-result>
        <foundry:doc-desc>
            Retrieves at static text. For more informations about static texts in Foundry please
            refer to the static texts section in the Foundry documentation.
        </foundry:doc-desc>
    </foundry:doc>
    <func:function name="foundry:get-static-text">
        <xsl:param name="module"/>
        <xsl:param name="id"/>
        <xsl:param name="html" select="'true'"/>
        <xsl:param name="lang" select="$lang"/>
        <func:result>
            <xsl:choose>
                <xsl:when test="$module = '' and document(concat($theme-prefix, '/texts/global.xml'))/foundry:static-texts/text[@id=$id]/translation[@lang = $lang]">
                    <xsl:value-of select="document(concat($theme-prefix, '/texts/global.xml'))/foundry:static-texts/text[@id=$id]"/>
                </xsl:when>
                <xsl:when test="not($module = '') and document(concat($theme-prefix, '/texts/', $module, '.xml'))/foundry:static-texts/text[@id=$id]/translation[@lang = $lang]">
                    <xsl:value-of select="document(concat($theme-prefix, '/texts/', $module, '.xml'))/foundry:static-texts/text[@id=$id]/translation[@lang = $lang]"/>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:choose>
                        <xsl:when test="foundry:debug-enabled()">
                            <xsl:choose>
                                <xsl:when test="$html = 'true'">
                                    <span class="foundry-debug-missing-translation">
                                        <span class="foundry-placeholder">
                                            <xsl:value-of select="$id"/>
                                        </span>
                                        <span class="foundry-missing-translation-path">
                                            <xsl:choose>
                                                <xsl:when test="$module = ''">
                                                    <xsl:value-of select="document(concat($theme-prefix, '/texts/global.xml'))/foundry:static-texts/text[@id=$id]/translation[@lang=$lang]"/>
                                                </xsl:when>
                                                <xsl:otherwise>
                                                    <xsl:value-of select="document(concat($theme-prefix, '/texts/', $module, '.xml'))/foundry:static-texts/text[@id=$id]/translation[@lang=$lang]"/>
                                                </xsl:otherwise>
                                            </xsl:choose>
                                        </span>
                                    </span>
                                </xsl:when>
                                <xsl:otherwise>
                                    <xsl:value-of select="$id"/>
                                </xsl:otherwise>
                            </xsl:choose>
                        </xsl:when>
                    </xsl:choose>
                </xsl:otherwise>
            </xsl:choose>
        </func:result>
    </func:function>
    
    <foundry:doc section="devel">
        <foundry:doc-result>
            <code>true</code> if the debug mode if active, <code>false</code> otherwise.
        </foundry:doc-result>
        <foundry:doc-desc>
            A helper function to determine if the debug mode should be enabled. The debug mode
            of foundry is automatically enabled if the theme is viewed as development theme.
        </foundry:doc-desc>
    </foundry:doc>
    <func:function name="foundry:debug-enabled">
        <xsl:choose>
            <xsl:when test="contains($theme-prefix, 'devel-themedir')">
                <func:result select="true()"/>
            </xsl:when>
            <xsl:otherwise>
                <func:result select="false()"/>
            </xsl:otherwise>
        </xsl:choose>
    </func:function>
    
    <foundry:doc section="devel">
        <foundry:doc-desc>
            Helper template for processing additional attributes. This are copied from the result 
            tree XML created by CCM to the HTML output generated by Foundry without any further
            processing.
        </foundry:doc-desc>
    </foundry:doc>
    <xsl:template name="foundry:process-attributes">
        <xsl:for-each select="@*">
            <xsl:if test="(name() != 'href_no_javascript')
                       and (name() != 'hint')
                       and (name() != 'label')">
                <xsl:attribute name="{name()}">
                    <xsl:value-of select="."/>
                </xsl:attribute>
            </xsl:if>
        </xsl:for-each>
        <xsl:if test="name() = 'bebop:formWidget' and (not(@id) and @name)">
            <xsl:attribute name="id">
                <xsl:value-of select="@name"/>
            </xsl:attribute>
        </xsl:if>
    </xsl:template>
    
    <func:function name="foundry:shying">
        <xsl:param name="text"/>
        <func:result select="translate($text, '\-', '&shy;')"/>
    </func:function>
    
    
    <func:function name="foundry:title">
        <func:result>
            <xsl:choose>
                <!-- Use fixed title for some special content items -->
                <xsl:when test="$data-tree//cms:contentPanel">
                    <xsl:choose>
                        <!-- Glossary -->
                        <xsl:when test="$data-tree//cms:contentPanel/cms:item/type/label = 'Glossary Item'">
                            <xsl:value-of select="foundry:get-static-text('layout/page/title/glossary')"/>
                        </xsl:when>
                        <!-- FAQ -->
                        <xsl:when test="$data-tree//cms:contentPanel/cms:item/type/label = 'FAQ Item'">
                            <xsl:value-of select="foundry:get-static-text('layout/page/title/faq')"/>
                        </xsl:when>
                        <!-- Else use title of CI -->
                        <xsl:otherwise>
                            <xsl:value-of select="foundry:shying($data-tree//cms:contentPanel/cms:item/title)"/>
                        </xsl:otherwise>
                    </xsl:choose>
                </xsl:when>
                <!-- Localized title for A-Z list -->
                <xsl:when test="$data-tree/bebop:title = 'AtoZ'">
                    <xsl:value-of select="foundry:get-static-text('layout/page/title/atoz')"/>
                </xsl:when>
                <!-- Localized title for search -->
                <xsl:when test="$data-tree/bebop:title = 'Search'">
                    <xsl:value-of select="foundry:get-static-text('layout/page/title/search')"/>
                </xsl:when>
                <!-- Localized title for log in -->
                <xsl:when test="$data-tree/@application = 'login'">
                    <xsl:value-of select="foundry:get-static-text('layout/page/title/login')"/>
                </xsl:when>
                <!-- Localited title for sitemap -->
                <xsl:when test="$data-tree/@id = 'sitemapPage'">
                    <xsl:value-of select="foundry:get-static-text('layout/page/title/sitemap')"/>
                </xsl:when>
                <!-- Title for content section-->
                <xsl:otherwise>
                    <xsl:for-each select="$data-tree/nav:categoryMenu//nav:category[@isSelected='true']">
                        <xsl:choose>
                            <!-- Special rule: Use content item title für root-page in navigation -->
                            <xsl:when test="position() = last() and position() = 1">
                                <xsl:value-of select="foundry:shying(/bebop:page//title)"/>
                            </xsl:when>
                            <!-- Else use the name of the category -->
                            <xsl:when test="position() = last()">
                                <xsl:value-of select="foundry:shying(./@title)"/>
                            </xsl:when>
                        </xsl:choose>
                    </xsl:for-each>
                </xsl:otherwise>
            </xsl:choose>
        </func:result>
    </func:function>
    
</xsl:stylesheet>