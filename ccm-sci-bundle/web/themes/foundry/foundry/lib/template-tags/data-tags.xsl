<?xml version="1.0" encoding="UTF-8"?>
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
<!-- This file contains several template tags which output data from the result tree -->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:foundry="http://foundry.libreccm.org"
                exclude-result-prefixes="xsl foundry"
                version="2.0">

    <foundry:doc-file>
        <foundry:doc-file-title>Data tags</foundry:doc-file-title>
        <foundry:doc-file-desc>
            <p>
                These tags can be used to display several informations from the XML provided by CCM.
            </p>
        </foundry:doc-file-desc>
    </foundry:doc-file>
    
    <foundry:doc section="user"
                 type="template-tag">
        <foundry:doc-desc>
            Outputs the title of the current page. For a content item, this is 
            the title of the content item. For more details please refer to 
            the documentation of the <code>foundry:title</code> function.
        </foundry:doc-desc>
        <foundry:doc-see-also>
            <foundry:doc-link href="#foundry:title">
                <code>foundry:title</code>
            </foundry:doc-link>
        </foundry:doc-see-also>
    </foundry:doc>
    <xsl:template match="show-page-title">
        <xsl:value-of select="foundry:title()"/>
    </xsl:template>
    
    <foundry:doc section="user"
                 type="template-tag">
        <foundry:doc-desc>
            Outputs a static text which is retrieved from the 
            <code>static-texts/global.xml</code> file in the layout file. The key is the content
            of the element. If at least one of the attributes <code>id</code>, <code>class</code>
            or <code>with-colorset</code> is present at the attribute, the text is wrapped in a
            <code>span</code> element.
        </foundry:doc-desc>
        <foundry:doc-attributes>
            <foundry:doc-attribute name="id">
                An unique id for the text. 
            </foundry:doc-attribute>
            <foundry:doc-attribute name="class">
                One or more classes to format the text per CSS.
            </foundry:doc-attribute>
            <foundry:doc-attribute name="with-colorset">
                Add the classes for using the Colorset feature to the <code>span</code> element 
                the text is wrapped in.
            </foundry:doc-attribute>
        </foundry:doc-attributes>
    </foundry:doc>
    <xsl:template match="show-text">
        <xsl:choose>
            <xsl:when test="@id != '' or @class != '' or with-colorset = 'true'">
                <span>
                    <xsl:call-template name="foundry:set-id-and-class"/>
                    <xsl:value-of select="foundry:get-static-text('', .)"/>
                </span>
            </xsl:when>
            <xsl:otherwise>
                <xsl:value-of select="foundry:get-static-text('', .)"/>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>
    
</xsl:stylesheet>
