<#ftl ns_prefixes={
"bebop":"http://www.arsdigita.com/bebop/1.0",
"cms":"http://www.arsdigita.com/cms/1.0",
"nav":"http://ccm.redhat.com/navigation",
"ui": "http://www.arsdigita.com/ui/1.0"}
>

<#--
    Outputs the lead text of the provided news item.
-->
<#macro lead item>
    <#if (item["./lead"]?size > 0)> 
        ${item["./lead"]}
    <#elseif (item["./nav:attribute[@name='lead']"]?size > 0)>
        ${item["./nav:attribute[@name='lead']"]}
    </#if>
</#macro>

<#--
    Output the main text of the provided news item.
-->
<#macro main item>
    <#if (item["./textAsset"]?size > 0)>
        ${item["./textAsset/content"]}
    </#if>
</#macro>

<#--
    Outputs value of the date property of the provided news item as provided by the model.
-->
<#macro newsDate item>
    <#if (item["./newsDate"]?size > 0)>
        ${item["./newsDate"]}
    <#elseif (item["./nav:attribute[@name='newsDate']"]?size > 0)>
        ${item["./nav:attribute[@name='newsDate']"]}
    </#if>
</#macro>

<#--
    Outputs the year part of the date property of the provided news item.
-->
<#macro newsDateYear item>
    <#if (item["./newsDate"]?size > 0)>
        ${item["./newsDate/@year"]}
    <#elseif (item["./nav:attribute[@name='newsDate']"]?size > 0)>
        ${item["./nav:attribute[@name='newsDate']/@year"]}
    </#if>
</#macro>

<#--
    Outputs the month part of the date property of the provided news item.
-->
<#macro newsDateMonth item>
    <#if (item["./newsDate"]?size > 0)>
        ${item["./newsDate/@month"]}
    <#elseif (item["./nav:attribute[@name='newsDate']"]?size > 0)>
        ${item["./nav:attribute[@name='newsDate']/@month"]}
    </#if>
</#macro>

<#--
    Outputs the day part of the date property of the provided news item.
-->
<#macro newsDateDay item>
    <#if (item["./newsDate"]?size > 0)>
        ${item["./newsDate/@day"]}
    <#elseif (item["./nav:attribute[@name='newsDate']"]?size > 0)>
        ${item["./nav:attribute[@name='newsDate']/@day"]}
    </#if>
</#macro>

<#--
    Outputs the short day name of the date property of the provided news item.
-->
<#macro newsDateDayNameShort item>
    <#if (item["./newsDate"]?size > 0)>
        ${item["./newsDate/@dayNameShort"]}
    <#elseif (item["./nav:attribute[@name='newsDate']"]?size > 0)>
        ${item["./nav:attribute[@name='newsDate']/@dayNameShort"]}
    </#if>
</#macro>

<#--
    Outputs the hour part of the date property of the provided news item.
-->
<#macro newsDateHour item>
    <#if (item["./newsDate"]?size > 0)>
        ${item["./newsDate/@hour"]}
    <#elseif (item["./nav:attribute[@name='newsDate']"]?size > 0)>
        ${item["./nav:attribute[@name='newsDate']/@hour"]}
    </#if>
</#macro>

<#--
    Outputs the minute part of the date property of the provided news item.
-->
<#macro newsDateMinute item>
    <#if (item["./newsDate"]?size > 0)>
        ${item["./newsDate/@minute"]}
    <#elseif (item["./nav:attribute[@name='newsDate']"]?size > 0)>
        ${item["./nav:attribute[@name='newsDate']/@minute"]}
    </#if>
</#macro>

<#--
    Outputs the second part of the date property of the provided news item.
-->
<#macro newsDateSecond item>
    <#if (item["./newsDate"]?size > 0)>
        ${item["./newsDate/@second"]}
    <#elseif (item["./nav:attribute[@name='newsDate']"]?size > 0)>
        ${item["./nav:attribute[@name='newsDate']/@second"]}
    </#if>
</#macro>

h