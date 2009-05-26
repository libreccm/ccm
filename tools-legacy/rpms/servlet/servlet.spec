%define	javadir	         %{_datadir}/java

Summary: Servlet 2.3 libraries
Name:    ccm-servlet
Version: 2.3
Release: 2
Group:   Applications/Internet/CCM
License: see description
Source: %{name}-%{version}.jar
Prefix:  /usr
Prefix:  /etc

BuildArchitectures: noarch
BuildRoot: %{_tmppath}/%{name}-root

Provides:  ccm-servlet23
Provides:  servletapi4 = 4.0.4

%description
Servlet 2.3 libraries

ServletAPI
  (c) Apache
  Apache Software License

%prep

%build

%install
rm -rf $RPM_BUILD_ROOT

install -d -m 755 $RPM_BUILD_ROOT%{javadir}
install -m 644 %_sourcedir/%{name}-%{version}.jar $RPM_BUILD_ROOT%{javadir}/%{name}-%{version}.jar

if [ -n "$AUTO_BUILD_ROOT" ]; then
  mkdir -p $AUTO_BUILD_ROOT%{javadir}
  cp -p %_sourcedir/%{name}-%{version}.jar $AUTO_BUILD_ROOT%{javadir}
  ln -s $AUTO_BUILD_ROOT%{javadir}/%{name}-%{version}.jar $AUTO_BUILD_ROOT%{javadir}/%{name}.jar
fi

%post
USR_DIR=$RPM_INSTALL_PREFIX0
# RPM bug workaround
if [ "x$USR_DIR" = "x" ]; then USR_DIR=/usr ; fi

JAVA_CONFIG_MAIN=`echo /usr/bin/javaconfig | sed -e "s,/usr,$USR_DIR,"`
JAVA_CONFIG_CCM=`echo /usr/share/ccm-tools/bin/javaconfig | sed -e "s,/usr,$USR_DIR,"`

if [ -z "$JAVA_CONFIG" ] && [ -x $JAVA_CONFIG_MAIN ]; then JAVA_CONFIG=$JAVA_CONFIG_MAIN; fi
if [ -z "$JAVA_CONFIG" ] && [ -x $JAVA_CONFIG_CCM ]; then JAVA_CONFIG=$JAVA_CONFIG_CCM; fi

if [ -n "$JAVA_CONFIG" ]
then
    $JAVA_CONFIG %{javadir}/%{name}.jar
else
    JAVA_DIR=`echo %{javadir} | sed -e "s,/usr,$USR_DIR,"`
    cd $JAVA_DIR
    ln -sf %{name}-%{version}.jar %{name}.jar
fi

%postun
USR_DIR=$RPM_INSTALL_PREFIX0
# RPM bug workaround
if [ "x$USR_DIR" = "x" ]; then USR_DIR=/usr ; fi

JAVA_CONFIG_MAIN=`echo /usr/bin/javaconfig | sed -e "s,/usr,$USR_DIR,"`
JAVA_CONFIG_CCM=`echo /usr/share/ccm-tools/bin/javaconfig | sed -e "s,/usr,$USR_DIR,"`

if [ -z "$JAVA_CONFIG" ] && [ -x $JAVA_CONFIG_MAIN ]; then JAVA_CONFIG=$JAVA_CONFIG_MAIN; fi
if [ -z "$JAVA_CONFIG" ] && [ -x $JAVA_CONFIG_CCM ]; then JAVA_CONFIG=$JAVA_CONFIG_CCM; fi

if [ -n "$JAVA_CONFIG" ]
then
    $JAVA_CONFIG %{javadir}/%{name}.jar
else
    JAVA_DIR=`echo %{javadir} | sed -e "s,/usr,$USR_DIR,"`
    cd $JAVA_DIR
    find . -name %{name}.jar -type l -maxdepth 1 -exec rm {} \;
fi

%clean
rm -rf $RPM_BUILD_ROOT

%files
%defattr(-,root,root)
%{javadir}/*

