# Freemarker functions for native SQL based publication lists

Import Path
: `/ccm-sci-publications-navigation.ftl`

## `getSciPublicationsList(listId: String = "itemList"): Node`

Retrieves a publications list. The list to use can be selected using 
the optional `listId` parameter.

## `getPublications(list: Node): Sequence<Node>`

Returns the publications of the provided publication list.

## `getTitleFilterValue(list: Node): String`

Returns the value of the title filter of the provided publication list.

## `getYearOfPublicationFilterValue(list: Node): String`

Returns the value of the year of publication filter of the provided 
publication list.

## `getAuthorsFilterValue(list: Node): String`

Returns the value of the authors filter of the provided 
publication list.

## `getSort(list: Node): String`

Returns the property which is used to sort the list.

## `getCount(list: Node): String`

Returns the number of publications in the list.

## `getCurrentPage(list: Node): String`

Gets the number of the current page of the list.

## `getLimit(list: Node): String`

Returns the maximum number of publications per page.

## `getMaxPages(list: Node): String`

Returns the number of pages of the list.

## `getNextPageLink(list: Node): String`

Returns the link to the next page of the list.

## `getNextPageLink(list: Node): String`

Returns the link to the previous page of the list.

## `getOffset(list: Node): String`

Gets the index of the first publication of the current page.

## `getPublicationId(item: Node): String

Returns the ID of the provided publication item.

## `getPublicationObjectType(item: Node): String`

Returns the type of the provided publication item.

## `getPublicationTitle(item: Node): String`

Returns the title of the provided publication item.

## `getPublicationYear(item: Node): String`

Gets the year of publication of the publication.

## `getPublicationAuthors(item: Node): Sequence<Node>`

Gets the authors of the publication.

## `hasAuthorSurname(author: Node): boolean`

Determines if the provided author has a surname.

## `getAuthorSurname(author: Node): String`

Gets the surname of the author.

## `hasAuthorGivenName(author: Node): boolean`

Determines if the provided author has a given name.

## `getAuthorGivenName(author: Node): String`

Gets the given name of the author.

## `getPublicationPlace(item: Node): String`

Gets the value of the place property of the publication.

## `getPublicationOrganization(item: Node): Node`

Getsh the organization assigned to a publication.

## `getPublicationOrganizationName(item: Node): String`

Gets the name of the organization.

## `getPublicationUnpublishedPlace(item: Node): String`

Gets the place of the publication of the type `UnPublished`.

## `getPublicationPublisher(item: Node): String`

Gets the publisher of the publication.

## `getPublisherPlace(item: Node): String`

Gets the place of the publisher.

## `getPublisherName(item: Node): String`

Gets the name of the publisher.

## `getPublicationJournal(item: Node): Node`

Gets the journal to which the publication is assigned.

## `getJournalName(journal: Node): String`

Gets the name of the journal.

## `getPublicationIssue(item: Node): String`

Gets the issue in which the publication was published.

## `hasPublicationVolumeOfJournal(item: Node): String`

Determines if the publication has a value for the `volume` property.

## `getPublicationVolumeOfJournal(item: Node): String`

Returns the value of the `volume` property.

## `getPublicationPagesFrom(item: Node): String`

Gets the value of the `pageFrom` property.

## `getPublicationPagesTo(item: Node): String`

Gets the value of the `pageTo` property.

## `getPublicationCollectedVolume(item: Node): Node`

Gets the collected volume to which the publication is assigned.

## `getCollectedVolumeAuthors(collectedVolume: Node): Sequence<Node>`

Returns the authors/editors of the collected volume.

## `getCollectedVolumeTitle(collectedVolume: Node): String`

Returns the title of the collected volume.

## `getCollectedVolumePublisher(collectedVolume: Node): Node`

Returns the publisher of the collected volume.

## `getCollectedVolume(collectedVolume: Node): String`

Gets the place of the collected volume.

## `hasProceedings(item: Node): boolean`

Determines if the publication has proceedings.

## `getProceedigns(item: Node): Sequence<Node>`

Returns the proceedings the publication.





