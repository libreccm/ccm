#!/bin/sh
#
# Build everything!

[ -z "$BUNDLETEMPLATE" ] && BUNDLETEMPLATE="`pwd`/tools/bundle/TEMPLATE"
[ -z "$BUNDLEDIR" ] && BUNDLEDIR="ccm-ldn-aplaws/ccm-ldn-aplaws"
[ -z "$BUNDLES" ] && BUNDLES="standard complete demo devel"

set -e
#set -v
#set -x

PACKAGE_ARG=0
BUILD_ARG=0
DOCS_ARG=0
NO_JAVADOC_ARG=0

export AUTO_BUILD_ROOT=/var/tmp/$USER-auto-build-root
rm -rf $AUTO_BUILD_ROOT && mkdir $AUTO_BUILD_ROOT;

function display_usage() {
    cat <<EOF
Usage: $0 [OPTIONS] APPS-FILE

Options:
 -?        Display this usage message
 -p        Run packaging step
 -b        Run application build step
 -d        Run documentation build step
 -j        Don't build javadoc (app-doc.rpm)
 -r [name] Restart application build from app [name]
EOF
    exit 1
}

#   Generate skeleton project.xml.
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
function write_project_xml() {

    rm -f project.xml

    cat > project.xml <<EOF
<?xml version="1.0" encoding="ISO-8859-1"?>

<ccm:project name="$CCM_APP"
       prettyName="Red Hat Web Application Framework"
         ccmVersion="6.1"
      versionFrom="$CCM_APP"
        xmlns:ccm="http://ccm.redhat.com/ccm-project">

  <ccm:build>
    <ccm:application name="$CCM_APP" prettyName="Red Hat Web Application Framework" buildOrder="1"/>
  </ccm:build>

  <ccm:prebuilt>
EOF



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
    #  [gt,lt]: similar to [ge,le] respectively, except that we do no funky suffixes to
    # the tagged version.  Every time we sort, we use '-u' to supress duplicate lines.
    # We then grep for the tagged version and take the first one below or above it,
    # respectively.

    $BUILD_HOME/tools/misc/expand-dependencies $BUILD_HOME $CCM_APP |
    while read app_dep
    do
        echo -n "Processing dependency: $app_dep"

        app_name=$(expr match "$app_dep" '\([^ ]*\) *->')
        app_version=$(expr match "$app_dep" '.*version="\([^"]*\)"')
        app_relation=$(expr match "$app_dep" '.*relation="\([^"]*\)"')

        if [ "$app_relation" = "" ]
        then
            app_relation=eq
        fi

        app_tagged_version="$app_version"

        # Mangle the tagged version
        case $app_relation in
            le)  app_tagged_version="${app_version}.001";;
            ge)  app_tagged_version="${app_version}.-1";;
        esac

        output=$(rpm $RPM_ARGS -q --queryformat '%{VERSION}\n' $app_name | grep -v 'is not installed')
        sortedoutput=$(echo -e "$output\n$app_tagged_version")

        sortedoutput=$(echo "$sortedoutput" | sort -u -n -t. -k1,1 -k2,2 -k3,3 -k4,4 -k5,5)

        # echo -e "output: \n$output"
        # echo -e "sortedoutput: \n$sortedoutput"

        case $app_relation in
            eq) match=$(echo "$output" | grep -F -x "$app_version") ;;
            le|lt) match=$(echo "$sortedoutput" | grep -B1 -F -x "$app_tagged_version" | head --lines=-1) ;;
            ge|gt) match=$(echo "$sortedoutput" | grep -A1 -F -x "$app_tagged_version" | tail --lines=+2) ;;
        esac

        if [ "$(echo "$match" | sed 's/ //g')" = "" ]
        then
            echo " ... unsatisfied!"
            if [ "$output" != "" ]
            then
                echo "  Found only: $output, check $APPS_FILE and $CCM_APP/$CCM_APP/application.xml"
            else
                echo
                echo -n "  Application $app_name not built at all"
                if echo " ${APPS} " | grep -F " ${app_name} " > /dev/null
                then
                    :
                else
                    echo -n ", please add it to $APPS_FILE"
                fi
                echo
                echo "    Restart the build process with '-r $app_name'"
                echo
            fi

            exit 1
        fi

        echo " ... found '$match'"

        echo "<ccm:application name=\"$app_name\" version=\"$match\"/>" >> project.xml

    done
    if [ "$?" == 1 ]
    then
        exit 1
    fi

    cat >> project.xml << EOF2
  </ccm:prebuilt>

