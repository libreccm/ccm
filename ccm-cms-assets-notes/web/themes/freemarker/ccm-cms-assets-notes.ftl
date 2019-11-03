<#ftl ns_prefixes={
"bebop":"http://www.arsdigita.com/bebop/1.0",
"cms":"http://www.arsdigita.com/cms/1.0",
"nav":"http://ccm.redhat.com/navigation",
"ui": "http://www.arsdigita.com/ui/1.0"}
>

<#--filedoc
    Functions for processing note attachments.
-->

<#--doc
    Generates a sorted sequence of hashes (see Freemarker documentation) for 
    the note attachments of a content item.

    @param item The model of the content item to use.

    @return A sorted sequence of note attachments of the provided content item.
    The following keys are available in each hash:
    * `content`: The content of the note.
    * `rank`: The sort key of the note.
-->
<#function getNotes item>
    <#--  <#return item["./ca_notes"]>  -->
    <#return item["./ca_notes"]?map(
        note -> {
            "content": note["./content"],
            "rank": note["./rank"]?number
        })?sort_by("rank")>
</#function>

<#--  <#function getContent note>
    <#return note["./content"]>
</#function>  -->