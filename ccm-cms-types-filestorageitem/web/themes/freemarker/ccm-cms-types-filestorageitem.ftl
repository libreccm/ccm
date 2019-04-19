<#ftl ns_prefixes={
"bebop":"http://www.arsdigita.com/bebop/1.0",
"cms":"http://www.arsdigita.com/cms/1.0",
"nav":"http://ccm.redhat.com/navigation",
"ui": "http://www.arsdigita.com/ui/1.0"}
>

<#function getDescription item>
    <#if (item["./description"]?size > 0)>
        <#return item["./description"]>
    <#else if (item["./nav:attribute[@name = 'description']"])>
        <#return item["./nav:attribute[@name = 'description']"]>
    </#if>
</#function>

<#function getFileId item>
    <#if (item["./file/id"]?size > 0)>
        <#return item["./file/id"]>
    <#else if (item["./nav:attribute[@name = 'file.id']"])>
        <#return item["./nav:attribute[@name = 'file.id']"]>
    </#if>
</#function>

<#function getFileName item>
    <#if (item["./file/name"]?size > 0)>
        <#return item["./file/name"]>
    <#else if (item["./nav:attribute[@name = 'file.name']"])>
        <#return item["./nav:attribute[@name = 'file.name']"]>
    </#if>
</#function>

<#function getFileLink item mode="download" useFileName=true>
    <#if mode == "stream" && useFileName>
        <#return "${dispatcherPrefix}/cms-service/stream/asset/${getFileName(item)}?asset_id=${getFileId(item)">
    <#else if mode="stream" && !useFileName>
        <#return "${dispatcherPrefix}/cms-service/stream/asset/?asset_id=${getFileId(item)">
    <#else if mode="download" && useFileName>
        <#return "${dispatcherPrefix}/cms-service/download/asset/${getFileName(item)}?asset_id=${getFileId(item)">
    </#else>
        <#return "${dispatcherPrefix}/cms-service/download/asset/?asset_id=${getFileId(item)">
    </#if>
</#function>