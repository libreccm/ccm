<#ftl ns_prefixes={
"bebop":"http://www.arsdigita.com/bebop/1.0",
"cms":"http://www.arsdigita.com/cms/1.0",
"nav":"http://ccm.redhat.com/navigation",
"ui": "http://www.arsdigita.com/ui/1.0"}
>

<#import ../ccm-cms/content-item.ftl as ContentItem>

<#function getFilters series>
    <#return series["./filters/filter"]>
</#function>

<#function getLink series>
    <#return ContentItem.generateContentItemLink(series)>
</#function>

<#function getName series>
    <#return series["./title"]>
</#function>

<#function getVolume series>
    <#return series["./volume"]>
</#function>

<#function getVolumeHref volume>
    <#return ContentItem.generateContentItemLink(volume)>
</#function>

