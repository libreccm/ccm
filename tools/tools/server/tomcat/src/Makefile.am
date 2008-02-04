# Process with automake

AUTOMAKE_OPTIONS = foreign no-dependencies 1.4

SUBDIRS = com

all:
	ant build

install:
	ant -Dinstdir=$(DESTDIR)$(pkgdatadir)/server/tomcat/classes deploy

clean:
	ant clean

EXTRA_DIST = build.xml

# End of files
