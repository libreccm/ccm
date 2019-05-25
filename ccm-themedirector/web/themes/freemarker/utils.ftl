<#ftl ns_prefixes={
"bebop":"http://www.arsdigita.com/bebop/1.0",
"cms":"http://www.arsdigita.com/cms/1.0",
"nav":"http://ccm.redhat.com/navigation",
"ui": "http://www.arsdigita.com/ui/1.0"}
>

<#--
    Gets the application of the page served from the model
-->
<#function getPageApplication>
    <#return model["/bebop:page/@application"]>
</#function>

<#function getPageTitle>
    <#return model["//nav:categoryMenu/nav:category/@title"]>
</#function>

<#function getSiteHostName>
    <#return model["/bebop:page/ui:siteBanner/@hostname"]>
</#function>

<#function getSiteName>
    <#return model["/bebop:page/ui:siteBanner/@sitename"]>
</#function>

<#function getBooleanAttrValue fromNode attrName>
    <#assign path='@' + attrName>
    <#if (fromNode[path]?size > 0)>
        <#assign value=fromNode[path][0]>
        <#return value?lower_case?switch('true', true, 'false', false, 'yes', true, 'no', false, false)>
    <#else>
        <#return false>
    </#if>
</#function>

<#function formatDateTime style date>
    <#assign year   = (date["./@year"]?size > 0)?then(date["./@year"]?number, 0)>
    <#assign month  = (date["./@month"]?size > 0)?then(date["./@month"]?number + 1, 0)>
    <#assign day    = (date["./@day"]?size > 0)?then(date["./@day"]?number, 0)>
    <#assign hour   = (date["./@hour"]?size > 0)?then(date["./@hour"]?number, 0)>
    <#assign minute = (date["./@minute"]?size > 0)?then(date["./@minute"]?number, 0)>
    <#assign second = (date["./@second"]?size > 0)?then(date["./@second"]?number, 0)>
    <#return _formatDateTime(style, year, month, day, hour, minute, second)>
</#function>