# Freemarker functions for generating the exportLink links of an publication item

Import Path
: `/ccm-sci-publications/export-links.ftl`

## `getHref(exportLink: Node): String`

Returns the URL for for the provided export link.

## `getFormatKey(exportLink: Node): String`

Gets the key of the format provided by the export link provided by the
`exportLink` parameter.

## `getFormatName(exportLink: Node): String`

Gets the name of the format provided by the export link provided by the
`exportLink` parameter.