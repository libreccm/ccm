<#ftl ns_prefixes={
"bebop":"http://www.arsdigita.com/bebop/1.0",
"cms":"http://www.arsdigita.com/cms/1.0",
"nav":"http://ccm.redhat.com/navigation",
"ui": "http://www.arsdigita.com/ui/1.0"}
>

<#--filedoc
    Functions for processing article items.
-->

<#--doc
    Gets the lead text of the provided article.

    @param item The article item to use.

    @return The lead text of the article.
-->
<#function getLead item>
    <#if (item["./lead"]?size > 0)> 
        <#return item["./lead"]>
    <#elseif (item["./nav:attribute[@name='lead']"]?size > 0)>
        <#return item["./nav:attribute[@name='lead']"]>
    </#if>
</#function>

<#--doc
    Checks if the provided item has a lead property.

    @param item The article item to use.

    @return `true` If the provided article has a lead text, `false` otherwise.
-->
<#function hasLead item>
    <#return (item["./lead"]?size > 0 || item["./nav:attribute[@name='lead']"]?size > 0)>
</#function>

<#--doc
    Gets the main text the the provided article.

    @param item The article item to use.

    @return The main text of the article.
-->
<#function getMainText item>
    <#if (item["./textAsset"]?size > 0)>
        <#return item["./textAsset/content"]>
    </#if>
</#function>