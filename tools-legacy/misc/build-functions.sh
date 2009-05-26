function print_separator() {
    echo "--------------------------------------------------------------------------------------------------"
}

# Gets svn revision for app whose tree starts in current dir.
#    INPUT:
#         arg1 (optional) dir to check, default is the current dir
#    OUTPUT:
#         $SVN_REVISION
function get_svn_revision {
    local DIR=$1
    if [ -n "$DIR" ]; then
        pushd $DIR > /dev/null
    fi
    SVN_REVISION=""
    local revision=$(svn info . | grep -i '^last changed rev' | cut '-d ' -f4)
    local ccm_app=$(pwd | sed 's!.*/!!')
    if [ -z "$revision" ]; then
        echo "Could not find the most recent svn revision number for $ccm_app"
        exit 1
    fi
    #  Woo hoo, we have revision number now!
    echo "Found the svn revision number for $ccm_app: $revision"
    SVN_REVISION=".r$revision"
    if [ -n "$DIR" ]; then
        popd > /dev/null
    fi
}


# Bails out if uncommitted svn changes found in current dir
function check_svn_clean {
    local DIR=$1
    if [ -n "$DIR" ]; then
        pushd $DIR > /dev/null
    fi
    echo "Running 'svn status' in $(pwd)"
    local svnstatus=$(svn st | grep -v '^\?')
    if [ -n "$svnstatus" ]; then
        echo "$svnstatus"
        echo "Uncommited changes exist.  Bailing out."
        exit 1
    fi
    if [ -n "$DIR" ]; then
        popd > /dev/null
    fi
}

function check_svn_tagged {
    SVN_REVISION=
    if [ -n "$SVN_TAGGED" ]; then
        check_svn_clean $@ &&  get_svn_revision $@
    fi
}

# INPUT:
#    arg1:  Build root dir
#    arg2:  app dir, whose properties we're about to inquire
#    arg3:  SVN_TAGGED
# OUTPUT:
#    $APP_NAME
#    $APP_VERSION
#    $RELEASE
function set_application_properties() {

        local BUILD_HOME=$1
        local APP=$2
        local SVN_TAGGED=$3

        local PROJ_FILE=$BUILD_HOME/$APP/project.xml

        if [ ! -e $PROJ_FILE ]; then
            echo "$PROJ_FILE doesn't exist"
            exit 1;
        fi

        local VERSION_FROM=`grep "versionFrom=\".*\"" $PROJ_FILE | sed 's/.*versionFrom="\(.*\)"/\1/'`

        if [ -z $VERSION_FROM ]; then
            echo "versionFrom not found"
        fi

        local APP_FILE=$BUILD_HOME/$APP/$VERSION_FROM/application.xml

        if [ ! -e $APP_FILE ]; then
            echo "$APP_FILE doesn't exist"
            exit 1
        fi

        local FRAGMENT=$BUILD_HOME/$APP/application.xml.frag

        awk 'BEGIN { inTag = 0; } /<ccm:application/ { inTag=1; } inTag==1 { print $0; } inTag==1 && />/ { inTag = 0; }' \
            $APP_FILE > $FRAGMENT

        APP_VERSION=`grep 'version=\"[^"]*\"' $FRAGMENT | sed 's/.*version="\([^\"]*\)".*/\1/'`
        RELEASE=`grep 'release=\"[^"]*\"' $FRAGMENT | sed 's/.*release="\([^\"]*\)".*/\1/'`
        if [ -n "$SVN_TAGGED" ]; then
            get_svn_revision $BUILD_HOME/$APP/$APP
            RELEASE="${RELEASE}${SVN_REVISION}"
        fi

        APP_NAME=`grep 'name=\"[^"]*\"' $FRAGMENT | sed 's/.*name="\([^\"]*\)".*/\1/'`

        rm -f $FRAGMENT

}


