-- Add column for authors property in ct_publications table
alter table ct_publications add authors varchar(2048);

-- Fill the authors property for existing publications
update ct_publications set authors = (array_to_string(array(select cms_persons.surname || ', ' || cms_persons.givenname 
                                                            from cms_persons 
                                                            join ct_publications_authorship on cms_persons.person_id = ct_publications_authorship.person_id 
                                                            where ct_publications_authorship.publication_id = ct_publications.publication_id 
                                                            order by ct_publications_authorship.authorship_order), '; '));