<#ftl ns_prefixes={
"bebop":"http://www.arsdigita.com/bebop/1.0",
"cms":"http://www.arsdigita.com/cms/1.0",
"nav":"http://ccm.redhat.com/navigation",
"ui": "http://www.arsdigita.com/ui/1.0"}
>

<#function getSections item>
    <#return item["./cms:articleSectionPanel/toc/section"]>
</#function>

<#function getSectionTitle section>
    <#return section>
</#function>

<#function getSectionLink section>
    <#return section["./@link"]>
</#function>

<#function isActiveSection item section>
    <#return (section["./@rank"].@@text == item["./rank"].@@text)>
</#function>