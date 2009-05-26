#!/usr/bin/perl -w

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
use CCM::Interpolate('interpolate_file');
use CCM::Runtime;
use CCM::Util;
use File::Find;
use File::Path;
use File::Spec;
use Getopt::Long;
use Sys::Hostname;

my $OS = $^O;

my $installed = 0;
my $verbose = 0;
my $dummy = 0;
my $type = "application";
my $extends;
my $extends_version;
my $appname;
my $webapponly = 0;
my $hostname = hostname();
my $ccmversion = '6.1';
my $runtime = CCM::Runtime->new();
my $nextport;
my $newdevdir = 0;

GetOptions('verbose+' => \$verbose,
           'dummy' => \$dummy,
           'type=s' => \$type,
           'extends=s' => \$extends,
           'appname=s' => \$appname,
           'webapponly' => \$webapponly,
           'version=s' => \$ccmversion);

my $ccmrootdir = defined $ENV{'CCM_ZIP_ROOT'} ? $ENV{'CCM_ZIP_ROOT'} :
    $OS eq 'MSWin32' ? File::Spec->catdir(File::Spec->rootdir(),'ccm') :
    File::Spec->rootdir();

my $templatedir = defined $ENV{'CCM_TEMPLATE_DIR'} ? $ENV{'CCM_TEMPLATE_DIR'} : File::Spec->catdir($ccmrootdir,'usr','share','ccm-devel','template');
my $ccmdevelroot = defined $ENV{'CCM_DEVEL_ROOT'} ? $ENV{'CCM_DEVEL_ROOT'} : File::Spec->catdir($ccmrootdir,'var','ccm-devel');
my $ccmtoolshome = defined $ENV{'CCM_TOOLS_HOME'} ? $ENV{'CCM_TOOLS_HOME'} : File::Spec->catdir($ccmrootdir,'usr','share','tools');
my $etcdir = File::Spec->catdir($ccmrootdir,'etc');
my $scratchdir = File::Spec->catdir($ccmrootdir,'var','tmp');
my $devdir = File::Spec->catdir($ccmdevelroot,'dev');
my $webdir = File::Spec->catdir($ccmdevelroot,'web');
my $portalloc = File::Spec->catfile($ccmrootdir,'var','lib','ccm-devel','portalloc.txt');
my $resinconf5x = File::Spec->catfile($etcdir,'ccm-devel','resin.conf.in');
my $resinconf = File::Spec->catfile($ccmtoolshome,'server','resin','conf','resin-devel.conf.in');
my $tomcatconf5x = File::Spec->catfile($etcdir,'ccm-devel','server.xml.in');
my $tomcatconf = File::Spec->catfile($ccmtoolshome,'server','tomcat','conf','server-devel.xml.in');
my $nocvsroot = File::Spec->catdir($ccmrootdir,'temp','no-cvsroot');
my $envvars = File::Spec->catfile($etcdir,'ccm-devel','envvars.in');

my $ccmdevelhome = defined $ENV{'CCM_DEVEL_HOME'} ? $ENV{'CCM_DEVEL_HOME'} :
    defined $ENV{'CCM_CONFIG_HOME'} ? $ENV{'CCM_CONFIG_HOME'} :
    File::Spec->catdir($ccmrootdir,'usr','share','ccm-devel');

my $JAVA_CMD = $runtime->getJavaCommand();

if ($OS eq 'MSWin32') {
    if ($#ARGV != 1) {
        &show_help();
    }
} else {
    if ($#ARGV != 0 && $#ARGV != 1) {
        &show_help();
    }
}

my $project = $ARGV[0];
my $user = undef;

if ($#ARGV == 1) {
    $user = $ARGV[1];
} elsif (defined $ENV{'USER'}) {
    $user = $ENV{'USER'};
} elsif (defined $ENV{'LOGNAME'}) {
    $user = $ENV{'LOGNAME'};
} else {
    &myerror("cannot determine username, please specify as the last argument to the command");
}

