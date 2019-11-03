<#ftl ns_prefixes={
"bebop":"http://www.arsdigita.com/bebop/1.0",
"cms":"http://www.arsdigita.com/cms/1.0",
"nav":"http://ccm.redhat.com/navigation",
"ui": "http://www.arsdigita.com/ui/1.0"}
>

<#--filedoc
    Functions for processing File Storage Items.
-->

<#--doc
    Determines if a file storage item has a description.

    @param item The model of the file storage item to use.

    @return `true` if the file storage item has a description, `false` otherwise.
-->
<#function hasDescription item>
    <#return (item["./description"]?size > 0 || item["./nav:attribute[@name = 'description']"]?size > 0)>
</#function>

<#--doc
    Gets the description of a file storage item.

    @param item The model of the file storage item to use.

    @return The description of the file storage item.
-->
<#function getDescription item>
    <#if (item["./description"]?size > 0)>
        <#return item["./description"]>
    <#elseif (item["./nav:attribute[@name = 'description']"]?size > 0)>
        <#return item["./nav:attribute[@name = 'description']"]>
    </#if>
</#function>

<#--doc
    The ID of the file of the file storage item.

    @param item The model of the file storage item to use.

    @return The ID of the file.
-->
<#function getFileId item>
    <#if (item["./file/id"]?size > 0)>
        <#return item["./file/id"]>
    <#elseif (item["./nav:attribute[@name = 'file.id']"]?size > 0)>
        <#return item["./nav:attribute[@name = 'file.id']"]>
    </#if>
</#function>

<#--doc
    Determines if the file name property is set.

    @param item The model of the file storage item to use.

    @return `true` if the file name property has a value, `false` if not.
-->
<#function hasFileName item>
    <#return (item["./file/name"]?size > 0 || item["./nav:attribute[@name = 'file.name']"]?size > 0)>
</#function>

<#--doc
    Gets the name of the file of file storage item.

    @param item The model of the file storage item to use.

    @return The name of the file. 
-->
<#function getFileName item>
    <#if (item["./file/name"]?size > 0)>
        <#return item["./file/name"]>
    <#elseif (item["./nav:attribute[@name = 'file.name']"]?size > 0)>
        <#return item["./nav:attribute[@name = 'file.name']"]>
    </#if>
</#function>

<#--doc
    Constructs the link for downloading or viewing associated file.

    @param item The model of the file storage item to use.

    @param mode The mode for the link. Either `stream` or `download`. If the 
    mode is set to stream and the client has an application for viewing the file
    the file is shown. Otherwise the file is downloaded.

    @return The link for downloading or viewing the file.
-->
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