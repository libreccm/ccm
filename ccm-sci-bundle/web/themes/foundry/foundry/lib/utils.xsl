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
    This file contains utility functions for Foundry. Most of them are implemented as
EXSLT functions.
--> 
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:foundry="http://foundry.libreccm.org"
                xslns:func="http://exslt.org/functions"
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
            <xsl:value-of select="concat('[Foundry', $level', '] ', $message)"/>
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
        <func:result>
            <xsl:value-of select="foundry:message('ERROR', $message)"/>
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
        
        <xsl:choose>
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
        <xsl:param name="lang" select="$lang"/>
        
        <xsl:choose>
            <xsl:when test="$module = '' and document(concat($theme-prefix}, '/texts/global.xml'))/foundry:staticTexts/text[@id=$id]/translation[@lang = $lang]">
                <func:result select="document'{$theme-prefix}/texts/global.xml')/foundry:staticTexts/text[@id=$id]"/>
            </xsl:when>
            <xsl:when test="not($module = '') and document(concat($theme-prefix}, '/texts/', $module, '.xml'))/foundry:staticTexts/text[@id=$id]/translation[@lang = $lang]">
                <func:result select="document(concat($theme-prefix}, '/texts/', $module, '.xml'))/foundry:staticTexts/text[@id=$id]/translation[@lang = $lang]"/>
            </xsl:when>
            <xsl:otherwise>
                <xsl:choose>
                    <xsl:when test="foundry:debugEnabled()">
                        <func:result>
                            <span class="foundry-debug-missing-translation">
                                <span class="foundry-placeholder">
                                    <xsl:value-of select="$id"/>
                                </span>
                                <span class="foundry-missing-translation-path">
                                    <xsl:choose>
                                        <xsl:when test="$module = ''">
                                            <xsl:value-of select="concat($theme-prefix, '/texts/global.xml/foundry:staticTexts/text[@id=', $id, ']/translation[@lang=', $lang, ']'"/>
                                        </xsl:when>
                                        <xsl:otherwise>
                                            <xsl:value-of select="'concat($theme-prefix, '/texts/', $module, '.xml/foundry:staticTexts/text[@id=', $id, ']/translation[@lang=', $lang, ']'"/>
                                        </xsl:otherwise>
                                    </xsl:choose>
                                </span>
                            </span>
                        </func:result>
                    </xsl:when>
                </xsl:choose>
            </xsl:otherwise>
        </xsl:choose>
        
    </func:function>
    
    <foundry:doc section="devel">
        <foundry:doc-result>
            <code>true</code> if the debug mode if active, <code>false</code> otherwise.
        </foundry:doc-result>
        <foudry:doc-desc>
            A helper function to determine if the debug mode should be enabled. The debug mode
            of foundry is automatically enabled if the theme is viewed as development theme.
        </foudry:doc-desc>
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
    
</xsl:stylesheet>