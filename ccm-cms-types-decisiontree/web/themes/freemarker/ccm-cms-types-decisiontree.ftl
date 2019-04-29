<#ftl ns_prefixes={
"bebop":"http://www.arsdigita.com/bebop/1.0",
"cms":"http://www.arsdigita.com/cms/1.0",
"nav":"http://ccm.redhat.com/navigation",
"ui": "http://www.arsdigita.com/ui/1.0"}
>

<#function getCurrentSection item>
    <#return item["./sections[title]/*"]>
</#function>

<#function getParameters item>
    <#return item["./parameters"]>
</#function>

<#function getSectionOid section>
    <#return section["./@oid"]>
</#function>

<#function getSectionUrl section>
    <#return section["./customInfo/@currentURL"]>
</#function>

<#function getSectionTitle section>
    <#return section["./title"]>
</#function>

<#function getSectionInstructions section>
    <#return section["./instructions"]>
</#function>

<#function getSectionOptions section>
    <#return section["./sectionOptions"]>
</#function>

<#function getSectionOptionOid option>
    <#return section["./@oid"]>
</#function>

<#function getSectionOptionParameterName option>
    <#return section["./parameterName"]>
</#function>

<#function getSectionOptionValue option>
    <#return section["./value"]>
</#function>

