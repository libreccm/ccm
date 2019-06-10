# Freemarker functions for SciDepartment items

Import Path
: `/ccm-sci-types-department.ftl`

## `getDescription(data: Node): HtmlString`

Returns the description of the department.

## `getShortDescription(data: Node): String`

Returns the short description of the department.

## `getDepartmentHeads(data: Node): Sequence<Node>`

Gets the heads of the department.

## `getDepartmentHeadId(head: Node): String`

Gets the ID of a head of a department.

## `getDepartmentHeadLink(head: Node): String`

Gets the link to the detail view of head of a department.

## `getDepartmentViceHeads(data: Node): Sequence<Node>`

Gets the vice heads of the department.

## `getDepartmentViceHeadId(head: Node): String`

Gets the ID of a vice head of a department.

## `getDepartmentViceHeadLink(head: Node): String`

Gets the link to the detail view of vicehead of a department.

## `getDepartmentSecretariats(data: Node): Sequence<Node>`

Gets the secretariats of the department.

## `getDepartmentSecretariatId(sec: Node): String`

Gets the ID of a secretariats of a department.

## `getDepartmentSecretariatLink(sec: Node): String`

Gets the link to the detail view of secretariats of a department.

## `getProjects(data: Node): Sequence<Node>`

Returns the list of projects assigned to the department.

## `getProjectId(project: Node): String`

Gets the id of a project.

## `getProjectLink(project: Node): String`

Returns the link to the detail view of a project.






