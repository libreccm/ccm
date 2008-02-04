Release Notes for Terms Importer upgrade
Introduction

These release notes relate to changes to the com.arsdigita.london.terms.importer package and related supporting files in the directory /usr/share/doc/ccm-ldn-terms-VERSION/esd.

The purpose of these changes is to enable the importer to correctly import revised ESD controlled lists into a system that has an earlier version of the lists. Specifically a requirement was to upgrade a production version of APLAWS with a 1.02 version of ESD lists to have 1.04 lists.

User Interface

The upgraded importer tool has exactly the same interface as the earlier version. Therefore the tool works as documented in the APLAWS Admin Guide. One previously undocumented command is optional. To recap:

First generate the XML files to import

# cat files-1.04.txt | perl generate.pl

Then import the domains and terms in those domains

# ccm-run com.arsdigita.london.terms.Importer domain-*

Then add the hierarchical relationships between the terms

# ccm-run com.arsdigita.london.terms.Importer hierarchy-*

Then add the mappings between the terms

# ccm-run com.arsdigita.london.terms.Importer mapping-*

Then add the related terms (this is not in the original documentation)

# ccm-run com.arsdigita.london.terms.Importer related-*

The most difficult part of this process is determining what should be the contents of the input file, in our case files-1.04.txt. This determines which XML files are generated. The released version of files-1.02.txt generated too many files, so that too many domains were imported into our APLAWS installation. In addition, a hierarchy mapping LGDL to LGSL was imported, followed by a related mapping of the same terms. This second import was superfluous and resulted in incorrect data. The files-1.04.txt was created with fewer entries than files-1.02.txt for this reason. 

It was necessary to edit one of the XSL files that transform the ESD toolkit XML into terms importer XML. The file LGDLLGSLItemsHierarchy2mixedHierarchy.xsl was changed to ensure that the LGSL terms added to the LGDL were all preferred terms. The same result could have been attained by changing the java code, but we decided to make as few changes to java code as possible.

Changes to the Java code

All changes were made only to classes in com.arsdigita.london.terms.importer . This package is only used to import the terms through the command line UI. No changes were made to the terms code , or to categories etc.
The classes changed were DomainBuilder HierarchyBuilder, MixedHierarchyBuilder and RelatedBuilder. 
DomainBuilder was altered to update the Domain information.
The other classes were updated to delete the existing mappings between terms, and import the new ones.


