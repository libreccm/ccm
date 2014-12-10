-- Add column for reviewed property to ct_publications table
alter table ct_publications add reviewed boolean;

-- Copy existing values for reviewed from
-- * ct_article_in_collected_volume
-- * ct_article_in_journal
-- * ct_collected_volume
-- * ct_monograph
-- * ct_working_paper

update ct_publications set reviewed = (select ct_article_in_collected_volume.reviewed 
                                       from ct_article_in_collected_volume 
                                       where ct_article_in_collected_volume.article_id = ct_publications.publication_id)
from ct_article_in_collected_volume
where ct_publications.publication_id = ct_article_in_collected_volume.article_id;

update ct_publications set reviewed = (select ct_article_in_journal.reviewed
                                       from ct_article_in_journal
                                       where ct_article_in_journal.article_in_journal_id = ct_publications.publication_id)
from ct_article_in_journal
where ct_publications.publication_id = ct_article_in_journal.article_in_journal_id;

update ct_publications set reviewed = (select ct_collected_volume.reviewed
                                       from ct_collected_volume
                                       where ct_collected_volume.collected_volume_id  = ct_publications.publication_id)
from ct_collected_volume
where ct_publications.publication_id = ct_collected_volume.collected_volume_id;

update ct_publications set reviewed = (select ct_monograph.reviewed
                                       from ct_monograph
                                       where ct_monograph.monograph_id = ct_publications.publication_id)
from ct_monograph
where ct_publications.publication_id = ct_monograph.monograph_id;

update ct_publications set reviewed = (select ct_working_paper.reviewed
                                       from ct_working_paper
                                       where ct_working_paper.working_paper_id = ct_publications.publication_id)
from ct_working_paper
where ct_publications.publication_id = ct_working_paper.working_paper_id;

-- Drop obsoletes columns

alter table ct_article_in_collected_volume drop column reviewed;
alter table ct_article_in_journal drop column reviewed;
alter table ct_collected_volume drop column reviewed;
alter table ct_monograph drop column reviewed;
alter table ct_working_paper drop column reviewed;