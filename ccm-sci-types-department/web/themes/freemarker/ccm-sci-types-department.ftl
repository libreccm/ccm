<#ftl ns_prefixes={
"bebop":"http://www.arsdigita.com/bebop/1.0",
"cms":"http://www.arsdigita.com/cms/1.0",
"nav":"http://ccm.redhat.com/navigation",
"ui": "http://www.arsdigita.com/ui/1.0"}
>

<#import ../ccm-cms.ftl as CMS>

<#function getDescription data>
    <#if (data["./instituteDesc"]?size > 0)>
        <#return data["./departmentDescription"]>
    <#elseif (data["./description"]?size > 0)>
        <#return data["./description"]>
    </#if>
</#function>

<#function getShortDescription data>
    <#if (data["./instituteShortDescription"])>
        <#return data["./departmentShortDescription"])>
    <#elseif (data["./shortDescription"])>
        <#return data["./shortDescription"])>
    <#elseif (data["./shortDesc"])>
        <#return data["./shortDesc"])>
    </#if>
</#function>

<#function getDepartmentHeads data>
    <#return data["./heads"]>
</#function>

<#function getDepartmentHeadId head>
    <#return data["./masterVersion/id"] + "_" + data["./@name"]>
</#function>

<#function getDepartmentHeadLink head>
    <#return CMS.generateContentItemLink(head)>
</#function>

<#function getDepartmentViceHeads data>
    <#return data["./heads"]>
</#function>

<#function getDepartmentViceHeadId head>
    <#return head["./masterVersion/id"] + "_" + data["./@name"]>
</#function>

<#function getDepartmentViceHeadLink head>
    <#return CMS.generateContentItemLink(head)>
</#function>

<#function getSecretariats data>
    <#return data["./heads"]>
</#function>

<#function getSecretariatsId sec>
    <#return sec["./masterVersion/id"] + "_" + data["./@name"]>
</#function>

<#function getSecretariatsLink sec>
    <#return CMS.generateContentItemLink(sec)>
</#function>

<#function getProjects data>
    <#return data["."]>
</#function>

<#function getProjectId project>
    <#return project["./masterVersion/id"] + "_" + data["./@name"]>
</#function>

<#function getProject project>
    <#return CMS.generateContentItemLink(project)>
</#function>




