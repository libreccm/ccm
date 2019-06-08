# Freemarker functions for Portal Workspaces

Import Path
: `/ccm-portalworkspace.ftl`

## `getPortals(): Sequence<Node>`

Returns all available portals.

## `isSelected(portal: Node): boolean`

Determines if the provided portal is selected.

## `getPortalLink(portal: Node): String`

Gets the link for selecting the provided portal.

## `getPortalTitle(portal: Node): String`

Returns the title of the provided portal.

## `getPortalEditForm(): Node`

Returns the edit form for the selected portal.

## `getPortalLayoutForm(): Node`

Returns the form for editing the layout of the selected portal.

## `getAddPageLink(): String`

Returns the link for adding another portal.

## `getBasicPropertiesLink(): String`

Gets the link for the basic properties of the selected portal.

## `getPortletsFromColumn(colNumber: String): Sequence<Node>`

Returns the portals in the column `colNumber`.

## `getWorkspacePrimaryUrl(): String`

Returns the primary URL of the portal workspace.

## `getMovePortletLeftLink(portlet: Node): String`

Returns the link for moving the provided portlet left.

## `getMovePortletRightLink(portlet: Node): String`

Returns the link for moving the provided portlet right.

## `getMovePortletUpLink(portlet: Node): String`

Returns the link for moving the provided portlet up.

## `getMovePortletDownLink(portlet: Node): String`

Returns the link for moving the provided portlet down.

## `getCustomizePortletLink(portlet: Node): String`

Returns the link for customizing the portlet.

## `getDeletePortletLink(portlet: Node): String`

Returns the link for deleting the portlet.