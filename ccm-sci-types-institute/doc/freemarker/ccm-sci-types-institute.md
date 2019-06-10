# Freemarker functions for SciDepartment items

Import Path
: `/ccm-sci-types-department.ftl`

## `getDescription(data: Node): HtmlString`

Returns the description of the institute.

## `getShortDescription(data: Node): String`

Returns the short description of the institute.

## `getDepartments(data: Node): Sequence<Node>`

Returns the departments assigned to a institute.

## `getDepartmentOid(department: Node): String`

Gets the OID of a department.

## `getDepartmentTitle(department: Node): String`

Gets the title of a department.

## `getDepartmentLink(department: Node): String`

Returns the link to the detail view of a department.