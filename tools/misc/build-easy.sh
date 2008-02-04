#! /bin/sh

set -e

. ${0%/*}/build-functions.sh

function display_usage() {
    cat <<EOF
Usage: $0 [OPTIONS] [ccm-app1 ccm-app2 ...]

Options:
 -?        Display this usage message
 -x        Start from scratch
 -b        Build javadoc RPMs
 -s        turn off SVN release # tagging
 -v        be verbose (set -v)
EOF
    exit 1
}

SVN_TAGGED=1
SCRATCH_ARG=
export CCM_BUILD_NO_DOCS=1
export CCM_DIST_NOZIPS=1
export SVN_REVISION
BUILD_ROOT=$PWD

ALL_ARGS="$@"

while getopts xbsv opt; do
    case $opt in
        x) SCRATCH_ARG=1;;
        b) unset CCM_BUILD_NO_DOCS;;
        s) SVN_TAGGED=;;
        v) set -v;;
        [?]) display_usage
    esac
done

shift $(($OPTIND-1))

BUILD_ARGS="$@"

export AUTO_BUILD_ROOT=/var/tmp/$USER-auto-build-root
rm -rf $AUTO_BUILD_ROOT && mkdir $AUTO_BUILD_ROOT

set_environment $BUILD_ROOT

add_required_apps $BUILD_ROOT $BUILD_ARGS

arrange_build_order $BUILD_ROOT $RESOLVED_APPS

if [ -z "$SORTED_APPS" -a -z "$SCRATCH_ARG" ]
then
    echo "You must provide at least one app name or -x (from scratch) option"
    display_usage
fi

print_separator

start=$(date)
echo "  ${0##*/} started at ${start},"
echo "  args: $ALL_ARGS"

check_tools $RPM_DB $SCRATCH_ARG

for app in $SORTED_APPS
do
    if echo " $BUILD_ARGS " | grep -F " ${app} " > /dev/null
    then
        echo "..... Building: $app"
    else
        echo "....... Adding: $app (for dependency resolution)"
    fi
done

print_separator

if [ -n "$BUILD_TOOLS" ]
then
    build_tools $BUILD_ROOT
fi

. $VIRTUAL_ROOT/etc/profile.d/ccm-tools.sh
. $VIRTUAL_ROOT/etc/profile.d/ccm-devel.sh
. $VIRTUAL_ROOT/etc/profile.d/ccm-scripts.sh
export CCM_RPM_DIR=$HOMETOPDIR
export CCM_RPMBUILD_FLAGS="$RPM_ARGS"
export CLASSPATH="$CLASSPATH:$VIRTUAL_ROOT/usr/share/java/ccm-servlet-2.3.jar"

for app in $SORTED_APPS
do
    build_app_conditionally $BUILD_ROOT "$BUILD_ARGS" $app
done

echo "  ${0##*/}   started at ${start},"
echo "                completed at $(date)."

print_separator
