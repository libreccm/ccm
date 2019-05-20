<#ftl ns_prefixes={
"bebop":"http://www.arsdigita.com/bebop/1.0",
"cms":"http://www.arsdigita.com/cms/1.0",
"nav":"http://ccm.redhat.com/navigation",
"ui": "http://www.arsdigita.com/ui/1.0"}
>

<#function getProfileOwner data>
    <#return data["./profileOwner"]>
</#function>

<#function getProfileOwnerSurname owner>
    <#return owner["./surname"]>
</#function>

<#function getProfileOwnerGivenName owner>
    <#return owner["./givenname"]>
</#function>

<#function getProfileOwnerTitlePre owner>
    <#return owner["./titlePre"]>
</#function>

<#function getProfileOwnerTitlePost owner>
    <#return owner["./titlePost"]>
</#function>

<#function getProfilePosition>
    <#return data["./position"]>
</#function>

<#function getProfileInterests>
    <#return data["./interests"]>
</#function>

<#function getProfileMisc>
    <#return data["./misc"]>
</#function>

<#function getProfileOwnerContact owner>
    <#return owner["./contact"]>
</#function>

<#function getProfileImage data>
    <#if (data["./ppp:profile/ppp:profileImage"]?size > 0)>
        <#return data["./ppp:profile/ppp:profileImage/imageAttachments[1]/*"]>
    <#elseif (data["./nav:greetingdata/cms:data/profileOwner/owner/imageAttachments"]?size > 0)>
        <#return data["./nav:greetingdata/cms:data/profileOwner/owner/imageAttachments[1]/*"]>
    </#if>
</#function>

<#function getProfileOwnerName data>
    <#return data["./ppp:profile/ppp:ownerName"]>
</#function>

<#function getPersonalPublications data>
    <#return data["./ppp:profile/personalPublications"]>
</#function>

<#function getPersonalPublicationsAvailablePublicationGroups data>
    <#return data["./ppp:profile/personalPublications/availablePublicationGroups"]>
</#function>

<#function getPublicationGroupId group>
    <#return group["./@name"]>
</#function>

<#function getPublicationGroupLink group>
    <#if (data["ppp:profile/personalPublications/publications/@all"] == "all")>
        <#return "#" + group["./@name"]>
    <#else>
        <#return "?group=" + group["./@name"]>
    </#if>
</#function>

<#function getPublicationGroups data>
    <#return data["./ppp:profile/personalPublications/publications/publicationGroup"]>
</#function>

<#function getPublicationsOfGroup data>
    <#return data["./publications"]>
</#function>

<#function hasPublicationsPaginator profile>
    <#return (model["/ppp:profile/personalPublications/publications/publicationGroup/nav:paginator"]?size > 0)>
</#function>

<#function getPublicationsPaginatorBaseUrl profile>
    <#if (model["/ppp:profile/personalPublications/publications/publicationGroup/nav:paginator/@baseURL"]?contains("?"))>
        <#return model["/ppp:profile/personalPublications/publications/publicationGroup/nav:paginator/@baseURL"].@@text + "&">
    <#else>
        <#return model["/ppp:profile/personalPublications/publications/publicationGroup/nav:paginator/@baseURL"].@@text + "?">
    </#if>
<#function>

<#function getPublicationsPaginatorPageCount profile>
    <#return model["/ppp:profile/personalPublications/publications/publicationGroup/nav:paginator/@pageCount"].@@text>
</#function>

<#function getPublicationsPaginatorPageNumber profile>
    <#return model["/ppp:profile/personalPublications/publications/publicationGroup/nav:paginator/@pageNumber"].@@text>
</#function>

<#function getPublicationsPaginatorPageParam profile>
    <#return model["/ppp:profile/personalPublications/publications/publicationGroup/nav:paginator/@pageParam"].@@text>
</#function>

<#function getPublicationsPaginatorPageSize profile>
    <#return model["/ppp:profile/personalPublications/publications/publicationGroup/nav:paginator/@pageSize"].@@text>
</#function>

<#function getPublicationsPaginatorObjectBegin profile>
    <#return model["/ppp:profile/personalPublications/publications/publicationGroup/nav:paginator/@objectBegin"].@@text>
</#function>

<#function getPublicationsPaginatorObjectCount profile>
    <#return model["/ppp:profile/personalPublications/publications/publicationGroup/nav:paginator/@objectCount"].@@text>
</#function>

<#function getPublicationsPaginatorObjectEnd profile>
    <#return model["/ppp:profile/personalPublications/publications/publicationGroup/nav:paginator/@objectEnd"].@@text>
</#function>

