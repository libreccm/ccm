<#ftl ns_prefixes={
"bebop":"http://www.arsdigita.com/bebop/1.0",
"cms":"http://www.arsdigita.com/cms/1.0",
"nav":"http://ccm.redhat.com/navigation",
"ui": "http://www.arsdigita.com/ui/1.0"}
>

<#function getRelatedLinks item linkListName="NONE">

    <#return item["./links[./linkListName='${linkListName}']"]?map(
        link -> {
            "linkType": _getLinkType(link),
            "title": link["./linkTitle"].@@text,
            "description": link["./linkDescription"].@@text, 
            "linkOrder": link["./linkOrder"]?number,
            "targetUri": _getTargetUri(link)
        })?sort_by("linkOrder")>

    <#--  <#return item["./links[./linkListName='${linkListName}']"]?sort_by("linkOrder")>  -->
</#function>

<#function _getLinkType link>
    <#if (link["./targetType"].@@text == "externalLink" && link["./targetURI"] == "caption")>
        <#return "caption">
    <#elseif (link["./targetType"].@@text == "internalLink")>
        <#return "internalLink">
    <#else>
        <#return "externalLink">
    </#if>
</#function>

<#--  <#function getLinkTitle link>
    <#return link["./linkTitle"].@@text>
</#function>

<#function getLinkDescription link>
    <#return link["./linkDescription"].@@text>
</#function>

<#function getLinkOrder link>
    <#return link["./linkOrder"].@@text>
</#function>  -->

<#function _getInternalLinkParameters link>
    <#if (_getLinkType(link) == "caption")>
        <#return "">
    <#elseif (link["./targetURI"]?size > 0)>
        <#assign targetUri=link["./targetURI"].@@text>
        <#if (targetUri@starts_with("&?"))>
            <#return "&${targetUri[3]}">
        <#else>
            <#return "">
        </#if>
    <#else>
        <#return "">
    </#if>
</#function>

<#function _getTargetUri link>
    <#if (_getLinkType(link) == "caption")>
        <#return "">
    <#elseif (_getLinkType(link) == "internalLink")>
        <#return "${contextPrefix}/redirect/?oid=${link['./targetItem/@oid']}${_getInternalLinkParameters(link)}">
    <#else>
        <#return link["./targetURI"].@@text>
    </#if>
</#function>