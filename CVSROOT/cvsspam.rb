#!/usr/bin/ruby -w

# Part of CVSspam
#   http://www.badgers-in-foil.co.uk/projects/cvsspam/
# Copyright (c) David Holroyd

# collect_diffs.rb expects to find this script in the same directory as it
#

# TODO: exemplify syntax for 'cvs admin -m' when log message is missing
# TODO: make max-line limit on diff output configurable
# TODO: put more exact max size limit on whole email
# TODO: support non-html mail too (text/plain, multipart/alternative)

# If you want another 'todo keyword' (TODO & FIXME are highlighted by default)
# you could add
#   $task_keywords << "KEYWORD" << "MAYBEANOTHERWORD"
# to your cvssppam.conf


$version = "0.2.10"


$maxSubjectLength = 200
$maxLinesPerDiff = 1000
$maxDiffLineLength = 1000	# may be set to nil for no limit
$charset = nil			# nil implies 'don't specify a charset'
$mailSubject = ''

def blah(text)
  $stderr.puts("cvsspam.rb: #{text}") if $debug
end

def min(a, b)
  a<b ? a : b
end

# NB must ensure the time is UTC
# (the Ruby Time object's strftime() doesn't supply a numeric timezone)
DATE_HEADER_FORMAT = "%a, %d %b %Y %H:%M:%S +0000"

# the regexps given as keys must not use capturing subexpressions '(...)'
class MultiSub
  def initialize(hash)
    @mash = Array.new
    expr = nil
    hash.each do |key,val|
      if expr == nil ; expr="(" else expr<<"|(" end
      expr << key << ")"
      @mash << val
    end
    @re = Regexp.new(expr)
  end

  def gsub!(text)
    text.gsub!(@re) { |match|
      idx = -1
      $~.to_a.each { |subexp|
        break unless idx==-1 || subexp==nil
        idx += 1
      }
      idx==-1 ? match : @mash[idx].call(match)
    }
  end
end

# Limited support for encoding non-US_ASCII characters in mail headers
class HeaderEncoder
  def initialize
    @right_margin = 78
    @encoding = 'q' # quoted-printable, base64 not supported
    @charset = nil # TODO: some better default?
  end

  attr_accessor :charset

  def encode_header(io, name, value)
    name = name + ": "
    if requires_rfc2047?(value)
      rfc2047_encode_quoted(io, name, value)
    else
      wrap_basic_header(io, name, value)
    end
  end


  def wrap_basic_header(io, start, rest)
    rest.scan(/\s*\S+/) do |match|
      if start.length>0 && start.length+match.length>@right_margin
        io.puts(start)
        start = " "
        match.sub!(/^\s+/, "") # strip existing leading-whitespace
      end
      start << match
    end
    io.puts(start)
  end

  UNDERSCORE = "_"[0]
  SPACE = " "[0]
  TAB = "\t"[0]

  def rfc2047_encode_quoted(io, start, rest)
    raise "no charset" if @charset.nil?
    code_begin = "=?#{@charset}?#{@encoding}?"
    start << code_begin
    rest.each_byte do |b|
      code = if b>126 || b==UNDERSCORE || b==TAB
        sprintf("=%02x", b)
      elsif b == SPACE
        "_"
      else
        b.chr
      end

      if start.length+code.length+2 > @right_margin
        io.puts(start + "?=")
        start = " " + code_begin
      end
      start << code
    end
    io.puts(start + "?=")
  end

 private
  def requires_rfc2047?(word)
    (word =~ /[\177-\377]/) != nil
  end
end


class LogReader
  def initialize(logIO)
    @io = logIO
    advance
  end

  def currentLineCode ; @line[1,1]  end


  class ConstrainedIO
    def initialize(reader)
      @reader = reader
      @linecode = reader.currentLineCode
    end

    def each
      return if @reader == nil
      while true
        yield @reader.currentLine
        break unless @reader.advance && currentValid?
      end
      @reader = nil
    end

    def gets
      return nil if @reader == nil
      line = @reader.currentLine
      return nil if line==nil || !currentValid?
      @reader.advance
      return line
    end

    def currentValid?
      @linecode == @reader.currentLineCode
    end
  end

  def getLines
    ConstrainedIO.new(self)
  end

  def eof ; @line==nil  end

  def advance
    @line = @io.gets
    return false if @line == nil
    unless @line[0,1] == "#"
      raise "#{$logfile}:#{@io.lineno} line did not begin with '#': #{@line}"
    end
    return true
  end

  def currentLine
    @line==nil ? nil : @line[3, @line.length-4]
  end
end


def htmlEncode(text)
  text.gsub(/./) do
    case $&
      when "&" then "&amp;"
      when "<" then "&lt;"
      when ">" then "&gt;"
      else $&
    end
  end
end

# actually, allows '/' to appear
def urlEncode(text)
  text.sub(/[^a-zA-Z0-9\-,.*_\/]/) do
    "%#{sprintf('%2X', $&[0])}"
  end
end

