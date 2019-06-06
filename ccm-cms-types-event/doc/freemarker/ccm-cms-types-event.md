# Freemarker functions for Event items

Import path
: `/ccm-cms-types-event.ftl`

## `getLead(item: Node): String`

Returns the lead text of the event.

## `getMainText(item: Node): String`

Returns the main text of the event.

## `getEndDate(item: Node): DateTimeNode`

Returns the end date of the provided event item. To format the date
the `formatDateTime` function provided by the ccm-cms module should be used.

## `getEndDateYear(item: Node): String`

Returns the year part of the end date of the event.

## `getEndDateMonth(item: Node): String`

Returns the month part of the end date of the event.  

## `getEndDateShortMonth(item: Node): String`

Returns the the short name of month part of the end date of the event. 

## `getEndTime(item: Node): String`

Gets the end time of the event.

## `getEndTimeHour(item: Node): String`

Gets the hour part of the end time of the event.

## `getEndTimeMinute(item: Node): String`

Gets the minute part of the end time of the event.

## `getEndTimeSecond(item: Node): String`

Gets the second part of the end time of the event.

## `getStartDate(item: Node): DateTimeNode`

Returns the start date of the provided event item. To format the date
the `formatDateTime` function provided by the ccm-cms module should be used.

## `getStartDateYear(item: Node): String`

Returns the year part of the start date of the event.

## `getStartDateMonth(item: Node): String`

Returns the month part of the start date of the event.  

## `getStartDateShortMonth(item: Node): String`

Returns the the short name of month part of the start date of the event. 

## `getStartTime(item: Node): String`

Gets the start time of the event.

## `getStartTimeHour(item: Node): String`

Gets the hour part of the start time of the event.

## `getStartTimeMinute(item: Node): String`

Gets the minute part of the start time of the event.

## `getStartTimeSecond(item: Node): String`

Gets the second part of the start time of the event.

## `getLocation(item: Node): String`

Gets the location of the event.

## `getMainContributor(item: Node): String`

Gets the value of the `mainContributor` property of the event.

## `getEventType(item: Node): String`

Returns the value of the `eventType` property of the event.

## `getCost(item: Node): String`

Returns the value of the `cost` property of the event.

## `getMapLink(item: Node): String`

Returns the value of the `mapLink` property of the event.

## `getEventDateAddendum(item: Node): String`

Returns the value of the addendum property of the event.