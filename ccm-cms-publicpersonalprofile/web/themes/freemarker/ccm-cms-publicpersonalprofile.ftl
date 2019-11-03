<#ftl ns_prefixes={
"bebop":"http://www.arsdigita.com/bebop/1.0",
"cms":"http://www.arsdigita.com/cms/1.0",
"nav":"http://ccm.redhat.com/navigation",
"ppp":"http://www.arsdigita.com/PublicPersonalProfile/1.0",
"ui": "http://www.arsdigita.com/ui/1.0"}
>

<#--filedoc
    Provides functions for proceesing public person profiles.
-->

<#--doc
    Gets the full owner name.

    @param model The model of the public personal profile to use.

    @return The full name of the profile owner.
-->
<#function getProfileOwnerFullName model>
    <#return model["./ppp:profile/ppp:ownerName"]>
</#function>

<#--doc
    Gets the model of the profile owner.

    @param model The model of the public personal profile to use.

    @return The model of the profile owner.
-->
<#function getProfileOwner data>
    <#return data["./profileOwner/owner"]>
</#function>

<#--doc
    Gets the surname of the profile owner.

    @param owner The profile owner model as returned by `getProfileOwner`.

    @return  The surname of the profile owner.
-->
<#function getProfileOwnerSurname owner>
    <#return owner["./surname"]>
</#function>

<#--doc
    Gets the given name of the profile owner.

    @param owner The profile owner model as returned by `getProfileOwner`.

    @return  The given name of the profile owner.
-->
<#function getProfileOwnerGivenName owner>
    <#return owner["./givenname"]>
</#function>

<#--doc
    Gets the name prefix of the profile owner.

    @param owner The profile owner model as returned by `getProfileOwner`.

    @return  The name prefix of the profile owner.
-->
<#function getProfileOwnerTitlePre owner>
    <#return owner["./titlePre"]>
</#function>

<#--doc
    Gets the name suffix of the profile owner.

    @param owner The profile owner model as returned by `getProfileOwner`.

    @return  The name suffix of the profile owner.
-->
<#function getProfileOwnerTitlePost owner>
    <#return owner["./titlePost"]>
</#function>

<#--doc
    Gets the value of the `position` property of the profile.

     @param model The model of the public personal profile to use.

    @return  The value of the `position`property.
-->
<#function getProfilePosition data>
    <#return data["./position"]>
</#function>

<#--doc
    Gets the value of the `interests` property of the profile.

     @param model The model of the public personal profile to use.

    @return  The value of the `interests`property.
-->
<#function getProfileInterests data>
    <#return data["./interests"]>
</#function>

<#--doc
    Gets the value of the `misc` property of the profile.

    @param model The model of the public personal profile to use.

    @return The value of the `misc`property.
-->
<#function getProfileMisc data>
    <#return data["./misc"]>
</#function>

<#--doc
    Gets the contact assoicated with the owner of the profile. The contact
    can be further processed using the functions provided by the 
    ccm-cms-types-contact module.

    @param model The model of public personal profile to use.

    @return The model of the contact asssociated with the profile owner.
-->
<#function getProfileOwnerContact owner>
    <#return owner["./contacts/contact"]>
</#function>

<#--doc
    Gets the image associated with the profile.

    @param model The model of public personal profile to use.

    @return The model of the image associated with the profile.
-->
<#function getProfileImage data>
    <#if (data["./ppp:profile/ppp:profileImage"]?size > 0)>
        <#return data["./ppp:profile/ppp:profileImage/imageAttachments[1]/*"]>
    <#elseif (data["./nav:greetingdata/cms:data/profileOwner/owner/imageAttachments"]?size > 0)>
        <#return data["./nav:greetingdata/cms:data/profileOwner/owner/imageAttachments[1]/*"]>
    </#if>
</#function>

<#--doc
    Gets the URL of the profile image.

    @param data The model of the profile image as returned by `getProfileImage`.

    @return The URL of the profile image.
-->
<#function getProfileImageUrl data>
    <#if (data["./ppp:profile/ppp:profileImage"]?size > 0)>
        <#assign imageId=data["./ppp:profile/ppp:profileImage/imageAttachments[1]/image/id"]>
        <#return dispatcherPrefix + '/cms-service/stream/image/?image_id=' + imageId>
    <#elseif (data["./nav:greetingdata/cms:data/profileOwner/owner/imageAttachments"]?size > 0)>
        <#assign imageId=data["./nav:greetingdata/cms:data/profileOwner/owner/imageAttachments[1]/image/id"]>
        <#return dispatcherPrefix + '/cms-service/stream/image/?image_id=' + imageId>
    </#if>
