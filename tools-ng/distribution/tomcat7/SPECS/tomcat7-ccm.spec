# Copyright (c) 2000-2012, JPackage Project
# All rights reserved.
#
# Redistribution and use in source and binary forms, with or without
# modification, are permitted provided that the following conditions
# are met:
#
# 1. Redistributions of source code must retain the above copyright
#    notice, this list of conditions and the following disclaimer.
# 2. Redistributions in binary form must reproduce the above copyright
#    notice, this list of conditions and the following disclaimer in the
#    documentation and/or other materials provided with the
#    distribution.
# 3. Neither the name of the JPackage Project nor the names of its
#    contributors may be used to endorse or promote products derived
#    from this software without specific prior written permission.
#
# THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
# "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
# LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
# A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
# OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
# SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
# LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
# DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
# THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
# (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
# OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
#
###%define rel 4%{?dist}

###%define section free

%define major_version 7
%define minor_version 0
%define micro_version 37
%define packdname apache-tomcat-%{major_version}.%{minor_version}.%{micro_version}
%define jspspec 2.2
%define servletspec 3.0
%define elspec 2.2
%define tcuid 91
#--%global arch noarch

%define dist .TCbin

# FHS 2.3 compliant tree structure - http://www.pathname.com/fhs/2.3/
%define homedir %{_datadir}/%{name}
%define basedir %{_var}/lib/%{name}

%define appdir %{basedir}/webapps
%define bindir %{_datadir}/%{name}/bin
%define confdir %{_sysconfdir}/%{name}
%define libdir %{_javadir}/%{name}
%define logdir %{_var}/log/%{name}
%define cachedir %{_var}/cache/%{name}
%define tempdir %{cachedir}/temp
%define workdir %{cachedir}/work
%define _initrddir %{_sysconfdir}/init.d

# Avoid RPM 4.2+'s internal dep generator, it may produce bogus
# Provides/Requires here.
%define _use_internal_dependency_generator 0

# This prevents aggressive stripping.
%define debug_package %{nil}


Name: tomcat7
Epoch: 0
Version: %{major_version}.%{minor_version}.%{micro_version}
Release: 1ccm%{dist}
Summary: Apache Servlet/JSP Engine, RI for Servlet %{servletspec}/JSP %{jspspec} API

License: ASL 2.0
URL: http://tomcat.apache.org/
Source0: apache-tomcat-%{major_version}.%{minor_version}.%{micro_version}.tar.gz
Source1: %{name}-%{major_version}.%{minor_version}.conf
Source2: %{name}-%{major_version}.%{minor_version}.init
Source3: %{name}-%{major_version}.%{minor_version}.sysconfig
Source4: %{name}-%{major_version}.%{minor_version}.wrapper
Source5: %{name}-%{major_version}.%{minor_version}.logrotate
Source6: %{name}-%{major_version}.%{minor_version}-digest.script
Source7: %{name}-%{major_version}.%{minor_version}-tool-wrapper.script
Source8: %{name}-%{major_version}.%{minor_version}.starter

###Source10 http://apache.mirror.clusters.cc/tomcat/tomcat-7/v%{version}/bin/extras/tomcat-juli.jar

BuildRoot: %{_tmppath}/%{name}-%{version}-%{release}-root
BuildArch: noarch

Requires(pre): shadow-utils
Requires(pre): shadow-utils
Requires: java >= 0:1.6.0
Requires: procps
Requires(post):  /sbin/chkconfig
Requires(preun): /sbin/chkconfig
Requires(post):  /lib/lsb/init-functions
Requires(preun): /lib/lsb/init-functions
Requires(post):    jpackage-utils >= 0:1.7.5
Requires(postun):  jpackage-utils >= 0:1.7.5

%description
Tomcat is the servlet container that is used in the official Reference
Implementation for the Java Servlet and JavaServer Pages technologies.
The Java Servlet and JavaServer Pages specifications are developed by
Sun under the Java Community Process.

Tomcat is developed in an open and participatory environment and
released under the Apache Software License version 2.0. Tomcat is intended
to be a collaboration of the best-of-breed developers from around the world.

This package distributes the original, unmodified binary Apache Tomcat 
distribution in a FHS compliant structure and includes configuration and 
helper files to start Tomcat during systems init process.

# --------------------------------------------------------------------------

%package admin-webapps
Summary: The host-manager and manager web applications for Apache Tomcat
Requires: %{name} = %{epoch}:%{version}-%{release}

%description admin-webapps
The host-manager and manager web applications for Apache Tomcat.

This package distributes the original, unmodified binary Apache Tomcat 
distribution in a FHS compliant structure and includes configuration and 
helper files to start Tomcat during systems init process.

# --------------------------------------------------------------------------

%package docs-webapp
Group: System Environment/Applications
Summary: The docs web application for Apache Tomcat
Requires: %{name} = %{epoch}:%{version}-%{release}

%description docs-webapp
The docs web application for Apache Tomcat.

This package distributes the original, unmodified binary Apache Tomcat 
distribution in a FHS compliant structure and includes configuration and 
helper files to start Tomcat during systems init process.

