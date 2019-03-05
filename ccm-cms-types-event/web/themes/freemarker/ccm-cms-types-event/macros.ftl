<#ftl ns_prefixes={
"bebop":"http://www.arsdigita.com/bebop/1.0",
"cms":"http://www.arsdigita.com/cms/1.0",
"nav":"http://ccm.redhat.com/navigation",
"ui": "http://www.arsdigita.com/ui/1.0"}
>

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

<#macro location item>
    <#if (item["./location"]?size > 0)>
        ${item["./location"]}
    </#if>
</#macro>

<#macro mainContributor item>
    <#if (item["./mainContributor"]?size > 0)>
        ${item["./mainContributor"]}
    <#/if>
</#macro>

<#macro eventType item>
    <#if (item["./eventType"]?size > 0)>
        ${item["./eventType"]}
    <#/if>
</#macro>

<#macro cost item>
    <#if (item["./cost"]?size > 0)>
        ${item[./cost"]}
    </#if>
</#macro>

<#macro mapLink item>
    <#if (item["./mapLink"]?size > 0)>
        <#nested item["./mapLink">
    </#if>
</#macro>







