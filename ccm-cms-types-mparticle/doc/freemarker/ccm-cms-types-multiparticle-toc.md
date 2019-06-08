# Freemarker functions for generating the table of contents of a MultiPartArticle

Import Path
: `/ccm-cms-types-multiparticle-toc.ftl`

These functions can be used to generate the table of contents (toc) for
a multi part article. An example:

    ... // Other imports
    <#import "/ccm-cms-types-multiparticle-toc.ftl" as Toc>
    
    ... // Other things

    <#list Toc.getSections(item)>
        <div class="mpa-toc">
            <h3>${getLocalizedText("mpa.toc")}</h3>
            <ul class="mpa-toc">
                <#items as section>
                    <li>
                        <a href="${Toc.getSectionLink(section)}" 
                        class="${Toc.isActiveSection(item, section)?then('active', '')}">
                        ${Toc.getSectionTitle(section)}
                        </a>
                    </li>
                </#items>
            </ul>
        </div>
    </#list>

## `getSections(item: Node): Sequence<Node>`

Returns the sections of the provided MultiPartArticle item. The 
sections can be further be processed using the other functions 
provided by this file.

## `getSectionTitle(section: Node): String`

Gets the title of the provided section.

## `getSectionLink(section: Node): String`

Gets the link for displaying the provided section.

## `isActiveSection(section: Node): String`

Returns `true` if the provided section is the active section.