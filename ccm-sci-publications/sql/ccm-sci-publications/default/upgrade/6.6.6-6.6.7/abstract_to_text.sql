-- Alter abstract and misc column type to text 
ALTER TABLE ct_publications ALTER COLUMN abstract TYPE text;
ALTER TABLE ct_publications ALTER COLUMN misc TYPE text;