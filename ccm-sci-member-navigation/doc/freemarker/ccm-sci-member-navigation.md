# Freemaker functions for SQL member lists

Import Path
: `/ccm-sci-member-navigation.ftl`

## `getSciMemberList(listId: String = "itemList"): Node`

Gets the member list. Default is to use the list with the name 
`itemList`. The name can be overridden using the optional `listId`
parameter.

## `getMembers(list: Node): Sequence<Node>`

Returns the members in the provided list.

## `getSurnameFilterValue(list: Node): String`

Gets the value of the surname filter of the provided list.

## `getCount(list: Node): String`

Gets the number of items in the list.

## `getCurrentPage(list: Node): String`

Returns the number of the current page of the list.

## `getLimit(list: Node): String`

Gets the maximum number of items per page.

## `getMaxPages(list: Node): String`

Returns the number of pages.

## `getNextPageLink(list: Node): String`

Returns the link to the next page of the list.

## `getPreviousPageLink(list: Node): String`

Returns the link to the previous page of the list.

## `getOffset(list: Node): String`

Returns the index of the items shown.

## `getMemberItemId(item: Node): String`

Returns the ID of the provided member item.

## `getMemberItemName(item: Node): String`

Returns the name of the provided member item.

## `getMemberItemTitle(item: Node): String`

Returns the value of the title property of the provided member item.

## `getMemberItemSurname(item: Node): String`

Returns the value of the surname property of the provided member item.

## `getMemberItemGivenName(item: Node): String`

Returns the value of the given name property of the provided member 
item.

## `getMemberItemTitlePre(item: Node): String`

Returns the value of the `titlePre` property of the provided member 
item.

## `getMemberItemTitlePost(item: Node): String`

Returns the value of the `titlePost` property of the provided member 
item.

## `getMemberItemCotactEntries(item: Node): Sequence<Node>`

Gets the contact entries of the provided member item.


