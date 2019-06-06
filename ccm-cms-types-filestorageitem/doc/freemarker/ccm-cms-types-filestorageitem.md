# Freemarker functions for File Storage Items

Import path
: `/ccm-cms-types-filestorageitem.ftl`

## `getDescription(item: Node): String`

Gets the description of the file storage item.

## `getFileId(item: Node): String`

Returns the ID for the file represented by the file storage item.

## `getFileName(item: Node): String`

Returns the name of the file represented by the file storage item.

## `getFileLink(item: Node, mode: String="download", useFileName: boolean = true): String`

Returns the link for downloading or viewing the file. The optional 
parameter `mode` controls if the link for downloading or for viewing 
the file is generated. The supported values are `download` (default 
value) and `stream`. Unknown values are interpreted as `download`. 

The optional `useFileName` parameter controls if the name of the file (see `getFileName`) is included in the link. The default value is `true`.