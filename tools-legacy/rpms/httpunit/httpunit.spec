%define	javadir	         %{_datadir}/java

Summary: HTTPUnit libraries
Name:    httpunit
Version: 1.5.4
Release: 1ccm
Group:   Applications/Internet/CCM
License: see description
Source: %{name}-%{version}.jar
Prefix:  /usr
Prefix:  /etc

BuildArchitectures: noarch
BuildRoot: %{_tmppath}/%{name}-root

%description
HTTPUnit libraries

HTTPUnitAPI
  (c) 2000-2003, Russell Gold
  MIT License

%prep

%build

%install
rm -rf $RPM_BUILD_ROOT

install -d -m 755 $RPM_BUILD_ROOT%{javadir}
install -m 644 %_sourcedir/%{name}-%{version}.jar $RPM_BUILD_ROOT%{javadir}/%{name}-%{version}.jar

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
