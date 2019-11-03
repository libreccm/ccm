<#ftl ns_prefixes={
"bebop":"http://www.arsdigita.com/bebop/1.0",
"cms":"http://www.arsdigita.com/cms/1.0",
"nav":"http://ccm.redhat.com/navigation",
"ui": "http://www.arsdigita.com/ui/1.0"}
>

<#import "../ccm-cms/content-item.ftl" as ContentItem>

<#--filedoc
    Functions for processing Series items.
-->

<#--doc
    Gets the filters for the volumes list of the series.

    @param series The series to use

    @return A sequence of the filters for the list of volumes of the series.
-->
<#function getFilters series>
    <#return series["./filters/filter"]>
</#function>

<#--doc
    Constructs the link to the details view of a series.

    @param series The series to use.

    @return The link to the detail view of the series.
-->
<#function getLink series>
    <#return ContentItem.generateContentItemLink(series)>
</#function>

<#--doc
    Gets the name of the series.

    @param series The series to use.

    @return The name of the series.
-->
<#function getName series>
    <#return series["./title"]>
</#function>

<#--doc
    Gets the volumes of the series.

    @param series The series to use.

    @return A sequence of the volumes of the series.
-->
<#function getVolume series>
    <#return series["./@volume"]>
</#function>

<#--doc
    Gets the link to the detail view of volume of a series.

    @param volume The volume to use.

    @return The link to the detail view of the volume.
-->
<#function getVolumeHref volume>
    <#return ContentItem.generateContentItemLink(volume)>
</#function>

