# Freemarker functions for Sidenote assets

Functions for processing note assets assigned to a content item.

Import path
: `/ccm-cms-assets-notes.ftl`

## `getNotes(item: Node): Sequence<Node>`

Returns the notes assigned to a content item.

## `getContent(item: Node): String

Gets the content of a note. The return value is the HTML content of the node.