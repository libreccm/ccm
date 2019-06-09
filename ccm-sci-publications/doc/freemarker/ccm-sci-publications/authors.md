# Freemarker functions for processing the authors of a publication item

Import Path
: `/ccm-sci-publications/authors.ftl`

## `getLink(author: Node, keyId: String): String

Gets the link to the homepage of the author from the contact entries 
of the author. The key of the contact entry to use is selected using 
the `keyId` parameter.

## `getId(author: Node): String`

Returns the ID of the author as a string usable as value of the 
`id` attribute of a HTML element. The returned string consists of
the ID of the master version of the author item, and the name of the
author, separated by an underscore.

## `getPosition(author: Node): String`

Returns the position of provided author item in the sequence of authors.

## `isLast(author: Node): boolean

Determines if the provided author is the last author in the sequence of authors.

## `getSurname(author: Node): String`

Gets the surname of the author.

## `getGivenName(author: Node): String`

Gets the given name of the author.

## `isEditor(author: Node): boolean`

Determines if the provided author is an editor.