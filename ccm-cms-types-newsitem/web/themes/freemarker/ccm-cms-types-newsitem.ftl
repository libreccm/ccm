<#ftl ns_prefixes={
"bebop":"http://www.arsdigita.com/bebop/1.0",
"cms":"http://www.arsdigita.com/cms/1.0",
"nav":"http://ccm.redhat.com/navigation",
"ui": "http://www.arsdigita.com/ui/1.0"}
>
<#import "/ccm-cms-types-article/macros.ftl" as Article>

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

<#function getLead item>
    <#return Article.getLead(item)>
</#function>

<#function getMainText item>
    <#return Article.getMainText(item)>
</#function>

<#--
    Output the main text of the provided news item.
-->
<#macro main item>
    <#if (item["./textAsset"]?size > 0)>
        ${item["./textAsset/content"]}
    </#if>
</#macro>

<#function getNewsDate item>
    <#if (item["./newsDate"]?size > 0)>
        <#return item["./newsDate"].@@text />
    <#elseif (item["./nav:attribute[@name='newsDate']"]?size > 0)>
        <#return item["./nav:attribute[@name='newsDate']"].@@text />
    </#if>
</#function>

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

<#function getNewsDateYear item>
    <#if (item["./newsDate"]?size > 0)>
        <#return item["./newsDate/@year"]>
    <#elseif (item["./nav:attribute[@name='newsDate']"]?size > 0)>
        <#return item["./nav:attribute[@name='newsDate']/@year"]>
    </#if>
</#function>

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

<#function getNewsDateMonth item>
    <#if (item["./newsDate"]?size > 0)>
        <#return item["./newsDate/@month"]>
    <#elseif (item["./nav:attribute[@name='newsDate']"]?size > 0)>
        <#return item["./nav:attribute[@name='newsDate']/@month"]>
    </#if>
</#function>

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

<#function getNewsDateDay item>
    <#if (item["./newsDate"]?size > 0)>
        <#return item["./newsDate/@day"]>
    <#elseif (item["./nav:attribute[@name='newsDate']"]?size > 0)>
        <#return item["./nav:attribute[@name='newsDate']/@day"]>
    </#if>
</#function>


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

<#function getNewsDateDayNameShort item>
    <#if (item["./newsDate"]?size > 0)>
        <#return item["./newsDate/@dayNameShort"]>
    <#elseif (item["./nav:attribute[@name='newsDate']"]?size > 0)>
        <#return item["./nav:attribute[@name='newsDate']/@dayNameShort"]>
    </#if>
</#function>

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

<#function newsDateHour item>
    <#if (item["./newsDate"]?size > 0)>
        <#return item["./newsDate/@hour"]>
    <#elseif (item["./nav:attribute[@name='newsDate']"]?size > 0)>
        <#return $item["./nav:attribute[@name='newsDate']/@hour"]>
    </#if>
</#function>

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

<#function newsDateMinute item>
    <#if (item["./newsDate"]?size > 0)>
        <#return item["./newsDate/@minute"]>
    <#elseif (item["./nav:attribute[@name='newsDate']"]?size > 0)>
        <#return item["./nav:attribute[@name='newsDate']/@minute"]>
    </#if>
</#function>

<#--
    Outputs the Second (the time unit) part of the date property of the provided news item.
-->
<#macro newsDateSecond item>
    <#if (item["./newsDate"]?size > 0)>
        ${item["./newsDate/@second"]}
    <#elseif (item["./nav:attribute[@name='newsDate']"]?size > 0)>
        ${item["./nav:attribute[@name='newsDate']/@second"]}
    </#if>
</#macro>

<#function newsDateSecond item>
    <#if (item["./newsDate"]?size > 0)>
        <#return item["./newsDate/@second"]>
    <#elseif (item["./nav:attribute[@name='newsDate']"]?size > 0)>
        <#return item["./nav:attribute[@name='newsDate']/@second"]>
    </#if>
</#function>

