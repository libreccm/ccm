<#ftl ns_prefixes={
"bebop":"http://www.arsdigita.com/bebop/1.0",
"cms":"http://www.arsdigita.com/cms/1.0",
"nav":"http://ccm.redhat.com/navigation",
"ui": "http://www.arsdigita.com/ui/1.0"}
>
<#--filedoc
    Functions for processing publication lists.
-->

<#--doc
    Gets the publication list with provided ID from the model.

    @param listId The ID of the to retrieve.

    @return The model of the list with the provided ID.
-->
<#function getSciPublicationsList listId="itemList">
    <#return model["/bebop:page/nav:sci-publication-list[@id='${listId}']"]>
</#function>

<#--doc
    Checks if the current page is showing a single publication (retrieved via 
    NativeSQL queries). 

    @return `true` if the model contains a single publication `false` otherwise.
-->
<#function hasPublication>
    <#return (model["/bebop:page/nav:publication"]?size > 0)>
</#function>

<#--doc
    Retrieve a single publication from the model.
    
    @return The model of the publication.
-->
<#function getPublication>
    <#return model["/bebop:page/nav:publication/publication"]>
</#function>

<#--doc
    Get the publications from a publication list

    @param list The list to use.

    @return A sequence with the models of the publication in the list.
-->
<#function getPublications list>
    <#return list["./publication"]>
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
    Gets the years available for the years filter.

    @param The model of the list to use.

    @return A sequence of the years for the year filter.
-->
<#function getYearOfPublicationFilterAvailableYears list>
    <#return list["./filters/available-years/year"]>
</#function>

<#--doc
    Gets the current value of the year filter.

    @param The model of the list to use.

    @return The current value of the years filter.
-->
<#function getYearOfPublicationFilterValue list>
    <#if (list["./fiters/year"]?size > 0)>
        <#return list["./filters/year"].@@text>
    <#else>
        <#return "">
    </#if>
</#function>

<#--doc
    Gets the current value of the author filter.

    @param The model of the list to use.

    @return The current value of the author filter.
-->
<#function getAuthorsFilterValue list>
    <#if (list["./filters/authors"]?size > 0)>
        <#return list["./filters/authors"]>
    <#else>
        <#return "">
    </#if>
</#function>

<#--doc
    Gets the currently selected sort order of the list.

    @param The model of the list to use.

    @return The currently selected sort order.
-->
<#function getSort list>
    <#if (list["./filters/sort"]?size > 0)>
        <#return list["./filters/sort"]>
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

<#function getLimit list>
    <#return list["./paginator/@limit"]>
</#function>

<#--doc
    Gets the maximum number of items per page.

    @param The model of the list to use.

    @return The maximum number of items per page.
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
    Get the ID of a publication in a list.

    @param The model of the publication to use (as returned by `getPublications`).

    @return The ID of the publication.
-->
<#function getPublicationId item>
    <#return item["./item-id"]>
</#function>

<#--doc
    Gets the object type of the publication.

    @param The model of the publication to use (as returned by `getPublications`).

    @return The type of the publication.
-->
<#function getPublicationObjectType item>
    <#return item["./@object-type"]>
</#function>

<#--doc
    Gets the title of the publication.

    @param The model of the publication to use (as returned by `getPublications`).

    @return The title of the publication.
-->
<#function getPublicationTitle item>
    <#return item["./title"]>
</#function>

<#--doc
    Gets the year of the publication.

    @param The model of the publication to use (as returned by `getPublications`).

    @return The year of the publication.
-->
<#function getPublicationYear item>
    <#return item["./year"]>
</#function>

<#--doc
    Gets the authors of the publication.

    @param The model of the publication to use (as returned by `getPublications`).

    @return A sequence of the authors of the publication.
-->
<#function getPublicationAuthors item>
    <#return item["./authors/author"]>
</#function>

<#--doc
    Determines if an author has a surname.

    @param The model of an author (as returned by `getPublicationAuthors`).

    @return `true` if the author has a surname, `false` otherwise.
-->
<#function hasAuthorSurname author>
    <#return (author["./@surname"]?size > 0)>
</#function>

<#--doc
    Gets the surname of an author.

    @param The model of an author (as returned by `getPublicationAuthors`).

    @return A surname of an author.
-->
<#function getAuthorSurname author>
    <#return author["./@surname"]>
</#function>

<#--doc
    Determines if an author has a given name.

    @param The model of an author (as returned by `getPublicationAuthors`).

    @return `true` if the author has a given name, `false` otherwise.
-->
<#function hasAuthorGivenName author>
    <#return (author["./@givenname"]?size > 0)>
</#function>

<#--doc
    Gets the given name of an author.

    @param The model of an author (as returned by `getPublicationAuthors`).

    @return A given name of an author.
-->
<#function getAuthorGivenName author>
    <#return author["./@givenname"]>
</#function>

<#--doc
    Gets the place of the publication.

    @param The model of the publication to use (as returned by `getPublications`).

    @return The place of the publication.
-->
<#function getPublicationPlace item>
    <#return item["./place"]>
</#function>

<#--doc
    Gets the organization associated with a publication.

    @param The model of the publication to use (as returned by `getPublications`).

    @return The model of the organization associated with the publication.
-->
<#function getPublicationOrganization item>
    <#return item["./organization"]>
</#function>

<#--doc
    Gets the name of the associated organization.

    @param orga The model of the associated organization to use (as returned by `getPublicationOrganization`).

    @return The name of the organization.
