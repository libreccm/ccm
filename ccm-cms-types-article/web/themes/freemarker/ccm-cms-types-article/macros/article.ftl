<#ftl ns_prefixes={
"bebop":"http://www.arsdigita.com/bebop/1.0",
"cms":"http://www.arsdigita.com/cms/1.0",
"nav":"http://ccm.redhat.com/navigation",
"ui": "http://www.arsdigita.com/ui/1.0"}
>

<#--
    Outputs the lead text the provided article.
-->
<#macro lead item>
    <#if (item["./lead"]?size > 0)> 
        ${item["./lead"]}
    <#elseif (item["./nav:attribute[@name='lead']"]?size > 0)>
        ${item["./nav:attribute[@name='lead']"]}
    </#if>
</#macro>

<#--
    Output the main text of the provided article.
-->
<#macro main item>
    <#if (item["./textAsset"]?size > 0)>
        ${item["./textAsset/content"]}
    </#if>
</#macro>