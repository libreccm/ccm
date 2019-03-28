<#ftl ns_prefixes={
"bebop":"http://www.arsdigita.com/bebop/1.0",
"cms":"http://www.arsdigita.com/cms/1.0",
"nav":"http://ccm.redhat.com/navigation",
"ui": "http://www.arsdigita.com/ui/1.0"}
>

<#--
    Outputs the lead text the provided article.
    
    Disabled 2019-03-25, remove 2019-04-07 if this macro is not required
-->
<#--  <#macro lead item>
    <#if (item["./lead"]?size > 0)> 
        ${item["./lead"]}
    <#elseif (item["./nav:attribute[@name='lead']"]?size > 0)>
        ${item["./nav:attribute[@name='lead']"]}
    </#if>
</#macro>  -->

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
    Output the main text of the provided article.

    Disabled 2019-03-25, remove 2019-04-07 if this macro is not required
-->
<#--  <#macro mainText item>
    <#if (item["./textAsset"]?size > 0)>
        ${item["./textAsset/content"]}
    </#if>
</#macro>  -->

<#--
    Gets the main text the the provided article.
-->
<#function getMainText item>
    <#if (item["./textAsset"]?size > 0)>
        <#return item["./textAsset/content"]>
    </#if>
</#function>