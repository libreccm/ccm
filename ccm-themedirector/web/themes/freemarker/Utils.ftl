<#ftl ns_prefixes={
"bebop":"http://www.arsdigita.com/bebop/1.0",
"cms":"http://www.arsdigita.com/cms/1.0",
"nav":"http://ccm.redhat.com/navigation",
"ui": "http://www.arsdigita.com/ui/1.0"}
>

<#-- Move to Navigation macros/functions? -->
<#macro pageTitle>
    ${model["//nav:categoryMenu/nav:category/@title"]}
</#macro>

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