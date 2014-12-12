-- Make volume_of_series alpha numerical
ALTER TABLE ct_publications_volume_in_series ALTER volumeofseries TYPE VARCHAR(128);