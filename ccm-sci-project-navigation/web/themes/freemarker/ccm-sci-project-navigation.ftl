<#ftl ns_prefixes={
"bebop":"http://www.arsdigita.com/bebop/1.0",
"cms":"http://www.arsdigita.com/cms/1.0",
"nav":"http://ccm.redhat.com/navigation",
"ui": "http://www.arsdigita.com/ui/1.0"}
>

<#--filedoc
    Functions for processing project lists.
-->

<#--doc
    Gets the project list with provided ID from the model.

    @param listId The ID of the to retrieve.

    @return The model of the list with the provided ID.
-->
<#function getSciProjectList listId="itemList">
    <#return model["/bebop:page/nav:sci-project-list[@id='${listId}']"]>
</#function>

<#--doc
    Get the projects from a project list

    @param list The list to use.

    @return A sequence with the models of the projects in the list.
-->
<#function getProjects list>
    <#return list["./project"]>
</#function>

<#--doc
    Gets the current value of the title filter.

    @param The model of the list to use.

    @return The value of the title filter.
-->
<#function getTitleFilterValue list>
    <#if (list["./filters/title"]?size > 0)>
        <#return list["./filters/title"]>
    <#else>
        <#return "">
    </#if>
</#function>

<#--doc
    Gets the current value of the research field filter.

    @param The model of the list to use.

    @return The current value of the research field filter.
-->
<#function getResearchFieldFilterValue list>
    <#if (list["./filters/researchfield"]?size > 0)>
        <#return list["./filters/researchfield"]>
    <#else>
        <#return "">
    </#if>
</#function>

<#--doc
    Gets the number of projects in the list.

    @param The model of the list to use.

    @return The number of projects in the list.
-->
<#function getCount list>
    <#return list["./paginator/@count"]>
</#function>

<#--doc
    Gets the current page of the list.

    @param The model of the list to use.

    @return The current page of the list.
-->
<#function getCurrentPage list>
    <#return list["./paginator/@currentPage"]>
</#function>

<#--doc
    Gets the maximum number of items per page.

    @param The model of the list to use.

    @return The maximum number of items per page.
-->
<#function getLimit list>
    <#return list["./paginator/@limit"]>
</#function>

<#--doc
    Gets the number of pages of the list.

    @param The model of the list to use.

    @return The number of pages of the list.
-->
<#function getMaxPages list>
    <#return list["./paginator/@maxPages"]>
</#function>

<#--doc
    Gets the link to the next page of the list.

    @param list The list model as returned by `getSciMemberList`.

    @return The link to the next page of the list.
-->
<#function getNextPageLink list>
    <#return list["./paginator/@nextPageLink"]>
</#function>

<#--doc
    Gets the link to the previous page of the list.

    @param list The list model as returned by `getSciMemberList`.

    @return The link to the previous page of the list.
-->
<#function getPrevPageLink list>
    <#return list["./paginator/@prevPageLink"]>
</#function>

<#--doc
    Gets the index to the first visible item.

    @param list The list model as returned by `getSciMemberList`.

    @return The index of the first visible item.
-->
<#function getOffset list>
    <#return list["./paginator/@offset"]>
</#function>

<#--doc
    Gets the ID of the project.

    @param item The model of the project item to use (as returned by `getProjects`)

    @return The ID of the provided project item.
-->
<#function getProjectItemId item>
    <#return item["./item-id"]>
</#function>

<#--doc
    Gets the name of the project item.

    @param item The model of the project item to use (as returned by `getProjects`).

    @return The name of the project item.
-->
<#function getProjectItemName item>
    <#return item["./name"]>
</#function>

<#--doc
    Gets the title of the project item.

    @param title The model of the project item to use (as returned by `getProjects`).

    @return The title of the project item.
-->
<#function getProjectItemTitle item>
    <#return item["./title"]>
</#function>

<#--doc
    Gets the object type of the project item.

    @param item The model of the project item to use (as returned by `getProjects`).

    @return The object type of the project item.
-->
<#function getProjectItemObjectType item>
    <#return item["./@object-type"]>
</#function>

<#--doc
    Gets the begin date of the project item.

    @param item The model of the project item to use (as returned by `getProjects`).

    @return The begin date of the project.
-->
<#function getProjectItemBegin item>
    <#return item["./project-begin"]>
</#function>

<#--doc
    Gets day of the begin date of the project item.

    @param item The model of the project item to use (as returned by `getProjects`).

    @return The day of the begin date of the project.
-->
<#function getProjectItemBeginDay item>
    <#return item["./project-begin/@day"]>
</#function>

<#--doc
    Gets the month of the begin date of the project item.

    @param item The model of the project item to use (as returned by `getProjects`).

    @return The month of the begin date of the project.
-->
<#function getProjectItemBeginMonth item>
    <#return item["./project-begin/@month-name"]>
</#function>

<#--doc
    Get year of the begin date of the project item.

    @param item The model of the project item to use (as returned by `getProjects`).

    @return The year of the begin date of the project.
-->
<#function getProjectItemBeginYear item>
    <#return item["./project-begin/@year"]>
</#function>

<#--doc
    Gets the end date of the project item.

    @param item The model of the project item to use (as returned by `getProjects`).

    @return The end date of the project.
-->
<#function getProjectItemEnd item>
    <#return item["./project-end"]>
</#function>

<#--doc
    Gets the day of the end date of the project item.

    @param item The model of the project item to use (as returned by `getProjects`).

    @return The day of the end date of the project.
-->
<#function getProjectItemEndDay item>
    <#return item["./project-end/@day"]>
</#function>

<#--doc
    Gets the month of the end date of the project item.

    @param item The model of the project item to use (as returned by `getProjects`).

    @return The month of the end date of the project.
-->
<#function getProjectItemEndMonth item>
    <#return item["./project-end/@month-name"]>
</#function>

<#--doc
    Gets the year of the end date of the project item.

    @param item The model of the project item to use (as returned by `getProjects`).

    @return The year of the end date of the project.
-->
<#function getProjectItemEndYear item>
    <#return item["./project-end/@year"]>
</#function>

<#--doc
    Gets the short description of the project.

    @param item The model of the project item to use (as returned by `getProjects`).

    @return The short description of the project.
-->
<#function getProjectItemShortDesc item>
    <#return item["./project-short-desc"]>
</#function>

<#--doc
    Gets the members of the project.

    @param item The model of the project item to use (as returned by `getProjects`).

    @return A sequence containing the models for the members of the project.
-->
<#function getProjectItemMembers item>
    <#return item["./members/member"]>
</#function>

<#--doc
    Gets the surname of a project member

    @param member The model of the project member to use (as returned by `getProjectItemMembers`).

    @return The surname of the project name.
-->
<#function getProjectMemberSurname member>
    <#return member["./@surname"]>
</#function>

<#--doc
    Gets the given name of a project member

    @param member The model of the project member to use (as returned by `getProjectItemMembers`).

    @return The given name of the project name.
-->
<#function getProjectMemberGivenname member>
    <#return member["./@givenname"]>
</#function>
