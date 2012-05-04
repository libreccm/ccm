#/bin/sh

ccm-run com.arsdigita.london.terms.Importer lgcl-1.03-addendum.xml

ccm-run com.arsdigita.london.terms.Importer hierarchy-lgcl-1.03-addendum.xml

ccm-run com.arsdigita.london.terms.Importer ../mapping-lgcl-1.03-gcl-2.0.xml

ccm-run com.arsdigita.london.terms.Importer ../mapping-lgcl-1.03-lgsl-2.00.xml

ccm-run com.arsdigita.london.terms.Importer ../related-lgcl-1.03.xml
