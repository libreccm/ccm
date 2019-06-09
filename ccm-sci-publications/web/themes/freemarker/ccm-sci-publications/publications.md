# Freemarker functions for publication items.

Import Path
: `/ccm-sci-publications/publications.ftl`

## `getAssignedTermsDomains(item: Node, domain: String): Sequence<Node>`

Returns the categories from the category system with the name provided 
by the `domain` parameters which are assigned to the publication.

## `getAuthors(item: Node): Sequence<Node>`

Returns the authors of the publication.

## `getPublisher(item: Node): Sequence<Node>`

Returns the publisher of the publication.

## `getYearOfPublication(item: Node): Sequence<Node>`

Returns the year of publication.

## `getNumberOfPages(item: Node): String`

Gets the number of pages of the publication.

## `getNumberOfVolumes(item: Node): String`

Gets the number of volumes of the publication.

## `getVolume(item: Node): String`

Gets the value of the `volume` property of the publication.

## `getEdition(item: Node): String`

Get the edition of the publication.

## `getIsbn(item: Node): String`

Gets the ISBN of the publication.

## `getLanguageOfPublication(item: Node): String`

Gets the language of the publication.

## `getSeries(item: Node): Node`

Gets the series to which the publication is assigned.

## `isReviewed(item: Node): boolean`

Determines if the publication is reviewed.

## `getAbstract(item: Node): String`

Returns the abstract of the publication.

## `getMisc(item: Node): String`

Returns the value of the `misc` property of the publication.

## `getExportLinks(item: Node): Sequence<Node>`

Returns the export links for the publication.

## `getPlace(item: Node): String`

Returns the value of the `place` property of the publication.

## `getPagesFrom(item: Node): String`

Returns the value of the `pagesFrom` property of the publication.

## `getPagesTo(item: Node): String`

Returns the value of the `pagesTo` property of the publication.

## `getNumber(item: Node): String`

Returns the value of the `number` property of the publication.

## `getYearFirstPublished(item: Node): String`

Returns the value of the `yearFirstPublished` property of the 
publication.

## `getLibrarySignatures(item: Node): Sequence<Node>`

Returns the library signatures assigned to a publication.

## `getOrganization(item: Node): Node`

Gets the organization assigned to a publication.

## `getOrderer(item: Node): Node`

Gets the orderer assigned to a publication.

## `getIssn(item: Node): Node`

Gets the ISSN of a publication.

## `getLastAccessed(item: Node): String`

Gets the value of the `lastAccessed` property of a publication.

## `getUrl(item: Node): String`

Gets the value of the `url` property of a publication.

## `getUrn(item: Node): String`

Gets the value of the `urn` property of a publication.

## `getDoi(item: Node): String`

Gets the value of the `doi` property of a publication.

## `getIssue(item: Node): String`

Gets the issue of a publication.

## `getJournal(item: Node): Node`

Gets the journal to which a publication is assigned.

## `getCollectedVolume(item: Node): Node`

Gets the collected volume to which a publication is assigned.

## `getChapter(item: Node): String`

Gets the value of the `chapter` property of a publication.

## `getNameOfConference(item: Node): String`

Gets the name of the conference.

## `getPlaceOfConference(item: Node): String`

Gets the place of the conference.

## `getDateFromConference(item: Node): DateNode`

Gets the start date of the conference.

## `getDateToConference(item: Node): DateNode`

Gets the end date of the conference.

## `getProceedings(item: Node): Node`

Gets the proceedings to which an publication is assigned.

## `getProceedingsPapers(item: Node): Sequence<Node>`

Gets the papers of a proceedings publication item.

## `getSeriesVolume(item: Node): String`

Gets the volume of the series for a publication.

## `getEvent(item: Node): String`

Gets the value of the `event` property of a publication of the type
_talk_.

## `getDateOfTask(item: Node): DateNode`

Gets the value of the `date` property of a publication of the type
_talk_.



