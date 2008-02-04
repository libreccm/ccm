#!/bin/sh

# Exit immediately if command fails
set -e

# Print command executed to stdout
set -v

# cp -f configure.in configure.in.tmp
sed "s/^RELEASE=.*/&${SVN_REVISION}/" < configure.in > configure.in.tmp
if [ -n "$AUTO_BUILD_COUNTER" ]
then
  perl -i -p -e "s/VERSION=(.*)/VERSION=\$1.AUTO.\$ENV{'AUTO_BUILD_COUNTER'}/" configure.in.tmp
fi


# Initialize the build environment
aclocal
autoconf configure.in.tmp > configure
automake -a

# Make sure that ./configure is executable
chmod u+x ./configure

# Configure & make a distribution - no need to
# actually compile at this stage since this is
# done by rpm later
#.install_link/configure --prefix=$AUTO_BUILD_ROOT
./configure --prefix=$AUTO_BUILD_ROOT

env CCM_TOOLS_HOME=`pwd` make check
make install DESTDIR=$DESTDIR

rm -f ccm-tools-*.tar.gz
make dist

perl Makefile.PL PREFIX=$AUTO_BUILD_ROOT
make -f Makefile.perl
make -f Makefile.perl test

# Build the rpms
if [ -n "$RPM_DIR" ]
then
  rpmbuild -ta --define "_topdir $RPM_DIR" --clean ccm-tools-*.tar.gz
else
  rpmbuild -ta --clean ccm-tools-*.tar.gz
fi

# Build the zips
./make-zip.sh

rm -f configure.in.tmp

# We're all done!
exit 0

# End of file
