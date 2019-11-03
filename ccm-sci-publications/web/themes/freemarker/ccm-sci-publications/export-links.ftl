<#ftl ns_prefixes={
"bebop":"http://www.arsdigita.com/bebop/1.0",
"cms":"http://www.arsdigita.com/cms/1.0",
"nav":"http://ccm.redhat.com/navigation",
"ui": "http://www.arsdigita.com/ui/1.0"}
>

<#--filedoc
    Funtions for processing the export links for publications.
-->

<#--doc
    Gets the export links of a publication.

    @param item The publication item to use.

    @return A sequence of the export links of the publication.
-->
<#function getExportLinks item>
    <#return item["./publicationExportLink"]>
</#function>

<#--doc
    Determines if the provided publication item has export links.

    @param item The publication item to use.

    @return `true` if the publication item has export links, `false` otherwise.
-->
<#function hasExportLinks item>
    <#return (item["./publicationExportLink"]?size > 0)>
</#function>

<#--doc
    Constructs the URL for the export link.

    @param exportLink The export link to use (as returned by `getExportLinks`).

    @return The URL of the export link.
-->
<#function getHref exportLink>
    <#return "${dispatcherPrefix}/scipublications/export/?format=${exportLink['./formatKey']}&publication=${exportLink['./publicationId']}">
</#function>

<#--doc
    Gets the format key of an export link.

    @param exportLink The export link to use (as returned by `getExportLinks`).

    @return The key for the format provided by the export link.
-->
<#function getFormatKey exportLink>
    <#return exportLink["./formatKey"]>
</#function>

<#--doc
    Gets the format name of an export link.

    @param exportLink The export link to use (as returned by `getExportLinks`).

    @return The of the export format provided by the export link.
-->
<#function getFormatName exportLink>
    <#return exportLink["./formatName"]>
</#function>

