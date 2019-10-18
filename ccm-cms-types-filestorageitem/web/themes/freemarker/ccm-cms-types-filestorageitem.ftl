<#ftl ns_prefixes={
"bebop":"http://www.arsdigita.com/bebop/1.0",
"cms":"http://www.arsdigita.com/cms/1.0",
"nav":"http://ccm.redhat.com/navigation",
"ui": "http://www.arsdigita.com/ui/1.0"}
>

<#function hasDescription item>
    <#return (item["./description"]?size > 0 || item["./nav:attribute[@name = 'description']"]?size > 0)>
</#function>

<#function getDescription item>
    <#if (item["./description"]?size > 0)>
        <#return item["./description"]>
    <#elseif (item["./nav:attribute[@name = 'description']"]?size > 0)>
        <#return item["./nav:attribute[@name = 'description']"]>
    </#if>
</#function>

<#function getFileId item>
    <#if (item["./file/id"]?size > 0)>
        <#return item["./file/id"]>
    <#elseif (item["./nav:attribute[@name = 'file.id']"]?size > 0)>
        <#return item["./nav:attribute[@name = 'file.id']"]>
    </#if>
</#function>

<#function hasFileName item>
    <#return (item["./file/name"]?size > 0 || item["./nav:attribute[@name = 'file.name']"]?size > 0)>
</#function>

<#function getFileName item>
    <#if (item["./file/name"]?size > 0)>
        <#return item["./file/name"]>
    <#elseif (item["./nav:attribute[@name = 'file.name']"]?size > 0)>
        <#return item["./nav:attribute[@name = 'file.name']"]>
    </#if>
</#function>

<#function getFileLink item mode="download" useFileName=true>
    <#if (mode == "stream" && useFileName && hasFileName(item))>
        <#return "${dispatcherPrefix}/cms-service/stream/asset/${getFileName(item)}?asset_id=${getFileId(item)}">
    <#elseif (mode="stream" && (!useFileName || !hasFileName(item)))>
        <#return "${dispatcherPrefix}/cms-service/stream/asset/?asset_id=${getFileId(item)}">
    <#elseif (mode="download" && useFileName && hasFileName(item))>
        <#return "${dispatcherPrefix}/cms-service/download/asset/${getFileName(item)}?asset_id=${getFileId(item)}">
    <#else>
        <#return "${dispatcherPrefix}/cms-service/download/asset/?asset_id=${getFileId(item)}">
    </#if>
</#function>