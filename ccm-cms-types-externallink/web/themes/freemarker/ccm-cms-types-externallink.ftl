<#ftl ns_prefixes={
"bebop":"http://www.arsdigita.com/bebop/1.0",
"cms":"http://www.arsdigita.com/cms/1.0",
"nav":"http://ccm.redhat.com/navigation",
"ui": "http://www.arsdigita.com/ui/1.0"}
>

<#function getDescription item>
    <#return item["./pageDescription"]>
</#function>

<#function getComment item>
    <#return item["./comment"]>
</#function>

<#function isTargetNewWindow item>
    <#return item["./targetNewWindows"] == "true">
</#function>

<#function getUrl item>
    <#return item["./url]">
</#function>