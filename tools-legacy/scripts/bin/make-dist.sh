#!/bin/sh
#
# Make all the different binary distributions

# Exit on error
set -e

if [ "x$CCM_SCRIPTS_VERBOSE" = "x1" ]; then
    set -v
fi

if [ "x$CCM_SCRIPTS_HOME" = "x" ]; then
    echo "Please set the CCM_SCRIPTS_HOME environment variable"
    exit 1
fi

if [ "x$CCM_ROOT_DIR" = "x" ]; then
    echo "Please set the CCM_ROOT_DIR environment variable"
    exit 1
fi


function try_rpms() {
    if [ ! "x$CCM_DIST_NORPMS" = "x1" ]; then
        if [ -f /usr/bin/rpmbuild -o -f /usr/local/bin/rpmbuild ]; then
          $CCM_SCRIPTS_HOME/bin/make-rpm.sh
	else
	  echo "Skipping RPMs because we can't find rpmbuild"
	fi
    else
        echo "Skipping RPMs because CCM_DIST_NORPMS is set"
    fi
}

function try_zips() {
    if [ ! "x$CCM_DIST_NOZIPS" = "x1" ]; then
        if [ "x$CCM_SCRIPTS_COMPAT" = "x" ]; then
            "$CCM_SCRIPTS_HOME/bin/make-zip"
        else
            if [ -e "$CCM_SCRIPTS_HOME/bin/make-zip.${CCM_SCRIPTS_COMPAT}.sh" ]; then
"$CCM_SCRIPTS_HOME/bin/make-zip.${CCM_SCRIPTS_COMPAT}.sh"
fi
        fi
    else
        echo "Skipping ZIPs because CCM_DIST_NOZIPS is set"
    fi
}

cd $CCM_ROOT_DIR

OS=`uname -s`
case "x$OS" in
   *Linux*)
     try_zips
     try_rpms
    ;;

   *SunOS*)
     try_zips
     try_rpms
    ;;

   *)
     try_zips
    ;;
esac


# Clean out build dir to save disk space
cd $BUILD_DIR
rm -rf $PACKAGE-$VERSION-$RELEASE

exit 0

# End of file
