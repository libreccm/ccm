#!/usr/bin/perl -wT

use strict;
use Cwd;
use File::Spec;

$ENV{PATH} = "/bin:/usr/bin";
delete $ENV{BASH_ENV};

if ($#ARGV != 0) {
    print "syntax: configure.pl system.conf\n";
    exit -1;
}

my $windows_environment = 0;
if ($^O eq 'MSWin32' || (defined $ENV{CCM_WINDOWS_MACHINE} && $ENV{CCM_WINDOWS_MACHINE} == "1")) {
    $windows_environment = 1;
}

my $errors = 0;

my $app_home;
my $resin_home;
my $java_home;
my $runtime_home;

my %hosts;

my %srun;
my @srun;
my %http;
my %vars;
my $container;
my $webxml = "servlet23";

&parse_config($ARGV[0]);
&check_config();

exit -1 if $errors;

&generate_enterprise();
&generate_webxml();

system("rm -f $app_home/bin/* $app_home/conf/*");

my $host = `hostname`;
chomp $host;

if ($container eq 'resin') {

    if (!exists $hosts{$host}) {
        die "cannot find entry for $host in configuration file\n";
    }

    if ($hosts{$host}->{type} eq 'resin') {
        &generate_run_resin($host);
        &generate_resin_back($host);
    } elsif (exists $hosts{'localhost'} && $hosts{'localhost'}->{type} eq 'resin') {
        &generate_run_resin('localhost');
        &generate_resin_back('localhost');
    }


    if ($hosts{$host}->{type} eq 'apache') {
        &generate_resin_vhost($host);
        &generate_resin_front($host);
    } elsif (exists $hosts{'localhost'} && $hosts{'localhost'}->{type} eq 'apache') {
        &generate_resin_front('localhost');
    }

    &generate_run($host);
} elsif ($container eq 'tomcat4') {
    &generate_modjk();
    &generate_workers();
    &generate_tomcat_vhost();
    &generate_tomcat4_server_xml();

    if ($windows_environment) {
        &generate_tomcat_service();
    } else {
        $hosts{$host}->{type} = 'tomcat4';
        &generate_run($host);
        &generate_run_tomcat();
    }

}


exit 0;

sub parse_config {
    my $config = shift;

    print "Reading $config\n";

    my $abs_path = File::Spec->rel2abs($config);
    my $directory = (File::Spec->splitpath($abs_path))[1];
    my $old_cwd = cwd();
    $old_cwd =~ /^(.*)$/;
    $old_cwd = $1;

    chdir ($directory);

    my $conf = "";

    {
        local $/;
        $/ = undef;

    open CONF, "<$config" or die "cannot open config file $config: $!";
        $conf = <CONF>;
        close CONF;
    }

    my $line = 0;
    foreach (split $/, $conf) {
        $line++;
        chomp;

        next if /^\s*$/;
        next if /^\s*\#/;

        if (/^\s*Include\s+\"(.+?)\"\s*$/) {
            # include a config file
            parse_config ($1);
            next;
        }

        if (/^\s*servlet-container\s*=\s*(\S+)\s*$/) {
            $container = $1;
        } elsif (/^\s*servlet-webxml\s*=\s*(\S+)\s*$/) {
            $webxml = $1;
        } elsif (/^\s*apache-home\s*=\s*(\S+)\s*$/) {
            warn "apache-home option is obsolete, please use include the resin-httpd-vhost.conf file in httpd.conf";
        } elsif (/^\s*resin-home\s*=\s*(\S+)\s*$/) {
            $resin_home = $1;
        } elsif (/^\s*java-home\s*=\s*(\S+)\s*$/) {
            $java_home = $1;
        } elsif (/^\s*app-home\s*=\s*(\S+)\s*$/) {
            $app_home = $1;
        } elsif (/^\s*runtime-home\s*=\s*(\S+)\s*$/) {
            $runtime_home = $1;
        } elsif (/^\s*resin\s*=\s*(\S+)\s*$/) {
            if (exists $hosts{$1}) {
                die "duplicate entry for host $1 line $line\n";
            }
            $hosts{$1} = { type => "resin" };
        } elsif (/^\s*tomcat\s*=\s*(\S+)\s*$/) {
            if (exists $hosts{$1}) {
                die "duplicate entry for host $1 line $line\n";
            }
            $hosts{$1} = { type => "tomcat" };
        } elsif (/^\s*apache\s*=\s*(\S+)\s*$/) {
            if (exists $hosts{$1}) {
                die "duplicate entry for host $1 line $line\n";
            }
            $hosts{$1} = { type => "apache" };
        } elsif (/^\s*srun\s*=\s*(\S+):(\d+)\s*$/) {
            $srun{$1} = { port => $2, firewall => $1, connect => $2};
            push @srun, $1;
        } elsif (/^\s*srun\s*=\s*(\S+):(\d+)\s*,\s*(\S+):(\d+)\s*$/) {
            $srun{$1} = { port => $2, firewall => $3, connect => $4};
            push @srun, $1;
        } elsif (/^\s*http\s*=\s*(\S+):(\d+)\s*$/) {
            $http{$1} = $2;
        } elsif (/^\s*((?:\w|-)+)\s*=\s*(.*?)\s*$/) {
            $vars{$1} = $2;
        } else {
            die "malformed config option at line $line";
        }
    }

    chdir ($old_cwd);
}


