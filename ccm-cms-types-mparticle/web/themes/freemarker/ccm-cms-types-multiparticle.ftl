<#ftl ns_prefixes={
"bebop":"http://www.arsdigita.com/bebop/1.0",
"cms":"http://www.arsdigita.com/cms/1.0",
"nav":"http://ccm.redhat.com/navigation",
"ui": "http://www.arsdigita.com/ui/1.0"}
>

<#function getSummary item>
    <#if (item["./summary"]?size > 0)>
        <#return item["./summary"]>
    <#elseif (item["./nav:attribute[@name = 'summary']"]?size > 0)>
        <#return item["./nav:attribute[@name = 'summary']"]>
    <#else>
        <#return "">
    </#if>
</#function>

<#function getSections item>
    <#return item["./cms:articleSectionPanel/cms:item"]>
</#function>

<#function getSectionTitle section>
    <#return section["./title"]>
</#function>

<#function getSectionContent section>
    <#return section["./text/content"]>
</#function>

<#function getSectionRank section>
    <#return section["./rank"]>
</#function>

<#function getPageNumber item>
    <#return item["./articleSectionPanel/@pageNumber"]>
</#function>

<#function getNumberOfPages item>
    <#return item["./cms:mpadata/numberOfPages"]>
</#function>

<#function hasPreviousPage item>
    <#return (item["./cms:articleSectionPanel/@pageNumber"] != "all" && item["./cms:articleSectionPanel/@pageNumber"]?number > 1)>
</#function>

<#function hasNextPage item>
    <#return (item["./cms:articleSectionPanel/@pageNumber"] != "all" && item["./cms:articleSectionPanel/@pageNumber"]?number < item["./cms:mpadata/numberOfPages"]?number)>
</#function>

<#function hasMultiplePages item>
    <#return (item["./cms:articleSectionPanel/@pageNumber"] != "all") && item["./cms:articleSectionPanel/@pageNumber"] != "1">
</#function>

<#function getLinkToPreviousPage item>
    <#return "?page${item['./cms:articleSectionPanel/@pageNumber']?number - 1}">
</#function>

<#function getLinkToNextPage item>
    <#return "?page${item['./cms:articleSectionPanel/@pageNumber']?number + 1}">
</#function>

<#function getAllSectionsLink item>
    <#return "?page=all">
</#function>

