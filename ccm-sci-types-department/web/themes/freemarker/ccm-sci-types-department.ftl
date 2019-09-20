<#ftl ns_prefixes={
"bebop":"http://www.arsdigita.com/bebop/1.0",
"cms":"http://www.arsdigita.com/cms/1.0",
"nav":"http://ccm.redhat.com/navigation",
"ui": "http://www.arsdigita.com/ui/1.0"}
>

<#import "/ccm-cms/content-item.ftl" as ContentItem>

<#function getDescription data>
    <#if (data["./departmentDesc"]?size > 0)>
        <#return data["./departmentDescription"]>
    <#elseif (data["./description"]?size > 0)>
        <#return data["./description"]>
    </#if>
</#function>

<#function getShortDescription data>
    <#if (data["./departmentShortDescription"]?size > 0)>
        <#return data["./departmentShortDescription"]>
    <#elseif (data["./shortDescription"]?size > 0)>
        <#return data["./shortDescription"]>
    <#elseif (data["./shortDesc"]?size > 0)>
        <#return data["./shortDesc"]>
    <#elseif (data["./nav:attribute[@name='departmentShortDescription']"]?size > 0)>
        <#return data["./nav:attribute[@name='departmentShortDescription']"]>
    </#if>
</#function>

<#function getDepartmentHeads data>
    <#return data["./heads/head"]>
</#function>

<#function getDepartmentHeadId head>
    <#return data["./masterVersion/id"] + "_" + data["./@name"]>
</#function>

<#function getDepartmentHeadLink head>
    <#return ContentItem.generateContentItemLink(head)>
</#function>

<#function getDepartmentViceHeads data>
    <#return data["./viceheads/vicehead"]>
</#function>

<#function getDepartmentViceHeadId head>
    <#return head["./masterVersion/id"] + "_" + data["./@name"]>
</#function>

<#function getDepartmentViceHeadLink head>
    <#return CMS.generateContentItemLink(head)>
</#function>

<#function getSecretariats data>
    <#return data["./secretariats/secretariat"]>
</#function>

<#function getSecretariatsId sec>
    <#return sec["./masterVersion/id"] + "_" + data["./@name"]>
</#function>

<#function getSecretariatsLink sec>
    <#return ContentItem.generateContentItemLink(sec)>
</#function>

<#function getMembers data>
    <#return data["./member"]>
</#function>

<#function getProjects data>
    <#return data["."]>
</#function>

<#function getProjectId project>
    <#return project["./masterVersion/id"] + "_" + data["./@name"]>
</#function>

<#function getProject project>
    <#return ContentItem.generateContentItemLink(project)>
</#function>




