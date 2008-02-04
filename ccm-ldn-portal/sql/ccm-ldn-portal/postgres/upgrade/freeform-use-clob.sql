alter table portlet_freeform_html add content_clob text;

update portlet_freeform_html set content_clob = content;

alter table portlet_freeform_html drop content;

