<#ftl ns_prefixes={
"bebop":"http://www.arsdigita.com/bebop/1.0",
"cms":"http://www.arsdigita.com/cms/1.0",
"nav":"http://ccm.redhat.com/navigation",
"ui": "http://www.arsdigita.com/ui/1.0"}
>

<#function getDescription item>
    <#if (item["./description"]?size > 0)>
        <#return item["./description"]>
    <#else if(item["./nav:attribute[@name='description']"]?size > 0)>
        <#return item["./nav:attribute[@name='description']"]>
    </#if>
</#function>

<#function getLink item>
    <#if (item["./url"]?size > 0)>
        <#return item["./url"]>
    <#else if(item["./nav:attribute[@name = 'url']"]?size > 0)>
        <#return item["./nav:attribute[@name = 'url']"]>
    </#if>
</#function>

