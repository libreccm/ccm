#!/usr/bin/perl -w
#
# Copyright (C) 2004 Red Hat Inc. All Rights Reserved.
#
# The contents of this file are subject to the CCM Public
# License (the "License"); you may not use this file except in
# compliance with the License. You may obtain a copy of
# the License at http://www.redhat.com/licenses/ccmpl.html
#
# Software distributed under the License is distributed on an "AS
# IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
# implied. See the License for the specific language governing
# rights and limitations under the License.
#
# Daniel Berrange <berrange@redhat.com>
# Dennis Gregorovic <dgregor@redhat.com>
#
# $Id: project.pl 288 2005-02-22 00:55:45Z sskracic $

BEGIN {
    if ( exists $ENV{'CCM_TOOLS_HOME'} && defined $ENV{'CCM_TOOLS_HOME'} ) {
        if ( -d "$ENV{'CCM_TOOLS_HOME'}/lib" ) {
            push @INC, "$ENV{'CCM_TOOLS_HOME'}/lib";
        } else {
            print "$ENV{'CCM_TOOLS_HOME'}/lib was not found\n";
            exit 1;
        }
    } else {
        print "The CCM_TOOLS_HOME environment variable must be set first.\n";
        exit 1;
    }
}

use strict;
use CCM::Runtime;
use CCM::Util;
use File::Spec;

my $runtime = CCM::Runtime->new();
my $server;
my $user;

if ($^O eq 'MSWin32') {
    if (@ARGV != 2) {
        CCM::Util::error("ccm-devel-profile <servername> <username>");
    }
    $server = shift;
    $user = shift;
} else {
    if (@ARGV != 1 && @ARGV != 2) {
        CCM::Util::error("ccm-devel-profile <servername>");
    }
    $server = shift;
    $user = shift;
    if (!defined $user) {
        if (exists $ENV{'USER'}) {
            $user = $ENV{'USER'};
        } elsif (exists $ENV{'LOGNAME'}) {
            $user = $ENV{'LOGNAME'};
        } else {
            CCM::Util::error("cannot determine username");
        }
    }
}

my $ccmdevelhome = CCM::Util::getRequiredEnvVariable('CCM_DEVEL_HOME');
my $ccmdevelroot = CCM::Util::getRequiredEnvVariable('CCM_DEVEL_ROOT');
my $ccmdevelconfdir = CCM::Util::getRequiredEnvVariable('CCM_DEVEL_CONF_DIR');
CCM::Util::error("CCM_DEVEL_ROOT is not a directory: '$ccmdevelroot'") unless -d $ccmdevelroot;

my $ccmdevhome = File::Spec->catdir($ccmdevelroot,'dev',$user,$server);
if (! -d $ccmdevhome) {
    CCM::Util::error("Cannot find project $server (directory $ccmdevhome does not exist)");
}

my $projectxml = File::Spec->catfile($ccmdevhome,'project.xml');
if (-f $projectxml) {
    my $found_61 = 0;
    open (IN, $projectxml)
        or CCM::Util::error("cannot open $projectxml: $!");
    while ($_ = <IN>) {
        if (m/ccmVersion=\"6\.1\"/) {
            $found_61 = 1;
            last;
        }
    }
    close IN;
    if (!$found_61) {
        my $file;
        if ($^O eq 'MSWin32') {
            $file = File::Spec->catfile($ccmdevelconfdir, 'project5x.cmd');
        } else {
            $file = File::Spec->catfile($ccmdevelconfdir, 'project5x.sh');
        }
        open (IN, $file) or die "could not open $file";
        while ($_ = <IN>) {
            print;
        }
        close IN;
        exit;
    }
}

my $ccmwebhome = File::Spec->catdir($ccmdevelroot,'web',$user,$server);
my $ccmhome = $ccmwebhome;


my $java_home = $runtime->getJavaHome();
my $path = defined $ENV{'PATH'} ? $ENV{'PATH'} : "";
$path = CCM::Util::catpath(File::Spec->catdir($java_home,'bin'),$path);
$path = CCM::Util::catpath(File::Spec->catdir($ccmdevelhome,'bin'),$path);

my $classpath = $runtime->getClassPath();
my $optit_home = $ENV{'OPTIT_HOME'};
if (defined $optit_home) {
    $classpath = CCM::Util::catpath(File::Spec->catfile($optit_home,'lib','optit.jar'),$classpath);
}
my $servletjar = $runtime->getServletJar("2.3");
if (defined $servletjar) {
    $classpath = CCM::Util::catpath($classpath, $servletjar);
}

&printEnvVars("CCM_HOME" => $ccmhome,
              "CCM_DEV_HOME" => $ccmdevhome,
              "CCM_WEB_HOME" => $ccmwebhome,
              "PATH" => $path,
              "JAVA_HOME" => $java_home,
              "CLASSPATH" => $classpath);

if ($^O eq 'MSWin32' && !defined $ENV{'CCM_ZIP_ROOT'}) {
    &printEnvVars("CCM_ZIP_ROOT" => File::Spec->catdir(File::Spec->rootdir(),'ccm'));
}

if ($^O eq 'MSWin32') {
    print "
doskey cddev=cd $ccmdevhome
doskey cdweb=cd $ccmwebhome
";
} else {
    print "
cddev() {
    cd \"$ccmdevhome\"
}
cdweb() {
    cd \"$ccmwebhome\"
}
tailccm() {
    (
        cdweb;
        tail -f logs/ccm.log
    )
}
taillogs() {
    (
        cdweb;
        tail -f logs/*.log
    )
}
";
}

sub printEnvVars {
    while (@_) {
        my $key = shift;
        my $value = shift;
        if ($^O eq 'MSWin32') {
            print "set $key=$value\n";
        } else {
            print "$key=$value\n";
            print "export $key\n";
        }
    }
}