</#function>

<#--doc
    Gets the name of the profile owner.

    @param data The model of the public personal profile to use.

    @return The name of the profile owner.
-->
<#function getProfileOwnerName data>
    <#return data["./ppp:profile/ppp:ownerName"]>
</#function>

<#--doc
    Gets the personal publications of the profile owner.

    @param data The model of the public personal profile to use.

    @return The models of the publications of the profile owner.
-->
<#function getPersonalPublications data>
    <#return data["./ppp:profile/personalPublications"]>
</#function>

<#--doc
    Gets the available publication groups.

    @param data The model of the public personal profile to use.

    @return The models of the available publication groups.
-->
<#function getPersonalPublicationsAvailablePublicationGroups data>
    <#return data["./ppp:profile/personalPublications/availablePublicationGroups/availablePublicationGroup"]>
</#function>

<#--doc
    Gets the ID of a publication group.

    @param The model of the publication group to use.

    @return The ID of the provided publication group.
-->
<#function getPublicationGroupId group>
    <#return group["./@name"]>
</#function>

<#--doc
    Gets the link for a publication group.

    @param The model of the publication group to use.

    @return The link for the provided publication group.
-->
<#function getPublicationGroupLink group>
    <#if (model["ppp:profile/personalPublications/publications/@all"]?size > 0
          && model["ppp:profile/personalPublications/publications/@all"] == "all")>
        <#return "#" + group["./@name"]>
    <#else>
        <#return "?group=" + group["./@name"]>
    </#if>
</#function>

<#--doc
    Gets the available publication groups.

    @param data The model of the public personal profile to use.

    @return The models of the available publication groups.
-->
<#function getPublicationGroups data>
    <#return data["./ppp:profile/personalPublications/publications/publicationGroup"]>
</#function>

<#--doc
    Gets the publications in group..

    @param data The model of the publication group to use.

    @return The models of the publication in the provided group.
-->
<#function getPublicationsOfGroup data>
    <#return data["./publications"]>
</#function>

<#--doc
    Checks if a paginator for the publications is available.

    @param profile The model of the public personal profile to use.

    @return `true` if a paginator is available, `false` otherwise.
-->
<#function hasPublicationsPaginator profile>
    <#return (model["/bebop:page/ppp:profile/personalPublications/publications/publicationGroup/nav:paginator"]?size > 0)>
</#function>

<#--doc
    Gets the base URL for the paginator.

    @param profile The model of the public personal profile to use.

    @return The base URL for the paginator.
-->
<#function getPublicationsPaginatorBaseUrl profile>
    <#if (model["/bebop:page/ppp:profile/personalPublications/publications/publicationGroup/nav:paginator/@baseURL"]?contains("?"))>
        <#return model["/bebop:page/ppp:profile/personalPublications/publications/publicationGroup/nav:paginator/@baseURL"] + "&">
    <#else>
        <#return model["/bebop:page/ppp:profile/personalPublications/publications/publicationGroup/nav:paginator/@baseURL"] + "?">
    </#if>
</#function>

<#--doc
    Gets the number of pages of the publications paginator.

    @param profile The model of the public personal profile to use.

    @return The number of pages.
-->
<#function getPublicationsPaginatorPageCount profile>
    <#return model["/bebop:page/ppp:profile/personalPublications/publications/publicationGroup/nav:paginator/@pageCount"]?number>
</#function>

<#--doc
    Gets the current page of the publications paginator.

    @param profile The model of the public personal profile to use.

    @return The current page.
-->
<#function getPublicationsPaginatorPageNumber profile>
    <#return model["/bebop:page/ppp:profile/personalPublications/publications/publicationGroup/nav:paginator/@pageNumber"]?number>
</#function>

<#--doc
    Gets the name of the `page` parameter of the publications paginator.

    @param profile The model of the public personal profile to use.

    @return The name of the `page` parameter.
-->
<#function getPublicationsPaginatorPageParam profile>
    <#return model["/bebop:page/ppp:profile/personalPublications/publications/publicationGroup/nav:paginator/@pageParam"]>
</#function>

<#--doc
    Gets the maximum number of publications per page.

    @param profile The model of the public personal profile to use.

    @return The maximum number of publications per page.
