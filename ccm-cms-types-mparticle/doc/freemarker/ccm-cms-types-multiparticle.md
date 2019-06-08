# Freemarker functions for MultiPartArticles

Import Path
: `/ccm-cms-types-multiparticle.ftl`

## `getSummary(item: Node): String`

Returns the summary of the provided MultiPartArticle item.

## `getSections(item: Node): Sequence<Node>`

Returns the currently displayed sections of the multipart article.
The sections can be processed further by some of the other funtions 
provided by this library.

## `getSectionTitle(section: Node): String`

Returns title title of the provided section.

## `getSectionTitle(section: Node): HtmlString`

Gets the content of the provided section.

## `getSectionRank(section: Node): String`

Gets the value of the `rank` property of the provided section.

## `getPageNumber(item: Node): String`

Gets the number of the page of the multipart article displayed.

## `getNumberOfPages(item: Node): String`

Returns the number of pages of the provided multipart article.

## `hasPreviousPage(item: Node): boolean`

Returns `true` if the provided multipart article has previous pages.

## `hasPreviousPage(item: Node): boolean`

Returns `true` if the provided multipart article has more pages.

## `hasMultiplePages(item: Node): boolean`

Returns `true` if the provided multipart article has more than one page.

## `getLinkToPreviousPage(item: Node): String`

Returns the link to the previous page if any.

## `getLinkToNextPage(item: Node): String`

Returns the link to the next page if any.

## `getAllSectionsLink(item: Node): String`

Returns the link for showing the complete multipart article on a single page.