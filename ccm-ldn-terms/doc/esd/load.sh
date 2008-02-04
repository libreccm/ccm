set -x
export JAVA_OPTS=-Xmx512m
time ccm-run com.arsdigita.london.terms.Importer domain-*
time ccm-run com.arsdigita.london.terms.Importer hierarchy-*
time ccm-run com.arsdigita.london.terms.Importer mapping-*
time ccm-run com.arsdigita.london.terms.Importer related-*
time ccm-run com.arsdigita.london.terms.Importer synonyms-*