-->
<#function getPublicationsPaginatorPageSize profile>
    <#return model["/bebop:page/ppp:profile/personalPublications/publications/publicationGroup/nav:paginator/@pageSize"]?number>
</#function>

<#--doc
    Gets the index of the first publication shown.

    @param profile The model of the public personal profile to use.

    @return The index of the first publication.
-->
<#function getPublicationsPaginatorObjectBegin profile>
    <#return model["/bebop:page/ppp:profile/personalPublications/publications/publicationGroup/nav:paginator/@objectBegin"]?number>
</#function>

<#--doc
    Gets the number of publications.

    @param profile The model of the public personal profile to use.

    @return The number of publications.
-->
<#function getPublicationsPaginatorObjectCount profile>
    <#return model["/bebop:page/ppp:profile/personalPublications/publications/publicationGroup/nav:paginator/@objectCount"]?number>
</#function>

<#--doc
    Gets the index of the last publication shown.

    @param profile The model of the public personal profile to use.

    @return The index of the last publication.
-->
<#function getPublicationsPaginatorObjectEnd profile>
    <#return model["/bebop:page/ppp:profile/personalPublications/publications/publicationGroup/nav:paginator/@objectEnd"]?number>
</#function>

<#--doc
    Gets the link to the previous page.

    @param profile The model of the public personal profile to use.

    @return The link to the previous page.
-->
<#function getPublicationsPaginatorPrevPageLink profile>
    <#return getPublicationsPaginatorBaseUrl(profile) + getPublicationsPaginatorPageParam(profile) + "=" + (getPublicationsPaginatorPageNumber(profile) - 1)>
</#function>

<#--doc
    Gets the link to the first page.

    @param profile The model of the public personal profile to use.

    @return The link to the first page.
-->
<#function getPublicationsPaginatonFirstPageLink profile>
    <#return getPublicationsPaginatorBaseUrl(profile) + getPublicationsPaginatorPageParam(profile) + "=1">
</#function>

<#--doc
    Gets the link to the next page.

    @param profile The model of the public personal profile to use.

    @return The link to the next page.
-->
<#function getPublicationsPaginatorNextPageLink profile>
    <#return getPublicationsPaginatorBaseUrl(profile) + getPublicationsPaginatorPageParam(profile) + "=" + (getPublicationsPaginatorPageNumber(profile) + 1)>
</#function>

<#--doc
    Gets the link to the last page.

    @param profile The model of the public personal profile to use.

    @return The link to the last page.
-->
<#function getPublicationsPaginatorLastPageLink profile>
    <#return getPublicationsPaginatorBaseUrl(profile) + getPublicationsPaginatorPageParam(profile) + "=" + getPublicationsPaginatorPageCount(profile)>
</#function>

<#--doc
    Gets the available project groups.

    @param data The model of the public personal profile to use.

    @return The models of the available project groups.
-->
<#function getAvailableProjectGroups data>
    <#return data["./ppp:profile/personalProjects/availableProjectGroups/availableProjectGroup"]>
</#function>

<#--doc
    Gets the ID of a project group.

    @param The model of the project group to use.

    @return The ID of the provided project group.
-->
<#function getProjectGroupId group>
    <#return group["./@name"]>
</#function>

<#--doc
    Gets the link for a project group.

    @param The model of the project group to use.

    @return The link for the provided project group.
-->
<#function getProjectGroupLink group>
    <#if (model["/bebop:page/ppp:profile/personalProjects/projects/@all"]?size > 0
          && model["/bebop:page/ppp:profile/personalProjects/projects/@all"] == "all")>
        <#return "#" + group["./@name"]>
    <#else>
        <#return "?group=" + group["./@name"]>
    </#if>
</#function>

<#--doc
    Gets the available project groups.

    @param data The model of the public personal profile to use.

    @return The models of the available project groups.
-->
<#function getProjectGroups data>
    <#return data["./ppp:profile/personalProjects/projects/projectGroup"]>
</#function>

<#--doc
    Gets the project in group..

    @param data The model of the project group to use.

    @return The models of the project in the provided group.
-->
<#function getProjectsOfGroup data>
    <#return data["./project"]>
</#function>

<#--doc
    Checks if a paginator for the projects is available.

    @param profile The model of the public personal profile to use.

    @return `true` if a paginator is available, `false` otherwise.
-->
<#function hasProjectsPaginator profile>
    <#return (model["./bebop:page/ppp:profile/personalProjects/projects/projectGroup/nav:paginator"]?size > 0)>
</#function>

