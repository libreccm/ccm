<#ftl ns_prefixes={
"bebop":"http://www.arsdigita.com/bebop/1.0",
"cms":"http://www.arsdigita.com/cms/1.0",
"nav":"http://ccm.redhat.com/navigation",
"ui": "http://www.arsdigita.com/ui/1.0"}
>

<#import ../ccm-cms.ftl as CMS>

<#function getTitle journal>
    <#return journal["./title"]>
</#function>

<#function getHref journal>
    <#return CMS.generateContentItemLink(journal["./@oid"])>
</#function>