my $doimport = exists $ENV{'CCM_DEVEL_CVSROOT'} ? ($webapponly ? 0 : 1) : 0;
my $cvsroot;
if ($doimport) {
    $cvsroot = $ENV{'CCM_DEVEL_CVSROOT'};
    # untaint $cvsroot
    if ( $cvsroot =~ m!^([\w\.:/-]+)$! ) {
        # $cvsroot only contains alphanumeric, ':', '/', and '.'
        $cvsroot = $1;
    } else {
        &myerror("cvsroot contains invalid characters - $cvsroot");
    }
} else {
    $cvsroot = $nocvsroot;
}


if ($project =~ /^((?:\w|-)+)$/) {
    # $project only contains 'word' characters and is untainted
    $project = $1;
} else {
    &myerror("The project name can only contain letters, numbers, hyphens and underscores");
}

if ($user =~ /^((?:\w|-)+)$/) {
    # $user only contains 'word' characters and is untainted
    $user = $1;
} else {
    &myerror("The user name can only contain letters, numbers, hyphens and underscores");
}
my $logfile = File::Spec->catfile($scratchdir, "ccm-devel-create-$project-$user.log");

# untaint $ENV{'PATH'} so that we can run 'system'
# $ENV{'PATH'} = '/bin:/usr/bin';

if (defined $extends && !$webapponly) {
    my @dirs = glob ($ENV{CCM_DIST_HOME} . "/projects/$extends-*");
    if ($#dirs == -1) {
        &myerror("cannot find parent project '$extends' in $ENV{CCM_DIST_HOME}/projects/");
    } else {
        my ($version, $major, $minor, $revision);
        foreach (@dirs) {
            if (m|(\d+)\.(\d+)\.(\d+)|) {
                my $newer = 0;
                if (!defined $version) {
                    $newer = 1;
                } else {
                    if ($1 > $major) {
                        $newer = 1;
                    } elsif ($1 == $major) {
                        if ($2 > $minor) {
                            $newer = 1;
                        } elsif ($2 == $minor) {
                            if ($3 > $revision) {
                                $newer = 1;
                            }
                        }
                    }
                }
                if ($newer) {
                    ($major, $minor, $revision) = ($1, $2, $3);
                    $version = "$major.$minor.$revision";
                }
            }
        }
        $extends_version = $version;
    }
}

$appname = $project unless $appname;

if (! -d File::Spec->catdir($devdir,$user)) {
    &myerror("'" . File::Spec->catdir($devdir,$user) . "' does not exist.  The system administrator needs to" .
             " create your account by running 'ccm-devel-user $user'");
}

&init();

my $globalvars = {
    'appname' => $appname,
    'buildOrder' => ($ccmversion eq '5x' || $ccmversion eq '6.0') ? " buildOrder=\"1\"" : "",
    'ccm-devel-home' => (($OS eq "MSWin32") ? "/" : "") . &all_forward_slashes($ccmdevelhome),
    'ccm-version' => $ccmversion,
    'deploy-dir' => &all_forward_slashes("$webdir/$user/$project"),
    'dev-dir' => &all_forward_slashes("$devdir/$user/$project"),
    'extends' => defined $extends ? "extends=\"$extends\" extendsVersion=\"$extends_version\"" : "",
    'hostname' => $hostname,
    'http-port' => $nextport,
    'log-dir' => File::Spec->canonpath("$webdir/$user/$project/logs"),
    'package' => $project,
    'port' => $nextport,
    'project' => $project,
    'root-dir' => File::Spec->canonpath("$webdir/$user/$project"),
    'servlet-engine' => 'servlet23',
    'shutdown-port' => $nextport + 1,
    'type' => $type,
    'user' => $user,
    'versionfromattr' => ($ccmversion eq '5x' || $ccmversion eq '6.0') ? "versionFrom=\"$project\"" : "",
    'webapp-dir' => File::Spec->canonpath("$webdir/$user/$project/webapps/ccm"),
    'webapp-root' => File::Spec->canonpath("$webdir/$user/$project/webapps"),
    'work-dir' => File::Spec->canonpath("$webdir/$user/$project/tmp")
    };

