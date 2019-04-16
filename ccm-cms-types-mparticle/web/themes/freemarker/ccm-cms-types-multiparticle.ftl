<#ftl ns_prefixes={
"bebop":"http://www.arsdigita.com/bebop/1.0",
"cms":"http://www.arsdigita.com/cms/1.0",
"nav":"http://ccm.redhat.com/navigation",
"ui": "http://www.arsdigita.com/ui/1.0"}
>

<#function getSummary item>
    <#if (item["./summary"]?size > 0)>
        <#return item["./summary"]>
    <#elseif (item["./nav:attribute[@name = 'summary']"]?size > 0)>
        <#return item["./nav:attribute[@name = 'summary']"]>
    <#else>
        <#return "">
    </#if>
</#function>