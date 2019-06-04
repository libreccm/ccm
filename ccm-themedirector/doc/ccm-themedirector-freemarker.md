# Freemarker functions provided by ccm-themedirectory

## Language utilities

Provides functions for gettign the available languages for the current page.

Import Path
    :`/ccm-themedirector/language.ftl`

### `Sequence<String> getAvailableLanguages()`

Returns the functions in which the primary content of the current page is available.

## Utility functions

Import Path
    :`/ccm-themedirectory/utils.ftl`

### `String getPageApplication()`

Returns the application by which the current page was generated.

### `String getPageTitle()`

Return the title of the current page as provided by the 
_Category Menu_ Component of the _ccm-navigation_ module. 

### `String getSiteHostName()`

Returns the host name of the CCM installation as provided by the 
_SiteBanner_ component of the _ccm-core_ module.

### `String getSiteName()`

Returns the host name of the CCM installation as provided by the 
_SiteBanner_ component of the _ccm-core_ module.

### `String formatDateTime(style: String, date: DateValueNode)`

Formats a date or date-time value according to a pattern defined in the
theme manifest. The pattern must be a valid pattern for the Java `DateTimeFormatter` class (see https://docs.oracle.com/javase/8/docs/api/java/time/format/DateTimeFormatter.html). The definition in the theme manifest is done in the `date-time-formats` section. A definition for a date style looks like this:

    "date-time-formats": [
        ...
        {
            "style": "standard",
            "lang": "de",
            "format":  "dd. MMM YYYY"
        },
        {
            "style": "standard",
            "lang": "en",
            "format": "MM/dd/YY"
        }
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