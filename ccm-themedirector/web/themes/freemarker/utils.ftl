<#ftl ns_prefixes={
"bebop":"http://www.arsdigita.com/bebop/1.0",
"cms":"http://www.arsdigita.com/cms/1.0",
"nav":"http://ccm.redhat.com/navigation",
"ui": "http://www.arsdigita.com/ui/1.0"}
>

<#---
    Gets the application of the page served from the model.

    @return The current application.
-->
<#function getPageApplication>
    <#return model["/bebop:page/@application"]>
</#function>

<#---
    Get the title of the current page.

    This will only work of the current page is a navigation page with a category 
    menu.

    @return The title of the current page
-->
<#function getPageTitle>
    <#return model["//nav:categoryMenu/nav:category/@title"]>
</#function>

<#---
    Get the hostname from the sitebanner data.
-->
<#function getSiteHostName>
    <#return model["/bebop:page/ui:siteBanner/@hostname"]>
</#function>

<#---
    Get the name of the site from the sitebanner data.
-->
<#function getSiteName>
    <#return model["/bebop:page/ui:siteBanner/@sitename"]>
</#function>

<#-- 
    Internal function. Not for public use.
    Internal function for coverting several string values like `yes`, `true`, 
    `no` etc. to a boolean value. 
-->
<#function getBooleanAttrValue fromNode attrName>
    <#assign path='@' + attrName>
    <#if (fromNode[path]?size > 0)>
        <#assign value=fromNode[path][0]>
        <#return value?lower_case?switch('true', true, 'false', false, 'yes', true, 'no', false, false)>
    <#else>
        <#return false>
    </#if>
</#function>

<#---
    A wrapper for the `_formatDateTime` function which adds missing numbers.
    `_formatDateTime` uses Java APIs for formatting which don't work well with
    incomplete dates. This function takes a date from the data model and checks
    if a component (year, month, day, hour, minute, second) is missing. If the
    the component is missing the function adds uses a default value of that
    component.

    @param style The date format style from the theme manifest to use.
    @param date the date to format.
-->
<#function formatDateTime style date>
    <#assign year   = (date["./@year"]?size > 0)?then(date["./@year"]?number, 0)>
    <#assign month  = (date["./@month"]?size > 0)?then(date["./@month"]?number, 0)>
    <#assign day    = (date["./@day"]?size > 0)?then(date["./@day"]?number, 0)>
    <#assign hour   = (date["./@hour"]?size > 0)?then(date["./@hour"]?number, 0)>
    <#assign minute = (date["./@minute"]?size > 0)?then(date["./@minute"]?number, 0)>
    <#assign second = (date["./@second"]?size > 0)?then(date["./@second"]?number, 0)>
    <#return _formatDateTime(style, year, month, day, hour, minute, second)>
</#function>