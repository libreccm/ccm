<?xml version="1.0" encoding="utf-8"?>
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

    <foundry:doc section="user" type="template-tag">
        <foundry:doc-desc>
            <p>
                Outputs the the date of a news. The <code>news-date</code> must contain at least 
                one <code>format</code> element. The <code>format</code> element encloses the
                format definition for the specific language or the default format. The language
                for which a format is used is provided using the <code>lang</code> attribute at the 
                <code>format</code> element. The default format has a <code>default</code> attribute
                with the value <code>true</code>. An example:
            </p>
            <pre>
                &lt;news-date&gt;
                    &lt;format default="true"&gt;
                        &lt;iso-date/&gt;
                    &lt;/format&gt;
                    &lt;format lang="de"&gt;
                        &lt;day zero="true"/&gt;.&lt;month zero="true"/&gt;.&lt;year/&gt;
                    &lt;/format&gt;
                &lt;/news-date&gt;
            </pre>
            <p>
                In this example a visitor with a browser using <em>German</em> as default locale 
                will see the news date in the date format that common in Germany 
                (<code>dd.mm.yyyy</code>). For all other languages, the default format is used.
                In this case the <code>iso-format</code> is used.
            </p>
        </foundry:doc-desc>
    </foundry:doc>
    <xsl:template match="/content-item-layout//news-date">
        <xsl:choose>
            <xsl:when test="./format[@lang = $language]">
                <xsl:apply-templates select="./format[@lang = $language]"/>
            </xsl:when>
            <xsl:otherwise>
                <xsl:apply-templates select="./format[@default = 'true']"/>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>
    
    <foundry:doc section="user" type="template-tag">
        <foundry:doc-desc>
            <p>
                Outputs the news date in the short format as provided in the data tree by CCM.
            </p>
        </foundry:doc-desc>
    </foundry:doc>
    <xsl:template match="/content-item-layout//news-date/format/format//short-date">
        <xsl:param name="contentitem-tree" tunnel="yes"/>
        
        <xsl:choose>
            <xsl:when test="$contentitem-tree/newsDate">
                <xsl:value-of select="$contentitem-tree/newsDate/@date"/>
            </xsl:when>
            <xsl:when test="$contentitem-tree/nav:attribute[@name = 'newsDate']">
                <xsl:value-of select="$contentitem-tree/nav:attribute[@name = 'newsDate']/@date"/>
            </xsl:when>
        </xsl:choose>
    </xsl:template>
    
    <foundry:doc section="user" type="template-tag">
        <foundry:doc-desc>
            <p>
                Outputs the news date in the long format as provided in the data tree by CCM.
            </p>
        </foundry:doc-desc>
    </foundry:doc>
    <xsl:template match="/content-item-layout//news-date/format//long-date">
        <xsl:param name="contentitem-tree" tunnel="yes"/>
        
        <xsl:choose>
            <xsl:when test="$contentitem-tree/newsDate">
                <xsl:value-of select="$contentitem-tree/newsDate/@longDate"/>
            </xsl:when>
            <xsl:when test="$contentitem-tree/nav:attribute[@name = 'newsDate']">
                <xsl:value-of select="$contentitem-tree/nav:attribute[@name = 'newsDate']/@longDate"/>
            </xsl:when>
        </xsl:choose>
        
    </xsl:template>
    
    <foundry:doc section="user" type="template-tag">
        <foundry:doc-desc>
            <p>
                A short hand template to output the news date in ISO8601 style (yyyy-mm-dd).
            </p>
        </foundry:doc-desc>
    </foundry:doc>
    <xsl:template match="/content-item-layout//news-date/format//iso-date">
        <xsl:param name="contentitem-tree" tunnel="yes"/>
        
        <xsl:variable name="year">
            <xsl:choose>
                <xsl:when test="$contentitem-tree/newsDate">
                    <xsl:value-of select="$contentitem-tree/newsDate/@year"/>
                </xsl:when>
                <xsl:when test="$contentitem-tree/nav:attribute[@name = 'newsDate']">
                    <xsl:value-of select="$contentitem-tree/nav:attribute[@name = 'newsDate']/@year"/>
                </xsl:when>
            </xsl:choose>
        </xsl:variable>
        
        <xsl:variable name="month">
            <xsl:choose>
                <xsl:when test="$contentitem-tree/newsDate">
                    <xsl:value-of select="$contentitem-tree/newsDate/@month"/>
                </xsl:when>
                <xsl:when test="$contentitem-tree/nav:attribute[@name = 'newsDate']">
                    <xsl:value-of select="$contentitem-tree/nav:attribute[@name = 'newsDate']/@month"/>
                </xsl:when>
            </xsl:choose>
        </xsl:variable>
        
        <xsl:variable name="day-value">
            <xsl:choose>
                <xsl:when test="$contentitem-tree/newsDate">
                    <xsl:value-of select="$contentitem-tree/newsDate/@day"/>
                </xsl:when>
                <xsl:when test="$contentitem-tree/nav:attribute[@name = 'newsDate']">
                    <xsl:value-of select="$contentitem-tree/nav:attribute[@name = 'newsDate']/@day"/>
                </xsl:when>
            </xsl:choose>
        </xsl:variable>
        
        <xsl:variable name="day">
            <xsl:choose>
                <xsl:when test="string-length($day-value) &lt; 2">
                    <xsl:value-of select="concat('0', $day-value)"/>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:value-of select="$day-value"/>
                </xsl:otherwise>
            </xsl:choose>
        </xsl:variable>
        
        <xsl:value-of select="concat($year, '-', $month, '-', $day)"/>
    </xsl:template>
    
    <foundry:doc section="user" type="template-tag">
        <foundry:doc-desc>
            <p>
                Outputs the year of a news date. If the <code>short</code> attribute is set to
                <code>true</code> only the last two columns are shown.
            </p>
        </foundry:doc-desc>
        <foundry:doc-attributes>
            <foundry:doc-attribute name="short">
                <p>
                    If set to <code>true</code> only the last two digits of the year are shown.
                </p>
            </foundry:doc-attribute>
        </foundry:doc-attributes>
    </foundry:doc>
    <xsl:template match="/content-item-layout//news-date/format//year">
        <xsl:param name="contentitem-tree" tunnel="yes"/>
        
        <xsl:variable name="year-value">
            <xsl:choose>
                <xsl:when test="$contentitem-tree/newsDate">
                    <xsl:value-of select="$contentitem-tree/newsDate/@year"/>
                </xsl:when>
                <xsl:when test="$contentitem-tree/nav:attribute[@name = 'newsDate']">
                    <xsl:value-of select="$contentitem-tree/nav:attribute[@name = 'newsDate']/@year"/>
                </xsl:when>
            </xsl:choose>
        </xsl:variable>
        
        <xsl:choose>
            <xsl:when test="./@short = 'true'">
                <xsl:value-of select="substring($year-value, 3)"/>
            </xsl:when>
            <xsl:otherwise>
                <xsl:value-of select="$year-value"/>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>
    
    <foundry:doc section="user" type="template-tag">
        <foundry:doc-desc>
            <p>
                Outputs the month part of the news date. If the value has only one digit it is 
                prefixed by a zero by default.
            </p>
        </foundry:doc-desc>
        <foundry:doc-attributes>
            <foundry:doc-attribute name="zero">
                <p>
                    If set to <code>false</code> one digit values (months from 1 to 9) are not 
                    prefixed with a zero.
                </p>
            </foundry:doc-attribute>
        </foundry:doc-attributes>
    </foundry:doc>
    <xsl:template match="/content-item-layout//news-date/format//month">
        <xsl:param name="contentitem-tree" tunnel="yes"/>
    
        <xsl:variable name="month-value">
            <xsl:choose>
                <xsl:when test="$contentitem-tree/newsDate">
                    <xsl:value-of select="$contentitem-tree/newsDate/@month"/>
                </xsl:when>
                <xsl:when test="$contentitem-tree/nav:attribute[@name = 'newsDate']">
                    <xsl:value-of select="$contentitem-tree/nav:attribute[@name = 'newsDate']/@month"/>
                </xsl:when>
            </xsl:choose>
        </xsl:variable>
        
        <xsl:choose>
            <xsl:when test="string-length($month-value) &lt; 2 and ./@zero = 'false'">
                <xsl:value-of select="$month-value"/>
            </xsl:when>
            <xsl:otherwise>
                <xsl:value-of select="concat('0', $month-value)"/>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>
    
    <foundry:doc section="user" type="template-tag">
        <foundry:doc-desc>
            <p>
                Outputs the month name as provided in the data tree by CCM.
            </p>
        </foundry:doc-desc>
    </foundry:doc>
    <xsl:template match="/content-item-layout//news-date/format//month-name">
        <xsl:param name="contentitem-tree" tunnel="yes"/>
    
        <xsl:choose>
            <xsl:when test="$contentitem-tree/newsDate">
                <xsl:value-of select="$contentitem-tree/newsDate/@monthName"/>
            </xsl:when>
            <xsl:when test="$contentitem-tree/nav:attribute[@name = 'newsDate']">
                <xsl:value-of select="$contentitem-tree/nav:attribute[@name = 'newsDate']/@monthName"/>
            </xsl:when>
        </xsl:choose>
    </xsl:template>
    
     <foundry:doc section="user" type="template-tag">
        <foundry:doc-desc>
            <p>
                Outputs the day part of the news date. If the value has only one digit it is 
                prefixed by a zero by default.
            </p>
        </foundry:doc-desc>
        <foundry:doc-attributes>
            <foundry:doc-attribute name="zero">
                <p>
                    If set to <code>false</code> one digit values (days from 1 to 9) are not 
                    prefixed with a zero.
                </p>
            </foundry:doc-attribute>
        </foundry:doc-attributes>
    </foundry:doc>
    <xsl:template match="/content-item-layout//news-date/format//day">
        <xsl:param name="contentitem-tree" tunnel="yes"/>
    
        <xsl:variable name="day-value">
            <xsl:choose>
                <xsl:when test="$contentitem-tree/newsDate">
                    <xsl:value-of select="$contentitem-tree/newsDate/@day"/>
                </xsl:when>
                <xsl:when test="$contentitem-tree/nav:attribute[@name = 'newsDate']">
                    <xsl:value-of select="$contentitem-tree/nav:attribute[@name = 'newsDate']/@day"/>
                </xsl:when>
            </xsl:choose>
        </xsl:variable>
        
        <xsl:choose>
            <xsl:when test="string-length($day-value) &lt; 2 and ./@zero = 'true'">
                <xsl:value-of select="concat('0', $day-value)"/>
            </xsl:when>
            <xsl:otherwise>
                <xsl:value-of select="$day-value"/>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>
</xsl:stylesheet>
    