#!/bin/bash
set -e

#NAME
# deploy2jboss.sh - deploy CCM webapps under JBoss AS 4.0.5 4.2.0
#
#SYNOPSIS
# deploy2jboss.sh [--force] service_name [source_server_configuration]"
#
#DESCRIPTION
#   jbossas RPM/Red Hat Application Stack (JBOSS_HOME not set)
#       Creates new SysV service and JBoss AS server configuration based on
#       source JBoss server configuration ( 'default' if not specified ).
#   If JBOSS_HOME is set, normal JBoss installation is assumed
#   and shell startup script is created in $JBOSS_HOME/bin

# Requires: getopt sed jar java

usage() {
  echo "Usage: $0 [--force] service_name [source_server_configuration]"
  exit 1
}

OPTS=`getopt -o f -l force -- "$@"`
if (($?)); then usage; fi
eval set -- "$OPTS"
FORCE=0
while true; do
  case "$1" in
    -f|--force) FORCE=1; shift;;
    --) shift; break;;
    *) usage;;
  esac
done

if test -z "$1"; then
  usage
fi
JBOSSCONF=$1
SRCCONF=${2:-default}

if [[ -z "$JBOSS_HOME" ]]; then
  # JBoss paths for Red Hat Application Stack
  JBOSS_HOME=/var/lib/jbossas
  LOGDIR=/var/log/$JBOSSCONF
  CONFDIR=/etc/jbossas
  JBOSSUS=servlet
  JBOSSGR=servlet
  APPSTK=1
else 
  APPSTK=0
fi

SUPPORTED_JBASVERS="4.0.5 4.2.0"
JBASVER=`java -jar $JBOSS_HOME/bin/run.jar -V|head -1|cut -d' ' -f2`
UNSUPPORTED=1
for v in $SUPPORTED_JBASVERS; do
  if [[ $JBASVER =~ $v ]]; then
    UNSUPPORTED=0
    break
  fi
done

if (($UNSUPPORTED)); then
  echo "JBoss AS version $JBASVER not supported. Please use one of $SUPPORTED_JBASVERS"
  exit 1
fi
if test ! -d $JBOSS_HOME/server/$SRCCONF; then
  echo "server configuraton '$SRCCONF' doesn't exist"
  exit 1
fi

echo "Deploying CCM_HOME=$CCM_HOME to JBOSS_HOME=$JBOSS_HOME/server/$JBOSSCONF"
if (($APPSTK)); then
  echo "Application Stack detected"
fi

# jboss.server.home.url
cd $JBOSS_HOME/server
if test -d $JBOSSCONF; then
  echo -n "'$JBOSSCONF' already there, "
  if (($FORCE)); then
    echo -n "updating from '$SRCCONF'..."
    rm -rf $JBOSSCONF
  else
    echo "aborting, use --force"
    exit 1
  fi
else
  echo -n "creating '$JBOSSCONF' from '$SRCCONF'..."
fi

mkdir $JBOSSCONF
cd $JBOSSCONF
mkdir data
mkdir tmp
mkdir work
cp -rd --preserve=mode,timestamps $JBOSS_HOME/server/$SRCCONF/deploy .
cp -rd --preserve=mode,timestamps $JBOSS_HOME/server/$SRCCONF/lib .

# JBoss options run.conf or rpmized /etc/jbossas/...
cat > /tmp/jbossconf$$.sed <<EOF
/JBOSSCONF=/c\\
JBOSSCONF="$JBOSSCONF"
/JBOSSUS=/c\\
JBOSSUS="$JBOSSUS"
/JBOSSGR=/c\\
JBOSSGR="$JBOSSGR"
\$a\\
JAVA_OPTS="-Xms512m -Xmx512m -Dsun.rmi.dgc.client.gcInterval=3600000 -Dsun.rmi.dgc.server.gcInterval=3600000 -Duser.language=en -Duser.country=US -Dccm.home=$CCM_HOME"
EOF
# XXX -Duser.* -> LANG=en_US.UTF-8 workaround for ChainedResourceBundle
# Sun JDK JMX agent: -Dcom.sun.management.jmxremote
# JRockit Tools: -Xmanagement
# debugger: -Xdebug -Xrunjdwp:transport=dt_socket,address=localhost:8787,server=y,suspend=n

