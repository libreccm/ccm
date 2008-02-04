--
-- Copyright (C) 2002-2004 Red Hat Inc. All Rights Reserved.
--
-- This library is free software; you can redistribute it and/or
-- modify it under the terms of the GNU Lesser General Public License
-- as published by the Free Software Foundation; either version 2.1 of
-- the License, or (at your option) any later version.
--
-- This library is distributed in the hope that it will be useful,
-- but WITHOUT ANY WARRANTY; without even the implied warranty of
-- MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
-- Lesser General Public License for more details.
--
-- You should have received a copy of the GNU Lesser General Public
-- License along with this library; if not, write to the Free Software
-- Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
--
-- $Id: versioning.sql 287 2005-02-22 00:29:02Z sskracic $
-- $DateTime: 2004/08/17 23:15:09 $


WHENEVER SQLERROR EXIT ROLLBACK;

-- check that there are no existing MPAs in which sections are mapped
-- to more than one Article
declare

v_section_name varchar2(4000);
v_count integer;
begin
  for v_article_section in (
     select cmas.section
       from ct_mp_articles_map cmas
     ) loop

  select count(*) into v_count
        from ct_mp_articles_map cmas
       where cmas.section = v_article_section.section;

  if (v_count > 1) then

	select title into v_section_name
	from cms_pages
	where item_id=v_article_section.section;

    RAISE_APPLICATION_ERROR(-20000, 
          'Upgrade cannot proceed if any MultiPartArticle Sections are pointed to by ' ||
          'more than one article. The following section violates this condition: ' || 
          v_section_name || ', section ID ' || v_article_section.section ||
          '.  Ensure this section is mapped to only one MultiPartArticle before proceeding.' 
    );
    end if;
  end loop;
end;
/

WHENEVER SQLERROR CONTINUE;

-- No MPA sections are mapped to more than one MultiPartArticle, so we can continue.

-- First drop constraints that will get in the way

alter table ct_mp_articles_map drop constraint ct_mp_articles_map_article_fk;
alter table ct_mp_articles_map drop constraint ct_mp_articles_map_section_fk;
alter table ct_mp_sections drop constraint ct_mp_sections_text_fk;
alter table ct_mp_sections drop constraint ct_mp_sections_image_fk;
 
alter table ct_mp_sections add (article_id INTEGER);
alter table ct_mp_sections add (rank INTEGER);

declare
begin

   for new_entry in (
	select section_id from ct_mp_sections
   ) loop

	update ct_mp_sections set (article_id, rank) =
		(select article, rank
	           from ct_mp_articles_map
	          where section = new_entry.section_id)
	 where section_id = new_entry.section_id;
   end loop;
end;
/
show errors;

-- now get rid of the extra table and put the constraints back

drop table ct_mp_articles_map; 

alter table ct_mp_sections add constraint ct_mp_sections_article_id_fk
   foreign key (article_id) references ct_mp_articles(article_id);

alter table ct_mp_sections add constraint ct_mp_sections_text_fk
   foreign key (text) references cms_text(text_id);

alter table ct_mp_sections add constraint ct_mp_sections_image_fk
   foreign key (image) references cms_images(image_id);

-- done with MP Article work.

-- Article versioning work:

-- we need to version this association so that the caption in the map will
-- roll back correctly.  And, the only way to do that is to make the 
-- tables extend cms_items which is not much fun.
alter table cms_article_image_map drop constraint cms_article_image_map_pk;
alter table cms_article_image_map add (map_id integer);

--drop constraints that are going to get in the way

alter table cms_article_image_map drop constraint caim_article_id_fk;
alter table cms_article_image_map drop constraint caim_image_id_fk;
alter table cms_text_pages drop constraint cms_text_pages_text_id_fk;                  

declare
begin

   update cms_article_image_map set map_id = acs_object_id_seq.nextval;

   for new_entry in (
       select caim.map_id, caim.article_id, caim.image_id, v.master_id, ci.version 
	 from cms_article_image_map caim, vc_objects v, cms_items ci
        where v.object_id = caim.article_id
	  and ci.item_id = caim.article_id
   ) loop

     insert into acs_objects 
      (object_id, object_type, display_name, default_domain_class) 
     values 
      (new_entry.map_id, 'com.arsdigita.cms.ArticleImageAssociation', 'Image Association between article with id of ' || new_entry.article_id || ' and image with id of ' || new_entry.image_id, 'com.arsdigita.cms.ArticleImageAssociation');

     insert into vc_objects (object_id, is_deleted, master_id)
     values
     (new_entry.map_id, '0', new_entry.article_id);

     update vc_objects set master_id = null
	where object_id=new_entry.image_id;

     insert into cms_items (item_id, name, version)
     values
	(new_entry.map_id, to_char(new_entry.map_id), new_entry.version);

   end loop;

end;
/
show errors;

-- Add a row in cms_version_map for each row in cms_article_image_map
-- that is associated with an article marked "live"

insert into cms_version_map
     select caim2.map_id,
	    caim.map_id,
	    cvm.timestamp
       from cms_article_image_map caim,
	    cms_article_image_map caim2,
	    cms_version_map cvm,
	    cms_items ci
      where caim.article_id=ci.item_id
	and ci.version = 'live'
	and ci.item_id=cvm.version_id
	and cvm.item_id=caim2.article_id;


-- reestablish proper constraints. Foreign key constraints that do not
-- point directly up the object hierarchy should be "on delete set
-- null", not "on delete cascade."

alter table cms_article_image_map add constraint
   cms_article_image_map_pk primary key (map_id); 

alter table cms_article_image_map add constraint
   cms_article_image_map_id_nn check (map_id is not null);

alter table cms_article_image_map add constraint
   cms_article_image_map_id_fk foreign key (map_id) references cms_items
   (item_id) on delete cascade;

alter table cms_article_image_map add constraint caim_article_id_fk
   foreign key (article_id) references cms_articles(article_id) on delete
   set null;

alter table cms_article_image_map add constraint caim_image_id_fk 
   foreign key (image_id) references cms_images(image_id) on delete
   set null;

alter table cms_text_pages add constraint cms_text_pages_text_id_fk
   foreign key (text_id) references cms_text (text_id) on delete set
   null;
