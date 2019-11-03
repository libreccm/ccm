<#ftl ns_prefixes={
"bebop":"http://www.arsdigita.com/bebop/1.0",
"cms":"http://www.arsdigita.com/cms/1.0",
"nav":"http://ccm.redhat.com/navigation",
"ui": "http://www.arsdigita.com/ui/1.0"}
>

<#import "../ccm-cms/content-item.ftl" as ContentItem>

<#--filedoc
    Functions for processing Proceedings items.
-->

<#--doc
    Gets the URL of the detail view of the proceedings item.

    @param proceedings The proceedings item to use.

    @return The URL of the detail view of the proceedings.
-->
<#function getHref proceedings>
    <#return ContentItem.generateContentItemLink(proceedings["./@oid"])>
</#function>

<#--doc
    Gets the URL of the detail of a paper (InProceedings item) of a proceedings 
    publication.

    @param paper The InProceedings item to use.

    @return The URL of the detail view of the paper.
-->
<#function getPaperHref paper>
    <#return ContentItem.generateContentItemLink(paper["./@oid"])>
</#function>