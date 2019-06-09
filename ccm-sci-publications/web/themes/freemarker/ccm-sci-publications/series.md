# Freemarker functions for Series

Import Path
: `/ccm-sci-publications/series.ftl`

## `getFilters(series: Node): Sequence<Node>`

Returns the filters for list of publications of the series. The 
filters can be procesed by the functions provided for object list 
filters.

## `getLink(series: Node): String`

Returns the link to the detail view of the series.

## `getName(series: Node): String`

Returns the name of a series.

## `getVolume(series: Node): String`

Gets the value of the `volume` property.

## `getVolumeHref(volume: Node): String`

Gets a link to a volume of a series.