# Here's how we deal with eq,lt,le,gt,ge relations:
#
#  [eq]: - easiest, we look for the exact match
#
#  [ge]: we compose the list which is made up from
# installed version(s) of the required packages and the required
# version.
#
# An example:
#    <ccm:requires name="ccm-core" version="6.1.2" relation="ge"/>
# Now suppose that we have following ccm-core available in our local rpm db:
#   ccm-core-6.1.1
#   ccm-core-6.1.3
#
# We now construct a list of versions:
#
#   6.1.1
#   6.1.3
#   6.1.2.-1
#
#  First two are installed ones, the last is one mentioned in Requires:
# We call that one the 'tagged' version.
# We append '.-1' b/c we're just about to sort numerically that list, which
# will put the tagged version before the 6.1.2, if such exists.  Sorting is
# performed numerically with field delimiter being period (.), so the sorted
# list looks like:
#
#   6.1.1
#   6.1.2.-1
#   6.1.3
#
# We now grep this list for the tagged version (6.1.2.-1), and take the first line *below*
# the match.  In the case 6.1.2 was available, it will yield a match, since it will be
# the first one below the tag 6.1.2.-1.  However, in our example, the match is '6.1.3'.
# In case there are no lines after the tagged version, dependencies can't be met.
#
#  [le]: similar to [ge], except that the tagged version carries the .001 suffix.
# We proceed with composing the list and sorting it as above, but we take the first
# line *above* the tagged version.  If no line exists above the tagged version,
# dependencies can't be met.
#
#  [gt,lt]: similar to [ge,le], except that we do no funky suffixes to
# the tagged version.  Every time we sort, we use '-u' to supress duplicate lines.
# We then grep for the tagged version and take the first one below or above it,
# respectively.
#
#  INPUT:
#    arg1: path to local rpm database
#    arg2: package name to check
#    arg3: package version to check; optional
#    arg4: dependency relation (eq, le, lt, ge, gt); optional, default = eq
#          checked only if package version (arg3) is provided
#  OUTPUT:
#    RPM_CHECK_ERROR: empty on successful check
#    RPM_MATCH: version number of installed package that resolves dependency
#
function check_local_rpm() {
    local rpmdb=$1
    local pkg_name=$2
    local pkg_version=$3
    local pkg_relation=$4

    if [ -z "$pkg_relation" ]
    then
        pkg_relation=eq
    fi

    local dependency="$2 $4 $3"

    local output=""
    local sortedoutput=""
    local match=""

    if [ -n "$pkg_version" ]
    then
        local pkg_tagged_version="$pkg_version"

        # Mangle the tagged version
        case $pkg_relation in
            le)  pkg_tagged_version="${pkg_version}.001";;
            ge)  pkg_tagged_version="${pkg_version}.-1";;
        esac

        output=$(rpm --dbpath $rpmdb -q --queryformat '%{VERSION}\n' $pkg_name | grep -v 'is not installed') || true;
        sortedoutput=$(echo -e "$output\n$pkg_tagged_version")

        sortedoutput=$(echo "$sortedoutput" | sort -u -n -t. -k1,1 -k2,2 -k3,3 -k4,4 -k5,5)

        # echo -e "output: \n$output"
        # echo -e "sortedoutput: \n$sortedoutput"

        case $pkg_relation in
            eq) match=$(echo "$output" | grep -F -x "$pkg_version") ;;
            le|lt) match=$(echo "$sortedoutput" | grep -B1 -F -x "$pkg_tagged_version" | head --lines=-1) ;;
            ge|gt) match=$(echo "$sortedoutput" | grep -A1 -F -x "$pkg_tagged_version" | tail --lines=+2) ;;
        esac
    else
        output=$(rpm --dbpath $rpmdb -q --queryformat '%{VERSION}\n' $pkg_name | grep -v 'is not installed') \
            && match=$(echo "$output" | sort -u -n -t. -k1,1 -k2,2 -k3,3 -k4,4 -k5,5 | tail -1)
    fi

    RPM_MATCH=""
    RPM_CHECK_ERROR=""

    if [ "$(echo "$match" | sed 's/ //g')" = "" ]
    then
        if [ "$output" != "" ]
        then
            RPM_CHECK_ERROR="'$output'"
        else
            RPM_CHECK_ERROR="'$pkg_name' not built at all"
        fi
    else
        RPM_MATCH="$match"
    fi
}



