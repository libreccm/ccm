### ccm-cms-publicpersonalprofile

Functions for processing the data of a public personal profile.

#### getProfileOwner 

    getProfileOwner(data)

Get the data about the profile owner.

##### Parameters

`data` The public personal profile data

##### Returns

The data about the owner of the profile.

#### getProfileOwnerSurname

    getProfileOwnerSurname(owner)

Gets the surname of a profile owner.

##### Parameters

`owner` The owner.

##### Returns 

The surname of the provided profile owner.

#### getProfileOwnerGivenName

    getProfileOwnerGivenName(owner)

Gets the given name of a profile owner.

##### Parameters

`owner` The owner.

##### Returns 

The given name of the provided profile owner.

#### getProfileOwnerTitlePre

    getProfileOwnerTitlePre(owner)

Gets the titles a profile owner.

##### Parameters

`owner` The owner.

##### Returns 

The titles of the provided profile owner which preceed the name of the owner.

#### getProfileOwnerTitlePost

    getProfileOwnerTitlePost(owner)

Gets the titles a profile owner.

##### Parameters

`owner` The owner.

##### Returns 

The titles of the provided profile owner which follow the name of the owner.

#### getProfileOwnerContact

    getProfileOwnerContact(owner)

Gets the contact data of the owner. 

##### Parameters

`owner` The owner.

##### Returns

The contact data of the owner. The contact data are in the same format as a
content item of the type `ccm-cms-types-contact`. The returned data can be
processed further using the functions for content items of the type 
`ccm-cms-types-contact?`.

#### getProfileImage

    getProfileImage(data)

Return the data of the image attached to the profile, if any.

##### Parameters

`data` The data of the profile.

##### Returns

The data about the image attached to the profile if an image was attached 
to the profile. The returned data is a image attachement which can be 
processed further by the functions provided for processing image assets 
(see `ccm-cms-assets-imagestep`).
 
#### getProfileOwnerName

    getProfileOwnerName(data)

Gets the name of the profile owner.    

#### Parameters

`data` The data of the profile.

#### Returns

The name of the content item of the type `Person` assigned to the profile.

#### getPersonalPublications

    getPersonalPublications(data)

Gets the data about the personal publications of the profile owner. 

#### Parameters

`data` The data of the profile.

#### Returns

The data about the personal publications of the author, organized
in publications groups.

#### getPersonalPublicationsAvailablePublicationGroups

    getPersonalPublicationsAvailablePublicationGroups(data)

Get the available publications groups.

##### Parameters 

`data` The data of the profile.

##### Returns

A sequence of the available publication groups. They can processed further
using `getPublicationGroupId` and `getPublicationGroupLink`.

#### getPublicationGroupId

    getPublicationGroupId(group)

##### Parameters

`group` The publication group.

##### Returns

The ID of the publication group.

#### getPublicationGroupLink

    getPublicationGroupLink(group)

##### Parameters

`group` The publication group.

##### Returns

The link for showing the publications of the group.

#### getPublicationGroups

    getPublicationGroups(data)

Get all publication groups currently displayed. 

##### Parameters

`data` The data of the profile.

##### Returns

A sequence of the publication groups in the data of the profile.

#### getPublicationsOfGroup

    getPublicationsOfGroup(data)

Gets the publiations of a group. The publication can be processed
further by the functions provided by `ccm-sci-publications`.

##### Parameters

`data` The data of the group.

##### Returns

The publications of the group.

#### getAvailableProjectGroups

    getAvailableProjectGroups(data)

Get the available project groups.

##### Parameters 

`data` The data of the profile.

##### Returns

A sequence of the available project groups. They can processed further
using `getProjectGroupId` and `getProjectGroupLink`.

#### getProjectGroupId

    getProjectGroupId(group)

##### Parameters

`group` The Project group.

##### Returns

The ID of the Project group.

#### getProjectGroupLink

    getProjectGroupLink(group)

##### Parameters

`group` The Project group.

##### Returns

The link for showing the Projects of the group.

#### getProjectGroups

    getProjectGroups(data)

Get all project groups currently displayed. 

##### Parameters

`data` The data of the profile.

##### Returns

A sequence of the project groups in the data of the profile.

#### getProjectsOfGroup

    getProjectOfGroup(data)

Gets the projects of a group. The projects can be processed
further by the functions provided by `ccm-sci-types-project`.

##### Parameters

`data` The data of the group.

##### Returns

The projects of the group.