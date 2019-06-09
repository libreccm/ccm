# Freemarker functions for processing library signatures

Import Path
: `/ccm-sci-publications/library-signatures.ftl`

A library signature object contains the library signature (usually an
alpha numeric code) and additional information.

## `getLibrary(signature: Node): String`

Returns the the library of the signature.

## `getSignature(signature: Node): String`

Returns the signature itself.

## `getLibraryLink(signature: Node): String`

Returns the link to the homepage of the library.

## `getMisc(signature: Node): String`

Returns the value of the `misc` property of the signature.

