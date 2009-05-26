#!/usr/bin/perl -w
#
# Extract version info from the project.xml and application.xml
#
# XXX, this can probably be done more 'cleanly' using XSLT
# (but only at the cost of a 10x increase in code size ;-)

use strict;

local $/ = undef;
open (FILE, '<project.xml')
    or die "cannot open project.xml: $!";

my $OS = $^O;

my $project = <FILE>;

close FILE;

my ($name, $prettyName, $type, $version, $release, $extends, $extendsVersion, $requires, $projectname, @apps);


if ($project =~ /<ccm:project\s*([^>]*?)\s*>/)  {
    my $info = $1;

    if ($info =~ /name="((?:\w|-)+)"/) {
        $name = $1;
    } else {
        die "cannot find //ccm:project/\@name attribute in project.xml";
    }

    if ($info =~ /prettyName="((?:\w|-)+)"/) {
        $prettyName = $1;
    } else {
        $prettyName = $name;
    }

    if ($info =~ /type="((?:\w|-)+)"/) {
        $type = $1;
    } else {
        die "cannot find //ccm:project/\@type attribute in project.xml";
    }

    if ($info =~ /extends="((?:\w|-)+)"/) {
        $extends = $1;
    } 

    if ($info =~ /extendsVersion="(.+?)"/) {
        $extendsVersion = $1;
    } 

    if ( $type ne "project" && !defined($extends) ) {
        die "The project must either be of type 'project' or extend a 'project'.";
    } else {
        $projectname = $type eq "project" ? $name : $extends;
    }

    if ( $info =~ /release="([^\"]+)"/) {
        $release = $1;
    }

    if ($info =~ /versionFrom="((?:\w|-)+)"/) {
        my $versionFrom = $1;
        open (FILE, "$versionFrom/application.xml")
            or die "cannot open $versionFrom/application.xml: $!";

        my $app = <FILE>;

        close FILE;

        if ($app =~ /<ccm:application\s*([^>]*?)\s*>/) {
            $info = $1;

            if ($info =~ /version="(\d+\.\d+\.\d+)"/) {
                $version = $1;
            } else {
                die "cannot find //ccm:application/\@version attribute in $versionFrom/application.xml";
            }

            if ( ( ! defined $release ) && $info =~ /release="([^\"]+)"/) {
                $release = $1;
            } else {
                die "cannot find //ccm:application/\@release attribute in $versionFrom/application.xml";
            }

        } else {
            die "cannot find <ccm:application> tag in $versionFrom/application.xml";
        }
    } else {
        if ($info =~ /\Wversion="([a-zA-Z0-9\._]+)"/) {
            $version = $1;
        } else {
            die "cannot find //ccm:project/\@version attribute in project.xml";
        }
        if ( ! defined $release ){
            die "cannot find //ccm:project/\@release attribute in project.xml";
        }
    }

} else {
    die "cannot find <ccm:project> tag in project.xml";
}

if ($project =~ /<ccm:prebuilt>(.*?)<\/ccm:prebuilt>/s)  {
    my $info = $1;

    $info =~ s/<!--.*?-->//g;
    @apps = ($info =~ /<ccm:application[^>]*? name=\"([^\"]+)\"[^>]*?>/g);
}

if (defined $extends) {
    $requires = "$extends = $extendsVersion";
    if (@apps) {
        $requires .= " " . join (" ", @apps);
    }
} else {
    $requires="";
}

my $apps;
if ($project =~ /<ccm:build>(.*?)<\/ccm:build>/s)  {
    my $ccmbuild = $1;
    $apps = join (' ', ($ccmbuild =~ /<ccm:application\s+[^>]*?name="([^\"]+?)"[^>]*?\s*>/gs));
} else {
    $apps = "";
}

if ($OS eq 'MSWin32') {
    print "
set CCM_PACKAGE=$name
set CCM_PRETTYNAME=$prettyName
set CCM_TYPE=$type
set CCM_VERSION=$version
set CCM_RELEASE=$release
set CCM_REQUIRES=$requires
set CCM_PROJECT=$projectname
set CCM_APPS=$apps
";
} else {
    print "
CCM_PACKAGE=$name
CCM_PRETTYNAME=$prettyName
CCM_TYPE=$type
CCM_VERSION=$version
CCM_RELEASE=$release
CCM_REQUIRES='$requires'
CCM_PROJECT='$projectname'
CCM_APPS='$apps'
export CCM_PACKAGE CCM_PRETTYNAME CCM_TYPE CCM_VERSION CCM_RELEASE CCM_REQUIRES CCM_PROJECT CCM_APPS
";
}

exit 0;
