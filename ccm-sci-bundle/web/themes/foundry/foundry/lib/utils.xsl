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
                xmlns:xs="http://www.w3.org/2001/XMLSchema"
                xmlns:bebop="http://www.arsdigita.com/bebop/1.0"
                xmlns:cms="http://www.arsdigita.com/cms/1.0"
                xmlns:foundry="http://foundry.libreccm.org"
                xmlns:nav="http://ccm.redhat.com/navigation"
                version="1.0">
                
    
    <foundry:doc section="devel">
        <foundry:doc-param name="value" mandatory="yes">
            The value to evaluate.
        </foundry:doc-param>
        <foundry:doc-desc>
            <p>
                A helper function for evaluating certain string values to boolean. This function has two
                two purposes. First it simplifies some expressions. for example if you have a
                template tag with a attribute containing a (pseudo) boolean value (attribute values
                are always treated as strings) you would have to write something like:
            </p>
            <pre>
                ...
                <xsl:if test="./@attr = 'true'">
                    ...
                </xsl:if>
                ...
            </pre>
            <p>
                Using <code>foundry:boolean</code> this can be simplified to 
            </p>
            <pre>
                ...
                <xsl:if test="foundry:boolean(./@attr)">
                    ...
                </xsl:if>
                ...
            </pre>
            <p>
                The more important purpose is to make the usage of boolean values more user 
                friendly, especially in the templates. Using <code>foundry:boolean</code> no only
                <code>true</code> is evaluated to boolean <code>true</code>. A number of other 
                strings is also evaluated to <code>true</code>:
            </p>
            <ul>
                <li>
                    <code>true</code>
                </li>
                <li>
                    <code>TRUE</code>
                </li>
                <li>
                    <code>yes</code>
                </li>
                <li>
                    <code>YES</code>
                </li>
                <li>
                    <code>t</code>
                </li>
                <li>
                    <code>T</code>
                </li>
                <li>
                    <code>y</code>
                </li>
                <li>
                    <code>Y</code>
                </li>
            </ul>
            <p>
                All other values are evaluated to <code>false</code>.
            </p>
        </foundry:doc-desc>
    </foundry:doc>
    <xsl:function name="foundry:boolean" as="xs:boolean">
        <xsl:param name="value" as="xs:string"/>
        <xsl:choose>
            <xsl:when test="$value = 'true'
                            or $value = 'TRUE'
                            or $value = 'yes'
                            or $value = 'YES'
                            or $value = 't'
                            or $value = 'T'
                            or $value = 'y'
                            or $value = 'Y'">
                <xsl:sequence select="true()"/>
            </xsl:when>
            <xsl:otherwise>
                <xsl:sequence select="false()"/>
            </xsl:otherwise>
        </xsl:choose>

    </xsl:function>
    
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
    <xsl:function name="foundry:message" as="xs:string">
        <xsl:param name="level" as="xs:string"/>
        <xsl:param name="message" as="xs:string"/>
        
        <xsl:sequence select="concat('[Foundry ', $level, '] ', $message)"/>
    </xsl:function>
    
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
    <xsl:function name="foundry:message-info" as="xs:string">
        <xsl:param name="message" as="xs:string"/>
        
        <xsl:sequence select="foundry:message('INFO', $message)"/>
    </xsl:function>
    
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
    <xsl:function name="foundry:message-warn" as="xs:string">
        <xsl:param name="message" as="xs:string"/>
        
        <xsl:sequence select="foundry:message('WARNING', $message)"/>
    </xsl:function>
    
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
    <xsl:function name="foundry:message-error" as="xs:string">
        <xsl:param name="message" as="xs:string"/>
       
        <xsl:sequence select="foundry:message('ERROR', $message)"/>
    </xsl:function>
    
    <foundry:doc section="devel">
        <foundry:doc-param name="node">
            The node from which the value of the attribute is read.
        </foundry:doc-param>
        <foundry:doc-param name="attribute-name">
            The attribute to check for.
        </foundry:doc-param>
        <foundry:doc-param name="default-value">
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
                select="foundry:get-attribute-value(current(), 'width', '640')" /&gt;
                    &lt;xsl:variable name="height" 
                select="foundry:get-attribute-value(current(), 'height', '480')" /&gt;
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
    <xsl:function name="foundry:get-attribute-value">
        <xsl:param name="node"/>
        <xsl:param name="attribute-name"/>
        <xsl:param name="default-value"/>
        
        <xsl:choose>
            <xsl:when test="$node/@*[name() = $attribute-name]">
                <xsl:sequence select="$node/@*[name() = $attribute-name]"/>
            </xsl:when>
            <xsl:otherwise>
                <xsl:sequence select="$default-value"/>
            </xsl:otherwise>
        </xsl:choose>
        
    </xsl:function>
    
    <xsl:function name="foundry:get-setting" as="xs:string">
        <xsl:param name="module" as="xs:string"/>
        <xsl:param name="setting" as="xs:string"/>
        
        <xsl:sequence select="foundry:get-setting($module, $setting, '', '')"/>
    </xsl:function>
    
    <xsl:function name="foundry:get-setting" as="xs:string">
        <xsl:param name="module" as="xs:string"/>
        <xsl:param name="setting" as="xs:string"/>
        <xsl:param name="default" as="xs:string"/>
        
        <xsl:sequence select="foundry:get-setting($module, $setting, $default, '')"/>
    </xsl:function>
    
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
            This function retrieves the value of a setting from the theme configuration. For
            more informations about the configuration system of Foundry please refer to the 
            <em>configuration</em> section of the Foundry documentation.
        </foundry:doc-desc>
    </foundry:doc>
    <xsl:function name="foundry:get-setting" as="xs:string">
        <xsl:param name="module" as="xs:string"/>
        <xsl:param name="setting" as="xs:string"/>
        <xsl:param name="default" as="xs:string"/>
        <xsl:param name="node"/>
        
        <xsl:choose>
            <xsl:when test="$node and $node != ''">
                <xsl:sequence select="$node"/>
            </xsl:when>
            <xsl:when test="$module = ''">
                <xsl:sequence select="document(concat($theme-prefix, '/conf/global.xml'))/foundry:configuration/setting[@id=$setting]"/>
            </xsl:when>
            <xsl:when test="not($module = '') and document(concat($theme-prefix, '/conf/', $module, '.xml'))/foundry:configuration/setting[@id=$setting]">
                <xsl:sequence select="document(concat($theme-prefix, '/conf/', $module, '.xml'))/foundry:configuration/setting[@id=$setting]"/>
            </xsl:when>
            <xsl:otherwise>
                <xsl:message>
                    <xsl:choose>
                        <xsl:when test="$module=''">
                            <xsl:sequence select="foundry:message-warn(concat('Setting &quot;', $setting, '&quot; not found in global.xml'))"/>
                        </xsl:when>
                        <xsl:otherwise>
                            <xsl:sequence select="foundry:message-warn(concat('Setting &quot;', $setting, '&quot; not found in', $module, '.xml'))"/>
                        </xsl:otherwise>
                    </xsl:choose>
                </xsl:message>
                
                <xsl:sequence select="$default"/>
            </xsl:otherwise>
        </xsl:choose>
        
    </xsl:function>
    
    <xsl:function name="foundry:get-static-text" as="xs:string">
        <xsl:param name="module" as="xs:string"/>
        <xsl:param name="id" as="xs:string"/>
        
        <xsl:sequence select="foundry:get-static-text($module, $id, true(), $lang)"/>
    </xsl:function>
    
    <xsl:function name="foundry:get-static-text" as="xs:string">
        <xsl:param name="module" as="xs:string"/>
        <xsl:param name="id" as="xs:string"/>
        <xsl:param name="html" as="xs:boolean"/>
        
        <xsl:sequence select="foundry:get-static-text($module, $id, $html, $lang)"/>
    </xsl:function>
    
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
    <xsl:function name="foundry:get-static-text" as="xs:string">
        <xsl:param name="module" as="xs:string"/>
        <xsl:param name="id" as="xs:string"/>
        <xsl:param name="html" as="xs:boolean"/>
        <xsl:param name="lang" as="xs:string"/>
        
        <xsl:choose>
            <xsl:when test="$module = '' and document(concat($theme-prefix, '/texts/global.xml'))/foundry:static-texts/text[@id=$id]/translation[@lang = $lang]">
                <xsl:sequence select="document(concat($theme-prefix, '/texts/global.xml'))/foundry:static-texts/text[@id=$id]"/>
            </xsl:when>
            <xsl:when test="not($module = '') and document(concat($theme-prefix, '/texts/', $module, '.xml'))/foundry:static-texts/text[@id=$id]/translation[@lang = $lang]">
                <xsl:sequence select="document(concat($theme-prefix, '/texts/', $module, '.xml'))/foundry:static-texts/text[@id=$id]/translation[@lang = $lang]"/>
            </xsl:when>
            <xsl:otherwise>
                <xsl:choose>
                    <xsl:when test="foundry:debug-enabled()">
                        <xsl:choose>
                            <xsl:when test="$html">
                                <span class="foundry-debug-missing-translation">
                                    <span class="foundry-placeholder">
                                        <xsl:sequence select="$id"/>
                                    </span>
                                    <span class="foundry-missing-translation-path">
                                        <xsl:choose>
                                            <xsl:when test="$module = ''">
                                                <xsl:sequence select="document(concat($theme-prefix, '/texts/global.xml'))/foundry:static-texts/text[@id=$id]/translation[@lang=$lang]"/>
                                            </xsl:when>
                                            <xsl:otherwise>
                                                <xsl:sequence select="document(concat($theme-prefix, '/texts/', $module, '.xml'))/foundry:static-texts/text[@id=$id]/translation[@lang=$lang]"/>
                                            </xsl:otherwise>
                                        </xsl:choose>
                                    </span>
                                </span>
                            </xsl:when>
                            <xsl:otherwise>
                                <xsl:sequence select="$id"/>
                            </xsl:otherwise>
                        </xsl:choose>
                    </xsl:when>
                </xsl:choose>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:function>
    
    <foundry:doc section="devel">
        <foundry:doc-result>
            <code>true</code> if the debug mode if active, <code>false</code> otherwise.
        </foundry:doc-result>
        <foundry:doc-desc>
            A helper function to determine if the debug mode should be enabled. The debug mode
            of foundry is automatically enabled if the theme is viewed as development theme.
        </foundry:doc-desc>
    </foundry:doc>
    <xsl:function name="foundry:debug-enabled" as="xs:boolean">
        <xsl:choose>
            <xsl:when test="contains($theme-prefix, 'devel-themedir')">
                <xsl:sequence select="true()"/>
            </xsl:when>
            <xsl:otherwise>
                <xsl:sequence select="false()"/>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:function>
    
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
                    <xsl:sequence select="."/>
                </xsl:attribute>
            </xsl:if>
        </xsl:for-each>
        <xsl:if test="name() = 'bebop:formWidget' and (not(@id) and @name)">
            <xsl:attribute name="id">
                <xsl:sequence select="@name"/>
            </xsl:attribute>
        </xsl:if>
    </xsl:template>
    
    <xsl:function name="foundry:shying" as="xs:string">
        <xsl:param name="text" as="xs:string"/>
        
        <xsl:message>
            <xsl:sequence select="concat('foundry:shying called with ', $text)"/>
        </xsl:message>
        <xsl:message>
            <xsl:sequence select="concat('Result: ', translate($text, '\-', '&shy;'))"/>
        </xsl:message>
        
        <xsl:sequence select="translate($text, '\-', '&shy;')"/>
    </xsl:function>
    
    
    <xsl:function name="foundry:title" as="xs:string">
        <xsl:choose>
            <!-- Use fixed title for some special content items -->
            <xsl:when test="$data-tree//cms:contentPanel">
                <xsl:choose>
                    <!-- Glossary -->
                    <xsl:when test="$data-tree//cms:contentPanel/cms:item/type/label = 'Glossary Item'">
                        <xsl:sequence select="foundry:get-static-text('layout/page/title/glossary')"/>
                    </xsl:when>
                    <!-- FAQ -->
                    <xsl:when test="$data-tree//cms:contentPanel/cms:item/type/label = 'FAQ Item'">
                        <xsl:sequence select="foundry:get-static-text('layout/page/title/faq')"/>
                    </xsl:when>
                    <!-- Else use title of CI -->
                    <xsl:otherwise>
                        <xsl:sequence select="foundry:shying($data-tree//cms:contentPanel/cms:item/title)"/>
                    </xsl:otherwise>
                </xsl:choose>
            </xsl:when>
            <!-- Localized title for A-Z list -->
            <xsl:when test="$data-tree/bebop:title = 'AtoZ'">
                <xsl:sequence select="foundry:get-static-text('layout/page/title/atoz')"/>
            </xsl:when>
            <!-- Localized title for search -->
            <xsl:when test="$data-tree/bebop:title = 'Search'">
                <xsl:sequence select="foundry:get-static-text('layout/page/title/search')"/>
            </xsl:when>
            <!-- Localized title for log in -->
            <xsl:when test="$data-tree/@application = 'login'">
                <xsl:sequence select="foundry:get-static-text('layout/page/title/login')"/>
            </xsl:when>
            <!-- Localited title for sitemap -->
            <xsl:when test="$data-tree/@id = 'sitemapPage'">
                <xsl:sequence select="foundry:get-static-text('layout/page/title/sitemap')"/>
            </xsl:when>
            <!-- Title for content section-->
            <xsl:otherwise>
                <xsl:for-each select="$data-tree/nav:categoryMenu//nav:category[@isSelected='true']">
                    <xsl:choose>
                        <!-- Special rule: Use content item title für root-page in navigation -->
                        <xsl:when test="position() = last() and position() = 1">
                            <xsl:sequence select="foundry:shying(/bebop:page//title)"/>
                        </xsl:when>
                        <!-- Else use the name of the category -->
                        <xsl:when test="position() = last()">
                            <xsl:sequence select="foundry:shying(./@title)"/>
                        </xsl:when>
                    </xsl:choose>
                </xsl:for-each>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:function>
    
</xsl:stylesheet>