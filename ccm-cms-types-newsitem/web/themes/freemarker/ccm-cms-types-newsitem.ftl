<#ftl ns_prefixes={
"bebop":"http://www.arsdigita.com/bebop/1.0",
"cms":"http://www.arsdigita.com/cms/1.0",
"nav":"http://ccm.redhat.com/navigation",
"ui": "http://www.arsdigita.com/ui/1.0"}
>
<#import "/ccm-cms-types-article.ftl" as Article>

<#function getLead item>
    <#return Article.getLead(item)>
</#function>

<#function getMainText item>
    <#return Article.getMainText(item)>
</#function>

<#function getNewsDate item>
    <#if (item["./newsDate"]?size > 0)>
        <#return item["./newsDate"] />
    <#elseif (item["./nav:attribute[@name='newsDate']"]?size > 0)>
        <#return item["./nav:attribute[@name='newsDate']"] />
    </#if>
</#function>

<#function getNewsDateYear item>
    <#if (item["./newsDate"]?size > 0)>
        <#return item["./newsDate/@year"]>
    <#elseif (item["./nav:attribute[@name='newsDate']"]?size > 0)>
        <#return item["./nav:attribute[@name='newsDate']/@year"]>
    </#if>
</#function>

<#function getNewsDateMonth item>
    <#if (item["./newsDate"]?size > 0)>
        <#return item["./newsDate/@month"]>
    <#elseif (item["./nav:attribute[@name='newsDate']"]?size > 0)>
        <#return item["./nav:attribute[@name='newsDate']/@month"]>
    </#if>
</#function>

<#function getNewsDateDay item>
    <#if (item["./newsDate"]?size > 0)>
        <#return item["./newsDate/@day"]>
    <#elseif (item["./nav:attribute[@name='newsDate']"]?size > 0)>
        <#return item["./nav:attribute[@name='newsDate']/@day"]>
    </#if>
</#function>

<#function getNewsDateDayNameShort item>
    <#if (item["./newsDate"]?size > 0)>
        <#return item["./newsDate/@dayNameShort"]>
    <#elseif (item["./nav:attribute[@name='newsDate']"]?size > 0)>
        <#return item["./nav:attribute[@name='newsDate']/@dayNameShort"]>
    </#if>
</#function>

<#function newsDateHour item>
    <#if (item["./newsDate"]?size > 0)>
        <#return item["./newsDate/@hour"]>
    <#elseif (item["./nav:attribute[@name='newsDate']"]?size > 0)>
        <#return $item["./nav:attribute[@name='newsDate']/@hour"]>
    </#if>
</#function>

<#function newsDateMinute item>
    <#if (item["./newsDate"]?size > 0)>
        <#return item["./newsDate/@minute"]>
    <#elseif (item["./nav:attribute[@name='newsDate']"]?size > 0)>
        <#return item["./nav:attribute[@name='newsDate']/@minute"]>
    </#if>
</#function>

<#function newsDateSecond item>
    <#if (item["./newsDate"]?size > 0)>
        <#return item["./newsDate/@second"]>
    <#elseif (item["./nav:attribute[@name='newsDate']"]?size > 0)>
        <#return item["./nav:attribute[@name='newsDate']/@second"]>
    </#if>
</#function>

<#function getNewsDateIso item>
    <#if (item["./newsDate"]?size > 0)>
        <#return item["./newsDate/@iso"]>
    <#elseif (item["./nav:attribute[@name='newsDate']"]?size > 0)>
        <#return item["./nav:attribute[@name='newsDate']/@iso"]>
    </#if>
</#function>

