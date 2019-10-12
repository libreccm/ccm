<#ftl ns_prefixes={
"bebop":"http://www.arsdigita.com/bebop/1.0",
"cms":"http://www.arsdigita.com/cms/1.0",
"nav":"http://ccm.redhat.com/navigation",
"ui": "http://www.arsdigita.com/ui/1.0"}
>

<#function getSciPublicationsList listId="itemList">
    <#return model["/bebop:page/nav:sci-publication-list[@id='${listId}']"]>
</#function>

<#function getPublications list>
    <#return list["./publication"]>
</#function>

<#function getTitleFilterValue list>
    <#if (list["./filters/title"]?size > 0)>
        <#return list["./filters/title"]>
    <#else>
        <#return "">
    </#if>
</#function>

<#function getYearOfPublicationFilterAvailableYears list>
    <#return list["./filters/available-years/year"]>
</#function>

<#function getYearOfPublicationFilterValue list>
    <#if (list["./fiters/year"]?size > 0)>
        <#return list["./fiters/year"].@@text>
    <#else>
        <#return "">
    </#if>
</#function>

<#function getAuthorsFilterValue list>
    <#if (list["./filters/authors"]?size > 0)>
        <#return list["./filters/authors"]>
    <#else>
        <#return "">
    </#if>
</#function>

<#function getSort list>
    <#if (list["./filters/sort"]?size > 0)>
        <#return list["./filters/sort"]>
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

<#function getPublicationId item>
    <#return item["./item-id"]>
</#function>

<#function getPublicationObjectType item>
    <#return item["./@object-type"]>
</#function>

<#function getPublicationTitle item>
    <#return item["./title"]>
</#function>

<#function getPublicationYear item>
    <#return item["./year"]>
</#function>

<#function getPublicationAuthors item>
    <#return item["./authors/author"]>
</#function>

<#function hasAuthorSurname author>
    <#return (author["./@surname"]?size > 0)>
</#function>

<#function getAuthorSurname author>
    <#return author["./@surname"]>
</#function>

<#function hasAuthorGivenName author>
    <#return (author["./@givenname"]?size > 0)>
</#function>

<#function getAuthorGivenName author>
    <#return author["./@givenname"]>
</#function>

<#function getPublicationPlace item>
    <#return item["./place"]>
</#function>

<#function getPublicationOrganization item>
    <#return item["./organization"]>
</#function>

<#function getPublicationOrganizationName orga>
    <#return orga["./title"]>
</#function>

<#function getPublicationUnpublishedPlace item>
    <#return item["./unpublished-place"]>
</#function>

<#function getPublicationPublisher item>
    <#return item["./publisher"]>
</#function>

<#function getPublisherPlace publisher>
    <#return publisher["./@place"]>
</#function>

<#function getPublisherName publisher>
    <#return publisher["./@name"]>
</#function>

<#function getPublicationJournal item>
    <#return item["./journal"]>
</#function>

<#function getJournalName journal>
    <#return journal["./@name"]>
</#function>

<#function getPublicationIssue item>
    <#return item["./issue"].@@text>
</#function>

<#function hasPublicationVolumeOfJournal item>
    <#return (item["./volume-of-journal"]?size > 0)>
</#function>

<#function getPublicationVolumeOfJournal item>
    <#return item["./volume-of-journal"].@@text>
</#function>

<#function getPublicationPagesFrom item>
    <#if (item["./pages-from"]?size > 0)>
        <#return item["./pages-from"].@@text?number>
    <#else>
        <#return 0>
    </#if>
</#function>

<#function getPublicationPagesTo item>
    <#if (item["./pages-to"]?size > 0)>
        <#return item["./pages-to"].@@text?number>
    <#else>
        <#return 0>
    </#if>
</#function>

<#function getPublicationCollectedVolume item>
    <#return item["./collected-volume"]>
</#function>

<#function getCollectedVolumeAuthors collectedVolume>
    <#return collectedVolume["./authors/author"]>
</#function>

<#function getCollectedVolumeTitle collectedVolume>
    <#return collectedVolume["./title"].@@text>
</#function>

<#function getCollectedVolumePublisher collectedVolume>
    <#return collectedVolume["./publisher"]>
</#function>

<#function getCollectedVolumePlace collectedVolume>
    <#return collectedVolume["./place"]>
</#function>

<#function hasProceedings item>
    <#return (item["./proceedings"]?size > 0)>
</#function>

<#function getProceedings item>
    <#return item["./proceedings"]>
</#function>