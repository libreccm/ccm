<#ftl ns_prefixes={
"bebop":"http://www.arsdigita.com/bebop/1.0",
"cms":"http://www.arsdigita.com/cms/1.0",
"nav":"http://ccm.redhat.com/navigation",
"ui": "http://www.arsdigita.com/ui/1.0"}
>

<#import "/ccm-cms-types-article/macros.ftl" as Article>

<#--
    Outputs the lead text of the provided event.
-->
<#macro lead item>
    <#if (item["./lead"]?size > 0)> 
        ${item["./lead"]}
    <#elseif (item["./nav:attribute[@name='lead']"]?size > 0)>
        ${item["./nav:attribute[@name='lead']"]}
    </#if>
</#macro>

<#--
    Output the main text of the provided event.
-->
<#macro main item>
    <#if (item["./textAsset"]?size > 0)>
        ${item["./textAsset/content"]}
    </#if>
</#macro>

<#macro endDate item>
    <#if (item["./endDate"]?size > 0)>
        ${item["./endDate"]}
    <#elseif (item["./nav:attribute[@name='endDate']"]?size > 0)>
        ${item["./nav:attribute[@name='endDate']"]}
    </#if>
</#macro>

<#macro endDateYear item>
    <#if (item["./endDate"]?size > 0)>
        ${item["./endDate/@year"]}
    <#elseif (item["./nav:attribute[@name='endDate']"]?size > 0)>
        ${item["./nav:attribute[@name='endDate']/@year"]}
    </#if>
</#macro>

<#macro endDateMonth item>
    <#if (item["./endDate"]?size > 0)>
        ${item["./endDate/@month"]}
    <#elseif (item["./nav:attribute[@name='endDate']"]?size > 0)>
        ${item["./nav:attribute[@name='endDate']/@month"]}
    </#if>
</#macro>

<#macro endDateDay item>
    <#if (item["./endDate"]?size > 0)>
        ${item["./endDate/@day"]}
    <#elseif (item["./nav:attribute[@name='endDate']"]?size > 0)>
        ${item["./nav:attribute[@name='endDate']/@day"]}
    </#if>
</#macro>

<#macro endDateDayNameShort item>
    <#if (item["./endDate"]?size > 0)>
        ${item["./endDate/@dayNameShort"]}
    <#elseif (item["./nav:attribute[@name='endDate']"]?size > 0)>
        ${item["./nav:attribute[@name='endDate']/@dayNameShort"]}
    </#if>
</#macro>

<#macro endDateHour item>
    <#if (item["./endDate"]?size > 0)>
        ${item["./endDate/@hour"]}
    <#elseif (item["./nav:attribute[@name='endDate']"]?size > 0)>
        ${item["./nav:attribute[@name='endDate']/@hour"]}
    </#if>
</#macro>

<#macro endDateMinute item>
    <#if (item["./endDate"]?size > 0)>
        ${item["./endDate/@minute"]}
    <#elseif (item["./nav:attribute[@name='endDate']"]?size > 0)>
        ${item["./nav:attribute[@name='endDate']/@minute"]}
    </#if>
</#macro>

<#macro endDateSecond item>
    <#if (item["./endDate"]?size > 0)>
        ${item["./endDate/@second"]}
    <#elseif (item["./nav:attribute[@name='endDate']"]?size > 0)>
        ${item["./nav:attribute[@name='endDate']/@second"]}
    </#if>
</#macro>

<#macro startDate item>
    <#if (item["./startDate"]?size > 0)>
        ${item["./startDate"]}
    <#elseif (item["./nav:attribute[@name='startDate']"]?size > 0)>
        ${item["./nav:attribute[@name='startDate']"]}
    </#if>
</#macro>

<#macro startDateYear item>
    <#if (item["./startDate"]?size > 0)>
        ${item["./startDate/@year"]}
    <#elseif (item["./nav:attribute[@name='startDate']"]?size > 0)>
        ${item["./nav:attribute[@name='startDate']/@year"]}
    </#if>
</#macro>

<#macro startDateMonth item>
    <#if (item["./startDate"]?size > 0)>
        ${item["./startDate/@month"]}
    <#elseif (item["./nav:attribute[@name='startDate']"]?size > 0)>
        ${item["./nav:attribute[@name='startDate']/@month"]}
    </#if>
</#macro>

<#macro startDateDay item>
    <#if (item["./startDate"]?size > 0)>
        ${item["./startDate/@day"]}
    <#elseif (item["./nav:attribute[@name='startDate']"]?size > 0)>
        ${item["./nav:attribute[@name='startDate']/@day"]}
    </#if>
</#macro>

<#macro startDateDayNameShort item>
    <#if (item["./startDate"]?size > 0)>
        ${item["./startDate/@dayNameShort"]}
    <#elseif (item["./nav:attribute[@name='startDate']"]?size > 0)>
        ${item["./nav:attribute[@name='startDate']/@dayNameShort"]}
    </#if>
</#macro>

<#macro startDateHour item>
    <#if (item["./startDate"]?size > 0)>
        ${item["./startDate/@hour"]}
    <#elseif (item["./nav:attribute[@name='startDate']"]?size > 0)>
        ${item["./nav:attribute[@name='startDate']/@hour"]}
    </#if>
</#macro>

<#macro startDateMinute item>
    <#if (item["./startDate"]?size > 0)>
        ${item["./startDate/@minute"]}
    <#elseif (item["./nav:attribute[@name='startDate']"]?size > 0)>
        ${item["./nav:attribute[@name='startDate']/@minute"]}
    </#if>
</#macro>

<#macro startDateSecond item>
    <#if (item["./startDate"]?size > 0)>
        ${item["./startDate/@second"]}
    <#elseif (item["./nav:attribute[@name='startDate']"]?size > 0)>
        ${item["./nav:attribute[@name='startDate']/@second"]}
    </#if>
</#macro>

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

<#macro location item>
    <#if (item["./location"]?size > 0)>
        ${item["./location"]}
    </#if>
</#macro>

<#function getLocation(item)>
    <#return item["./location"]>
</#function>

<#macro mainContributor item>
    <#if (item["./mainContributor"]?size > 0)>
        ${item["./mainContributor"]}
    </#if>
</#macro>

<#function getMainContributor item>
    <#return item["./mainContributor"]>
</#function>

<#macro eventType item>
    <#if (item["./eventType"]?size > 0)>
        ${item["./eventType"]}
    </#if>
</#macro>

<#function getEventType item>
    <#return item["./eventType"]>
</#function>

<#macro cost item>
    <#if (item["./cost"]?size > 0)>
        ${item["./cost"]}
    </#if>
</#macro>

<#function getCost item>
    <#return item["./cost"]>
</#function>

<#macro mapLink item>
    <#if (item["./mapLink"]?size > 0)>
        <#nested item["./mapLink"]>
    </#if>
</#macro>

<#function getMapLink>
    <#return item["./mapLink"]>
</#function>