<#function getPublicationsPaginatorPrevPageLink profile>
    <#return getPublicationsPaginatorBaseUrl(profile) + getPublicationsPaginatorPageParam(profile) + "=" + (getPublicationsPaginatorPageNumber(profile) - 1)>
</#function>

<#function getPublicationsPaginatonFirstPageLink profile>
    <#return getPublicationsPaginatorBaseUrl(profile) + getPublicationsPaginatorPageParam(profile) + "=1)>
</#function>

<#function getPublicationsPaginatorNextPageLink profile>
    <#return getPublicationsPaginatorBaseUrl(profile) + getPublicationsPaginatorPageParam(profile) + "=" + (getPublicationsPaginatorPageNumber(profile) + 1)>
</#function>

<#function getPublicationsPaginatorLastPageLink profile>
    <#return getPublicationsPaginatorBaseUrl(profile) + getPublicationsPaginatorPageParam(profile) + "=" + getPublicationsPaginatorPageCount(profile))>
</#function>

<#function getAvailableProjectGroups data>
    <#return data["./ppp:profile/personalProjects/availableProjectGroups/availableProjectGroup"]>
</#function>

<#function getProjectGroupId group>
    <#return group["./@name"]>
<#function>

<#function getProjectGroupLink group>
    <#if (data["ppp:profile/personalPublications/publications/@all"] == "all")>
        <#return "#" + group["./@name"]>
    <#else>
        <#return "?group=" + group["./@name"]>
    </#if>
</#function>

<#function getProjectGroups data>
    <#return data["./ppp:profile/personalProjects/projects/projectGroup"]>
</#function>

<#function getProjectsOfGroup data>
    <#return data["./ppp:profile/personalProjects/projects/projectGroup/project"]>
</#function>

<#function hasProjectsPaginator profile>
    <#return (model["/ppp:profile/personalProjects/projects/projectGroup/nav:paginator"]?size > 0)>
</#function>

<#function getProjectsPaginatorBaseUrl profile>
    <#if (model["/ppp:profile/personalProjects/projects/projectGroup/nav:paginator/@baseURL"]?contains("?"))>
        <#return model["/ppp:profile/personalProjects/projects/projectGroup/nav:paginator/@baseURL"].@@text + "&">
    <#else>
        <#return model["/ppp:profile/personalProjects/projects/projectGroup/nav:paginator/@baseURL"].@@text + "?">
    </#if>
<#function>

<#function getProjectsPaginatorPageCount profile>
    <#return model["/ppp:profile/personalProjects/projects/projectGroup/nav:paginator/@pageCount"].@@text>
</#function>

<#function getProjectsPaginatorPageNumber profile>
    <#return model["/ppp:profile/personalProjects/projects/projectGroup/nav:paginator/@pageNumber"].@@text>
</#function>

<#function getProjectsPaginatorPageParam profile>
    <#return model["/ppp:profile/personalProjects/projects/projectGroup/nav:paginator/@pageParam"].@@text>
</#function>

<#function getProjectsPaginatorPageSize profile>
    <#return model["/ppp:profile/personalProjects/projects/projectGroup/nav:paginator/@pageSize"].@@text>
</#function>

<#function getProjectsPaginatorObjectBegin profile>
    <#return model["/ppp:profile/personalProjects/projects/projectGroup/nav:paginator/@objectBegin"].@@text>
</#function>

<#function getProjectsPaginatorObjectCount profile>
    <#return model["/ppp:profile/personalProjects/projects/projectGroup/nav:paginator/@objectCount"].@@text>
</#function>

<#function getProjectsPaginatorObjectEnd profile>
    <#return model["/ppp:profile/personalProjects/projects/projectGroup/nav:paginator/@objectEnd"].@@text>
</#function>

<#function getProjectsPaginatorPrevPageLink profile>
    <#return getProjectsPaginatorBaseUrl(profile) + getProjectsPaginatorPageParam(profile) + "=" + (getProjectsPaginatorPageNumber(profile) - 1)>
</#function>

<#function getProjectsPaginatonFirstPageLink profile>
    <#return getProjectsPaginatorBaseUrl(profile) + getProjectsPaginatorPageParam(profile) + "=1)>
</#function>

<#function getProjectsPaginatorNextPageLink profile>
    <#return getProjectsPaginatorBaseUrl(profile) + getProjectsPaginatorPageParam(profile) + "=" + (getProjectsPaginatorPageNumber(profile) + 1)>
</#function>

<#function getProjectsPaginatorLastPageLink profile>
    <#return getProjectsPaginatorBaseUrl(profile) + getProjectsPaginatorPageParam(profile) + "=" + getProjectsPaginatorPageCount(profile))>
</#function>





