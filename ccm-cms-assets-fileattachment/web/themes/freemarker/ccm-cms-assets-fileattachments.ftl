<#ftl ns_prefixes={
"bebop":"http://www.arsdigita.com/bebop/1.0",
"cms":"http://www.arsdigita.com/cms/1.0",
"nav":"http://ccm.redhat.com/navigation",
"ui": "http://www.arsdigita.com/ui/1.0"}
>

<#function getFileAttachments item>
    <#return item["./fileAttachments"]?sort_by("fileOrder")>
</#function>

<#function getFileType file>
    <#if (file["./mimeType/mimeType"].@@text == "text/plain") && file["./mimeType/label"].@@text == "caption">
        <#return "caption">
    <#else>
        <#return "file">
    </#if>
</#function>

<#function getMimeType file>
    <#return file["./mimeType/mimeType"]>    
</#function>

<#function getMimeTypeLabel file>
    <#return file["./mimeType/label"]>    
</#function>

<#function getMimeTypeFileExtension file>
    <#return file["./mimeType/fileExtension"]>    
</#function>

<#function getFileSize file unit="byte">
    <#if unit == "kB">
        <#return (file["./length"].@@text?number / 1000)?round>
    <#elseif unit == "KiB">
        <#return (file["./length"].@@text?number / 1024)?round>
    <#elseif unit == "MB">
        <#return (file["./length"].@@text?number / 1000000)?round>
    <#elseif unit == "MiB">
        <#return (file["./length"].@@text?number / 1048576)?round>
    <#else>
        <#return file["./length"].@@text?number>
    </#if>
</#function>

<#function getFileId file>
    <#return file["./id"]>
</#function>

<#function getFileName file>
    <#return file["./name"].@@text>
</#function>

<#function getFileDescription file>
    <#return file["./description"].@@text>
</#function>

<#function getFileUrl file>
    <#return dispatcherPrefix + '/cms-service/stream/asset/' + getFileName(file) + '?asset_id=' + getFileId(file)>
</#function>



