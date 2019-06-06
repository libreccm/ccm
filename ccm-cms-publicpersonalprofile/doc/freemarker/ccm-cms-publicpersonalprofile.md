# Freemarker functions for Public Personal Profiles

Import path
: `/ccm-cms-publicpersonalprofile.md`

Functions for processing the data of a public personal profile.

## `getProfileOwner(data: Node): Node`

Get the data about the profile owner. The return value is a XML node
which can be further processed with other functions.

## `getProfileOwnerSurname(owner: Node): String`

Gets the surname of a profile owner.

## `getProfileOwnerGivenName(owner: Node): String`

Gets the given name of a profile owner.

## `getProfileOwnerTitlePre(owner: Node): String`

Gets the titles a profile owner.

## `getProfileOwnerTitlePost(owner: Node): String`

Gets the titles a profile owner.

## `getProfilePosition(data: Node): String`

Returns the value of the `position` property of a profile.

## `getProfileInterests(data: Node): String`

Returns the value of the `interests` property of a profile. 

## `getProfileMisc(data: Node): String`

Returns the value of the `misc` property of a profile.

## `getProfileOwnerContact(owner: Node): Node`

Gets the contact data of the owner. The contact data is in the same
format as a content item of the type `ccm-cms-types-contact`. The
returned data can be processed further using the functions for content 
items of the type `ccm-cms-types-contact?`. The functions provided
by the `ccm-cms-types-contact` module can be used to process this data.

## `getProfileImage(data: Node): String`

Returns the data of the image attached to the profile, if any.
The returned data is a image attachement which can be 
processed further by the functions provided for processing image assets (see `ccm-cms-assets-imagestep`).
 
## `getProfileOwnerName(data: Node): String`

Gets the name of the profile owner which is the name of the content item of the type `Person` assigned to the profile.

## `getPersonalPublications(data: Node): Sequence<Node>`

Gets the data about the personal publications of the profile owner, 
organized in publications groups.

## `getPersonalPublicationsAvailablePublicationGroups(data: Node): Sequence<Node>`

Get the available publications groups. The items of the sequence can be processed further using `getPublicationGroupId` and 
`getPublicationGroupLink`.

## `getPublicationGroupId(group: Node): String`

Returns the ID of the publication group.

## `getPublicationGroupLink(group: Node): String`

Returns the link for showing the publications of the group.

## `getPublicationGroups(data: Node): Sequence<Node>`

Get all publication groups currently displayed. 

## `getPublicationsOfGroup(data: Node): Sequence<Node>`

Gets the publiations of a group. The publication can be processed
further by the functions provided by `ccm-sci-publications`.

## `hasPublicationsPaginator(profile: Node): boolean`

Determines if there is paginator for the current publication group.

## `getPublicationsPaginatorBaseUrl(profile: Node): String`

Returns the base URL for the publications paginator.

## `getPublicationsPaginatorPageCount(profile: Node): String`

Returns the number of pages from the publications paginatator.

## `getPublicationsPaginatorPageNumber(profile: Node): String`

Returns the current page of the  current publication group.

## `getPublicationsPaginatorPageParam(profile: Node): String`

Gets the name of the URL parameter for changing the current page.

## `getPublicationsPaginatorPageSize(profile: Node): String`

Gets the page size.

## `getPublicationsPaginatorObjectBegin(profile: Node): String`

Gets the index of the first displayed item of current publication group.

## `getPublicationsPaginatorObjectCount(profile: Node): String`

Gets the index of the number of items in the current publication group.

## `getPublicationsPaginatorObjectEnd(profile: Node): String`

Gets the index of the last displayed item of current publication group.

## `getPublicationsPaginatorPrevPageLink(profile: Node): String`

Gets the link to the previous page of the current publication group.

## `getPublicationsPaginatonFirstPageLink(profile. Node): String`

Gets the link the first page of the current publication group.

## `getPublicationsPaginatorNextPageLink(profile: Node): String`

Gets the link to the next page of the current publication group.

## `getPublicationsPaginatorLastPageLink(profile: Node): String`

Gets the link to the last page of the current publication group.

## `getAvailableProjectGroups(data: Node): Sequence<Node>`

Returns the available project groups. The sequence can be processed 
further using `getProjectGroupId` and `getProjectGroupLink`.

## `getProjectGroupId(group: Node): String`

Returns the ID of the Project group.

## `getProjectGroupLink(group: Node): String`

Returns the link for showing the Projects of the group.

#### `getProjectGroups(data: Node): String`

Gets all project groups currently displayed. 

## `getProjectsOfGroup(data: Node): Sequence<Node>`

Gets the projects of a group. The projects can be processed
further by the functions provided by `ccm-sci-types-project`.

## `hasProjectsPaginator(profile: Node): boolean`

Determines if the current project group has a paginator.

## `getProjectsPaginatorBaseUrl(profile: Node): String`

Returns the base URL for the projects paginator.

## `getProjectsPaginatorPageCount(profile: Node): String`

Returns the number of pages from the project paginatator.

## `getProjectsPaginatorPageNumber(profile: Node): String`

Returns the current page of the  current project group.

## `getProjectsPaginatorPageParam(profile: Node): String`

Gets the name of the URL parameter for changing the current page.

## `getProjectsPaginatorPageSize(profile: Node): String`

Gets the page size.

## `getProjectsPaginatorObjectBegin(profile: Node): String`

Gets the index of the first displayed item of current project group.

## `getProjectsPaginatorObjectCount(profile: Node): String`

Gets the number of items in the current project group.

## `getProjectsPaginatorObjectEnd(profile: Node): String`

Gets the index of the last displayed item of current project group.

## `getProjectsPaginatorPrevPageLink(profile: Node): String`

Gets the link to the previous page of the current project group.

## `getProjectsPaginatonFirstPageLink(profile: Node): String`

Gets the link the first page of the current project group.

## `getProjectsPaginatorNextPageLink(profile: Node): String`

Gets the link to the next page of the current project group.

## `getProjectsPaginatorLastPageLink(profile: Node): String`

Gets the link to the last page of the current project group.