#   Generate skeleton project.xml.
#
# We will generate the <ccm:prebuilt> block by expanding
# the <ccm:dependencies> for each required CCM app in turn.
# The actual expansion work will be done by a perl script.
# However we must deal with the relation="eq,gt,ge,lt,le" as well.
# This is where life gets interesting.  This shell function will
# use:
#    1. the output of the perl script, which looks like:
#        ccm-core -> version="6.1.1" relation="ge"
#        ccm-forum -> version="1.4.2"
#    2. local rpm database
# to find out which RPM packages (installed in scratch RPM
# database, and which have been built during the execution of
# this very script) can satisfy the dependencies listed.
#
#  INPUT:
#      arg1: build root dir
#      arg2: ccm app name, which must match the directory name in
#            the build root dir
#
#  OUTPUT:
#       - project.xml will be written to the $arg1/$arg2 directory
#       - RPM_CHECK_ERROR will contain the error message, if something
#         goes wrong, eg. unresolved dependencies

function write_project_xml() {

    echo "Generating project.xml"

    local BUILD_ROOT="$1"
    local CCM_APP="$2"

    local PROJ_FILE="$BUILD_ROOT/$CCM_APP/project.xml"

    rm -f $PROJ_FILE

    cat > $PROJ_FILE <<EOF
<?xml version="1.0" encoding="ISO-8859-1"?>

<ccm:project name="$CCM_APP"
       prettyName="Red Hat Web Application Framework"
         ccmVersion="6.1"
      versionFrom="$CCM_APP"
        xmlns:ccm="http://ccm.redhat.com/ccm-project">

  <ccm:build>
    <ccm:application name="$CCM_APP"/>
  </ccm:build>

  <ccm:prebuilt>
EOF

    $BUILD_ROOT/tools/misc/expand-dependencies $BUILD_ROOT $CCM_APP |
    while read app_dep
    do
        echo -n "  Processing dependency: $app_dep"

        local app_name=$(expr match "$app_dep" '\([^ ]*\) *->')
        local app_version=$(expr match "$app_dep" '.*version="\([^"]*\)"')
        local app_relation=$(expr match "$app_dep" '.*relation="\([^"]*\)"')

        check_local_rpm $RPM_DB $app_name $app_version $app_relation

        if [ -n "$RPM_CHECK_ERROR" ]
        then
            echo " ... $RPM_CHECK_ERROR"
            # It seems there's no easy way to pass the error message to the caller
            # b/c of while ... do ... done construct constraints
            echo error >> $PROJ_FILE
        else
            echo " ... found '$RPM_MATCH'"
            echo "<ccm:application name=\"$app_name\" version=\"$RPM_MATCH\"/>" >> $PROJ_FILE
        fi

    done

    local FAILED=$(grep -c ^error $PROJ_FILE)

    if [ "$FAILED" -gt 0 ]
    then
        echo "$FAILED unresolved dependencies"
        exit 1
    fi


    cat >> $PROJ_FILE << EOF2
  </ccm:prebuilt>

</ccm:project>
EOF2

    echo "Successfully written $PROJ_FILE"

}



#  INPUT:
#      arg1: build root dir
#
function set_environment() {

    local BUILD_HOME=$1
    export VIRTUAL_ROOT=$BUILD_HOME/root
    if [ -f $BUILD_HOME/rpm ]
    then
        RPM="$BUILD_HOME/rpm"
    else
        RPM="$(which rpm)"
    fi

    export RPM

    export RPM_DB=$VIRTUAL_ROOT/rpmdb
    export RPM_ARGS="--dbpath $RPM_DB"

    export HOMETOPDIR="$(echo ~/rpm)"
    export HOMERPMDIR="$HOMETOPDIR/RPMS/noarch"
    export HOMESRPMDIR="$HOMETOPDIR/SRPMS"
    export HOMEBUILDDIR="$HOMETOPDIR/BUILD"

    export PATH=$VIRTUAL_ROOT/usr/bin:$PATH
}


