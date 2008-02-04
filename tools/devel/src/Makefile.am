# Process with automake

AUTOMAKE_OPTIONS = foreign no-dependencies 1.4

SUBDIRS = com

all:
	ant -Dlibdir=$(top_srcdir)/lib build

install:
	ant -Dlibdir=$(top_srcdir)/lib -Dinstdir=$(DESTDIR)$(pkgdatadir)/classes deploy

clean:
	ant clean

EXTRA_DIST = build.xml

# End of files
