<#ftl ns_prefixes={
"bebop":"http://www.arsdigita.com/bebop/1.0",
"cms":"http://www.arsdigita.com/cms/1.0",
"nav":"http://ccm.redhat.com/navigation",
"ui": "http://www.arsdigita.com/ui/1.0"}
>

<#--filedoc
    Functions for processing related links.
-->

<#--doc
    Generates a sorted sequence of hashes (see Freemarker documentation) 
    from the related links of a content item.

    @param item The model of the content item to use.

    @param linkListName: The name of the link list to use.

    @return A sorted sequence of hashes. Each hash provides the following keys:
    * `linkType`: The type of the link. Either `externalLink`, `internalLink` or `caption`.
    * `title`: The title of the link.
    * `description`: The description of the link.
    * `linkOrder`: The sort of the link.
    * `targetUri`: The URL of the link.
-->
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

<#--doc
    *Internal* function for determing the type a related link.

    @param link The link

    @return The type of the link. Either `externalLink`, `internalLink` or `caption`.
-->
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

<#--doc
    *Internal* function for getting parameters for the link.

    @param link The link

    @return Parameters for an internal link.
-->
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

<#--doc
    *Internal* function for constructing the target URI of a related link.

    @param link The link model to use.

    @return The URL for the target of the link.
-->
<#function _getTargetUri link>
    <#if (_getLinkType(link) == "caption")>
        <#return "">
    <#elseif (_getLinkType(link) == "internalLink")>
        <#return "${contextPrefix}/redirect/?oid=${link['./targetItem/@oid']}${_getInternalLinkParameters(link)}">
    <#else>
        <#return link["./targetURI"].@@text>
    </#if>
</#function>