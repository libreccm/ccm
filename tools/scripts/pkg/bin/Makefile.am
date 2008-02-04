# Process with automake

AUTOMAKE_OPTIONS = foreign no-dependencies 1.4

SUBDIRS = resin tomcat4

scriptsdir = $(pkgdatadir)/pkg/bin
scripts_SCRIPTS = $(wildcard *.pl *.sh.in *.cmd)

PERL_FILES=$(wildcard *.pl)

TEST_FILES=$(PERL_FILES:%.pl=.%.pl.test)

check: $(TEST_FILES)

.%.pl.test: %.pl
	@perl -Tc $<
	@touch $@

CLEANFILES = $(TEST_FILES)

EXTRA_DIST = $(scripts_SCRIPTS)

# End of files
