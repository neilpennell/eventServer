#!/usr/bin/perl
# # ! /etc/Tivoli/bin/perl
#
# events.pl
# 
# script for processing TEC reception log dumps using wtdumprl
# It summarises the classes, peak event frequency and average rate of the dump file supplied
# as argument 1.
# NB The average calulation ignores minutes when there are no events, so is an overestimate.
# The output is written to a file called "input_filename.stats"
#
# V1.0	Original	Jun2001	Paul Claridge (paul@vicjen.co.uk)
#--------------------------------------------------------------------

open(IN,"$ARGV[0]") || die "Specify file to be processed as an argument!\n"; 

$old_time='';
$peak_min_events=$evts_tot=0;
while(<IN>) {
	if( /^1~[^\(]*\((\w{3})\s+(\d+)\s*(\d+):(\d+):.*/) {
		$new_time="$1_$2_$3_$4";
		if($old_time ne $new_time) { 
			# write min hash as string to tmp
			if (%min) {
				for(keys %min) {
					push(@tmp,"$_=$min{$_}");
					$tmp{$old_time}=join(',',@tmp);
					$min_tot+=$min{$_};
					}
				if($min_tot>$peak_min_events) {
					$peak_min_events=$min_tot if $min_tot>$peak_min_events;
					$peak_min=$old_time;
					}
				}
			@tmp=%min=(); 
			$min_tot=0;
			$old_time=$new_time;
			}
		next;
		}

	if(/^\#\#\# EVENT \#\#\#\s/) { $flag=1; next; }

	if($flag && /^([^\;]*)\;.*$/) {
		$evts{$1}++;
		$min{$1}++;
		$flag=0;
		}
	}

# catch last set of data (may be incomplete for that minute)
if (%min) {
	for(keys %min) {
		push(@tmp,"$_=$min{$_}");
		$tmp{$old_time}=join(',',@tmp);
		$min_tot+=$min{$_};
		}
	if($min_tot>$peak_min_events) {
		$peak_min_events=$min_tot if $min_tot>$peak_min_events;
		$peak_min=$old_time;
		}
	}

open(OUT,"> $ARGV[0].stats");
for (sort keys %tmp) { print OUT "$_\n\t",join("\n\t",sort split(',',$tmp{$_})),"\n"; }
print OUT "\n";
for (sort keys %evts) {
	print OUT "$_ = $evts{$_}\n";
	$evts_tot+=$evts{$_};
	}
print OUT "\n--- Max events per minute=<$peak_min_events> ($peak_min)
--- Total events=<$evts_tot>
--- Approximate events/min=<",sprintf("%.2f",$evts_tot/scalar keys %tmp),"> (OVERESTIMATE!)\n";