#  INPUT:
#      arg1: build root dir
#
function build_tools() {

    local BUILD_HOME=$1
    set_environment $BUILD_HOME

    rm -rf $VIRTUAL_ROOT

    mkdir $VIRTUAL_ROOT
    mkdir $RPM_DB

    # (20041108 Seb)
    #  This directory must exist because 'rpm -i' of httpunit and friends will fail.
    #  In a real world, this dir would have been already created by the packages httpunit at ali depend on.
    mkdir -p $VIRTUAL_ROOT/usr/share/java
    mkdir -p $VIRTUAL_ROOT/etc/ccm
    mkdir -p $VIRTUAL_ROOT/etc/profile.d
    mkdir -p $VIRTUAL_ROOT/usr/share/ccm-tools
    mkdir -p $VIRTUAL_ROOT/usr/share/doc
    mkdir -p $VIRTUAL_ROOT/usr/bin
    mkdir -p $VIRTUAL_ROOT/var/cache
    mkdir -p $VIRTUAL_ROOT/var/lib/ccm
    mkdir -p $VIRTUAL_ROOT/var/lib/ccm-devel
    mkdir -p $VIRTUAL_ROOT/var/log
    mkdir -p $VIRTUAL_ROOT/var/opt/ccm/data

    $RPM $RPM_ARGS --initdb

    cat > fake.spec <<EOF
Name: fake
Version: 2.0.0
Release: 1
BuildArchitectures: noarch
Summary: fake depends
License: None
Group: Fake
Provides: /bin/bash
Provides: /bin/sh
Provides: /usr/bin/perl
Provides: /bin/tar
Provides: /usr/bin/md5sum
Provides: /usr/bin/locate
Provides: /usr/bin/rpmbuild
Provides: /usr/bin/unzip
Provides: /usr/bin/zip
Provides: tomcat4
Provides: resin
Provides: perl(File::Path)
Provides: perl(Getopt::Long)
Provides: perl(Sys::Hostname)
Provides: perl(strict)
Provides: perl(Exporter)
Provides: perl(vars)
Provides: perl(Cwd)
Provides: perl(File::Basename)
Provides: perl(File::Spec)
Provides: perl(File::Copy)
Provides: perl(File::Find)
Provides: perl(File::stat)
Provides: perl(POSIX)
Provides: perl(Carp)
Provides: perl(lib)
Provides: perl
Provides: ant = 0:1.6.2
Provides: ant-nodeps = 0:1.6.2
Provides: ant-contrib = 0:0.6


%description
Fake

%files
EOF

    rpmbuild --define="_topdir $HOMETOPDIR" -ba fake.spec

    $RPM $RPM_ARGS -ivh --noscripts $HOMERPMDIR/fake-2.0.0-1.noarch.rpm

    export RPM_DIR=$HOMETOPDIR

    # Build the build tools
    for i in ccm-java httpunit junit junitperf servlet
    do
      pushd $BUILD_HOME/tools/rpms/$i > /dev/null
      (
        print_separator
        set -e
        check_svn_tagged
        ./rollingbuild.sh
        TOOLS_NAME=`grep 'Name:' *.spec | sed -e 's/Name://' | sed -e 's/ //g'`
        TOOLS_VERSION=`grep 'Version:' *.spec | sed -e 's/Version://' | sed -e 's/ //g'`
        TOOLS_RELEASE=`grep 'Release:' *.spec | sed -e 's/Release://' | sed -e 's/ //g'`
        $RPM $RPM_ARGS -ivh --noscripts --relocate /usr=$VIRTUAL_ROOT/usr --relocate /etc=$VIRTUAL_ROOT/etc $HOMERPMDIR/$TOOLS_NAME-$TOOLS_VERSION-$TOOLS_RELEASE.noarch.rpm
      ) || exit $?
      popd > /dev/null
    done

    pushd $BUILD_HOME/tools/tools > /dev/null
    (
      print_separator
      set -e
      check_svn_tagged
      TOOLS_VERSION=`grep 'VERSION=' configure.in | sed -e 's/VERSION=//'`
      TOOLS_RELEASE="`grep 'RELEASE=' configure.in | sed -e 's/RELEASE=//'`${SVN_REVISION}"
      ccm_tools_home="$HOMEBUILDDIR/ccm-tools-$TOOLS_VERSION"
      CCM_TOOLS_HOME="$ccm_tools_home" ./rollingbuild.sh
      $RPM $RPM_ARGS -ivh --noscripts --relocate /usr=$VIRTUAL_ROOT/usr --relocate /etc=$VIRTUAL_ROOT/etc --relocate /var=$VIRTUAL_ROOT/var $HOMERPMDIR/ccm-tools-$TOOLS_VERSION-$TOOLS_RELEASE.noarch.rpm
    ) || exit $?
    popd > /dev/null
    . $VIRTUAL_ROOT/etc/profile.d/ccm-tools.sh

    pushd $BUILD_HOME/tools/devel > /dev/null
    (
      print_separator
      set -e
      check_svn_tagged
      CCM_TOOLS_HOME=$VIRTUAL_ROOT/usr/share/ccm-tools ./rollingbuild.sh
      DEVEL_VERSION=`grep 'VERSION=' configure.in | sed -e 's/VERSION=//'`
      DEVEL_RELEASE="`grep 'RELEASE=' configure.in | sed -e 's/RELEASE=//'`${SVN_REVISION}"
      $RPM $RPM_ARGS -ivh --noscripts --relocate /usr=$VIRTUAL_ROOT/usr --relocate /etc=$VIRTUAL_ROOT/etc --relocate /var=$VIRTUAL_ROOT/var $HOMERPMDIR/ccm-devel-$DEVEL_VERSION-$DEVEL_RELEASE.noarch.rpm
    ) || exit $?
    popd > /dev/null
    . $VIRTUAL_ROOT/etc/profile.d/ccm-devel.sh

    pushd $BUILD_HOME/tools/scripts > /dev/null
    (
      print_separator
      set -e
      check_svn_tagged
      CCM_TOOLS_HOME=$VIRTUAL_ROOT/usr/share/ccm-tools ./rollingbuild.sh
      SCRIPTS_VERSION=`grep 'VERSION=' configure.in | sed -e 's/VERSION=//'`
      SCRIPTS_RELEASE="`grep 'RELEASE=' configure.in | sed -e 's/RELEASE=//'`${SVN_REVISION}"
      $RPM $RPM_ARGS -ivh --noscripts --relocate /usr=$VIRTUAL_ROOT/usr --relocate /etc=$VIRTUAL_ROOT/etc $HOMERPMDIR/ccm-scripts-$SCRIPTS_VERSION-$SCRIPTS_RELEASE.noarch.rpm
    ) || exit $?
    popd > /dev/null
    . $VIRTUAL_ROOT/etc/profile.d/ccm-scripts.sh

    pushd $BUILD_HOME/tools/bundle > /dev/null
    (
      print_separator
      set -e
      check_svn_tagged
      ./rollingbuild.sh
      BUNDLE_VERSION=`grep 'VERSION=' configure.in | sed -e 's/VERSION=//'`
      BUNDLE_RELEASE="`grep 'RELEASE=' configure.in | sed -e 's/RELEASE=//'`${SVN_REVISION}"
      $RPM $RPM_ARGS -ivh --noscripts --relocate /usr=$VIRTUAL_ROOT/usr  $HOMERPMDIR/ccm-tools-bundle-$BUNDLE_VERSION-$BUNDLE_RELEASE.noarch.rpm
    ) || exit $?
    popd > /dev/null

    # This is somewhat lame-ish way, but we really need
    # ant-contrib package installed.
    # The build system needs ant-contrib.jar (<if> task).
    # It is no longer present in the ccm-devel, so the appropriate
    # RPM must be installed beforehand.

    if rpm -q ant-contrib | grep 'not installed'
    then
        echo "Please install ant-contrib RPM, the build system depends on it."
        exit 1
    else
        for file in $(rpm -ql ant-contrib)
        do
            if [ -f $file ]
            then
                local dir=$VIRTUAL_ROOT${file%/*}
                mkdir -p $dir
                cp -f $file $dir
            fi
        done
    fi

}

#  INPUT:
#      arg1: build root dir
#      arg2: ccm app dir below the build root
#      $VIRTUAL_ROOT
#      $SVN_TAGGED
#      $RPM
#      $RPM_ARGS
#      $HOMERPMDIR
function build_app() {
    local BUILD_ROOT=$1
    local CCM_APP=$2
    pushd $BUILD_ROOT/$CCM_APP > /dev/null
    (
      echo "BUILDING $CCM_APP"

      write_project_xml $BUILD_ROOT $CCM_APP

      rm -rf rollingbuild build MANIFEST MANIFEST.SKIP

      check_svn_tagged $CCM_APP

      CCM_SHARED_LIB_DIST_DIR=$VIRTUAL_ROOT/usr/share/java CCM_CONFIG_LIB_DIR=$VIRTUAL_ROOT/usr/share/java CCM_TOOLS_HOME=$VIRTUAL_ROOT/usr/share/ccm-tools CCM_SCRIPTS_HOME=$VIRTUAL_ROOT/usr/share/ccm-scripts CCM_CONFIG_HOME=$VIRTUAL_ROOT/usr/share/ccm-devel $VIRTUAL_ROOT/usr/share/ccm-scripts/bin/build.sh

    ) || exit $?
    (
      set -e
      set_application_properties $BUILD_ROOT $CCM_APP $SVN_TAGGED
      $RPM $RPM_ARGS --oldpackage --replacefiles --replacepkgs --relocate /usr=$VIRTUAL_ROOT/usr --relocate /etc=$VIRTUAL_ROOT/etc -Uvh $HOMERPMDIR/$APP_NAME-$APP_VERSION-$RELEASE.noarch.rpm
    ) || exit $?
    popd > /dev/null
}

#  INPUT:
#     arg1: build root dir
#     arg2, arg3, ....: list of apps to build
#  OUTPUT:
#     $SORTED_APPS
#
function arrange_build_order() {
    pushd $1 > /dev/null
    shift
    SORTED_APPS=""
    local apps="$@"
    local app_build_order=$(grep ccm:requires */*/application.xml | sed 's!^\(ccm-[^/]*\)/.*name="\([^"]*\)".*!\2 \1!' | tsort)
    local app
    for app in $app_build_order
    do
        if echo " ${apps} " | grep -F " ${app} " > /dev/null
        then
            SORTED_APPS="$SORTED_APPS $app"
        fi
    done

    for app in $apps
    do
        if ! echo " ${SORTED_APPS} " | grep -F " ${app} " > /dev/null
        then
            echo "Could not read $app/*/application.xml."
            echo "Make sure to include a copy or symbolic link of this application in the build area."
            exit 1
        fi
    done
    popd > /dev/null
}


