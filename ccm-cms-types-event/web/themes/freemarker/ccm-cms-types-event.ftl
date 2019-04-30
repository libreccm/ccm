<#ftl ns_prefixes={
"bebop":"http://www.arsdigita.com/bebop/1.0",
"cms":"http://www.arsdigita.com/cms/1.0",
"nav":"http://ccm.redhat.com/navigation",
"ui": "http://www.arsdigita.com/ui/1.0"}
>

<#import "/ccm-cms-types-article/macros.ftl" as Article>

<#function getLead item>
    <#return Article.getLead(item)>
</#function>

<#function getMainText item>
    <#return Article.getMainText(item)>
</#function>


<#function getEndDate item>
    <#if (item["./endDate"]?size > 0)>
        <#return item["./endDate"]>
    <#elseif (item["./nav:attribute[@name='endDate']"]?size > 0)>
        <#return item["./nav:attribute[@name='endDate']"]>
    </#if>
</#function>

<#function getEndDateYear item>
    <#if (item["./endDate"]?size > 0)>
        <#return item["./endDate/@year"]>
    <#elseif (item["./nav:attribute[@name='endDate']"]?size > 0)>
        <#return item["./nav:attribute[@name='endDate']/@year"]>
    </#if>
</#function>

<#function getEndDateMonth item>
    <#if (item["./endDate"]?size > 0)>
        <#return item["./endDate/@month"]>
    <#elseif (item["./nav:attribute[@name='endDate']"]?size > 0)>
        <#return item["./nav:attribute[@name='endDate']/@month"]>
    </#if>
</#function>

<#function getEndDateDay item>
    <#if (item["./endDate"]?size > 0)>
        <#return item["./endDate/@day"]>
    <#elseif (item["./nav:attribute[@name='endDate']"]?size > 0)>
        <#return item["./nav:attribute[@name='endDate']/@day"]>
    </#if>
</#function>

<#function getEndDateDayNameShort item>
    <#if (item["./endDate"]?size > 0)>
        <#return item["./endDate/@dayNameShort"]>
    <#elseif (item["./nav:attribute[@name='endDate']"]?size > 0)>
        <#return item["./nav:attribute[@name='endDate']/@dayNameShort"]>
    </#if>
</#function>

<#function getEndTime item>
    <#if (item["./endTime"]?size > 0)>
        <#return item["./endTime"]>
    <#elseif (item["./nav:attribute[@name='endTime']"]?size > 0)>
        <#return item["./nav:attribute[@name='endTime']"]>
    </#if>
</#function>

<#function getEndTimeHour item>
    <#if (item["./endTime"]?size > 0)>
        <#return item["./endTime/@hour"]>
    <#elseif (item["./nav:attribute[@name='endTime']"]?size > 0)>
        <#return item["./nav:attribute[@name='endTime']/@hour"]>
    </#if>
</#function>

<#function getEndTimeMinute item>
    <#if (item["./endTime"]?size > 0)>
        <#return item["./endTime/@minute"]>
    <#elseif (item["./nav:attribute[@name='endTime']"]?size > 0)>
        <#return item["./nav:attribute[@name='endTime']/@minute"]>
    </#if>
</#function>

<#function getEndTimeSecond item>
    <#if (item["./endTime"]?size > 0)>
        <#return item["./endTime/@second"]>
    <#elseif (item["./nav:attribute[@name='endTime']"]?size > 0)>
        <#return item["./nav:attribute[@name='endTime']/@second"]>
    </#if>
</#function>

<#function getStartDate item>
    <#if (item["./startDate"]?size > 0)>
        <#return item["./startDate"]>
    <#elseif (item["./nav:attribute[@name='startDate']"]?size > 0)>
        <#return item["./nav:attribute[@name='startDate']"]>
    </#if>
</#function>

<#function getStartDateYear item>
    <#if (item["./startDate"]?size > 0)>
        <#return item["./startDate/@year"]>
    <#elseif (item["./nav:attribute[@name='startDate']"]?size > 0)>
        <#return item["./nav:attribute[@name='startDate']/@year"]>
    </#if>
</#function>

<#function getStartDateMonth item>
    <#if (item["./startDate"]?size > 0)>
        <#return item["./startDate/@month"]>
    <#elseif (item["./nav:attribute[@name='startDate']"]?size > 0)>
        <#return item["./nav:attribute[@name='startDate']/@month"]>
    </#if>
</#function>

<#function getStartDateDay item>
    <#if (item["./startDate"]?size > 0)>
        <#return item["./startDate/@day"]>
    <#elseif (item["./nav:attribute[@name='startDate']"]?size > 0)>
        <#return item["./nav:attribute[@name='startDate']/@day"]>
    </#if>
</#function>

<#function getStartDateDayNameShort item>
    <#if (item["./startDate"]?size > 0)>
        <#return item["./startDate/@dayNameShort"]>
    <#elseif (item["./nav:attribute[@name='startDate']"]?size > 0)>
        <#return item["./nav:attribute[@name='startDate']/@dayNameShort"]>
    </#if>
</#function>

<#function getStartTime item>
    <#if (item["./startTime"]?size > 0)>
        <#return item["./startTime"]>
    <#elseif (item["./nav:attribute[@name='startTime']"]?size > 0)>
        <#return item["./nav:attribute[@name='startTime']"]>
    </#if>
</#function>

<#function hasStartDate item>
</#function>

<#function hasEndDate item>
</#function>

<#function hasStartTime item>
    <#return (item["./startTime"]?size > 0 || item["./nav:attribute[@name='startTime']"]?size > 0)>
</#function>

<#function hasEndTime item>
    <#return (item["./endTime"]?size > 0 || item["./nav:attribute[@name='endTime']"]?size > 0)>
</#function>

<#function hasStartDate item>
    <#return (item["./startDate"]?size > 0 || item["./nav:attribute[@name='startDate']"]?size > 0)>
</#function>

<#function hasEndDate item>
    <#return (item["./endDate"]?size > 0 || item["./nav:attribute[@name='endDate']"]?size > 0)>
</#function>

<#function getstartTimeHour item>
    <#if (item["./startTime"]?size > 0)>
        <#return item["./startTime/@hour"]>
    <#elseif (item["./nav:attribute[@name='startTime']"]?size > 0)>
        <#return item["./nav:attribute[@name='startTime']/@hour"]>
    </#if>
</#function>

<#function getstartTimeMinute item>
    <#if (item["./startTime"]?size > 0)>
        <#return item["./startTime/@minute"]>
    <#elseif (item["./nav:attribute[@name='startTime']"]?size > 0)>
        <#return item["./nav:attribute[@name='startTime']/@minute"]>
    </#if>
</#function>

<#function getstartTimeSecond item>
    <#if (item["./startTime"]?size > 0)>
        <#return item["./startTime/@second"]>
    <#elseif (item["./nav:attribute[@name='startTime']"]?size > 0)>
        <#return item["./nav:attribute[@name='startTime']/@second"]>
    </#if>
</#function>

<#function getLocation(item)>
    <#return item["./location"]>
</#function>

<#function getMainContributor item>
    <#return item["./mainContributor"]>
</#function>

<#function getEventType item>
    <#return item["./eventType"]>
</#function>

<#function getCost item>
    <#return item["./cost"]>
</#function>

<#function getMapLink item>
    <#return item["./mapLink"]>
</#function>

<#function getEventDateAddendum item>
    <#return item["./eventDate"]>
</#function>




