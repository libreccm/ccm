#!/usr/bin/perl -w
#
# Extract version info from the project.xml and application.xml
#
# XXX, this can probably be done more 'cleanly' using XSLT
# (but only at the cost of a 10x increase in code size ;-)

use lib "$ENV{'CCM_TOOLS_HOME'}/lib";
use strict;
use CCM::Util;
use File::Spec;

local $/ = undef;
open (FILE, '<project.xml')
    or die "cannot open project.xml: $!";

my $OS = $^O;

my $project = <FILE>;
$project =~ s/<!--.*?-->//sg;
close FILE;

my $ccmVersion = '6.0';
if ($project =~ /<ccm:project .*?ccmVersion="(.*?)".*?ccm:project>/s) {
    $ccmVersion = $1;
}
unless ($ccmVersion eq '6.1') {
    if ($^O eq 'MSWin32') {
        print "set CCM_SCRIPTS_COMPAT=$ccmVersion\n";
    } else {
        print "CCM_SCRIPTS_COMPAT=$ccmVersion\n";
        print "export CCM_SCRIPTS_COMPAT\n";
    }
    my $file = File::Spec->catfile($ENV{'CCM_SCRIPTS_HOME'},'bin',"extract-version-$ccmVersion.pl");
    if (-f $file) {
        open (IN, $file) or die "could not open $file";
        my $contents = <IN>;
        close IN;
        eval $contents;
        exit;
    } else {
        die "$file does not exist";
    }
}

my ($name, $prettyName, $version, $release, $requires, @apps, $apps);

my $app = $ENV{'CCM_APP'};
if ($project =~ /<ccm:build>(.*?)<\/ccm:build>/s)  {
    my $info = $1;
    if (defined $app) {
	@apps = ($info =~ /<ccm:application\s+([^>]*?name="$app"[^>]*?)\s*>/g);
	if ( @apps == 0 ) {
	    die "could not find $app in project.xml";
	}
	if ( @apps > 1 ) {
	    die "multiple applications named '$app' in project.xml";
	}
    } else {
	@apps = ($info =~ /<ccm:application\s+[^>]*?name="([^\"]+)"[^>]*?>/g);
	if ( @apps == 0 ) {
	    die "CCM_APP not defined and no applications in project.xml";
	}
	if ( @apps > 1 ) {
	    die "CCM_APP not defined and multiple applications in project.xml";
	}
	$app = $apps[0];
    }
} else {
    die "no apps found in project.xml";
}
@apps = ();

my $info = $1;

open (FILE, "$app/application.xml")
    or die "cannot open $app/application.xml: $!";

my $appxml = <FILE>;
$appxml =~ s/<!--.*?-->//s;

close FILE;

if ($appxml =~ /<ccm:application\s*([^>]*?)\s*>/) {
    $info = $1;

    if ($info =~ /name="((?:\w|-)+)"/) {
        $name = $1;
    } else {
        die "cannot find //ccm:application/\@name attribute in $app/application.xml";
    }
    if ($info =~ /prettyName="((?:\w|-|\s)+)"/) {
        $prettyName = $1;
    } else {
        die "cannot find //ccm:application/\@prettyName attribute in $app/application.xml";
    }
    if ($info =~ /version="([^-\"]+)"/) {
        $version = $1;
    } else {
        die "cannot find //ccm:application/\@version attribute in $app/application.xml";
    }

    if ($info =~ /release="(\d+)"/) {
        $release = $1;
    } else {
        die "cannot find //ccm:application/\@release attribute in $app/application.xml";
    }

} else {
    die "cannot find <ccm:application> tag in $app/application.xml";
}

my $rpm_dependencies = "";
foreach (&dependencies($appxml =~ /<ccm:requires(.*?)>/g)) {
    $rpm_dependencies .= "Requires: $_\\n";
}
foreach (&dependencies($appxml =~ /<ccm:runRequires(.*?)>/g)) {
    $rpm_dependencies .= "Requires: $_\\n";
}
foreach (&dependencies($appxml =~ /<ccm:buildRequires(.*?)>/g)) {
    $rpm_dependencies .= "BuildRequires: $_\\n";
}

if ($project =~ /<ccm:prebuilt>(.*?)<\/ccm:prebuilt>/s)  {
    my $info = $1;

    @apps = ($info =~ /<ccm:application[^>]*? name=\"([^\"]+)\"[^>]*?>/g);
}
if (@apps > 0) {
    $requires .= " " . join (" ", @apps);
} else {
    $requires = "";
}

if ($project =~ /<ccm:build>(.*?)<\/ccm:build>/s)  {
    my $ccmbuild = $1;
    $apps = join (' ', ($ccmbuild =~ /<ccm:application\s+[^>]*?name="([^\"]+?)"[^>]*?\s*>/gs));
} else {
    $apps = "";
}

CCM::Util::printEnvVars(
                        'CCM_APP' => $app,
                        'CCM_APPS' => $apps,
                        'CCM_DESCRIPTION' => $name,
                        'CCM_PACKAGE' => $name,
                        'CCM_PRETTYNAME' => $prettyName,
                        'CCM_RELEASE' => $release,
                        'CCM_REQUIRES' => $requires,
                        'CCM_RPM_DEPENDENCIES' => $rpm_dependencies,
                        'CCM_SUMMARY' => $name,
                        'CCM_VERSION' => $version,
                        );

exit 0;

sub dependencies {
    my @deps_text = @_;
    my @deps_parsed = ();

    my %relations = ( "lt" => "<",
		      "le" => "<=",
		      "eq" => "=",
		      "ge" => ">=",
		      "gt" => ">" );

    foreach (@deps_text) {
	my $info = $_;
	my $name;
	my $version;
	my $relation;
	if ($info =~ /name="(.*?)"/) {
	    $name = $1;
	    if ($info =~ /version="(.*?)"/) {
		$version = $1;
		if ($info =~ /relation="(.*?)"/) {
		    if (defined $relations{$1}) {
			$relation = $relations{$1};
		    } else {
			die "unknown relation '$1'";
		    }
		}
		if ( defined $ENV{'CCM_BUILD_COUNTER'} && defined $ENV{'AUTO_BUILD_ROOT'} ) {
		    my $latest_file = (sort grep {/$name-([^-]+)\.jar$/} glob("$ENV{'AUTO_BUILD_ROOT'}/usr/share/java/$name-$version*.jar"))[0];
		    if (defined $latest_file && $latest_file =~ /$name-([^-]+)\.jar/) {
			$version = $1;
		    }
		}
	    }
	}
	my $text = "$name";
	if (defined $version) {
	    if (defined $relation) {
		$text .= " $relation $version";
	    } else {
		$text .= " = $version";
	    }
	}
	push @deps_parsed, $text;
    }
    return @deps_parsed;
}