&sanity_check();
if (!$webapponly) {
    if ($doimport) {
        if (!$installed) {
            &clone_template();
            &import_app();
        }
        &checkout_app();
    } else {
        &clone_template();
    }
}
&make_webapp();
&notify_user();
&cleanup();

exit 0;

sub show_help {
    print STDERR "
syntax: ccm-devel-create.pl [--verbose] [--dummy] [--extends <project>]
                            [--type project|application] [--version <version>]
                            <project-name> [user]

    If your project is based on CCM Core 5.x or 6.0, you must set the
    --version flag to '5x' or '6.0', accordingly.

    The '--extends' and '--type' options only apply to projects on
    CCM Core 5.x or 6.0.

    If your operating system does not set the \$USER or \$LOGNAME environment
    variable (ie, MS Windows), then supply your username as the last
    argument

    ccm-devel-create.pl project aplaws
";

    exit -1;
}

sub init {
    if (-f $portalloc . ".bak") {
        unlink $portalloc . ".bak";
    }
    $SIG{__DIE__} = \&abort;
    $nextport = &next_port();
}

sub sanity_check {
    if ($doimport) {
        chdir $scratchdir;
        if (-x "$project-$user") {
            rmtree("$project-$user");
            if (-x "$project-$user") {
                &myerror("cannot remove existing project directory " . File::Spec->catdir($scratchdir,"$project-$user"));
            }
        }
        system("CVSROOT=$cvsroot cvs -q co -l $project 1> $logfile 2>&1") != 0
            or $installed = 1;
    }
    if (-d File::Spec->catdir($devdir,$user,$project) && !$webapponly) {
        &myerror("a project is already checked out by the name $project");
    }
    if (-d File::Spec->catdir($webdir,$user,$project)) {
        &myerror("a project has already been deployed with the name $project");
    }
}

sub clone_template {
    my $basedir;
    if ($doimport) {
        chdir $scratchdir;
        $basedir = "$project-$user";
    } else {
        chdir File::Spec->catdir($devdir,$user);
        $basedir = $project;
    }
    mkdir ($basedir, 0777) unless $dummy;
    $newdevdir = 1;

    my @dirs = ();
    my @files = ();
    my $endchars;
    find(sub { if (-d && $File::Find::name ne $templatedir) { push @dirs, $File::Find::name } }, $templatedir);
    find(sub { if (-f) { push @files, $File::Find::name } }, $templatedir);
    if ($OS eq 'MSWin32') {
        $endchars = '@@';
    } else {
        $endchars = '::';
    }
    foreach (@dirs) {
        next if /CVS/;
        chomp;

        my $templatesub = "$templatedir";
        $templatesub =~ s/\\/\\\\/g;
        s/$templatesub.//;
        s/$endchars(\w+)$endchars/exists $globalvars->{$1} ? $globalvars->{$1} : $endchars . $1 . $endchars/gex;

        print "Cloning directory " . File::Spec->catdir($project,$_) . "\n" if $verbose;
        mkdir (File::Spec->catfile($basedir, $_), 0777) unless $dummy;
    }
    foreach my $src (@files) {
        next if $src =~ /CVS/;
        chomp $src;

        my $dst = $src;
        my $templatesub = $templatedir;
        $templatesub =~ s/\\/\\\\/g;
        $dst =~ s/$templatesub.//;
        $dst =~ s/__(\w+)__/exists $globalvars->{$1} ? $globalvars->{$1} : '__' . $1 . '__'/gex;
        $dst =~ s/$endchars(\w+)$endchars/exists $globalvars->{$1} ? $globalvars->{$1} : $endchars . $1 . $endchars/gex;

        print ("Cloning file " . File::Spec->catdir($project,$dst) . "\n") if $verbose;
        next if $dummy;
        &interpolate_file('source' => $src,
                          'destination' => File::Spec->catfile($basedir, $dst),
                          'vars' => $globalvars);
    }
    find( sub { if (m/\.sh$/ || m/\.pl$/) { chmod 0755, $File::Find::name}}, $basedir);
}

