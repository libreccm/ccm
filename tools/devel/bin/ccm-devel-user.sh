#!/bin/sh

set -e

if [ "x$1" = "x" ]; then
  echo "syntax: ccm-devel-user.sh <username>"
  exit 1
fi

OS=`uname -s`
if [ "x$OS" = "xSunOS" ]; then
  PATH=/usr/xpg4/bin:$PATH
fi

USER_ID=`id -u`

if [ "$USER_ID" != "0" ]; then
  echo "you must be root to run this"
  exit 1
fi

USERNAME=$1

id -u $USERNAME > /dev/null

if [ "$?" != "0" ]; then
  echo "$USERNAME is not a valid user.  You must create a system account first."
  exit 1
fi

if [ ! -d "$CCM_DEVEL_ROOT" ]; then
  echo "CCM_DEVEL_ROOT is not set or is not a directory: '${CCM_DEVEL_ROOT}'"
  return
fi

cd "$CCM_DEVEL_ROOT"

if [ -d dev/$USERNAME ]; then
  echo "development directories already exist for $USERNAME"
  exit 1
fi

mkdir dev/$USERNAME
mkdir web/$USERNAME
mkdir rpm/$USERNAME

mkdir rpm/$USERNAME/BUILD
mkdir rpm/$USERNAME/RPMS
mkdir rpm/$USERNAME/RPMS/noarch
mkdir rpm/$USERNAME/RPMS/i386
mkdir rpm/$USERNAME/RPMS/i486
mkdir rpm/$USERNAME/RPMS/i586
mkdir rpm/$USERNAME/RPMS/i686
mkdir rpm/$USERNAME/RPMS/sparc9
mkdir rpm/$USERNAME/RPMS/sparc64
mkdir rpm/$USERNAME/RPMS/sparc
mkdir rpm/$USERNAME/SPECS
mkdir rpm/$USERNAME/SOURCES
mkdir rpm/$USERNAME/SRPMS

chown -R $USERNAME dev/$USERNAME
chown -R $USERNAME web/$USERNAME
chown -R $USERNAME rpm/$USERNAME
chgrp -R ccm-devel dev/$USERNAME
chgrp -R ccm-devel web/$USERNAME
chgrp -R ccm-devel rpm/$USERNAME
chmod -R u=rwX,g=rXs,o=rX dev/$USERNAME
chmod -R u=rwX,g=rXs,o=rX web/$USERNAME
chmod -R u=rwX,g=rXs,o=rX rpm/$USERNAME

echo %_topdir $CCM_DEVEL_ROOT/rpm/$USER > rpm/rpmmacros

if [ -x /usr/bin/gpasswd ]; then
  /usr/bin/gpasswd -a $USERNAME ccm-devel
else
  echo "Please add $USERNAME to the ccm-devel group"
fi
