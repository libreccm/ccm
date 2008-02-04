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

echo "  Extracting application sources"
mkdir -p $CCM_SRC_DIR
cd $CCM_SRC_DIR
rm -rf $CCM_APP_NAME
unzip -q $CCM_DIST_ZIP_DIR/$CCM_PACKAGE_NAME.zip

cd $CCM_APP_NAME

echo "  Removing old install directory"
rm -rf $CCM_INST_WEBAPP_DIR
rm -rf $CCM_INST_DEVEL_DIR
mkdir -p $CCM_INST_WEBAPP_DIR
mkdir -p $CCM_INST_DEVEL_DIR

if [ "$CCM_TYPE" = "project" ]; then
  # Startup scripts and Config files
  mkdir -p $CCM_INST_WEBAPP_DIR/bin
  mkdir -p $CCM_INST_WEBAPP_DIR/conf
  mkdir -p $CCM_INST_WEBAPP_DIR/etc
  mkdir -p $CCM_INST_WEBAPP_DIR/runtime/bin
  mkdir -p $CCM_INST_WEBAPP_DIR/runtime/bin/resin
  mkdir -p $CCM_INST_WEBAPP_DIR/runtime/bin/tomcat4
  mkdir -p $CCM_INST_WEBAPP_DIR/runtime/conf
  mkdir -p $CCM_INST_WEBAPP_DIR/runtime/conf/resin
  mkdir -p $CCM_INST_WEBAPP_DIR/runtime/conf/tomcat4

  cp $CCM_SCRIPTS_HOME/pkg/bin/configure.cmd $CCM_INST_WEBAPP_DIR/runtime/configure.cmd
  cp $CCM_SCRIPTS_HOME/pkg/bin/configure.pl $CCM_INST_WEBAPP_DIR/runtime/configure.pl
  cp $CCM_SCRIPTS_HOME/pkg/bin/resin/* $CCM_INST_WEBAPP_DIR/runtime/bin/resin/
  cp $CCM_SCRIPTS_HOME/pkg/bin/tomcat4/* $CCM_INST_WEBAPP_DIR/runtime/bin/tomcat4/
  cp $CCM_SCRIPTS_HOME/pkg/conf/resin/* $CCM_INST_WEBAPP_DIR/runtime/conf/resin/
  cp $CCM_SCRIPTS_HOME/pkg/conf/tomcat4/* $CCM_INST_WEBAPP_DIR/runtime/conf/tomcat4/

  cat $CCM_SCRIPTS_HOME/pkg/conf/system.conf-resin.in | $CCM_SCRIPTS_HOME/bin/interpolate.sh > "$CCM_INST_WEBAPP_DIR/etc/$CCM_APP_NAME.cfg-resin"
  cat $CCM_SCRIPTS_HOME/pkg/conf/system.conf-tomcat4.in | $CCM_SCRIPTS_HOME/bin/interpolate.sh > "$CCM_INST_WEBAPP_DIR/etc/$CCM_APP_NAME.cfg-tomcat4"
  cat $CCM_SCRIPTS_HOME/pkg/conf/system.conf-tomcat4-win2k.in | $CCM_SCRIPTS_HOME/bin/interpolate.sh > "$CCM_INST_WEBAPP_DIR/etc/$CCM_APP_NAME.cfg-tomcat4-win2k"
fi;

# Build the beast
ccm-configure.sh
ant build
ant build-tests
ant javadoc

if [ $CCM_TYPE = 'application' ]; then
  # Deploy only the local applications & config
  ant make-config-local
  ant make-init-local

  ant -Ddeploy.init.dir=$CCM_INST_DEVEL_DIR/etc deploy-config-init-local
  ant -Ddeploy.web.dir=$CCM_INST_DEVEL_DIR/etc deploy-config-webxml-local
  ant -Ddeploy.web.dir=$CCM_INST_DEVEL_DIR/dist/WEB-INF deploy-config-web-orig-local
  ant -Ddeploy.dir=$CCM_INST_DEVEL_DIR/dist deploy-local
  ant -Ddeploy.src.dir=$CCM_INST_DEVEL_DIR/src deploy-src
  ant -Ddeploy.test.dir=$CCM_INST_DEVEL_DIR/test deploy-test

  ant -Ddeploy.dir=$CCM_INST_WEBAPP_DIR/dist deploy-config-webxml-local
  ant -Ddeploy.dir=$CCM_INST_WEBAPP_DIR/dist deploy-config-web-orig-local
  ant -Ddeploy.dir=$CCM_INST_WEBAPP_DIR/dist deploy-config-init-local
  ant -Ddeploy.dir=$CCM_INST_WEBAPP_DIR/dist deploy-local
else
  # Deploy full project to dev area
  ant make-config
  ant make-init

  ant -Ddeploy.init.dir=$CCM_INST_DEVEL_DIR/etc deploy-config-init
  ant -Ddeploy.web.dir=$CCM_INST_DEVEL_DIR/etc deploy-config-webxml
  ant -Ddeploy.web.dir=$CCM_INST_DEVEL_DIR/dist/WEB-INF deploy-config-web-orig
  ant -Ddeploy.dir=$CCM_INST_DEVEL_DIR/dist deploy-local
  ant -Ddeploy.dir=$CCM_INST_DEVEL_DIR/dist deploy-global
  ant -Ddeploy.src.dir=$CCM_INST_DEVEL_DIR/src deploy-src
  ant -Ddeploy.test.dir=$CCM_INST_DEVEL_DIR/test deploy-test

  ant -Ddeploy.dir=$CCM_INST_WEBAPP_DIR/dist deploy
fi

# These files will be generated after installation
rm -f $CCM_INST_WEBAPP_DIR/dist/WEB-INF/resources/enterprise.init
rm -f $CCM_INST_WEBAPP_DIR/dist/WEB-INF/web.xml

ant -Ddeploy.api.dir=$CCM_INST_API_DIR/api deploy-api-nodeps

mkdir -p $CCM_INST_WEBAPP_DIR/etc
mkdir -p $CCM_INST_WEBAPP_DIR/dist/WEB_INF/resources

cp config.vars $CCM_INST_WEBAPP_DIR/etc/$CCM_PACKAGE.config.vars

DIST_DIR=$CCM_INST_WEBAPP_DIR/dist
[ -f $DIST_DIR/WEB-INF/resources/enterprise.init.in ] &&
  mv $DIST_DIR/WEB-INF/resources/enterprise.init.in $DIST_DIR/WEB-INF/resources/$CCM_PACKAGE.enterprise.init.in

[ -f $DIST_DIR/WEB-INF/servlet-mappings.xml ] &&
  mv $DIST_DIR/WEB-INF/servlet-mappings.xml $DIST_DIR/WEB-INF/$CCM_PACKAGE.servlet-mappings.xml

[ -f $DIST_DIR/WEB-INF/servlet-declarations.xml ] &&
  mv $DIST_DIR/WEB-INF/servlet-declarations.xml $DIST_DIR/WEB-INF/$CCM_PACKAGE.servlet-declarations.xml

#########################################################################
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
