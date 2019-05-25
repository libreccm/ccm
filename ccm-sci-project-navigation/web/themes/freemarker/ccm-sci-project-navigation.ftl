<#ftl ns_prefixes={
"bebop":"http://www.arsdigita.com/bebop/1.0",
"cms":"http://www.arsdigita.com/cms/1.0",
"nav":"http://ccm.redhat.com/navigation",
"ui": "http://www.arsdigita.com/ui/1.0"}
>

<#function getSciProjectList listId="itemList">
    <#return model["/bebop:page/nav:sci-project-list[@id='${listId}']"]>
</#function>

<#function getProjects list>
    <#return list["./project"]>
</#function>

<#function getTitleFilterValue list>
    <#if (list["./filters/title"]?size > 0)>
        <#return list["./filters/title"]>
    <#else>
        <#return "">
    </#if>
</#function>

<#function getResearchFieldFilterValue list>
    <#if (list["./filters/researchfield"]?size > 0)>
        <#return list["./filters/researchfield"]>
    <#else>
        <#return "">
    </#if>
</#function>

<#function getCount list>
    <#return list["./paginator/@count"]>
</#function>

<#function getCurrentPage list>
    <#return list["./paginator/@currentPage"]>
</#function>

<#function getLimit list>
    <#return list["./paginator/@limit"]>
</#function>

<#function getMaxPages list>
    <#return list["./paginator/@maxPages"]>
</#function>

<#function getNextPageLink list>
    <#return list["./paginator/@nextPageLink"]>
</#function>

<#function getPrevPageLink list>
    <#return list["./paginator/@prevPageLink"]>
</#function>

<#function getOffset list>
    <#return list["./paginator/@offset"]>
</#function>

<#function getProjectItemId item>
    <#return item["./item-id"]>
</#function>

<#function getProjectItemName item>
    <#return item["./name"]>
</#function>

<#function getProjectItemTitle item>
    <#return item["./title"]>
</#function>

<#function getProjectItemObjectType item>
    <#return item["./@object-type"]>
</#function>

<#function getProjectItemBegin item>
    <#return item["./project-begin"]>
</#function>

<#function getProjectItemBeginDay item>
    <#return item["./project-begin/@day"]>
</#function>

<#function getProjectItemBeginMonth item>
    <#return item["./project-begin/@month-name"]>
</#function>

<#function getProjectItemBeginYear item>
    <#return item["./project-begin/@year"]>
</#function>

<#function getProjectItemEnd item>
    <#return item["./project-end"]>
</#function>

<#function getProjectItemEndDay item>
    <#return item["./project-end/@day"]>
</#function>

<#function getProjectItemEndMonth item>
    <#return item["./project-end/@month-name"]>
</#function>

<#function getProjectItemEndYear item>
    <#return item["./project-end/@year"]>
</#function>

<#function getProjectItemShortDesc item>
    <#return item["./project-short-desc"]>
</#function>

<#function getProjectItemMembers item>
    <#return item["./members/member"]>
</#function>

<#function getProjectMemberSurname member>
    <#return member["./surname"]>
</#function>

<#function getProjectMemberGivenname member>
    <#return member["./givenname"]>
</#function>






