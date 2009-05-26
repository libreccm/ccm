#!/bin/sh


# Exit immediately if command fails
set -e

# Print command executed to stdout
#set -v

make maintainer-clean || :

# Intialise the build environment
aclocal
autoconf
automake -a

# Do a VPATH build so we don't mess up the source dir
#builddir="obj"
#mkdir $builddir || :
#ln -s `pwd` $builddir/.install_link
#cd $builddir

# Configure & make a distribution - no need to 
# actually compile at this stage since this is 
# done by rpm later
#.install_link/configure
ID=`id -u`
if [ $ID = 0 ]; then
  ./configure --prefix=/usr/local
else
  ./configure --prefix=$HOME/usr
fi
make

# End of file
