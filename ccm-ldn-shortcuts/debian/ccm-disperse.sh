#!/bin/sh
#
# Author: Berkan Eskikaya <berkan@runtime-collective.com>, 2004
#
# $Id: ccm-disperse.sh,v 1.1.1.1 2004/11/12 09:30:15 fabrice Exp $
 
. debian/ccm-settings.sh

SRCDIR="$1"
[ "x$SRCDIR" != "x" ] || exit 1

# automatically generate the .install file lists for dh_install:

(cd $SRCDIR && find . -type f \
  | grep -v DEBIAN            \
  | grep -v var/www/html      \
  | sed -e 's/^\.\/*\(.*\)/\1/' -e '/./,/^$/!d') > debian/${name}.install
 
(cd $SRCDIR && find . -type f -path './var/www/html/*' \
  | sed -e 's/^\.\/*\(.*\)/\1/' -e '/./,/^$/!d') > debian/${name}-doc.install 

# for some reason, dh_install doesn't work properly; let's disperse the files ourselves:

for pkg in ${name} ${name}-doc; do
  # skip this package if mentioned in CCM_DEBIAN_NO_PACKAGES
  [ `echo $CCM_DEBIAN_NO_PACKAGES | tr ' ' '\n' | grep -c "^${pkg}$"` -eq 1 ] && continue || true

  # this should exist [due to dh_installdocs, dh_installchangelog], but just in case...
  mkdir -p debian/$pkg

  [ -e debian/${pkg}.install ] && \
  echo -n "Dispersing files to debian/$pkg/ ... " && \
  for file in `cat debian/${pkg}.install`; do
    dir=`dirname $file`
    mkdir -p debian/$pkg/$dir
    cp $SRCDIR/$file debian/$pkg/$dir/
  done
  echo "done."
done