if (($APPSTK)); then
  # create new SysV service based on jbossas
  cd /etc/init.d/
  if (($FORCE)); then
    rm $JBOSSCONF
  fi
  if test ! -e $JBOSSCONF; then
    ln -s jbossas $JBOSSCONF
  fi
  cd /etc/sysconfig/
  if (($FORCE)); then
    rm $JBOSSCONF
  fi
  if test ! -e $JBOSSCONF; then
    cp jbossas $JBOSSCONF
    sed -i -f /tmp/jbossconf$$.sed $JBOSSCONF
  fi
  # jboss.server.log.dir
  cd $LOGDIR
  # re-create logdir on each deployment
  rm -rf $JBOSSCONF
  mkdir $JBOSSCONF
  chown $JBOSSUS:$JBOSSGR $JBOSSCONF

  cd $JBOSS_HOME/server/$JBOSSCONF
  chown $JBOSSUS:$JBOSSGR data
  chown $JBOSSUS:$JBOSSGR tmp
  chown $JBOSSUS:$JBOSSGR work

  if test ! -e log; then
    ln -s $LOGDIR/$JBOSSCONF log
  fi
  if test ! -e conf; then
    ln -s $CONFDIR/$JBOSSCONF conf
  fi

else
  cd $JBOSS_HOME/bin
  rm -f $JBOSSCONF.conf
  cp run.conf $JBOSSCONF.conf
  sed -i -f /tmp/jbossconf$$.sed $JBOSSCONF.conf
  cd $JBOSS_HOME/server/$JBOSSCONF
  cp -rd --preserve=mode,timestamps $JBOSS_HOME/server/$SRCCONF/conf .
  mkdir log
fi
rm /tmp/jbossconf$$.sed

# customize default JBoss services
if test -d deploy/jbossweb-tomcat55.sar/; then
  # JBAS4.0.5 w/ Tomcat5.5 in jbossweb-tomcat55.sar
  cd deploy/jbossweb-tomcat55.sar/
elif test -d deploy/jboss-web.deployer/; then
  # JBAS4.2.0 w/ Tomcat6.0 in jboss-web.deployer
  cd deploy/jboss-web.deployer/
else
  echo "Tomcat not found in $SRCCONF. Please re-install JBoss AS or choose different source server configuration."
  exit 1
fi

# rename Tomcat's default ROOT webapp
mv ROOT.war jbossweb.war
sed -i 's/href="\/status/href="status/' jbossweb.war/index.html

echo "done."

# WEBAPPS
# $JBOSSCONF/deploy/
cd ..
# TODO option to deploy webapps directly from ccm-* RPMs
#      ( /usr/share/java/webapps/ccm-*/ )
# NB: default p2fs destinations in com.arsdigita.cms.enterprise.init:
#     {ccm.home}data/p2fs and
#     {ccm.home}webapps/ROOT/packages/content-section/templates

# Use hostinit-ed CCM_HOME
for ccmwebapp in $CCM_HOME/webapps/ccm-*; do
  name=$(basename $ccmwebapp)
  ln -s $ccmwebapp $name.war
done
ln -s $CCM_HOME/webapps/ROOT ROOT.war

# JARs
function extract_pkgs() {
  pkgs=$(jar tf $1 |sed -rn '/^.+-INF/d;/\.class$/{s/\/[^\/]+\.class$//;p}' |sort -u)
}

# shared CL location
# $JBOSSCONF/lib/
cd ../lib
# development environment CCM_HOME
if test -d $CCM_HOME/webapps/WEB-INF/classes; then
  echo -n "creating JAR from $CCM_HOME/webapps/WEB-INF/classes..."
  jar cf ccm-ALL.jar -C $CCM_HOME/webapps/WEB-INF/classes .
  echo "done."
