FILES = themes-with-freemarker.md 
FILES += ../../ccm-themedirector/doc/freemarker/ccm-themedirector.md 
FILES += ../../ccm-cms-assets-fileattachment/doc/freemarker/ccm-cms-assets-fileattachment.md
FILES += ../../ccm-cms-assets-imagestep/doc/freemarker/ccm-cms-assets-imagestep.md

all: html pdf

html:
	pandoc -t html -o themes-with-freemarker.html ${FILES}

pdf:
	pandoc -t latex -o themes-with-freemarker.pdf ${FILES}
