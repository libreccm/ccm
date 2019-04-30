all: html pdf

html:
	pandoc -t html -o themes-with-freemarker.html themes-with-freemarker.md ../../ccm-themedirector/doc/freemarker/ccm-themedirector.md

pdf:
	pandoc -t latex -o themes-with-freemarker.pdf themes-with-freemarker.md ../../ccm-themedirector/doc/freemarker/ccm-themedirector.md
