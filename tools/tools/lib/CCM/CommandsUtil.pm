#
# CCM::CommandsUtil
#
# Copyright (C) 2003 Red Hat, Inc.
#
# $Id: CommandsUtil.pm 288 2005-02-22 00:55:45Z sskracic $

=pod

=head1 NAME

CCM:CommandsUtil

=head1 SYNOPSIS

=head1 DESCRIPTION

=head1 METHODS

=over 4

=cut

package CCM::CommandsUtil;

use vars qw(@ISA @EXPORT_OK);
require Exporter;

@ISA = qw(Exporter);
@EXPORT_OK = qw();  # symbols to export on request

sub printUsage {
    if ( open USAGE, "< $0.usage" ) {
        local undef $/;
        print <USAGE> . "\n";
    } else {
        print "no usage available\n";
    }
}

sub printUsageAndExit {
    &printUsage();
    exit;
}

sub printHelp {
    if ( open HELP, "< $0.help" ) {
        local undef $/;
        print <HELP> . "\n";
    } else {
        print "no help available\n";
    }
}

sub printHelpAndExit {
    &printHelp();
    exit;
}

sub isCommandSafe {
    my $command = shift;

    if ($command =~ /^([:\/\\\w\s\d\.-]+)$/) {
        return 1;
    }

    return 0;
}

sub runSafe {
    my $command = shift;

    if ( ! &isCommandSafe($command) ) {
        print STDERR "command: '$command' did not pass safety filter\n";
        exit 1;
    }

    return &runAndExitOnError($command, @_);
}

sub runAndExitOnError {
    system @_;
    if ($?) {
        exit $? >> 8;
    }
    return $?;
}

sub getServletContainerCommand {
    my $sc = shift;
    my $type = shift;
    my $optional = shift;

    die "'type' must be specified'" unless ( defined $type );
    $type = uc $type;

    $optional = 0 unless ( defined $optional );

    if ( ! defined $sc ) {
        $sc = $ENV{'CCM_SERVLET_CONTAINER'};
        if ( ! defined $sc ) {
            if ( $optional ) {
                return undef;
            } else {
                print STDERR "servlet container not specified\n";
                exit 1;
            }
        }
    }
    $sc = uc $sc;

    my $command = $ENV{"CCM_SERVLET_${sc}_${type}"};

    if ( ! defined $command ) {
        if ( $optional ) {
            return undef;
        } else {
            print STDERR "CCM_SERVLET_${sc}_${type} not defined\n";
            exit 1;
        }
    }
    return $command;
}

1 # So that the require or use succeeds.

__END__

=back 4

=head1 AUTHORS

=head1 COPYRIGHT

=head1 SEE ALSO

L<perl(1)>

=cut
