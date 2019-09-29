<#ftl ns_prefixes={
"bebop":"http://www.arsdigita.com/bebop/1.0",
"cms":"http://www.arsdigita.com/cms/1.0",
"nav":"http://ccm.redhat.com/navigation",
"ui": "http://www.arsdigita.com/ui/1.0"}
>

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