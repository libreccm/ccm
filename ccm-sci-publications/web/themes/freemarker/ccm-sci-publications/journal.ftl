<#ftl ns_prefixes={
"bebop":"http://www.arsdigita.com/bebop/1.0",
"cms":"http://www.arsdigita.com/cms/1.0",
"nav":"http://ccm.redhat.com/navigation",
"ui": "http://www.arsdigita.com/ui/1.0"}
>

<#import "../ccm-cms/content-item.ftl" as ContentItem>

<#function getFirstYear(journal)>
    <#return journal["./firstYear"]>
</#function>

<#function getHref journal>
    <#return ContentItem.generateContentItemLink(journal["./@oid"])>
</#function>

<#function getIssn journal>
    <#return journal["./issn"]>
</#function>

<#function getLastYear(journal)>
    <#return journal["./lastYear"]>
</#function>

<#function getTitle journal>
    <#return journal["./title"]>
</#function>



