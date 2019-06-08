# Freemarker functions for News items

Import Path
: `/ccm-cms-types-newsitem.ftl`

## `getLead(item: Node): String`

Returns the lead text of the provided news item.

## `getMainText(item: Node): HtmlString`

Returns the main text of the news item.

## `getNewsDate(item: Node): Node` 

Returns the date of the news. For formatting the date the `formatDateTime` function from the `utils.ftl` library can be used.

## `getNewsDateYear(item: Node): String`

Gets the value of the `year` property of the news date.

## `getNewsDateMonth(item: Node): String`

Gets the value of the `month` property of the news date.

## `getNewsDateDay(item: Node): String`

Gets the value of the `Day` property of the news date.

## `getNewsDateDayNameShort(item: Node): String`

Gets the value of the `year` property of the news date as short day name.

## `newsDateHour(item: Node): String`

Gets the value of the `hour` property of the news date.

## `newsDateMinute(item: Node): String`

Gets the value of the `minute` property of the news date.

## `newsDateSecond(item: Node): String`

Gets the value of the `second` property of the news date.

## `newsDateIso(item: Node): String`

Gets the date of the news as ISO date.