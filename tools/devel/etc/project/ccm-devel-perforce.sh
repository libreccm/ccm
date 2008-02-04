#!/bin/sh

if [ "x$1" = "x" ] ; then
  echo "ccm-devel-profile.sh <servername>"
  return
else
  SERVER=$1
fi

P4CONFIG=$HOME/.p4config-$SERVER
export P4CONFIG
