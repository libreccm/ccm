<#ftl ns_prefixes={
"bebop":"http://www.arsdigita.com/bebop/1.0",
"cms":"http://www.arsdigita.com/cms/1.0",
"nav":"http://ccm.redhat.com/navigation",
"ui": "http://www.arsdigita.com/ui/1.0"}
>

<#import ../ccm-cms/content-item.ftl as ContentItem>

<#--filedoc
    Functions for processing SciInstitute items.
-->

<#--doc
    Gets the description of the institute.

    @param data The SciInstitute to use.

    @return The description of the institute.
-->
<#function getDescription data>
    <#if (data["./instituteDesc"]?size > 0)>
        <#return data["./instituteDesc"]>
    <#elseif (data["./description"]?size > 0)>
        <#return data["./description"]>
    </#if>
</#function>

<#--doc
    Gets the short description of the institute.

    @param data The SciInstitute to use.

    @return The short description of the institute.
-->
<#function getShortDescription data>
    <#if (data["./instituteShortDescription"])>
        <#return data["./instituteShortDescription"])>
    <#elseif (data["./shortDescription"])>
        <#return data["./shortDescription"])>
    </#if>
</#function>

<#--doc
    Gets the departments of an institute.

    @param data The SciInstitute to use.

    @return A sequence of SciDepartments.
-->
<#function getDepartments data>
    <#return data["./departments"]>
</#function>

<#--doc
    Gets the OID of a department.

    @param department The SciDepartment to use.

    @return The OID of the department.
-->
<#function getDepartmentOid department>
    <#return department["./@oid"]>
</#function>

<#--doc
    Gets the title of a department.

    @param department The SciDepartment to use.

    @return The title of the department.
-->
<#function getDepartmentTitle department>
    <#return department["./title"]>
</#function>

<#--doc
    Gets the link the to department.

    @param department The SciDepartment to use.

    @return The link to the department.
-->
<#function getDepartmentLink department>
    <#return ContentItem.generateContentItemLink(department)>
</#function>


