#!/usr/bin/perl

print "$0 @ARGV\n";

$ADDRESS = shift;

($name, $aliases, $addrtype, $length, @addrs) = 
gethostbyaddr(pack ('c4',(split(/\./, $ADDRESS))), 2);

print "Canonical name:          <$name>\n";
print "Aliases:                 <$aliases>\n";
print "Address Type (2) :       <$addrtype>\n";
print "Length (4) :             <$length>\n";

($a, $b, $c, $d) = unpack('C4', $addrs[0]);

print "Address:                 <$a.$b.$c.$d>\n";

exit 0;
