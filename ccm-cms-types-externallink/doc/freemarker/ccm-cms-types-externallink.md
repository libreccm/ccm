# Freemarker functions for ExternalLink items

Import path
: `/ccm-cms-types-externallink.ftl`

## `getDescription(item: Node): String`

Gets the description of the external link item.

## `getComment(item: Node): String`

Gets the value of the `comment` property of the link item.

## `isTargetNewWindow(item: Node): String`

Returns `true` if the link should be opened in a new window/tab.

## `getUrl(item: Node): String`

Returns the URL of the external link.