#!/usr/bin/ruby -w

# Part of CVSspam
#   http://www.badgers-in-foil.co.uk/projects/cvsspam/
# Copyright (c) David Holroyd

$repositorydir = ARGV.shift

$tmpdir = ENV["TMPDIR"] || "/tmp"

# try to pick a name to avoid collisions with other people's commits
$dirtemplate = "#cvsspam.#{Process.getpgrp}.#{Process.uid}"

def find_data_dir
  Dir["#{$tmpdir}/#{$dirtemplate}-*"].each do |dir|
    stat = File.stat(dir)
    return dir if stat.owned?
  end
  nil
end

$datadir = find_data_dir()

if $datadir==nil
  $datadir = "#{$tmpdir}/#{$dirtemplate}-#{rand(99999999)}"
  Dir.mkdir($datadir, 0700)
end

$tags = nil

# Record any tag name found in 'Entries' for files being commited.  This is
# required at pre-commit-time as we have no other way of dertermining what
# branch a file was on if it's been removed.
#
# If the commitinfo-tags file exists from a previous, unsuccessful commit,
# then it's possible for it to contain multiple entries for a particular file.
# The consumer of commitinfo-tags must take care to only use the last entry
# for a given filename.

def write_tag(name, file)
  if $tags.nil?
    $tags = File.new("#{$datadir}/commitinfo-tags", File::WRONLY|File::CREAT|File::APPEND);
    return if $tags.nil?
  end
  $tags.puts("#{name}\t#{file}")
end

File.open("CVS/Entries") do |file|
  file.each_line do |line|
    next if line =~ /^D/
    info = line.split(/\//)
    # skip entries not commited this invocation,
    if ARGV.delete(info[1]).nil?
      next
    end
    if info[5] =~ /^T(.+)/
      write_tag($1, "#{$repositorydir}/#{info[1]}")
    end
  end
end

$tags.close unless $tags.nil?

unless ARGV.empty?
  $stderr.puts "Nothing in CVS/Entries for "+ARGV.join(", ")
end


# Record the directory currently being commited to.
# 
# This script (and collect_diffs.rb) will be run just for the files in a
# single directory.
# 
# A commit to files in multiple directories will therefore produce multiple
# invocations of these scripts.  To send the email only when the whole commit
# is done, each run overwrites the 'lastdir' file; collect_diffs.rb will
# later inspect the value it contains to work out if it needs to generate the
# email yet.

File.open("#{$datadir}/lastdir", "w") { |file|
	file.write $repositorydir
}