#  INPUT:
#     arg1: build root dir
#     arg2, arg3, ....: list of apps to build
#  OUTPUT:
#     $RESOLVED_APPS  ... a list of all applications to build, with dependencies resolved,
#                         might contain duplicates
#
function add_required_apps() {
    local BUILD_HOME=$1
    shift
    RESOLVED_APPS=""
    local app
    local deps
    for app in "$@"
    do
        deps=$($BUILD_HOME/tools/misc/expand-dependencies $BUILD_HOME $app) || {
            echo "  Could not build the dependencies for: $app"
            echo "  Consult earlier messages, if any, to determine the culprit."
            exit 1
        }
        RESOLVED_APPS=$(echo -e "$app\n$RESOLVED_APPS\n$deps" | cut -d " " -f1)
    done
    # A lame way to convert all whitespace into single space, I know, ...
    RESOLVED_APPS=$(echo $RESOLVED_APPS)
}


#  INPUT:
#     arg1: build root dir
#     arg2: list of args to build supplied on the command line
#     arg3: app to check and, maybe, build
#     $RPM_DB
#
#  The arg3 app will be built unconditionally if it's included in $BUILD_ARGS.
# Otherwise, it will only be built if it hasn't already been built.
#
function build_app_conditionally() {
    local BUILD_ROOT=$1
    local BUILD_ARGS=$2
    local CCM_APP=$3
    if echo " $BUILD_ARGS " | grep -F " ${CCM_APP} " > /dev/null
    then
        build_app $BUILD_ROOT $CCM_APP
    else
        check_local_rpm $RPM_DB $CCM_APP
        if [ -z "$RPM_CHECK_ERROR" ]
        then
            echo "Application '$CCM_APP' appears already built ($RPM_MATCH), skipping ..."
        else
            build_app $BUILD_ROOT $CCM_APP
        fi
    fi
    print_separator
}


#  INPUT:
#     arg1: path to local rpm database
#     arg2: non-empty if -x has been provided on the command line
#     $RPM_DB
#
#  OUTPUT:
#     $BUILD_TOOLS non-empty if tools must be built
#
function check_tools() {
    BUILD_TOOLS=yes
    if [ -n "$2" ]
    then
        echo "Starting the build process from scratch on user demand (-x flag provided)."
    elif rpm -q --dbpath $1 ccm-tools-bundle > /dev/null
    then
        BUILD_TOOLS=
        echo "Tools OK."
    else
        echo "Starting the build process from scratch, tools appear built incompletely or not at all."
    fi
    echo "${BUILD_TOOLS:+..... Building: TOOLS}"
}



