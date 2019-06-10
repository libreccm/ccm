FILES = themes-with-freemarker.md 
FILES += ../../ccm-cms/doc/freemarker/ccm-cms.md
FILES += ../../ccm-cms-assets-fileattachment/doc/freemarker/ccm-cms-assets-fileattachment.md
FILES += ../../ccm-cms-assets-imagestep/doc/freemarker/ccm-cms-assets-imagestep.md
FILES += ../../ccm-cms-assets-notes/doc/freemarker/ccm-cms-assets-notes.md
FILES += ../../ccm-cms-assets-relatedlink/doc/freemarker/ccm-cms-assets-relatedlink.md
FILES += ../../ccm-cms-publicpersonalprofile/doc/freemarker/ccm-cms-publicpersonalprofile.md
FILES += ../../ccm-cms-types-address/doc/freemarker/ccm-cms-types-address.md
FILES += ../../ccm-cms-types-article/doc/freemarker/ccm-cms-types-article.md
FILES += ../../ccm-cms-types-contact/doc/freemarker/ccm-cms-types-contact.md
FILES += ../../ccm-cms-types-article/doc/freemarker/ccm-cms-types-article.md
FILES += ../../ccm-cms-types-bookmark/doc/freemarker/ccm-cms-types-bookmark.md
FILES += ../../ccm-cms-types-contact/doc/freemarker/ccm-cms-types-contact.md
FILES += ../../ccm-cms-types-event/doc/freemarker/ccm-cms-types-event.md
FILES += ../../ccm-cms-types-externallink/doc/freemarker/ccm-cms-types-externallink.md
FILES += ../../ccm-cms-types-filestorageitem/doc/freemarker/ccm-cms-types-filestorageitem.md
FILES += ../../ccm-cms-types-formitem/doc/freemarker/ccm-cms-types-formitem.md
FILES += ../../ccm-cms-types-image/doc/freemarker/ccm-cms-types-image.md
FILES += ../../ccm-cms-types-mparticle/doc/freemarker/ccm-cms-types-multiparticle.md
FILES += ../../ccm-cms-types-mparticle/doc/freemarker/ccm-cms-types-multiparticle-toc.md
FILES += ../../ccm-cms-types-newsitem/doc/freemarker/ccm-cms-types-newsitem.md
FILES += ../../ccm-cms-types-siteproxy/doc/freemarker/ccm-cms-types-siteproxy.md
FILES += ../../ccm-core/doc/freemarker/ccm-core/user-banner.md
FILES += ../../ccm-navigation/doc/freemarker/ccm-navigation/object-list.md
FILES += ../../ccm-portalworkspace/doc/freemarker/ccm-portalworkspace.md
FILES += ../../ccm-sci-bundle/web/assets/mathjax/CONTRIBUTING.md
FILES += ../../ccm-sci-bundle/web/assets/mathjax/README.md
FILES += ../../ccm-sci-member-navigation/doc/freemarker/ccm-sci-member-navigation.md
FILES += ../../ccm-sci-project-navigation/doc/freemarker/ccm-sci-project-navigation.md
FILES += ../../ccm-sci-publications/doc/freemarker/ccm-sci-publications/article.md
FILES += ../../ccm-sci-publications/doc/freemarker/ccm-sci-publications/authors.md
FILES += ../../ccm-sci-publications/doc/freemarker/ccm-sci-publications/collected-volume.md
FILES += ../../ccm-sci-publications/doc/freemarker/ccm-sci-publications/export-links.md
FILES += ../../ccm-sci-publications/doc/freemarker/ccm-sci-publications/journal.md
FILES += ../../ccm-sci-publications/doc/freemarker/ccm-sci-publications/library-signatures.md
FILES += ../../ccm-sci-publications/doc/freemarker/ccm-sci-publications/orderer.md
FILES += ../../ccm-sci-publications/doc/freemarker/ccm-sci-publications/proceedings.md
FILES += ../../ccm-sci-publications-navigation/doc/freemarker/ccm-sci-publications-navigation.md
FILES += ../../ccm-sci-publications/web/themes/freemarker/ccm-sci-publications/publications.md
FILES += ../../ccm-sci-publications/web/themes/freemarker/ccm-sci-publications/publisher.md
FILES += ../../ccm-sci-publications/web/themes/freemarker/ccm-sci-publications/series.md
FILES += ../../ccm-sci-types-department/doc/freemarker/ccm-sci-department.md
FILES += ../../ccm-sci-types-institute/doc/freemarker/ccm-sci-types-institute.md
FILES += ../../ccm-sci-types-project/doc/freemarker/ccm-sci-types-project.md
FILES += ../../ccm-subsite/doc/freemarker/ccm-subsite.md
FILES += ../../ccm-themedirector/doc/freemarker/ccm-themedirector/language.md 
FILES += ../../ccm-themedirector/doc/freemarker/ccm-themedirector/utils.md 

all: html pdf

html:
	pandoc -t html -o themes-with-freemarker.html ${FILES}

pdf:
	pandoc -t latex -o themes-with-freemarker.pdf ${FILES}
