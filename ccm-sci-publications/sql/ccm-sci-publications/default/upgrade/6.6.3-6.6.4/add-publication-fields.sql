-- Add fields for year of first publication and language of the publication
ALTER TABLE ct_publications ADD firstPublished INTEGER;
ALTER TABLE ct_publications ADD lang VARCHAR(128);