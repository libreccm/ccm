#!/bin/sh

if [ "x$1" = "x" ] ; then
  echo "ccm-devel-profile.sh <servername>"
  return
else
  SERVER=$1
fi

umask 002

not_in_path () {
  # Check to see is the $value is already in the $path.
  # Returns 0 (true) if not in the path and 1 (false) if it is.
  #
  # Example:
  #   not_in_path $PATH, /usr/bin && PATH=$PATH:/usr/bin
  #
  path=$1
  value=$2

  if [ "x$path" = "x" ]; then
    return 0
  else
    if [ "$OSTYPE" = "solaris" ] && [ -x "/usr/xpg4/bin/grep" ]; then
        GREP="/usr/xpg4/bin/grep"
    else
        GREP="grep"
    fi

    match=`echo $path | $GREP -c -E "(^|:)$value(:|\$)"`
    if [ "$match" = "0" ]; then
        return 0
    else
        return 1
    fi
  fi
}


if [ "x$JAVA_HOME" = "x" ]; then
  JRE_DIRS="/opt/IBMJava2-131 /opt/IBMJava2-13 /usr/j2se /usr/java/jdk1.3.1 /usr/local/jdk1.3.1 /opt/jdk1.3.1 /usr/java /usr/local/java"
  for i in $JRE_DIRS
  do
    if [ -d $i ]; then
      JAVA_HOME=$i
      break;
    fi
  done

  if [ "x$JAVA_HOME" = "x" ]; then
    echo "Cannot find suitable JDK 1.3.x installation"
    echo "Looked in dirs:"
    echo $JRE_DIRS
    return
  fi

  unset JRE_DIRS

  not_in_path "$PATH", "$JAVA_HOME/bin" &&  PATH="$JAVA_HOME/bin:$PATH"

  export JAVA_HOME PATH

fi

if [ "x$PG_JDBC2_LIB" = "x" ]; then
  PG_JDBC2_LOCS="/usr/share/pgsql/java/rh-pgsql-jdbc2.jar /usr/share/pgsql/jdbc7.2dev-1.2.jar /usr/share/pgsql/pg73b1jdbc2.jar"

  for i in $PG_JDBC2_LOCS
  do
    if [ -f $i ]; then
      PG_JDBC2_LIB=$i
      break;
    fi
  done

  if [ "x$PG_JDBC2_LIB" = "x" ]; then
    echo "Warning: Cannot find postgres jdbc2 library"
    echo "Looked for: $PG_JDBC2_LOCS"
    echo "Point PG_JDBC2_LIB to correct location if using PostgreSQL"
  fi

  unset PG_JDBC2_LOCS

  export PG_JDBC2_LIB
fi

if [ "x$2" != "x" ]; then
  USERNAME=$2
else
  if [ "x$USER" != "x" ]; then
    USERNAME=$USER
  else
    if [ "x$LOGNAME" != "x" ]; then
      USERNAME=$LOGNAME
    else
      echo "cannot determine username"
      return
    fi
  fi
fi

if [ ! -d "$CCM_DEVEL_ROOT" ]; then
  echo "CCM_DEVEL_ROOT is not set or is not a directory: '${CCM_DEVEL_ROOT}'"
  return
fi

CCM_DEV_HOME="$CCM_DEVEL_ROOT/dev/$USERNAME/$SERVER"
CCM_WEB_HOME="$CCM_DEVEL_ROOT/web/$USERNAME/$SERVER"
CCM_HOME="$CCM_DEV_HOME"
CCM_SERVLET_CONTAINER=resin

if [ ! -d $CCM_DEV_HOME ]; then
  echo "Cannot find project $SERVER (directory $CCM_DEV_HOME does not exist)"
  return
fi

RESIN_HOME=""
if [ -d /opt/resin/latest ]; then
  RESIN_HOME=/opt/resin/latest
else
  RESIN_HOME=`/bin/ls -d /opt/resin/* | sort -r | head -1`
fi

TOMCAT_HOME=""
if [ -d /usr/share/tomcat ]; then
  TOMCAT_HOME=/usr/share/tomcat
fi

if [ "x$RESIN_HOME" = "x" ] && [ "x$TOMCAT_HOME" = "x" ] ; then
  echo "Cannot find Resin install in /opt/resin/2.1.* or a Tomcat install in /usr/share/tomcat"
  return
fi

not_in_path "$PATH", "$RESIN_HOME/bin" && PATH="$RESIN_HOME/bin:$PATH"
not_in_path "$PATH", "$TOMCAT_HOME/bin" && PATH="$TOMCAT_HOME/bin:$PATH"

export CCM_HOME CCM_DEV_HOME CCM_WEB_HOME RESIN_HOME TOMCAT_HOME
export PATH
export JAVA_LIB_HOME=/usr/share/java

not_in_path "$CLASSPATH", "$JAVA_LIB_HOME/junit.jar" && CLASSPATH="$JAVA_LIB_HOME/junit.jar:$CLASSPATH"
not_in_path "$CLASSPATH", "$JAVA_LIB_HOME/httpunit.jar" && CLASSPATH="$JAVA_LIB_HOME/httpunit.jar:$CLASSPATH"


JARS=""
JARS="$JARS $CCM_WEB_HOME/webapps/ccm/WEB-INF/lib/jaas.jar"
JARS="$JARS $CCM_WEB_HOME/webapps/ccm/WEB-INF/lib/jce.jar"
JARS="$JARS $CCM_WEB_HOME/webapps/ccm/WEB-INF/lib/sunjce_provider.jar"
JARS="$JARS $CCM_WEB_HOME/webapps/ccm/WEB-INF/lib/xerces.jar"
JARS="$JARS $CCM_WEB_HOME/webapps/ccm/WEB-INF/lib/xalan.jar"
if [ "x$ORACLE_HOME" != "x" ]; then
  JARS="$JARS $ORACLE_HOME/jdbc/lib/classes12.zip"
