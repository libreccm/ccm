update cms_links x
set link_order = nvl((select sort_key 
			   	  from portlet_bookmarks y
				  where y.target_id = x.link_id), x.link_order);
commit;

alter table portlet_bookmarks
drop column sort_key;

update acs_objects 
set object_type = 'uk.gov.westsussex.portlet.Bookmark', 
	default_domain_class = 'uk.gov.westsussex.portlet.bookmarks.Bookmark'
where object_id in (select target_id 
					from portlet_bookmarks);