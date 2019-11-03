<#ftl ns_prefixes={
"bebop":"http://www.arsdigita.com/bebop/1.0",
"cms":"http://www.arsdigita.com/cms/1.0",
"nav":"http://ccm.redhat.com/navigation",
"ui": "http://www.arsdigita.com/ui/1.0"}
>

<#--filedoc
    Functions for processing file attachments.
-->

<#--doc
    Gets the file attachements of an content item.

    @param item The content item providing the file attachments.

    @return A sorted sequence of hashes (see Freemarker documentation) containing 
    the data the file attachments. The following keys are available: 
    
    * `fileType`: Type of the attachments. Either `file` or `caption`
    * `mimeType`: The mime type of the file.
    * `mimeTypeLabel`: A human readable label for the mime type.
    * `fileSize`: The size of the file.
    * `fileExtension`: The extension part of the file name.
    * `fileId`: The ID of the file.
    * `fileName`: The name of the file.
    * `description`: The description of the file attachment.
    * `fileUrl`: The URL for downloading the file.
    * `fileOrder`: The value of the order property of the file attachment.
-->
<#function getFileAttachments item>

    <#return item["./fileAttachments"]?map(
        file -> { 
            "fileType": _getFileType(file), 
            "mimeType": file["./mimeType/mimeType"].@@text, 
            "mimeTypeLabel": file["./mimeType/label"].@@text, 
            "fileSize": file["./length"].@@text?number, 
            "fileExtension": file["./mimeType/fileExtension"],
            "fileId": file["./id"].@@text, 
            "fileName": file["./name"].@@text, 
            "description": file["./description"].@@text, 
            "fileUrl": dispatcherPrefix + '/cms-service/stream/asset/' + file["./name"].@@text + '?asset_id=' + file["./id"], 
            "fileOrder": file["./fileOrder"]?number
        })?sort_by("fileOrder")>

    <#--  <#return item["./fileAttachments"]?sort_by("fileOrder")>  -->
</#function>

<#--  <#function getFileType file>
    <#return file.fileType>
</#function>  -->

<#--doc
    *Internal* function for determing the type of a file attachment.

    @param file The file attachment.

    @return The type the file. Either `caption` or `file`.
-->
<#function _getFileType file>
    <#if (file["./mimeType/mimeType"].@@text == "text/plain") && file["./mimeType/label"].@@text == "caption">
        <#return "caption">
    <#else>
        <#return "file">
    </#if>
</#function>

<#--  <#function getMimeType file>
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
</#function>  -->



