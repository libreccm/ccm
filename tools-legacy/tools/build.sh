#!/bin/sh

set -e
set -v

# Intialise the build environment
aclocal
autoconf
automake -a

if [ -n "$1" ]; then
  PREFIX=$1
fi
if [ -z "$PREFIX"]; then
  ID=`id -u`
  if [ $ID = 0 ]; then
    PREFIX=
  else
    PREFIX=$HOME
  fi
fi

./configure --prefix=$PREFIX/usr --sysconfdir=$PREFIX/etc --localstatedir=$PREFIX/var
make
make install

# End of file