sub interpolate_file {
    my $src = shift;
    my $dest = shift;
    my $vars = shift;

    open SRC, "<$src" or die "cannot read file $src: $!";
    open DEST, ">$dest" or die "cannot create file $dest: $!";

    while (<SRC>) {
        s/::((?:\w|-)+)::/exists $vars->{$1} ? $vars->{$1} : (exists $vars{$1} ? $vars{$1} : "::${1}::")/gex;
        print DEST;
    }

    close SRC;
    close DEST;

    print "Wrote $dest\n";

}


sub check_config {
    &check_value("servlet-container", $container);
    &check_value('java-home', $java_home);
    &check_value('app-home', $app_home);
    &check_value('runtime-home', $runtime_home);

    if (defined $container) {
        if ($container eq 'resin') {
            &check_value('resin-home', $resin_home);
        } elsif ($container eq 'tomcat4') {
        } else {
            die "unknown servlet container $container\n";
        }
    }

    if ( ($windows_environment) && ($container ne 'tomcat4') ) {
        die "$container is not yet supported under Windows\n";
    }

}


sub check_value {
    my $option = shift;
    my $value = shift;

    $errors++ unless defined $value;
    warn "$option not specified in config file\n" unless defined $value;
}


sub generate_enterprise {
    &interpolate_file($app_home . "/dist/WEB-INF/resources/enterprise.init.in",
                      $app_home . "/dist/WEB-INF/resources/enterprise.init");
}

sub generate_webxml {
  &interpolate_file($app_home . "/dist/WEB-INF/web.xml.$webxml",
                    $app_home . "/dist/WEB-INF/web.xml");
  &insert_webxml_includes($app_home . "/dist/WEB-INF/web.xml");
}

sub insert_webxml_includes {
    my $webxml = shift;

    my $abs_path = File::Spec->rel2abs($webxml);
    my $directory = (File::Spec->splitpath($abs_path))[1];

    my $servdec = "";
    my $servmap = "";

    local $/;
    $/ = undef;

    opendir(DIR, $directory) || die "can't opendir $directory: $!";

    my @files = grep { /.*\.servlet-declarations\.xml$/ && -f "$directory/$_" } readdir(DIR);
    foreach (@files) {
        open(FILE, "< $directory/$_") || die "can't open file $directory/$_: $!";
        $servdec .= <FILE> . "\n";
        close(FILE);
    }

    rewinddir DIR;

    @files = grep { /.*\.servlet-mappings\.xml$/ && -f "$directory/$_" } readdir(DIR);
    foreach (@files) {
        open(FILE, "< $directory/$_") || die "can't open file $directory/$_: $!";
        $servmap .= <FILE> . "\n";
        close(FILE);
    }

    closedir DIR;

    open(FILE, "< $webxml") || die "can't open file $webxml: $!";
    my $webxml_contents = <FILE>;
    close FILE;

    my $servdec_match_regexp = '<!-- /ADDITIONAL SERVLET DECLARATIONS -->';
    my $servmap_match_regexp = '<!-- /ADDITIONAL SERVLET MAPPINGS -->';
    my $servdec_replace_regexp = "$servdec<!-- /ADDITIONAL SERVLET DECLARATIONS -->";
    my $servmap_replace_regexp = "$servmap<!-- /ADDITIONAL SERVLET MAPPINGS -->";

    $webxml_contents =~ s/$servmap_match_regexp/$servmap_replace_regexp/s;
    $webxml_contents =~ s/$servdec_match_regexp/$servdec_replace_regexp/s;

    open(FILE, "> $webxml") || die "can't open file $webxml: $!";
    print FILE $webxml_contents;
    close FILE;
}

