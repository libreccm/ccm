# Copyright (c) 2000-2009, JPackage Project
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
#    distribution.//
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

# APLAWS+ details
%global bundle compl
%global major_version 2
%global minor_version 0
%global micro_version 0
%global revision beta1.r2321
%define dist .openccm

%global apuid 291
%global apusr aplaws
%global arch noarch

# Servlet Container Details
%global sc_name tomcat6
%global sc_major_version 6
%global sc_minor_version 0
%global sc_micro_version 35
%global packdname apache-tomcat-%{sc_major_version}.%{sc_minor_version}.%{sc_micro_version}

%global jspspec 2.1
%global servletspec 2.5

# FHS 2.3 compliant tree structure - http://www.pathname.com/fhs/2.3/
%global homedir %{_datadir}/%{name}
# FHS:  /var/lib dir: state saving data, no user configuration
#       /srv     dir: site-specific data served by the system,
#                     read-only data, writable data, scripts
# %global basedir %{_var}/lib/%{name}
%global basedir /srv/%{name}

%global bindir %{homedir}/bin
%global libdir %{homedir}/lib
%global confdir %{_sysconfdir}/%{name}
%global logdir %{_var}/log/%{name}
%global cachedir %{_var}/cache/%{name}
%global tempdir   %{cachedir}/temp
%global workdir   %{cachedir}/work
%global webappdir %{basedir}/webapps
%global addondir  %{basedir}/ccm-addons
%global initrddir %{_sysconfdir}/init.d

%global ccmdir /ROOT

# Avoid RPM 4.2+'s internal dep generator, it may produce bogus
# Provides/Requires here.
%define _use_internal_dependency_generator 0

# This prevents aggressive stripping.
%define debug_package %{nil}

Name:          aplaws
Epoch:         0
Version:       %{major_version}.%{minor_version}.%{micro_version}
Release:       %{bundle}.%{revision}
Summary:       APLAWS+ Collaboration and Content Management System bundled with Tomcat6 servlet container.

License:       LGPL 
URL:           http://apalaws.org/
# Tomcat stuff
Source0:       apache-tomcat-%{sc_major_version}.%{sc_minor_version}.%{sc_micro_version}.tar.gz
Source1:       %{name}-%{major_version}.%{minor_version}.conf
Source2:       %{name}-%{major_version}.%{minor_version}.init
Source3:       %{name}-%{major_version}.%{minor_version}.sysconfig
Source4:       %{name}-%{major_version}.%{minor_version}.wrapper
Source5:       %{name}-%{major_version}.%{minor_version}.logrotate
#  Source6:       %{sc_name}-%{sc_major_version}.%{sc_minor_version}-digest.script
#  Source7:       %{sc_name}-%{sc_major_version}.%{sc_minor_version}-tool-wrapper.script
# APLAWS+ stuff
Source8:       %{name}-%{major_version}-%{minor_version}-%{micro_version}-%{bundle}.war
Source9:       postgresql-jdbc-8.4.701.jar
Source10:      ojdbc14.jar
Source11:      %{name}-addon-%{major_version}-%{minor_version}-%{micro_version}-%{bundle}.zip

Patch0:        %{sc_name}-%{sc_major_version}.%{sc_minor_version}-tomcat-users-webapp.patch
Patch1:        %{sc_name}-%{sc_major_version}.%{sc_minor_version}-server-xml.patch

BuildArch:     noarch


# BuildRequires: ant
# BuildRequires: ant-nodeps
BuildRequires: findutils
# BuildRequires: java-1.6.0-devel
BuildRequires: jpackage-utils >= 0:1.7.0

Requires:         java-1.6.0
Requires:         procps
Requires(pre):    shadow-utils
Requires(pre):    shadow-utils
Requires(post):   chkconfig
Requires(preun):  chkconfig
Requires(post):   redhat-lsb
Requires(preun):  redhat-lsb
Requires(post):   jpackage-utils
Requires(postun): jpackage-utils


%description
APLAWS is content and collaboration management web application.

This package distributes APLAWS with the original, unmodified binary 
Apache Tomcat distribution integrated in a FHS compliant structure and 
includes configuration and helper files to start Tomcat during systems 
init process.

# --------------------------------------------------------------------------

%package addons
Summary: Provides additional packages not included in the APLAWS ${bundle} bundle.
Requires: %{name} = %{epoch}:%{version}-%{release}

%description addons
Provides additional packages not included in the APLAWS ${bundle} bundle.

# --------------------------------------------------------------------------



%prep

%setup  -n %{packdname}  -q
chmod -R go=u-w *
chmod -R u+w *

# remove pre-built binaries and windows files
find . -type f \( \
    -name "*.bat" -o \
    -name "*.gz" -o \
    -name "*.war" -o \
    -name "*.zip" -o \
    -name "Thumbs.db" \) | xargs -t %{__rm}

# excluded in order to provide the managers class files
#     -name "*.class" -o \

%patch0 -p0
%patch1 -p0

