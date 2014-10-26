case class GitLog(commitHash: String, author: String, date: String, comment: String, diffs: List[GitDiff])

  def gitLogs = rep1(gitLog <~ opt(newline))

  def gitLog = commitHash ~ merge ~ author ~ date ~ comment ~ allDiffs ^^ {
    case h ~ m ~ a ~ d ~ c ~ diffs => GitLog(h, a, d, c, diffs)
  }

  def commitHash: Parser[String] = "commit " ~> anythingWithoutNewLine <~ newline

  def merge: Parser[Option[String]] = opt("Merge: " ~> anythingWithoutNewLine <~ newline)

  def author: Parser[String] = "Author: " ~> anythingWithoutNewLine <~ newline

  def date: Parser[String] = "Date:   " ~> anythingWithoutNewLine <~ newline

  def comment: Parser[String] = newline ~> rep1(commentLine <~ newline) <~ newline ^^ {
    case commentLines => commentLines.mkString("\n")
  }

  def commentLine: Parser[String] = "    " ~> anythingWithoutNewLine

  def allDiffs: Parser[List[GitDiff]] = rep(gitDiff)
  def oldFile: Parser[String] = "--- " ~> anythingWithoutNewLine <~ newline ^^ {
  def newFile: Parser[String] = "+++ " ~> anythingWithoutNewLine <~ newline ^^ {
  def anythingWithoutNewLine: Parser[String] = """[^\n]*""".r

  def asGitLogs(reader: Reader): List[GitLog] = parseAll(gitLogs, reader) match {
    case Success(s, _) => s