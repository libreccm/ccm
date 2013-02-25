-- Add fields for year of first publication and language of the publication
ALTER TABLE ct_publications ADD COLUMN firstPublished INTEGER;
ALTER TABLE ct_publications ADD COLUMN lang VARCHAR(128);