# --------------------------------------------------------------------------

%package webapps
##Group: System Environment/Applications
Summary: The ROOT and examples web applications for Apache Tomcat
Requires: %{name} = %{epoch}:%{version}-%{release}

%description webapps
The ROOT and examples web applications for Apache Tomcat.

This package distributes the original, unmodified binary Apache Tomcat 
distribution in a FHS compliant structure and includes configuration and 
helper files to start Tomcat during systems init process.

# --------------------------------------------------------------------------



%prep
###%setup -q -c

%setup -n %{packdname}
chmod -R go=u-w *
chmod -R u+w *

# remove pre-built binaries and windows files
find . -type f \( \
    -name "*.bat" -o \
    -name "*.gz" -o \
    -name "*.war" -o \
    -name "*.zip" -o \
    -name "Thumbs.db" \) | xargs -t %{__rm}

# excluded in order to provice the managers class files
#     -name "*.class" -o \

### currently nothing to patch
### %patch0 -p0




%build
# Nope.


%install
%{__rm} -rf $RPM_BUILD_ROOT

# build initial path structure
%{__install} -d -m 0755 ${RPM_BUILD_ROOT}%{_bindir}
%{__install} -d -m 0755 ${RPM_BUILD_ROOT}%{_sbindir}
%{__install} -d -m 0755 ${RPM_BUILD_ROOT}%{_initrddir}
%{__install} -d -m 0755 ${RPM_BUILD_ROOT}%{_sysconfdir}/logrotate.d
%{__install} -d -m 0755 ${RPM_BUILD_ROOT}%{_sysconfdir}/sysconfig

%{__install} -d -m 0755 ${RPM_BUILD_ROOT}%{homedir}
%{__install} -d -m 0755 ${RPM_BUILD_ROOT}%{bindir}
%{__install} -d -m 0755 ${RPM_BUILD_ROOT}%{confdir}
%{__install} -d -m 0755 ${RPM_BUILD_ROOT}%{confdir}/Catalina/localhost
%{__install} -d -m 0755 ${RPM_BUILD_ROOT}%{libdir}
%{__install} -d -m 0755 ${RPM_BUILD_ROOT}%{logdir}
/bin/touch ${RPM_BUILD_ROOT}%{logdir}/catalina.out
%{__install} -d -m 0755 ${RPM_BUILD_ROOT}%{tempdir}
%{__install} -d -m 0755 ${RPM_BUILD_ROOT}%{workdir}

%{__install} -d -m 0755 ${RPM_BUILD_ROOT}%{webappdir}
%{__install} -d -m 0755 ${RPM_BUILD_ROOT}%{appdir}