-->
<#function getPublicationOrganizationName orga>
    <#return orga["./title"]>
</#function>

<#--doc
    Gets the place of the publication of type Unpublished.

    @param The model of the publication to use (as returned by `getPublications`).

    @return The place of the publication.
-->
<#function getPublicationUnpublishedPlace item>
    <#return item["./unpublished-place"]>
</#function>

<#--doc
    Gets the publisher of the publication.

    @param The model of the publication to use (as returned by `getPublications`).

    @return The model of the publisher of the publication.
-->
<#function getPublicationPublisher item>
    <#return item["./publisher"]>
</#function>

<#--doc
    Gets the place of the publisher.

    @param publisher The model of the publisher to use (as returned by `getPublicationPublisher`). 

    @return The place of the publisher.
-->
<#function getPublisherPlace publisher>
    <#return publisher["./@place"]>
</#function>

<#--doc
    Gets the name of the publisher.

    @param publisher The model of the publisher to use (as returned by `getPublicationPublisher`). 

    @return The name of the publisher.
-->
<#function getPublisherName publisher>
    <#return publisher["./@name"]>
</#function>

<#--doc
    Gets the journal associated with a publication.

    @param The model of the publication to use (as returned by `getPublications`).

    @return The model of the journal associated with the publication.
-->
<#function getPublicationJournal item>
    <#return item["./journal"]>
</#function>

<#--doc
    Gets the name of a journal.

    @param journal The model of the journal to use.

    @return The name of the journal.
-->
<#function getJournalName journal>
    <#return journal["./@name"]>
</#function>

<#--doc
    Gets the issue of the publication.

    @param The model of the publication to use (as returned by `getPublications`).

    @return The issue of the publication.
-->
<#function getPublicationIssue item>
    <#return item["./issue"].@@text>
</#function>

<#--doc
    Determines if the publication has a value for the volume of journal.

    @param The model of the publication to use (as returned by `getPublications`).

    @return `true` if the publication has a value for volume of journal, `false`
    otherwise.
-->
<#function hasPublicationVolumeOfJournal item>
    <#return (item["./volume-of-journal"]?size > 0)>
</#function>

<#--doc
    Gets the volume of the publication.

    @param The model of the publication to use (as returned by `getPublications`).

    @return The volume of the publication.
-->
<#function getPublicationVolumeOfJournal item>
    <#return item["./volume-of-journal"].@@text>
</#function>

<#--doc
    Gets the value of the `pagesFrom` property of the publication.

    @param The model of the publication to use (as returned by `getPublications`).

    @return The value of the `pagesFrom` property of the publication.
-->
<#function getPublicationPagesFrom item>
    <#if (item["./pages-from"]?size > 0)>
        <#return item["./pages-from"].@@text?number>
    <#else>
        <#return 0>
    </#if>
</#function>

<#--doc
    Gets the value of the `pagesTo` property of the publication.

    @param The model of the publication to use (as returned by `getPublications`).

    @return The value of the `pagesTo` property of the publication.
-->
<#function getPublicationPagesTo item>
    <#if (item["./pages-to"]?size > 0)>
        <#return item["./pages-to"].@@text?number>
    <#else>
        <#return 0>
    </#if>
</#function>

<#--doc
    Gets the collected volume associated with an publication.

    @param The model of the publication to use (as returned by `getPublications`).

    @return The model of the associated collected volume.
-->
<#function getPublicationCollectedVolume item>
    <#return item["./collected-volume"]>
</#function>

<#--doc
    Get the authors of a collected volume.

    @param The model of the collected volume to use (as returned by `getPublicationCollectedVolume`).

    @return A sequence of models of the authors of the collected volume. The 
    authors can be processed by `getAuthorSurname` and `getAuthorSurname`.
-->
<#function getCollectedVolumeAuthors collectedVolume>
    <#return collectedVolume["./authors/author"]>
</#function>

<#--doc
    Gets the title of a collected volume.

    @param The model of the collected volume to use (as returned by `getPublicationCollectedVolume`).

    @return The title of the collected volume.
-->
<#function getCollectedVolumeTitle collectedVolume>
    <#return collectedVolume["./title"].@@text>
</#function>

<#--doc
    Gets the publisher of a collected volume.

    @param The model of the collected volume to use (as returned by `getPublicationCollectedVolume`).

    @return The model of the publisher of the collected volume. The model 
    can be processed further by the functions for processing authors in this
    file.
-->
<#function getCollectedVolumePublisher collectedVolume>
    <#return collectedVolume["./publisher"]>
</#function>

<#--doc
    Gets the place of a collected volume.

    @param The model of the collected volume to use (as returned by `getPublicationCollectedVolume`).

    @return The place of the collected volume.
-->
<#function getCollectedVolumePlace collectedVolume>
    <#return collectedVolume["./place"]>
</#function>

<#--doc
    Determines if the provided publication has associated proceedings.

    @param The model of the publication to use (as returned by `getPublications`).

    @return `true` if the publication has associated proceedings, `false` otherwise.
-->
<#function hasProceedings item>
    <#return (item["./proceedings"]?size > 0)>
</#function>

<#--doc
    Gets the proccedings associated with an publication.

    @param The model of the publication to use (as returned by `getPublications`).

    @return The model of the associated proceedings.
-->
<#function getProceedings item>
    <#return item["./proceedings"]>
</#function>