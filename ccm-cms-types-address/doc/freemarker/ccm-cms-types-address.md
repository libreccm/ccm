# Freemarker functions for ccm-cms-types-address

Import Path
: `/ccm-cms-type-address.ftl`

## `getAddressText(item: Node): String`

Returns the value of the `text` property of the address.

## `getCity(item: Node): String`

Returns the value of the `city` property of the address.

## `getPostalCode(item: Node): String`

Gets the postal code of the address.

## `getState(item: Node): String`

Gets the value of the `state` property of the address. (state means the a federal state or the equivialent here, for example California in the USA oder Lower Saxony in Germany)

## `getCountry(item: Node): String`

The country of the address.

## `getIsoCountryCode(item: Node): String`

Gets the ISO country code for the country of the address.