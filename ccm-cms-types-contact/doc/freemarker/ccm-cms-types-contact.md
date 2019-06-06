# Freemarker functions for Contact items

Import path
: `/ccm-cms-types-contact.ftl`

## `getAddress(item: Node): Node`

Returns the address associated wit the provided contact item. The 
address can be processed further using the functions provided by the 
ccm-cms-types-address module.

## `getPerson(item: Node): Node`

Returns the person associated with the provided contact. The returned
person item can be processed further using functions provided by the 
ccm-cms module.

## `getContactEntries(item: Node): Sequence<Node>`

Returns the contact entries of the provided contact.

## `getContactEntry(item: Node, keyId: String): Node`

Returns the contact entry with the provided `keyId` if the provided contact has a matching contact entry. If not `null` is returned.

## `getContactEntryLabel(entry: Node): String`

Returns the label of the provided contact entry.

## `getContactEntryValue(entry: Node): String`

Returns the value of the provided contact entry.