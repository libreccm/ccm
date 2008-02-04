# Process with automake

AUTOMAKE_OPTIONS = foreign no-dependencies 1.4

scriptsdir = $(pkgdatadir)/bin
scripts_SCRIPTS = $(wildcard *.pl *.sh) make-zip make-source env-conf make-rpm

check-local:
	perl -c env-conf
	perl -c extract-version.pl
	perl -cT interpolate.pl
	perl -c make-rpm
	perl -c make-source
	perl -c make-zip

CLEANFILES = $(TEST_FILES)

EXTRA_DIST = $(scripts_SCRIPTS)

# End of files
