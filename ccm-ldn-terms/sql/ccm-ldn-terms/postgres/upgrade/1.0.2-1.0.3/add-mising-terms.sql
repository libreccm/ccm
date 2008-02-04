
begin;

insert into acs_objects (object_id, object_type, default_domain_class, display_name)
  select
      nextval('acs_object_id_seq'),
      'com.arsdigita.london.terms.Term',
      'com.arsdigita.london.terms.Term',
      'missing term for category ID: ' || cats.category_id
  from
      cat_categories cats, trm_domains doms
          where cats.default_ancestors like doms.model_category_id || '/_%'
          and not exists (select 1 from trm_terms trm
             where trm.model_category_id = cats.category_id) ;

insert into trm_terms (
    term_id,
    domain,
    unique_id,
    is_atoz,
    model_category_id
) select
    ao.object_id,
    dom.key,
    ao.object_id,
    'f',
    cat.category_id
  from
    acs_objects ao,
    cat_categories cat,
    trm_domains dom
  where
    cat.default_ancestors like dom.model_category_id || '/_%'
    and ao.object_type = 'com.arsdigita.london.terms.Term'
    and ao.display_name = 'missing term for category ID: ' || cat.category_id
    and not exists (select 1 from trm_terms trm
             where trm.model_category_id = cat.category_id) ;

update acs_objects set
    display_name = 'com.arsdigita.london.terms.Term ' || object_id
  where
    object_type = 'com.arsdigita.london.terms.Term'
    and display_name like 'missing term for category ID:%';

commit;