rm -rf webapps/docs 
rm -rf webapps/examples 
rm -rf webapps/ROOT/* 
%{__cp} %{SOURCE9}  lib/
%{__cp} %{SOURCE10} lib/

# unpack ccm application
cd webapps/ROOT/
unzip %{SOURCE8}
cd ../..

mkdir addons
cd addons/
unzip %{SOURCE11}



%build

# Nope.


%install
rm -rf $RPM_BUILD_ROOT

# build initial path structure
%{__install} -d -m 0755 ${RPM_BUILD_ROOT}%{_bindir}
%{__install} -d -m 0755 ${RPM_BUILD_ROOT}%{_sbindir}
%{__install} -d -m 0755 ${RPM_BUILD_ROOT}%{initrddir}
%{__install} -d -m 0755 ${RPM_BUILD_ROOT}%{_sysconfdir}/logrotate.d
%{__install} -d -m 0755 ${RPM_BUILD_ROOT}%{_sysconfdir}/sysconfig
%{__install} -d -m 0775 ${RPM_BUILD_ROOT}%{homedir}
%{__install} -d -m 0755 ${RPM_BUILD_ROOT}%{bindir}
%{__install} -d -m 0755 ${RPM_BUILD_ROOT}%{libdir}
%{__install} -d -m 0775 ${RPM_BUILD_ROOT}%{confdir}
%{__install} -d -m 0775 ${RPM_BUILD_ROOT}%{confdir}/Catalina/localhost
%{__install} -d -m 2755 ${RPM_BUILD_ROOT}%{webappdir}
%{__install} -d -m 2755 ${RPM_BUILD_ROOT}%{addondir}
%{__install} -d -m 0775 ${RPM_BUILD_ROOT}%{logdir}
/bin/touch ${RPM_BUILD_ROOT}%{logdir}/catalina.out
%{__install} -d -m 2775 ${RPM_BUILD_ROOT}%{tempdir}
%{__install} -d -m 2775 ${RPM_BUILD_ROOT}%{workdir}

# move things into place
# tomcat stuff first
# pushd %--{packdname}
    %{__cp} -a $RPM_BUILD_DIR/%{packdname}/LICENSE         ${RPM_BUILD_ROOT}/%{homedir}/
    %{__cp} -a $RPM_BUILD_DIR/%{packdname}/NOTICE          ${RPM_BUILD_ROOT}/%{homedir}/
    %{__cp} -a $RPM_BUILD_DIR/%{packdname}/RELEASE*        ${RPM_BUILD_ROOT}/%{homedir}/
    %{__cp} -a $RPM_BUILD_DIR/%{packdname}/bin/*.{jar,xml} ${RPM_BUILD_ROOT}%{bindir}
    %{__cp} -a $RPM_BUILD_DIR/%{packdname}/conf/*.{policy,properties,xml} ${RPM_BUILD_ROOT}%{confdir}
    %{__cp} -a $RPM_BUILD_DIR/%{packdname}/lib/*.jar ${RPM_BUILD_ROOT}%{libdir}
    %{__cp} -a $RPM_BUILD_DIR/%{packdname}/webapps/* ${RPM_BUILD_ROOT}%{webappdir}
    %{__cp} -a $RPM_BUILD_DIR/%{packdname}/addons/*  ${RPM_BUILD_ROOT}%{addondir}
# popd


# supporting files for configuration, init etc.
%{__sed} -e "s|\@\@\@TCHOME\@\@\@|%{homedir}|g"  \
         -e "s|\@\@\@TCTEMP\@\@\@|%{tempdir}|g"  \
         -e "s|\@\@\@LIBDIR\@\@\@|%{_libdir}|g"  \
         -e "s|\@\@\@TCPID\@\@\@|%{name}|g"  \
         -e "s|\@\@\@TCUSER\@\@\@|%{apusr}|g" %{SOURCE1} \
    > ${RPM_BUILD_ROOT}%{confdir}/%{name}.conf

%{__sed} -e "s|\@\@\@TCHOME\@\@\@|%{homedir}|g" \
         -e "s|\@\@\@TCTEMP\@\@\@|%{tempdir}|g" \
         -e "s|\@\@\@LIBDIR\@\@\@|%{_libdir}|g" \
         -e "s|\@\@\@TCPID\@\@\@|%{name}|g"     \
         -e "s|\@\@\@TCUSER\@\@\@|%{apusr}|g" %{SOURCE3} \
    > ${RPM_BUILD_ROOT}%{_sysconfdir}/sysconfig/%{name}

%{__install} -m 0644 %{SOURCE2} \
    ${RPM_BUILD_ROOT}%{initrddir}/%{name}

%{__install} -m 0644 %{SOURCE4} \
    ${RPM_BUILD_ROOT}%{_sbindir}/%{name}
%{__ln_s} %{name} ${RPM_BUILD_ROOT}%{_sbindir}/d%{name}

%{__sed} -e "s|\@\@\@TCLOG\@\@\@|%{logdir}|g" %{SOURCE5} \
    > ${RPM_BUILD_ROOT}%{_sysconfdir}/logrotate.d/%{name}

# %{__sed} -e "s|\@\@\@TCHOME\@\@\@|%{homedir}|g" \
#    -e "s|\@\@\@TCTEMP\@\@\@|%{tempdir}|g" \
#    -e "s|\@\@\@LIBDIR\@\@\@|%{_libdir}|g" %{SOURCE6} \
#     > ${RPM_BUILD_ROOT}%{_bindir}/%{name}-digest

# %{__sed} -e "s|\@\@\@TCHOME\@\@\@|%{homedir}|g" \
#    -e "s|\@\@\@TCTEMP\@\@\@|%{tempdir}|g" \
#    -e "s|\@\@\@LIBDIR\@\@\@|%{_libdir}|g" %{SOURCE7} \
#     > ${RPM_BUILD_ROOT}%{_bindir}/%{name}-tool-wrapper

# symlink to the FHS locations where we've installed things
pushd ${RPM_BUILD_ROOT}%{homedir}
    %{__ln_s} %{confdir} conf
    %{__ln_s} %{logdir} logs
    %{__ln_s} %{tempdir} temp
    %{__ln_s} %{workdir} work
    %{__ln_s} %{webappdir} webapps
popd



%pre
# add the aplaws user and group
groupadd -g %{apuid} -r %{apusr}-admin 2>/dev/null || :
useradd -c "APLAWS+" -u %{apuid} -g nobody -N   \
    -s /bin/bash -r -d %{homedir} %{apusr} 2>/dev/null || :


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
%defattr(0664,root,root,0775)
# % %  doc {LICENSE,NOTICE,RELEASE*}

# attribute flags for rpm support and config files
# %attr(0755,root,root) %{_bindir}/%{name}-digest
# %attr(0755,root,root) %{_bindir}/%{name}-tool-wrapper
%attr(0755,root,root) %{_sbindir}/d%{name}
%attr(0755,root,root) %{_sbindir}/%{name}
%attr(0755,root,root) %{initrddir}/%{name}
%attr(0644,root,root) %config(noreplace) %{_sysconfdir}/logrotate.d/%{name}
%config(noreplace) %{_sysconfdir}/sysconfig/%{name}

# attribute flags for basic tomcat directories and config files
%attr(2775,aplaws,aplaws-admin) %dir %{basedir}
%attr(7775,aplaws,aplaws-admin) %dir %{webappdir}
%attr(0775,aplaws,aplaws-admin) %dir %{cachedir}
%attr(0775,aplaws,aplaws-admin) %dir %{tempdir}
%attr(0775,aplaws,aplaws-admin) %dir %{workdir}
# (from sl spec)
%attr(0775,aplaws,aplaws-admin) %dir %{logdir}
%attr(0644,aplaws,aplaws-admin) %{logdir}/catalina.out

%attr(0775,root,aplaws-admin) %dir %{confdir}
%attr(0775,root,aplaws-admin) %dir %{confdir}/Catalina
%attr(0775,root,aplaws-admin) %dir %{confdir}/Catalina/localhost
%attr(0664,aplaws,aplaws-admin) %config(noreplace) %{confdir}/%{name}.conf
%attr(0664,aplaws,aplaws-admin) %config(noreplace) %{confdir}/*.policy
%attr(0664,aplaws,aplaws-admin) %config(noreplace) %{confdir}/*.properties
%attr(0664,aplaws,aplaws-admin) %config(noreplace) %{confdir}/context.xml
%attr(0664,aplaws,aplaws-admin) %config(noreplace) %{confdir}/server.xml
%attr(0664,aplaws,aplaws-admin) %config(noreplace) %{confdir}/tomcat-users.xml
%attr(0666,aplaws,aplaws-admin) %config(noreplace) %{confdir}/web.xml

%dir %{homedir}
# specify files to include in package for installation
%defattr(0664,aplaws,aplaws-admin,0775)
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

#  %files admin-webapps
%defattr(0664,aplaws,aplaws-admin,0775)
%{webappdir}/host-manager
%{webappdir}/manager

#%files webapps
%defattr(0664,aplaws,aplaws-admin,0775)
%{webappdir}/ROOT

%attr(0774,aplaws,aplaws-admin) %{webappdir}/ROOT/WEB-INF/bin/ccm
%attr(0774,aplaws,aplaws-admin) %{webappdir}/ROOT/WEB-INF/bin/ccm-hostinit
%attr(0774,aplaws,aplaws-admin) %{webappdir}/ROOT/WEB-INF/bin/ccm-run
%attr(0774,aplaws,aplaws-admin) %{webappdir}/ROOT/WEB-INF/bin/libexec/ant/bin/ant
%attr(0774,aplaws,aplaws-admin) %{webappdir}/ROOT/WEB-INF/bin/libexec/ant/bin/antRun

%files addons
%defattr(0664,aplaws,aplaws-admin,0775)
%{addondir}



%changelog
* Thu May 17 2012 Peter Boy <pboy@zes.uni-bremen.de> 0:2.0.0-1
- Initial release, heavily borrowed from jpp, Scientific Linux, 
  and especially Fedora 16
