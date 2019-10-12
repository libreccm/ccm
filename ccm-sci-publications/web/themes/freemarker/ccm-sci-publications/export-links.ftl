<#ftl ns_prefixes={
"bebop":"http://www.arsdigita.com/bebop/1.0",
"cms":"http://www.arsdigita.com/cms/1.0",
"nav":"http://ccm.redhat.com/navigation",
"ui": "http://www.arsdigita.com/ui/1.0"}
>

<#function getExportLinks item>
    <#return item["./publicationExportLink"]>
</#function>

<#function hasExportLinks item>
    <#return (item["./publicationExportLink"]?size > 0)>
</#function>

<#function getHref exportLink>
    <#return "${dispatcherPrefix}/scipublications/export/?format=${exportLink['./formatKey']}&publication=${exportLink['./publicationId']}">
</#function>

<#function getFormatKey exportLink>
    <#return exportLink["./formatKey"]>
</#function>

<#function getFormatName exportLink>
    <#return exportLink["./formatName"]>
</#function>

