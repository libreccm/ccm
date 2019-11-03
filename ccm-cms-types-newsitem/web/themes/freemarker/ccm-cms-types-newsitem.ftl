<#ftl ns_prefixes={
"bebop":"http://www.arsdigita.com/bebop/1.0",
"cms":"http://www.arsdigita.com/cms/1.0",
"nav":"http://ccm.redhat.com/navigation",
"ui": "http://www.arsdigita.com/ui/1.0"}
>
<#import "/ccm-cms-types-article.ftl" as Article>

<#--filedoc
    Functions for News items
-->

<#--doc
    Gets the lead text of a news item

    @param item The news item to use.

    @return The lead text of the news item.
-->
<#function getLead item>
    <#return Article.getLead(item)>
</#function>

<#--doc
    Gets the main text of a news item

    @param item The news item to use.

    @return The main of the news item.
-->
<#function getMainText item>
    <#return Article.getMainText(item)>
</#function>

<#--doc
    Gets the date of a news item

    @param item The news item to use.

    @return The date of the news item.
-->
<#function getNewsDate item>
    <#if (item["./newsDate"]?size > 0)>
        <#return item["./newsDate"] />
    <#elseif (item["./nav:attribute[@name='newsDate']"]?size > 0)>
        <#return item["./nav:attribute[@name='newsDate']"] />
    </#if>
</#function>

<#--doc
    Gets the year of the news date.

    @param item The news item to use.

    @return The year of the news date.
-->
<#function getNewsDateYear item>
    <#if (item["./newsDate"]?size > 0)>
        <#return item["./newsDate/@year"]>
    <#elseif (item["./nav:attribute[@name='newsDate']"]?size > 0)>
        <#return item["./nav:attribute[@name='newsDate']/@year"]>
    </#if>
</#function>

<#--doc
    Gets the month of the news date.

    @param item The news item to use.

    @return The month of the news date.
-->
<#function getNewsDateMonth item>
    <#if (item["./newsDate"]?size > 0)>
        <#return item["./newsDate/@month"]>
    <#elseif (item["./nav:attribute[@name='newsDate']"]?size > 0)>
        <#return item["./nav:attribute[@name='newsDate']/@month"]>
    </#if>
</#function>

<#--doc
    Gets the day of the news date.

    @param item The news item to use.

    @return The day of the news date.
-->
<#function getNewsDateDay item>
    <#if (item["./newsDate"]?size > 0)>
        <#return item["./newsDate/@day"]>
    <#elseif (item["./nav:attribute[@name='newsDate']"]?size > 0)>
        <#return item["./nav:attribute[@name='newsDate']/@day"]>
    </#if>
</#function>

<#--doc
    Gets the short name of the day of the news date.

    @param item The news item to use.

    @return The short name of the day of the news date.
-->
<#function getNewsDateDayNameShort item>
    <#if (item["./newsDate"]?size > 0)>
        <#return item["./newsDate/@dayNameShort"]>
    <#elseif (item["./nav:attribute[@name='newsDate']"]?size > 0)>
        <#return item["./nav:attribute[@name='newsDate']/@dayNameShort"]>
    </#if>
</#function>

<#--doc
    Gets the hour of the news date.

    @param item The news item to use.

    @return The hour of the news date.
-->
<#function newsDateHour item>
    <#if (item["./newsDate"]?size > 0)>
        <#return item["./newsDate/@hour"]>
    <#elseif (item["./nav:attribute[@name='newsDate']"]?size > 0)>
        <#return $item["./nav:attribute[@name='newsDate']/@hour"]>
    </#if>
</#function>

<#--doc
    Gets the minute of the news date.

    @param item The news item to use.

    @return The minute of the news date.
-->
<#function newsDateMinute item>
    <#if (item["./newsDate"]?size > 0)>
        <#return item["./newsDate/@minute"]>
    <#elseif (item["./nav:attribute[@name='newsDate']"]?size > 0)>
        <#return item["./nav:attribute[@name='newsDate']/@minute"]>
    </#if>
</#function>

<#--doc
    Gets the second of the news date.

    @param item The news item to use.

    @return The second of the news date.
-->
<#function newsDateSecond item>
    <#if (item["./newsDate"]?size > 0)>
        <#return item["./newsDate/@second"]>
    <#elseif (item["./nav:attribute[@name='newsDate']"]?size > 0)>
        <#return item["./nav:attribute[@name='newsDate']/@second"]>
    </#if>
</#function>

<#--doc
    Gets the news date in ISO format (`yyyy-mm-dd HH:mm:ss`).

    @param item The news item to use.

    @return The in ISO format.
-->
<#function getNewsDateIso item>
    <#if (item["./newsDate"]?size > 0)>
        <#return item["./newsDate/@iso"]>
    <#elseif (item["./nav:attribute[@name='newsDate']"]?size > 0)>
        <#return item["./nav:attribute[@name='newsDate']/@iso"]>
    </#if>
</#function>

