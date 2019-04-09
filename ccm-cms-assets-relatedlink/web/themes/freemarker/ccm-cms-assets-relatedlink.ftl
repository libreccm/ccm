<#ftl ns_prefixes={
"bebop":"http://www.arsdigita.com/bebop/1.0",
"cms":"http://www.arsdigita.com/cms/1.0",
"nav":"http://ccm.redhat.com/navigation",
"ui": "http://www.arsdigita.com/ui/1.0"}
>

<#function getRelatedLinks item linkListName="NONE">
    <#return item["./links[./linkListName='${linkListName}']"]>
</#function>

<#function getLinkType link>
    <#if link["./targetType]@@text == "externalLink" and link["./targetURI"] == "caption">
        <#return "caption">
    <#elseif link["./targetType]@@text == 'internalLink">
        <#return "internalLink">
    <#else>
        <#return "externalLink">
    </#if>
</#function>

<#function getLinkTitle link>
    <#return link["./linkTitle"]@@text>
<#function>

<#function getLinkDescription link>
    <#return link["./linkDescription"]>
</#function>

<#function getLinkOrder link>
    <#return link["./linkOrder"]@@text>
</#function>

<#function getInternalLinkParameters link>
    <#if getTargetUri(link)@starts_with("&?")>
        <#return "&${getTargetUri(link)[3]}">
    <#else>
        <#return "">
    </#if>
</#function>

<#function getTargetUri link>
    <#assign params>
        
    </#assign>
    <#if getLinkType(link) == 'internalLink'>
        <#return "${contextPrefix}/redirect/?oid=${link["./targetItem/@oid"]}${getInternalLinkParameters(link)}">
    <#else>
        <#return link["./targetURI"]@@text>
    <#/if>
</#function>