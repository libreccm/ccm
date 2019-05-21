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
    <#return list["./fiters/title"]>
</#function>

<#function getYearOfPublicationFilterAvailableYears list>
    <#return list["./filters/available-years/year"]>
</#function>

<#function getYearOfPublicationFilterValue list>
    <#return list["./fiters/year"]>
</#function>

<#function getAuthorsFilterValue list>
    <#return list["./filters/authors"]>
</#function>

<#function getSort list>
    <#return list["./filters/sort"]>
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
    <#return item["./object-type"]>
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

<#function getAuthorSurname author>
    <#return author["./surname"]>
</#function>

<#function getAuthorGivenName author>
    <#return author["./givenname"]>
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
    <#return journal["./name"]>
</#function>

<#function getPublicationIssue item>
    <#return item["./issue"]>
</#function>

<#function getPublicationVolumeOfJournal item>
    <#return item["./volume-of-journal"]>
</#function>

<#function getPublicationPagesFrom item>
    <#return item["./pages-from"]>
</#function>

<#function getPublicationPagesTo item>
    <#return item["./pages-to"]>
</#function>

<#function getPublicationCollectedVolume item>
    <#return item["./collected-volume"]>
</#function>

<#function getCollectedVolumeAuthors collectedVolume>
    <#return collectedVolume["./authors/author"]>
</#function>

<#function getCollectedVolumeTitle collectedVolume>
    <#return collectedVolume["./title"]>
</#function>

<#function getCollectedVolumePublisher collectedVolume>
    <#return collectedVolume["./publisher"]>
</#function>

<#function getProceedings item>
    <#return item["./proceedings"]>
</#function>