# a top-level directory under the $CVSROOT
class Repository
  @@repositories = Hash.new

  def initialize(name)
    @name = name
    @common_prefix = nil
    @all_tags = Hash.new
  end

  def add_tag(tag_name)
    if @all_tags[tag_name]
      @all_tags[tag_name] += 1
    else
      @all_tags[tag_name] = 1
    end 
  end

  def has_multiple_tags
    @all_tags.length > 1
  end

  def each_tag
    @all_tags.each_key do |tag|
      yield tag
    end
  end

  def trunk_only?
    @all_tags.length==1 && @all_tags[nil]!=nil
  end

  def mixed_tags?
    @all_tags.length>1
  end

  def tag_count
    @all_tags.length
  end

  # calculate the path prefix shared by all files commited to this
  # reposotory
  def merge_common_prefix(path)
    if @common_prefix == nil
      @common_prefix = path.dup
    else
      path = path.dup
      until @common_prefix == path
        if @common_prefix.size>path.size
          if @common_prefix.sub!(/(.*)\/.*$/, '\1').nil?
            raise "unable to merge '#{path}' in to '#{@common_prefix}': prefix totally different"
          end
        else
          if path.sub!(/(.*)\/.*$/, '\1').nil?
            raise "unable to merge '#{path}' in to '#{@common_prefix}': prefix totally different"
          end
        end
      end
    end
  end

  attr_reader :name, :common_prefix

  def Repository.get(name)
    name =~ /^[^\/]+/
    name = $&
    rep = @@repositories[name]
    if rep.nil?
      rep =  Repository.new(name)
      @@repositories[name] = rep
    end
    rep
  end

  def Repository.count
    @@repositories.size
  end

  def Repository.each
    @@repositories.each_value do |rep|
      yield rep
    end
  end

  def Repository.array
    @@repositories.values
  end

  def to_s
    if trunk_only?
      @name
    elsif mixed_tags?
      "#{@name}@.."
    else
      "#{@name}@#{@all_tags.keys[0]}"
    end
  end
end

class FileEntry
  def initialize(path)
    @path = path
    @lineAdditions = @lineRemovals = 0
    @repository = Repository.get(path)
    @repository.merge_common_prefix(basedir())
    @isEmpty = @isBinary = false
    @has_diff = nil
  end

  attr_accessor :path, :type, :lineAdditions, :lineRemovals, :isBinary, :isEmpty, :fromVer, :toVer

  def file
    @path =~ /.*\/(.*)/
    $1
  end

  def tag=(name)
    @tag = name
    @repository.add_tag(name)
  end

  def tag
    @tag
  end

  def basedir
    @path =~ /(.*)\/.*/
    $1
  end

  def repository
    @repository
  end

  def name_after_common_prefix
    @path.slice(@repository.common_prefix.size+1,@path.size-@repository.common_prefix.size-1)
  end

  def removal?
    @type == "R"
  end

  def addition?
    @type == "A"
  end

  def modification?
    @type == "M"
  end

  def has_diff=(diff)
    @has_diff = diff if @has_diff.nil?
  end

  def has_diff?
    @has_diff
  end
end


class LineConsumer
  def handleLines(lines, emailIO)
    @emailIO = emailIO
    @lineCount = 0
    setup
    lines.each do |line|
      @lineCount += 1
      consume(line)
    end
    teardown
  end

  def setup
  end

  def teardown
  end

  def lineno
    @lineCount
  end

  def println(text)
    @emailIO.puts(text)
  end

  def print(text)
    @emailIO.print(text)
  end
end


# TODO: consolidate these into a nicer framework,
mailSub = proc { |match| "<a href=\"mailto:#{match}\">#{match}</a>" }
urlSub = proc { |match| "<a href=\"#{match}\">#{match}</a>" }
bugzillaSub = proc { |match|
  match =~ /([0-9]+)/
  "<a href=\"#{$bugzillaURL.sub(/%s/, $1)}\">#{match}</a>"
}
jiraSub = proc { |match|
  "<a href=\"#{$jiraURL.sub(/%s/, match)}\">#{match}</a>"
}
ticketSub = proc { |match|
  match =~ /([0-9]+)/
  "<a href=\"#{$ticketURL.sub(/%s/, $1)}\">#{match}</a>"
}
commentSubstitutions = {
		'(?:mailto:)?[\w\.\-\+\=]+\@[\w\-]+(?:\.[\w\-]+)+\b' => mailSub,
		'\b(?:http|https|ftp):[^ \t\n<>"]+[\w/]' => urlSub}


class CommentHandler < LineConsumer
  def initialize
    @lastComment = nil
  end

  def setup
    @haveBlank = false
    @comment = ""
  end

  def consume(line)
    if line =~ /^\s*$/
      @haveBlank = true
    else
      if @haveBlank
        @comment += "\n"
        @haveBlank = false
      end
      $mailSubject = line unless $mailSubject.length > 0
      @comment += line += "\n"
    end
  end

  def teardown
    unless @comment == @lastComment
      println("<pre class=\"comment\">")
      encoded = htmlEncode(@comment)
      $commentEncoder.gsub!(encoded)
      println(encoded)
      println("</pre>")
      @lastComment = @comment
    end
  end
end


class TagHandler < LineConsumer
  def initialize
    @tag = nil
  end

  def consume(line)
    # TODO: check there is only one line
    @tag = line
  end

  def getLastTag
    tmp = @tag
    @tag = nil
    tmp
  end
end

class VersionHandler < LineConsumer
  def consume(line)
    # TODO: check there is only one line
    $fromVer,$toVer = line.split(/,/)
  end
end


