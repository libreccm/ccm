<#ftl ns_prefixes={
"bebop":"http://www.arsdigita.com/bebop/1.0",
"cms":"http://www.arsdigita.com/cms/1.0",
"nav":"http://ccm.redhat.com/navigation",
"ui": "http://www.arsdigita.com/ui/1.0"}
>

<#--filedoc
    Commons functions for publication types. Some functions in this
    module will only work for specific publication types.
-->

<#--doc
    Gets the assigned terms of an publication.

    @param item The publication item to use.

    @param domain The domain to use.

    @return The assigned terms.
-->
<#function getAssignedTermsDomains item domain>
    <#return item["./terms:assignedTerms/terms:term[@domain='${domain}']"]>
</#function>

<#--doc
    Gets the authors of the publication.

    @param item The publication item to use.

    @return A sequence of the authors of the publication. The authors can
    be further processed by the functions provided by the `authors` library.
-->
<#function getAuthors item>
    <#return item["./authors/author"]>
</#function>

<#--doc
    Gets the publisher of an publication.

    @param item The publication item to use.

    @return The publisher of the publication. The `publisher` library
    provides functions for processing the publisher.
-->
<#function getPublisher item>
    <#return item["./publisher"]>
</#function>

<#--doc
    Gets the value of the `yearOfPublication` property of the publication.

    @param item The publication item to use.

    @return The value of the `yearOfPublication` property of the publication.
-->
<#function getYearOfPublication item>
    <#return item["./yearOfPublication"]>
</#function>

<#--doc
    Gets the value of the `numberOfPages` property  of the publication.

    @param item The publication item to use.

    @return The value of the `numberOfPages` property of the publication.
-->
<#function getNumberOfPages item>
    <#return item["./numberOfPages"]>
</#function>

<#--doc
    Gets the value of the `numberOfVolumes` property of the publication.

    @param item The publication item to use.

    @return The value of the `numberOfVolumes` property of the publication.
-->
<#function getNumberOfVolumes item>
    <#return item["./numberOfVolumes"]>
</#function>

<#--doc
    Gets the value of the `volume` property of the publication.

    @param item The publication item to use.

    @return The value of the `volume` property of the publication.
-->
<#function getVolume item>
    <#return item["./volume"]>
</#function>

<#--doc
    Gets the value of the `edition` property of the publication.

    @param item The publication item to use.

    @return The value of the `edition` property of the publication.
-->
<#function getEdition item>
    <#return item["./edition"]>
</#function>

<#--doc
    Gets the value of the `isbn` property of the publication.

    @param item The publication item to use.

    @return The value of the `isbn` property of the publication.
-->
<#function getIsbn item>
    <#return item["./isbn"]>
</#function>

<#--doc
    Gets the value of the `languageOfPublication` property of the publication.

    @param item The publication item to use.

    @return The value of the `languageOfPublication` property of the publication.
-->
<#function getLanguageOfPublication item>
    <#return item["./languageOfPublication"]>
</#function>

<#--doc
    Gets the series associated with publication.

    @param item The publication item to use.

    @return A sequence of series associated with the publication. The `series` 
    module provides functions for processing the returned data.
--> 
<#function getSeries item>
    <#return item["./series/series"]>
</#function>

<#--doc
    Gets the value of the `reviewed` property of the publication.

    @param item The publication item to use.

    @return The value of the `reviewed` property of the publication.
-->
<#function isReviewed item>
    <#if (item["./reviewed"]?size > 0)>
        <#return item["./reviewed"] == "true">
    <#else>
        <#return false>
    </#if>
</#function>

<#--doc
    Gets the value of the `abstract` property of the publication.

    @param item The publication item to use.

    @return The value of the `abstract` property of the publication.
-->
<#function getAbstract item>
    <#return item["./abstract"]>
</#function>

<#--doc
    Gets the value of the `misc` property of the publication.

    @param item The publication item to use.

    @return The value of the `misc` property of the publication.
-->
<#function getMisc item>
    <#return item["./misc"]>
</#function>

<#--doc
    Determines if the publication has a value of the `languageOfPublicatiopn` property.

    @param item The publication item to use.

    @return `true` if the publication has a value for the `languageOfPublication`.
-->
<#function hasPlace item>
    <#return (item["./place"]?size > 0)>
</#function>

<#--doc
    Gets the value of the `place` property of the publication.

    @param item The publication item to use.

    @return The value of the `place` property of the publication.
-->
<#function getPlace item>
    <#return item["./place"].@@text>
</#function>

<#--doc
    Gets the value of the `pagesFrom` property of the publication.

    @param item The publication item to use.

    @return The value of the `getPagesFrom` property of the publication.
-->
<#function getPagesFrom item>
    <#return item["./pagesFrom"]>
</#function>

<#--doc
    Gets the value of the `pagesTo` property of the publication.

    @param item The publication item to use.

    @return The value of the `pagesTo` property of the publication.
-->
<#function getPagesTo item>
    <#return item["./pagesTo"]>
</#function>

<#--doc
    Gets the value of the `number` property of the publication.

    @param item The publication item to use.

    @return The value of the `number` property of the publication.
-->
<#function getNumber item>
    <#return item["./number"]>
</#function>

<#--doc
    Gets the value of the `yearFirstPublished` property of the publication.

    @param item The publication item to use.

    @return The value of the `yearFirstPublished` property of the publication.
