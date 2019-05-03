### Common functions

#### Language related

Import path: `<#import /language.ftl as Lang>`

##### `getAvailableLanguages`

    Sequence getAvailableLanguages()

Returns the available languages for the current document as sequence. These sequence can be used for creating links for selecting the language:

```
<ul class="language-selector">
    <#list Lang.getAvailableLanguages()?sort as lang>
        <li class="${(lang==negotiatedLanguage)?then('selected', '')}">${lang}</li>
    </#list>
</ul>
```

This example uses the `list` directive from Freemarker to iterate over the available languages returned by `getAvailableLanguages` The Freemarker build-in `?then` is used together with the `negotiatedLanguage` variable to
check if the curent language is the selected language. If this is the case 
a CSS class is added to the HTML. 

#### Basic functions

Import path: `<#import /utils.ftl as Utils>`

##### getPageApplication

    getPageApplication()

Return the application of the current page.

##### getPageTitle

    getPageTitle()

Returns the title of the current page

##### getSiteHostName

    getSiteHostName()

Returns the name of the host serving the site.

##### getSiteName

    getSiteName()

Returns the name of the site.

##### getBooleanAttrValue

    getBooleanAttrValue(fromNode: Node attrName: String)

A helper function which tries to convert the value of the attribute `attrName` of the node `fromNode` to a boolean. The following values 
are interpreted as true: `true`, `yes`. All other values are interpreted
as `false`.

###### Parameters

`fromNode` A XML node

`attrName` The name of attribute to interpret as boolean

###### Returns

A boolean for the value of the attribute. If the attribute is not present 
in the provided node the function returns `false`.

##### formatDateTime

    formatDateTime(style: String date: Node)

Formats the value of date/time value node according to the provided
`style`. The is defined in the theme manifest in the `date-time-formats` 
section. It is possible to define different styles for different languages. 
The style definition in the theme manifest must be in the format expected by the Java 
[DateTimeFormatter](https://docs.oracle.com/javase/8/docs/api/index.html?java/time/format/DateTimeFormatter.html) class.


###### Parameters

`style` A date-time format defined in the theme manifest. The format must be formatted as expected by the [DateTimeFormatter](https://docs.oracle.com/javase/8/docs/api/index.html?java/time/format/DateTimeFormatter.html) class.

`date` The node providing the data of the date to format.

###### Returns

A date formatted as defined in `style`.

###### Examples

In the theme manifest in the following format is defined:

```
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
```

The use this format:

```
Utils.formatDateTime('news', News.getDateTime(item))
```

`News.getDateTime` gets the date of a news item. If the date of the news is 2019-04-01 the return value of the function for german is

    01.04.2019

and for english:

    4/1/19


