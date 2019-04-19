<#ftl ns_prefixes={
"bebop":"http://www.arsdigita.com/bebop/1.0",
"cms":"http://www.arsdigita.com/cms/1.0",
"nav":"http://ccm.redhat.com/navigation",
"ui": "http://www.arsdigita.com/ui/1.0"}
>

<#function getAssignedTermsDomains item domain>
    <#return item["./terms:assignedTerms/terms:term[@domain='${domain}']]>
</#function>

<#function getAuthors item>
    <#return item["./authors/author"]>
</#function>

<#function getPublisher item>
    <#return item["./publisher"]>
</#function>

<#function getYearOfPublication item>
    <#return item["./yearOfPublication"]>
</#function>

<#function getNumberOfPages item>
    <#return item["./numberOfPages"]>
</#function>

<#function getNumberOfVolumes item>
    <#return item["./numberOfVolumes"]>
</#function>

<#function getVolume item>
    <#return item["./volume"]>
</#function>

<#function getEdition item>
    <#return item["./edition"]>
</#function>

<#function getIsbn item>
    <#return item["./isbn"]>
</#function>

<#function getLanguageOfPublication item>
    <#return item["./languageOfPublication"]>
</#function>

<#function getSeries item>
    <#return item["./series/series"]>
</#function>

<#function isReviewed item>
    <#return item["./reviewed"] == "true">
</#function>

<#function getAbstract item>
    <#return item["./abstract"]>
</#function>

<#function getMisc item>
    <#return item["./misc"]>
</#function>

<#function getExportLinks item>
    <#return item["./publicationExportLink"]>
</#function>

<#function getPlace item>
    <#return item["./place"]>
</#function>

<#function getPagesFrom item>
    <#return item["./pagesFrom"]>
</#function>

<#function getPagesTo item>
    <#return item["./pagesTo"]>
</#function>

<#function getNumber item>
    <#return item["./number"]>
</#function>

<#function getYearFirstPublished item>
    <#return item["./yearFirstPublished"]>
</#function>

<#function getLibrarySignatures item>
    <#return item["./librarysignatures/librarysignatures"]>
</#function>

<#function getOrganization item>
    <#return item["./organization"]>
</#function>

<#function getOrderer item>
    <#return item["./orderer]>
</#function>

<#function getIssn item>
    <#return item["./issn"]>
</#function>

<#function getLastAccessed item>
    <#return item["./lastAccessed"]>
</#function>

<#function getUrl item>
    <#return item["./url"]>
</#function>

<#function getUrn item>
    <#return item["./getUrn"]>
</#function>

<#function getDoi item>
    <#return item["./doi"]>
</#function>

<#function getIssue item>
    <#return item["./issue"]>
</#function>

<#function getJournal item>
    <#return item["./journal"]>
</#function>

<#function getCollectedVolume item>
    <#return item["./collectedVolume"]>
</#function>

<#function getChapter item>
    <#return item["./chapter"]>
</#function>

<#function getNameOfConference item>
    <#return item["./nameOfConference"]>
</#function>

<#function getPlaceOfConference item>
    <#return item["./placeOfConference"]>
</#function>

<#function getDateFromOfConference item>
    <#return item["./dateFromOfConference"]>
</#function>

<#function getDateToOfConference item>
    <#return item["./dateToOfConference"]>
</#function>

<#function getProceedings item>
    <#return item["./proceedings"]>
</#function>

<#function getArticles item>
    <#return item["./articles/article"]>
</#function>

<#function getProceedigingsPapers item>
    <#return item["./papers/paper"]>
</#function>

<#function getSeriesVolumes item>
    <#return item["./volumes/publication"]>
</#function>

<#function getEvent item>
    <#return item["./event"]>
</#function>

<#function getDateOfTalk item>
    <#return item["./dateOfTalk"]>
</#function>