#!/bin/sh
#

# Exit immediately if command fails
set -e

# Print command executed to stdout
set -v

TEMPLATE=`dirname $0`

# Bundle specific values (must be executed in Bundle directory)
. ./bundle.in

# prepare .spec.in
cat $TEMPLATE/ccm-bundle.spec.IN ChangeLog > ccm-bundle-$BUNDLE_NAME.spec.in

# prepare .am
cat <<EOF > Makefile.am
SUBDIRS = cfg

dist-hook:
	cp \$(top_builddir)/@PACKAGE@.spec \$(distdir)/

EOF

cat <<EOF > cfg/Makefile.am
AUTOMAKE_OPTIONS = foreign no-dependencies 1.4

cfgdir = \$(datadir)/ccm-tools/bundles/$BUNDLE_NAME
cfg_DATA = web.xml applications.cfg integration.properties

EXTRA_DIST = \$(cfg_DATA)

EOF

# prepare configure.in
cat <<EOF > configure.in
AC_INIT(ccm-bundle-$BUNDLE_NAME.spec.in)
BUNDLE_NAME=$BUNDLE_NAME
BUNDLE_PRETTY_NAME="$BUNDLE_PRETTY_NAME"
VERSION=$VERSION
RELEASE=$RELEASE${SVN_REVISION}
EOF

echo -n 'REQUIRES_LIST="' >> configure.in
echo -n `cat cfg/applications.cfg | grep -v '#' | grep -v '^\s*$'` >> configure.in
echo '"' >> configure.in

cat <<EOF >> configure.in
AM_INIT_AUTOMAKE(ccm-bundle-$BUNDLE_NAME, $VERSION)
AC_SUBST(RELEASE)
AC_SUBST(REQUIRES_LIST)
AC_SUBST(BUNDLE_NAME)
AC_SUBST(BUNDLE_PRETTY_NAME)

AC_OUTPUT(Makefile
          cfg/Makefile
          ccm-bundle-$BUNDLE_NAME.spec)

dnl End of file
EOF

# Initialise the build environment
aclocal
autoconf
automake -a

chmod +x configure

# Do a VPATH build so we don't mess up the source dir
#builddir=".rollingbuild-$$"
#mkdir $builddir
#ln -s `pwd` $builddir/.install_link
#cd $builddir

# Configure & make a distribution - no need to 
# actually compile at this stage since this is 
# done by rpm later
#.install_link/configure --prefix=$AUTO_BUILD_PREFIX
./configure --prefix=$AUTO_BUILD_ROOT

make install

rm -f ccm-bundle-$BUNDLE_NAME-*.tar.gz
make dist

# Build the rpms
rpmbuild -ta --clean ccm-bundle-$BUNDLE_NAME-*.tar.gz

$TEMPLATE/make-zip.sh

# Cleanup the build dir
#cd ..
#rm -rf $builddir

# We're all done!
exit 0

# End of file
