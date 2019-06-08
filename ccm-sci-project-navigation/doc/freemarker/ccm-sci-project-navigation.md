# Freemarker functions for SQL project lists

Import path
: `/ccm-sci-project-navigation.ftl`

## `getSciProjectList(listId: String = "itemList"): Node`

Returns an project list. The list can be selected by the optional
`listId` parameter. The default value for the parameter is `itemList`.

## `getProjects(list: Node): Sequence<Node>`

Returns the projects in a project list.

## `getTitleFilterValue(list: Node): String`

Gets the value of the title filter of the provided list.

## `getResearchFieldFilterValue(list: Node): String`

Returns the value of the research field filter of the provided list.

## `getCount(list: Node): String`

Returns the number of projects in the provided list.

## `getCurrentPage(list: Node): String`

Returns the number of the current page of the list.

## `getLimit(list: Node): String`

Gets the maximum number of items per page.

## `getMaxPages(list: Node): String`

Gets the number of page of the provided list.

## `getNextPageLink(list: Node): String`

Gets the link to the next page of the provided list.

## `getPrevPageLink(list: Node): String`

Gets the link to the previous page of the provided list.

## `getOffset(list: Node): String`

Gets the index of the first item on the current page.

## `getProjectItemId(item: Node): String`

Returns the ID of the provided project item.

## `getProjectItemName(item: Node): String`

Returns the name of the provided project item.

## `getProjectItemTitle(item: Node): String`

Returns the value of the `title` property of the provided project item.

## `getProjectItemObjectType(item: Node): String`

Returns the value of the object type of the provided project item.

## `getProjectItemBegin(item: Node): String`

Returns the value of the `begin` property of the provided project item.

## `getProjectItemBeginDay(item: Node): String`

Returns the value of the `day` property of begin date of the provided 
project item.

## `getProjectItemBeginMonth(item: Node): String`

Returns the value of the `month` property of begin date of the 
provided project item.

## `getProjectItemBeginYear(item: Node): String`

Returns the value of the `year` property of begin date of the provided 
project item.

## `getProjectItemEnd(item: Node): String`

Returns the value of the `end` property of the provided project item.

## `getProjectItemEndDay(item: Node): String`

Returns the value of the `day` property of end date of the provided 
project item.

## `getProjectItemEndMonth(item: Node): String`

Returns the value of the `month` property of end date of the 
provided project item.

## `getProjectItemEndYear(item: Node): String`

Returns the value of the `year` property of end date of the provided 
project item.

## `getProjectItemShortDesc(item: Node): String`

Returns the value of the `short-desc` property of the provided project 
item.

## `getProjectItemMembers(item: Node): Sequence<Node>`

Returns the members of the project.

## `getProjectMemberSurname(member: Node): String`

Returns the surname of the provided member.

## `getProjectMemberGivenname(member: Node): String`

Returns the given name of the provided member.
