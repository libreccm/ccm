#!/bin/sh
#
# DependsOn: tools

# Exit immediately if command fails
set -e

# Print command executed to stdout
set -v

sed "s/^RELEASE=.*/&${SVN_REVISION}/" < configure.in > configure.in.tmp

# Intialise the build environment
aclocal
autoconf configure.in.tmp > configure
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

make check
make install

rm -f ccm-tools-bundle-*.tar.gz
make dist

# Build the rpms
if [ -n "$RPM_DIR" ]
then
  rpmbuild -ta --define "_topdir $RPM_DIR" --clean ccm-tools-bundle-*.tar.gz
else
  rpmbuild -ta --clean ccm-tools-bundle-*.tar.gz
fi

./make-zip.sh

# Cleanup the build dir
#cd ..
#rm -rf $builddir

# We're all done!
exit 0

# End of file
