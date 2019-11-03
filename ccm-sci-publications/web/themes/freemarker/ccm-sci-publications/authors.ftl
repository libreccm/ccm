<#ftl ns_prefixes={
"bebop":"http://www.arsdigita.com/bebop/1.0",
"cms":"http://www.arsdigita.com/cms/1.0",
"nav":"http://ccm.redhat.com/navigation",
"ui": "http://www.arsdigita.com/ui/1.0"}
>

<#--filedoc
    Functions for processing the authors of a publication.
-->

<#--doc
    Constructs the link the homepage of the author.

    @param author The author to use.

    @param keyId The `keyId` of the contact entry providing the link to the homepage.

    @return The link to the homepage of the author.
-->
<#function getLink author keyId>
    <#return author["./contacts/contact/contactentries[./keyId = '${keyId}']/value"]>
</#function>

<#--doc
    Determines if an author has a link to a homepage.

    @param author The author to use.

    @param keyId The `keyId` of the contact entry providing the link to the homepage.

    @return `true` if the author has a link to a homepage, `false` otherwise.
-->
<#function hasLink author keyId>
    <#return (author["./contacts/contact/contactentries[./keyId = '${keyId}']"]?size > 0)>
</#function>

<#--doc
    Gets the ID of the author.

    @param author The author to use.

    @return The ID of the author.
-->
<#function getId author>
    <#return author["./masterVersion/id"] + "_" + author["./@name"]> 
</#function>

<#--doc
    Gets the position of the author in the sequence of authors.

    @param author The author to use.

    @return The position of the author.
-->
<#function getPosition author>
    <#return author["./position()"]>
</#function>

<#--doc
    Determines if the author is the last author in the sequence.

    @param author The author to use.

    @return `true` if the author is the last in the sequence, `false`otherwise.
-->
<#function isLast author>
    <#return author["./last()"]>
</#function>

<#--doc
    Determines if the author has a surname.

    @param author The author to use.

    @return `true` if the author has a surname, `false`otherwise.
-->
<#function hasSurname author>
    <#return (author["./surname"]?size > 0)>
</#function>

<#--doc
    Gets the surname of the author.

    @param author The author to use.

    @return The surname of the author.
-->
<#function getSurname author>
    <#return author["./surname"]>
</#function>

<#--doc
    Determines if the author has a givnen name.

    @param author The author to use.

    @return `true` if the author has a given name, `false`otherwise.
-->
<#function hasGivenName author>
    <#return (author["./givenname"]?size > 0)>
</#function>

<#--doc
    Gets the given name of the author.

    @param author The author to use.

    @return The given name of the author.
-->
<#function getGivenName author>
    <#return author["./givenname"]>
</#function>

<#--doc
    Determines if the author is an editor.

    @param author The author to use.

    @return `true` if the author an editor, `false`otherwise.
-->
<#function isEditor author>
    <#return author["./@isEditor"] == "true">
</#function>

