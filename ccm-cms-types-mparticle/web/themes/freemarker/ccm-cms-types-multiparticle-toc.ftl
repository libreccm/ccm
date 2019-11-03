<#ftl ns_prefixes={
"bebop":"http://www.arsdigita.com/bebop/1.0",
"cms":"http://www.arsdigita.com/cms/1.0",
"nav":"http://ccm.redhat.com/navigation",
"ui": "http://www.arsdigita.com/ui/1.0"}
>

<#--filedoc
    Functions for creating the table of contents of a multi part article.
-->

<#--doc
    Gets the sections of a multi part article.

    @param item The model of the multi part article to use.

    @return The sections of the multi part article.
-->
<#function getSections item>
    <#return item["./cms:articleSectionPanel/toc/section"]>
</#function>

<#--doc
    Gets the title of a section.

    @param section The model of the section as returned by `getSections`.

    @return The title of the section.
-->
<#function getSectionTitle section>
    <#return section>
</#function>

<#--doc
    Gets the link for the section.

    @param section The model of the section as returned by `getSections`.

    @return The link for the section.
-->
<#function getSectionLink section>
    <#return section["./@link"]>
</#function>

<#--doc
    Determines of the provided section is the active section.

    @param item The model of the multi part article to use.

    @param section The model of the section as returned by `getSections`.

    @return `true` if the provided section is the active section, `false` otherwise.
-->
<#function isActiveSection item section>
    <#return (section["./@rank"].@@text == item["./rank"].@@text)>
</#function>