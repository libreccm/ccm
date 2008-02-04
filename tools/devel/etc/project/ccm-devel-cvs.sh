#!/bin/sh

if [ "x$1" = "x" ] ; then
  echo "ccm-devel-profile.sh <servername>"
  return
else
  SERVER=$1
fi

if [ ! -d $CCM_DEVEL_CVSROOT ]; then
  echo "Cannot find CVS root $CCM_DEVEL_CVSROOT"
  return
fi

CVSROOT=$CCM_DEVEL_CVSROOT
export CVSROOT

