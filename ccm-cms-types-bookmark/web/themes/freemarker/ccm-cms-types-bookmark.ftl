<#ftl ns_prefixes={
"bebop":"http://www.arsdigita.com/bebop/1.0",
"cms":"http://www.arsdigita.com/cms/1.0",
"nav":"http://ccm.redhat.com/navigation",
"ui": "http://www.arsdigita.com/ui/1.0"}
>

<#--filedoc
    Functions for Bookmark items.
-->

<#--doc 
    Gets the description of a bookmark.

    @param item The bookmark item to use.

    @return The description of the provided bookmark.
-->
<#function getDescription item>
    <#if (item["./description"]?size > 0)>
        <#return item["./description"]>
    <#else if(item["./nav:attribute[@name='description']"]?size > 0)>
        <#return item["./nav:attribute[@name='description']"]>
    </#if>
</#function>

<#--doc
    Gets the link of a bookmark.

    @param item The bookmark item to use.

    @return The link of the provided bookmark.
-->
<#function getLink item>
    <#if (item["./url"]?size > 0)>
        <#return item["./url"]>
    <#else if(item["./nav:attribute[@name = 'url']"]?size > 0)>
        <#return item["./nav:attribute[@name = 'url']"]>
    </#if>
</#function>