sub import_app {
    return unless $doimport;
    chdir File::Spec->catdir($scratchdir,"$project-$user");
    print "Importing $project-$user\n" if $verbose;
    return if $dummy;
    &myrun("CVSROOT=$cvsroot cvs -q import -m 'Initial import of application template' $project $project initial 1>> $logfile 2>&1");
}

sub checkout_app {
    return unless $doimport;
    chdir File::Spec->catdir($devdir,$user);
    print "Checking out $project\n" if $verbose;
    return if $dummy;
    &myrun("CVSROOT=$cvsroot cvs -q co $project 1>> $logfile 2>&1");
    chdir $project;
}

sub make_webapp {
    if (!$webapponly) {
        chdir File::Spec->catdir($devdir,$user,$project);
        &interpolate_file('source' => "ant.properties.in",
                          'destination' => "ant.properties",
                          'vars' => $globalvars);
        unlink("ant.properties.in");
        &myrun('ccm-configure');
    }
    chdir File::Spec->catdir($webdir,$user);
    print "Creating webapp root\n" if $verbose;
    return if $dummy;
    mkdir $project, 0777;
    chdir $project;
    mkdir 'webapps', 0777;
    mkdir 'logs', 0777;
    mkdir 'conf', 0777;
    &do_interp($envvars, File::Spec->catfile($webdir,$user,$project,'conf','envvars'));
    mkdir 'tmp', 0777;
    mkdir 'data', 0777;
    my $interp_count = 0;
    if ($ccmversion eq '5x' || $ccmversion eq '6.0') {
        $interp_count = &do_interp($resinconf5x, File::Spec->catfile($webdir,$user,$project,'conf','resin.conf')) +
            &do_interp($tomcatconf5x, File::Spec->catfile($webdir,$user,$project,'conf','server.xml'));
    } else {
        $interp_count = &do_interp($resinconf, File::Spec->catfile($webdir,$user,$project,'conf','resin.conf')) +
            &do_interp($tomcatconf, File::Spec->catfile($webdir,$user,$project,'conf','server.xml'));
    }
    if ($interp_count == 0) {
        print "WARNING: no ccm-tools servlet container packages found\n";
    }
}

sub do_interp {
    my $source = shift;
    my $target = shift;
    if (-f $source) {
        &interpolate_file('source' => $source,
                          'destination' => $target,
                          'vars' => $globalvars);
        print "wrote $target\n";
        return 1;
    } else {
        return 0;
    }
}

sub cleanup {
    rmtree(File::Spec->catdir($scratchdir,"$project-$user"));
}

sub abort {
    &cleanup();
    if (-f $portalloc . ".bak") {
        unlink $portalloc;
        rename $portalloc . ".bak", $portalloc;
    }
    if ( $newdevdir && -d File::Spec->catdir($devdir,$user,$project) ) {
        rmtree(File::Spec->catdir($devdir,$user,$project));
        rmtree(File::Spec->catdir($webdir,$user,$project));
    }
    print STDERR "Installation failed: $_[0]\n";
    print STDERR "Install logs at $logfile\n";
    exit 1;
}

sub notify_user {
    if ($webapponly) {
        print "
The webapp root has been created, your server will run on port $nextport.
Checkout your application to $devdir/$user/$project
";
    } else {
        print "
Installation complete; your server will run on port $nextport.
";
    }
}

sub next_port {
    open PORT, "<$portalloc"
        or &myerror("cannot open port allocation file: $portalloc");
    my $port = <PORT>;
    chomp $port;
    close PORT;

    &myerror("corrupt port allocation file") unless $port =~ /^\d+$/;

    my $nextport = $port + 2;

    rename $portalloc, $portalloc . ".bak";

    open PORT, ">$portalloc"
        or &myerror("cannot write port allocation file");
    print PORT $nextport, "\n";
    close PORT;

    return $port;
}

sub all_forward_slashes {
    # takes in a string, turns all backslashes to forward slashes, and returns the string
    my $in = shift;
    $in =~ s!\\!/!g;
    return $in;
}
sub myerror {
    &abort(@_);
}

sub myrun {
    system(@_);
    if ($?) {
        &myerror("cannot run @_ script: $!");
    }
}

# This is the end, my only friend, the end.