# move things into place
# tomcat stuff first
# pushd %{packdname}
    %{__cp} -a $RPM_BUILD_DIR/%{packdname}/LICENSE         ${RPM_BUILD_ROOT}/%{homedir}/
    %{__cp} -a $RPM_BUILD_DIR/%{packdname}/NOTICE          ${RPM_BUILD_ROOT}/%{homedir}/
    %{__cp} -a $RPM_BUILD_DIR/%{packdname}/RELEASE*        ${RPM_BUILD_ROOT}/%{homedir}/
    %{__cp} -a $RPM_BUILD_DIR/%{packdname}/bin/*.{jar,xml} ${RPM_BUILD_ROOT}%{bindir}
    %{__cp} -a $RPM_BUILD_DIR/%{packdname}/conf/*.{policy,properties,xml} ${RPM_BUILD_ROOT}%{confdir}
    %{__cp} -a $RPM_BUILD_DIR/%{packdname}/lib/*.jar ${RPM_BUILD_ROOT}%{libdir}
    %{__cp} -a $RPM_BUILD_DIR/%{packdname}/webapps/* ${RPM_BUILD_ROOT}%{webappdir}
# popd


# supporting files for configuration, init etc.
%{__sed} -e "s|\@\@\@TCHOME\@\@\@|%{homedir}|g" \
   -e "s|\@\@\@TCTEMP\@\@\@|%{tempdir}|g" \
   -e "s|\@\@\@LIBDIR\@\@\@|%{_libdir}|g" %{SOURCE1} \
    > ${RPM_BUILD_ROOT}%{confdir}/%{name}.conf
%{__sed} -e "s|\@\@\@TCHOME\@\@\@|%{homedir}|g" \
   -e "s|\@\@\@TCTEMP\@\@\@|%{tempdir}|g" \
   -e "s|\@\@\@LIBDIR\@\@\@|%{_libdir}|g" %{SOURCE3} \
    > ${RPM_BUILD_ROOT}%{_sysconfdir}/sysconfig/%{name}
%{__install} -m 0644 %{SOURCE2} \
    ${RPM_BUILD_ROOT}%{initrddir}/%{name}
%{__install} -m 0644 %{SOURCE4} \
    ${RPM_BUILD_ROOT}%{_sbindir}/%{name}
%{__ln_s} %{name} ${RPM_BUILD_ROOT}%{_sbindir}/d%{name}
%{__sed} -e "s|\@\@\@TCLOG\@\@\@|%{logdir}|g" %{SOURCE5} \
    > ${RPM_BUILD_ROOT}%{_sysconfdir}/logrotate.d/%{name}
%{__sed} -e "s|\@\@\@TCHOME\@\@\@|%{homedir}|g" \
   -e "s|\@\@\@TCTEMP\@\@\@|%{tempdir}|g" \
   -e "s|\@\@\@LIBDIR\@\@\@|%{_libdir}|g" %{SOURCE6} \
    > ${RPM_BUILD_ROOT}%{_bindir}/%{name}-digest
%{__sed} -e "s|\@\@\@TCHOME\@\@\@|%{homedir}|g" \
   -e "s|\@\@\@TCTEMP\@\@\@|%{tempdir}|g" \
   -e "s|\@\@\@LIBDIR\@\@\@|%{_libdir}|g" %{SOURCE7} \
    > ${RPM_BUILD_ROOT}%{_bindir}/%{name}-tool-wrapper

# symlink to the FHS locations where we've installed things
pushd ${RPM_BUILD_ROOT}%{homedir}
    %{__ln_s} %{confdir} conf
    %{__ln_s} %{logdir} logs
    %{__ln_s} %{tempdir} temp
    %{__ln_s} %{workdir} work
    %{__ln_s} %{webappdir} webapps
popd



%pre
# add the tomcat user and group
%{_sbindir}/groupadd -g %{tcuid} -r tomcat 2>/dev/null || :
%{_sbindir}/useradd -c "Apache Tomcat" -u %{tcuid} -g tomcat \
    -s /bin/nologin -r -d %{homedir} tomcat 2>/dev/null || :
## In case user tomcat exists already from a previous tomcat6 install, fix the homedir.
#%{_sbindir}/usermod -d %{homedir} tomcat 2>/dev/null || :


%post
# install but don't activate
/sbin/chkconfig --add %{name}


%preun
# clean tempdir and workdir on removal or upgrade
%{__rm} -rf %{workdir} %{tempdir}
if [ "$1" = "0" ]; then
    %{initrddir}/%{name} stop >/dev/null 2>&1
    /sbin/chkconfig --del %{name}
fi


# base package
%files
%defattr(0664,root,tomcat,0775)
# % %  doc {LICENSE,NOTICE,RELEASE*}

# attribute flags for rpm support and config files
%attr(0755,root,root) %{_bindir}/%{name}-digest
%attr(0755,root,root) %{_bindir}/%{name}-tool-wrapper
%attr(0755,root,root) %{_sbindir}/d%{name}
%attr(0755,root,root) %{_sbindir}/%{name}
%attr(0755,root,root) %{initrddir}/%{name}
%attr(0644,root,root) %config(noreplace) %{_sysconfdir}/logrotate.d/%{name}
%config(noreplace) %{_sysconfdir}/sysconfig/%{name}

# attribute flags for basic tomcat directories and config files
%attr(0765,root,tomcat) %dir %{basedir}
%attr(0775,root,tomcat) %dir %{webappdir}
%attr(0775,root,tomcat) %dir %{cachedir}
%attr(0775,root,tomcat) %dir %{tempdir}
%attr(0775,root,tomcat) %dir %{workdir}
# (from sl spec)
%attr(0775,root,tomcat) %dir %{logdir}
%attr(0644,tomcat,tomcat) %{logdir}/catalina.out

%attr(0775,root,tomcat) %dir %{confdir}
%attr(0775,root,tomcat) %dir %{confdir}/Catalina
%attr(0775,root,tomcat) %dir %{confdir}/Catalina/localhost
%attr(0664,tomcat,tomcat) %config(noreplace) %{confdir}/%{name}.conf
%attr(0664,tomcat,tomcat) %config(noreplace) %{confdir}/*.policy
%attr(0664,tomcat,tomcat) %config(noreplace) %{confdir}/*.properties
%attr(0664,tomcat,tomcat) %config(noreplace) %{confdir}/context.xml
%attr(0664,tomcat,tomcat) %config(noreplace) %{confdir}/server.xml
%attr(0664,tomcat,tomcat) %config(noreplace) %{confdir}/tomcat-users.xml
%attr(0666,tomcat,tomcat) %config(noreplace) %{confdir}/web.xml

%dir %{homedir}
# specify files to include in package for installation
%{homedir}/LICENSE
%{homedir}/NOTICE
%{homedir}/RELEASE*
%{homedir}/bin
%{homedir}/lib
%{homedir}/temp
%{homedir}/webapps
%{homedir}/work
%{homedir}/logs
%{homedir}/conf

%files admin-webapps
%defattr(0664,root,tomcat,0775)
%{webappdir}/host-manager
%{webappdir}/manager

%files docs-webapp
%defattr(-,root,root,-)
%{webappdir}/docs

%files webapps
%defattr(0664,root,tomcat,0775)
%{webappdir}/ROOT
%{webappdir}/examples

%changelog
* Sun Jun 21 2014 Peter Boy <pboy@zes.uni-bremen.de> 0:7.0.53-1
- Initial release, heavily borrowed from jpp, Scientific Linux, 
  and CentOS






