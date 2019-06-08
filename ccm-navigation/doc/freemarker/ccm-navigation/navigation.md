# Freemarker functions for ccm-navigation

Import Path
: `/ccm-navigation/navigation.ftl`

## `getCategoryPath(): Sequence<Node>`

Returns the path of the current category.

## `isRootPage(): boolean`

Determines if the current page is a root page (the current category has no parent).

## `getSelectedCategory(): Node`

Returns the current category.

## `getSelectedCategoryId(): String`

Returns the ID of the current category.

## `getCategoryTitle(category: Node): String`

Returns the title of the provided category.

## `getCategoryUrl(category: Node): String`

Returns the URL of the provided category.

## `getCategoryId(category: Node): String`

Returns the ID of the provided category.

## `isCategorySelected(category: Node): String`

Determines if the provided category is the current category.

## `getNavigationRootUrl(navigationId: String = "categoryMenu"): String`

Gets the URL for the root of the category system. 

## `getNavigationTitle(navigationId: String = "categoryMenu"): String`

Gets the title of the category system.

## `getCategoryMenu(menuId: String = "categoryMenu"): String`

Returns the root categories of the category menu. If the current page
has more than on category menu component or the name of the category 
menu component is not `categoryMenu` the optional `menuId` parameter can be used to select the category menu component to use.

## `getCategoryHierachy(hierachyId: String = "categoryNav"): String`

Returns the root categories of the category hierarchy. If the current 
page has more than on category menu component or the name of the 
category menu component is not `categoryNav` the optional 
`hierachyId` parameter can be used to select the category menu 
component to use.

## `getSubCategories(ofCategory: Node): Sequence<Node>`

Returns the sub categories of the provided category.

## `getSubCategoriesOfCategoryWithId(categoryId: String): Sequence<Node>`

Returns the sub categories of the ID with the provided ID. The 
category must be available in one of the category menu components of 
the page.

## `getGreetingItem(): Node`

Returns the greeting/index item of the current category. The functions 
for proceesing content items can be used to process the returned value
further.



