FILES = themes-with-freemarker.md 
FILES += ../../ccm-themedirector/doc/freemarker/ccm-themedirector.md 
FILES += ../../ccm-cms-assets-fileattachment/doc/freemarker/ccm-cms-assets-fileattachment.md
FILES += ../../ccm-cms-assets-imagestep/doc/freemarker/ccm-cms-assets-imagestep.md
FILES += ../../ccm-cms-assets-notes/doc/freemarker/ccm-cms-assets-notes.md
FILES += ../../ccm-cms-assets-relatedlink/doc/freemarker/ccm-cms-assets-relatedlink.md
FILES += ../../ccm-cms-publicpersonalprofile/doc/freemarker/ccm-cms-publicpersonalprofile.md

all: html pdf

html:
	pandoc -t html -o themes-with-freemarker.html ${FILES}

pdf:
	pandoc -t latex -o themes-with-freemarker.pdf ${FILES}
