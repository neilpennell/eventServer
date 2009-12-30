#!/usr/bin/perl
#-------------------------------------------------------------------------------
#
# Program:     SendEvents.pl
#
# Description: Replays an event stream back to the local TEC Server. This is
#              useful for testing purposes, because the content and timing of
#              the event stream is reproducible. Events are taken from the
#              reception log. 
#
# Author:      Jeff Mills 10/29/1996
#              Modified: 8/8/2000 by Howard McKinney
#              - Added the '-d' switch, added some error checking,
#                cleaned up messages. 
#              Modified: 02/11/2002 by Ana Biazetti
#              - fixed "-t" flag to support a "0" value
#
# Parameters: -d specifies the directory where the event data are kept. 
#             -t specifies a static time delay to use in between events. If not
#                specified, then the time delay is taken from the time_list file
#                found in the events direcotory.
#
# Returns:     1 - normal completion
#              0 - abnormal termination
#
# Assumptions:
#
# Limitations:
#
#-------------------------------------------------------------------------------
require 'getopts.pl';

#$BINDIR = "$ENV{'BINDIR'}";
#if ($BINDIR eq '') {
#	$BO = `objcall 0.0.0 self`;
#	chop ($OSERV = `objcall $BO getattr oserv`);
#	$EXECDIR = `objcall $OSERV query install_dir`;
#	$INTERP = `objcall $OSERV query interp`;
#	$BINDIR = "$EXECDIR/$INTERP";
#}

#$TEC_BIN_DIR = "$BINDIR/TME/TEC";
#$ENV{'TEC_BIN_DIR'} = "$TEC_BIN_DIR";
#$ENV{'PATH'} = "$ENV{'PATH'}:$TEC_BIN_DIR:$TEC_BIN_DIR/contrib";

@events = ();
@timer = ();
$static_time_slice = '';

sub get_time_slice {
	local ($index) = @_;

	if ( $static_time_slice ne '' ) {
		$static_time_slice;
	} else {
		$timer[$i];
	}
}

&Getopts('d:t:');

if ( ! $opt_d ) {
	print STDERR "Usage: SendEvents.pl -d <events directory> [-t <time slice>]\n\n";
	exit(1);
}

$events_dir = $opt_d;
$events_list = $events_dir . "/events_list";
$time_list = $events_dir . "/time_list";

if ( ! -r $events_list ) {
    print STDERR "$events_list does not exist or is unreadable.\n";
    exit(1);
}
	
open ( EVENTS, "$events_list") || die "Could not open $events_list!";
while (<EVENTS>) {
	chop;
	push ( @events, $_ )
};

if ($opt_t ne '') {
	$static_time_slice = $opt_t;
} else {
	if ( ! -r $time_list ) {
		print STDERR "$time_list does not exist or is unreadable.\n";
		exit(1);
	} else {
		open(TIMER, "$time_list");
		while (<TIMER>) {
			chop;
			push(@timer, $_)
		};
		close(TIMER);
	}
}

foreach $i (0..$#events) {
	$time_slice = &get_time_slice($i);
	print "Sending $events[$i] in $time_slice seconds ....\n";

	if ($time_slice > 0) {
		sleep $time_slice;
	}

 	$event = $events_dir . "/" . $events[$i] . "/" . $events[$i];
	$command = "cat $event  | ./ms -s localhost:5500 - ";
	system $command;
}

