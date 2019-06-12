# Creating themes for LibreCCM with Freemarker

Starting with version 2.5 the LibreCCM platform support 
[Freemarker](https://freemarker.apache.org) as an alternative to XSL. 
Freemarker is a project of the Apache Foundation and a well known and 
mature template engine for Java. The support for Freemarker in version 2.5 
is a backport from the upcoming version 7 of the LibreCCM platform. 

Compared to XSL Freemarker is a lot easier to use, especially if you have
worked with other template engines like Twig, Velocity etc before. In 
version 7 of the LibreCCM platform Freemarker will be become the primary 
template  engine. XSL will still be supported, but we recommanded that you 
port your themes to Freemarker. Why Freemarker and not one of the other 
template  engines? Freemarker is able to process XML in a 
[similar way than XSL](https://freemarker.apache.org/docs/xgui.html). 

Freemarker also allows it to define 
[user defined directivies](https://freemarker.apache.org/docs/dgui_misc_userdefdir.html)
and [functions](https://freemarker.apache.org/docs/ref_directive_function.html).
To make it easier to create impressive themes we provide functions and
macros for Freemarker we provide several functions and macros which hide the
complexity of the XML data model created by CCM from the template author. 
It is recommanded not to access the XML data model directly. Instead the 
provided functions should be used. Otherwise your theme might brake when 
the XML structure changes. 

## General structure of a Freemarker theme

Freemarker themes have a different structure than the usual "old style" themes 
of LibreCCM. Each Freemaker based theme must have a theme manifest file
called `theme.json` on the root of its directory structure. This file
provides several informations for processing the theme and serves as a
central configuration point. The file must have the following structure:

    {
        "name": "an-example-theme",
        "templates": {
            "applications": [
                {
                    "application-name": "someApp",
                    "application-class": "somePageClass",
                    "template": "/templates/someApp.html.ftl"
                },
                ...                
            ],
            "contentitems": [
                {
                    "view": "list",
                    "contentType": "com.arsdigita.cms.contenttypes.Article",
                    "template": "/templates/contentitems/list/article.html.ftl"
                },
                {
                    "view": "details",
                    "contentType": "com.arsdigita.cms.contenttypes.Article",
                    "template": "/templates/contentitems/detail/article.html.ftl"
                },
                {
                    "view": "details",                    
                    "contentType": "com.arsdigita.cms.contenttypes.Article",
                    "style": "fancy",
                    "template": "/templates/contentitems/detail/article.html.ftl"
                },
                ...
            ],
            "default-application-template": "/templates/default-layout.html.ftl",
            "default-contentitem-template": "/templates/contentitems/detail/default.html.ftl",
        },
        "data-time-formats": [
            {
                "style": "event",
                "lang": "de",
                "format": "dd. MMM. YYYY"
            },
            {
                "style": "event",
                "lang": "en",
                "format": "MM/dd/YY"
            },
            ...
        ]
    }

The `name` key defines the name of the theme which should be unique per
installation. The `templates` contains several subkeys which define the 
templates to use.

The objects in the array of the `applications` key specify which template is
used for an application. To determine which template should be used the 
attributes `application` and and `class` of from the root element of the
XML data model are used. The value of the `application` attribute is matched
against the value for the `application-name` field, the value of the class 
attribute against the value of the `application-class` field.

CCM will first try to find a a template definition 
which matches both the `class` and the `application`. If none is found 
a template definition which a matching `application-name` and an empty 
`application-class` will be looked up. If there is no match the template defined
as `default-application-template` is used. 

There is one special value value for the `template` field: `XSL_FALLBACK.XSL`. 
If the `template` field is set to this value CCM will fallback to the old XSL 
themes.

For content items the procedure is similar, only the names of the fields in the 
template definition differ. The `view` field is used internally to select either
the detail or the list view. The `contentType` field is the content types of the
content items. The third field `style` is optional and can be used to select
different templates depending on the theme context. For more informations please
refer to the documentation of the `contentItem` macro provided by the ccm-cms 
module.

The `date-time-formats` section is used to define date-time formats to use
with the `formatDateTime` function provided by `ccm-themedirector`.

## Predefined variables and functions

Several variables and functions are predefined and available without 
importing another file. 

### Variables

#### `contextPath`

The context path in which CCM is running.

#### `contextPrefix`

The context prefix.

#### `dispatcherPrefix`

Prefix for the CCM dispatcher (usually `/ccm`)

#### `host`

The current host.

#### `model`

The XML document created by LibreCCM.

#### `negotiatedLanguage`

The language negoiated between the user agent and LibreCCM.

#### `requestScheme`

The protocol (http or https).

#### `selectedLanguage`

The language selected by the user.

#### `themePrefix`

The prefix of the theme. Only available if a development theme is viewed.

### Functions

#### `getLocalizedText`

```
String getLocalizedText(String key)
```

Returns the localized text from the resource bundle of the theme. 

#### `getContentItemTemplate` 

```
String getContentItemTemplate(String objectType, view="DETAIL", style="")
```

This is an internal function!

Returns the path for the template of a content item of a specific type. 

#### `_formatDateTime`

An internal functions date time formatting. This functions should not be
used directly.

## Functions and Macros