sub generate_run {
    my $host = shift;

    my $disable_resin = ($hosts{$host}->{type} eq 'resin' ||
                         (exists $hosts{'localhost'} && $hosts{'localhost'}->{type} eq 'resin')) ? "" : "#";
    my $disable_apache = ($hosts{$host}->{type} eq 'resin' ||
                          (exists $hosts{'localhost'} && $hosts{'localhost'}->{type} eq 'resin')) ? "#" : "";

    &interpolate_file($runtime_home . "/bin/$container/run.sh.in",
                      $app_home . "/bin/run.sh",
                      {
                          'app-home' => $app_home,
                          'disable-apache' => $disable_apache,
                          'disable-resin' => $disable_resin,
                      });
    chmod 0755, $app_home . "/bin/run.sh";
}

sub generate_run_tomcat {
    my $host = shift;

    &interpolate_file($runtime_home . "/bin/tomcat4/run-tomcat.sh.in",
                      $app_home . "/bin/run-tomcat.sh",
                      {
                          'java-home' => $java_home,
                          'app-home' => $app_home,
                      });
    chmod 0755, $app_home . "/bin/run-tomcat.sh";
}

sub generate_run_resin {
    my $host = shift;

    &interpolate_file($runtime_home . "/bin/resin/run-resin.sh.in",
                      $app_home . "/bin/run-resin.sh",
                      {
                          'resin-home' => $resin_home,
                          'resin-conf' => "$app_home/conf/resin.conf",
                          'java-home' => $java_home,
                          'app-home' => $app_home,
                      });
    chmod 0755, $app_home . "/bin/run-resin.sh";
}

sub generate_resin_front {
    my $host = shift;

    &interpolate_file($runtime_home . "/conf/$container/resin-front.conf.in",
                      $app_home . "/conf/resin-apache.conf",
                      {
                          'srun-begin1' => $#srun > -1 ? "" : "<!--",
                          'srun-host1' => $#srun > -1 ? $srun{$srun[0]}->{firewall} : "",
                          'srun-port1' => $#srun > -1 ? $srun{$srun[0]}->{connect} : "",
                          'srun-end1' => $#srun > -1 ? "" : "-->",

                          'srun-begin2' => $#srun > 0 ? "" : "<!--",
                          'srun-host2' => $#srun > 0 ? $srun{$srun[1]}->{firewall} : "",
                          'srun-port2' => $#srun > 0 ? $srun{$srun[1]}->{connect} : "",
                          'srun-end2' => $#srun > 0 ? "" : "-->",

                          'srun-begin3' => $#srun > 1 ? "" : "<!--",
                          'srun-host3' => $#srun > 1 ? $srun{$srun[2]}->{firewall} : "",
                          'srun-port3' => $#srun > 1 ? $srun{$srun[2]}->{connect} : "",
                          'srun-end3' => $#srun > 1 ? "" : "-->",

                          'srun-begin4' => $#srun > 2 ? "" : "<!--",
                          'srun-host4' => $#srun > 2 ? $srun{$srun[3]}->{firewall} : "",
                          'srun-port4' => $#srun > 2 ? $srun{$srun[3]}->{connect} : "",
                          'srun-end4' => $#srun > 2 ? "" : "-->",

                          'srun-begin5' => $#srun > 3 ? "" : "<!--",
                          'srun-host5' => $#srun > 3 ? $srun{$srun[4]}->{firewall} : "",
                          'srun-port5' => $#srun > 3 ? $srun{$srun[4]}->{connect} : "",
                          'srun-end5' => $#srun > 3 ? "" : "-->",
                      });
}


sub generate_resin_back {
    my $host = shift;

    &interpolate_file($runtime_home . "/conf/$container/resin-back.conf.in",
                      $app_home . "/conf/resin.conf",
                      {
                          'webapp-dir' => "$app_home/dist",
                          'threads' => $vars{'db-pool'} - 5,
                          'keepalive' => $vars{'db-pool'} - 6,
                          'http-begin' => exists $http{$host} ? "" : "<!--",
                          'http-end' => exists $http{$host}  ? "" : "-->",
                          'http-port' => exists $http{$host} ? $http{$host} : 80,
                          'srun-begin' => exists $srun{$host} ? "" : "<!--",
                          'srun-end' => exists $srun{$host} ? "" : "-->",
                          'srun-host' => $host,
                          'srun-port' => exists $srun{$host} ? $srun{$host}->{port} : 6802,

                      });
}


