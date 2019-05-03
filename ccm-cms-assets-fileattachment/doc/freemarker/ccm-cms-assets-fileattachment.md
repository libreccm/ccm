### ccm-cms-assets-fileattachment

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

#### getFileAttachments

    getFileAttachments(item: ContentItemNode): Sequence

Return the file attachments of a content items

##### Parameters

`item` The content item from which the file attachments are retrieved.

##### Returns

A sequence of the file attachments of the provided content item.

#### getFileType

    getFileType(file)

Returns the type of the file attachments which is either `caption` or `file`.

##### Parameters

`file` The file attachment

##### Returns

The type of the file attachment.

#### getMimeType

    getMimeType(file)

Returns the mime type of the file.

##### Parameters

`file` The file 

##### Returns

The mime type of the file.

#### getMimeTypeFileExtension

    getMimeTypeFileExtension(file)

Returns the usual file extension for the mime type of the file.

##### Parameters

`file` The file

##### Returns

The usual file extension for the mime type of the file.

#### getFileSize

    getFileSize(file unit="byte")

Returns the size of the file in the provided unit.

##### Parameters

`file`The file

`unit` Optional parameter for unit in which the size is returned. Default 
value is `byte`. Supported values are `byte`, `kB` `KiB`, `MB` and `MiB`. 
All other values are interpreted as `byte`.

##### Returns 

The size of the file in the provided unit.


#### getFileId

    getFileId(file)

Returns the ID of the file.

##### Parameters

`file` The file

##### Returns

The ID of the file.

#### getFileName

    getFileName(file)

Returns the name of file.

##### Parameters 

`file` The file

##### Returns

The name of the file.

#### getFileDescription

    getFileDescription(file)

Returns the name of file.

##### Parameters 

`file` The file

##### Returns

The description of the file.

#### getFileUrl

    getFileUrl(file)

Returns the name of file.

##### Parameters 

`file` The file

##### Returns

The URL of the file.
