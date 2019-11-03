<#ftl ns_prefixes={
"bebop":"http://www.arsdigita.com/bebop/1.0",
"cms":"http://www.arsdigita.com/cms/1.0",
"nav":"http://ccm.redhat.com/navigation",
"ui": "http://www.arsdigita.com/ui/1.0"}
>

<#import "/ccm-cms-types-article.ftl" as Article>

<#--filedoc
    Functions for processing Event items.
-->

<#--doc
    Gets the lead text of an event item

    @param item The event item to use.

    @return The lead text of the event item.
-->

<#function getLead item>
    <#return Article.getLead(item)>
</#function>

<#--doc
    Gets the main text of an event item

    @param item The event item to use.

    @return The main of the event item.
-->
<#function getMainText item>
    <#return Article.getMainText(item)>
</#function>

<#--doc
    Gets the end date of an event item

    @param item The news event to use.

    @return The date of the event item.
-->
<#function getEndDate item>
    <#if (item["./endDate"]?size > 0)>
        <#return item["./endDate"]>
    <#elseif (item["./nav:attribute[@name='endDate']"]?size > 0)>
        <#return item["./nav:attribute[@name='endDate']"]>
    </#if>
</#function>

<#--doc
    Gets the year of the end date of the event.

    @param item The event item to use.

    @return The year of the end date of the event.
-->
<#function getEndDateYear item>
    <#if (item["./endDate"]?size > 0)>
        <#return item["./endDate/@year"]>
    <#elseif (item["./nav:attribute[@name='endDate']"]?size > 0)>
        <#return item["./nav:attribute[@name='endDate']/@year"]>
    </#if>
</#function>

<#--doc
    Gets the month of the end date of the event.

    @param item The event item to use.

    @return The month of the end date of the event.
-->
<#function getEndDateMonth item>
    <#if (item["./endDate"]?size > 0)>
        <#return item["./endDate/@month"]>
    <#elseif (item["./nav:attribute[@name='endDate']"]?size > 0)>
        <#return item["./nav:attribute[@name='endDate']/@month"]>
    </#if>
</#function>

<#--doc
    Gets the day of the end date of the event.

    @param item The event item to use.

    @return The day of the end date of the event.
-->
<#function getEndDateDay item>
    <#if (item["./endDate"]?size > 0)>
        <#return item["./endDate/@day"]>
    <#elseif (item["./nav:attribute[@name='endDate']"]?size > 0)>
        <#return item["./nav:attribute[@name='endDate']/@day"]>
    </#if>
</#function>

<#--doc
    Gets the short name of the day of the end date of the event.

    @param item The event item to use.

    @return The short name of the day of the end date of the event.
-->
<#function getEndDateDayNameShort item>
    <#if (item["./endDate"]?size > 0)>
        <#return item["./endDate/@dayNameShort"]>
    <#elseif (item["./nav:attribute[@name='endDate']"]?size > 0)>
        <#return item["./nav:attribute[@name='endDate']/@dayNameShort"]>
    </#if>
</#function>

<#--doc
    Gets the end time of the event.

    @param item The event item to use.

    @return The end time of the event.
-->
<#function getEndTime item>
    <#if (item["./endTime"]?size > 0)>
        <#return item["./endTime"]>
    <#elseif (item["./nav:attribute[@name='endTime']"]?size > 0)>
        <#return item["./nav:attribute[@name='endTime']"]>
    </#if>
</#function>

<#--doc
    Gets the hour of the end time of the event.

    @param item The event item to use.

    @return The hour of the end time of the event.
-->
<#function getEndTimeHour item>
    <#if (item["./endTime"]?size > 0)>
        <#return item["./endTime/@hour"]>
    <#elseif (item["./nav:attribute[@name='endTime']"]?size > 0)>
        <#return item["./nav:attribute[@name='endTime']/@hour"]>
    </#if>
</#function>

<#--doc
    Gets the minute of the end time of the event.

    @param item The event item to use.

    @return The minute of the end time of the event.
-->
<#function getEndTimeMinute item>
    <#if (item["./endTime"]?size > 0)>
        <#return item["./endTime/@minute"]>
    <#elseif (item["./nav:attribute[@name='endTime']"]?size > 0)>
        <#return item["./nav:attribute[@name='endTime']/@minute"]>
    </#if>
</#function>

<#--doc
    Gets the second of the end time of the event.

    @param item The event item to use.

    @return The second of the end time of the event.
-->
<#function getEndTimeSecond item>
    <#if (item["./endTime"]?size > 0)>
        <#return item["./endTime/@second"]>
    <#elseif (item["./nav:attribute[@name='endTime']"]?size > 0)>
        <#return item["./nav:attribute[@name='endTime']/@second"]>
    </#if>
</#function>

<#--doc
    Gets the start date of an event item

    @param item The news event to use.

    @return The start of the event item.
-->
<#function getStartDate item>
    <#if (item["./startDate"]?size > 0)>
        <#return item["./startDate"]>
    <#elseif (item["./nav:attribute[@name='startDate']"]?size > 0)>
        <#return item["./nav:attribute[@name='startDate']"]>
    </#if>
</#function>

<#--doc
    Gets the year of the start date of the event.

    @param item The event item to use.

    @return The year of the start date of the event.
-->
<#function getStartDateYear item>
    <#if (item["./startDate"]?size > 0)>
        <#return item["./startDate/@year"]>
    <#elseif (item["./nav:attribute[@name='startDate']"]?size > 0)>
        <#return item["./nav:attribute[@name='startDate']/@year"]>
    </#if>
</#function>

