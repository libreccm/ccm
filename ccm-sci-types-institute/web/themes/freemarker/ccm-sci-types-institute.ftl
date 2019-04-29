<#ftl ns_prefixes={
"bebop":"http://www.arsdigita.com/bebop/1.0",
"cms":"http://www.arsdigita.com/cms/1.0",
"nav":"http://ccm.redhat.com/navigation",
"ui": "http://www.arsdigita.com/ui/1.0"}
>

<#import ../ccm-cms.ftl as CMS>

<#function getDescription data>
    <#if (data["./instituteDesc"]?size > 0)>
        <#return data["./instituteDesc"]>
    <#elseif (data["./description"]?size > 0)>
        <#return data["./description"]>
    </#if>
</#function>

<#function getShortDescription data>
    <#if (data["./instituteShortDescription"])>
        <#return data["./instituteShortDescription"])>
    <#elseif (data["./shortDescription"])>
        <#return data["./shortDescription"])>
    </#if>
</#function>

<#function getDepartments data>
    <#return data["./departments"]>
</#function>

<#function getDepartmentOid department>
    <#return department["./@oid"]>
</#function>

<#function getDepartmentTitle department>
    <#return department["./title"]>
</#function>

<#function getDepartmentLink department>
    <#return CMS.generateContentItemLink(department)>
</#function>