fi
if [ "x$PG_JDBC2_LIB" != "x" ]; then
  JARS="$JARS $PG_JDBC2_LIB"
fi
if [ "x$OPTIT_HOME" != "x" ]; then
  JARS="$JARS $OPTIT_HOME/lib/optit.jar"
fi

SERVER_CLASSPATH=

for i in $JARS ; do
  SERVER_CLASSPATH=$SERVER_CLASSPATH:$i
done
export SERVER_CLASSPATH

RESIN_ARGS="$RESIN_ARGS_CUSTOM -conf $CCM_WEB_HOME/conf/resin.conf"
RESIN_ARGS="$RESIN_ARGS -pid $CCM_WEB_HOME/conf/resin.pid"
RESIN_ARGS="$RESIN_ARGS -stdout $CCM_WEB_HOME/logs/resin-stdout.log"
RESIN_ARGS="$RESIN_ARGS -stderr $CCM_WEB_HOME/logs/resin-stderr.log"

TOMCAT_ARGS="$TOMCAT_ARGS_CUSTOM -config $CCM_WEB_HOME/conf/server.xml"

JAVA_ARGS="-J-classic -J-Djava.compiler=NONE -J-Xrunoii:filter=$OPTIT_HOME/filters/Resin.oif -J-Xbootclasspath/a:$OPTIT_HOME/lib/oibcp.jar -J-verbosegc"

function cddev() {
    cd "$CCM_DEV_HOME"
}

function cdweb() {
    cd "$CCM_WEB_HOME"
}

function tailccm() {
    (
        cdweb;
        tail -f logs/ccm.log
    )
}

function taillogs() {
    (
        cdweb;
        tail -f logs/*.log
    )
}

function ccm-inst() {
    (
        cddev;
        ant clean;
        ant deploy
    )
}

function ccm-start-resin() {
    (
        cd $RESIN_HOME/bin;
        verify_classpath && (
            CLASSPATH=$SERVER_CLASSPATH sh httpd.sh $* $RESIN_ARGS start
        );
    )
}

function ccm-start-tomcat4() {
    (
        verify_classpath && (
            "$JAVA_HOME/bin/java" $JAVA_OPTS $CATALINA_OPTS \
            -classpath "$SERVER_CLASSPATH:$TOMCAT_HOME/bin/bootstrap.jar:$JAVA_HOME/lib/tools.jar" \
            -Djava.endorsed.dirs="$TOMCAT_HOME/bin:$TOMCAT_HOME/common/lib" \
            -Dcatalina.base="$TOMCAT_HOME" \
            -Dcatalina.home="$TOMCAT_HOME" \
            org.apache.catalina.startup.Bootstrap "$@" $TOMCAT_ARGS start \
            >> "$CCM_WEB_HOME/logs/catalina.out" 2>&1 &
        );
    )
}

function ccm-start() {
    if [ "$CCM_SERVLET_CONTAINER" = "resin" ]; then
        ccm-start-resin;
    else
        if [ "$CCM_SERVLET_CONTAINER" = "tomcat4" ]; then
            ccm-start-tomcat4;
        else
            echo "CCM_SERVLET_CONTAINER must be either 'resin' or 'tomcat4'"
            return 1
        fi
    fi
}

function ccm-stop-resin() {
    (
        cd $RESIN_HOME/bin ;
        (
            CLASSPATH=$SERVER_CLASSPATH sh httpd.sh $RESIN_ARGS stop
        )
    )
}

function ccm-stop-tomcat4() {
    (
        verify_classpath && (
            "$JAVA_HOME/bin/java" $JAVA_OPTS $CATALINA_OPTS \
            -classpath "$SERVER_CLASSPATH:$TOMCAT_HOME/bin/bootstrap.jar:$JAVA_HOME/lib/tools.jar" \
            -Djava.endorsed.dirs="$TOMCAT_HOME/bin:$TOMCAT_HOME/common/lib" \
            -Dcatalina.base="$TOMCAT_HOME" \
            -Dcatalina.home="$TOMCAT_HOME" \
            org.apache.catalina.startup.Bootstrap "$@" $TOMCAT_ARGS stop \
            >> "$CCM_WEB_HOME/logs/catalina.out" 2>&1 &
        );
    )
}

function ccm-stop() {
    if [ "$CCM_SERVLET_CONTAINER" = "resin" ]; then
        ccm-stop-resin;
    else
        if [ "$CCM_SERVLET_CONTAINER" = "tomcat4" ]; then
            ccm-stop-tomcat4;
        else
            echo "CCM_SERVLET_CONTAINER must be either 'resin' or 'tomcat4'"
            return 1
        fi
    fi
}

function ccm-restart() {
    ccm-stop;
    sleep 10;
    ccm-start
}

function ccm-start-optit() {
    (
        cd $RESIN_HOME/bin ;
        verify_classpath && (
            CLASSPATH=$SERVER_CLASSPATH sh httpd.sh $JAVA_ARGS $RESIN_ARGS start
        )
    )
}

verify_classpath () {
  for i in $JARS ; do
    if [ ! -f $i ] ; then
      echo Cannot find $i
      return 1
    fi
  done
  return 0
}

unset not_in_path
