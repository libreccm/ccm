#Makefile for Red Hat Documentation
#Created by Tammy Fox <tfox@redhat.com>
#Copyright Red Hat Inc. 2001

include Makefile.local

TOP         =   $(shell pwd)
PRINTENTITY =   SET-FOR-PRINT
LANG        =   en
SGMLFILE    =   $(DOCNAME).sgml
HTMLSCRIPT  =   db2indexed-html
VERSION     =   $(shell awk '/Version:/ { print $$2 }' $(DOCNAME).spec)
RELEASE     =   $(shell awk '/Release:/ { print $$2 }' $(DOCNAME).spec)
get-ext     =   $(subst .,,$(suffix $(1)))

SPLITTER      = java -cp ../docs-stuff/classes/ com.redhat.ccm.documentation.Splitter

sedexpr     =   -e 's/^\(<!ENTITY \+% \+$2 \+"\)\(INCLUDE\|IGNORE\)\(">\)/\1$1\3/'

incl        =   $(call sedexpr,INCLUDE,$1)
ignore      =   $(call sedexpr,IGNORE,$1)

inclsedcmd    = sed $(call incl,$1) $2
ignoresedcmd  = sed $(call ignore,$1) $2

all: sgml html ps pdf rpm

sgml:
ifdef SPLITTERDIRS
	$(SPLITTER) build $(shell find $(SPLITTERDIRS) -type f -not -name "*~" -not -name "\#*")
	find build/ -type f -exec ln -sf {} \;
endif

html: sgml
	$(call ignoresedcmd,$(PRINTENTITY),$(SGMLFILE)) > $(DOCNAME)-temp.sgml
	$(HTMLSCRIPT) --output $(DOCNAME) $(DOCNAME)-temp.sgml
	@rm $(DOCNAME)-temp.sgml
	@rm -rf $(DOCNAME)-$(VERSION)
	@mv $(DOCNAME) $(DOCNAME)-$(VERSION)

ps pdf:	sgml $(SGMLFILE)
	@rm -f $(DOCNAME).$@
	$(MAKE) $(addsuffix .$@,$(basename $(SGMLFILE)))

%.ps %.pdf:
	$(call inclsedcmd,$(PRINTENTITY),$(SGMLFILE)) > $(DOCNAME)-temp.sgml
	db2indexed-$(call get-ext,$@) $(notdir $(DOCNAME)-temp.sgml)
	@rm $(DOCNAME)-temp.sgml
	@mv $(DOCNAME)-temp.$(call get-ext,$@) $(DOCNAME)-$(VERSION)-$(RELEASE).$(call get-ext,$@)

tarball: html
	@cp -f ../docs-stuff/icon/docs.png $(DOCNAME)-$(VERSION)
	@tar cvjf $(DOCNAME)-$(VERSION).tbz $(DOCNAME)-$(VERSION)/*
	@echo "The archive is in $(DOCNAME)-$(VERSION).tbz"

zip: html
	@cp -f ../docs-stuff/icon/docs.png $(DOCNAME)-$(VERSION)
	@zip -r $(DOCNAME)-$(VERSION).zip $(DOCNAME)-$(VERSION)/*
	@echo "The archive is in $(DOCNAME)-$(VERSION).zip"

srpm:	tarball
	@rpmbuild -ts $(DOCNAME)-$(VERSION).tbz
	@echo "SRPM created"

rpm:	tarball
	@rpmbuild -bb --define "_sourcedir `pwd`" $(DOCNAME).spec
	@echo "RPM created"

clean:
	@rm -rfv *.ps *.pdf *.tex *.out *.log html $(DOCNAME) $(DOCNAME)-$(VERSION) $(DOCNAME).junk *.tbz generated-index.sgml build
	find . -maxdepth 1 -lname "build/*" -exec rm {} \;

zipclean: clean
	@rm -f *.zip

splitter: classes/com/redhat/ccm/documentation/Splitter.class

classes/com/redhat/ccm/documentation/Splitter.class: src/com/redhat/ccm/documentation/Splitter.java
	@mkdir -p classes
	javac -d classes src/com/redhat/ccm/documentation/Splitter.java