# Freemarker function for ObjectLists

Import Path
: `/ccm-navigation/object-list.ftl`

Many functions provided by this library have a parameter `listId`. 
In most cases the value is `itemList`.

## `getItems(listId: String): Sequence<Node>`

Returns the items in the object list with the provided ID.

## `getObjectCount(listId: String): number`

Returns then number of objects in the object list with the provided ID.

## `getPagniatorBaseUrl(listId: String): String`

Gets the base URL of the list paginator.

## `getPaginatorBegin(listId: String): Number`

Returns the index of the first item displayed.

## `getPaginatorEnd(listId: String): Number`

Returns the index of the last item displayed.

## `getPageCount(listId: String): Number`

Gets the number of pages of the object list with the provided ID.

## `getPageNumber(listId: String): Number`

Gets the number of page displayed.

## `getPageParam(listId: String): String`

Gets the name of the page param for the object list with the provided 
ID.

## `getPageSize(listId: String): Number`

Gets the number of objects per page for the object list with the 
provided ID.

## `getPrevPageLink(listId: String): String`

Gets the link to the previous page of the object list with the 
provided ID.

## `getNextPageLink(listId: String): String`

Gets the link to the next page of the object list with the provided 
ID.

## `getFirstPageLink(listId: String): String`

Gets the link to the first page of the object list with the provided 
ID.

## `getLastPageLink(listId: String): String`

Gets the link to the last page of the object list with the provided 
ID.

## `getItemTitle(item: Node): String`

Gets the title of a list item.

## `getItemLead(item: Node): String`

Gets the lead text of a list item.

## `getItemProperty(item: Node, property: String): String`

A generic function the get the value of the property with the name
provided the `property` parameter.

## `hasImage(item: Node): boolean`

Determines if the provided list item has an image attachment.

## `getImageId(item: Node): String`

Gets the ID of the image attachment of the provided list item.

## `getImageUrl(item: Node): String`

Gets the URL of the image attachment of the provided list item.

## `getImageCaption(item: Node): String`

Gets the caption of the image attachment of the provided list item.

## `getFilters(listId: String): Sequence<Node>`

Returns the filters for the current list.

## `getFilterLabel(filter: Node): String`

Gets the label of the provided filter.

## `getFilterType(filter: Node): String`

Gets the type of the provided filter.

## `getSelectFilterOptions(filter: Node): Sequence<Node>`

Returns the options of the select filter. If the provided filter
is not a filter of the type _select_ an empty sequence is returned.

## `getSelectFilterOptionLabel(option: Node): String`

Returns the label of the provided filter.

## `getCategoryFilterSearchString(filter: Node): String`

Returns the search string for the provided category filter.

## `getCategoryFilterSeparator(filter: Node): String`

Gets the separation character for the value of the provided
category filter.

## `getCategoryFilterMultiple(filter: Node): boolean`

Determines if the provided category allows multiple selections.

## `getCategoryFilterCategories(filter: Node): Sequence<Node>`

Returns the categories for the provided category filter.

## `getCategoryFilterCategoryGroups(filter: Node): Sequence<Node>`

Returns the category groups of the provided category filter.

## `getCategoryGroupLabel(group: Node): String`

Returns the label of the provided category group.

## `getCategoryFilterCategoryGroupsCategories(groups: Sequence<Node>): Sequence<Node>`

Gets the categories of all category groups.

## `getCategoryFilterCategoryId(category: Node): String`

Gets the ID of the provided category of a category filter.

## `getCategoryFilterCategoryLabel(category: Node): String`

Gets the label of the provided category of a category filter.





