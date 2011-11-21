opendir(my $dh, $ARGV[0]) or
    die "cannot opendir $ARGV[0]: $!\n";
my(@files) = grep { /\.txt$/ } readdir($dh);
closedir($dh);

print "<corpus>\n";
foreach my $file (sort @files) {
    # print "$file\n";
    print "<document>\n";
    open(my $fh, "$ARGV[0]/$file") or
	die "cannot open file $ARGV[0]/$file: $!\n";
    print "<docid>" . (split(/\./, $file))[0] . "</docid>\n";
    print "<text>\n";
    my(%sChar);
    my(%eChar);
    # generate character counter
    my($counter) = 0;
    my($i) = 0;
    my($text) = "";
    while (<$fh>) {
	chomp;
#	print "$_\n";
	if ($text eq "") {
	    $text = $_;
	}
	else {
	    $text .= " " . $_;
	}
	# seems to have already pruned multiple spaces
	my(@words) = split(/\s|\-|\/|'/, $_);
	foreach my $w (@words) {
	    my($prefix) = $w;
#	    $prefix =~ s/(^[^A-Za-z0-9\"]*).*/$1/;
	    $prefix =~ s/(^\W*).*/$1/;
	    $sChar{$counter + length($prefix)} = $i;
	    my($text) = $w;
	    $text =~ s/\W+\d+(,\d+)*\W+$//;
	    $text =~ s/\W*(\[\d+\])*$//;
	    $eChar{$counter + length($text)} = $i;
	    $i++;
	    $counter += length($w) + 1;
#	    $counter++;
	}
    }
    close($fh);
    print "$text\n</text>\n";
#    foreach my $key (sort keys %sChar) {
#	print "$key $sChar{$key}\n";
#    }
#    foreach my $key (sort keys %eChar) {
#	print "$key $eChar{$key}\n";
#    }
    my($annotation) = "$ARGV[0]/$file";
    $annotation =~ s/txt$/a2/;
    open(my $fh, $annotation) or
	die "cannot open annotation $annotation: $!\n";
    my($qCount) = 0;
    while (<$fh>) {
	chomp;
	my(@fields) = split;
	if ($_ =~ /^T/) {
#	    print "$_\n";
	    if ((!exists($sChar{$fields[2]})) || (!exists($eChar{$fields[3]}))) {
		die "cannot locate bytes $file: $_\n";
	    }
	    else {
		print "<localization>\n";
		print "<label>$fields[1]</label>\n";
		print "<id>$fields[0]</id>\n";
		print "<string>@fields[4..$#fields]</string>\n";
		print "<start>$sChar{$fields[2]}</start>\n";
		print "<end>$eChar{$fields[3]}</end>\n";
		print "</localization>\n";
	    }
	}
	elsif ($_ =~ /^(E|\*)/) {
#	    print "$_\n";
	    print "<relation>\n";
	    print "<label>$fields[1]</label>\n";
	    if ($fields[0] eq "*") {
		print "<id>Q$qCount</id>\n";
		print "<arg>$fields[2]</arg>\n";
		print "<arg>$fields[3]</arg>\n";
		$qCount++;
	    }
	    else {
		print "<id>$fields[0]</id>\n";
		print "<arg>" . (split(/:/, $fields[2]))[1] . "</arg>\n";
		print "<arg>" . (split(/:/, $fields[3]))[1] . "</arg>\n";
	    }
	    print "</relation>\n";
	}
	else {
	    die "$_\n";
	}
#	print "$_\n";
    }
    close($fh);
    print "</document>\n";
}
print "</corpus>\n";
