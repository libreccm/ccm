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

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
                xmlns:bebop="http://www.arsdigita.com/bebop/1.0"
                xmlns:cms="http://www.arsdigita.com/cms/1.0"
                xmlns:nav="http://ccm.redhat.com/navigation"
                xmlns:mandalay="http://mandalay.quasiweb.de"
                exclude-result-prefixes="xsl bebop cms nav"
                version="1.0">

    <!-- DE Leadtext -->
    <!-- EN lead text view -->
    <xsl:template match="cms:item[objectType='com.arsdigita.cms.contenttypes.Event']" 
                  mode="lead">
        <xsl:variable name="setLeadText">
            <xsl:call-template name="mandalay:getSetting">
                <xsl:with-param name="module"  select="'Event'"/>
                <xsl:with-param name="setting" select="'setLeadText'"/>
                <xsl:with-param name="default" select="'true'"/>
            </xsl:call-template>
        </xsl:variable>

        <xsl:if test="./lead and $setLeadText = 'true'">
            <div class="lead">
                <xsl:value-of disable-output-escaping="yes" select="./lead"/>
            </div>
        </xsl:if>
    </xsl:template>

    <!-- DE Bild -->
    <!-- EN image -->
    <xsl:template match="cms:item[objectType='com.arsdigita.cms.contenttypes.Event']" 
                  mode="image">

        <!-- DE Hole alle benötigten Einstellungen-->
        <!-- EN Getting all needed setting-->
        <xsl:variable name="setImage">
            <xsl:call-template name="mandalay:getSetting">
                <xsl:with-param name="module"  select="'Event'"/>
                <xsl:with-param name="setting" select="'setImage'"/>
                <xsl:with-param name="default" select="'true'"/>
            </xsl:call-template>
        </xsl:variable>
        <xsl:variable name="setImageMaxHeight">
            <xsl:call-template name="mandalay:getSetting">
                <xsl:with-param name="module"  select="'Event'"/>
                <xsl:with-param name="setting" select="'setImageMaxHeight'"/>
                <xsl:with-param name="default" select="''"/>
            </xsl:call-template>
        </xsl:variable>
        <xsl:variable name="setImageMaxWidth">
            <xsl:call-template name="mandalay:getSetting">
                <xsl:with-param name="module"  select="'Event'"/>
                <xsl:with-param name="setting" select="'setImageMaxWidth'"/>
                <xsl:with-param name="default" select="''"/>
            </xsl:call-template>
        </xsl:variable>
        <xsl:variable name="setImageCaption">
            <xsl:call-template name="mandalay:getSetting">
                <xsl:with-param name="module"  select="'Event'"/>
                <xsl:with-param name="setting" select="'setImageCaption'"/>
                <xsl:with-param name="default" select="'true'"/>
            </xsl:call-template>
        </xsl:variable>

        <xsl:if test="$setImage = 'true'">
            <xsl:call-template name="mandalay:imageAttachment">
                <xsl:with-param name="showCaption" select="$setImageCaption"/>
                <xsl:with-param name="maxHeight" select="$setImageMaxHeight" />
                <xsl:with-param name="maxWidth" select="$setImageMaxWidth" />
            </xsl:call-template>
        </xsl:if>
    </xsl:template>

    <!-- DE Vollansicht -->
    <!-- EN Detailed view -->
    <xsl:template name="CT_Event_graphics" 
                  match="cms:item[objectType='com.arsdigita.cms.contenttypes.Event']" 
                  mode="detailed_view">

        <!-- DE Hole alle benötigten Einstellungen-->
        <!-- EN Getting all needed setting-->
        <xsl:variable name="dateSeparator">
            <xsl:call-template name="mandalay:getSetting">
                <xsl:with-param name="module"  select="'Event'"/>
                <xsl:with-param name="setting" select="'dateSeparator'"/>
                <xsl:with-param name="default" select="' :: '"/>
            </xsl:call-template>
        </xsl:variable>
        <xsl:variable name="timeSeparator">
            <xsl:call-template name="mandalay:getSetting">
                <xsl:with-param name="module"  select="'Event'"/>
                <xsl:with-param name="setting" select="'timeSeparator'"/>
                <xsl:with-param name="default" select="' - '"/>
            </xsl:call-template>
        </xsl:variable>
        <xsl:variable name="setDateFormat">
            <xsl:call-template name="mandalay:getSetting">
                <xsl:with-param name="module"  select="'Event'"/>
                <xsl:with-param name="setting" select="'setDateFormat'"/>
                <xsl:with-param name="default" select="'S'"/>
            </xsl:call-template>
        </xsl:variable>
        <xsl:variable name="setLocation">
            <xsl:call-template name="mandalay:getSetting">
                <xsl:with-param name="module"  select="'Event'"/>
                <xsl:with-param name="setting" select="'setLocation'"/>
                <xsl:with-param name="default" select="'true'"/>
            </xsl:call-template>
        </xsl:variable>
        <xsl:variable name="setDate">
            <xsl:call-template name="mandalay:getSetting">
                <xsl:with-param name="module"  select="'Event'"/>
                <xsl:with-param name="setting" select="'setDate'"/>
                <xsl:with-param name="default" select="'true'"/>
            </xsl:call-template>
        </xsl:variable>
        <xsl:variable name="setEventDate">
            <xsl:call-template name="mandalay:getSetting">
                <xsl:with-param name="module"  select="'Event'"/>
                <xsl:with-param name="setting" select="'setEventDate'"/>
                <xsl:with-param name="default" select="'true'"/>
            </xsl:call-template>
        </xsl:variable>
        <xsl:variable name="setEventType">
            <xsl:call-template name="mandalay:getSetting">
                <xsl:with-param name="module"  select="'Event'"/>
                <xsl:with-param name="setting" select="'setEventType'"/>
                <xsl:with-param name="default" select="'true'"/>
            </xsl:call-template>
        </xsl:variable>
        <xsl:variable name="setMainContributor">
            <xsl:call-template name="mandalay:getSetting">
                <xsl:with-param name="module"  select="'Event'"/>
                <xsl:with-param name="setting" select="'setMainContributor'"/>
                <xsl:with-param name="default" select="'true'"/>
            </xsl:call-template>
        </xsl:variable>
        <xsl:variable name="setCost">
            <xsl:call-template name="mandalay:getSetting">
                <xsl:with-param name="module"  select="'Event'"/>
                <xsl:with-param name="setting" select="'setCost'"/>
                <xsl:with-param name="default" select="'true'"/>
            </xsl:call-template>
        </xsl:variable>
        <xsl:variable name="setMapLink">
            <xsl:call-template name="mandalay:getSetting">
                <xsl:with-param name="module"  select="'Event'"/>
                <xsl:with-param name="setting" select="'setMapLink'"/>
                <xsl:with-param name="default" select="'true'"/>
            </xsl:call-template>
        </xsl:variable>

        <div id="mainBody">
            <div class="details table">
                <xsl:if test="./location and $setLocation = 'true'">
                    <div class="location tableRow">
                        <span class="key">
                            <xsl:call-template name="mandalay:getStaticText">
                                <xsl:with-param name="module" select="'Event'" />
                                <xsl:with-param name="id" select="'location'" />
                            </xsl:call-template>
                        </span>
                        <span class="value">
                            <xsl:value-of disable-output-escaping="yes" select="./location"/>
                        </span>
                    </div>
                </xsl:if>

                <xsl:choose>
                    <xsl:when test="not(./endDate) or ./startDate = ./endDate">
                        <!-- Zeige nur das StartDate an -->
                        <div class="date tableRow">
                            <span class="key">
                                <xsl:call-template name="mandalay:getStaticText">
                                    <xsl:with-param name="module" select="'Event'" />
                                    <xsl:with-param name="id" select="'date'" />
                                </xsl:call-template>
                            </span>
                            <span class="value">
                                <xsl:choose>
                                    <xsl:when test="$setDateFormat = 'L'">
                                        <xsl:value-of disable-output-escaping="yes" 
                                                      select="./startDate/@longDate"/>
                                    </xsl:when>
                                    <xsl:otherwise>
                                        <xsl:value-of disable-output-escaping="yes" 
                                                      select="./startDate/@date"/>
                                    </xsl:otherwise>
                                </xsl:choose>
                                <xsl:if test="./startTime/@time and ./endTime/@time and not(./startTime/@time = ./endTime/@time)">
                                    <xsl:value-of select="$dateSeparator"/>
                                    <xsl:value-of disable-output-escaping="yes" 
                                                  select="./startTime/@time"/>
                                    <xsl:value-of select="$timeSeparator"/>
                                    <xsl:value-of disable-output-escaping="yes" 
                                                  select="./endTime/@time"/>
                                    <xsl:call-template name="mandalay:getStaticText">
                                        <xsl:with-param name="module" select="'Event'" />
                                        <xsl:with-param name="id" select="'timeAppendix'" />
                                    </xsl:call-template>
                                </xsl:if>
                            </span>
                        </div>
                        <xsl:if test="not(./endTime/@time) or ./startTime/@time = ./endTime/@time">
                            <!-- Zeige nur die StartTime an-->
                            <div class="time tableRow">
                                <span class="key">
                                    <xsl:call-template name="mandalay:getStaticText">
                                        <xsl:with-param name="module" select="'Event'" />
                                        <xsl:with-param name="id" select="'startTime'" />
                                    </xsl:call-template>
                                </span>
                                <span class="value">
                                    <xsl:value-of disable-output-escaping="yes" 
                                                  select="./startTime/@time"/>
                                </span>
                            </div>
                        </xsl:if>
                    </xsl:when>

                    <xsl:otherwise>
                        <div class="datetime tableRow">
                            <span class="key">
                                <xsl:call-template name="mandalay:getStaticText">
                                    <xsl:with-param name="module" select="'Event'" />
                                    <xsl:with-param name="id" select="'startTime'" />
                                </xsl:call-template>
                            </span>
                            <span class="value">
                                <xsl:choose>
                                    <xsl:when test="$setDateFormat = 'L'">
                                        <xsl:value-of disable-output-escaping="yes" 
                                                      select="./startDate/@longDate"/>
                                    </xsl:when>
                                    <xsl:otherwise>
                                        <xsl:value-of disable-output-escaping="yes" 
                                                      select="./startDate/@date"/>
                                    </xsl:otherwise>
                                </xsl:choose>
                                <xsl:if test="./startTime">
                                    <xsl:call-template name="mandalay:getStaticText">
                                        <xsl:with-param name="module" select="'Event'" />
                                        <xsl:with-param name="id" select="'timePrefix'" />
                                    </xsl:call-template>
                                    <xsl:value-of disable-output-escaping="yes" select="./startTime/@time"/>
                                    <xsl:call-template name="mandalay:getStaticText">
                                        <xsl:with-param name="module" select="'Event'" />
                                        <xsl:with-param name="id" select="'timeAppendix'" />
                                    </xsl:call-template>
                                </xsl:if>
                            </span>
                        </div>
                        <div class="endTime tableRow">
                            <span class="key">
                                <xsl:call-template name="mandalay:getStaticText">
                                    <xsl:with-param name="module" select="'Event'" />
                                    <xsl:with-param name="id" select="'endTime'" />
                                </xsl:call-template>
                            </span>
                            <span class="value">
                                <xsl:choose>
                                    <xsl:when test="$setDateFormat = 'L'">
                                        <xsl:value-of disable-output-escaping="yes" select="./endDate/@longDate"/>
                                    </xsl:when>
                                    <xsl:otherwise>
                                        <xsl:value-of disable-output-escaping="yes" select="./endDate/@date"/>
                                    </xsl:otherwise>
                                </xsl:choose>
                                <xsl:if test="./endTime">
                                    <xsl:call-template name="mandalay:getStaticText">
                                        <xsl:with-param name="module" select="'Event'" />
                                        <xsl:with-param name="id" select="'timePrefix'" />
                                    </xsl:call-template>
                                    <xsl:value-of disable-output-escaping="yes" select="./endTime/@time"/>
                                    <xsl:call-template name="mandalay:getStaticText">
                                        <xsl:with-param name="module" select="'Event'" />
                                        <xsl:with-param name="id" select="'timeAppendix'" />
                                    </xsl:call-template>
                                </xsl:if>
                            </span>
                        </div>
                    </xsl:otherwise>
                </xsl:choose>

                <xsl:if test="./eventDate and $setEventDate = 'true'">
                    <div class="eventDate tableRow">
                        <span class="key">
                            <xsl:call-template name="mandalay:getStaticText">
                                <xsl:with-param name="module" select="'Event'" />
                                <xsl:with-param name="id" select="'eventDate'" />
                            </xsl:call-template>
                        </span>
                        <span class="value">
                            <xsl:value-of disable-output-escaping="yes" select="./eventDate"/>
                        </span>
                    </div>
                </xsl:if>

                <xsl:if test="./eventType and $setEventType = 'true'">
                    <div class="eventType tableRow">
                        <span class="key">
                            <xsl:call-template name="mandalay:getStaticText">
                                <xsl:with-param name="module" select="'Event'" />
                                <xsl:with-param name="id" select="'eventType'" />
                            </xsl:call-template>
                        </span>
                        <span class="value">
                            <xsl:value-of disable-output-escaping="yes" select="./eventType"/>
                        </span>
                    </div>
                </xsl:if>

                <xsl:if test="./mainContributor and $setMainContributor = 'true'">
                    <div class="mainContributor tableRow">
                        <span class="key">
                            <xsl:call-template name="mandalay:getStaticText">
                                <xsl:with-param name="module" select="'Event'" />
                                <xsl:with-param name="id" select="'mainContributor'" />
                            </xsl:call-template>
                        </span>
                        <div class="value">
                            <xsl:value-of disable-output-escaping="yes" select="./mainContributor"/>
                        </div>
                    </div>
                </xsl:if>

                <xsl:if test="./cost and $setCost = 'true'">
                    <div class="cost tableRow">
                        <span class="key">
                            <xsl:call-template name="mandalay:getStaticText">
                                <xsl:with-param name="module" select="'Event'" />
                                <xsl:with-param name="id" select="'cost'" />
                            </xsl:call-template>
                        </span>
                        <span class="value">
                            <xsl:value-of disable-output-escaping="yes" select="./cost"/>
                        </span>
                    </div>
                </xsl:if>

                <xsl:if test="./mapLink and $setMapLink = 'true'">
                    <div class="mapLink tableRow">
                        <a>
                            <xsl:attribute name="href">
                                <xsl:value-of select="./mapLink"/>
                            </xsl:attribute>
                            <span class="key">
                                <xsl:call-template name="mandalay:getStaticText">
                                    <xsl:with-param name="module" select="'Event'" />
                                    <xsl:with-param name="id" select="'mapLink'" />
                                </xsl:call-template>
                            </span>
                        </a>
                    </div>
                </xsl:if>
                <div class="endFloat"/>
            </div>

            <xsl:value-of disable-output-escaping="yes" select="./textAsset/content"/>
            <div class="endFloat"/>
        </div>
        <div class="endFloat"/>
    
    </xsl:template>

    <!-- DE Listenansicht -->
    <!-- EN List view -->
    <xsl:template name="CT_Event_List" 
                  match="nav:item[nav:attribute[@name='objectType'] = 'com.arsdigita.cms.contenttypes.Event']" 
                  mode="list_view">

        <!-- DE Hole alle benötigten Einstellungen-->
        <!-- EN Getting all needed setting-->
        <xsl:variable name="dateSeparator">
            <xsl:call-template name="mandalay:getSetting">
                <xsl:with-param name="module"  select="'Event'"/>
                <xsl:with-param name="setting" select="'listView/dateSeparator'"/>
                <xsl:with-param name="default" select="' :: '"/>
            </xsl:call-template>
        </xsl:variable>
        <xsl:variable name="timeSeparator">
            <xsl:call-template name="mandalay:getSetting">
                <xsl:with-param name="module"  select="'Event'"/>
                <xsl:with-param name="setting" select="'listView/timeSeparator'"/>
                <xsl:with-param name="default" select="' - '"/>
            </xsl:call-template>
        </xsl:variable>
        <xsl:variable name="setDate">
            <xsl:call-template name="mandalay:getSetting">
                <xsl:with-param name="module"  select="'Event'"/>
                <xsl:with-param name="setting" select="'listView/setDate'"/>
                <xsl:with-param name="default" select="'true'"/>
            </xsl:call-template>
        </xsl:variable>
        <xsl:variable name="setDateFormat">
            <xsl:call-template name="mandalay:getSetting">
                <xsl:with-param name="module"  select="'Event'"/>
                <xsl:with-param name="setting" select="'listView/setDateFormat'"/>
                <xsl:with-param name="default" select="'S'"/>
            </xsl:call-template>
        </xsl:variable>
        <xsl:variable name="setLeadText">
            <xsl:call-template name="mandalay:getSetting">
                <xsl:with-param name="module"  select="'Event'"/>
                <xsl:with-param name="setting" select="'listView/setLeadText'"/>
                <xsl:with-param name="default" select="'true'"/>
            </xsl:call-template>
        </xsl:variable>
        <xsl:variable name="setLeadTextLength">
            <xsl:call-template name="mandalay:getSetting">
                <xsl:with-param name="module"  select="'Event'"/>
                <xsl:with-param name="setting" select="'listView/setLeadTextLength'"/>
                <xsl:with-param name="default" select="'0'"/>
            </xsl:call-template>
        </xsl:variable>
        <xsl:variable name="setImage">
            <xsl:call-template name="mandalay:getSetting">
                <xsl:with-param name="module"  select="'Event'"/>
                <xsl:with-param name="setting" select="'listView/setImage'"/>
                <xsl:with-param name="default" select="'true'"/>
            </xsl:call-template>
        </xsl:variable>
        <xsl:variable name="setMoreButton">
            <xsl:call-template name="mandalay:getSetting">
                <xsl:with-param name="module"  select="'Event'"/>
                <xsl:with-param name="setting" select="'listView/setMoreButton'"/>
                <xsl:with-param name="default" select="'auto'"/>
            </xsl:call-template>
        </xsl:variable>

        <xsl:if test="$setDate = 'true' and ((nav:attribute[@name='startDate'] or nav:attribute[@name='endDate']) or nav:attribute[@name='eventDate'])">
            <div class="dateline">
                <xsl:choose>
                    <xsl:when test="not(nav:attribute[@name='endDate']) or nav:attribute[@name='startDate'] = nav:attribute[@name='endDate']">
                        <span class="date">
                            <xsl:choose>
                                <xsl:when test="$setDateFormat = 'L'">
                                    <xsl:value-of select="nav:attribute[@name='startDate']/@longDate"/>
                                </xsl:when>
                                <xsl:when test="$setDateFormat = 'M'">
                                    <span class="day">
                                        <xsl:value-of select="substring(nav:attribute[@name='startDate']/@date, 1, 2)"/>
                                    </span>
                                    <span class="month">
                                        <xsl:value-of select="substring(nav:attribute[@name='startDate']/@monthName, 1, 3)"/>
                                    </span>
                                    <span class="year">
                                        <xsl:value-of select="nav:attribute[@name='startDate']/@year"/>
                                    </span>
                                </xsl:when>
                                <xsl:otherwise>
                                    <xsl:value-of select="nav:attribute[@name='startDate']/@date"/>
                                </xsl:otherwise>
                            </xsl:choose>
                        </span>
                    </xsl:when>
                    <xsl:otherwise>
                        <span class="startDate">
                            <xsl:choose>
                                <xsl:when test="$setDateFormat = 'L'">
                                    <xsl:value-of select="nav:attribute[@name='startDate']/@longDate"/>
                                </xsl:when>
                                <xsl:when test="$setDateFormat = 'M'">
                                    <span class="day">
                                        <xsl:value-of select="substring(nav:attribute[@name='startDate']/@date, 1, 2)"/>
                                    </span>
                                    <span class="month">
                                        <xsl:value-of select="substring(nav:attribute[@name='startDate']/@monthName, 1, 3)"/>
                                    </span>
                                    <span class="year">
                                        <xsl:value-of select="nav:attribute[@name='startDate']/@year"/>
                                    </span>
                                </xsl:when>
                                <xsl:otherwise>
                                    <xsl:value-of select="nav:attribute[@name='startDate']/@date"/>
                                </xsl:otherwise>
                            </xsl:choose>
                        </span>
                        <span class="separator">
                            <xsl:value-of select="$dateSeparator"/>
                        </span>
                        <span class="endDate">
                            <xsl:choose>
                                <xsl:when test="$setDateFormat = 'L'">
                                    <xsl:value-of select="nav:attribute[@name='endDate']/@longDate"/>
                                </xsl:when>
                                <xsl:when test="$setDateFormat = 'M'">
                                    <span class="day">
                                        <xsl:value-of select="substring(nav:attribute[@name='endDate']/@date, 1, 2)"/>
                                    </span>
                                    <span class="month">
                                        <xsl:value-of select="substring(nav:attribute[@name='endDate']/@monthName, 1, 3)"/>
                                    </span>
                                    <span class="year">
                                        <xsl:value-of select="nav:attribute[@name='endDate']/@year"/>
                                    </span>
                                </xsl:when>
                                <xsl:otherwise>
                                    <xsl:value-of select="nav:attribute[@name='endDate']/@date"/>
                                </xsl:otherwise>
                            </xsl:choose>
                        </span>
                    </xsl:otherwise>
                </xsl:choose>
                <xsl:if test="nav:attribute[@name='eventDate']">
                    <xsl:value-of select="$dateSeparator"/>
                    <span class="location">
                        <xsl:value-of select="nav:attribute[@name='eventDate']"/>
                    </span>
                </xsl:if>
            </div>
        </xsl:if>

        <xsl:if test="$setImage = 'true' and nav:attribute[@name='imageAttachments.image.id']">
            <a>
                <xsl:attribute name="href">
                    <xsl:value-of select="nav:path"/>
                </xsl:attribute>
                <xsl:attribute name="title">
                    <xsl:call-template name="mandalay:shying">
                        <xsl:with-param name="title">
                            <xsl:value-of select="nav:attribute[@name='title']"/>
                        </xsl:with-param>
                        <xsl:with-param name="mode">dynamic</xsl:with-param>
                    </xsl:call-template>
                </xsl:attribute>

                <div class="image">
                    <img>
                        <xsl:attribute name="src">/ccm/cms-service/stream/image/?image_id=<xsl:value-of select="nav:attribute[@name='imageAttachments.image.id']"/>&amp;maxWidth=150&amp;maxHeight=100</xsl:attribute>
                        <xsl:if test="nav:attribute[@name='imageAttachments.caption']">
                            <xsl:attribute name="alt">
                                <xsl:value-of select="nav:attribute[@name='imageAttachments.caption']"/>
                            </xsl:attribute>
                            <xsl:attribute name="title">
                                <xsl:value-of select="nav:attribute[@name='imageAttachments.caption']"/>
                            </xsl:attribute>
                        </xsl:if>
                    </img>
                </div>
            </a>
        </xsl:if>

        <a class="CIname">
            <xsl:attribute name="href">
                <xsl:value-of select="nav:path"/>
            </xsl:attribute>
            <xsl:attribute name="title">
                <xsl:call-template name="mandalay:shying">
                    <xsl:with-param name="title">
                        <xsl:value-of select="nav:attribute[@name='title']"/>
                    </xsl:with-param>
                    <xsl:with-param name="mode">dynamic</xsl:with-param>
                </xsl:call-template>
            </xsl:attribute>
            <xsl:call-template name="mandalay:shying">
                <xsl:with-param name="title">
                    <xsl:value-of disable-output-escaping="yes" 
                                  select="nav:attribute[@name='title']"/>
                </xsl:with-param>
                <xsl:with-param name="mode">dynamic</xsl:with-param>
            </xsl:call-template>
        </a>

        <xsl:if test="nav:attribute[@name='lead'] and $setLeadText = 'true'">
            <br />
            <span class="intro">
                <xsl:choose>
                    <xsl:when test="$setLeadTextLength = '0'">
                        <xsl:value-of disable-output-escaping="yes" 
                                      select="nav:attribute[@name='lead']" />
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:value-of disable-output-escaping="yes" 
                                      select="substring(nav:attribute[@name='lead'], 1, $setLeadTextLength)" />
                        <xsl:if test="string-length(nav:attribute[@name='lead']) > $setLeadTextLength">
                            <xsl:text>...</xsl:text>
                            <xsl:if test="$setMoreButton = 'auto'">
                                <xsl:call-template name="mandalay:moreButton">
                                    <xsl:with-param name="href" select="nav:path"/>
                                    <xsl:with-param name="module" select="'Event'"/>
                                </xsl:call-template>
                            </xsl:if>
                        </xsl:if>
                    </xsl:otherwise>
                </xsl:choose>
                <xsl:if test="$setMoreButton = 'true'">
                    <xsl:call-template name="mandalay:moreButton">
                        <xsl:with-param name="href" select="nav:path"/>
                        <xsl:with-param name="module" select="'Event'"/>
                    </xsl:call-template>
                </xsl:if>
            </span>
        </xsl:if>
        <xsl:if test="$setDate = 'true' and ((nav:attribute[@name='startDate'] or nav:attribute[@name='endDate']) or nav:attribute[@name='eventDate'])">
            <div class="endFloat"/>
        </xsl:if>
    </xsl:template>

    <xsl:template name="CT_Event_Link" 
                  match="*/cms:item/links[targetItem/objectType = 'com.arsdigita.cms.contenttypes.Event']" 
                  mode="link_view">

        <!-- DE Hole alle benötigten Einstellungen-->
        <!-- EN Getting all needed setting-->
        <xsl:variable name="setImageAndText">
            <xsl:call-template name="mandalay:getSetting">
                <xsl:with-param name="module"  select="'Event'"/>
                <xsl:with-param name="setting" select="'linkView/setImageAndText'"/>
                <xsl:with-param name="default" select="'true'"/>
            </xsl:call-template>
        </xsl:variable>
        <xsl:variable name="setImage">
            <xsl:call-template name="mandalay:getSetting">
                <xsl:with-param name="module"  select="'Event'"/>
                <xsl:with-param name="setting" select="'linkView/setImage'"/>
                <xsl:with-param name="default" select="'true'"/>
            </xsl:call-template>
        </xsl:variable>
        <xsl:variable name="setImageMaxHeight">
            <xsl:call-template name="mandalay:getSetting">
                <xsl:with-param name="module"  select="'Event'"/>
                <xsl:with-param name="setting" select="'linkView/setImageMaxHeight'"/>
                <xsl:with-param name="default" select="''"/>
            </xsl:call-template>
        </xsl:variable>
        <xsl:variable name="setImageMaxWidth">
            <xsl:call-template name="mandalay:getSetting">
                <xsl:with-param name="module"  select="'Event'"/>
                <xsl:with-param name="setting" select="'linkView/setImageMaxWidth'"/>
                <xsl:with-param name="default" select="''"/>
            </xsl:call-template>
        </xsl:variable>
        <xsl:variable name="setImageCaption">
            <xsl:call-template name="mandalay:getSetting">
                <xsl:with-param name="module"  select="'Event'"/>
                <xsl:with-param name="setting" select="'linkView/setImageCaption'"/>
                <xsl:with-param name="default" select="'true'"/>
            </xsl:call-template>
        </xsl:variable>
        <xsl:variable name="setDescription">
            <xsl:call-template name="mandalay:getSetting">
                <xsl:with-param name="module"  select="'Event'"/>
                <xsl:with-param name="setting" select="'linkView/setDescription'"/>
                <xsl:with-param name="default" select="'true'"/>
            </xsl:call-template>
        </xsl:variable>
        <xsl:variable name="setDescriptionLength">
            <xsl:call-template name="mandalay:getSetting">
                <xsl:with-param name="module"  select="'Event'"/>
                <xsl:with-param name="setting" select="'linkView/setDescriptionLength'"/>
                <xsl:with-param name="default" select="'0'"/>
            </xsl:call-template>
        </xsl:variable>
        <xsl:variable name="setMoreButton">
            <xsl:call-template name="mandalay:getSetting">
                <xsl:with-param name="module"  select="'Event'"/>
                <xsl:with-param name="setting" select="'linkView/setMoreButton'"/>
                <xsl:with-param name="default" select="'auto'"/>
            </xsl:call-template>
        </xsl:variable>

        <!-- DE Wenn es Bilder gibt, dann soll das erste hier als Link angezeigt werden -->
        <!-- EN -->
        <xsl:if test="./targetItem/imageAttachments and $setImage = 'true'">
            <a class="CIname">
                <xsl:attribute name="href">
                    <xsl:text>/redirect/?oid=</xsl:text>
                    <xsl:value-of select="./targetItem/@oid"/>
                </xsl:attribute>
                <xsl:attribute name="title">
                    <xsl:call-template name="mandalay:shying">
                        <xsl:with-param name="title">
                            <xsl:value-of select="./linkTitle"/>
                        </xsl:with-param>
                        <xsl:with-param name="mode">dynamic</xsl:with-param>
                    </xsl:call-template>
                </xsl:attribute>
                <xsl:for-each select="./targetItem">
                    <xsl:call-template name="mandalay:imageAttachment">
                        <xsl:with-param name="showCaption" select="$setImageCaption" />
                        <xsl:with-param name="maxHeight" select="$setImageMaxHeight" />
                        <xsl:with-param name="maxWidth" select="$setImageMaxWidth" />
                    </xsl:call-template>
                </xsl:for-each>
            </a>
        </xsl:if>
        <xsl:if test="$setImageAndText = 'true' or not(./targetItem/imageAttachments) or $setImage = 'false'">
            <a class="CIname">
                <xsl:attribute name="href">
                    <xsl:text>/redirect/?oid=</xsl:text>
                    <xsl:value-of select="./targetItem/@oid"/>
                </xsl:attribute>
                <xsl:attribute name="title">
                    <xsl:call-template name="mandalay:shying">
                        <xsl:with-param name="title">
                            <xsl:value-of select="./linkTitle"/>
                        </xsl:with-param>
                        <xsl:with-param name="mode">dynamic</xsl:with-param>
                    </xsl:call-template>
                </xsl:attribute>
                <xsl:call-template name="mandalay:shying">
                    <xsl:with-param name="title">
                        <xsl:value-of disable-output-escaping="yes" select="./linkTitle"/>
                    </xsl:with-param>
                    <xsl:with-param name="mode">dynamic</xsl:with-param>
                </xsl:call-template>
            </a>
            <xsl:if test="./linkDescription and $setDescription">
                <br />
                <xsl:choose>
                    <xsl:when test="$setDescriptionLength = '0'">
                        <xsl:value-of disable-output-escaping="yes" 
                                      select="./linkDescription" />
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:value-of disable-output-escaping="yes" 
                                      select="substring(./linkDescription, 1, $setDescriptionLength)" />
                        <xsl:if test="string-length(./linkDescription) > $setDescriptionLength">
                            <xsl:text>...</xsl:text>
                            <xsl:if test="$setMoreButton = 'auto'">
                                <xsl:call-template name="mandalay:moreButton">
                                    <xsl:with-param name="href" select="./targetItem/@oid"/>
                                    <xsl:with-param name="module" select="'Event'"/>
                                </xsl:call-template>
                            </xsl:if>
                        </xsl:if>
                    </xsl:otherwise>
                </xsl:choose>
                <xsl:if test="$setMoreButton = 'true'">
                    <xsl:call-template name="mandalay:moreButton">
                        <xsl:with-param name="href" select="./targetItem/@oid"/>
                        <xsl:with-param name="module" select="'Event'"/>
                    </xsl:call-template>
                </xsl:if>
            </xsl:if>
        </xsl:if>
    </xsl:template>

</xsl:stylesheet>
