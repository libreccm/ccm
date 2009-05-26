#!/bin/sh
#
# Make all the zip binary distribution

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


echo "Creating binary zip file"

if [ ! -d $CCM_INST_DIR ]; then
  mkdir $CCM_INST_DIR
fi

if [ ! -d $CCM_SRC_DIR ]; then
  mkdir $CCM_SRC_DIR
fi


cd $CCM_SRC_DIR

# Extract the sources
echo "  Removing old source code directory"
rm -rf $CCM_APP_NAME
echo "  Extracting application sources"
unzip $CCM_DIST_ZIP_DIR/$CCM_PACKAGE_NAME.zip > /dev/null

cd $CCM_APP_NAME

echo "  Removing old install directory"
rm -rf $CCM_INST_WEBAPP_DIR
rm -rf $CCM_INST_DEVEL_DIR
mkdir -p $CCM_INST_WEBAPP_DIR
mkdir -p $CCM_INST_DEVEL_DIR

# Startup scripts
mkdir -p $CCM_INST_WEBAPP_DIR/bin
mkdir -p $CCM_INST_WEBAPP_DIR/runtime/bin
mkdir -p $CCM_INST_WEBAPP_DIR/runtime/bin/resin
mkdir -p $CCM_INST_WEBAPP_DIR/runtime/bin/tomcat4

