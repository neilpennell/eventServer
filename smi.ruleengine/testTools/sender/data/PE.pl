#!/usr/bin/perl
require 'getopts.pl';

&Getopts('d:');

if ( ! $opt_d ) {
   print STDERR "Usage: wtdumprl | ParseEvents.pl -d <events directory>\n\n";
   exit(1);
}

$events_dir = $opt_d;
$events_list = $events_dir . "/events_list";
$time_list = $events_dir . "/time_list";
$raw_events = $events_dir . "/rawEvents";

if ( ! -d $events_dir ) {
	mkdir ("$events_dir", 0775);
	print "$events_dir does not exist. Directory created.\n";
}

open (Master_List, ">$events_list") || die "Unable to open $events_list!";
open (TIME, ">$time_list") || die "Unable to open $time_list!";
open (RAW, ">$raw_events") || die "Unable to open $raw_events";

$fname="event000000";
$open_file = 0;
$first=1;

print "Saving events to $events_dir...";

print TIME "0\n";
RECORD:
while (<>) {

	if (/^\n|^###|^PROCESSED/) { next RECORD; }

	# Dates before Sept 8, 2001 have a 9 digit epoch format, while dates afterwards have 10.
	if (/^\d+\~\d+\~\d+\~(\d{9,10})\(/) {
		$time = $1;
		if (!$first) {
			$slice = $time - $previous;
			print TIME "$slice\n";
		} 
		$first = 0;
		$previous = $time;

		$fname++;
		$event_dir = $events_dir . "/" . $fname;
	
		# mkdir ($event_dir, 0775);
		# open (LIST, ">$event_dir/events_list") || die "trying to open $fname/events_list" ;
		# print LIST "$fname\n";
		# close (LIST);
		print Master_List "$fname\n";
		$open_file = 1;
		$_ = '';
	}

	# if ($open_file) {
		# open(FNAME, ">$event_dir/$fname") || die "trying to open $fname/$fname" ;
		# $open_file = 0;
	# }

	# print FNAME;
	print RAW;

}

print "\nDone.\n";
