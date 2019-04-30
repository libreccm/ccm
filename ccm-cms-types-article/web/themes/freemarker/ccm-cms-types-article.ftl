<#ftl ns_prefixes={
"bebop":"http://www.arsdigita.com/bebop/1.0",
"cms":"http://www.arsdigita.com/cms/1.0",
"nav":"http://ccm.redhat.com/navigation",
"ui": "http://www.arsdigita.com/ui/1.0"}
>

<#--
    Gets the lead text of the provided article.
-->
<#function getLead item>
    <#if (item["./lead"]?size > 0)> 
        <#return item["./lead"]>
    <#elseif (item["./nav:attribute[@name='lead']"]?size > 0)>
        <#return item["./nav:attribute[@name='lead']"]>
    </#if>
</#function>

<#--
    Gets the main text the the provided article.
-->
<#function getMainText item>
    <#if (item["./textAsset"]?size > 0)>
        <#return item["./textAsset/content"]>
    </#if>
</#function>