<#--doc
    Gets the month of the start date of the event.

    @param item The event item to use.

    @return The month of the start date of the event.
-->
<#function getStartDateMonth item>
    <#if (item["./startDate"]?size > 0)>
        <#return item["./startDate/@month"]>
    <#elseif (item["./nav:attribute[@name='startDate']"]?size > 0)>
        <#return item["./nav:attribute[@name='startDate']/@month"]>
    </#if>
</#function>

<#--doc
    Gets the day of the start date of the event.

    @param item The event item to use.

    @return The day of the start date of the event.
-->
<#function getStartDateDay item>
    <#if (item["./startDate"]?size > 0)>
        <#return item["./startDate/@day"]>
    <#elseif (item["./nav:attribute[@name='startDate']"]?size > 0)>
        <#return item["./nav:attribute[@name='startDate']/@day"]>
    </#if>
</#function>

<#--doc
    Gets the short name of the day of the start date of the event.

    @param item The event item to use.

    @return The short name of the day of the start date of the event.
-->
<#function getStartDateDayNameShort item>
    <#if (item["./startDate"]?size > 0)>
        <#return item["./startDate/@dayNameShort"]>
    <#elseif (item["./nav:attribute[@name='startDate']"]?size > 0)>
        <#return item["./nav:attribute[@name='startDate']/@dayNameShort"]>
    </#if>
</#function>

<#--doc
    Gets the start time of the event.

    @param item The event item to use.

    @return The start time of the event.
-->
<#function getStartTime item>
    <#if (item["./startTime"]?size > 0)>
        <#return item["./startTime"]>
    <#elseif (item["./nav:attribute[@name='startTime']"]?size > 0)>
        <#return item["./nav:attribute[@name='startTime']"]>
    </#if>
</#function>

<#--doc
    Determines if the provided event item has a start time

    @param item The event item to use.

    @return `true` if the provided event item has a start time, `false` otherwise.
-->
<#function hasStartTime item>
    <#return (item["./startTime"]?size > 0 || item["./nav:attribute[@name='startTime']"]?size > 0)>
</#function>

<#--doc
    Determines if the provided event item has a end time

    @param item The event item to use.

    @return `true` if the provided event item has a end time, `false` otherwise.
-->
<#function hasEndTime item>
    <#return (item["./endTime"]?size > 0 || item["./nav:attribute[@name='endTime']"]?size > 0)>
</#function>

<#--doc
    Determines if the provided event item has a start date.

    @param item The event item to use.

    @return `true` if the provided event item has a start date, `false` otherwise.
-->
<#function hasStartDate item>
    <#return (item["./startDate"]?size > 0 || item["./nav:attribute[@name='startDate']"]?size > 0)>
</#function>

<#--doc
    Determines if the provided event item has a end date.

    @param item The event item to use.

    @return `true` if the provided event item has a end date, `false` otherwise.
-->
<#function hasEndDate item>
    <#return (item["./endDate"]?size > 0 || item["./nav:attribute[@name='endDate']"]?size > 0)>
</#function>

<#--doc
    Gets the hour of the start time of the event.

    @param item The event item to use.

    @return The hour of the start time of the event.
-->
<#function getstartTimeHour item>
    <#if (item["./startTime"]?size > 0)>
        <#return item["./startTime/@hour"]>
    <#elseif (item["./nav:attribute[@name='startTime']"]?size > 0)>
        <#return item["./nav:attribute[@name='startTime']/@hour"]>
    </#if>
</#function>

<#--doc
    Gets the minute of the start time of the event.

    @param item The event item to use.

    @return The minute of the start time of the event.
-->
<#function getstartTimeMinute item>
    <#if (item["./startTime"]?size > 0)>
        <#return item["./startTime/@minute"]>
    <#elseif (item["./nav:attribute[@name='startTime']"]?size > 0)>
        <#return item["./nav:attribute[@name='startTime']/@minute"]>
    </#if>
</#function>

<#--doc
    Gets the second of the start time of the event.

    @param item The event item to use.

    @return The second of the start time of the event.
-->
<#function getstartTimeSecond item>
    <#if (item["./startTime"]?size > 0)>
        <#return item["./startTime/@second"]>
    <#elseif (item["./nav:attribute[@name='startTime']"]?size > 0)>
        <#return item["./nav:attribute[@name='startTime']/@second"]>
    </#if>
</#function>

<#--doc
    Gets the location of the event.

    @param item The event item to use.

    @return The value of the `location` property of the event.
-->
<#function getLocation(item)>
    <#return item["./location"]>
</#function>

<#--doc
    Gets the main contributor of the event.

    @param item The event item to use.

    @return The value of the `mainContributor` property of the event.
-->
<#function getMainContributor item>
    <#return item["./mainContributor"]>
</#function>

<#--doc
    Gets the type of the event.

    @param item The event item to use.

    @return The value of the `eventType` property of the event.
-->
<#function getEventType item>
    <#return item["./eventType"]>
</#function>

<#--doc
    Gets the cost of the event.

    @param item The event item to use.

    @return The value of the `cost` property of the event.
-->
<#function getCost item>
    <#return item["./cost"]>
</#function>

<#--doc
    Gets the map link for the event.

    @param item The event item to use.

    @return The value of the `mapLink` property of the event.
-->
<#function getMapLink item>
    <#return item["./mapLink"]>
</#function>

<#--doc
    Gets the addendium  of the event.

    @param item The event item to use.

    @return The value of the `eventDate` property of the event.
-->
<#function getEventDateAddendum item>
    <#return item["./eventDate"]>
</#function>




