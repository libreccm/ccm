# Utility functions

Import Path
    :`/ccm-themedirectory/utils.ftl`

## `getPageApplication(): String`

Return the application of the current page.

## `getPageTitle(): String`

Return the title of the current page as provided by the 
_Category Menu_ Component of the _ccm-navigation_ module. 

## `getSiteHostName(): String`

Returns the host name of the CCM installation as provided by the 
_SiteBanner_ component of the _ccm-core_ module.

## `getSiteName(): String`

Returns the host name of the CCM installation as provided by the 
_SiteBanner_ component of the _ccm-core_ module.

## `getBooleanAttrValue(fromNode: Node, attrName: String): boolean`

A helper function which tries to convert the value of the attribute `attrName` of the node `fromNode` to a boolean. The following values 
are interpreted as true: `true`, `yes`. All other values are interpreted
as `false`.

## `formatDateTime(style: String, date: DateValueNode): String`

Formats the value of date/time value node according to the provided
`style`. The is defined in the theme manifest in the `date-time-formats` 
section. It is possible to define different styles for different languages. 
The style definition in the theme manifest must be in the format expected by the Java 
[DateTimeFormatter](https://docs.oracle.com/javase/8/docs/api/index.html?java/time/format/DateTimeFormatter.html) class.

### Example

In the theme manifest in the following format is defined:

    "date-time-formats": [
        ...
        {
            "style": "news",
            "lang": "de",
            "format": "dd.MM.YYYY"
        },
        {
            "style": "news",
            "lang": "en",
            "format": "MM/dd/YY"
        },
        ...
    ]

Each style must have a name. It is possible to have different patterns
for a style for different languages. The pattern itself is provided by the `format` property. For a documentation of the pattern format 
please refer to the documentation of the Java `DateTimeFormatter`.

The second parameter of these function is a date value, at the moment 
this is an XML node if several attributes providing the year, month etc. of the date. This value is usually provided by special function
for the specific content type. A typical usage of the `formatDateTime`
function looks like this:

    <span>${Utils.formatDateTime("standard", News.getNewsDate(item))}</span>

In this example the `getNewsDate` function was used to retrieve the date of a news.

`News.getNewsDate` gets the date of a news item. If the date of the news is 
2019-04-01 the return value of the function for german is

    01.04.2019

and for english:

    4/1/19