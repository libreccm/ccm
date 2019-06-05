# Freemarker functions for related links

Import path
: `/ccm-cms-assets-relatedlinks`

Functions for processing the related links assigned to a content item.

## `getRelatedLinks(item: Node, linkListName: String = "NONE"): Sequence<Node>`

Retrieves the related links assigned to a content item. Related links can be organized in named list. The optional parameters `linkListName` controls which list is used. If the parameter is omitted the default value `NONE` is used.

## `getLinkType(link: Node): String`

Gets the type of the provided link which can either be `externalLink`,
`internalLink` or `caption`.

## `getLinkTitle(link: Node): String`

Gets the title of the provided link.

## `getLinkDescription(link: Node): String`

Gets the description of the provided link.

## `getLinkOrder(link: Node): String`

Gets the order value for the provided link.

## `getInternalLinkParameters(link: Node): String`

Gets the URL parameters of the of the provided link (The part after 
the question mark).

## `getTargetUri(link: Node`): String

Gets the URI of the target of the provided link.

