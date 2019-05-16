<#ftl ns_prefixes={
"bebop":"http://www.arsdigita.com/bebop/1.0",
"cms":"http://www.arsdigita.com/cms/1.0",
"nav":"http://ccm.redhat.com/navigation",
"ui": "http://www.arsdigita.com/ui/1.0"}
>

<#function getSciProjectList listId="itemList">
    <#return model["/bebop:page/nav:sci-project-list[@id='${listId}'"]>
</#function>

<#function getProjects list>
    <#return list["./project]>
</#function>

<#function getTitleFilterValue list>
    <#return list["./filters/title"]>
</#function>

<#function getResearchFieldFilterValue list>
    <#return list["./filters/researchfield"]>
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
    <#return item["./@object-type]>
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