<#--doc
    Gets the base URL for the paginator.

    @param profile The model of the public personal profile to use.

    @return The base URL for the paginator.
-->
<#function getProjectsPaginatorBaseUrl profile>
    <#if (model["/bebop:page/ppp:profile/personalProjects/projects/projectGroup/nav:paginator/@baseURL"]?contains("?"))>
        <#return model["/bebop:page/bebop:page/ppp:profile/personalProjects/projects/projectGroup/nav:paginator/@baseURL"] + "&">
    <#else>
        <#return model["/bebop:page/ppp:profile/personalProjects/projects/projectGroup/nav:paginator/@baseURL"] + "?">
    </#if>
</#function>

<#--doc
    Gets the number of pages of the projects paginator.

    @param profile The model of the public personal profile to use.

    @return The number of pages.
-->
<#function getProjectsPaginatorPageCount profile>
    <#return model["/bebop:page/ppp:profile/personalProjects/projects/projectGroup/nav:paginator/@pageCount"]?number>
</#function>

<#--doc
    Gets the current page of the projects paginator.

    @param profile The model of the public personal profile to use.

    @return The current page.
-->
<#function getProjectsPaginatorPageNumber profile>
    <#return model["/bebop:page/ppp:profile/personalProjects/projects/projectGroup/nav:paginator/@pageNumber"]?number>
</#function>

<#--doc
    Gets the name of the `page` parameter of the projects paginator.

    @param profile The model of the public personal profile to use.

    @return The name of the `page` parameter.
-->
<#function getProjectsPaginatorPageParam profile>
    <#return model["/bebop:page/ppp:profile/personalProjects/projects/projectGroup/nav:paginator/@pageParam"]>
</#function>

<#--doc
    Gets the maximum number of projects per page.

    @param profile The model of the public personal profile to use.

    @return The maximum number of projects per page.
-->
<#function getProjectsPaginatorPageSize profile>
    <#return model["/bebop:page/ppp:profile/personalProjects/projects/projectGroup/nav:paginator/@pageSize"]?number>
</#function>

<#--doc
    Gets the index of the first project shown.

    @param profile The model of the public personal profile to use.

    @return The index of the first project.
-->
<#function getProjectsPaginatorObjectBegin profile>
    <#return model["/bebop:page/ppp:profile/personalProjects/projects/projectGroup/nav:paginator/@objectBegin"]?number>
</#function>

<#--doc
    Gets the number of projects.

    @param profile The model of the public personal profile to use.

    @return The number of projects.
-->
<#function getProjectsPaginatorObjectCount profile>
    <#return model["/bebop:page/ppp:profile/personalProjects/projects/projectGroup/nav:paginator/@objectCount"]?number>
</#function>

<#--doc
    Gets the index of the last project shown.

    @param profile The model of the public personal profile to use.

    @return The index of the last project.
-->
<#function getProjectsPaginatorObjectEnd profile>
    <#return model["/bebop:page/ppp:profile/personalProjects/projects/projectGroup/nav:paginator/@objectEnd"]?number>
</#function>

<#--doc
    Gets the link to the previous page.

    @param profile The model of the public personal profile to use.

    @return The link to the previous page.
-->
<#function getProjectsPaginatorPrevPageLink profile>
    <#return getProjectsPaginatorBaseUrl(profile) + getProjectsPaginatorPageParam(profile) + "=" + (getProjectsPaginatorPageNumber(profile) - 1)>
</#function>

<#--doc
    Gets the link to the first page.

    @param profile The model of the public personal profile to use.

    @return The link to the first page.
-->
<#function getProjectsPaginatonFirstPageLink profile>
    <#return getProjectsPaginatorBaseUrl(profile) + getProjectsPaginatorPageParam(profile) + "=1">
</#function>

<#--doc
    Gets the link to the next page.

    @param profile The model of the public personal profile to use.

    @return The link to the next page.
-->
<#function getProjectsPaginatorNextPageLink profile>
    <#return getProjectsPaginatorBaseUrl(profile) + getProjectsPaginatorPageParam(profile) + "=" + (getProjectsPaginatorPageNumber(profile) + 1)>
</#function>

<#--doc
    Gets the link to the last page.

    @param profile The model of the public personal profile to use.

    @return The link to the last page.
-->
<#function getProjectsPaginatorLastPageLink profile>
    <#return getProjectsPaginatorBaseUrl(profile) + getProjectsPaginatorPageParam(profile) + "=" + getProjectsPaginatorPageCount(profile)>
</#function>





