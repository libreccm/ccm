# Freemarker functions for SciProject items

Import Path
: `/ccm-sci-types-project.ftl`

## `getBegin(item: Node): DateNode`

Returns the begin date of the project. To format the date the 
`formatDateTime` function provided by `ccm-themedirector` should be 
used.

## `getEnd(item: Node): DateNode`

Returns the end date of the project. To format the date the 
`formatDateTime` function provided by `ccm-themedirector` should be 
used.

## `getDescription(item: Node): HtmlString`

Gets the description of the project.

## `getShortDescription(item: Node): String`

Gets the short description of the project.

## `getSponsors(item: Node): Sequence<Node>`

Returns the sponsors of the project.

## `getSponsorName(sponsor: Node): String`

Returns the name of the sponsor.

## `hasSponsorFundingCode(sponsor: Node): boolean`

Determines if the sponsor has assigned a funding code to the project.

## `getSponsorFundingCode(sponsor: Node): String`

Returns the funding code of the project assigned by the sponsor.

## `getSponsorLink(sponsor: Node): String`

Gets the link to the homepage of the sponsor.

## `hasFunding(item: Node): String`

Determines if the project has a text describing the funding
of the project.

## `getFunding(item: Node): HtmlString`

Gets the text describing the funding of the project.

## `hasFundingVolume(item: Node): boolean`

Determines if the project has a value for the `fundingVolume` property.

## `getFundingVolume(item: Node): String`

Returns the value of the `fundingVolume` property.

## `getMembers(item: Node): Sequence<Node>`

Returns the members of the project.

## `getMemberRole(member: Node): String`

Gets the role of the member.

## `getMemberStatus(member: Node): String`

Gets the status of the member.

## `getMemberId(member: Node): String`

Gets the ID of the member.

## `getMemberLink(member: Node): String`

Gets a link to the detail view of the member.

## `getMemberSurname(member: Node): String`

Gets the surname of the member.

## `getMemberGivenName(member: Node): String`

Gets the given name of the member.

## `getMemberTitlePre(member: Node): String`

Gets the value of the `titlePre` property of the member.

## `getMemberTitlePost(member: Node): String`

Gets the value of the `titlePost` property of the member.

## `getInvolvedOrganizations(item: Node): Sequence<Node>`

Gets the organizations involved in the project.

## `getInvolvedOrganizationName(orga: Node): String`

Gets the name of the organization.

## `getInvolvedOrganizationLink(orga: Node): String`

Gets the link to the homepage of the organization.