class FileHandler < LineConsumer
  def setTagHandler(handler)
    @tagHandler = handler
  end

  def setup
    $fileHeaderHtml = ''
    println("<hr /><a name=\"file#{$fileEntries.size+1}\" /><div class=\"file\">")
  end

  def consume(line)
    $file = FileEntry.new(line)
    if $diff_output_limiter.choose_to_limit?
      $file.has_diff = false
    end
    $fileEntries << $file
    $file.tag = getTag
    handleFile($file)
  end

 protected
  def getTag
    @tagHandler.getLastTag
  end

  def println(text)
    $fileHeaderHtml << text << "\n"
  end

  def print(text)
    $fileHeaderHtml << text
  end
end


# don't make any links
class NoFrontend
  def path(path, tag)
    htmlEncode(path)
  end

  def version(path, version)
    version
  end

  def diff(file)
    '-&gt;'
  end
end

# Superclass for objects that can link to CVS frontends on the web (ViewCVS,
# Chora, etc.).
class WebFrontend < NoFrontend
  def initialize(base_url)
    @base_url = base_url
  end

  def path(path, tag)
    path_for_href = ""
    result = ""
    path.split("/").each do |component|
      unless result == ""
        result << "/"
        path_for_href << "/"
      end
      path_for_href << component
      result << "<a href=\"#{path_url(path_for_href, tag)}\">#{htmlEncode(component)}</a>"
    end
    result
  end

  def version(path, version)
    "<a href=\"#{version_url(path, version)}\">#{version}</a>"
  end

  def diff(file)
    "<a href=\"#{diff_url(file)}\">#{super(file)}</a>"
  end

 protected
  def add_repo(url)
    if @repository_name
      if url =~ /\?/
        "#{url}&amp;cvsroot=#{urlEncode(@repository_name)}"
      else
        "#{url}?cvsroot=#{urlEncode(@repository_name)}"
      end
    else
      url
    end
  end
end

# Link to ViewCVS
class ViewCVSFrontend < WebFrontend

  attr_accessor :repository_name

  def initialize(base_url)
    super(base_url)
    @repository_name = nil
  end

  def path_url(path, tag)
    if tag == nil
      add_repo(@base_url + urlEncode(path))
    else
      add_repo("#{@base_url}#{urlEncode(path)}?only_with_tag=#{urlEncode(tag)}")
    end
  end

  def version_url(path, version)
    add_repo("#{@base_url}#{urlEncode(path)}?rev=#{version}&amp;content-type=text/vnd.viewcvs-markup")
  end

  def diff_url(file)
    add_repo("#{@base_url}#{urlEncode(file.path)}.diff?r1=#{file.fromVer}&amp;r2=#{file.toVer}")
  end
end

# Link to Chora, from the Horde framework
class ChoraFrontend < WebFrontend
  def path_url(path, tag)
    # TODO: can we pass the tag somehow?
    "#{@base_url}/cvs.php/#{urlEncode(path)}"
  end

  def version_url(path, version)
    "#{@base_url}/co.php/#{urlEncode(path)}?r=#{version}"
  end

  def diff_url(file)
    "#{@base_url}/diff.php/#{urlEncode(file.path)}?r1=#{file.fromVer}&r2=#{file.toVer}"
  end
end

# Link to CVSweb
class CVSwebFrontend < WebFrontend
  def path_url(path, tag)
    if tag == nil
      add_repo(@base_url + urlEncode(path))
    else
      add_repo("#{@base_url}#{urlEncode(path)}?only_with_tag=#{urlEncode(tag)}")
    end
  end

  def version_url(path, version)
    add_repo("#{@base_url}#{urlEncode(path)}?rev=#{version}&amp;content-type=text/x-cvsweb-markup")
  end

  def diff_url(file)
    add_repo("#{@base_url}#{urlEncode(file.path)}.diff?r1=text&amp;tr1=#{file.fromVer}&amp;r2=text&amp;tr2=#{file.toVer}&amp;f=h")
  end
end


# in need of refactoring...

class AddedFileHandler < FileHandler
  def handleFile(file)
    file.type="A"
    file.toVer=$toVer
    print("<span class=\"pathname\" id=\"added\">")
    print($frontend.path(file.basedir, file.tag))
    println("<br /></span>")
    println("<div class=\"fileheader\" id=\"added\"><big><b>#{htmlEncode(file.file)}</b></big> <small id=\"info\">added at #{$frontend.version(file.path,file.toVer)}</small></div>")
  end
end

class RemovedFileHandler < FileHandler
  def handleFile(file)
    file.type="R"
    file.fromVer=$fromVer
    print("<span class=\"pathname\" id=\"removed\">")
    print($frontend.path(file.basedir, file.tag))
    println("<br /></span>")
    println("<div class=\"fileheader\" id=\"removed\"><big><b>#{htmlEncode(file.file)}</b></big> <small id=\"info\">removed after #{$frontend.version(file.path,file.fromVer)}</small></div>")
  end
end

class ModifiedFileHandler < FileHandler
  def handleFile(file)
    file.type="M"
    file.fromVer=$fromVer
    file.toVer=$toVer
    print("<span class=\"pathname\">")
    print($frontend.path(file.basedir, file.tag))
    println("<br /></span>")
    println("<div class=\"fileheader\"><big><b>#{htmlEncode(file.file)}</b></big> <small id=\"info\">#{$frontend.version(file.path,file.fromVer)} #{$frontend.diff(file)} #{$frontend.version(file.path,file.toVer)}</small></div>")
  end
