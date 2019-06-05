# Functions for File Attachments

Import Path
: `/ccm-cms-assets-fileattachments.ftl`

This module provides functions for dealing with file attachments. A
possible usage these functions:

```
<#list FileAttachments.getFileAttachments(item)>
    <div class="file-attachments">

        <h2>
            ${getLocalizedText("layout.page.main.fileAttachments")}
        </h2>

        <ul class="file-attachments">
            <#items as file>
                <#if FileAttachments.getFileType(file) == "caption">
                    <li class="caption">
                    <strong>${FileAttachments.getFileName(file)}</strong>
                    <p>
                        ${FileAttachments.getFileDescription(file)}
                    </p>
                    </li>
                <#else>
                    <li class="file-attachment">
                        <a href="${FileAttachments.getFileUrl(file)}">
                            <span class="fa fa-download"></span>
                            ${FileAttachments.getFileDescription(file)} 
                            (${FileAttachments.getMimeTypeFileExtension(file)},
                            ${FileAttachments.getFileSize(file, "KiB")} KB)                                      
                            
                        </a>
                    </li>
                </#if>
            </#items>
        </ul>
    </div>
</#list>
```

## `getFileAttachments(item: Node): Sequence<Node>`

Retrieves the file attachments of the provided content item.

## `getFileType(file: Node): String`

Returns the type of the file attachments which is either `caption` or `file`.

## `getMimeType(file: Node): String`

Returns the mime type of the file, for example `image/png` or `application/pdf`.

## `getMimeTypeFileExtension(file: Node): String`
    
Returns the usual file extension for the mime type of the file.

## `getFileSize(file: Node, unit: String = "byte"): Number`

Returns the size of the provided file. The unit in which the size of the file is returned can be changed by using the optional parameter `unit`. The default value for the unit is `byte`.

## `getFileId(file: Node): String`

Returns the ID of the file.

## `getFileName(file: Node): String`

Returns the name of file.

## `getFileDescription(file: Node): String`

Returns the description of the file.

#### `getFileUrl(file: Node): String`

Returns the URL of the file.
