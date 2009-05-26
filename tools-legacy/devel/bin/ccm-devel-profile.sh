#!/bin/sh

if [ "x$1" = "x" ]
then
  echo "ccm-devel-profile.sh <servername>"
  return
fi


if [ "x$CCM_DEVEL_CONF_DIR" = "x" ]
then
  echo "CCM_DEVEL_CONF_DIR not set"
  return
fi

TEMP1=`mktemp /tmp/tempenv.XXXXXX` || exit 1
perl $CCM_DEVEL_CONF_DIR/project.pl $@ > $TEMP1
if [ $? != 0 ]; then
  return
fi

. $TEMP1
rm -f $TEMP1

for i in $CCM_DEVEL_CONF_DIR/project.d/*.sh
do
  if [ -r $i ]
  then
    . $i
  fi
done

if [ -n "$CCM_DEV_HOME" ]
then
  # "$CCM_DEV_HOME/project.sh" allows the user to set environment variables or
  # perform other actions on a per-project basis.
  if [ -r "$CCM_DEV_HOME/project.sh" ]; then
      . "$CCM_DEV_HOME/project.sh"
  fi
fi

echo "CCM_DEV_HOME: ${CCM_DEV_HOME:-[Not Set]}"
echo "CCM_WEB_HOME: ${CCM_WEB_HOME:-[Not Set]}"
echo "CCM_HOME: ${CCM_HOME:-[Not Set]}"