end


class UnifiedDiffStats
  def initialize
    @diffLines=3  # the three initial lines in the unidiff
  end

  def diffLines
    @diffLines
  end

  def consume(line)
    @diffLines += 1
    case line[0,1]
      when "+" then $file.lineAdditions += 1
      when "-" then $file.lineRemovals += 1
    end
  end
end

# TODO: change-within-line colourisation should really be comparing the
#       set of lines just removed with the set of lines just added, but
#       it currently considers just a single line

class UnifiedDiffColouriser < LineConsumer
  def initialize
    @currentState = "@"
    @currentStyle = "info"
    @lineJustDeleted = nil
    @lineJustDeletedSuperlong = false
    @truncatedLineCount = 0
  end

  def output=(io)
    @emailIO = io
  end

  def consume(line)
    initial = line[0,1]
    superlong_line = false
    if $maxDiffLineLength && line.length > $maxDiffLineLength+1
      line = line[0, $maxDiffLineLength+1]
      superlong_line = true
      @truncatedLineCount += 1
    end
    if initial != @currentState
      prefixLen = 1
      suffixLen = 0
      if initial=="+" && @currentState=="-" && @lineJustDeleted!=nil
        # may be an edit, try to highlight the changes part of the line
        a = line[1,line.length-1]
        b = @lineJustDeleted[1,@lineJustDeleted.length-1]
        prefixLen = commonPrefixLength(a, b)+1
        suffixLen = commonPrefixLength(a.reverse, b.reverse)
        # prevent prefix/suffux having overlap,
        suffixLen = min(suffixLen, min(line.length,@lineJustDeleted.length)-prefixLen)
        deleteInfixSize = @lineJustDeleted.length - (prefixLen+suffixLen)
        addInfixSize = line.length - (prefixLen+suffixLen)
        oversize_change = deleteInfixSize*100/@lineJustDeleted.length>33 || addInfixSize*100/line.length>33

        if prefixLen==1 && suffixLen==0 || deleteInfixSize<=0 || oversize_change
          print(htmlEncode(@lineJustDeleted))
        else
          print(htmlEncode(@lineJustDeleted[0,prefixLen]))
          print("<span id=\"removedchars\">")
          print(formatChange(@lineJustDeleted[prefixLen,deleteInfixSize]))
          print("</span>")
          print(htmlEncode(@lineJustDeleted[@lineJustDeleted.length-suffixLen,suffixLen]))
        end
        if superlong_line
          println("<strong class=\"error\">[...]</strong>")
        else
          println("")
        end
        @lineJustDeleted = nil
      end
      if initial=="-"
        @lineJustDeleted=line
        @lineJustDeletedSuperlong = superlong_line
        shift(initial)
        # we'll print it next time (fingers crossed)
        return
      elsif @lineJustDeleted!=nil
        print(htmlEncode(@lineJustDeleted))
        if @lineJustDeletedSuperlong
          println("<strong class=\"error\">[...]</strong>")
        else
          println("")
        end
        @lineJustDeleted = nil
      end
      shift(initial)
      if prefixLen==1 && suffixLen==0 || addInfixSize<=0 || oversize_change
        encoded = htmlEncode(line)
      else
        encoded = htmlEncode(line[0,prefixLen]) +
        "<span id=\"addedchars\">" +
        formatChange(line[prefixLen,addInfixSize]) +
        "</span>" +
        htmlEncode(line[line.length-suffixLen,suffixLen])
      end
    else
      encoded = htmlEncode(line)
    end
    if initial=="-"
      unless @lineJustDeleted==nil
        print(htmlEncode(@lineJustDeleted))
        if @lineJustDeletedSuperlong
          println("<strong class=\"error\">[...]</strong>")
        else
          println("")
        end
        @lineJustDeleted=nil
      end
    end
    if initial=="+"
      $task_keywords.each do |task|
        if line =~ /\b(#{task}\b.*)/
          $task_list << $1
          encoded.sub!(/\b#{task}\b/, "<span class=\"task\">#{task}</span>")
          encoded = "<a name=\"task#{$task_list.size}\" />" + encoded
          break
        end
      end
    end
    print(encoded)
    if superlong_line
      println("<strong class=\"error\">[...]</strong>")
    else
      println("")
    end
  end

  def teardown
    unless @lineJustDeleted==nil
      print(htmlEncode(@lineJustDeleted))
      if @lineJustDeletedSuperlong
        println("<strong class=\"error\">[...]</strong>")
      else
        println("")
      end
      @lineJustDeleted = nil
    end
    shift(nil)
    if @truncatedLineCount>0
      println("<strong class=\"error\" title=\"#{@truncatedLineCount} lines truncated at column #{$maxDiffLineLength}\">[Note: Some over-long lines of diff output only partialy shown]</strong>")
    end
  end

  # start the diff output, using the given lines as the 'preamble' bit
  def start_output(*lines)
    print("<pre class=\"diff\"><small id=\"info\">")
    lines.each do |line|
      println(htmlEncode(line))
    end
  end

 private

  def formatChange(text)
    return '<small id="info">^M</small>' if text=="\r"
    htmlEncode(text).gsub(/ /, '&nbsp;')
  end

  def shift(nextState)
    unless @currentState == nil
      if @currentStyle == "info"
        print("</small></pre>")
      else
        print("</pre>")
      end
      @currentStyle = case nextState
        when "\\" then "info" # as in '\ No newline at end of file'
        when "@" then "info"
        when " " then "context"
        when "+" then "added"
        when "-" then "removed"
      end
      unless nextState == nil
        if @currentStyle=='info'
          print("<pre class=\"diff\"><small id=\"info\">")
        else
          print("<pre class=\"diff\" id=\"#{@currentStyle}\">")
        end
      end
    end
    @currentState = nextState
  end

  def commonPrefixLength(a, b)
    length = 0
    a.each_byte do |char|
      break unless b[length]==char
      length = length + 1
    end
    return length
  end
end


class UnifiedDiffHandler < LineConsumer
  def setup
    @stats = UnifiedDiffStats.new
    @colour = UnifiedDiffColouriser.new
    @colour.output = @emailIO
    @lookahead = nil
  end

  def consume(line)
    case lineno()
     when 1
      @diffline = line
     when 2
      @lookahead = line
     when 3
      println($fileHeaderHtml)
      @colour.start_output(@diffline, @lookahead, line)
     else
      unless $file.removal? && $no_removed_file_diff
        @stats.consume(line)
      end
      if @stats.diffLines < $maxLinesPerDiff
        @colour.consume(line)
      elsif @stats.diffLines == $maxLinesPerDiff
        @colour.consume(line)
        @colour.teardown
      end
    end
  end

  def teardown
    if @lookahead == nil
      $file.isEmpty = true
    elsif @lookahead  =~ /Binary files .* and .* differ/
      $file.isBinary = true
    else
      unless $file.removal? && $no_removed_file_diff
        if @stats.diffLines > $maxLinesPerDiff
          println("</pre>")
          println("<strong class=\"error\">[truncated at #{$maxLinesPerDiff} lines; #{@stats.diffLines-$maxLinesPerDiff} more skipped]</strong>")
        else
          @colour.teardown
        end
        println("</div>") # end of "file" div
	$file.has_diff = true
      end
    end
  end
end


# a filter that counts the number of characters output to the underlying object
class OutputCounter
  # TODO: This should probably be a subclass of IO
  # TODO: assumes unix end-of-line convention

  def initialize(io)
    @io = io
    # TODO: use real number of chars representing end of line (for platform)
    @eol_size = 1
    @count = 0;
  end

  def puts(text)
    @count += text.length
    @count += @eol_size unless text =~ /\n$/
    @io.puts(text)
  end

  def print(text)
    @count += text.length
    @io.print(text)
  end

  attr_reader :count
end


# a filter that can be told to stop outputing data to the underlying object
class OutputDropper
  def initialize(io)
    @io = io
    @drop = false
  end

  def puts(text)
    @io.puts(text) unless @drop
  end

  def print(text)
    @io.print(text) unless @drop
  end

  attr_accessor :drop
end


# TODO: the current implementation of the size-limit continues to generate
# HTML-ified diff output, but doesn't add it to the email.  This means we
# can report 'what you would have won', but is less efficient than turning
# of the diff highlighting code.  Does this matter?

# Counts the amount of data written, and when choose_to_limit? is called,
# checks this count against the configured limit, discarding any further
# output if the limit is exceeded.  We aren't strict about the limit becase
# we don't want to chop-off the end of a tag and produce invalid HTML, etc.
class OutputSizeLimiter
  def initialize(io, limit)
    @dropper = OutputDropper.new(io)
    @counter = OutputCounter.new(@dropper)
    @limit = limit
    @written_count = nil
  end

  def puts(text)
    @counter.puts(text)
  end

  def print(text)
    @counter.print(text)
  end

  def choose_to_limit?
    return true if @dropper.drop
    if @counter.count >= @limit
      @dropper.drop = true
      @written_count = @counter.count
      return true
    end
    return false
  end

  def total_count
    @counter.count
  end

  def written_count
    if @written_count.nil?
      total_count
    else
      @written_count
    end
  end
end


cvsroot_dir = "#{ENV['CVSROOT']}/CVSROOT"
$config = "#{cvsroot_dir}/cvsspam.conf"
$users_file = "#{cvsroot_dir}/users"

$debug = false
$recipients = Array.new
$sendmail_prog = "/usr/sbin/sendmail"
$no_removed_file_diff = false
$task_keywords = ['TODO', 'FIXME']
$bugzillaURL = nil
$jiraURL = nil
$ticketURL = nil
$viewcvsURL = nil
$choraURL = nil
$cvswebURL = nil
$from_address = nil
$subjectPrefix = nil
$files_in_subject = false;
$smtp_host = nil
$repository_name = nil
# 2MiB limit on attached diffs,
$mail_size_limit = 1024 * 1024 * 2

require 'getoptlong'

opts = GetoptLong.new(
  [ "--to",     "-t", GetoptLong::REQUIRED_ARGUMENT ],
  [ "--config", "-c", GetoptLong::REQUIRED_ARGUMENT ],
  [ "--debug",  "-d", GetoptLong::NO_ARGUMENT ],
  [ "--from",   "-u", GetoptLong::REQUIRED_ARGUMENT ]
)

opts.each do |opt, arg|
  $recipients << arg if opt=="--to"
  $config = arg if opt=="--config"
  $debug = true if opt=="--debug"
  $from_address = arg if opt=="--from"
end


if ARGV.length != 1
  if ARGV.length > 1
    $stderr.puts "extra arguments not needed: #{ARGV[1, ARGV.length-1].join(', ')}"
  else
    $stderr.puts "missing required file argument"
  end
  puts "Usage: cvsspam.rb [ --to <email> ] [ --config <file> ] <collect_diffs file>"
  exit(-1)
end

$logfile = ARGV[0]


$additionalHeaders = Array.new
$problemHeaders = Array.new

# helper functions called from the 'config file'
def addHeader(name, value)
  if name =~ /^[!-9;-~]+$/
    $additionalHeaders << [name, value]
  else
    $problemHeaders << [name, value]
  end
end
def addRecipient(email)
  $recipients << email
end
class GUESS
end

if FileTest.exists?($config)
  blah("Using config '#{$config}'")
  load $config
else
  blah("Config file '#{$config}' not found, ignoring")
end

if $recipients.empty?
  fail "No email recipients defined"
end

if $viewcvsURL != nil
  $viewcvsURL << "/" unless $viewcvsURL =~ /\/$/
  $frontend = ViewCVSFrontend.new($viewcvsURL)
elsif $choraURL !=nil
  $frontend = ChoraFrontend.new($choraURL)
elsif $cvswebURL !=nil
  $cvswebURL << "/" unless $cvswebURL =~ /\/$/
  $frontend = CVSwebFrontend.new($cvswebURL)
else
  $frontend = NoFrontend.new
end

if $viewcvsURL != nil || $cvswebURL !=nil
  if $repository_name == GUESS
    # use the last component of the repository path as the name
    ENV['CVSROOT'] =~ /([^\/]+$)/
    $frontend.repository_name = $1
  elsif $repository_name != nil
    $frontend.repository_name = $repository_name
  end
end


if $bugzillaURL != nil
  commentSubstitutions['\b[Bb][Uu][Gg]\s*#?[0-9]+'] = bugzillaSub
end
if $jiraURL != nil
  commentSubstitutions['\b[a-zA-Z]+-[0-9]+\b'] = jiraSub
end
if $ticketURL != nil
  commentSubstitutions['\b[Tt][Ii][Cc][Kk][Ee][Tt]\s*#?[0-9]+\b'] = ticketSub
end
$commentEncoder = MultiSub.new(commentSubstitutions)


tagHandler = TagHandler.new

$handlers = Hash[">" => CommentHandler.new,
		 "U" => UnifiedDiffHandler.new,
		 "T" => tagHandler,
		 "A" => AddedFileHandler.new,
		 "R" => RemovedFileHandler.new,
		 "M" => ModifiedFileHandler.new,
		 "V" => VersionHandler.new]

$handlers["A"].setTagHandler(tagHandler)
$handlers["R"].setTagHandler(tagHandler)
$handlers["M"].setTagHandler(tagHandler)

$fileEntries = Array.new
$task_list = Array.new
$allTags = Hash.new

File.open("#{$logfile}.emailtmp", File::RDWR|File::CREAT|File::TRUNC) do |mail|

  $diff_output_limiter = OutputSizeLimiter.new(mail, $mail_size_limit)

  File.open($logfile) do |log|
    reader = LogReader.new(log)

    until reader.eof
      handler = $handlers[reader.currentLineCode]
      if handler == nil
        raise "No handler file lines marked '##{reader.currentLineCode}'"
      end
      handler.handleLines(reader.getLines, $diff_output_limiter)
    end
  end

end

if $subjectPrefix == nil
  $subjectPrefix = "[CVS #{Repository.array.join(',')}]"
end

if $files_in_subject
  all_files = ""
  $fileEntries.each do |file|
    name = htmlEncode(file.name_after_common_prefix)
    if all_files != ""
      all_files = all_files + ";" + name
    else
      all_files = name
    end
  end
  $mailSubject = all_files + ":" + $mailSubject
end

mailSubject = "#{$subjectPrefix} #{$mailSubject}"
if mailSubject.length > $maxSubjectLength
  mailSubject = mailSubject[0, $maxSubjectLength]
end

$encoder = HeaderEncoder.new
$encoder.charset = $charset.nil? ? "ISO-8859-1" : $charset


def make_html_email(mail)
  mail.puts(<<HEAD)
<html>
<head>
<style><!--
  body {background-color:#ffffff;}
  .file {border:1px solid #eeeeee;margin-top:1em;margin-bottom:1em;}
  .pathname {font-family:monospace; float:right;}
  .fileheader {margin-bottom:.5em;}
  .diff {margin:0;}
  .tasklist {padding:4px;border:1px dashed #000000;margin-top:1em;}
  .tasklist ul {margin-top:0;margin-bottom:0;}
  tr.alt {background-color:#eeeeee}
  #added {background-color:#ddffdd;}
  #addedchars {background-color:#99ff99;font-weight:bolder;}
  tr.alt #added {background-color:#ccf7cc;}
  #removed {background-color:#ffdddd;}
  #removedchars {background-color:#ff9999;font-weight:bolder;}
  tr.alt #removed {background-color:#f7cccc;}
  #info {color:#888888;}
  #context {background-color:#eeeeee;}
  td {padding-left:.3em;padding-right:.3em;}
  tr.head {border-bottom-width:1px;border-bottom-style:solid;}
  tr.head td {padding:0;padding-top:.2em;}
  .task {background-color:#ffff00;}
  .comment {padding:4px;border:1px dashed #000000;background-color:#ffffdd}
  .error {color:red;}
  hr {border-width:0px;height:2px;background:black;}
--></style>
</head>
<body>
HEAD

  unless ($problemHeaders.empty?)
    mail.puts("<strong class=\"error\">Bad header format in '#{$config}':<ul>")
    $stderr.puts("Bad header format in '#{$config}':")
    $problemHeaders.each do |header|
      mail.puts("<li><pre>#{htmlEncode(header[0])}</pre></li>")
      $stderr.puts(" - #{header[0]}")
    end
    mail.puts("</ul></strong>")
  end
  mail.puts("<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\" rules=\"cols\">")

  haveTags = false
  Repository.each do |repository|
    haveTags |= repository.has_multiple_tags
  end

  filesAdded = 0
  filesRemoved = 0
  filesModified  = 0
  totalLinesAdded = 0
  totalLinesRemoved = 0
  file_count = 0
  lastPath = ""
  last_repository = nil
  $fileEntries.each do |file|
    unless file.repository == last_repository
      last_repository = file.repository
      mail.print("<tr class=\"head\"><td colspan=\"#{last_repository.has_multiple_tags ? 5 : 4}\">")
      if last_repository.has_multiple_tags
        mail.print("Mixed-tag commit")
      else
        mail.print("Commit")
      end
      mail.print(" in <b><tt>#{htmlEncode(last_repository.common_prefix)}</tt></b>")
      if last_repository.trunk_only?
        mail.print("<span id=\"info\"> on MAIN</span>")
      else
        mail.print(" on ")
        tagCount = 0
        last_repository.each_tag do |tag|
          tagCount += 1
          if tagCount > 1
            mail.print tagCount<last_repository.tag_count ? ", " : " & "
          end
          mail.print tag ? htmlEncode(tag) : "<span id=\"info\">MAIN</span>"
        end
      end
      mail.puts("</td></tr>")
    end
    file_count += 1
    if (file_count%2==0)
      mail.print("<tr class=\"alt\">")
    else
      mail.print("<tr>")
    end
    if file.addition?
      filesAdded += 1
    elsif file.removal?
      filesRemoved += 1
    elsif file.modification?
      filesModified += 1
    end
    name = htmlEncode(file.name_after_common_prefix)
    slashPos = name.rindex("/")
    if slashPos==nil
      prefix = ""
    else
      thisPath = name[0,slashPos]
      name = name[slashPos+1,name.length]
      if thisPath == lastPath
        prefix = "&nbsp;"*(slashPos) + "/"
      else 
        prefix = thisPath + "/"
      end
      lastPath = thisPath
    end
    if file.addition?
      name = "<span id=\"added\">#{name}</span>"
    elsif file.removal?
      name = "<span id=\"removed\">#{name}</span>"
    end
    if file.has_diff?
      mail.print("<td><tt>#{prefix}<a href=\"#file#{file_count}\">#{name}</a></tt></td>")
    else
      mail.print("<td><tt>#{prefix}#{name}</tt></td>")
    end
    if file.isEmpty
      mail.print("<td colspan=\"2\" align=\"center\"><small id=\"info\">[empty]</small></td>")
    elsif file.isBinary
      mail.print("<td colspan=\"2\" align=\"center\"><small id=\"info\">[binary]</small></td>")
    else
      if file.lineAdditions>0
        totalLinesAdded += file.lineAdditions
        mail.print("<td align=\"right\" id=\"added\">+#{file.lineAdditions}</td>")
      else
        mail.print("<td></td>")
      end
      if file.lineRemovals>0
        totalLinesRemoved += file.lineRemovals
        mail.print("<td align=\"right\" id=\"removed\">-#{file.lineRemovals}</td>")
      else
        mail.print("<td></td>")
      end
    end
    if last_repository.has_multiple_tags
      if file.tag
        mail.print("<td>#{htmlEncode(file.tag)}</td>")
      else
        mail.print("<td><span id=\"info\">MAIN</span></td>")
      end
    elsif haveTags
      mail.print("<td></td>")
    end
    if file.addition?
      mail.print("<td nowrap=\"nowrap\" align=\"right\">added #{$frontend.version(file.path,file.toVer)}</td>")
    elsif file.removal?
      mail.print("<td nowrap=\"nowrap\">#{$frontend.version(file.path,file.fromVer)} removed</td>")
    elsif file.modification?
      mail.print("<td nowrap=\"nowrap\" align=\"center\">#{$frontend.version(file.path,file.fromVer)} #{$frontend.diff(file)} #{$frontend.version(file.path,file.toVer)}</td>")
    end

    mail.puts("</tr>")
  end
  if $fileEntries.size>1 && (totalLinesAdded+totalLinesRemoved)>0
    # give total number of lines added/removed accross all files
    mail.print("<tr><td></td>")
    if totalLinesAdded>0
      mail.print("<td align=\"right\" id=\"added\">+#{totalLinesAdded}</td>")
    else
      mail.print("<td></td>")
    end
    if totalLinesRemoved>0
      mail.print("<td align=\"right\" id=\"removed\">-#{totalLinesRemoved}</td>")
    else
      mail.print("<td></td>")
    end
    mail.print("<td></td>") if haveTags
    mail.puts("<td></td></tr>")
  end
  
  mail.puts("</table>")

  totalFilesChanged = filesAdded+filesRemoved+filesModified
  if totalFilesChanged > 1
    mail.print("<small id=\"info\">")
    changeKind = 0
    if filesAdded>0
      mail.print("#{filesAdded} added")
      changeKind += 1
    end
    if filesRemoved>0
      mail.print(" + ") if changeKind>0
      mail.print("#{filesRemoved} removed")
      changeKind += 1
    end
    if filesModified>0
      mail.print(" + ") if changeKind>0
      mail.print("#{filesModified} modified")
      changeKind += 1
    end
    mail.print(", total #{totalFilesChanged}") if changeKind > 1
    mail.puts(" files</small><br />")
  end

  if $task_list.size > 0
    task_count = 0
    mail.puts("<div class=\"tasklist\"><ul>")
    $task_list.each do |item|
      task_count += 1
      item = htmlEncode(item)
      mail.puts("<li><a href=\"#task#{task_count}\">#{item}</a></li>")
    end
    mail.puts("</ul></div>")
  end


  File.open("#{$logfile}.emailtmp") do |input|
    input.each do |line|
      mail.puts(line.chomp)
    end
  end
  if $diff_output_limiter.choose_to_limit?
    mail.puts("<p><strong class=\"error\">[Reached #{$diff_output_limiter.written_count} bytes of diffs.")
    mail.puts("Since the limit is about #{$mail_size_limit} bytes,")
    mail.puts("a further #{$diff_output_limiter.total_count-$diff_output_limiter.written_count} were skipped.]</strong></p>")
  end
  if $debug
    blah("leaving file #{$logfile}.emailtmp")
  else
    File.unlink("#{$logfile}.emailtmp")
  end

  mail.puts("<center><small><a href=\"http://www.badgers-in-foil.co.uk/projects/cvsspam/\" title=\"commit -&gt; email\">CVSspam</a> #{$version}</small></center>")

  mail.puts("</body></html>")

end

def sender_alias(address)
  if File.exists?($users_file)
    File.open($users_file) do |io|
      io.each_line do |line|
        if line =~ /^([^:]+)\s*:\s*([^\n\r]+)/
          if address == $1
            return $2
          end
        end
      end
    end
  end
  address
end

class MailContext
  def initialize(io)
    @done_headers = false
    @io = io
  end

  def header(name, value)
    raise "headers already commited" if @done_headers
    if name == "Subject"
      $encoder.encode_header(@io, "Subject", value)
    else
      @io.puts("#{name}: #{value}")
    end
  end

  def body
    @done_headers = true
    @io.puts
    yield @io
  end
end

class SendmailMailer
  def send(from, recipients)
    blah("invoking #{$sendmail_prog} -t")
    IO.popen("#{$sendmail_prog} -t", "w") do |mail|
      ctx = MailContext.new(mail) 
      ctx.header("To", recipients.join(','))
      ctx.header("From", from) if from
      yield ctx
    end
  end
end

class SMTPMailer
  def initialize(smtp_host)
    @smtp_host = smtp_host
  end

  class IOAdapter
    def initialize(mail)
      @mail = mail
    end
    def puts(text="")
      @mail.write(text)
      @mail.write("\r\n")
    end
    def print(text)
      @mail.write(text)
    end
  end

  def send(from, recipients)
    if from == nil
      from = ENV['USER'] || ENV['USERNAME'] || 'cvsspam'
    end  
    unless from =~ /@/
      from = "#{from}@#{ENV['HOSTNAME']||'localhost'}"
    end
    smtp = Net::SMTP.new(@smtp_host)
    blah("connecting to '#{@smtp_host}'")
    smtp.start()
    smtp.ready(from, recipients) do |mail|
      ctx = MailContext.new(IOAdapter.new(mail))
      ctx.header("To", recipients.join(','))
      ctx.header("From", from) if from
      ctx.header("Date", Time.now.utc.strftime(DATE_HEADER_FORMAT))
      yield ctx
    end
  end
end

if $smtp_host
  require 'net/smtp'
  mailer = SMTPMailer.new($smtp_host)
else
  mailer = SendmailMailer.new
end

$from_address = sender_alias($from_address) unless $from_address.nil?

mailer.send($from_address, $recipients) do |mail|
  mail.header("Subject", mailSubject)
  mail.header("MIME-Version", "1.0")
  mail.header("Content-Type", "text/html" + ($charset.nil? ? "" : "; charset=\"#{$charset}\""))
  if ENV['REMOTE_HOST']
    # TODO: I think this will always be an IP address.  If a hostname is
    # possible, it may need encoding of some kind,
    mail.header("X-Originating-IP", "[#{ENV['REMOTE_HOST']}]")
  end
  unless ($additionalHeaders.empty?)
    $additionalHeaders.each do |header|
      mail.header(header[0], header[1])
    end
  end
  mail.header("X-Mailer", "CVSspam #{$version} <http://www.badgers-in-foil.co.uk/projects/cvsspam/>")

  mail.body do |body|
    make_html_email(body)
  end
end