cp $CCM_SCRIPTS_HOME/pkg/bin/configure.pl $CCM_INST_WEBAPP_DIR/runtime/configure.pl
cp $CCM_SCRIPTS_HOME/pkg/bin/configure.cmd $CCM_INST_WEBAPP_DIR/runtime/configure.cmd
cp $CCM_SCRIPTS_HOME/pkg/bin/resin/* $CCM_INST_WEBAPP_DIR/runtime/bin/resin/
cp $CCM_SCRIPTS_HOME/pkg/bin/tomcat4/* $CCM_INST_WEBAPP_DIR/runtime/bin/tomcat4/

# Config files
mkdir -p $CCM_INST_WEBAPP_DIR/etc
mkdir -p $CCM_INST_WEBAPP_DIR/conf
mkdir -p $CCM_INST_WEBAPP_DIR/runtime/conf
mkdir -p $CCM_INST_WEBAPP_DIR/runtime/conf/resin
mkdir -p $CCM_INST_WEBAPP_DIR/runtime/conf/tomcat4
cp $CCM_SCRIPTS_HOME/pkg/conf/resin/* $CCM_INST_WEBAPP_DIR/runtime/conf/resin/
cp $CCM_SCRIPTS_HOME/pkg/conf/tomcat4/* $CCM_INST_WEBAPP_DIR/runtime/conf/tomcat4/

ccm-configure.sh

ant -Dproject.name=$CCM_PACKAGE make-config
ant make-init
ant make-init-local

cat $CCM_SCRIPTS_HOME/pkg/conf/system.conf-resin.in | $CCM_SCRIPTS_HOME/bin/interpolate.sh > $CCM_INST_WEBAPP_DIR/etc/system.conf-resin.in
cat config.vars >> $CCM_INST_WEBAPP_DIR/etc/system.conf-resin.in

cat $CCM_SCRIPTS_HOME/pkg/conf/system.conf-tomcat4.in | $CCM_SCRIPTS_HOME/bin/interpolate.sh > $CCM_INST_WEBAPP_DIR/etc/system.conf-tomcat4.in
cat config.vars >> $CCM_INST_WEBAPP_DIR/etc/system.conf-tomcat4.in

cat $CCM_SCRIPTS_HOME/pkg/conf/system.conf-tomcat4-win2k.in | $CCM_SCRIPTS_HOME/bin/interpolate.sh > $CCM_INST_WEBAPP_DIR/etc/system.conf-tomcat4-win2k.in
cat config.vars | perl -p -e 's/\n/\r\n/g;' >> $CCM_INST_WEBAPP_DIR/etc/system.conf-tomcat4-win2k.in

# Build the beast
echo "  Building sources"
ant build

# Generate the API docs
echo "  Generating API docs"
ant javadoc

# Run some tests
# XXX Nope, not yet we don't - dpb 17/9/2001
#ant runtests

###########################################################################
# Deploy webapp
echo "  Installing webapp distribution"
ant -Dj2ee.webapp.dir=$CCM_INST_WEBAPP_DIR/dist deploy

echo "  Installing webapp config files"
ant -Ddeploy.init.dir=$CCM_INST_WEBAPP_DIR/etc deploy-config-init

echo "  Creating webapp distribution"
(
  rm -f $CCM_DIST_ZIP_DIR/$CCM_PACKAGE_NAME-bin.zip
  cd $CCM_INST_WEBAPP_DIRNAME
  zip -r $CCM_DIST_ZIP_DIR/$CCM_PACKAGE_NAME-bin.zip $CCM_INST_WEBAPP_BASENAME > /dev/null
  echo "$CCM_DIST_ZIP_DIR/$CCM_PACKAGE_NAME-bin.zip"
  if [ ! "x$CCM_DIST_NOTARS" = "x1" ]; then
    rm -f $CCM_DIST_TAR_DIR/$CCM_PACKAGE_NAME-bin.tar.zip
    tar zcvf $CCM_DIST_TAR_DIR/$CCM_PACKAGE_NAME-bin.tar.gz $CCM_INST_WEBAPP_BASENAME > /dev/null
    echo "$CCM_DIST_TAR_DIR/$CCM_PACKAGE_NAME-bin.tar.gz"
  fi
)

#########################################################################
# Deploy developer

rm -rf $CCM_INST_DEVEL_DIR/bin
rm -rf $CCM_INST_DEVEL_DIR/conf
rm -rf $CCM_INST_DEVEL_DIR/etc
rm -rf $CCM_INST_DEVEL_DIR/runtime

if [ "$CCM_TYPE" = 'application' ]; then
  echo "  Installing application developer distribution"
  ant -Dj2ee.webapp.dir=$CCM_INST_DEVEL_DIR/dist deploy-local
  echo "  Installing application developer config"
  ant -Ddeploy.init.dir=$CCM_INST_DEVEL_DIR/etc deploy-config-init-local
else
  echo "  Installing project developer distribution"
  ant -Dj2ee.webapp.dir=$CCM_INST_DEVEL_DIR/dist deploy
  echo "  Installing project developer config"
  ant -Ddeploy.init.dir=$CCM_INST_DEVEL_DIR/etc deploy-config-init
fi

echo "  Installing developer source code"
ant -Ddeploy.src.dir=$CCM_INST_DEVEL_DIR/src deploy-src

echo "  Installing developer test code"
ant -Ddeploy.test.dir=$CCM_INST_DEVEL_DIR/test deploy-test

echo "  Creating developer distribution"
(
  rm -f $CCM_DIST_ZIP_DIR/$CCM_PACKAGE_NAME-devel.zip
  cd $CCM_INST_DEVEL_DIRNAME
  zip -r $CCM_DIST_ZIP_DIR/$CCM_PACKAGE_NAME-devel.zip $CCM_INST_DEVEL_BASENAME > /dev/null
  echo "$CCM_DIST_ZIP_DIR/$CCM_PACKAGE_NAME-devel.zip"
  if [ ! "x$CCM_DIST_NOTARS" = "x1" ]; then
    rm -f $CCM_DIST_TAR_DIR/$CCM_PACKAGE_NAME-devel.tar.zip
    tar zcvf $CCM_DIST_TAR_DIR/$CCM_PACKAGE_NAME-devel.tar.gz $CCM_INST_DEVEL_BASENAME > /dev/null
    echo "$CCM_DIST_TAR_DIR/$CCM_PACKAGE_NAME-devel.tar.gz"
  fi;
)

#########################################################################
# Deploy API docs

echo "  Installing API documentation"
ant -Ddeploy.api.dir=$CCM_INST_API_DIR/api deploy-api-nodeps

echo "  Creating API doc distribution"
(
  rm -f $CCM_DIST_ZIP_DIR/$CCM_PACKAGE_NAME-api.zip
  cd $CCM_INST_API_DIRNAME
  zip -r $CCM_DIST_ZIP_DIR/$CCM_PACKAGE_NAME-api.zip $CCM_INST_API_BASENAME/api > /dev/null
  echo "$CCM_DIST_ZIP_DIR/$CCM_PACKAGE_NAME-api.zip"
  if [ ! "x$CCM_DIST_NOTARS" = "x1" ]; then
    rm -f $CCM_DIST_TAR_DIR/$CCM_PACKAGE_NAME-api.tar.zip
    tar zcvf $CCM_DIST_TAR_DIR/$CCM_PACKAGE_NAME-api.tar.gz $CCM_INST_API_BASENAME/api > /dev/null
    echo "$CCM_DIST_TAR_DIR/$CCM_PACKAGE_NAME-api.tar.gz"
  fi
)

exit 0;

# End of file