</ccm:project>
EOF2

}


# Gets svn revision for app whose tree starts in current dir.
function get_svn_revision {
    SVN_REVISION=""
    revision=$(svn info . | grep -i '^last changed rev' | cut '-d ' -f4)
    ccm_app=$(pwd | sed 's!.*/!!')
    if [ -z "$revision" ]; then
        echo "Could not find the most recent svn revision number for $ccm_app"
        exit 1
    fi
    #  Woo hoo, we have revision number now!
    echo "Found the svn revision number for $ccm_app: $revision"
    SVN_REVISION=".r$revision"
}


# Bails out if uncommitted svn changes found in current dir
function check_svn_clean {
    echo "Running 'svn status' in $(pwd)"
    svnstatus=$(svn st | grep -v '^\?')
    if [ -n "$svnstatus" ]; then
        echo "$svnstatus"
        echo "Uncommited changes exist.  Bailing out."
        exit 1
    fi
}

function check_svn_tagged {
    SVN_REVISION=
    if [ -n "$SVN_TAGGED" ]; then
        check_svn_clean  &&  get_svn_revision
    fi
    export SVN_REVISION
}

while getopts r:qpdbvj opt; do
    case $opt in
        r) RESTART_FROM=$OPTARG;;
        p) PACKAGE_ARG=1;;
        d) DOCS_ARG=1;;
        b) BUILD_ARG=1;;
        j) export CCM_BUILD_NO_DOCS=1;;
        q) export CCM_DIST_NOZIPS=1;;
        v) set -v;;
        [?]) display_usage
    esac
done

if [ $PACKAGE_ARG == 1 -o $BUILD_ARG == 1 -o $DOCS_ARG == 1 ]; then
  DO_BUILD=0
  DO_DOCS=0
  DO_PACKAGE=0
  if [ $PACKAGE_ARG == 1 ]; then
    DO_PACKAGE=1
  fi
  if [ $DOCS_ARG == 1 ]; then
    DO_DOCS=1
  fi
  if [ $BUILD_ARG == 1 ]; then
    DO_BUILD=1
  fi
else
  DO_BUILD=1
  DO_DOCS=1
  DO_PACKAGE=1
fi

echo "Building apps: $DO_BUILD"
echo "Building docs: $DO_DOCS"
echo "Building ISOs: $DO_PACKAGE"
echo "Skipping Javadoc RPMs: ${CCM_BUILD_NO_DOCS:-0}"

shift $(($OPTIND-1))

APPS_FILE=$1
if [ -z $APPS_FILE ]; then
    display_usage
fi

if [ ! -e $APPS_FILE ]; then
    echo "$APPS_FILE doesn't exist"
    display_usage
fi

# get rid of newlines
apps=$(echo $(cat $APPS_FILE))