fi
deployedpkgs=""
echo -n "Analyzing JBossAS JARs: "
for deployedjar in *jar $JBOSS_HOME/lib/endorsed/*jar; do
  echo -n "$deployedjar "
  extract_pkgs $deployedjar
  deployedpkgs="$deployedpkgs $pkgs"
done
echo "done."

# JAR conflict resolution: JBoss JARs have precedence
for ccmjar in $CCM_HOME/webapps/WEB-INF/lib/*jar; do
  ccmjarbase=$(basename $ccmjar)
  if test ${ccmjarbase:0:3} = "ccm"; then
    ln -sf $ccmjar .
  else
    extract_pkgs $ccmjar
    conflicts=0
    for pkg in $pkgs; do
      if [[ $deployedpkgs =~ $pkg ]]; then
        echo -n "$pkg CONFLICT "
        conflicts=1
        break
      fi
    done
    if (($conflicts)); then
      echo "skipping $ccmjar"
    else
      ln -sf $ccmjar .
    fi
  fi
done

# Add JDBC JARs
# use ccm-java RPM configuration if present
if test -e /etc/sysconfig/ccm-java; then
  . /etc/sysconfig/ccm-java
  if [[ -r "$ORACLE" ]]; then
    ln -sf "$ORACLE" .
  else
    echo "Warning: Oracle JDBC JAR not found"
  fi
  if [[ -r "$POSTGRES" ]]; then
    ln -sf "$POSTGRES" .
  else
    echo "Warning: PostgreSQL JDBC JAR not found"
  fi
else
  # no ccm-java, assume CCM dev.env.
  # CLASSPATH which should include Oracle or PostgreSQL JDBC JAR
  # set by ccm-profile
  nojdbc=1
  IFS=:
  for c in $CLASSPATH; do
    if [[ $c =~ oracle ]] || [[ $c =~ postgresql ]]; then
      if [[ -r $c ]]; then
        ln -sf "$c" .
        nojdbc=0
      fi
    fi
  done
  IFS=
  if (($nojdbc)); then
    echo "Warning: JDBC JAR not found"
  fi
fi

if (($APPSTK)); then
  cd $CONFDIR
  # jboss.server.config.url
  if (($FORCE)); then
    rm -rf $JBOSSCONF
  fi
  if test ! -d $JBOSSCONF; then
    cp -rd --preserve=mode,timestamps $SRCCONF $JBOSSCONF
  fi
  cd $JBOSSCONF
else
  cd $JBOSS_HOME/server/$JBOSSCONF/conf
fi

if test -e jboss-log4j.xml; then
  # JBAS4.2
  LOG4J=jboss-log4j.xml
else
  LOG4J=log4j.xml
fi

# CCM log4j and XML/XSLT configuration is not used
if test ! -e $LOG4J.CCM; then
  # 1st time, modify JBoss log4j config to suppress verbose CCM logging

  sed -iCCM '/^.*Limit the org.apache category to INFO as its DEBUG is verbose/i\
   <category name="com.redhat.persistence">\
       <priority value="WARN"/>\
   </category>\
   <category name="com.arsdigita.runtime.Startup">\
       <priority value="INFO"/>\
   </category>\
   <category name="com.arsdigita.packaging.Loader">\
       <priority value="INFO"/>\
   </category>\
   <category name="com.arsdigita">\
       <priority value="WARN"/>\
   </category>\
   <category name="org.jboss">\
       <priority value="INFO"/>\
   </category>\
   <!-- Redirect/LoginSignal -->\
   <category name="org.apache.catalina.core.ContainerBase">\
      <priority value="FATAL"/>\
   </category>\
' $LOG4J
  sed -i '/<root>/a\
       <priority value="INFO" />' $LOG4J
fi

echo -n "To start APLAWS use: "
if (($APPSTK)); then
   echo "service $JBOSSCONF start"
else
  startup_script="$JBOSS_HOME/bin/$JBOSSCONF.sh"
  echo "RUN_CONF=$JBOSS_HOME/bin/$JBOSSCONF.conf $JBOSS_HOME/bin/run.sh -c $JBOSSCONF" > "$startup_script"
  echo "$startup_script"
fi

# example cmdln with ccm start:
#   -Dccm.home=/usr/share/ccm -Dccm.conf=/usr/share/ccm/conf/registry -Dcom.arsdigita.util.Assert.enabled=true -Djavax.xml.parsers.DocumentBuilderFactory=org.apache.xerces.jaxp.DocumentBuilderFactoryImpl -Djavax.xml.parsers.SAXParserFactory=org.apache.xerces.jaxp.SAXParserFactoryImpl -Djavax.xml.transform.TransformerFactory=com.icl.saxon.TransformerFactoryImpl -Djava.protocol.handler.pkgs=com.arsdigita.util.protocol -Djava.ext.dirs=/opt/java/jdk/jre/lib/ext:/opt/java/jdk/lib/ext:/usr/share/ccm/webapps/WEB-INF/system -Dlog4j.configuration=file:///usr/share/ccm/conf/log4j.properties -Xms1024m -Xmx1024m -Dcom.sun.management.jmxremote

# NOTE about resource: URL protocol
# JBoss has org.jboss.net.protocol.resource but it doesn't ignore
# leading slashes like c.a.util.protocol.resource does.
# Default parameter values are modified for JBoss handler on trunk r1346
# so for JBoss webapps/WEB-INF/system/ccm-core-*-system.jar is not needed,
# JBoss handler takes precedence anyway
# XXX java.ext.dirs=...WEB-INF/system
# XXX java.protocol.handler.pkgs=com.arsdigita.util.protocol

