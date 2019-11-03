<#ftl ns_prefixes={
"bebop":"http://www.arsdigita.com/bebop/1.0",
"cms":"http://www.arsdigita.com/cms/1.0",
"nav":"http://ccm.redhat.com/navigation",
"ui": "http://www.arsdigita.com/ui/1.0"}
>

<#import "/ccm-cms/content-item.ftl" as ContentItem>

<#--filedoc
    Functions for processing SciDepartment items.
-->

<#--doc
    Gets the description of a SciDepartment.

    @param data The SciDepartment to use.

    @return The description of the department.
-->
<#function getDescription data>
    <#if (data["./departmentDesc"]?size > 0)>
        <#return data["./departmentDescription"]>
    <#elseif (data["./description"]?size > 0)>
        <#return data["./description"]>
    </#if>
</#function>

<#--doc
    Gets the short description of a SciDepartment.

    @param data The SciDepartment to use.

    @return The short description of the department.
-->
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

<#--doc
    Gets the heads of the department.

    @param data The SciDepartment to use.

    @return A sequence of person items.
-->
<#function getDepartmentHeads data>
    <#return data["./heads/head"]>
</#function>

<#--doc
    Gets the ID of a department head.

    @param head The department head to use.

    @return The ID of the department head.
-->
<#function getDepartmentHeadId head>
    <#return data["./masterVersion/id"] + "_" + data["./@name"]>
</#function>

<#--doc
    Gets the link to a department head.

    @param head The department head to use.

    @return A link to the department head.
-->
<#function getDepartmentHeadLink head>
    <#return ContentItem.generateContentItemLink(head)>
</#function>

<#--doc
    Gets the vice heads of the department.

    @param data The SciDepartment to use.

    @return A sequence of person items.
-->
<#function getDepartmentViceHeads data>
    <#return data["./viceheads/vicehead"]>
</#function>

<#--doc
    Gets the ID of a department vice head.

    @param head The department vice head to use.

    @return The ID of the department vice head.
-->
<#function getDepartmentViceHeadId head>
    <#return head["./masterVersion/id"] + "_" + data["./@name"]>
</#function>

<#--doc
    Gets the link to a department vice head.

    @param head The department vice head to use.

    @return A link to the department vice head.
-->
<#function getDepartmentViceHeadLink head>
    <#return CMS.generateContentItemLink(head)>
</#function>

<#--doc
    Gets the secretariats of the department.

    @param data The SciDepartment to use.

    @return A sequence of person items.
-->
<#function getSecretariats data>
    <#return data["./secretariats/secretariat"]>
</#function>

<#--doc
    Gets the ID of a secretariat.

    @param head The secretariat to use.

    @return The ID of the secretariat.
-->
<#function getSecretariatsId sec>
    <#return sec["./masterVersion/id"] + "_" + data["./@name"]>
</#function>

<#--doc
    Gets the link to a secretariat.

    @param head The secretariat to use.

    @return A link to the secretariat.
-->
<#function getSecretariatsLink sec>
    <#return ContentItem.generateContentItemLink(sec)>
</#function>

<#--doc
    Gets the members of the department.

    @param data The SciDepartment to use.

    @return A sequence of persons.
-->
<#function getMembers data>
    <#return data["./member"]>
</#function>

<#--doc
    Gets the projects of the department.

    @param data The SciDepartment to use.

    @return A sequence of SciProjects.
-->
<#function getProjects data>
    <#return data["./project"]>
</#function>

<#--doc
    Gets the ID of a project.

    @param project The project to use.

    @return The ID of the project.
-->
<#function getProjectId project>
    <#return project["./masterVersion/id"] + "_" + data["./@name"]>
</#function>

<#--doc
    Gets the link to a project.

    @param project The project to use.

    @return A link to the project.
-->
<#function getProject project>
    <#return ContentItem.generateContentItemLink(project)>
</#function>




