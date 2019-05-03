### ccm-cms-assets-relatedlink

Functions for processing the related links assigned to a content item.

#### getRelatedLinks

    getRelatedLinks(item, linkListName="NONE")

Retrieves the related links assigned to a content item.

##### Parameters

`item` The content item

`linkListName` The related links of a content item can be organized in 
link lists. The default list (if no other is used) is `NONE` which is 
the default value of this parameter. Only the links which belong to the 
provided group are returned.

##### Returns

The related links of the provided `linkListGroup` of the provided content 
item.

#### getLinkType 

    getLinkType(link)

Gets the type of the provided link which can either be `externalLink`,
`internalLink` or `caption`.

##### Parameters

`link` The link

##### Returns 

The type of the provided link.

#### getLinkTitle

    getLinkTitle(link)

Gets the title of the provided link.

##### Parameters

`link` The link.

##### Returns

The title of the provided link.

#### getLinkDescription

    getLinkDescription(link)

Gets the description of the provided link.

##### Parameters

`link` The link.

##### Returns

The description of the provided link.

#### getLinkOrder

    getLinkOrder(link)

Gets the order value of the provided link.

##### Parameters

`link` The link.

##### Returns

The order value of the provided link.

#### getInternalLinkParameters

    getLinkInternalLinkParameters(link)

Gets the parameters of the of the provided link.

##### Parameters

`link` The link.

##### Returns

The parameters to add to the URL of the provided link.

#### getTargetUri

    getTargetUri(link)

Gets the URI of the target of the provided link.

##### Parameters

`link` The link.

##### Returns

The URI of the target of the provided link.