APPS=""
# First, reorder the supplied list of apps in proper dependency order
app_build_order=$(grep ccm:requires */*/application.xml | sed 's!^\(ccm-[^/]*\)/.*name="\([^"]*\)".*!\2 \1!' | tsort)
for app in $app_build_order
do
    if echo " ${apps} " | grep -F " ${app} " > /dev/null
    then
        APPS="$APPS $app"
    fi
done

for app in $apps
do
    if ! echo " ${APPS} " | grep -F " ${app} " > /dev/null
    then
        echo "Could not read $app/*/application.xml."
	echo "Make sure to include a copy or symbolic link of this application in the build area."
        exit 1
    fi
done

BUILD_APPS=""
BOOTSTRAP=1
for app in $APPS
do
    if [ "x$RESTART_FOUND" = "x1" -o "x$RESTART_FROM" = "x$app" ]; then
        RESTART_FOUND=1;
        BOOTSTRAP=0
        BUILD_APPS="$BUILD_APPS $app";
    fi
done

if [ -z "$BUILD_APPS" ]; then
  BUILD_APPS=$APPS
fi


if [ ! -z $RESTART_FROM ] && [ -z $RESTART_FOUND ] && [ $DO_BUILD = 1 ]; then
    echo "$RESTART_FROM not found in app list";
    display_usage
fi

echo "Processing apps: $apps"
echo "Building apps in order: $BUILD_APPS"

BUILD_HOME=`pwd`
VIRTUAL_ROOT=$BUILD_HOME/root
if [ -f $BUILD_HOME/rpm ]
then
    RPM="$BUILD_HOME/rpm"
else
    RPM="$(which rpm)"
fi

RPM_DB=$VIRTUAL_ROOT/rpmdb
RPM_ARGS="--dbpath $RPM_DB"

HOMETOPDIR="$(echo ~/rpm)"
HOMERPMDIR="$HOMETOPDIR/RPMS/noarch"
HOMESRPMDIR="$HOMETOPDIR/SRPMS"
HOMEBUILDDIR="$HOMETOPDIR/BUILD"

PATH=$VIRTUAL_ROOT/usr/bin:$PATH

  RUNTIME_DOCS="core/developer-guide"
  RUNTIME_DOCS="$RUNTIME_DOCS core/install-guide"
  RUNTIME_DOCS="$RUNTIME_DOCS cms/developer-guide"
  RUNTIME_DOCS="$RUNTIME_DOCS cms/deployment-guide"
  RUNTIME_DOCS="$RUNTIME_DOCS cms/admin-guide"

  APLAWS_DOCS="aplaws/quick-start"
  APLAWS_DOCS="$APLAWS_DOCS aplaws/admin-guide"
  APLAWS_DOCS="$APLAWS_DOCS themes"

  ALL_DOCS="$RUNTIME_DOCS $APLAWS_DOCS"
  ALL_DOCS="$ALL_DOCS aplaws/config-reference/complete"
  ALL_DOCS="$ALL_DOCS aplaws/config-reference/standard"
  ALL_DOCS="$ALL_DOCS aplaws/config-reference/demo"
  ALL_DOCS="$ALL_DOCS aplaws/config-reference/devel"



set_application_properties() {
        APP=$1

        if [ ! -e $APP/project.xml ]; then
            echo "project.xml doesn't exist";
            exit 1;
        fi

        VERSION_FROM=`grep "versionFrom=\".*\"" $APP/project.xml | sed 's/.*versionFrom="\(.*\)"/\1/'`

        if [ -z $VERSION_FROM ]; then
            echo "versionFrom not found";
        fi

        if [ ! -e $APP/$VERSION_FROM/application.xml ]; then
            echo "application.xml doesn't exist for $VERSION_FROM";
            exit 1
        fi

        cat $APP/$VERSION_FROM/application.xml | awk 'BEGIN { inTag = 0; } /<ccm:application/ { inTag=1; } inTag==1 { print $0; } inTag==1 && />/ { inTag = 0; }' > application.xml.frag

        APP_VERSION=`grep 'version=\"[^"]*\"' application.xml.frag | sed 's/.*version="\([^\"]*\)".*/\1/'`
        RELEASE=`grep 'release=\"[^"]*\"' application.xml.frag | sed 's/.*release="\([^\"]*\)".*/\1/'`
        if [ -n "$SVN_TAGGED" ]; then
            cd $APP/$APP
            get_svn_revision
            cd ../..
            RELEASE="${RELEASE}${SVN_REVISION}"
        fi

        APP_NAME=`grep 'name=\"[^"]*\"' application.xml.frag | sed 's/.*name="\([^\"]*\)".*/\1/'`

        rm application.xml.frag
}

if [ $DO_BUILD = 1 -a $BOOTSTRAP = 1 ]; then
  echo "Starting build"

  rm -rf $VIRTUAL_ROOT

  mkdir $VIRTUAL_ROOT
  mkdir $VIRTUAL_ROOT/rpmdb

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
  (
    set -e
    cd tools/rpms/$i
    check_svn_tagged
    ./rollingbuild.sh
    TOOLS_NAME=`grep 'Name:' *.spec | sed -e 's/Name://' | sed -e 's/ //g'`
    TOOLS_VERSION=`grep 'Version:' *.spec | sed -e 's/Version://' | sed -e 's/ //g'`
    TOOLS_RELEASE=`grep 'Release:' *.spec | sed -e 's/Release://' | sed -e 's/ //g'`
    $RPM $RPM_ARGS -ivh --noscripts --relocate /usr=$VIRTUAL_ROOT/usr --relocate /etc=$VIRTUAL_ROOT/etc $HOMERPMDIR/$TOOLS_NAME-$TOOLS_VERSION-$TOOLS_RELEASE.noarch.rpm
  ) || exit $?
done

(
  set -e
  cd tools/tools
  check_svn_tagged
  TOOLS_VERSION=`grep 'VERSION=' configure.in | sed -e 's/VERSION=//'`
  TOOLS_RELEASE="`grep 'RELEASE=' configure.in | sed -e 's/RELEASE=//'`${SVN_REVISION}"
  ccm_tools_home="$HOMEBUILDDIR/ccm-tools-$TOOLS_VERSION"
  CCM_TOOLS_HOME="$ccm_tools_home" ./rollingbuild.sh
  $RPM $RPM_ARGS -ivh --noscripts --relocate /usr=$VIRTUAL_ROOT/usr --relocate /etc=$VIRTUAL_ROOT/etc --relocate /var=$VIRTUAL_ROOT/var $HOMERPMDIR/ccm-tools-$TOOLS_VERSION-$TOOLS_RELEASE.noarch.rpm
) || exit $?
. $VIRTUAL_ROOT/etc/profile.d/ccm-tools.sh

(
  set -e
  cd tools/devel
  check_svn_tagged
  CCM_TOOLS_HOME=$VIRTUAL_ROOT/usr/share/ccm-tools ./rollingbuild.sh
  DEVEL_VERSION=`grep 'VERSION=' configure.in | sed -e 's/VERSION=//'`
  DEVEL_RELEASE="`grep 'RELEASE=' configure.in | sed -e 's/RELEASE=//'`${SVN_REVISION}"
  $RPM $RPM_ARGS -ivh --noscripts --relocate /usr=$VIRTUAL_ROOT/usr --relocate /etc=$VIRTUAL_ROOT/etc --relocate /var=$VIRTUAL_ROOT/var $HOMERPMDIR/ccm-devel-$DEVEL_VERSION-$DEVEL_RELEASE.noarch.rpm
) || exit $?
. $VIRTUAL_ROOT/etc/profile.d/ccm-devel.sh

(
  set -e
  cd tools/scripts
  check_svn_tagged
  CCM_TOOLS_HOME=$VIRTUAL_ROOT/usr/share/ccm-tools ./rollingbuild.sh
  SCRIPTS_VERSION=`grep 'VERSION=' configure.in | sed -e 's/VERSION=//'`
  SCRIPTS_RELEASE="`grep 'RELEASE=' configure.in | sed -e 's/RELEASE=//'`${SVN_REVISION}"
  $RPM $RPM_ARGS -ivh --noscripts --relocate /usr=$VIRTUAL_ROOT/usr --relocate /etc=$VIRTUAL_ROOT/etc $HOMERPMDIR/ccm-scripts-$SCRIPTS_VERSION-$SCRIPTS_RELEASE.noarch.rpm
) || exit $?
. $VIRTUAL_ROOT/etc/profile.d/ccm-scripts.sh

(
  set -e
  cd tools/bundle
  check_svn_tagged
  ./rollingbuild.sh
  BUNDLE_VERSION=`grep 'VERSION=' configure.in | sed -e 's/VERSION=//'`
  BUNDLE_RELEASE="`grep 'RELEASE=' configure.in | sed -e 's/RELEASE=//'`${SVN_REVISION}"
  $RPM $RPM_ARGS -ivh --noscripts --relocate /usr=$VIRTUAL_ROOT/usr  $HOMERPMDIR/ccm-tools-bundle-$BUNDLE_VERSION-$BUNDLE_RELEASE.noarch.rpm
) || exit $?

fi

if [ $DO_BUILD = 1 ]; then
  . $VIRTUAL_ROOT/etc/profile.d/ccm-tools.sh
  . $VIRTUAL_ROOT/etc/profile.d/ccm-devel.sh
  . $VIRTUAL_ROOT/etc/profile.d/ccm-scripts.sh

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
            dir=$VIRTUAL_ROOT${file%/*}
            mkdir -p $dir
            cp -f $file $dir
        fi
    done
fi


export CCM_RPM_DIR=$HOMETOPDIR
export CCM_RPMBUILD_FLAGS="$RPM_ARGS"
export CLASSPATH="$CLASSPATH:$VIRTUAL_ROOT/usr/share/java/ccm-servlet-2.3.jar"


for i in $BUILD_APPS
do
  export CCM_APP=$i
  (
    set -e
    cd $i
    echo "BUILDING $i in directory `pwd`"

    write_project_xml

    export CCM_APPS=$i
    rm -rf rollingbuild build MANIFEST MANIFEST.SKIP

    cd $CCM_APP
    check_svn_tagged
    cd ..

    if [ "x$AUTO_BUILD_COUNTER" != "x" ]; then
        CCM_BUILD_COUNTER=$AUTO_BUILD_COUNTER
        export CCM_BUILD_COUNTER
    fi

    CCM_SHARED_LIB_DIST_DIR=$VIRTUAL_ROOT/usr/share/java CCM_CONFIG_LIB_DIR=$VIRTUAL_ROOT/usr/share/java CCM_TOOLS_HOME=$VIRTUAL_ROOT/usr/share/ccm-tools CCM_SCRIPTS_HOME=$VIRTUAL_ROOT/usr/share/ccm-scripts CCM_CONFIG_HOME=$VIRTUAL_ROOT/usr/share/ccm-devel $VIRTUAL_ROOT/usr/share/ccm-scripts/bin/build.sh

  ) || exit $?
  (
    set -e
    set_application_properties $i
    $RPM $RPM_ARGS --replacefiles --replacepkgs --relocate /usr=$VIRTUAL_ROOT/usr --relocate /etc=$VIRTUAL_ROOT/etc -Uvh $HOMERPMDIR/$APP_NAME-$APP_VERSION-$RELEASE.noarch.rpm
  ) || exit $?
done

for BUNDLE in $BUNDLES
do
  (
    set -e
    cd $BUNDLEDIR/bundles/$BUNDLE
    check_svn_tagged
    $BUNDLETEMPLATE/rollingbuild.sh
  )  || exit $?
done

fi

# Documentation build
if [ $DO_DOCS == 1 ]; then

  for DOC in $ALL_DOCS
  do
    (
      set -e
      cd doc/$DOC
      make
      make zip
    )
  done

fi
# End of package build process

if [ $DO_PACKAGE == 1 ]; then
# Start of packaging proces

rm -rf dist
mkdir dist


# The distribution for runtime
mkdir dist/runtime
mkdir dist/runtime/RPMS
mkdir dist/runtime/SRPMS
mkdir dist/runtime/ZIPS
mkdir dist/runtime/doc
mkdir dist/bundles

(
  set -e

  BUNDLE_RADIX=`rpm -q --specfile tools/bundle/ccm-tools-bundle.spec`
  TOOLS_RADIX=`rpm -q --specfile tools/tools/ccm-tools.spec`
  DEVEL_RADIX=`rpm -q --specfile tools/devel/ccm-devel.spec`
  SCRIPTS_RADIX=`rpm -q --specfile tools/scripts/ccm-scripts.spec`
  JUNIT_RADIX=`rpm -q --specfile tools/rpms/junit/junit.spec`
  JUNITPERF_RADIX=`rpm -q --specfile tools/rpms/junitperf/junitperf.spec`
  HTTPUNIT_RADIX=`rpm -q --specfile tools/rpms/httpunit/httpunit.spec`
  CCM_JAVA_RADIX=`rpm -q --specfile tools/rpms/ccm-java/ccm-java.spec`
  SERVLET_RADIX=`rpm -q --specfile tools/rpms/servlet/servlet.spec`

  tools_chop=${TOOLS_RADIX%-*}
  TOOLS_VERSION=${tools_chop##*-}
  devel_chop=${DEVEL_RADIX%-*}
  DEVEL_VERSION=${devel_chop##*-}
  scripts_chop=${SCRIPTS_RADIX%-*}
  SCRIPTS_VERSION=${scripts_chop##*-}

  for radix in $BUNDLE_RADIX $TOOLS_RADIX $DEVEL_RADIX $SCRIPTS_RADIX $JUNIT_RADIX $JUNITPERF_RADIX $HTTPUNIT_RADIX $CCM_JAVA_RADIX $SERVLET_RADIX
  do
      cp ${HOMERPMDIR}/${radix}.noarch.rpm ${BUILD_HOME}/dist/runtime/RPMS
      if [ -f ${HOMESRPMDIR}/${radix}.src.rpm ]
      then
          cp ${HOMESRPMDIR}/${radix}.src.rpm ${BUILD_HOME}/dist/runtime/SRPMS
      fi
  done

  if [ "x$CCM_DIST_NOZIPS" != "x1" ]; then
    cp tools/tools/ccm-tools-$TOOLS_VERSION.zip $BUILD_HOME/dist/runtime/ZIPS
    cp tools/tools/ccm-tools-servlet-tomcat-$TOOLS_VERSION.zip $BUILD_HOME/dist/runtime/ZIPS
    cp tools/tools/ccm-tools-servlet-resin-$TOOLS_VERSION.zip $BUILD_HOME/dist/runtime/ZIPS
    cp tools/devel/ccm-devel-$DEVEL_VERSION.zip $BUILD_HOME/dist/runtime/ZIPS
    cp tools/scripts/ccm-scripts-$SCRIPTS_VERSION.zip $BUILD_HOME/dist/runtime/ZIPS
  fi

)

for DOC in $RUNTIME_DOCS
do
  (
    set -e
    cd doc/$DOC

    DOC_VERSION=`grep 'Version: ' *.spec | sed -e 's/Version://' | sed -e 's/ //g'`
    DOC_RELEASE=`grep 'Release: ' *.spec | sed -e 's/Release://' | sed -e 's/ //g'`
    DOC_NAME=`grep 'Name: ' *.spec | sed -e 's/Name://' | sed -e 's/ //g'`

    cp $DOC_NAME-$DOC_VERSION-$DOC_RELEASE.ps $BUILD_HOME/dist/runtime/doc
    cp $DOC_NAME-$DOC_VERSION-$DOC_RELEASE.pdf $BUILD_HOME/dist/runtime/doc
    cp $DOC_NAME-$DOC_VERSION.tbz $BUILD_HOME/dist/runtime/doc
    cp $DOC_NAME-$DOC_VERSION.zip $BUILD_HOME/dist/runtime/doc
    cp -a $DOC_NAME-$DOC_VERSION $BUILD_HOME/dist/runtime/doc
    cp $HOMERPMDIR/$DOC_NAME-$DOC_VERSION-$DOC_RELEASE.noarch.rpm $BUILD_HOME/dist/runtime/RPMS
  )
done

(
  set -e
  cd dist
  mkisofs -r -J -v -o ccm-runtime.iso runtime/
)


# Now the per-bundle distributions
for BUNDLE in $BUNDLES
do
  mkdir -p dist/bundles/$BUNDLE/RPMS
  mkdir -p dist/bundles/$BUNDLE/SRPMS
  mkdir -p dist/bundles/$BUNDLE/ZIPS
  mkdir -p dist/bundles/$BUNDLE/doc

  for i in `cat $BUNDLEDIR/bundles/$BUNDLE/cfg/applications.cfg | grep -v '#' | grep -v '^\s*$'`
  do
    echo "$BUNDLE -> $i"
    APP=`ls */*/src/$i.load | sed -e 's/\/.*$//'`

    set_application_properties $APP

    # Copy binary, documentation & source RPMs
    cp $HOMERPMDIR/$APP_NAME-$APP_VERSION-$RELEASE.noarch.rpm $BUILD_HOME/dist/bundles/$BUNDLE/RPMS
    cp $HOMERPMDIR/$APP_NAME-doc-$APP_VERSION-$RELEASE.noarch.rpm $BUILD_HOME/dist/bundles/$BUNDLE/RPMS
    cp $HOMESRPMDIR/$APP_NAME-$APP_VERSION-$RELEASE.src.rpm $BUILD_HOME/dist/bundles/$BUNDLE/SRPMS

    # Copy binary ZIP
    if [ "x$CCM_DIST_NOZIPS" != "x1" ]; then
      cp $BUILD_HOME/$APP/rollingbuild/dist/zips/$APP_NAME-$APP_VERSION-$RELEASE-bin.zip $BUILD_HOME/dist/bundles/$BUNDLE/ZIPS
    fi
  done

  (
    set -e
    cd $BUNDLEDIR/bundles/$BUNDLE
    check_svn_tagged
    BUNDLE_VERSION=`grep 'VERSION=' bundle.in | sed -e 's/VERSION=//'`
    BUNDLE_RELEASE="`grep 'RELEASE=' bundle.in | sed -e 's/RELEASE=//'`${SVN_REVISION}"
    cp $HOMERPMDIR/ccm-bundle-aplaws-plus-$BUNDLE-$BUNDLE_VERSION-$BUNDLE_RELEASE.noarch.rpm $BUILD_HOME/dist/bundles/$BUNDLE/RPMS
    cp $HOMESRPMDIR/ccm-bundle-aplaws-plus-$BUNDLE-$BUNDLE_VERSION-$BUNDLE_RELEASE.src.rpm $BUILD_HOME/dist/bundles/$BUNDLE/SRPMS
  )

  (
    set -e
    cd doc/aplaws/config-reference/$BUNDLE

    DOC_VERSION=`grep 'Version: ' *.spec | sed -e 's/Version://' | sed -e 's/ //g'`
    DOC_RELEASE=`grep 'Release: ' *.spec | sed -e 's/Release://' | sed -e 's/ //g'`
    DOC_NAME=`grep 'Name: ' *.spec | sed -e 's/Name://' | sed -e 's/ //g'`

    cp $DOC_NAME-$DOC_VERSION-$DOC_RELEASE.ps $BUILD_HOME/dist/bundles/$BUNDLE/doc
    cp $DOC_NAME-$DOC_VERSION-$DOC_RELEASE.pdf $BUILD_HOME/dist/bundles/$BUNDLE/doc
    cp $DOC_NAME-$DOC_VERSION.tbz $BUILD_HOME/dist/bundles/$BUNDLE/doc
    cp $DOC_NAME-$DOC_VERSION.zip $BUILD_HOME/dist/bundles/$BUNDLE/doc
    cp -a $DOC_NAME-$DOC_VERSION $BUILD_HOME/dist/bundles/$BUNDLE/doc
    cp $HOMERPMDIR/$DOC_NAME-$DOC_VERSION-$DOC_RELEASE.noarch.rpm $BUILD_HOME/dist/bundles/$BUNDLE/RPMS
  )
echo "APLAWS $APLAWS_DOCS"
  for DOC in $APLAWS_DOCS
  do
    (
      set -e
      cd doc/$DOC

      DOC_VERSION=`grep 'Version: ' *.spec | sed -e 's/Version://' | sed -e 's/ //g'`
      DOC_RELEASE=`grep 'Release: ' *.spec | sed -e 's/Release://' | sed -e 's/ //g'`
      DOC_NAME=`grep 'Name: ' *.spec | sed -e 's/Name://' | sed -e 's/ //g'`

      cp $DOC_NAME-$DOC_VERSION-$DOC_RELEASE.ps $BUILD_HOME/dist/bundles/$BUNDLE/doc
      cp $DOC_NAME-$DOC_VERSION-$DOC_RELEASE.pdf $BUILD_HOME/dist/bundles/$BUNDLE/doc
      cp $DOC_NAME-$DOC_VERSION.tbz $BUILD_HOME/dist/bundles/$BUNDLE/doc
      cp $DOC_NAME-$DOC_VERSION.zip $BUILD_HOME/dist/bundles/$BUNDLE/doc
      cp -a $DOC_NAME-$DOC_VERSION $BUILD_HOME/dist/bundles/$BUNDLE/doc
      cp $HOMERPMDIR/$DOC_NAME-$DOC_VERSION-$DOC_RELEASE.noarch.rpm $BUILD_HOME/dist/bundles/$BUNDLE/RPMS
    )
  done

  (
    cd dist/bundles
    mkisofs -r -J -v -o ccm-aplaws-$BUNDLE.iso $BUNDLE
  )
done

fi
# End of packaging