-->
<#function getYearFirstPublished item>
    <#return item["./yearFirstPublished"]>
</#function>

<#--doc
    Gets the library signatures of the publication.

    @param item The publication item to use.

    @return A sequence of the library signatures associated with the 
    publication. The `librarySignatures` module provides functions for 
    processing library signatures.
-->
<#function getLibrarySignatures item>
    <#return item["./librarysignatures/librarysignatures"]>
</#function>

<#--doc
    Gets the organizations associated with the publication.

    @param item The publication item to use.

    @return A sequence of organizations associated with the publication.
-->
<#function getOrganization item>
    <#return item["./organization"]>
</#function>

<#--doc
    Gets the orderer of the publication.

    @param item The publication item to use.

    @return A sequence or the orderers (organizations) of the publication.
-->
<#function getOrderer item>
    <#return item["./orderer"]>
</#function>

<#--doc
    Gets the value of the `issn` property of the publication.

    @param item The publication item to use.

    @return The value of the `issn` property of the publication.
-->
<#function getIssn item>
    <#return item["./issn"]>
</#function>

<#--doc
    Gets the value of the `lastAccesed` property of the publication.

    @param item The publication item to use.

    @return The value of the `lastAccessed` property of the publication.
-->
<#function getLastAccessed item>
    <#return item["./lastAccessed"]>
</#function>

<#--doc
    Gets the value of the `url` property of the publication.

    @param item The publication item to use.

    @return The value of the `url` property of the publication.
-->
<#function getUrl item>
    <#return item["./url"]>
</#function>

<#--doc
    Gets the value of the `urn` property of the publication.

    @param item The publication item to use.

    @return The value of the `urn` property of the publication.
-->
<#function getUrn item>
    <#return item["./getUrn"]>
</#function>

<#--doc
    Gets the value of the `doi` property of the publication.

    @param item The publication item to use.

    @return The value of the `doi` property of the publication.
-->
<#function getDoi item>
    <#return item["./doi"]>
</#function>

<#--doc
    Gets the value of the `issue` property of the publication.

    @param item The publication item to use.

    @return The value of the `issue` property of the publication.
-->
<#function getIssue item>
    <#return item["./issue"]>
</#function>

<#--doc
    Gets the journal associated with a publication.

    @param item The publication item to use.

    @return The Jornal associated with the the publication. The `journal` 
    module provides function for processing the journal.
-->
<#function getJournal item>
    <#return item["./journal"]>
</#function>

<#--doc
    Gets the collected volume associated with a publication.

    @param item The publication item to use.

    @return The collected volume associated with the publication. The
    `collected-volume` module provides functions for processing the 
    collected volume.
-->
<#function getCollectedVolume item>
    <#return item["./collectedVolume"]>
</#function>

<#--doc
    Gets the value of the `chapter` property of the publication.

    @param item The publication item to use.

    @return The value of the `chapter` property of the publication.
-->
<#function getChapter item>
    <#return item["./chapter"]>
</#function>

<#--doc
    Gets the value of the `nameOfConference` property of the publication.

    @param item The publication item to use.

    @return The value of the `nameOfConference` property of the publication.
-->
<#function getNameOfConference item>
    <#return item["./nameOfConference"]>
</#function>

<#--doc
    Gets the value of the `placeOfConference` property of the publication.

    @param item The publication item to use.

    @return The value of the `placeOfConference` property of the publication.
-->
<#function getPlaceOfConference item>
    <#return item["./placeOfConference"]>
</#function>

<#--doc
    Gets the value of the `dateFromOfConference` property of the publication.

    @param item The publication item to use.

    @return The value of the `dateFromOfConference` property of the publication.
-->
<#function getDateFromOfConference item>
    <#return item["./dateFromOfConference"]>
</#function>

<#--doc
    Gets the value of the `dateToOfConference` property of the publication.

    @param item The publication item to use.

    @return The value of the `dateToOfConference` property of the publication.
-->
<#function getDateToOfConference item>
    <#return item["./dateToOfConference"]>
</#function>

<#--doc
    Gets the Proceedings item associated with a InProceedings publication

    @param item The publication item to use.

    @return The Proceedings item associated with a InProceedings publication.
-->
<#function getProceedings item>
    <#return item["./proceedings"]>
</#function>

<#--doc
    Gets the articles of a CollectedVolume, item or Journal.

    @param item The publication item to use. 

    @return A sequence of articles.
-->
<#function getArticles item>
    <#return item["./articles/article"]>
</#function>

<#--doc
    Gets the papers of a Proceedings papers

    @param item The publication item to use. 

    @return A sequence of InProccedings items.
-->
<#function getProceedingsPapers item>
    <#return item["./papers/paper"]>
</#function>

<#--doc
    Gets the volumes of a series.

    @param item The series to use.

    @return A sequence of the publications in the series.
-->
<#function getSeriesVolumes item>
    <#return item["./volumes/publication"]>
</#function>

<#--doc
    Gets the value of the `event` property of the publication.

    @param item The publication item to use.

    @return The value of the `event` property of the publication.
-->
<#function getEvent item>
    <#return item["./event"]>
</#function>

<#--doc
    Gets the value of the `dateOfTalk` property of the publication.

    @param item The publication item to use.

    @return The value of the `dateOfTalk` property of the publication.
-->
<#function getDateOfTalk item>
    <#return item["./dateOfTalk"]>
</#function>