sub generate_modjk {
    &interpolate_file($runtime_home . "/conf/$container/mod_jk.conf.in",
                      $app_home . "/conf/mod_jk.conf", {
                          'app-home' => $app_home,
                          'webapp-dir' => "$app_home/dist",
                      });
}

sub generate_tomcat_apps {
    &interpolate_file($runtime_home . "/conf/$container/tomcat-apps.xml.in",
                      $vars{'tomcat-conf-home'} . "/apps-acsj.xml", {
                          'app-home' => $app_home,
                          'webapp-dir' => "$app_home/dist",
                      });
}

sub generate_tomcat_env {
    my $source_file;
    my $target_file;

    if ($windows_environment) {
        $source_file = "tomcat-env.cmd.in";
        $target_file = "tomcat-env.cmd";
    } else {
        $source_file = "tomcat-env.in";
        $target_file = "tomcat-env";
    }

    &interpolate_file($runtime_home . "/conf/$container/$source_file",
                      $app_home . "/conf/$target_file", {
                          'app-home' => $app_home,
                          'webapp-dir' => "$app_home/dist",
                          'java-home' => "$java_home",
                      });
}

sub generate_tomcat_service {
    &interpolate_file($runtime_home . "/bin/$container/tomcat-service-install.cmd.in",
                      $app_home . "/bin/tomcat-service-install.cmd", {
                          'app-home' => $app_home,
                          'webapp-dir' => "$app_home/dist",
                          'java-home' => "$java_home",
                      });
    &interpolate_file($runtime_home . "/bin/$container/tomcat-service-uninstall.cmd.in",
                      $app_home . "/bin/tomcat-service-uninstall.cmd", {
                          'app-home' => $app_home,
                          'webapp-dir' => "$app_home/dist",
                          'java-home' => "$java_home",
                      });
}

sub generate_tomcat4_server_xml {

    # This procedure only applies to Tomcat 4
    return unless ($container eq 'tomcat4');

    &interpolate_file($runtime_home . "/conf/$container/server.xml.in",
                      $app_home . "/conf/server.xml", {
                          'app-home' => $app_home,
                          'webapp-dir' => "$app_home/dist",
                          'java-home' => "$java_home",
                      });
    &interpolate_file($runtime_home . "/conf/$container/web.xml.in",
                      $app_home . "/conf/web.xml", {
                          'app-home' => $app_home,
                          'webapp-dir' => "$app_home/dist",
                          'java-home' => "$java_home",
                      });
    &interpolate_file($runtime_home . "/conf/$container/tomcat-users.xml.in",
                      $app_home . "/conf/tomcat-users.xml", {
                          'app-home' => $app_home,
                          'webapp-dir' => "$app_home/dist",
                          'java-home' => "$java_home",
                      });
}

sub generate_workers {
    my $workers_file;

    if ($windows_environment) {
        $workers_file = 'workers.properties-win32.in';
    } else {
        $workers_file = 'workers.properties.in';
    }

    &interpolate_file($runtime_home . "/conf/$container/$workers_file",
                      $app_home . "/conf/workers.properties", {
                          'app-home' => $app_home,
                          'webapp-dir' => "$app_home/dist",
                      });
}

sub generate_tomcat_vhost {
    &interpolate_file($runtime_home . "/conf/$container/httpd-tomcat-vhost.conf.in",
                      $app_home . "/conf/httpd-tomcat-vhost.conf", {
                          'app-home' => $app_home,
                          'webapp-dir' => "$app_home/dist",
                      });
}

sub generate_resin_vhost {
    my $host = shift;
    &interpolate_file($runtime_home . "/conf/$container/httpd-resin-vhost.conf.in",
                      $app_home . "/conf/httpd-resin-vhost.conf", {
                          'app-home' => $app_home,
			  'port' => $http{$host},
                          'webapp-dir' => "$app_home/dist",
                          'resin-module' => "$resin_home/modules/apache/mod_caucho.so",
                          'resin-conf' => "$app_home/conf/resin-apache.conf",
                      });
}

# End of file
